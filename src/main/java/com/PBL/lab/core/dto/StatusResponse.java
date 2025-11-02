package com.PBL.lab.core.dto;

import com.PBL.lab.core.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponse {
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