package com.PBL.lab.judge0.dto;

import com.PBL.lab.judge0.validation.ValidSubmission;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Submission Request DTO
 * 
 * Data Transfer Object for creating submissions.
 * Represents the request payload for POST /submissions endpoint.
 */
@Data
@ValidSubmission
public class SubmissionRequest {

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    @NotNull(message = "Language ID is required")
    @Min(value = 1, message = "Language ID must be positive")
    private Integer languageId;

    private String stdin;

    @JsonProperty("expected_output")
    private String expectedOutput;

    @JsonProperty("number_of_runs")
    @Min(value = 1, message = "Number of runs must be at least 1")
    @Max(value = 20, message = "Number of runs cannot exceed 20")
    private Integer numberOfRuns;

    @JsonProperty("cpu_time_limit")
    @DecimalMin(value = "0.0", message = "CPU time limit must be non-negative")
    @DecimalMax(value = "15.0", message = "CPU time limit cannot exceed 15 seconds")
    private BigDecimal cpuTimeLimit;

    @JsonProperty("cpu_extra_time")
    @DecimalMin(value = "0.0", message = "CPU extra time must be non-negative")
    @DecimalMax(value = "5.0", message = "CPU extra time cannot exceed 5 seconds")
    private BigDecimal cpuExtraTime;

    @JsonProperty("wall_time_limit")
    @DecimalMin(value = "1.0", message = "Wall time limit must be at least 1 second")
    @DecimalMax(value = "20.0", message = "Wall time limit cannot exceed 20 seconds")
    private BigDecimal wallTimeLimit;

    @JsonProperty("memory_limit")
    @Min(value = 2048, message = "Memory limit must be at least 2048 KB")
    @Max(value = 512000, message = "Memory limit cannot exceed 512000 KB")
    private Integer memoryLimit;

    @JsonProperty("stack_limit")
    @Min(value = 0, message = "Stack limit must be non-negative")
    @Max(value = 128000, message = "Stack limit cannot exceed 128000 KB")
    private Integer stackLimit;

    @JsonProperty("max_processes_and_or_threads")
    @Min(value = 1, message = "Max processes must be at least 1")
    @Max(value = 120, message = "Max processes cannot exceed 120")
    private Integer maxProcessesAndOrThreads;

    @JsonProperty("enable_per_process_and_thread_time_limit")
    private Boolean enablePerProcessAndThreadTimeLimit;

    @JsonProperty("enable_per_process_and_thread_memory_limit")
    private Boolean enablePerProcessAndThreadMemoryLimit;

    @JsonProperty("max_file_size")
    @Min(value = 0, message = "Max file size must be non-negative")
    @Max(value = 4096, message = "Max file size cannot exceed 4096 KB")
    private Integer maxFileSize;

    @JsonProperty("compiler_options")
    @Size(max = 512, message = "Compiler options cannot exceed 512 characters")
    private String compilerOptions;

    @JsonProperty("command_line_arguments")
    @Size(max = 512, message = "Command line arguments cannot exceed 512 characters")
    private String commandLineArguments;

    @JsonProperty("redirect_stderr_to_stdout")
    private Boolean redirectStderrToStdout;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("additional_files")
    private String additionalFiles; // Base64 encoded

    @JsonProperty("enable_network")
    private Boolean enableNetwork;

    /**
     * 제약조건 ID
     * - 기존에 저장된 제약조건 조합을 사용할 때 지정
     * - null이면 개별 제약조건 값들을 사용하여 새로 생성
     */
    @JsonProperty("constraints_id")
    private Long constraintsId;

    /**
     * Validation for project-type submissions
     */
    public boolean isValidForProject(boolean isProject) {
        if (isProject) {
            return (sourceCode == null || sourceCode.trim().isEmpty()) && 
                   additionalFiles != null && !additionalFiles.trim().isEmpty();
        } else {
            return sourceCode != null && !sourceCode.trim().isEmpty();
        }
    }
}
