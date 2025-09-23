package com.PBL.lab.judge0.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Submission Response DTO
 * 
 * Data Transfer Object for returning submission data.
 * Represents the response payload for submission endpoints.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionResponse {

    private String token;

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

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
     * Create SubmissionResponse from Submission entity
     */
    public static SubmissionResponse from(Submission submission) {
        return from(submission, false, null);
    }

    /**
     * Create SubmissionResponse from Submission entity with encoding option
     */
    public static SubmissionResponse from(Submission submission, boolean base64Encoded, String[] requestedFields) {
        if (submission == null) {
            return null;
        }

        SubmissionResponseBuilder builder = SubmissionResponse.builder()
                .token(submission.getToken())
                .languageId(submission.getLanguageId())
                .status(StatusResponse.from(submission.getStatus()))
                .createdAt(submission.getCreatedAt())
                .finishedAt(submission.getFinishedAt())
                .time(submission.getTime())
                .wallTime(submission.getWallTime())
                .memory(submission.getMemory())
                .exitCode(submission.getExitCode())
                .exitSignal(submission.getExitSignal())
                .message(submission.getInputOutput() != null ? submission.getInputOutput().getMessage() : null)
                .numberOfRuns(submission.getConstraints().getNumberOfRuns())
                .cpuTimeLimit(submission.getConstraints().getCpuTimeLimit())
                .cpuExtraTime(submission.getConstraints().getCpuExtraTime())
                .wallTimeLimit(submission.getConstraints().getWallTimeLimit())
                .memoryLimit(submission.getConstraints().getMemoryLimit())
                .stackLimit(submission.getConstraints().getStackLimit())
                .maxProcessesAndOrThreads(submission.getConstraints().getMaxProcessesAndOrThreads())
                .enablePerProcessAndThreadTimeLimit(submission.getConstraints().getEnablePerProcessAndThreadTimeLimit())
                .enablePerProcessAndThreadMemoryLimit(submission.getConstraints().getEnablePerProcessAndThreadMemoryLimit())
                .maxFileSize(submission.getConstraints().getMaxFileSize())
                .compilerOptions(submission.getConstraints().getCompilerOptions())
                .commandLineArguments(submission.getConstraints().getCommandLineArguments())
                .redirectStderrToStdout(submission.getConstraints().getRedirectStderrToStdout())
                .callbackUrl(submission.getConstraints().getCallbackUrl())
                .enableNetwork(submission.getConstraints().getEnableNetwork());

        // Handle text fields with base64 encoding
        if (base64Encoded) {
            builder.sourceCode(submission.getSourceCode())
                    .stdin(submission.getInputOutput() != null ? submission.getInputOutput().getStdin() : null)
                    .expectedOutput(submission.getInputOutput() != null ? submission.getInputOutput().getExpectedOutput() : null)
                    .stdout(submission.getInputOutput() != null ? submission.getInputOutput().getStdout() : null)
                    .stderr(submission.getInputOutput() != null ? submission.getInputOutput().getStderr() : null)
                    .compileOutput(submission.getInputOutput() != null ? submission.getInputOutput().getCompileOutput() : null);
        } else {
            // For non-base64, we need to decode if stored as base64
            builder.sourceCode(submission.getSourceCode())
                    .stdin(submission.getInputOutput() != null ? submission.getInputOutput().getStdin() : null)
                    .expectedOutput(submission.getInputOutput() != null ? submission.getInputOutput().getExpectedOutput() : null)
                    .stdout(submission.getInputOutput() != null ? submission.getInputOutput().getStdout() : null)
                    .stderr(submission.getInputOutput() != null ? submission.getInputOutput().getStderr() : null)
                    .compileOutput(submission.getInputOutput() != null ? submission.getInputOutput().getCompileOutput() : null);
        }

        // Handle additional files
        if (submission.getConstraints().getAdditionalFiles() != null) {
            // Convert byte array to base64 string
            builder.additionalFiles(java.util.Base64.getEncoder().encodeToString(submission.getConstraints().getAdditionalFiles()));
        }

        return builder.build();
    }

    /**
     * Create minimal response with only token (for async submissions)
     */
    public static SubmissionResponse minimal(String token) {
        return SubmissionResponse.builder()
                .token(token)
                .build();
    }
}
