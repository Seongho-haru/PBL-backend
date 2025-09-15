package com.PBL.lab.judge0.dto;

import com.PBL.lab.judge0.validation.ValidSubmission;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Submission Request DTO
 * 
 * Data Transfer Object for creating submissions.
 * Represents the request payload for POST /submissions endpoint.
 */
@Data
@ValidSubmission
public class SubmissionRequest {

    private String sourceCode;

    @NotNull(message = "Language ID is required")
    @Min(value = 1, message = "Language ID must be positive")
    private Integer languageId;

    private String stdin;

    private String expectedOutput;

    @Min(value = 1, message = "Number of runs must be at least 1")
    @Max(value = 20, message = "Number of runs cannot exceed 20")
    private Integer numberOfRuns;

    @DecimalMin(value = "0.0", message = "CPU time limit must be non-negative")
    @DecimalMax(value = "15.0", message = "CPU time limit cannot exceed 15 seconds")
    private BigDecimal cpuTimeLimit;

    @DecimalMin(value = "0.0", message = "CPU extra time must be non-negative")
    @DecimalMax(value = "5.0", message = "CPU extra time cannot exceed 5 seconds")
    private BigDecimal cpuExtraTime;

    @DecimalMin(value = "1.0", message = "Wall time limit must be at least 1 second")
    @DecimalMax(value = "20.0", message = "Wall time limit cannot exceed 20 seconds")
    private BigDecimal wallTimeLimit;

    @Min(value = 2048, message = "Memory limit must be at least 2048 KB")
    @Max(value = 512000, message = "Memory limit cannot exceed 512000 KB")
    private Integer memoryLimit;

    @Min(value = 0, message = "Stack limit must be non-negative")
    @Max(value = 128000, message = "Stack limit cannot exceed 128000 KB")
    private Integer stackLimit;

    @Min(value = 1, message = "Max processes must be at least 1")
    @Max(value = 120, message = "Max processes cannot exceed 120")
    private Integer maxProcessesAndOrThreads;

    private Boolean enablePerProcessAndThreadTimeLimit;

    private Boolean enablePerProcessAndThreadMemoryLimit;

    @Min(value = 0, message = "Max file size must be non-negative")
    @Max(value = 4096, message = "Max file size cannot exceed 4096 KB")
    private Integer maxFileSize;

    @Size(max = 512, message = "Compiler options cannot exceed 512 characters")
    private String compilerOptions;

    @Size(max = 512, message = "Command line arguments cannot exceed 512 characters")
    private String commandLineArguments;

    private Boolean redirectStderrToStdout;

    private String callbackUrl;

    private String additionalFiles; // Base64 encoded

    private Boolean enableNetwork;

    /**
     * Validation for project-type submissions
     */
    public boolean isValidForProject(boolean isProject) {
        if (isProject) {
            return (sourceCode == null || sourceCode.trim().isEmpty()) && 
                   additionalFiles != null && !additionalFiles.trim().isEmpty();
        } else {
            return sourceCode != null && !sourceCode.trim().isEmpty();
        }
    }
}
