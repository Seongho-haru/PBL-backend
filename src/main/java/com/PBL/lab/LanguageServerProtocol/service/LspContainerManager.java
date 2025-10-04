package com.PBL.lab.LanguageServerProtocol.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.AttachContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * LSP Container Manager - Docker Attach 방식으로 LSP와 직접 통신
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LspContainerManager {
    
    private static final String PROJECT_NAME = "pbl-lsp";
    private static final String VERSION = "1.0";
    
    private final DockerClient dockerClient;
    private final Map<String, LspContainer> activeContainers = new ConcurrentHashMap<>();
    
    // 언어별 LSP 명령어
    private final Map<String, String> lspCommands = Map.of(
        "python", "python -m pylsp",
        "java", "/usr/local/bin/jdtls",
        "javascript", "typescript-language-server --stdio",
        "typescript", "typescript-language-server --stdio"
    );
    
    @Data
    @Builder
    public static class LspContainer {
        private String containerId;
        private String containerName;
        private String language;
        private String sessionId;
        private Instant createdAt;
        private boolean running;
        private OutputStream stdin;  // LSP 입력
        private BlockingQueue<String> responseQueue;  // 응답 큐
        private AttachContainerResultCallback attachCallback;
    }
    
    @PostConstruct
    public void initialize() {
        log.info("Initializing LSP Container Manager (Docker Attach 방식)");
        cleanupExistingContainers();
        log.info("LSP Container Manager initialized");
    }
    
    private void cleanupExistingContainers() {
        try {
            List<Container> existingContainers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withLabelFilter(Map.of("com.docker.compose.project", PROJECT_NAME))
                .exec();
            
            for (Container container : existingContainers) {
                try {
                    log.info("Cleaning up existing LSP container: {}", container.getId());
                    dockerClient.removeContainerCmd(container.getId())
                        .withForce(true)
                        .exec();
                } catch (Exception e) {
                    log.warn("Failed to remove container {}: {}", container.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error during cleanup", e);
        }
    }
    
    /**
     * LSP 컨테이너 생성 및 LSP 프로세스 시작
     */
    public LspContainer createContainer(String language, String sessionId) {
        String lspCommand = lspCommands.get(language.toLowerCase());
        if (lspCommand == null) {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
        
        LspContainer existing = activeContainers.get(sessionId);
        if (existing != null && isContainerRunning(existing.getContainerId())) {
            log.info("Reusing existing container for session: {}", sessionId);
            return existing;
        }
        
        try {
            ensureImageExists(language);
            
            String containerName = PROJECT_NAME + "-" + language + "-" + 
                                 UUID.randomUUID().toString().substring(0, 8);
            
            log.info("Creating LSP container for language: {}, session: {}", language, sessionId);
            
            // LSP 프로세스를 메인 프로세스로 실행하는 컨테이너 생성
            CreateContainerResponse response = dockerClient
                .createContainerCmd(getImageName(language))
                .withName(containerName)
                .withCmd("sh", "-c", lspCommand)  // LSP를 메인 프로세스로!
                .withStdinOpen(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .withLabels(Map.of(
                    "com.docker.compose.project", PROJECT_NAME,
                    "com.docker.compose.service", language + "-lsp",
                    "com.docker.compose.version", VERSION,
                    "app", "lsp-server",
                    "language", language,
                    "session", sessionId
                ))
                .withHostConfig(HostConfig.newHostConfig()
                    .withMemory(512L * 1024 * 1024)  // 512MB
                    .withCpuQuota(100000L)  // 1 CPU
                    .withNetworkMode("none")
                    .withSecurityOpts(List.of("no-new-privileges:true"))
                )
                .exec();
            
            String containerId = response.getId();
            
            // 컨테이너 시작 전에 attach
            BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();
            
            AttachContainerCmd attachCmd = dockerClient.attachContainerCmd(containerId)
                .withStdIn(System.in)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .withLogs(false);
            
            PipedOutputStream stdin = new PipedOutputStream();
            PipedInputStream stdinPipe = new PipedInputStream(stdin, 8192);
            
            attachCmd.withStdIn(stdinPipe);
            
            // 응답 읽기용 콜백
            AttachContainerResultCallback callback = new AttachContainerResultCallback() {
                private final StringBuilder buffer = new StringBuilder();
                
                @Override
                public void onNext(com.github.dockerjava.api.model.Frame frame) {
                    try {
                        String data = new String(frame.getPayload(), StandardCharsets.UTF_8);
                        
                        if (frame.getStreamType() == com.github.dockerjava.api.model.StreamType.STDOUT) {
                            buffer.append(data);
                            
                            // Content-Length 기반 메시지 파싱
                            parseMessages(buffer, responseQueue);
                            
                        } else if (frame.getStreamType() == com.github.dockerjava.api.model.StreamType.STDERR) {
                            log.warn("[{}] LSP stderr: {}", sessionId, data);
                        }
                    } catch (Exception e) {
                        log.error("[{}] Error processing frame", sessionId, e);
                    }
                    super.onNext(frame);
                }
            };
            
            attachCmd.exec(callback);
            
            // 컨테이너 시작
            dockerClient.startContainerCmd(containerId).exec();
            
            log.info("Started LSP container: {}", containerId);
            
            LspContainer container = LspContainer.builder()
                .containerId(containerId)
                .containerName(containerName)
                .language(language)
                .sessionId(sessionId)
                .createdAt(Instant.now())
                .running(true)
                .stdin(stdin)
                .responseQueue(responseQueue)
                .attachCallback(callback)
                .build();
            
            activeContainers.put(sessionId, container);
            
            // 초기화 대기
            Thread.sleep(1000);
            
            log.info("Successfully created LSP container: {} for session: {}", containerName, sessionId);
            
            return container;
            
        } catch (Exception e) {
            log.error("Failed to create LSP container for language: {}", language, e);
            throw new RuntimeException("Failed to create LSP container", e);
        }
    }
    
    /**
     * Content-Length 기반 메시지 파싱
     */
    private void parseMessages(StringBuilder buffer, BlockingQueue<String> queue) {
        while (true) {
            String buffered = buffer.toString();
            int headerEnd = buffered.indexOf("\r\n\r\n");
            
            if (headerEnd == -1) break;
            
            String headers = buffered.substring(0, headerEnd);
            int contentLength = parseContentLength(headers);
            
            if (contentLength <= 0) break;
            
            int messageStart = headerEnd + 4;
            int messageEnd = messageStart + contentLength;
            
            if (buffered.length() < messageEnd) break;
            
            String message = buffered.substring(messageStart, messageEnd);
            queue.offer(message);
            
            // 처리된 메시지 제거
            buffer.delete(0, messageEnd);
        }
    }
    
    /**
     * Content-Length 헤더 파싱
     */
    private int parseContentLength(String headers) {
        String[] lines = headers.split("\r\n");
        for (String line : lines) {
            if (line.toLowerCase().startsWith("content-length:")) {
                return Integer.parseInt(line.substring(15).trim());
            }
        }
        return -1;
    }
    
    /**
     * LSP로 메시지 전송만 (응답은 별도 스레드에서 받음)
     */
    public void sendToLsp(String language, String userId, String jsonRpcMessage) {
        LspContainer container = activeContainers.get(userId);
        
        if (container == null) {
            log.warn("No container found for session: {}, creating new one", userId);
            container = createContainer(language, userId);
        }
        
        if (!isContainerRunning(container.getContainerId())) {
            log.error("Container {} is not running", container.getContainerId());
            throw new RuntimeException("Container is not running");
        }
        
        try {
            // UTF-8 바이트 수 계산 (문자 개수가 아님!)
            byte[] contentBytes = jsonRpcMessage.getBytes(StandardCharsets.UTF_8);
            int contentLength = contentBytes.length;
            
            // Content-Length 헤더 추가
            String header = "Content-Length: " + contentLength + "\r\n\r\n";
            byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
            
            // 헤더 + 본문 전송
            container.getStdin().write(headerBytes);
            container.getStdin().write(contentBytes);
            container.getStdin().flush();
            
        } catch (Exception e) {
            log.error("[{}] Failed to send to LSP", userId, e);
            throw new RuntimeException("Failed to send to LSP: " + e.getMessage(), e);
        }
    }
    
    /**
     * LSP로 JSON-RPC 메시지 전송 및 응답 수신 (호환성용)
     */
    public String sendAndReceive(String language, String userId, String jsonRpcMessage) {
        LspContainer container = activeContainers.get(userId);
        
        if (container == null) {
            log.warn("No container found for session: {}, creating new one", userId);
            container = createContainer(language, userId);
        }
        
        if (!isContainerRunning(container.getContainerId())) {
            log.error("Container {} is not running", container.getContainerId());
            throw new RuntimeException("Container is not running");
        }
        
        try {
            // Content-Length 헤더 추가
            byte[] jsonBytes = jsonRpcMessage.getBytes(StandardCharsets.UTF_8);
            String message = "Content-Length: " + jsonBytes.length + "\r\n\r\n" + jsonRpcMessage;
            
            log.info("[{}] → LSP: {}", userId, jsonRpcMessage);
            
            // stdin으로 메시지 전송
            container.getStdin().write(message.getBytes(StandardCharsets.UTF_8));
            container.getStdin().flush();
            
            // 응답 대기 (최대 60초로 증가 - completion은 시간이 걸릴 수 있음)
            String response = container.getResponseQueue().poll(60, TimeUnit.SECONDS);
            
            if (response == null) {
                log.debug("[{}] No response (might be notification)", userId);
                return "";
            }
            
            log.info("[{}] ← LSP: {}", userId, response);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[{}] LSP communication interrupted", userId, e);
            throw new RuntimeException("LSP communication interrupted", e);
        } catch (Exception e) {
            log.error("[{}] Failed to communicate with LSP", userId, e);
            throw new RuntimeException("Failed to communicate with LSP: " + e.getMessage(), e);
        }
    }
    
    private String getImageName(String language) {
        return switch (language.toLowerCase()) {
            case "python" -> "python-lsp:latest";
            case "java" -> "java-lsp:latest";
            case "javascript", "typescript" -> "javascript-lsp:latest";
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }
    
    private void ensureImageExists(String language) {
        String imageName = getImageName(language);
        
        try {
            dockerClient.inspectImageCmd(imageName).exec();
            log.debug("Image {} already exists", imageName);
        } catch (Exception e) {
            log.info("Image {} not found, building...", imageName);
            buildImage(language, imageName);
        }
    }
    
    private void buildImage(String language, String imageName) {
        try {
            // Dockerfile 경로 찾기
            String dockerfilePath = getDockerfilePath(language);
            File dockerfileFile = new File(dockerfilePath);
            
            if (!dockerfileFile.exists()) {
                throw new RuntimeException("Dockerfile not found: " + dockerfilePath);
            }
            
            log.info("Building image: {} from {}", imageName, dockerfilePath);
            
            // Dockerfile이 있는 디렉토리를 빌드 컨텍스트로 사용
            File buildContext = dockerfileFile.getParentFile();
            
            dockerClient.buildImageCmd(dockerfileFile)
                .withBaseDirectory(buildContext)
                .withTags(Set.of(imageName))
                .start()
                .awaitImageId();
            
            log.info("Successfully built image: {}", imageName);
            
        } catch (Exception e) {
            log.error("Failed to build image: {}", imageName, e);
            throw new RuntimeException("Failed to build LSP image: " + e.getMessage(), e);
        }
    }
    
    /**
     * 언어별 Dockerfile 경로 반환
     */
    private String getDockerfilePath(String language) {
        // 프로젝트 루트 찾기
        String projectRoot = System.getProperty("user.dir");
        String dockerfileBase = projectRoot + "/src/main/java/com/PBL/lab/LanguageServerProtocol/LSPDockerFile";
        
        return switch (language.toLowerCase()) {
            case "python" -> dockerfileBase + "/python/python38.Dockerfile";
            case "java" -> dockerfileBase + "/java/openjdk17.Dockerfile";
            case "javascript", "typescript" -> dockerfileBase + "/javascript/node18.Dockerfile";
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }
    
    public void removeContainer(String sessionId) {
        LspContainer container = activeContainers.remove(sessionId);
        
        if (container == null) {
            log.warn("No container found for session: {}", sessionId);
            return;
        }
        
        try {
            log.info("Removing LSP container: {} for session: {}", 
                    container.getContainerName(), sessionId);
            
            if (container.getStdin() != null) {
                container.getStdin().close();
            }
            
            if (container.getAttachCallback() != null) {
                container.getAttachCallback().close();
            }
            
            dockerClient.stopContainerCmd(container.getContainerId())
                .withTimeout(5)
                .exec();
            
            dockerClient.removeContainerCmd(container.getContainerId())
                .withForce(true)
                .exec();
            
            log.info("Successfully removed LSP container for session: {}", sessionId);
            
        } catch (Exception e) {
            log.error("Failed to remove container for session: {}", sessionId, e);
        }
    }
    
    public void removeContainerById(String containerId) {
        try {
            log.info("Removing container by ID: {}", containerId);
            
            dockerClient.stopContainerCmd(containerId)
                .withTimeout(5)
                .exec();
            
            dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .exec();
            
            activeContainers.values().removeIf(c -> c.getContainerId().equals(containerId));
            
            log.info("Successfully removed container: {}", containerId);
            
        } catch (Exception e) {
            log.error("Failed to remove container: {}", containerId, e);
        }
    }
    
    private boolean isContainerRunning(String containerId) {
        try {
            return dockerClient.inspectContainerCmd(containerId)
                .exec()
                .getState()
                .getRunning();
        } catch (Exception e) {
            return false;
        }
    }
    
    public LspContainer getContainer(String sessionId) {
        return activeContainers.get(sessionId);
    }
    
    public List<LspContainer> getContainersByLanguage(String language) {
        return activeContainers.values().stream()
            .filter(c -> c.getLanguage().equals(language))
            .toList();
    }
    
    public List<LspContainer> getAllContainers() {
        return new ArrayList<>(activeContainers.values());
    }
    
    public Set<String> getSupportedLanguages() {
        return lspCommands.keySet();
    }
    
    @Data
    @Builder
    public static class ContainerStats {
        private int totalContainers;
        private Map<String, Integer> containersByLanguage;
        private List<String> activeSessions;
    }
    
    public ContainerStats getStatistics() {
        Map<String, Integer> byLanguage = new HashMap<>();
        
        for (LspContainer container : activeContainers.values()) {
            byLanguage.merge(container.getLanguage(), 1, Integer::sum);
        }
        
        return ContainerStats.builder()
            .totalContainers(activeContainers.size())
            .containersByLanguage(byLanguage)
            .activeSessions(new ArrayList<>(activeContainers.keySet()))
            .build();
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down LSP Container Manager");
        
        List<String> sessions = new ArrayList<>(activeContainers.keySet());
        for (String sessionId : sessions) {
            removeContainer(sessionId);
        }
        
        log.info("LSP Container Manager shutdown complete");
    }
}
