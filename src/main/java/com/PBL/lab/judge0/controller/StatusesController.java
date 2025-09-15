package com.PBL.lab.judge0.controller;

import com.PBL.lab.judge0.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Statuses Controller
 * 
 * REST API endpoints for status information.
 * Complete port of Judge0 Ruby StatusesController.
 */
@RestController
@RequestMapping("/statuses")
@RequiredArgsConstructor
public class StatusesController {

    /**
     * GET /statuses
     * Get all available statuses
     */
    @GetMapping
    public ResponseEntity<List<StatusResponse>> index() {
        List<StatusResponse> statuses = Arrays.stream(Status.values())
                .map(status -> StatusResponse.builder()
                        .id(status.getId())
                        .description(status.getName())
                        .build())
                .toList();
        
        return ResponseEntity.ok(statuses);
    }

    /**
     * Status Response DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class StatusResponse {
        private Integer id;
        private String description;
    }
}
