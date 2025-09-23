package com.PBL.lab.judge0.docker;

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
 * Container Manager
 * 
 * Manages Docker container lifecycle and operations.
 * Simplified version without container pooling - creates fresh containers for each execution.
 */
@Component
@Slf4j
public class ContainerManager {
    private final DockerClient dockerClient;
    
    // Track active containers for cleanup purposes only
    private final ConcurrentHashMap<String, ContainerInfo> activeContainers = new ConcurrentHashMap<>();
    
    public ContainerManager(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }
    
    /**
     * Create a new container
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
     * Start container
     */
    public void startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();
            
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
     * Stop container
     */
    public void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId)
                    .withTimeout(10) // 10 seconds timeout
                    .exec();
            
            log.debug("Stopped container: {}", containerId);
            
        } catch (Exception e) {
            log.warn("Failed to stop container gracefully: {}, forcing kill", containerId);
            try {
                dockerClient.killContainerCmd(containerId).exec();
            } catch (Exception killException) {
                log.error("Failed to kill container: {}", containerId, killException);
            }
        }
    }

    /**
     * Remove container
     */
    public void removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .withRemoveVolumes(true)
                    .exec();
            
            activeContainers.remove(containerId);
            log.debug("Removed container: {}", containerId);
            
        } catch (Exception e) {
            log.error("Failed to remove container: {}", containerId, e);
        }
    }

    /**
     * Get container logs
     */
    public String getContainerLogs(String containerId) {
        try {
            LogContainerResultCallback callback = new LogContainerResultCallback();
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTimestamps(false)
                    .exec(callback)
                    .awaitCompletion(30, TimeUnit.SECONDS);
            
            return callback.getLogs();
                    
        } catch (Exception e) {
            log.error("Failed to get container logs: {}", containerId, e);
            return "";
        }
    }

    /**
     * Get container statistics
     */
    public ContainerStats getContainerStats(String containerId) {
        try {
            Statistics stats = dockerClient.statsCmd(containerId)
                    .withNoStream(true)
                    .exec(new StatisticsResultCallback())
                    .awaitResult(10, TimeUnit.SECONDS);
            
            return ContainerStats.from(stats);
            
        } catch (Exception e) {
            log.error("Failed to get container stats: {}", containerId, e);
            return null;
        }
    }

    /**
     * List all containers
     */
    public List<Container> listContainers(boolean showAll) {
        try {
            return dockerClient.listContainersCmd()
                    .withShowAll(showAll)
                    .exec();
        } catch (Exception e) {
            log.error("Failed to list containers", e);
            return List.of();
        }
    }

    /**
     * Check if container is running
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
     * Wait for container to finish
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
     * Cleanup orphaned containers
     */
    public void cleanupOrphanedContainers() {
        try {
            List<Container> containers = listContainers(true);
            long currentTime = System.currentTimeMillis();
            
            for (Container container : containers) {
                // Check if container is old and should be cleaned up
                if (container.getNames() != null && 
                    container.getNames()[0].contains("judge0-execution-")) {
                    
                    long containerAge = currentTime - (container.getCreated() * 1000);
                    if (containerAge > TimeUnit.HOURS.toMillis(1)) { // 1 hour old
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
     * Get active container count
     */
    public int getActiveContainerCount() {
        return activeContainers.size();
    }

    /**
     * Container configuration class
     */
    @lombok.Data
    public static class ContainerConfig {
        private String image;
        private String name;
        private String workingDir;
        private String user;
        private String[] command;
        private String[] environment;
        private com.github.dockerjava.api.model.HostConfig hostConfig;
        private String networkMode;
    }

    /**
     * Container statistics class
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ContainerStats {
        private final Long memoryUsage;
        private final Double cpuPercent;
        private final Long networkRx;
        private final Long networkTx;

        public static ContainerStats from(Statistics stats) {
            if (stats == null) {
                return new ContainerStats(null, null, null, null);
            }

            Long memoryUsage = stats.getMemoryStats() != null ? 
                    stats.getMemoryStats().getUsage() : null;
            
            // CPU percentage calculation would be more complex in real implementation
            Double cpuPercent = null;
            
            Long networkRx = null;
            Long networkTx = null;
            
            return new ContainerStats(memoryUsage, cpuPercent, networkRx, networkTx);
        }
    }

    /**
     * Container information tracking
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ContainerInfo {
        private final String containerId;
        private final ContainerConfig config;
        private long startTime;
        private long endTime;
        
        public long getRunningTime() {
            return (endTime > 0 ? endTime : System.currentTimeMillis()) - startTime;
        }
    }

    /**
     * Log result callback
     */
    private static class LogContainerResultCallback extends com.github.dockerjava.api.async.ResultCallbackTemplate<LogContainerResultCallback, com.github.dockerjava.api.model.Frame> {
        private final StringBuilder logs = new StringBuilder();

        @Override
        public void onNext(com.github.dockerjava.api.model.Frame frame) {
            logs.append(new String(frame.getPayload()));
        }

        public String getLogs() {
            return logs.toString();
        }

        @Override
        public String toString() {
            return logs.toString();
        }
    }

    /**
     * Statistics result callback
     */
    private static class StatisticsResultCallback extends com.github.dockerjava.api.async.ResultCallbackTemplate<StatisticsResultCallback, Statistics> {
        private Statistics statistics;

        @Override
        public void onNext(Statistics stats) {
            this.statistics = stats;
        }

        public Statistics awaitResult(long timeout, TimeUnit timeUnit) throws InterruptedException {
            awaitCompletion(timeout, timeUnit);
            return statistics;
        }
    }

    /**
     * Wait container result callback
     */
    private static class WaitContainerResultCallback extends com.github.dockerjava.api.async.ResultCallbackTemplate<WaitContainerResultCallback, com.github.dockerjava.api.model.WaitResponse> {
        private Integer statusCode;

        @Override
        public void onNext(com.github.dockerjava.api.model.WaitResponse response) {
            this.statusCode = response.getStatusCode();
        }

        public Integer awaitStatusCode(long timeout, TimeUnit timeUnit) throws InterruptedException {
            awaitCompletion(timeout, timeUnit);
            return statusCode;
        }
    }
}
