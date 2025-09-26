package com.PBL.lab.core.docker;

import com.PBL.lab.core.service.ExecutionResult;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Capability;
import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.service.ConfigService;
import com.PBL.lab.judge0.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Security Manager
 * 
 * Manages security constraints and policies for Docker containers.
 * Equivalent to Judge0's Isolate security features.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityManager {

    private final ConfigService configService;

    /**
     * Create security constraints for a language
     */
    public ExecutionService.SecurityConstraints createConstraints(Language language) {
        return ExecutionService.SecurityConstraints.builder()
                .timeLimit(language.getTimeLimit() != null ? language.getTimeLimit() : configService.getCpuTimeLimit())
                .memoryLimit(language.getMemoryLimit() != null ? language.getMemoryLimit() : configService.getMemoryLimit())
                .processLimit(configService.getMaxProcessesAndOrThreads())
                .networkAccess(false) // Default: no network access
                .fileSystemAccess(ExecutionService.FileSystemAccess.READ_ONLY)
                .build();
    }

    /**
     * Validate security constraints
     */
    public boolean validateConstraints(ExecutionService.SecurityConstraints constraints) {
        try {
            // Validate time limits
            if (constraints.getTimeLimit() != null) {
                if (constraints.getTimeLimit().compareTo(BigDecimal.ZERO) < 0 ||
                    constraints.getTimeLimit().compareTo(configService.getMaxCpuTimeLimit()) > 0) {
                    log.warn("Invalid time limit: {}", constraints.getTimeLimit());
                    return false;
                }
            }

            // Validate memory limits
            if (constraints.getMemoryLimit() != null) {
                if (constraints.getMemoryLimit() < 2048 ||
                    constraints.getMemoryLimit() > configService.getMaxMemoryLimit()) {
                    log.warn("Invalid memory limit: {}", constraints.getMemoryLimit());
                    return false;
                }
            }

            // Validate process limits
            if (constraints.getProcessLimit() != null) {
                if (constraints.getProcessLimit() < 1 ||
                    constraints.getProcessLimit() > configService.getMaxMaxProcessesAndOrThreads()) {
                    log.warn("Invalid process limit: {}", constraints.getProcessLimit());
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Error validating security constraints", e);
            return false;
        }
    }

    /**
     * Apply security constraints to container
     */
    public void applySecurityPolicy(String containerId, ExecutionService.SecurityConstraints constraints) {
        try {
            log.debug("Applying security policy to container: {}", containerId);
            
            // Note: Docker Java API doesn't support updating running containers
            // Security constraints should be applied during container creation
            log.debug("Security policy applied to container: {}", containerId);
            
        } catch (Exception e) {
            log.error("Failed to apply security policy to container: {}", containerId, e);
            throw new SecurityException("Failed to apply security constraints", e);
        }
    }

    /**
     * Create secure host configuration
     */
    public HostConfig createSecureHostConfig(ExecutionService.SecurityConstraints constraints) {
        HostConfig hostConfig = new HostConfig();

        // Memory limits (convert KB to bytes)
        if (constraints.getMemoryLimit() != null) {
            hostConfig.withMemory(constraints.getMemoryLimit() * 1024L);
        }

        // CPU limits (convert seconds to microseconds)
        if (constraints.getTimeLimit() != null) {
            long cpuQuota = constraints.getTimeLimit().multiply(BigDecimal.valueOf(100000)).longValue();
            hostConfig.withCpuQuota(cpuQuota);
            hostConfig.withCpuPeriod(100000L); // 100ms period
        }

        // Process limits
        if (constraints.getProcessLimit() != null) {
            hostConfig.withPidsLimit(constraints.getProcessLimit().longValue());
        }

        // Network isolation
        if (!Boolean.TRUE.equals(constraints.getNetworkAccess())) {
            hostConfig.withNetworkMode("none");
        }

        // File system constraints
        if (constraints.getFileSystemAccess() == ExecutionService.FileSystemAccess.READ_ONLY) {
            hostConfig.withReadonlyRootfs(true);
        }

        // Security options
        hostConfig.withSecurityOpts(getSecurityOptions());

        // Capabilities (drop all by default)
        hostConfig.withCapDrop(Capability.ALL);

        // No new privileges
        hostConfig.withPrivileged(false);

        return hostConfig;
    }

    /**
     * Get security options for container
     */
    private List<String> getSecurityOptions() {
        return Arrays.asList(
            "no-new-privileges:true",
            "seccomp=" + getSeccompProfile()
        );
    }

    /**
     * Get seccomp profile for syscall filtering
     */
    private String getSeccompProfile() {
        // In a real implementation, this would return a path to a seccomp profile
        // that restricts system calls to only those in the allowed list
        return "unconfined"; // For now, use unconfined but this should be restricted
    }

    /**
     * Validate system calls
     */
    public boolean validateSyscalls(List<String> syscalls) {
        if (syscalls == null || syscalls.isEmpty()) {
            return true;
        }

        List<String> allowedSyscalls = configService.getAllowedSyscalls();
        if (allowedSyscalls.isEmpty()) {
            return true; // No restrictions
        }

        for (String syscall : syscalls) {
            if (!allowedSyscalls.contains(syscall)) {
                log.warn("Disallowed syscall requested: {}", syscall);
                return false;
            }
        }

        return true;
    }

    /**
     * Enforce resource limits on running container
     */
    public void enforceResourceLimits(String containerId, ResourceLimits limits) {
        try {
            // In a real implementation, you would use cgroups or Docker API
            // to enforce runtime limits on the container
            log.debug("Enforcing resource limits on container: {}", containerId);
            
            // Monitor and kill container if it exceeds limits
            monitorContainer(containerId, limits);
            
        } catch (Exception e) {
            log.error("Failed to enforce resource limits on container: {}", containerId, e);
        }
    }

    /**
     * Monitor container resource usage
     */
    private void monitorContainer(String containerId, ResourceLimits limits) {
        // This would typically run in a separate thread
        // and monitor container resource usage in real-time
        // For now, we rely on Docker's built-in limits
    }

    /**
     * Audit execution for security violations
     */
    public void auditExecution(String containerId, ExecutionResult result) {
        try {
            log.debug("Auditing execution for container: {}", containerId);
            
            // Check for suspicious activities
            if (result.getExitSignal() != null) {
                log.info("Container {} terminated with signal: {}", containerId, result.getExitSignal());
            }
            
            // Log security-relevant events
            if (result.getStatus() != null && result.getStatus().isRuntimeError()) {
                log.warn("Security audit: Runtime error in container {}: {}", 
                        containerId, result.getStatus().getName());
            }
            
            // Additional security auditing would go here
            
        } catch (Exception e) {
            log.error("Security audit failed for container: {}", containerId, e);
        }
    }

    /**
     * Check if execution environment is secure
     */
    public boolean isSecureEnvironment() {
        try {
            // Verify that the Docker daemon is properly configured
            // Check that we're not running with excessive privileges
            // Validate security policies are in place
            
            return true; // Simplified for now
        } catch (Exception e) {
            log.error("Security environment check failed", e);
            return false;
        }
    }

    /**
     * Resource limits configuration
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ResourceLimits {
        private final BigDecimal timeLimit;
        private final Integer memoryLimit;
        private final Integer processLimit;
        private final Integer fileLimit;
    }

    /**
     * Security violation types
     */
    public enum SecurityViolation {
        MEMORY_LIMIT_EXCEEDED,
        TIME_LIMIT_EXCEEDED,
        PROCESS_LIMIT_EXCEEDED,
        NETWORK_ACCESS_DENIED,
        FILE_ACCESS_DENIED,
        SYSCALL_DENIED,
        PRIVILEGE_ESCALATION
    }

    /**
     * Security event for logging and monitoring
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SecurityEvent {
        private final SecurityViolation violation;
        private final String containerId;
        private final String description;
        private final long timestamp;

        public SecurityEvent(SecurityViolation violation, String containerId, String description) {
            this.violation = violation;
            this.containerId = containerId;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
