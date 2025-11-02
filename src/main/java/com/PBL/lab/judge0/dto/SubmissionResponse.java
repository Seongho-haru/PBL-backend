package com.PBL.lab.judge0.dto;

import com.PBL.lab.core.dto.BaseExecutionResponse;
import com.PBL.lab.core.dto.ExecutionInputOutputDTO;
import com.PBL.lab.core.dto.StatusResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.PBL.lab.judge0.entity.Submission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Submission Response DTO
 *
 * Data Transfer Object for returning submission data.
 * Represents the response payload for submission endpoints.
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionResponse extends BaseExecutionResponse {

    /**
     * Input/Output 정보를 담는 DTO 객체
     * - develop 브랜치와 호환성을 위해 input_output 필드명 사용 (스네이크 케이스)
     */
    @JsonProperty("input_output")
    private ExecutionInputOutputDTO inputOutput;

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

        return SubmissionResponse.builder()
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
                .sourceCode(submission.getSourceCode())
                .inputOutput(ExecutionInputOutputDTO.from(submission.getInputOutput()))
                .build();
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
