package com.PBL.lab.judge0.dto;

import com.PBL.lab.core.dto.ConstraintsResponse;
import com.PBL.lab.core.dto.StatusResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.core.enums.Status;
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

    private Long id;

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

    private ConstraintsResponse constraints;

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
                .id(submission.getId())
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

                .constraints(ConstraintsResponse.from(submission.getConstraints()));

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
