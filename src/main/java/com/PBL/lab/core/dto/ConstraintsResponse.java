package com.PBL.lab.core.dto;

import com.PBL.lab.core.entity.Constraints;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintsResponse {
    // 제약조건들
    @JsonProperty("number_of_runs")
    private Integer numberOfRuns;

    @JsonProperty("cpu_time_limit")
    private BigDecimal cpuTimeLimit;

    @JsonProperty("cpu_extra_time")
    private BigDecimal cpuExtraTime;

    @JsonProperty("wall_time_limit")
    private BigDecimal wallTimeLimit;

    @JsonProperty("memory_limit")
    private Integer memoryLimit;

    @JsonProperty("stack_limit")
    private Integer stackLimit;

    @JsonProperty("max_processes_and_or_threads")
    private Integer maxProcessesAndOrThreads;

    @JsonProperty("enable_per_process_and_thread_time_limit")
    private Boolean enablePerProcessAndThreadTimeLimit;

    @JsonProperty("enable_per_process_and_thread_memory_limit")
    private Boolean enablePerProcessAndThreadMemoryLimit;

    @JsonProperty("max_file_size")
    private Integer maxFileSize;

    @JsonProperty("compiler_options")
    private String compilerOptions;

    @JsonProperty("command_line_arguments")
    private String commandLineArguments;

    @JsonProperty("redirect_stderr_to_stdout")
    private Boolean redirectStderrToStdout;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("additional_files")
    private String additionalFiles;

    @JsonProperty("enable_network")
    private Boolean enableNetwork;

    public static ConstraintsResponse from(Constraints entity) {
        if (entity == null) {
            return null;
        }
        
        return ConstraintsResponse.builder()
                .numberOfRuns(entity.getNumberOfRuns())
                .cpuTimeLimit(entity.getCpuTimeLimit())
                .cpuExtraTime(entity.getCpuExtraTime())
                .wallTimeLimit(entity.getWallTimeLimit())
                .memoryLimit(entity.getMemoryLimit())
                .stackLimit(entity.getStackLimit())
                .maxProcessesAndOrThreads(entity.getMaxProcessesAndOrThreads())
                .enablePerProcessAndThreadTimeLimit(entity.getEnablePerProcessAndThreadTimeLimit())
                .enablePerProcessAndThreadMemoryLimit(entity.getEnablePerProcessAndThreadMemoryLimit())
                .maxFileSize(entity.getMaxFileSize())
                .compilerOptions(entity.getCompilerOptions())
                .commandLineArguments(entity.getCommandLineArguments())
                .redirectStderrToStdout(entity.getRedirectStderrToStdout())
                .callbackUrl(entity.getCallbackUrl())
                .additionalFiles(entity.getAdditionalFiles() != null ? 
                        java.util.Base64.getEncoder().encodeToString(entity.getAdditionalFiles()) : null)
                .enableNetwork(entity.getEnableNetwork())
                .build();
    }
}
