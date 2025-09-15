package com.PBL.lab.judge0.controller;

import com.PBL.lab.judge0.docker.ContainerPool;
import com.PBL.lab.judge0.service.DockerExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Container Pool Controller
 * 
 * Provides endpoints for monitoring and managing the container pool.
 * Useful for debugging and performance monitoring.
 */
@RestController
@RequestMapping("/api/pool")
@RequiredArgsConstructor
@Slf4j
public class ContainerPoolController {

    private final DockerExecutionService dockerExecutionService;

    /**
     * Get container pool statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPoolStatistics() {
        try {
            ContainerPool.PoolStatistics stats = dockerExecutionService.getPoolStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "healthy");
            response.put("pool", Map.of(
                "total_containers", stats.getTotalContainers(),
                "available_containers", stats.getAvailableContainers(),
                "busy_containers", stats.getBusyContainers(),
                "total_requests", stats.getTotalRequests(),
                "pool_hits", stats.getPoolHits(),
                "pool_misses", stats.getPoolMisses(),
                "hit_rate_percentage", String.format("%.2f", stats.getHitRate())
            ));
            
            // Add recommendations based on stats
            response.put("recommendations", getRecommendations(stats));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get pool statistics", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to retrieve pool statistics"));
        }
    }

    /**
     * Get pool health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getPoolHealth() {
        try {
            ContainerPool.PoolStatistics stats = dockerExecutionService.getPoolStatistics();
            
            boolean healthy = isPoolHealthy(stats);
            String status = healthy ? "healthy" : "degraded";
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("healthy", healthy);
            response.put("available_ratio", 
                    stats.getTotalContainers() > 0 ? 
                    (double) stats.getAvailableContainers() / stats.getTotalContainers() : 0);
            response.put("utilization", 
                    stats.getTotalContainers() > 0 ? 
                    (double) stats.getBusyContainers() / stats.getTotalContainers() * 100 : 0);
            
            if (!healthy) {
                response.put("issues", identifyIssues(stats));
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to check pool health", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to check pool health"));
        }
    }

    /**
     * Get detailed pool information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getPoolInfo() {
        try {
            ContainerPool.PoolStatistics stats = dockerExecutionService.getPoolStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("configuration", Map.of(
                "min_pool_size", "${judge0.container-pool.min-size}",
                "max_pool_size", "${judge0.container-pool.max-size}",
                "max_idle_time_ms", "${judge0.container-pool.max-idle-time}",
                "max_container_uses", "${judge0.container-pool.max-uses}",
                "health_check_interval_ms", "${judge0.container-pool.health-check-interval}",
                "container_image", "${judge0.container-pool.image}"
            ));
            
            response.put("current_state", Map.of(
                "total_containers", stats.getTotalContainers(),
                "available_containers", stats.getAvailableContainers(),
                "busy_containers", stats.getBusyContainers()
            ));
            
            response.put("performance", Map.of(
                "total_requests", stats.getTotalRequests(),
                "pool_hits", stats.getPoolHits(),
                "pool_misses", stats.getPoolMisses(),
                "hit_rate_percentage", String.format("%.2f", stats.getHitRate()),
                "average_acquire_time_estimate_ms", estimateAverageAcquireTime(stats)
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get pool info", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to retrieve pool information"));
        }
    }

    /**
     * Check if pool is healthy
     */
    private boolean isPoolHealthy(ContainerPool.PoolStatistics stats) {
        // Pool is healthy if:
        // 1. At least one container is available
        // 2. Hit rate is above 70% (if enough requests have been made)
        // 3. Not all containers are busy
        
        if (stats.getTotalContainers() == 0) {
            return false;
        }
        
        if (stats.getAvailableContainers() == 0 && stats.getBusyContainers() == stats.getTotalContainers()) {
            return false; // All containers are busy
        }
        
        if (stats.getTotalRequests() > 100 && stats.getHitRate() < 70) {
            return false; // Poor hit rate after warm-up
        }
        
        return true;
    }

    /**
     * Identify issues with the pool
     */
    private java.util.List<String> identifyIssues(ContainerPool.PoolStatistics stats) {
        java.util.List<String> issues = new java.util.ArrayList<>();
        
        if (stats.getTotalContainers() == 0) {
            issues.add("No containers in pool");
        }
        
        if (stats.getAvailableContainers() == 0) {
            issues.add("No available containers - pool exhausted");
        }
        
        if (stats.getTotalRequests() > 100 && stats.getHitRate() < 70) {
            issues.add("Low hit rate - consider increasing pool size");
        }
        
        if (stats.getBusyContainers() > stats.getTotalContainers() * 0.8) {
            issues.add("High utilization - pool near capacity");
        }
        
        return issues;
    }

    /**
     * Get recommendations based on pool statistics
     */
    private java.util.List<String> getRecommendations(ContainerPool.PoolStatistics stats) {
        java.util.List<String> recommendations = new java.util.ArrayList<>();
        
        if (stats.getHitRate() < 80 && stats.getTotalRequests() > 100) {
            recommendations.add("Consider increasing min pool size for better hit rate");
        }
        
        if (stats.getBusyContainers() > stats.getTotalContainers() * 0.8) {
            recommendations.add("Pool is running at high capacity - consider increasing max pool size");
        }
        
        if (stats.getAvailableContainers() > stats.getTotalContainers() * 0.8 && 
            stats.getTotalContainers() > 10) {
            recommendations.add("Pool has many idle containers - consider decreasing min pool size");
        }
        
        if (stats.getPoolMisses() > stats.getPoolHits() * 0.5) {
            recommendations.add("High miss rate - pool size may be too small for workload");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Pool is operating normally");
        }
        
        return recommendations;
    }

    /**
     * Estimate average container acquire time
     */
    private long estimateAverageAcquireTime(ContainerPool.PoolStatistics stats) {
        // This is a rough estimate based on hit rate
        // Hits are fast (~10ms), misses require container creation (~500ms)
        
        if (stats.getTotalRequests() == 0) {
            return 0;
        }
        
        double hitRate = stats.getHitRate() / 100.0;
        double missRate = 1.0 - hitRate;
        
        long hitTime = 10; // 10ms for a hit
        long missTime = 500; // 500ms for a miss (container creation)
        
        return Math.round(hitTime * hitRate + missTime * missRate);
    }
}
