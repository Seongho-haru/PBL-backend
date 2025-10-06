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
import java.util.stream.Collectors;

/**
 * LSP Container Manager - Docker Attach 방식으로 LSP와 직접 통신
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LspContainerManager {
    
    private static final String PROJECT_NAME = "pbl-lsp";
    private static final String VERSION = "1.0";

    // 컨테이너 최대 유휴 시간 (10분)
    private static final long MAX_IDLE_TIME_MS = 10 * 60 * 1000;
    // 건강 체크 주기 (1분)
    private static final long HEALTH_CHECK_INTERVAL_MS = 60 * 1000;

    private final DockerClient dockerClient;
    private final Map<String, LspContainer> activeContainers = new ConcurrentHashMap<>();

    // 주기적 정리를 위한 스케줄러
    private ScheduledExecutorService maintenanceExecutor;

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
        private Instant lastActivityAt;  // 마지막 활동 시간
        private boolean running;
        private OutputStream stdin;  // LSP 입력
        private BlockingQueue<String> responseQueue;  // 응답 큐
        private AttachContainerResultCallback attachCallback;

        /**
         * 컨테이너가 유휴 상태인지 확인
         */
        public boolean isIdle(long maxIdleTimeMs) {
            if (lastActivityAt == null) {
                return false;
            }
            long idleTime = System.currentTimeMillis() - lastActivityAt.toEpochMilli();
            return idleTime > maxIdleTimeMs;
        }

        /**
         * 마지막 활동 시간 업데이트
         */
        public void markActivity() {
            this.lastActivityAt = Instant.now();
        }
    }
    
    @PostConstruct
    public void initialize() {
        log.debug("LSP 컨테이너 매니저 초기화 중 (Docker Attach 방식)");
        cleanupExistingContainers();

        // 주기적 정리 스케줄러 시작
        maintenanceExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "lsp-container-maintenance");
            t.setDaemon(true);
            return t;
        });

        // 1분마다 건강 체크 및 정리 수행
        maintenanceExecutor.scheduleWithFixedDelay(
            this::performMaintenance,
            HEALTH_CHECK_INTERVAL_MS,
            HEALTH_CHECK_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );

        log.debug("LSP 컨테이너 매니저 초기화 완료 (주기적 정리 스케줄러 시작됨)");
    }
    
    /**
     * 주기적 유지보수 작업 - 건강 체크, 유휴 컨테이너 정리, 고아 컨테이너 제거
     */
    private void performMaintenance() {
        try {
            log.debug("LSP 컨테이너 주기적 정리 시작");

            // 1. 실행 중이지 않은 컨테이너 감지 및 정리
            List<String> toRemove = new ArrayList<>();
            for (Map.Entry<String, LspContainer> entry : activeContainers.entrySet()) {
                LspContainer container = entry.getValue();
                String sessionId = entry.getKey();

                // 컨테이너 건강 상태 체크
                if (!isContainerRunning(container.getContainerId())) {
                    log.warn("실행 중이지 않은 컨테이너 감지: sessionId={}, containerId={}",
                        sessionId, container.getContainerId());
                    toRemove.add(sessionId);
                    continue;
                }

                // 유휴 컨테이너 체크
                if (container.isIdle(MAX_IDLE_TIME_MS)) {
                    log.warn("유휴 컨테이너 감지 ({}분 비활성): sessionId={}, containerId={}",
                        MAX_IDLE_TIME_MS / 60000, sessionId, container.getContainerId());
                    toRemove.add(sessionId);
                }
            }

            // 비정상 및 유휴 컨테이너 제거
            for (String sessionId : toRemove) {
                removeContainer(sessionId);
            }

            // 2. Docker에 남아있는 고아 LSP 컨테이너 정리
            cleanupOrphanContainers();

            log.debug("LSP 컨테이너 주기적 정리 완료 (제거: {}개)", toRemove.size());

        } catch (Exception e) {
            log.error("LSP 컨테이너 유지보수 작업 중 오류 발생", e);
        }
    }

    /**
     * Docker에 남아있는 고아 컨테이너 정리
     */
    private void cleanupOrphanContainers() {
        try {
            List<Container> existingContainers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withLabelFilter(Map.of("com.docker.compose.project", PROJECT_NAME))
                .exec();

            for (Container container : existingContainers) {
                String containerId = container.getId();

                // activeContainers에 없는 컨테이너는 고아 컨테이너
                boolean isOrphan = activeContainers.values().stream()
                    .noneMatch(c -> c.getContainerId().equals(containerId));

                if (isOrphan) {
                    log.warn("고아 LSP 컨테이너 발견, 제거 중: {}", containerId);
                    try {
                        if ("running".equalsIgnoreCase(container.getState())) {
                            dockerClient.stopContainerCmd(containerId)
                                .withTimeout(5)
                                .exec();
                        }
                        dockerClient.removeContainerCmd(containerId)
                            .withForce(true)
                            .exec();
                        log.debug("고아 컨테이너 제거 완료: {}", containerId);
                    } catch (Exception e) {
                        log.warn("고아 컨테이너 제거 실패 {}: {}", containerId, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("고아 컨테이너 정리 작업 중 오류 발생", e);
        }
    }

    private void cleanupExistingContainers() {
        try {
            List<Container> existingContainers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withLabelFilter(Map.of("com.docker.compose.project", PROJECT_NAME))
                .exec();

            for (Container container : existingContainers) {
                try {
                    log.debug("기존 LSP 컨테이너 정리 중: {}", container.getId());
                    dockerClient.removeContainerCmd(container.getId())
                        .withForce(true)
                        .exec();
                } catch (Exception e) {
                    log.warn("컨테이너 제거 실패 {}: {}", container.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("정리 작업 중 오류 발생", e);
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
            log.debug("세션의 기존 컨테이너 재사용: {}", sessionId);
            return existing;
        }

        try {
            ensureImageExists(language);

            String containerName = PROJECT_NAME + "-" + language + "-" +
                                 UUID.randomUUID().toString().substring(0, 8);

            log.debug("LSP 컨테이너 생성 중 - 언어: {}, 세션: {}", language, sessionId);
            
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
                        log.error("[{}] 프레임 처리 중 오류", sessionId, e);
                    }
                    super.onNext(frame);
                }
            };
            
            attachCmd.exec(callback);
            
            // 컨테이너 시작
            dockerClient.startContainerCmd(containerId).exec();

            log.debug("LSP 컨테이너 시작됨: {}", containerId);
            
            LspContainer container = LspContainer.builder()
                .containerId(containerId)
                .containerName(containerName)
                .language(language)
                .sessionId(sessionId)
                .createdAt(Instant.now())
                .lastActivityAt(Instant.now())
                .running(true)
                .stdin(stdin)
                .responseQueue(responseQueue)
                .attachCallback(callback)
                .build();
            
            activeContainers.put(sessionId, container);
            
            // 초기화 대기
            Thread.sleep(1000);
            
            log.debug("LSP 컨테이너 생성 완료: {} (세션: {})", containerName, sessionId);
            
            return container;
            
        } catch (Exception e) {
            log.error("LSP 컨테이너 생성 실패 (언어: {})", language, e);
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
            log.warn("세션에 대한 컨테이너를 찾을 수 없음: {}, 새로 생성", userId);
            container = createContainer(language, userId);
        }

        if (!isContainerRunning(container.getContainerId())) {
            log.error("컨테이너가 실행 중이 아님: {}", container.getContainerId());
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

            // 마지막 활동 시간 업데이트
            container.markActivity();

        } catch (Exception e) {
            log.error("[{}] LSP로 전송 실패", userId, e);
            throw new RuntimeException("Failed to send to LSP: " + e.getMessage(), e);
        }
    }
    
    /**
     * LSP로 JSON-RPC 메시지 전송 및 응답 수신 (호환성용)
     */
    public String sendAndReceive(String language, String userId, String jsonRpcMessage) {
        LspContainer container = activeContainers.get(userId);

        if (container == null) {
            log.warn("세션에 대한 컨테이너를 찾을 수 없음: {}, 새로 생성", userId);
            container = createContainer(language, userId);
        }

        if (!isContainerRunning(container.getContainerId())) {
            log.error("컨테이너가 실행 중이 아님: {}", container.getContainerId());
            throw new RuntimeException("Container is not running");
        }

        try {
            // Content-Length 헤더 추가
            byte[] jsonBytes = jsonRpcMessage.getBytes(StandardCharsets.UTF_8);
            String message = "Content-Length: " + jsonBytes.length + "\r\n\r\n" + jsonRpcMessage;

            log.debug("[{}] → LSP: {}", userId, jsonRpcMessage);

            // stdin으로 메시지 전송
            container.getStdin().write(message.getBytes(StandardCharsets.UTF_8));
            container.getStdin().flush();

            // 마지막 활동 시간 업데이트
            container.markActivity();

            // 응답 대기 (최대 60초로 증가 - completion은 시간이 걸릴 수 있음)
            String response = container.getResponseQueue().poll(60, TimeUnit.SECONDS);

            if (response == null) {
                log.debug("[{}] 응답 없음 (알림일 수 있음)", userId);
                return "";
            }

            log.debug("[{}] ← LSP: {}", userId, response);

            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[{}] LSP 통신 중단됨", userId, e);
            throw new RuntimeException("LSP communication interrupted", e);
        } catch (Exception e) {
            log.error("[{}] LSP 통신 실패", userId, e);
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
            log.debug("이미지가 이미 존재함: {}", imageName);
        } catch (Exception e) {
            log.debug("이미지를 찾을 수 없어 빌드 시작: {}", imageName);
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
            
            log.info("이미지 빌드 중: {} (경로: {})", imageName, dockerfilePath);
            
            // Dockerfile이 있는 디렉토리를 빌드 컨텍스트로 사용
            File buildContext = dockerfileFile.getParentFile();
            
            dockerClient.buildImageCmd(dockerfileFile)
                .withBaseDirectory(buildContext)
                .withTags(Set.of(imageName))
                .start()
                .awaitImageId();
            
            log.info("이미지 빌드 완료: {}", imageName);

        } catch (Exception e) {
            log.error("이미지 빌드 실패: {}", imageName, e);
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
            log.warn("세션에 대한 컨테이너를 찾을 수 없음: {}", sessionId);
            return;
        }

        try {
            log.debug("LSP 컨테이너 제거 중: {} (세션: {})",
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
            
            log.debug("LSP 컨테이너 제거 완료 (세션: {})", sessionId);

        } catch (Exception e) {
            log.error("컨테이너 제거 실패 (세션: {})", sessionId, e);
        }
    }
    
    public void removeContainerById(String containerId) {
        try {
            log.debug("컨테이너 ID로 제거 중: {}", containerId);

            dockerClient.stopContainerCmd(containerId)
                .withTimeout(5)
                .exec();

            dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .exec();

            activeContainers.values().removeIf(c -> c.getContainerId().equals(containerId));

            log.debug("컨테이너 제거 완료: {}", containerId);

        } catch (Exception e) {
            log.error("컨테이너 제거 실패: {}", containerId, e);
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
        log.info("LSP 컨테이너 매니저 종료 중");

        // 스케줄러 종료
        if (maintenanceExecutor != null) {
            maintenanceExecutor.shutdown();
            try {
                if (!maintenanceExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    maintenanceExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                maintenanceExecutor.shutdownNow();
            }
        }

        // 모든 컨테이너 제거
        List<String> sessions = new ArrayList<>(activeContainers.keySet());
        for (String sessionId : sessions) {
            removeContainer(sessionId);
        }

        log.info("LSP 컨테이너 매니저 종료 완료");
    }
}
