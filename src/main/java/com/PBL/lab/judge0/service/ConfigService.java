package com.PBL.lab.judge0.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration Service - Judge0 시스템 설정 관리 서비스
 * 
 * 목적:
 * - Judge0 시스템의 모든 설정 매개변수 중앙 집중 관리
 * - 원본 Judge0 Ruby Config 헬퍼와 동등한 역할
 * - 실시간 설정 변경 및 시스템 동작 제어
 * 
 * 주요 설정 영역:
 * 1. 시스템 운영: 유지보수 모드, 대기열 크기 제한
 * 2. 기능 플래그: 동기 실행, 컴파일러 옵션, 콜백 등 기능 온/오프
 * 3. 실행 제한: CPU, 메모리, 시간 제한 기본값 및 최대값
 * 4. 보안 설정: 허용 언어, 시스템 호출, 새드박스 설정
 * 5. Docker 설정: 컨테이너 수, 제한 시간, 데몬 연결
 * 6. 외부 연동: 콜백 재시도, 제한 시간 등
 * 
 * 주요 기능:
 * - getConfigInfo(): 전체 설정 정보를 Map 형태로 반환
 * - validateLimits(): 실행 제한값 유효성 검증
 * - getAllowedLanguagesForCompilerOptions(): 컴파일러 옵션 허용 언어 목록
 * - getAllowedSyscalls(): 허용된 시스템 호출 목록
 * 
 * 설정 방식:
 * - @Value 어노테이션으로 application.yml에서 자동 로드
 * - 기본값 제공로 안전한 시스템 운영 보장
 * - 환경변수로 운영 환경별 오버라이드 가능
 * 
 * 보안 전략:
 * - 기본적으로 안전한 기본값 (보수적 설정)
 * - 고급 기능은 명시적 활성화 필요
 * - 시스템 리소스 보호를 위한 제한 설정
 */
@Service
@Getter
public class ConfigService {

    // System configuration
    @Value("${judge0.maintenance.mode:false}")
    private boolean maintenanceMode;

    @Value("${judge0.maintenance.message:Judge0 is currently in maintenance.}")
    private String maintenanceMessage;

    // Feature flags
    @Value("${judge0.features.enable-wait-result:true}")
    private boolean enableWaitResult;

    @Value("${judge0.features.enable-compiler-options:true}")
    private boolean compilerOptionsEnabled;

    @Value("${judge0.features.enable-command-line-arguments:true}")
    private boolean commandLineArgumentsEnabled;

    @Value("${judge0.features.enable-submission-delete:false}")
    private boolean submissionDeleteEnabled;

    @Value("${judge0.features.enable-callbacks:true}")
    private boolean callbacksEnabled;

    @Value("${judge0.features.enable-additional-files:true}")
    private boolean additionalFilesEnabled;

    @Value("${judge0.features.enable-batched-submissions:true}")
    private boolean batchedSubmissionsEnabled;

    @Value("${judge0.features.allow-enable-network:true}")
    private boolean networkAllowed;

    @Value("${judge0.features.enable-network:false}")
    private boolean enableNetwork;

    public boolean getEnableNetwork() {
        return enableNetwork;
    }

    @Value("${judge0.features.disable-implicit-base64-encoding:false}")
    private boolean disableImplicitBase64Encoding;

    // Queue configuration
    @Value("${judge0.queue.max-size:100}")
    private Integer maxQueueSize;

    @Value("${judge0.queue.max-submission-batch-size:20}")
    private Integer maxSubmissionBatchSize;

    // Execution limits - defaults
    @Value("${judge0.execution.number-of-runs:1}")
    private Integer numberOfRuns;

    @Value("${judge0.execution.cpu-time-limit:5.0}")
    private BigDecimal cpuTimeLimit;

    @Value("${judge0.execution.cpu-extra-time:1.0}")
    private BigDecimal cpuExtraTime;

    @Value("${judge0.execution.wall-time-limit:10.0}")
    private BigDecimal wallTimeLimit;

    @Value("${judge0.execution.memory-limit:128000}")
    private Integer memoryLimit; // KB

    @Value("${judge0.execution.stack-limit:64000}")
    private Integer stackLimit; // KB

    @Value("${judge0.execution.max-processes-and-or-threads:60}")
    private Integer maxProcessesAndOrThreads;

    @Value("${judge0.execution.max-file-size:1024}")
    private Integer maxFileSize; // KB

