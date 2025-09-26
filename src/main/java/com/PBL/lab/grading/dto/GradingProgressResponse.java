package com.PBL.lab.grading.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.PBL.lab.core.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Grading Progress Response DTO for SSE
 * 
 * Server-Sent Events를 위한 채점 진행상황 응답 DTO
 * 실시간으로 채점 진행률을 클라이언트에 전송하기 위해 사용
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradingProgressResponse {

    // GradingResponse와 동일한 기본 필드들
    private String token;

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

    @JsonProperty("constraints_id")
    private Long constraintsId;

    private String stdin;

    @JsonProperty("expected_output")
    private String expectedOutput;

    private String stdout;

    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    private String message;

    private StatusResponse status;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonProperty("finished_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime finishedAt;

    private BigDecimal time;

    @JsonProperty("wall_time")
    private BigDecimal wallTime;

    private Integer memory;

    @JsonProperty("exit_code")
    private Integer exitCode;

    @JsonProperty("exit_signal")
    private Integer exitSignal;

    // 제약조건들
    @JsonProperty("number_of_runs")
    private Integer numberOfRuns;
    
    @JsonProperty("cpu_time_limit")
    private BigDecimal cpuTimeLimit;
    
    @JsonProperty("cpu_extra_time")
    private BigDecimal cpuExtraTime;
    
    @JsonProperty("wall_time_limit")
    private BigDecimal wallTimeLimit;

    @JsonProperty("memory_limit")
    private Integer memoryLimit;
    
    @JsonProperty("stack_limit")
    private Integer stackLimit;
    
    @JsonProperty("max_processes_and_or_threads")
    private Integer maxProcessesAndOrThreads;
    
    @JsonProperty("enable_per_process_and_thread_time_limit")
    private Boolean enablePerProcessAndThreadTimeLimit;
    
    @JsonProperty("enable_per_process_and_thread_memory_limit")
    private Boolean enablePerProcessAndThreadMemoryLimit;
    
    @JsonProperty("max_file_size")
    private Integer maxFileSize;

    @JsonProperty("compiler_options")
    private String compilerOptions;

    @JsonProperty("command_line_arguments")
    private String commandLineArguments;

    @JsonProperty("redirect_stderr_to_stdout")
    private Boolean redirectStderrToStdout;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("additional_files")
    private String additionalFiles;

    @JsonProperty("enable_network")
    private Boolean enableNetwork;

    // Grading-specific fields
    @JsonProperty("problem_id")
    private Long problemId;

    // 진행상황 전용 필드들
    @JsonProperty("total_test_case")
    private Integer totalTestCase;

    @JsonProperty("done_test_case")
    private Integer doneTestCase;

    @JsonProperty("current_test_case")
    private Integer currentTestCase;

    @JsonProperty("progress_percentage")
    private Double progressPercentage;

    @JsonProperty("current_status")
    private String currentStatus;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    @JsonProperty("estimated_remaining_time")
    private Long estimatedRemainingTime; // milliseconds

    /**
     * Status Response nested class (GradingResponse와 동일)
     */
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatusResponse {
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


    /**
     * 진행률 계산
     */
    public void calculateProgress() {
        if (totalTestCase != null && totalTestCase > 0) {
            this.progressPercentage = (double) doneTestCase / totalTestCase * 100.0;
        } else {
            this.progressPercentage = 0.0;
        }
    }


    /**
     * 현재 테스트케이스 진행상황 업데이트
     */
    public void updateProgress(int current, int total) {
        this.totalTestCase = total;
        this.doneTestCase = current;
        this.currentTestCase = current + 1; // 1-based indexing for display
        calculateProgress();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 완료 상태로 업데이트
     */
    public void markComplete(int total) {
        this.totalTestCase = total;
        this.doneTestCase = total;
        this.currentTestCase = total;
        this.progressPercentage = 100.0;
        this.status = StatusResponse.from(Status.AC);
        this.currentStatus = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
        this.finishedAt = LocalDateTime.now();
    }

    /**
     * 에러 상태로 업데이트
     */
    public void markError(String errorMessage) {
        this.status = StatusResponse.from(Status.BOXERR);
        this.currentStatus = "ERROR";
        this.message = errorMessage;
        this.updatedAt = LocalDateTime.now();
        this.finishedAt = LocalDateTime.now();
    }

    /**
     * 진행 중 상태로 업데이트
     */
    public void markInProgress() {
        this.status = StatusResponse.from(Status.PROCESS);
        this.currentStatus = "PROCESSING";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * GradingResponse에서 GradingProgressResponse로 변환
     */
    public static GradingProgressResponse fromGradingResponse(GradingResponse gradingResponse) {
        if (gradingResponse == null) {
            return null;
        }

        return GradingProgressResponse.builder()
                .token(gradingResponse.getToken())
                .sourceCode(gradingResponse.getSourceCode())
                .languageId(gradingResponse.getLanguageId())
                .constraintsId(gradingResponse.getConstraintsId())
                .stdin(gradingResponse.getStdin())
                .expectedOutput(gradingResponse.getExpectedOutput())
                .stdout(gradingResponse.getStdout())
                .stderr(gradingResponse.getStderr())
                .compileOutput(gradingResponse.getCompileOutput())
                .message(gradingResponse.getMessage())
                .status(gradingResponse.getStatus() != null ? 
                        StatusResponse.builder()
                                .id(gradingResponse.getStatus().getId())
                                .description(gradingResponse.getStatus().getDescription())
                                .build() : null)
                .createdAt(gradingResponse.getCreatedAt())
                .finishedAt(gradingResponse.getFinishedAt())
                .time(gradingResponse.getTime())
                .wallTime(gradingResponse.getWallTime())
                .memory(gradingResponse.getMemory())
                .exitCode(gradingResponse.getExitCode())
                .exitSignal(gradingResponse.getExitSignal())
                .numberOfRuns(gradingResponse.getNumberOfRuns())
                .cpuTimeLimit(gradingResponse.getCpuTimeLimit())
                .cpuExtraTime(gradingResponse.getCpuExtraTime())
                .wallTimeLimit(gradingResponse.getWallTimeLimit())
                .memoryLimit(gradingResponse.getMemoryLimit())
                .stackLimit(gradingResponse.getStackLimit())
                .maxProcessesAndOrThreads(gradingResponse.getMaxProcessesAndOrThreads())
                .enablePerProcessAndThreadTimeLimit(gradingResponse.getEnablePerProcessAndThreadTimeLimit())
                .enablePerProcessAndThreadMemoryLimit(gradingResponse.getEnablePerProcessAndThreadMemoryLimit())
                .maxFileSize(gradingResponse.getMaxFileSize())
                .compilerOptions(gradingResponse.getCompilerOptions())
                .commandLineArguments(gradingResponse.getCommandLineArguments())
                .redirectStderrToStdout(gradingResponse.getRedirectStderrToStdout())
                .callbackUrl(gradingResponse.getCallbackUrl())
                .additionalFiles(gradingResponse.getAdditionalFiles())
                .enableNetwork(gradingResponse.getEnableNetwork())
                .problemId(gradingResponse.getProblemId())
                .build();
    }
}
