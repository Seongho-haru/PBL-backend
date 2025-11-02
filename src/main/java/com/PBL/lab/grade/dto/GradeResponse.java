package com.PBL.lab.grade.dto;

import com.PBL.lab.core.dto.BaseExecutionResponse;
import com.PBL.lab.core.dto.ConstraintsResponse;
import com.PBL.lab.core.dto.ExecutionInputOutputDTO;
import com.PBL.lab.core.dto.StatusResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.PBL.lab.grade.entity.Grade;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Grade Response DTO
 *
 * Data Transfer Object for returning grade data.
 * Represents the response payload for grade endpoints.
 */
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class GradeResponse extends BaseExecutionResponse {

    /**
     * Input/Output 정보를 담는 DTO 객체
     * - develop 브랜치와 호환성을 위해 err_inputOutput 필드명 사용
     */
    @JsonProperty("err_inputOutput")
    private ExecutionInputOutputDTO inputOutput;

    private String message;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    // Grade-specific fields
    @JsonProperty("problem_id")
    private Long problemId;

    private ProgressResponse progress;


    /**
     * Create GradeResponse from Grade entity with encoding option
     */
    public static GradeResponse from(Grade grade, boolean base64Encoded, String[] requestedFields) {

        if (grade == null) {
            return null;
        }
        ProgressResponse progress = null;

        GradeResponseBuilder builder = GradeResponse.builder()
                .id(grade.getId())
                .token(grade.getToken())
                .languageId(grade.getLanguageId())
                .status(StatusResponse.from(grade.getStatus()))
                .constraints(ConstraintsResponse.from(grade.getConstraints()))
                .createdAt(grade.getCreatedAt())
                .finishedAt(grade.getFinishedAt())
                .time(grade.getTime())
                .message(grade.getMessage())
                .inputOutput(ExecutionInputOutputDTO.from(grade.getInputOutput()))
                .wallTime(grade.getWallTime())
                .memory(grade.getMemory())
                .exitCode(grade.getExitCode())
                .exitSignal(grade.getExitSignal())
                .problemId(grade.getProblemId())
                .progress(progress);

        // Handle text fields with base64 encoding
        if (base64Encoded) {
            builder.sourceCode(grade.getSourceCode());
        } else {
            // For non-base64, we need to decode if stored as base64
            builder.sourceCode(grade.getSourceCode());
        }

        // 채점에서는 추가 파일이 필요 없음

        return builder.build();
    }



    /**
     * Create minimal response with only token (for async grades)
     */
    public static GradeResponse minimal(String token) {
        return GradeResponse.builder()
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
        // 에러 상태는 Grade의 status 필드에서 관리
        this.message = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

}