    @Value("${judge0.execution.enable-per-process-and-thread-time-limit:false}")
    private Boolean enablePerProcessAndThreadTimeLimit;

    @Value("${judge0.execution.enable-per-process-and-thread-memory-limit:false}")
    private Boolean enablePerProcessAndThreadMemoryLimit;

    @Value("${judge0.execution.redirect-stderr-to-stdout:false}")
    private Boolean redirectStderrToStdout;

    // Execution limits - maximums
    @Value("${judge0.execution.max-number-of-runs:20}")
    private Integer maxNumberOfRuns;

    @Value("${judge0.execution.max-cpu-time-limit:15.0}")
    private BigDecimal maxCpuTimeLimit;

    @Value("${judge0.execution.max-cpu-extra-time:5.0}")
    private BigDecimal maxCpuExtraTime;

    @Value("${judge0.execution.max-wall-time-limit:20.0}")
    private BigDecimal maxWallTimeLimit;

    @Value("${judge0.execution.max-memory-limit:512000}")
    private Integer maxMemoryLimit; // KB

    @Value("${judge0.execution.max-stack-limit:128000}")
    private Integer maxStackLimit; // KB

    @Value("${judge0.execution.max-max-processes-and-or-threads:120}")
    private Integer maxMaxProcessesAndOrThreads;

    @Value("${judge0.execution.max-max-file-size:4096}")
    private Integer maxMaxFileSize; // KB

    @Value("${judge0.execution.max-extract-size:10240}")
    private Integer maxExtractSize; // KB

    // Per-process limits control
    @Value("${judge0.execution.allow-enable-per-process-and-thread-time-limit:true}")
    private Boolean allowEnablePerProcessAndThreadTimeLimit;

    @Value("${judge0.execution.allow-enable-per-process-and-thread-memory-limit:true}")
    private Boolean allowEnablePerProcessAndThreadMemoryLimit;

    // Compiler options configuration
    @Value("${judge0.compiler.allowed-languages:}")
    private String allowedLanguagesForCompilerOptionsStr;

    // Callback configuration
    @Value("${judge0.callbacks.max-tries:3}")
    private Integer callbacksMaxTries;

    @Value("${judge0.callbacks.timeout:5.0}")
    private Double callbacksTimeout;

    // Cache configuration
    @Value("${judge0.cache.submission-cache-duration:1.0}")
    private Double submissionCacheDuration;

    // Docker configuration
    @Value("${judge0.docker.host:unix:///var/run/docker.sock}")
    private String dockerHost;

    @Value("${judge0.docker.registry-url:docker.io}")
    private String dockerRegistryUrl;

    @Value("${judge0.docker.max-concurrent-containers:10}")
    private Integer maxConcurrentContainers;

    @Value("${judge0.docker.container-timeout:300}")
    private Integer containerTimeout; // seconds

    // Security configuration
    @Value("${judge0.security.sandbox-user:judge}")
    private String sandboxUser;

    @Value("${judge0.security.allowed-syscalls:read,write,exit,exit_group}")
    private String allowedSyscallsStr;

