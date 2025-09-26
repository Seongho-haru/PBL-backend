package com.PBL.lab.grading.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.core.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Grading Response DTO
 * 
 * Data Transfer Object for returning grading data.
 * Represents the response payload for grading endpoints.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradingResponse {

    private String token;

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

    @JsonProperty("constraints_id")
    private Long constraintsId;

    private String stdin;

    @JsonProperty("expected_output")
    private String expectedOutput;

    private String stdout;

    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    private String message;

    private StatusResponse status;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonProperty("finished_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime finishedAt;

    private BigDecimal time;

    @JsonProperty("wall_time")
    private BigDecimal wallTime;

    private Integer memory;

    @JsonProperty("exit_code")
    private Integer exitCode;

    @JsonProperty("exit_signal")
    private Integer exitSignal;

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

    // Grading-specific fields
    @JsonProperty("problem_id")
    private Long problemId;

    /**
     * Status Response nested class
     */
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatusResponse {
        private Integer id;
        private String description;

        public static StatusResponse from(Status status) {
            if (status == null) {
                return null;
            }
            return StatusResponse.builder()
                    .id(status.getId())
                    .description(status.getName())
                    .build();
        }
    }

    /**
     * Create GradingResponse from Grading entity
     */
    public static GradingResponse from(Grading grading) {
        return from(grading, false, null);
    }

    /**
     * Create GradingResponse from Grading entity with encoding option
     */
    public static GradingResponse from(Grading grading, boolean base64Encoded, String[] requestedFields) {
        if (grading == null) {
            return null;
        }

        GradingResponseBuilder builder = GradingResponse.builder()
                .token(grading.getToken())
                .languageId(grading.getLanguageId())
                .status(StatusResponse.from(grading.getStatus()))
                .createdAt(grading.getCreatedAt())
                .finishedAt(grading.getFinishedAt())
                .time(grading.getTime())
                .wallTime(grading.getWallTime())
                .memory(grading.getMemory())
                .exitCode(grading.getExitCode())
                .exitSignal(grading.getExitSignal())
                .message(grading.getMessage())
                .stdout(grading.getStdout())
                .stderr(grading.getStderr())
                .compileOutput(grading.getCompileOutput())
                .problemId(grading.getProblemId());
        
        // stdin과 expectedOutput은 Grading에서는 직접 저장하지 않음
        builder.stdin(null)
                .expectedOutput(null);
        
        // 제약조건 설정
        if (grading.getConstraints() != null) {
            builder.numberOfRuns(grading.getConstraints().getNumberOfRuns())
                    .cpuTimeLimit(grading.getConstraints().getCpuTimeLimit())
                    .cpuExtraTime(grading.getConstraints().getCpuExtraTime())
                    .wallTimeLimit(grading.getConstraints().getWallTimeLimit())
                    .memoryLimit(grading.getConstraints().getMemoryLimit())
                    .stackLimit(grading.getConstraints().getStackLimit())
                    .maxProcessesAndOrThreads(grading.getConstraints().getMaxProcessesAndOrThreads())
                    .enablePerProcessAndThreadTimeLimit(grading.getConstraints().getEnablePerProcessAndThreadTimeLimit())
                    .enablePerProcessAndThreadMemoryLimit(grading.getConstraints().getEnablePerProcessAndThreadMemoryLimit())
                    .maxFileSize(grading.getConstraints().getMaxFileSize())
                    .compilerOptions(grading.getConstraints().getCompilerOptions())
                    .commandLineArguments(grading.getConstraints().getCommandLineArguments())
                    .redirectStderrToStdout(grading.getConstraints().getRedirectStderrToStdout())
                    .callbackUrl(grading.getConstraints().getCallbackUrl())
                    .enableNetwork(grading.getConstraints().getEnableNetwork());
            
            // 추가 파일 처리
            if (grading.getConstraints().getAdditionalFiles() != null) {
                builder.additionalFiles(java.util.Base64.getEncoder().encodeToString(grading.getConstraints().getAdditionalFiles()));
            }
        }

        // Handle text fields with base64 encoding
        if (base64Encoded) {
            builder.sourceCode(grading.getSourceCode());
        } else {
            // For non-base64, we need to decode if stored as base64
            builder.sourceCode(grading.getSourceCode());
        }

        // 채점에서는 추가 파일이 필요 없음

        return builder.build();
    }

    /**
     * Create minimal response with only token (for async gradings)
     */
    public static GradingResponse minimal(String token) {
        return GradingResponse.builder()
                .token(token)
                .build();
    }
}
