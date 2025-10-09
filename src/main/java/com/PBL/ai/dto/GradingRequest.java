package com.PBL.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradingRequest {
    @NotNull
    @JsonProperty("grading_token")
    private String gradingToken;

    @NotNull
    @JsonProperty("problem_id")
    private Long problemId;

}
