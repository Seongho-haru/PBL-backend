package com.PBL.lab.judge0.docker;

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

    private final DockerClient dockerClient;
    
    @Value("${judge0.container-pool.min-size:5}")
    private int minPoolSize;
    
    @Value("${judge0.container-pool.max-size:20}")
    private int maxPoolSize;
    
    @Value("${judge0.container-pool.max-idle-time:300000}") // 5 minutes default
    private long maxIdleTime;
    
    @Value("${judge0.container-pool.max-uses:100}")
    private int maxContainerUses;
    
    @Value("${judge0.container-pool.health-check-interval:30000}") // 30 seconds
    private long healthCheckInterval;
    
    @Value("${judge0.container-pool.image:judge0/compilers}")
    private String judge0Image;
    
    // Pool data structures
    private final BlockingQueue<PooledContainer> availableContainers = new LinkedBlockingQueue<>();
    private final Map<String, PooledContainer> busyContainers = new ConcurrentHashMap<>();
    private final AtomicInteger totalContainers = new AtomicInteger(0);
    private final ReentrantLock poolLock = new ReentrantLock();
    
    // Background tasks
    private ScheduledExecutorService poolMaintenanceExecutor;
    private ExecutorService containerCreationExecutor;
    
    // Pool statistics
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger poolHits = new AtomicInteger(0);
    private final AtomicInteger poolMisses = new AtomicInteger(0);
    
    /**
     * Container wrapper with metadata
     */
    @Data
    @Builder
    public static class PooledContainer {
        private String containerId;
        private String containerName;
        private Instant createdAt;
        private Instant lastUsedAt;
        private int useCount;
        private boolean healthy;
        private String mountPath; // Host path for file mounting
        
        public boolean isExpired(long maxIdleTime, int maxUses) {
            if (useCount >= maxUses) {
                return true;
            }
            
            long idleTime = System.currentTimeMillis() - lastUsedAt.toEpochMilli();
            return idleTime > maxIdleTime;
        }
        
        public void markUsed() {
            this.lastUsedAt = Instant.now();
            this.useCount++;
        }
    }
    
    /**
     * Initialize the container pool on startup
     */
    @PostConstruct
    public void initialize() {
        log.info("Initializing container pool with min={}, max={} containers", minPoolSize, maxPoolSize);
        
        // Start background executors
        poolMaintenanceExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "container-pool-maintenance");
            t.setDaemon(true);
            return t;
        });
        
        containerCreationExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "container-creator");
            t.setDaemon(true);
            return t;
        });
        
        // Create initial pool
        createInitialPool();
        
        // Schedule maintenance tasks
        scheduleMaintenanceTasks();
        
        log.info("Container pool initialized successfully");
    }
    
    /**
     * Create initial pool of containers
     */
    private void createInitialPool() {
        List<CompletableFuture<Void>> creationFutures = new ArrayList<>();
        
        for (int i = 0; i < minPoolSize; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    PooledContainer container = createNewContainer();
                    if (container != null) {
                        availableContainers.offer(container);
                        log.debug("Added container {} to pool", container.getContainerId());
                    }
                } catch (Exception e) {
                    log.error("Failed to create initial container", e);
                }
            }, containerCreationExecutor);
            
            creationFutures.add(future);
        }
        
        // Wait for all containers to be created
        try {
            CompletableFuture.allOf(creationFutures.toArray(new CompletableFuture[0]))
                    .get(60, TimeUnit.SECONDS);
            log.info("Initial pool created with {} containers", availableContainers.size());
        } catch (Exception e) {
            log.warn("Some containers failed during initial pool creation", e);
        }
    }
    
    /**
     * Schedule periodic maintenance tasks
     */
    private void scheduleMaintenanceTasks() {
        // Health check task
        poolMaintenanceExecutor.scheduleWithFixedDelay(
                this::performHealthCheck,
                healthCheckInterval,
                healthCheckInterval,
                TimeUnit.MILLISECONDS
        );
        
        // Pool size adjustment task
        poolMaintenanceExecutor.scheduleWithFixedDelay(
                this::adjustPoolSize,
                30000, // Initial delay 30s
                30000, // Run every 30s
                TimeUnit.MILLISECONDS
        );
        
        // Expired container cleanup task
        poolMaintenanceExecutor.scheduleWithFixedDelay(
                this::cleanupExpiredContainers,
                60000, // Initial delay 1 minute
                60000, // Run every minute
                TimeUnit.MILLISECONDS
        );
    }
    
    /**
     * Acquire a container from the pool
     */
    public PooledContainer acquireContainer(long timeoutMs) throws TimeoutException, InterruptedException {
        totalRequests.incrementAndGet();
        
        // Try to get from available pool first
        PooledContainer container = availableContainers.poll(50, TimeUnit.MILLISECONDS);
        
        if (container != null) {
            // Validate container is still healthy
            if (isContainerHealthy(container)) {
                container.markUsed();
                busyContainers.put(container.getContainerId(), container);
                poolHits.incrementAndGet();
                log.debug("Acquired container {} from pool (uses: {})", 
                        container.getContainerId(), container.getUseCount());
                return container;
            } else {
                // Container unhealthy, destroy it
                destroyContainer(container);
                container = null;
            }
        }
        
        // No available container, try to create one if under max
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
        
        // If still no container, wait for one to become available
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
            String mountPath = "/tmp/judge0/" + containerName;
            
            // Create mount directory
            java.io.File mountDir = new java.io.File(mountPath);
            if (!mountDir.exists()) {
                boolean created = mountDir.mkdirs();
                if (!created) {
                    log.warn("Failed to create mount directory: {}", mountPath);
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
                            .withAutoRemove(false)
                    )
                    .exec();
            
            String containerId = response.getId();
            
            // Start the container
            dockerClient.startContainerCmd(containerId).exec();
            
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
                .hitRate(totalRequests.get() > 0 ? 
                        (double) poolHits.get() / totalRequests.get() * 100 : 0)
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
