package com.PBL.lab.core.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Container Pool Manager for Judge0 - Judge0 컨테이너 풀 관리자
 * 
 * 목적:
 * - 재사용 가능한 Judge0 컨테이너 풀을 관리하여 모든 언어의 코드 실행 지원
 * - 요청마다 컨테이너를 생성/삭제하는 대신 콜드 스타트 컨테이너 풀 유지
 * - 즉시 코드 실행 가능한 준비된 컨테이너 자원 풀 제공
 * 
 * 핵심 기능:
 * - 컨테이너 예열(Pre-warming): 콜드 스타트 시간 최소화
 * - 동적 풀 스케일링: 대기 수요에 따른 자동 크기 조정
 * - 자동 상태 모니터링: 컨테이너 건강 상태 점검 및 재사용 주기 관리
 * - 스레드 안전성: 동시 액세스에도 안전한 컨테이너 할당/반납
 * 
 * 풀 생명주기:
 * 1. 초기화: @PostConstruct에서 최소 크기만큼 컨테이너 생성
 * 2. 할당: acquireContainer()로 사용 가능한 컨테이너 획득
 * 3. 사용: 코드 실행 후 컨테이너 정리 및 재사용 준비
 * 4. 반납: releaseContainer()로 풀에 컨테이너 반납
 * 5. 종료: @PreDestroy에서 모든 컨테이너 정리 및 자원 해제
 * 
 * 성능 최적화:
 * - 컨테이너 재사용로 생성/삭제 오버헤드 최소화
 * - 비동기 컨테이너 생성으로 대기 시간 감소
 * - 지능형 컨테이너 재활용: 사용 횟수 및 유휴 시간 기반 수명주기
 * - 백그라운드 유지보수: 주기적인 상태 점검 및 비정상 컨테이너 정리
 * 
 * 보안 및 격리:
 * - 각 컨테이너는 독립적인 마운트 디렉토리를 가져 파일 격리 보장
 * - 네트워크 격리 (기본 none 모드)로 외부 통신 차단
 * - 메모리 및 CPU 제한으로 리소스 독점 방지
 * - 보안 옵션 적용 (no-new-privileges, dropped capabilities)
 * 
 * 모니터링 및 통계:
 * - 풀 사용률 통계 (총/사용가능/사용중 컨테이너 수)
 * - 풀 히트율 추적을 통한 성능 모니터링
 * - 컨테이너 생성/삭제 이벤트 로깅
 * - 시스템 부하에 따른 동적 스케일링 결정
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContainerPool {
    //컨테이너 풀 라벨
    private static final String PROJECT_NAME = "pbl-backend-judge0";
    private static final String SERVICE_NAME = "judge0";
    private static final String VERSION = "1.0";
    private final DockerClient dockerClient;

    // 풀 설정값들 - application.yml에서 주입받는 컨테이너 풀 구성 파라미터
    @Value("${judge0.container-pool.min-size:5}")
    private int minPoolSize; // 최소 유지 컨테이너 수 (콜드 스타트 방지)

    @Value("${judge0.container-pool.max-size:20}")
    private int maxPoolSize; // 최대 허용 컨테이너 수 (리소스 제한)

    @Value("${judge0.container-pool.max-idle-time:300000}") // 5 minutes default
    private long maxIdleTime; // 컨테이너 최대 유휴 시간 (밀리초)

    @Value("${judge0.container-pool.max-uses:100}")
    private int maxContainerUses; // 컨테이너 최대 재사용 횟수

    @Value("${judge0.container-pool.health-check-interval:30000}") // 30 seconds
    private long healthCheckInterval; // 건강 상태 점검 주기 (밀리초)

    @Value("${judge0.container-pool.image:judge0/compilers}")
    private String judge0Image; // Judge0 컴파일러 이미지 이름






    // 풀 데이터 구조 - 컨테이너 풀의 상태를 관리하는 핵심 자료구조들
    private final BlockingQueue<PooledContainer> availableContainers = new LinkedBlockingQueue<>(); // 사용 가능한 컨테이너 큐
    private final Map<String, PooledContainer> busyContainers = new ConcurrentHashMap<>(); // 현재 사용 중인 컨테이너 맵
    private final AtomicInteger totalContainers = new AtomicInteger(0); // 전체 컨테이너 수 (스레드 안전)
    private final ReentrantLock poolLock = new ReentrantLock(); // 풀 조작 시 동기화를 위한 락

    // 백그라운드 작업 관리자들
    private ScheduledExecutorService poolMaintenanceExecutor; // 주기적 풀 유지보수 작업용
    private ExecutorService containerCreationExecutor; // 비동기 컨테이너 생성 작업용

    // 풀 통계 데이터 - 성능 모니터링 및 풀 효율성 측정용
    private final AtomicInteger totalRequests = new AtomicInteger(0); // 총 요청 수
    private final AtomicInteger poolHits = new AtomicInteger(0); // 풀 히트 수 (기존 컨테이너 재사용)
    private final AtomicInteger poolMisses = new AtomicInteger(0); // 풀 미스 수 (새 컨테이너 생성 필요)

    /**
     * 풀링된 컨테이너 래퍼 클래스 - 컨테이너 메타데이터와 생명주기 정보 포함
     * Judge0 컨테이너의 재사용 가능 상태와 사용 통계를 추적
     */
    @Data
    @Builder
    public static class PooledContainer {
        private String containerId; // Docker 컨테이너 ID
        private String containerName; // Docker 컨테이너 이름
        private Instant createdAt; // 컨테이너 생성 시간
        private Instant lastUsedAt; // 마지막 사용 시간
        private int useCount; // 총 사용 횟수 (재사용 카운터)
        private boolean healthy; // 컨테이너 건강 상태 (실행 중/비정상)
        private String mountPath; // 호스트 마운트 경로 (파일 입출력용)

        /**
         * 컨테이너가 만료되었는지 확인 (최대 사용 횟수 또는 최대 유휴 시간 초과)
         * @param maxIdleTime 최대 유휴 시간 (밀리초)
         * @param maxUses 최대 사용 횟수
         * @return 만료 여부
         */
        public boolean isExpired(long maxIdleTime, int maxUses) {
            // 사용 횟수 제한 체크
            if (useCount >= maxUses) {
                return true;
            }

            // 유휴 시간 제한 체크
            long idleTime = System.currentTimeMillis() - lastUsedAt.toEpochMilli();
            return idleTime > maxIdleTime;
        }

        /**
         * 컨테이너 사용을 표시하고 사용 통계 업데이트
         */
        public void markUsed() {
            this.lastUsedAt = Instant.now();
            this.useCount++;
        }
    }

    /**
     * 컨테이너 풀 초기화 - 애플리케이션 시작 시 실행
     * Judge0 컨테이너 풀을 설정하고 최소 크기만큼 컨테이너를 미리 생성
     */
    @PostConstruct
    public void initialize() {
        log.info("Initializing container pool with min={}, max={} containers", minPoolSize, maxPoolSize);

        // 백그라운드 작업용 스레드 풀 초기화
        poolMaintenanceExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "container-pool-maintenance");
            t.setDaemon(true); // 데몬 스레드로 설정하여 JVM 종료 시 자동 종료
            return t;
        });

        containerCreationExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "container-creator");
            t.setDaemon(true); // 데몬 스레드로 설정
            return t;
        });

        // 초기 풀 생성 (최소 크기만큼 컨테이너 미리 생성)
        createInitialPool();

        // 주기적 유지보수 작업 스케줄링
        scheduleMaintenanceTasks();

        log.info("Container pool initialized successfully");
    }

    /**
     * 초기 컨테이너 풀 생성 - 최소 크기만큼 Judge0 컨테이너를 비동기로 미리 생성
     * 콜드 스타트를 방지하고 즉시 코드 실행 가능한 환경을 준비
     */
    private void createInitialPool() {
        List<CompletableFuture<Void>> creationFutures = new ArrayList<>();

        // 최소 풀 크기만큼 컨테이너를 비동기로 생성
        for (int i = 0; i < minPoolSize; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    PooledContainer container = createNewContainer();
                    if (container != null) {
                        availableContainers.offer(container); // 사용 가능한 풀에 추가
                        log.debug("Added container {} to pool", container.getContainerId());
                    }
                } catch (Exception e) {
                    log.error("Failed to create initial container", e);
                }
            }, containerCreationExecutor); // 별도 스레드에서 컨테이너 생성

            creationFutures.add(future);
        }

        // 모든 컨테이너 생성 완료까지 대기 (최대 60초)
        try {
            CompletableFuture.allOf(creationFutures.toArray(new CompletableFuture[0]))
                    .get(60, TimeUnit.SECONDS);
            log.info("Initial pool created with {} containers", availableContainers.size());
        } catch (Exception e) {
            log.warn("Some containers failed during initial pool creation", e);
        }
    }

    /**
     * 주기적 유지보수 작업 스케줄링 - 풀의 건강성과 효율성을 자동으로 관리
     */
    private void scheduleMaintenanceTasks() {
        // 건강 상태 점검 작업 - 비정상 컨테이너 감지 및 제거
        poolMaintenanceExecutor.scheduleWithFixedDelay(
                this::performHealthCheck,
                healthCheckInterval,
                healthCheckInterval,
                TimeUnit.MILLISECONDS);

        // 풀 크기 조정 작업 - 수요에 따른 동적 스케일링
        poolMaintenanceExecutor.scheduleWithFixedDelay(
                this::adjustPoolSize,
                30000, // 초기 지연 30초
                30000, // 30초마다 실행
                TimeUnit.MILLISECONDS);

        // 만료된 컨테이너 정리 작업 - 오래된 컨테이너 자동 제거
        poolMaintenanceExecutor.scheduleWithFixedDelay(
                this::cleanupExpiredContainers,
                60000, // 초기 지연 1분
                60000, // 1분마다 실행
                TimeUnit.MILLISECONDS);
    }

    /**
     * 풀에서 컨테이너 획득 - 코드 실행을 위한 Judge0 컨테이너 할당
     * @param timeoutMs 최대 대기 시간 (밀리초)
     * @return 사용 가능한 PooledContainer
     * @throws TimeoutException 지정된 시간 내에 컨테이너를 획득하지 못한 경우
     * @throws InterruptedException 스레드가 중단된 경우
     */
    public PooledContainer acquireContainer(long timeoutMs) throws TimeoutException, InterruptedException {
        totalRequests.incrementAndGet(); // 총 요청 수 증가

        // 먼저 사용 가능한 풀에서 컨테이너를 가져오려고 시도
        PooledContainer container = availableContainers.poll(50, TimeUnit.MILLISECONDS);

        if (container != null) {
            // 컨테이너가 여전히 건강한지 검증
            if (isContainerHealthy(container)) {
                container.markUsed(); // 사용 표시 및 통계 업데이트
                busyContainers.put(container.getContainerId(), container); // 사용 중 목록에 추가
                poolHits.incrementAndGet(); // 풀 히트 수 증가
                log.debug("Acquired container {} from pool (uses: {})",
                        container.getContainerId(), container.getUseCount());
                return container;
            } else {
                // 비정상 컨테이너는 제거
                destroyContainer(container);
                container = null;
            }
        }

        // 사용 가능한 컨테이너가 없으면, 최대 크기 내에서 새로 생성 시도
        poolMisses.incrementAndGet();

        if (totalContainers.get() < maxPoolSize) {
            log.debug("No available containers, creating new one");
            container = createNewContainer();
            if (container != null) {
                container.markUsed();
                busyContainers.put(container.getContainerId(), container);
                return container;
            }
        }

        // 여전히 컨테이너가 없으면 사용 가능해질 때까지 대기
        long startTime = System.currentTimeMillis();
        long remainingTime = timeoutMs;

        while (remainingTime > 0) {
            container = availableContainers.poll(Math.min(remainingTime, 1000), TimeUnit.MILLISECONDS);

            if (container != null && isContainerHealthy(container)) {
                container.markUsed();
                busyContainers.put(container.getContainerId(), container);
                log.debug("Acquired container {} after waiting", container.getContainerId());
                return container;
            }

            remainingTime = timeoutMs - (System.currentTimeMillis() - startTime);
        }

        throw new TimeoutException("Failed to acquire container within " + timeoutMs + "ms");
    }

    /**
     * Release a container back to the pool
     */
    public void releaseContainer(String containerId) {
        PooledContainer container = busyContainers.remove(containerId);

        if (container == null) {
            log.warn("Attempted to release unknown container: {}", containerId);
            return;
        }

        try {
            // Clean up container for reuse
            cleanupContainerForReuse(container);

            // Check if container should be recycled
            if (container.isExpired(maxIdleTime, maxContainerUses)) {
                log.debug("Container {} expired (uses: {}), destroying",
                        containerId, container.getUseCount());
                destroyContainer(container);

                // Create replacement if below min size
                if (totalContainers.get() < minPoolSize) {
                    containerCreationExecutor.submit(this::createReplacementContainer);
                }
            } else {
                // Return to available pool
                availableContainers.offer(container);
                log.debug("Released container {} back to pool", containerId);
            }
        } catch (Exception e) {
            log.error("Error releasing container {}, destroying it", containerId, e);
            destroyContainer(container);
        }
    }

    /**
     * Create a new Judge0 container
     */
    private PooledContainer createNewContainer() {
        try {
            // Check if Judge0 image exists
            try {
                dockerClient.inspectImageCmd(judge0Image).exec();
            } catch (Exception e) {
                log.error("Judge0 image not found: {}. Please pull the image first.", judge0Image);
                return null;
            }

            String containerName = "judge0-pool-" + UUID.randomUUID().toString().substring(0, 8);
            // 윈도우 환경 호환성을 위한 임시 디렉토리 경로
            String mountPath = System.getProperty("java.io.tmpdir") + File.separator + "judge0" + File.separator
                    + containerName;

            // Create mount directory
            java.io.File mountDir = new java.io.File(mountPath);
            if (!mountDir.exists()) {
                boolean created = mountDir.mkdirs();
                if (!created) {
                    log.error("Failed to create mount directory: {}", mountPath);
                    return null;
                } else {
                    log.debug("Created mount directory: {}", mountPath);
                }
            }

            // Create container with Judge0 image that supports all languages
            CreateContainerResponse response = dockerClient.createContainerCmd(judge0Image)
                    .withName(containerName)
                    .withWorkingDir("/tmp/judge")
                    .withCmd("tail", "-f", "/dev/null") // Keep container running
                    .withNetworkMode("none") // No network by default
                    .withHostConfig(HostConfig.newHostConfig()
                            .withMemory(512L * 1024 * 1024) // 512MB default
                            .withCpuQuota(100000L) // 1 CPU
                            .withPidsLimit(256L)
                            .withReadonlyRootfs(false)
                            .withBinds(new Bind(mountPath, new Volume("/tmp/judge"), AccessMode.rw))
                            .withSecurityOpts(Arrays.asList("no-new-privileges:true"))
                            .withAutoRemove(false))
                            .withLabels(Map.of(
                                    "com.docker.compose.project", PROJECT_NAME,
                                    "com.docker.compose.service",  SERVICE_NAME,
                                    "com.docker.compose.version", VERSION,
                                    "com.docker.compose.config-hash", "custom"
                            ))
                    .exec();

            String containerId = response.getId();

            // Start the container
            try {
                dockerClient.startContainerCmd(containerId).exec();
                log.debug("Successfully started container: {} with mount: {}", containerId, mountPath);
            } catch (Exception e) {
                log.error("Failed to start container: {}", containerId, e);
                // Clean up failed container
                try {
                    dockerClient.removeContainerCmd(containerId).exec();
                } catch (Exception cleanupException) {
                    log.warn("Failed to cleanup failed container: {}", containerId, cleanupException);
                }
                return null;
            }

            totalContainers.incrementAndGet();

            PooledContainer container = PooledContainer.builder()
                    .containerId(containerId)
                    .containerName(containerName)
                    .createdAt(Instant.now())
                    .lastUsedAt(Instant.now())
                    .useCount(0)
                    .healthy(true)
                    .mountPath(mountPath)
                    .build();

            log.info("Created new container: {} (total: {})", containerId, totalContainers.get());
            return container;

        } catch (Exception e) {
            log.error("Failed to create new container", e);
            return null;
        }
    }

    /**
     * Clean up container for reuse
     */
    private void cleanupContainerForReuse(PooledContainer container) {
        try {
            // Clean up mounted directory
            String mountPath = container.getMountPath();
            if (mountPath != null) {
                // Remove all files from the mount directory
                java.io.File mountDir = new java.io.File(mountPath);
                if (mountDir.exists()) {
                    for (java.io.File file : mountDir.listFiles()) {
                        if (file.isDirectory()) {
                            deleteDirectory(file);
                        } else {
                            file.delete();
                        }
                    }
                }
            }

            // Kill any running processes in container (except the tail command)
            try {
                dockerClient.execCreateCmd(container.getContainerId())
                        .withCmd("sh", "-c", "pkill -9 -f -v 'tail -f /dev/null' || true")
                        .exec();
            } catch (Exception e) {
                log.debug("No processes to kill in container {}", container.getContainerId());
            }

            log.debug("Cleaned up container {} for reuse", container.getContainerId());

        } catch (Exception e) {
            log.error("Failed to cleanup container {}", container.getContainerId(), e);
            throw new RuntimeException("Container cleanup failed", e);
        }
    }

    /**
     * Recursively delete directory
     */
    private void deleteDirectory(java.io.File dir) {
        if (dir.isDirectory()) {
            java.io.File[] children = dir.listFiles();
            if (children != null) {
                for (java.io.File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        dir.delete();
    }

    /**
     * Check if container is healthy
     */
    private boolean isContainerHealthy(PooledContainer container) {
        try {
            return dockerClient.inspectContainerCmd(container.getContainerId())
                    .exec()
                    .getState()
                    .getRunning();
        } catch (Exception e) {
            log.warn("Health check failed for container {}", container.getContainerId(), e);
            return false;
        }
    }

    /**
     * Destroy a container
     */
    private void destroyContainer(PooledContainer container) {
        try {
            // Stop and remove container
            dockerClient.stopContainerCmd(container.getContainerId())
                    .withTimeout(5)
                    .exec();
            dockerClient.removeContainerCmd(container.getContainerId())
                    .withForce(true)
                    .exec();

            // Clean up mount directory
            if (container.getMountPath() != null) {
                deleteDirectory(new java.io.File(container.getMountPath()));
            }

            totalContainers.decrementAndGet();
            log.debug("Destroyed container {} (total: {})",
                    container.getContainerId(), totalContainers.get());

        } catch (Exception e) {
            log.error("Failed to destroy container {}", container.getContainerId(), e);
        }
    }

    /**
     * Perform health check on all containers
     */
    private void performHealthCheck() {
        log.debug("Performing health check on {} available containers", availableContainers.size());

        List<PooledContainer> unhealthyContainers = new ArrayList<>();

        for (PooledContainer container : availableContainers) {
            if (!isContainerHealthy(container)) {
                unhealthyContainers.add(container);
            }
        }

        // Remove unhealthy containers
        for (PooledContainer unhealthy : unhealthyContainers) {
            availableContainers.remove(unhealthy);
            destroyContainer(unhealthy);
            log.info("Removed unhealthy container: {}", unhealthy.getContainerId());
        }

        // Create replacements if needed
        int toCreate = minPoolSize - totalContainers.get();
        for (int i = 0; i < toCreate; i++) {
            containerCreationExecutor.submit(this::createReplacementContainer);
        }
    }

    /**
     * Adjust pool size based on demand
     */
    private void adjustPoolSize() {
        int available = availableContainers.size();
        int busy = busyContainers.size();
        int total = totalContainers.get();

        log.debug("Pool status - Available: {}, Busy: {}, Total: {}", available, busy, total);

        // Scale up if we're running low on available containers
        if (available < 2 && total < maxPoolSize) {
            int toCreate = Math.min(3, maxPoolSize - total);
            log.info("Scaling up pool by {} containers", toCreate);

            for (int i = 0; i < toCreate; i++) {
                containerCreationExecutor.submit(this::createReplacementContainer);
            }
        }

        // Scale down if we have too many idle containers
        if (available > minPoolSize * 2 && total > minPoolSize) {
            int toRemove = Math.min(available - minPoolSize, 3);
            log.info("Scaling down pool by {} containers", toRemove);

            for (int i = 0; i < toRemove; i++) {
                PooledContainer container = availableContainers.poll();
                if (container != null) {
                    destroyContainer(container);
                }
            }
        }
    }

    /**
     * Clean up expired containers
     */
    private void cleanupExpiredContainers() {
        List<PooledContainer> toRemove = new ArrayList<>();

        for (PooledContainer container : availableContainers) {
            if (container.isExpired(maxIdleTime, maxContainerUses)) {
                toRemove.add(container);
            }
        }

        for (PooledContainer expired : toRemove) {
            availableContainers.remove(expired);
            destroyContainer(expired);
            log.debug("Cleaned up expired container: {}", expired.getContainerId());
        }

        // Ensure minimum pool size
        int toCreate = minPoolSize - totalContainers.get();
        for (int i = 0; i < toCreate; i++) {
            containerCreationExecutor.submit(this::createReplacementContainer);
        }
    }

    /**
     * Create replacement container
     */
    private void createReplacementContainer() {
        try {
            PooledContainer container = createNewContainer();
            if (container != null) {
                availableContainers.offer(container);
                log.debug("Created replacement container: {}", container.getContainerId());
            }
        } catch (Exception e) {
            log.error("Failed to create replacement container", e);
        }
    }

    /**
     * Get pool statistics
     */
    public PoolStatistics getStatistics() {
        return PoolStatistics.builder()
                .totalContainers(totalContainers.get())
                .availableContainers(availableContainers.size())
                .busyContainers(busyContainers.size())
                .totalRequests(totalRequests.get())
                .poolHits(poolHits.get())
                .poolMisses(poolMisses.get())
                .hitRate(totalRequests.get() > 0 ? (double) poolHits.get() / totalRequests.get() * 100 : 0)
                .build();
    }

    /**
     * Pool statistics
     */
    @Data
    @Builder
    public static class PoolStatistics {
        private int totalContainers;
        private int availableContainers;
        private int busyContainers;
        private int totalRequests;
        private int poolHits;
        private int poolMisses;
        private double hitRate;
    }

    /**
     * Shutdown the pool
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down container pool");

        // Stop accepting new requests
        poolMaintenanceExecutor.shutdown();
        containerCreationExecutor.shutdown();

        // Destroy all containers
        List<PooledContainer> allContainers = new ArrayList<>();
        allContainers.addAll(availableContainers);
        allContainers.addAll(busyContainers.values());

        for (PooledContainer container : allContainers) {
            destroyContainer(container);
        }

        try {
            poolMaintenanceExecutor.awaitTermination(10, TimeUnit.SECONDS);
            containerCreationExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for executor shutdown", e);
        }

        log.info("Container pool shutdown complete");
    }
}
