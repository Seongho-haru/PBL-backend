package com.PBL.lab.grade.dto;

import com.PBL.lab.core.dto.BaseExecutionRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GradeRequest extends BaseExecutionRequest {
    // grade 전용 필드

    @NotNull(message = "문제 ID는 필수입니다")
    @Min(value = 1, message = "문제 ID는 양수여야 합니다")
    @JsonProperty("problem_id")
    private Long problemId; // 문제 ID
}
