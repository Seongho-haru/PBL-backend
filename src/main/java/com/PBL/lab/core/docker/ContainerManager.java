package com.PBL.lab.core.docker;

import com.PBL.lab.core.dto.CodeExecutionRequest;
import com.PBL.lab.core.entity.Language;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.PBL.lab.core.dto.ContainerConfig;
import com.PBL.lab.core.dto.ContainerStats;
import com.PBL.lab.core.dto.ExecResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.nio.file.Files.createDirectories;

/**
 * 컨테이너 관리자 - Docker 컨테이너 생명주기 및 운영 관리
 * 
 * 목적:
 * - Docker 컨테이너의 생성, 시작, 중지, 제거 등 전체 생명주기 관리
 * - 컨테이너 풀링 없이 각 실행마다 새로운 컨테이너 생성 (단순화된 버전)
 * - 컨테이너 로그, 통계, 상태 모니터링 제공
 * 
 * 핵심 기능:
 * - 컨테이너 생성 및 설정 관리
 * - 컨테이너 시작/중지/제거 작업
 * - 컨테이너 로그 수집 및 통계 정보 제공
 * - 고아 컨테이너 정리 및 리소스 관리
 * 
 * 사용 시나리오:
 * - 코드 실행 요청 시 새로운 Judge0 컨테이너 생성
 * - 실행 완료 후 컨테이너 정리 및 제거
 * - 시스템 리소스 정리를 위한 주기적 고아 컨테이너 정리
 */
@Component
@AllArgsConstructor
@Slf4j
public class ContainerManager {
    private final DockerClient dockerClient; // Docker 클라이언트 (컨테이너 조작용)

    // 활성 컨테이너 추적 - 정리 목적으로만 사용 (컨테이너 풀링 아님)
    private final ConcurrentHashMap<String, ContainerInfo> activeContainers = new ConcurrentHashMap<>();

    /**
     * 새로운 컨테이너 생성 - Judge0 코드 실행을 위한 Docker 컨테이너 생성
     * 
     * @param config 컨테이너 설정 (이미지, 명령어, 환경변수 등)
     * @return 생성된 컨테이너 ID
     */
    public String createContainer(ContainerConfig config) {
        try {
            CreateContainerResponse response = dockerClient.createContainerCmd(config.getImage())
                    .withName(config.getName())
                    .withWorkingDir(config.getWorkingDir())
                    .withUser(config.getUser())
                    .withCmd(config.getCommand())
                    .withEnv(config.getEnvironment())
                    .withHostConfig(config.getHostConfig())
                    .withNetworkMode(config.getNetworkMode())
                    .exec();

            String containerId = response.getId();
            activeContainers.put(containerId, new ContainerInfo(containerId, config, 0, 0));

            log.debug("Created container: {} with image: {}", containerId, config.getImage());
            return containerId;

        } catch (Exception e) {
            log.error("Failed to create container", e);
            throw new RuntimeException("Container creation failed", e);
        }
    }

    /**
     * 컨테이너 시작 - 생성된 컨테이너를 실행 상태로 전환
     * 
     * @param containerId 시작할 컨테이너 ID
     */
    public void startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();

            // 시작 시간 기록
            ContainerInfo info = activeContainers.get(containerId);
            if (info != null) {
                info.setStartTime(System.currentTimeMillis());
            }

