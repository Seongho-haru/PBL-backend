package com.PBL.lab.core.service;

import com.PBL.lab.core.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Execution Result
 * 
 * Represents the result of code execution.
 * Contains all output, metrics, and status information.
 */
@Data
@Builder
public class ExecutionResult {

    private String stdout;
    private String stderr;
    private String compileOutput;
    private String message;
    
    private Status status;
    
    private BigDecimal time;
    private BigDecimal wallTime;
    private Integer memory;
    
    private Integer exitCode;
    private Integer exitSignal;
    
    private String errorMessage;
    private String errorToken;
    
    // Container pool metrics
    private Long containerAcquireTime;
    private Long totalExecutionTime;

    /**
     * Check if execution was successful
     */
    public boolean isSuccessful() {
        return status != null && status.isSuccessful();
    }

    /**
     * Check if there's any output
     */
    public boolean hasOutput() {
        return (stdout != null && !stdout.trim().isEmpty()) ||
               (stderr != null && !stderr.trim().isEmpty());
    }

    /**
     * Check if there are compilation errors
     */
    public boolean hasCompilationError() {
        return status == Status.CE;
    }

    /**
     * Check if there are runtime errors
     */
    public boolean hasRuntimeError() {
        return status != null && status.isRuntimeError();
    }

    /**
     * Get formatted execution time
     */
    public String getFormattedTime() {
        if (time == null) {
            return "N/A";
        }
        return String.format("%.3f s", time);
    }

    /**
     * Get formatted memory usage
     */
    public String getFormattedMemory() {
        if (memory == null) {
            return "N/A";
        }
        return String.format("%d KB", memory);
    }

    /**
     * Create error result
     */
    public static ExecutionResult error(String errorMessage) {
        return ExecutionResult.builder()
                .status(Status.BOXERR)
                .errorMessage(errorMessage)
                .message(errorMessage)
                .build();
    }

    /**
     * Create compilation error result
     */
    public static ExecutionResult compilationError(String compileOutput) {
        return ExecutionResult.builder()
                .status(Status.CE)
                .compileOutput(compileOutput)
                .build();
    }

    /**
     * Create timeout result
     */
    public static ExecutionResult timeout() {
        return ExecutionResult.builder()
                .status(Status.TLE)
                .message("Time limit exceeded")
                .build();
    }

    /**
     * Create runtime error result
     */
    public static ExecutionResult runtimeError(Integer exitSignal, String stderr) {
        return ExecutionResult.builder()
                .status(Status.findRuntimeErrorByStatusCode(exitSignal))
                .exitSignal(exitSignal)
                .stderr(stderr)
                .build();
    }

    /**
     * Create successful result
     */
    public static ExecutionResult success(String stdout, String stderr, 
                                        BigDecimal time, BigDecimal wallTime, Integer memory) {
        return ExecutionResult.builder()
                .status(Status.AC)
                .stdout(stdout)
                .stderr(stderr)
                .time(time)
                .wallTime(wallTime)
                .memory(memory)
                .exitCode(0)
                .build();
    }
}
