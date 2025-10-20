package com.PBL.lab.grading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GradingRequest {
    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    @NotNull(message = "Language ID is required")
    @Min(value = 1, message = "Language ID must be positive")
    private Integer languageId;

    @JsonProperty("problem_id")
    private Long problemId;
}