            log.debug("Started container: {}", containerId);

        } catch (Exception e) {
            log.error("Failed to start container: {}", containerId, e);
            throw new RuntimeException("Container start failed", e);
        }
    }

    /**
     * 컨테이너 중지 - 실행 중인 컨테이너를 정상적으로 중지
     * 
     * @param containerId 중지할 컨테이너 ID
     */
    public void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId)
                    .withTimeout(0) // 10초 타임아웃
                    .exec();

            log.debug("Stopped container: {}", containerId);

        } catch (Exception e) {
            log.warn("Failed to stop container gracefully: {}, forcing kill", containerId);
            // 정상 중지 실패 시 강제 종료 시도
            try {
                dockerClient.killContainerCmd(containerId).exec();
            } catch (Exception killException) {
                log.error("Failed to kill container: {}", containerId, killException);
            }
        }
    }

    /**
     * 컨테이너 제거 - 컨테이너를 완전히 삭제하고 리소스 정리
     * 
     * @param containerId 제거할 컨테이너 ID
     */
    public void removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId)
                    .withForce(true) // 강제 제거
                    .withRemoveVolumes(true) // 볼륨도 함께 제거
                    .exec();

            activeContainers.remove(containerId); // 추적 목록에서도 제거
            log.debug("Removed container: {}", containerId);

        } catch (Exception e) {
            log.error("Failed to remove container: {}", containerId, e);
        }
    }

    /**
     * 컨테이너 로그 수집 - 컨테이너의 표준 출력 및 오류 로그 수집
     * 
     * @param containerId 로그를 수집할 컨테이너 ID
     * @return 컨테이너 로그 문자열
     */
    public String getContainerLogs(String containerId) {
        try (LogContainerResultCallback callback = new LogContainerResultCallback()) {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true) // 표준 출력 포함
                    .withStdErr(true) // 표준 오류 포함
                    .withTimestamps(false) // 타임스탬프 제외
                    .exec(callback)
                    .awaitCompletion(30, TimeUnit.SECONDS); // 30초 대기

            return callback.getLogs();

        } catch (Exception e) {
            log.error("Failed to get container logs: {}", containerId, e);
            return "";
        }
    }

    /**
     * 컨테이너 통계 정보 수집 - 메모리, CPU 사용량 등 리소스 통계 제공
     * 
     * @param containerId 통계를 수집할 컨테이너 ID
     * @return 컨테이너 통계 정보
     */
    public ContainerStats getContainerStats(String containerId) {
        try (StatisticsResultCallback callback = new StatisticsResultCallback()) {
            Statistics stats = dockerClient.statsCmd(containerId)
                    .withNoStream(true) // 스트림 모드 비활성화
                    .exec(callback)
                    .awaitResult(10, TimeUnit.SECONDS); // 10초 대기

            return ContainerStats.from(stats);

        } catch (Exception e) {
            log.error("Failed to get container stats: {}", containerId, e);
            return null;
        }
    }

    /**
     * 컨테이너 목록 조회 - Docker 호스트의 모든 컨테이너 목록 반환
     * 
     * @param showAll 중지된 컨테이너도 포함할지 여부
     * @return 컨테이너 목록
     */
    public List<Container> listContainers(boolean showAll) {
        try {
            return dockerClient.listContainersCmd()
                    .withShowAll(showAll) // 중지된 컨테이너 포함 여부
                    .exec();
        } catch (Exception e) {
            log.error("Failed to list containers", e);
            return List.of();
        }
    }

    /**
     * 컨테이너 실행 상태 확인 - 컨테이너가 현재 실행 중인지 확인
     * 
     * @param containerId 확인할 컨테이너 ID
     * @return 실행 중 여부
     */
    public boolean isContainerRunning(String containerId) {
        try {
            return dockerClient.inspectContainerCmd(containerId)
                    .exec()
                    .getState()
                    .getRunning();
        } catch (Exception e) {
            log.debug("Container {} is not running or doesn't exist", containerId);
            return false;
        }
    }

    /**
     * 컨테이너 완료 대기 - 컨테이너가 종료될 때까지 대기하고 종료 코드 반환
     * 
     * @param containerId    대기할 컨테이너 ID
     * @param timeoutSeconds 최대 대기 시간 (초)
     * @return 컨테이너 종료 코드
     */
    public Integer waitForContainer(String containerId, long timeoutSeconds) {
        try (WaitContainerResultCallback callback = new WaitContainerResultCallback()) {
            return dockerClient.waitContainerCmd(containerId)
                    .exec(callback)
                    .awaitStatusCode(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to wait for container: {}", containerId, e);
            return null;
        }
    }

    /**
     * 고아 컨테이너 정리 - 오래된 Judge0 실행 컨테이너들을 자동으로 정리
     * 시스템 리소스 절약을 위해 1시간 이상 된 컨테이너를 제거
     */
    public void cleanupOrphanedContainers() {
        try {
            List<Container> containers = listContainers(true); // 모든 컨테이너 조회
            long currentTime = System.currentTimeMillis();

            for (Container container : containers) {
                // Judge0 실행 컨테이너인지 확인
                if (container.getNames() != null &&
                        container.getNames()[0].contains("judge0-execution-")) {

                    long containerAge = currentTime - (container.getCreated() * 1000);
                    if (containerAge > TimeUnit.HOURS.toMillis(1)) { // 1시간 이상 된 컨테이너
                        log.info("Cleaning up orphaned container: {}", container.getId());
                        removeContainer(container.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to cleanup orphaned containers", e);
        }
    }

    /**
     * 활성 컨테이너 수 조회 - 현재 추적 중인 활성 컨테이너 수 반환
     * 
     * @return 활성 컨테이너 수
     */
    public int getActiveContainerCount() {
        return activeContainers.size();
    }


    /**
     * 컨테이너 정보 추적 클래스 - 컨테이너의 메타데이터와 실행 시간 추적
     */
    @Data
    @AllArgsConstructor
    private static class ContainerInfo {
        private final String containerId; // 컨테이너 ID
        private final ContainerConfig config; // 컨테이너 설정
        private long startTime; // 시작 시간
        private long endTime; // 종료 시간

        /**
         * 컨테이너 실행 시간 계산
         * 
         * @return 실행 시간 (밀리초)
         */
        public long getRunningTime() {
            return (endTime > 0 ? endTime : System.currentTimeMillis()) - startTime;
        }
    }

    /**
     * 로그 결과 콜백 클래스 - Docker 컨테이너 로그 수집을 위한 비동기 콜백
     */
    private static class LogContainerResultCallback extends
            com.github.dockerjava.api.async.ResultCallbackTemplate<LogContainerResultCallback, com.github.dockerjava.api.model.Frame> {
        private final StringBuilder logs = new StringBuilder(); // 수집된 로그 저장

        @Override
        public void onNext(com.github.dockerjava.api.model.Frame frame) {
            logs.append(new String(frame.getPayload())); // 프레임 데이터를 로그에 추가
        }

        public String getLogs() {
            return logs.toString(); // 수집된 로그 반환
        }

        @Override
        public String toString() {
            return logs.toString();
        }
    }

    /**
     * 통계 결과 콜백 클래스 - Docker 컨테이너 통계 수집을 위한 비동기 콜백
     */
    private static class StatisticsResultCallback
            extends com.github.dockerjava.api.async.ResultCallbackTemplate<StatisticsResultCallback, Statistics> {
        private Statistics statistics; // 수집된 통계 정보

        @Override
        public void onNext(Statistics stats) {
            this.statistics = stats; // 통계 정보 저장
        }

        public Statistics awaitResult(long timeout, TimeUnit timeUnit) throws InterruptedException {
            awaitCompletion(timeout, timeUnit); // 완료까지 대기
            return statistics; // 수집된 통계 반환
        }
    }

    /**
     * 대기 컨테이너 결과 콜백 클래스 - 컨테이너 종료 대기를 위한 비동기 콜백
     */
    private static class WaitContainerResultCallback extends
            com.github.dockerjava.api.async.ResultCallbackTemplate<WaitContainerResultCallback, com.github.dockerjava.api.model.WaitResponse> {
        private Integer statusCode; // 컨테이너 종료 코드

        @Override
        public void onNext(com.github.dockerjava.api.model.WaitResponse response) {
            this.statusCode = response.getStatusCode(); // 종료 코드 저장
        }

        public Integer awaitStatusCode(long timeout, TimeUnit timeUnit) throws InterruptedException {
            awaitCompletion(timeout, timeUnit); // 완료까지 대기
            return statusCode; // 종료 코드 반환
        }
    }

    /**
     * 언어별 실행 컨테이너 생성 (편의 메서드)
     *
     * 이 메서드는 Language 엔티티의 정보를 사용하여 적절한 Docker 이미지로
     * 실행 컨테이너를 생성합니다. 풀링 없이 매 실행마다 새로운 컨테이너를 생성합니다.
     *
     * @param language 언어 정보 (이미지 이름 포함)
     * @param workDir  작업 디렉토리 경로
     * @return 생성된 컨테이너 ID
     * @throws java.io.IOException 디렉토리 생성 실패 시
     */
    public String createExecutionContainer(Language language, Path workDir, CodeExecutionRequest request)
            throws java.io.IOException {
        String image = language.getEffectiveDockerImage();
        String containerName = "judge0-exec-" + java.util.UUID.randomUUID().toString().substring(0, 8);

        // 이미지 확인 및 자동 pull
        ensureImageExists(image);

        // 마운트 디렉토리 생성
        String mountPath = workDir.toString();
        String osName = System.getProperty("os.name", "linux").toLowerCase();
        String user = osName.contains("windows") ? "root" : "nobody:nogroup";
        createDirectories(Paths.get(mountPath));

        // 리소스 제한 설정 (동적)
        com.github.dockerjava.api.model.HostConfig hostConfig = buildHostConfig(request, mountPath);

        // 컨테이너 설정 빌드
        ContainerConfig config = ContainerConfig.builder()
                .image(image)
                .name(containerName)
                .workingDir("/tmp/judge")
                .user(user)
                .command(new String[] { "tail", "-f", "/dev/null" })
                .environment(buildEnvironmentVariables(request))
                .hostConfig(hostConfig)
                .networkMode(determineNetworkMode(request))
                .build();

        return createContainer(config);
    }

    /**
     * 환경변수 배열 생성
     *
     * 기본 환경변수를 설정합니다. 필요시 언어별 추가 환경변수를 설정할 수 있습니다.
     *
     * @param request 코드 실행 요청 정보
     * @return 환경변수 배열
     */
    private String[] buildEnvironmentVariables(CodeExecutionRequest request) {
        java.util.List<String> envVars = new java.util.ArrayList<>();
        envVars.add("PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin");
        envVars.add("LANG=C.UTF-8");

        // 추가 환경변수가 필요한 경우 여기에 추가
        // 예: 특정 언어의 경우 JAVA_HOME, PYTHON_PATH 등

        return envVars.toArray(new String[0]);
    }

    /**
     * 네트워크 모드 결정
     *
     * 요청의 enableNetwork 설정에 따라 네트워크 모드를 결정합니다.
     * 보안을 위해 기본값은 "none"(네트워크 차단)입니다.
     *
     * @param request 코드 실행 요청 정보
     * @return 네트워크 모드 ("none" 또는 "bridge")
     */
    private String determineNetworkMode(CodeExecutionRequest request) {
        if (request.getEnableNetwork() != null && request.getEnableNetwork()) {
            return "bridge";  // 네트워크 허용
        }
        return "none";  // 네트워크 차단 (기본값, 보안)
    }

    /**
     * HostConfig 생성 - 리소스 제한 동적 설정
     *
     * CodeExecutionRequest의 SecurityConstraints를 기반으로 리소스 제한을 설정합니다.
     * - 메모리 제한: constraints.memoryLimit (KB 단위) → Bytes로 변환
     * - CPU 제한: constraints.timeLimit (초 단위) → CPU Quota로 변환
     * - 프로세스 수 제한: constraints.processLimit
     *
     * @param request 코드 실행 요청 정보
     * @param mountPath 마운트 경로
     * @return HostConfig 객체
     */
    private com.github.dockerjava.api.model.HostConfig buildHostConfig(
            CodeExecutionRequest request,
            String mountPath) {

        com.PBL.lab.core.dto.SecurityConstraints constraints = request.getConstraints();

        // 메모리 제한 (KB → Bytes, 기본값: 128MB)
        Long memoryBytes = java.util.Optional.ofNullable(constraints)
                .map(com.PBL.lab.core.dto.SecurityConstraints::getMemoryLimit)
                .orElse(128000) * 1024L;

        // CPU 제한 (초 → CPU Quota, 기본값: 5초 = 500000)
        // CPU Quota = 100000 = 1 CPU core
        // timeLimit=5초 → 500000 (5 CPU cores worth of quota)
        Long cpuQuota = java.util.Optional.ofNullable(constraints)
                .map(com.PBL.lab.core.dto.SecurityConstraints::getTimeLimit)
                .orElse(java.math.BigDecimal.valueOf(5.0))
                .multiply(java.math.BigDecimal.valueOf(100000))
                .longValue();

        // 프로세스 수 제한 (기본값: 256)
        Long pidsLimit = java.util.Optional.ofNullable(constraints)
                .map(com.PBL.lab.core.dto.SecurityConstraints::getProcessLimit)
                .orElse(256)
                .longValue();

        log.debug("리소스 제한 설정 - 메모리: {}MB, CPU Quota: {}, 프로세스: {}",
                memoryBytes / 1024 / 1024, cpuQuota, pidsLimit);

        return com.github.dockerjava.api.model.HostConfig.newHostConfig()
                .withMemory(memoryBytes)
                .withCpuQuota(cpuQuota)
                .withPidsLimit(pidsLimit)
                .withBinds(new com.github.dockerjava.api.model.Bind(
                        mountPath,
                        new com.github.dockerjava.api.model.Volume("/tmp/judge"),
                        com.github.dockerjava.api.model.AccessMode.rw))
                .withSecurityOpts(List.of("no-new-privileges:true"));
    }

    /**
     * 이미지 존재 확인 및 자동 pull
     *
     * 로컬에 이미지가 없으면 Docker Hub에서 자동으로 pull합니다.
     * 이미지가 이미 존재하면 pull을 건너뜁니다.
     *
     * @param image Docker 이미지 이름 (예: judge0/compilers, python:3.11-slim)
     */
    private void ensureImageExists(String image) {
        try {
            dockerClient.inspectImageCmd(image).exec();
            log.debug("Image already exists: {}", image);
        } catch (Exception e) {
            log.info("Pulling image: {}", image);
            try {
                dockerClient.pullImageCmd(image)
                        .exec(new com.github.dockerjava.api.command.PullImageResultCallback())
                        .awaitCompletion();
                log.info("Image pulled successfully: {}", image);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Image pull interrupted", ie);
            }
        }
    }

    /**
     * 컨테이너 내에서 bash 스크립트 실행
     *
     * 지정된 스크립트를 컨테이너 내에서 실행하고 결과를 수집합니다.
     * 표준 출력, 표준 에러, 종료 코드를 포함한 실행 결과를 반환합니다.
     *
     * @param containerId    실행할 컨테이너 ID
     * @param scriptPath     실행할 스크립트 경로 (컨테이너 내 경로)
     * @param timeoutSeconds 최대 실행 시간 (초)
     * @return ExecResult 실행 결과 객체
     * @throws Exception 실행 중 오류 발생 시
     */
    public ExecResult executeScript(String containerId, String scriptPath, long timeoutSeconds) throws Exception {
        // NPE 방지: os.name이 null인 경우 기본값 "linux" 사용
        String osName = System.getProperty("os.name", "linux").toLowerCase();
        String user = osName.contains("windows") ? "root" : "nobody:nogroup";

        com.github.dockerjava.api.command.ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withCmd("bash", scriptPath)
                .withWorkingDir("/tmp/judge")
                .withUser(user)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

        java.io.ByteArrayOutputStream stdout = new java.io.ByteArrayOutputStream();
        java.io.ByteArrayOutputStream stderr = new java.io.ByteArrayOutputStream();

        boolean completed = dockerClient.execStartCmd(execResponse.getId())
                .exec(new ExecStartResultCallback(stdout, stderr))
                .awaitCompletion(timeoutSeconds, TimeUnit.SECONDS);

        com.github.dockerjava.api.command.InspectExecResponse inspectExec = dockerClient
                .inspectExecCmd(execResponse.getId()).exec();
        Integer exitCode = inspectExec.getExitCodeLong() != null ? inspectExec.getExitCodeLong().intValue() : null;

        return new ExecResult(
                stdout.toString(java.nio.charset.StandardCharsets.UTF_8.name()),
                stderr.toString(java.nio.charset.StandardCharsets.UTF_8.name()),
                exitCode,
                completed);
    }

    /**
     * Exec 시작 결과 콜백 클래스 - Docker 스크립트 실행 출력 수집용
     */
    private static class ExecStartResultCallback extends
            com.github.dockerjava.api.async.ResultCallbackTemplate<ExecStartResultCallback, com.github.dockerjava.api.model.Frame> {

        private final java.io.ByteArrayOutputStream stdout;
        private final java.io.ByteArrayOutputStream stderr;

        public ExecStartResultCallback(java.io.ByteArrayOutputStream stdout, java.io.ByteArrayOutputStream stderr) {
            this.stdout = stdout;
            this.stderr = stderr;
        }

        @Override
        public void onNext(com.github.dockerjava.api.model.Frame frame) {
            try {
                switch (frame.getStreamType()) {
                    case STDOUT:
                        stdout.write(frame.getPayload());
                        break;
                    case STDERR:
                        stderr.write(frame.getPayload());
                        break;
                    default:
                        break;
                }
            } catch (java.io.IOException e) {
                log.warn("Failed to write frame data", e);
            }
        }
    }
}
