package com.PBL.lab.core.controller;

import com.PBL.lab.core.docker.ContainerManager;
import com.PBL.lab.core.service.ConfigService;
import com.PBL.lab.judge0.service.ExecutionJobScheduler;
import com.PBL.lab.judge0.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health Controller
 * 
 * Health check endpoints for system monitoring.
 * Simplified version without container pooling.
 */
@RestController
@RequiredArgsConstructor
public class HealthController {

    private final SubmissionService submissionService;
    private final ExecutionJobScheduler executionJobScheduler;
    private final ContainerManager containerManager;
    private final ConfigService configService;

    /**
     * GET /workers
     * Get worker health and statistics
     */
    @GetMapping("/workers")
    public ResponseEntity<Map<String, Object>> workers() {
        try {
            ExecutionJobScheduler.QueueStatistics queueStats = executionJobScheduler.getQueueStatistics();
            
            Map<String, Object> response = Map.of(
                "queue", Map.of(
                    "size", submissionService.countSubmissionsInQueue(),
                    "max_size", configService.getMaxQueueSize(),
                    "enqueued_jobs", queueStats.getEnqueuedJobs(),
                    "processing_jobs", queueStats.getProcessingJobs(),
                    "succeeded_jobs", queueStats.getSucceededJobs(),
                    "failed_jobs", queueStats.getFailedJobs()
                ),
                "containers", Map.of(
                    "active", containerManager.getActiveContainerCount(),
                    "execution_mode", "one-time",
                    "pool_enabled", false
                ),
                "timestamp", LocalDateTime.now(),
                "status", "healthy"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(503).body(errorResponse);
        }
    }

    /**
     * Basic health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        try {
            Map<String, Object> response = Map.of(
                "status", "healthy",
                "timestamp", LocalDateTime.now(),
                "version", "1.13.1-spring-simplified",
                "uptime", getUptime(),
                "execution_mode", "one-time-containers"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(503).body(errorResponse);
        }
    }

    /**
     * Detailed system health check
     */
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        try {
            Map<String, Object> components = Map.of(
                "database", checkDatabaseHealth(),
                "docker", checkDockerHealth(),
                "queue", checkQueueHealth()
            );
            
            boolean allHealthy = components.values().stream()
                    .allMatch(component -> "healthy".equals(((Map<?, ?>) component).get("status")));
            
            Map<String, Object> response = Map.of(
                "status", allHealthy ? "healthy" : "degraded",
                "timestamp", LocalDateTime.now(),
                "components", components,
                "execution_info", Map.of(
                    "mode", "one-time-containers",
                    "description", "Fresh container created for each execution"
                )
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(503).body(errorResponse);
        }
    }

    /**
     * Check database health
     */
    private Map<String, Object> checkDatabaseHealth() {
        try {
            long totalSubmissions = submissionService.countSubmissionsInQueue(); // Simple DB query
            return Map.of(
                "status", "healthy",
                "total_submissions", totalSubmissions
            );
        } catch (Exception e) {
            return Map.of(
                "status", "unhealthy",
                "error", e.getMessage()
            );
        }
    }

    /**
     * Check Docker health
     */
    private Map<String, Object> checkDockerHealth() {
        try {
            int activeContainers = containerManager.getActiveContainerCount();
            return Map.of(
                "status", "healthy",
                "active_containers", activeContainers,
                "mode", "one-time-execution"
            );
        } catch (Exception e) {
            return Map.of(
                "status", "unhealthy",
                "error", e.getMessage()
            );
        }
    }

    /**
     * Check queue health
     */
    private Map<String, Object> checkQueueHealth() {
        try {
            ExecutionJobScheduler.QueueStatistics stats = executionJobScheduler.getQueueStatistics();
            long queueSize = submissionService.countSubmissionsInQueue();
            
            return Map.of(
                "status", queueSize < configService.getMaxQueueSize() ? "healthy" : "full",
                "queue_size", queueSize,
                "max_queue_size", configService.getMaxQueueSize(),
                "enqueued_jobs", stats.getEnqueuedJobs()
            );
        } catch (Exception e) {
            return Map.of(
                "status", "unhealthy",
                "error", e.getMessage()
            );
        }
    }

    /**
     * Get system uptime (simplified)
     */
    private String getUptime() {
        long uptimeMs = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return String.format("%d days, %d hours, %d minutes", 
                days, hours % 24, minutes % 60);
    }
}
