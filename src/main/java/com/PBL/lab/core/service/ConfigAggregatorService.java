package com.PBL.lab.core.service;

import com.PBL.lab.core.config.ExecutionLimitsConfig;
import com.PBL.lab.core.config.FeatureFlagsConfig;
import com.PBL.lab.core.config.SystemConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Aggregator Service
 *
 * 모든 Config 클래스들을 통합하여 관리하는 서비스입니다.
 * 각 Config 클래스의 설정값들을 하나의 Map으로 제공합니다.
 *
 * 주요 기능:
 * - 3개 Config 클래스 통합 관리
 * - 전체 설정 정보 Map 형태로 제공 (getConfigInfo)
 * - API 응답 및 관리자 페이지에서 사용
 *
 * Config 구성:
 * 1. SystemConfig - 시스템 운영 설정 (maintenance, queue, cache, security, callbacks, compiler)
 * 2. FeatureFlagsConfig - 기능 플래그 설정
 * 3. ExecutionLimitsConfig - 실행 제한 설정
 */
@Service
@RequiredArgsConstructor
public class ConfigAggregatorService {

    private final SystemConfig systemConfig;
    private final FeatureFlagsConfig featureFlagsConfig;
    private final ExecutionLimitsConfig executionLimitsConfig;

    /**
     * 전체 설정 정보를 Map 형태로 반환
     * - API 응답이나 관리자 페이지에서 사용
     * - 모든 설정값을 키-값 쌍으로 구성하여 반환
     * - 클라이언트가 현재 시스템 설정을 확인할 수 있도록 제공
     *
     * @return 설정 정보가 담긴 Map 객체
     */
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> config = new HashMap<>();

        // System Configuration - Maintenance
        config.put("maintenance_mode", systemConfig.isMaintenanceMode());

        // Feature Flags Configuration
        config.put("enable_wait_result", featureFlagsConfig.isEnableWaitResult());
        config.put("enable_compiler_options", featureFlagsConfig.isEnableCompilerOptions());
        config.put("allowed_languages_for_compiler_options", systemConfig.getAllowedLanguagesForCompilerOptions());
        config.put("enable_command_line_arguments", featureFlagsConfig.isEnableCommandLineArguments());
        config.put("enable_submission_delete", featureFlagsConfig.isEnableSubmissionDelete());
        config.put("enable_callbacks", featureFlagsConfig.isEnableCallbacks());
        config.put("enable_additional_files", featureFlagsConfig.isEnableAdditionalFiles());
        config.put("enable_batched_submissions", featureFlagsConfig.isEnableBatchedSubmissions());
        config.put("allow_enable_network", featureFlagsConfig.isAllowEnableNetwork());
        config.put("enable_network", featureFlagsConfig.getEnableNetwork());
        config.put("disable_implicit_base64_encoding", featureFlagsConfig.isDisableImplicitBase64Encoding());

        // System Configuration - Callbacks
        config.put("callbacks_max_tries", systemConfig.getCallbacksMaxTries());
        config.put("callbacks_timeout", systemConfig.getCallbacksTimeout());

        // System Configuration - Queue
        config.put("max_queue_size", systemConfig.getMaxQueueSize());
        config.put("max_submission_batch_size", systemConfig.getMaxSubmissionBatchSize());

        // Execution Limits Configuration - Defaults
        config.put("number_of_runs", executionLimitsConfig.getNumberOfRuns());
        config.put("cpu_time_limit", executionLimitsConfig.getCpuTimeLimit());
        config.put("cpu_extra_time", executionLimitsConfig.getCpuExtraTime());
        config.put("wall_time_limit", executionLimitsConfig.getWallTimeLimit());
        config.put("memory_limit", executionLimitsConfig.getMemoryLimit());
        config.put("stack_limit", executionLimitsConfig.getStackLimit());
        config.put("max_processes_and_or_threads", executionLimitsConfig.getMaxProcessesAndOrThreads());
        config.put("max_file_size", executionLimitsConfig.getMaxFileSize());
        config.put("enable_per_process_and_thread_time_limit", executionLimitsConfig.getEnablePerProcessAndThreadTimeLimit());
        config.put("enable_per_process_and_thread_memory_limit", executionLimitsConfig.getEnablePerProcessAndThreadMemoryLimit());
        config.put("redirect_stderr_to_stdout", executionLimitsConfig.getRedirectStderrToStdout());

        // Execution Limits Configuration - Maximums
        config.put("max_number_of_runs", executionLimitsConfig.getMaxNumberOfRuns());
        config.put("max_cpu_time_limit", executionLimitsConfig.getMaxCpuTimeLimit());
        config.put("max_cpu_extra_time", executionLimitsConfig.getMaxCpuExtraTime());
        config.put("max_wall_time_limit", executionLimitsConfig.getMaxWallTimeLimit());
        config.put("max_memory_limit", executionLimitsConfig.getMaxMemoryLimit());
        config.put("max_stack_limit", executionLimitsConfig.getMaxStackLimit());
        config.put("max_max_processes_and_or_threads", executionLimitsConfig.getMaxMaxProcessesAndOrThreads());
        config.put("max_max_file_size", executionLimitsConfig.getMaxMaxFileSize());
        config.put("max_extract_size", executionLimitsConfig.getMaxExtractSize());

        // Execution Limits Configuration - Controls
        config.put("allow_enable_per_process_and_thread_time_limit", executionLimitsConfig.getAllowEnablePerProcessAndThreadTimeLimit());
        config.put("allow_enable_per_process_and_thread_memory_limit", executionLimitsConfig.getAllowEnablePerProcessAndThreadMemoryLimit());

        // System Configuration - Cache
        config.put("submission_cache_duration", systemConfig.getSubmissionCacheDuration());

        return config;
    }
}