    /**
     * Get allowed languages for compiler options as list
     */
    public List<String> getAllowedLanguagesForCompilerOptions() {
        if (allowedLanguagesForCompilerOptionsStr == null || allowedLanguagesForCompilerOptionsStr.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(allowedLanguagesForCompilerOptionsStr.trim().split("\\s+"));
    }

    /**
     * Get allowed system calls as list
     */
    public List<String> getAllowedSyscalls() {
        if (allowedSyscallsStr == null || allowedSyscallsStr.trim().isEmpty()) {
            return List.of("read", "write", "exit", "exit_group");
        }
        return Arrays.asList(allowedSyscallsStr.split(","));
    }

    /**
     * Get configuration info map
     */
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> config = new HashMap<>();
        config.put("maintenance_mode", maintenanceMode);
        config.put("enable_wait_result", enableWaitResult);
        config.put("enable_compiler_options", compilerOptionsEnabled);
        config.put("allowed_languages_for_compiler_options", getAllowedLanguagesForCompilerOptions());
        config.put("enable_command_line_arguments", commandLineArgumentsEnabled);
        config.put("enable_submission_delete", submissionDeleteEnabled);
        config.put("enable_callbacks", callbacksEnabled);
        config.put("callbacks_max_tries", callbacksMaxTries);
        config.put("callbacks_timeout", callbacksTimeout);
        config.put("enable_additional_files", additionalFilesEnabled);
        config.put("max_queue_size", maxQueueSize);
        config.put("cpu_time_limit", cpuTimeLimit);
        config.put("max_cpu_time_limit", maxCpuTimeLimit);
        config.put("cpu_extra_time", cpuExtraTime);
        config.put("max_cpu_extra_time", maxCpuExtraTime);
        config.put("wall_time_limit", wallTimeLimit);
        config.put("max_wall_time_limit", maxWallTimeLimit);
        config.put("memory_limit", memoryLimit);
        config.put("max_memory_limit", maxMemoryLimit);
        config.put("stack_limit", stackLimit);
        config.put("max_stack_limit", maxStackLimit);
        config.put("max_processes_and_or_threads", maxProcessesAndOrThreads);
        config.put("max_max_processes_and_or_threads", maxMaxProcessesAndOrThreads);
        config.put("enable_per_process_and_thread_time_limit", enablePerProcessAndThreadTimeLimit);
        config.put("allow_enable_per_process_and_thread_time_limit", allowEnablePerProcessAndThreadTimeLimit);
        config.put("enable_per_process_and_thread_memory_limit", enablePerProcessAndThreadMemoryLimit);
        config.put("allow_enable_per_process_and_thread_memory_limit", allowEnablePerProcessAndThreadMemoryLimit);
        config.put("max_file_size", maxFileSize);
        config.put("max_max_file_size", maxMaxFileSize);
        config.put("number_of_runs", numberOfRuns);
        config.put("max_number_of_runs", maxNumberOfRuns);
        config.put("redirect_stderr_to_stdout", redirectStderrToStdout);
        config.put("max_extract_size", maxExtractSize);
        config.put("enable_batched_submissions", batchedSubmissionsEnabled);
        config.put("max_submission_batch_size", maxSubmissionBatchSize);
        config.put("submission_cache_duration", submissionCacheDuration);
        config.put("allow_enable_network", networkAllowed);
        config.put("enable_network", enableNetwork);
        config.put("disable_implicit_base64_encoding", disableImplicitBase64Encoding);
        return config;
    }

    /**
     * Validate execution limits
     */
    public void validateLimits(Integer numberOfRuns, BigDecimal cpuTimeLimit, BigDecimal cpuExtraTime,
                              BigDecimal wallTimeLimit, Integer memoryLimit, Integer stackLimit,
                              Integer maxProcesses, Integer maxFileSize) {
        
        if (numberOfRuns != null && (numberOfRuns < 1 || numberOfRuns > maxNumberOfRuns)) {
            throw new IllegalArgumentException("Number of runs must be between 1 and " + maxNumberOfRuns);
        }

        if (cpuTimeLimit != null && (cpuTimeLimit.compareTo(BigDecimal.ZERO) < 0 || cpuTimeLimit.compareTo(maxCpuTimeLimit) > 0)) {
            throw new IllegalArgumentException("CPU time limit must be between 0 and " + maxCpuTimeLimit + " seconds");
        }

        if (cpuExtraTime != null && (cpuExtraTime.compareTo(BigDecimal.ZERO) < 0 || cpuExtraTime.compareTo(maxCpuExtraTime) > 0)) {
            throw new IllegalArgumentException("CPU extra time must be between 0 and " + maxCpuExtraTime + " seconds");
        }

        if (wallTimeLimit != null && (wallTimeLimit.compareTo(BigDecimal.ONE) < 0 || wallTimeLimit.compareTo(maxWallTimeLimit) > 0)) {
            throw new IllegalArgumentException("Wall time limit must be between 1 and " + maxWallTimeLimit + " seconds");
        }

        if (memoryLimit != null && (memoryLimit < 2048 || memoryLimit > maxMemoryLimit)) {
            throw new IllegalArgumentException("Memory limit must be between 2048 and " + maxMemoryLimit + " KB");
        }

        if (stackLimit != null && (stackLimit < 0 || stackLimit > maxStackLimit)) {
            throw new IllegalArgumentException("Stack limit must be between 0 and " + maxStackLimit + " KB");
        }

        if (maxProcesses != null && (maxProcesses < 1 || maxProcesses > maxMaxProcessesAndOrThreads)) {
            throw new IllegalArgumentException("Max processes must be between 1 and " + maxMaxProcessesAndOrThreads);
        }

        if (maxFileSize != null && (maxFileSize < 0 || maxFileSize > maxMaxFileSize)) {
            throw new IllegalArgumentException("Max file size must be between 0 and " + maxMaxFileSize + " KB");
        }
    }
}
