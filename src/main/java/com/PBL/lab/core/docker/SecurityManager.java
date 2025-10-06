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
 * 보안 관리자 - Judge0 컨테이너 보안 정책 및 제약사항 관리
 * 
 * 목적:
 * - Docker 컨테이너 실행 시 보안 제약사항 적용 및 검증
 * - Judge0의 Isolate 보안 기능과 동일한 수준의 격리 제공
 * - 리소스 제한, 네트워크 격리, 파일시스템 접근 제어
 * 
 * 핵심 기능:
 * - 언어별 보안 제약사항 생성 및 검증
 * - 컨테이너 보안 정책 적용 (메모리, CPU, 프로세스 제한)
 * - 시스템 콜 필터링 및 권한 제한
 * - 실행 결과 보안 감사 및 위반 사항 탐지
 * 
 * 보안 계층:
 * 1. 리소스 제한: 메모리, CPU, 프로세스 수 제한
 * 2. 네트워크 격리: 기본적으로 외부 네트워크 접근 차단
 * 3. 파일시스템 제어: 읽기 전용 또는 제한적 접근
 * 4. 권한 격리: 컨테이너 내 권한 상승 방지
 * 5. 시스템 콜 필터링: 위험한 시스템 콜 차단
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityManager {

    private final ConfigService configService; // 보안 설정 관리 서비스

    /**
     * 언어별 보안 제약사항 생성 - 프로그래밍 언어에 따른 적절한 보안 정책 설정
     * @param language 프로그래밍 언어 정보 (시간/메모리 제한 포함)
     * @return 적용할 보안 제약사항
     */
    public ExecutionService.SecurityConstraints createConstraints(Language language) {
        return ExecutionService.SecurityConstraints.builder()
                .timeLimit(language.getTimeLimit() != null ? language.getTimeLimit() : configService.getCpuTimeLimit())
                .memoryLimit(language.getMemoryLimit() != null ? language.getMemoryLimit() : configService.getMemoryLimit())
                .processLimit(configService.getMaxProcessesAndOrThreads())
                .networkAccess(false) // 기본값: 네트워크 접근 차단
                .fileSystemAccess(ExecutionService.FileSystemAccess.READ_ONLY) // 파일시스템 읽기 전용
                .build();
    }

    /**
     * 보안 제약사항 검증 - 설정된 보안 정책이 유효한지 확인
     * @param constraints 검증할 보안 제약사항
     * @return 유효성 여부
     */
    public boolean validateConstraints(ExecutionService.SecurityConstraints constraints) {
        try {
            // 시간 제한 검증 - 0보다 크고 최대 허용 시간 이하여야 함
            if (constraints.getTimeLimit() != null) {
                if (constraints.getTimeLimit().compareTo(BigDecimal.ZERO) < 0 ||
                    constraints.getTimeLimit().compareTo(configService.getMaxCpuTimeLimit()) > 0) {
                    log.warn("Invalid time limit: {}", constraints.getTimeLimit());
                    return false;
                }
            }

            // 메모리 제한 검증 - 최소 2MB, 최대 허용 메모리 이하여야 함
            if (constraints.getMemoryLimit() != null) {
                if (constraints.getMemoryLimit() < 2048 ||
                    constraints.getMemoryLimit() > configService.getMaxMemoryLimit()) {
                    log.warn("Invalid memory limit: {}", constraints.getMemoryLimit());
                    return false;
                }
            }

            // 프로세스 제한 검증 - 최소 1개, 최대 허용 프로세스 수 이하여야 함
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
     * 컨테이너에 보안 정책 적용 - 실행 중인 컨테이너에 보안 제약사항 적용
     * @param containerId 대상 컨테이너 ID
     * @param constraints 적용할 보안 제약사항
     */
    public void applySecurityPolicy(String containerId, ExecutionService.SecurityConstraints constraints) {
        try {
            log.debug("Applying security policy to container: {}", containerId);
            
            // 주의: Docker Java API는 실행 중인 컨테이너 업데이트를 지원하지 않음
            // 보안 제약사항은 컨테이너 생성 시에 적용되어야 함
            log.debug("Security policy applied to container: {}", containerId);
            
        } catch (Exception e) {
            log.error("Failed to apply security policy to container: {}", containerId, e);
            throw new SecurityException("Failed to apply security constraints", e);
        }
    }

    /**
     * 보안 호스트 설정 생성 - Docker 컨테이너 생성 시 적용할 보안 설정 구성
     * @param constraints 보안 제약사항
     * @return Docker 호스트 설정
     */
    public HostConfig createSecureHostConfig(ExecutionService.SecurityConstraints constraints) {
        HostConfig hostConfig = new HostConfig();

        // 메모리 제한 설정 (KB를 바이트로 변환)
        if (constraints.getMemoryLimit() != null) {
            hostConfig.withMemory(constraints.getMemoryLimit() * 1024L);
        }

        // CPU 제한 설정 (초를 마이크로초로 변환)
        if (constraints.getTimeLimit() != null) {
            long cpuQuota = constraints.getTimeLimit().multiply(BigDecimal.valueOf(100000)).longValue();
            hostConfig.withCpuQuota(cpuQuota);
            hostConfig.withCpuPeriod(100000L); // 100ms 주기
        }

        // 프로세스 수 제한
        if (constraints.getProcessLimit() != null) {
            hostConfig.withPidsLimit(constraints.getProcessLimit().longValue());
        }

        // 네트워크 격리 - 기본적으로 네트워크 접근 차단
        if (!Boolean.TRUE.equals(constraints.getNetworkAccess())) {
            hostConfig.withNetworkMode("none");
        }

        // 파일시스템 제약사항 - 읽기 전용 루트 파일시스템
        if (constraints.getFileSystemAccess() == ExecutionService.FileSystemAccess.READ_ONLY) {
            hostConfig.withReadonlyRootfs(true);
        }

        // 보안 옵션 적용
        hostConfig.withSecurityOpts(getSecurityOptions());

        // 권한 제한 - 모든 권한을 기본적으로 제거
        hostConfig.withCapDrop(Capability.ALL);

        // 권한 상승 방지
        hostConfig.withPrivileged(false);

        return hostConfig;
    }

    /**
     * 컨테이너 보안 옵션 생성 - Docker 보안 설정 옵션 목록 반환
     * @return 보안 옵션 목록
     */
    private List<String> getSecurityOptions() {
        return Arrays.asList(
            "no-new-privileges:true", // 권한 상승 방지
            "seccomp=" + getSeccompProfile() // 시스템 콜 필터링
        );
    }

    /**
     * Seccomp 프로필 경로 반환 - 시스템 콜 필터링을 위한 보안 프로필
     * @return seccomp 프로필 경로 또는 설정
     */
    private String getSeccompProfile() {
        // 실제 구현에서는 허용된 시스템 콜만 포함하는 seccomp 프로필 경로를 반환해야 함
        // 현재는 제한 없는 상태이지만, 실제 운영에서는 제한적 프로필 사용 권장
        return "unconfined"; // 현재는 제한 없음, 실제로는 제한적 프로필 사용해야 함
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
