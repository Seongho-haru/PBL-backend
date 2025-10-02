package com.PBL.lab.grading.dto;

import com.PBL.lab.core.dto.ConstraintsResponse;
import com.PBL.lab.core.dto.StatusResponse;
import com.PBL.lab.judge0.dto.InputOutput;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.PBL.lab.grading.entity.Grading;
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
    private Long id;

    private String token;

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

//    private Language language;

    private ConstraintsResponse constraints;

    @JsonProperty("err_inputOutput")
    private InputOutput inputOutput;

    private String message;

    private StatusResponse status;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonProperty("finished_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime finishedAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    private BigDecimal time;

    @JsonProperty("wall_time")
    private BigDecimal wallTime;

    private Integer memory;

    @JsonProperty("exit_code")
    private Integer exitCode;

    @JsonProperty("exit_signal")
    private Integer exitSignal;

    // Grading-specific fields
    @JsonProperty("problem_id")
    private Long problemId;

    private ProgressResponse progress;


    /**
     * Create GradingResponse from Grading entity with encoding option
     */
    public static GradingResponse from(Grading grading, boolean base64Encoded, String[] requestedFields) {

        if (grading == null) {
            return null;
        }
        ProgressResponse progress = null;

        GradingResponseBuilder builder = GradingResponse.builder()
                .id(grading.getId())
                .token(grading.getToken())
                .languageId(grading.getLanguageId())
                .status(StatusResponse.from(grading.getStatus()))
                .constraints(ConstraintsResponse.from(grading.getConstraints()))
                .createdAt(grading.getCreatedAt())
                .finishedAt(grading.getFinishedAt())
                .time(grading.getTime())
                .message(grading.getMessage())
                .inputOutput(InputOutput.from(grading.getInputOutput()))
                .wallTime(grading.getWallTime())
                .memory(grading.getMemory())
                .exitCode(grading.getExitCode())
                .exitSignal(grading.getExitSignal())
                .problemId(grading.getProblemId())
                .progress(progress);

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

    /**
     * 프로그레스 업데이트 메서드
     */
    public void updateProgress(int doneTestCase, int totalTestCase) {
        if (this.progress != null) {
            this.progress.setDoneTestCase(doneTestCase);
            this.progress.setCurrentTestCase(doneTestCase + 1);
            this.progress.setProgressPercentage((double) doneTestCase / totalTestCase * 100);
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프로그레스 완료 메서드
     */
    public void completeProgress() {
        if (this.progress != null) {
            this.progress.setDoneTestCase(this.progress.getTotalTestCase());
            this.progress.setCurrentTestCase(this.progress.getTotalTestCase());
            this.progress.setProgressPercentage(100.0);
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프로그레스 에러 메서드
     */
    public void errorProgress(String errorMessage) {
        // 에러 상태는 Grading의 status 필드에서 관리
        this.message = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

}
