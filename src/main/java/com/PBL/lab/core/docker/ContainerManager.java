package com.PBL.lab.core.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
@Slf4j
public class ContainerManager {
    private final DockerClient dockerClient; // Docker 클라이언트 (컨테이너 조작용)
    
    // 활성 컨테이너 추적 - 정리 목적으로만 사용 (컨테이너 풀링 아님)
    private final ConcurrentHashMap<String, ContainerInfo> activeContainers = new ConcurrentHashMap<>();
    
    public ContainerManager(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }
    
    /**
     * 새로운 컨테이너 생성 - Judge0 코드 실행을 위한 Docker 컨테이너 생성
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
     * @param containerId 중지할 컨테이너 ID
     */
    public void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId)
                    .withTimeout(10) // 10초 타임아웃
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
     * @param containerId 로그를 수집할 컨테이너 ID
     * @return 컨테이너 로그 문자열
     */
    public String getContainerLogs(String containerId) {
        try {
            LogContainerResultCallback callback = new LogContainerResultCallback();
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
     * @param containerId 통계를 수집할 컨테이너 ID
     * @return 컨테이너 통계 정보
     */
    public ContainerStats getContainerStats(String containerId) {
        try {
            Statistics stats = dockerClient.statsCmd(containerId)
                    .withNoStream(true) // 스트림 모드 비활성화
                    .exec(new StatisticsResultCallback())
                    .awaitResult(10, TimeUnit.SECONDS); // 10초 대기
            
            return ContainerStats.from(stats);
            
        } catch (Exception e) {
            log.error("Failed to get container stats: {}", containerId, e);
            return null;
        }
    }

    /**
     * 컨테이너 목록 조회 - Docker 호스트의 모든 컨테이너 목록 반환
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
     * @param containerId 대기할 컨테이너 ID
     * @param timeoutSeconds 최대 대기 시간 (초)
     * @return 컨테이너 종료 코드
     */
    public Integer waitForContainer(String containerId, long timeoutSeconds) {
        try {
            return dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
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
     * @return 활성 컨테이너 수
     */
    public int getActiveContainerCount() {
        return activeContainers.size();
    }

    /**
     * 컨테이너 설정 클래스 - Docker 컨테이너 생성 시 필요한 모든 설정 정보
     */
    @lombok.Data
    public static class ContainerConfig {
        private String image; // Docker 이미지 이름 (예: judge0/compilers)
        private String name; // 컨테이너 이름
        private String workingDir; // 작업 디렉토리
        private String user; // 실행 사용자
        private String[] command; // 실행할 명령어
        private String[] environment; // 환경변수 목록
        private com.github.dockerjava.api.model.HostConfig hostConfig; // 호스트 설정 (리소스 제한 등)
        private String networkMode; // 네트워크 모드 (none, bridge 등)
    }

    /**
     * 컨테이너 통계 클래스 - 컨테이너의 리소스 사용량 통계 정보
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ContainerStats {
        private final Long memoryUsage; // 메모리 사용량 (바이트)
        private final Double cpuPercent; // CPU 사용률 (%)
        private final Long networkRx; // 네트워크 수신 바이트
        private final Long networkTx; // 네트워크 송신 바이트

        /**
         * Docker Statistics 객체에서 ContainerStats 생성
         * @param stats Docker Statistics 객체
         * @return ContainerStats 객체
         */
        public static ContainerStats from(Statistics stats) {
            if (stats == null) {
                return new ContainerStats(null, null, null, null);
            }

            Long memoryUsage = stats.getMemoryStats() != null ? 
                    stats.getMemoryStats().getUsage() : null;
            
            // CPU 퍼센트 계산은 실제 구현에서 더 복잡해질 수 있음
            Double cpuPercent = null;
            
            Long networkRx = null;
            Long networkTx = null;
            
            return new ContainerStats(memoryUsage, cpuPercent, networkRx, networkTx);
        }
    }

    /**
     * 컨테이너 정보 추적 클래스 - 컨테이너의 메타데이터와 실행 시간 추적
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ContainerInfo {
        private final String containerId; // 컨테이너 ID
        private final ContainerConfig config; // 컨테이너 설정
        private long startTime; // 시작 시간
        private long endTime; // 종료 시간
        
        /**
         * 컨테이너 실행 시간 계산
         * @return 실행 시간 (밀리초)
         */
        public long getRunningTime() {
            return (endTime > 0 ? endTime : System.currentTimeMillis()) - startTime;
        }
    }

    /**
     * 로그 결과 콜백 클래스 - Docker 컨테이너 로그 수집을 위한 비동기 콜백
     */
    private static class LogContainerResultCallback extends com.github.dockerjava.api.async.ResultCallbackTemplate<LogContainerResultCallback, com.github.dockerjava.api.model.Frame> {
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
    private static class StatisticsResultCallback extends com.github.dockerjava.api.async.ResultCallbackTemplate<StatisticsResultCallback, Statistics> {
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
    private static class WaitContainerResultCallback extends com.github.dockerjava.api.async.ResultCallbackTemplate<WaitContainerResultCallback, com.github.dockerjava.api.model.WaitResponse> {
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
}
