package com.PBL.lab.grading.dto;

import com.PBL.lab.core.dto.StatusResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class errorSubmission {
    private String token;

    private String stdin;
    private String stdout;
    private String stderr;
    @JsonProperty("expected_output")
    private String expectedOutput;
}
