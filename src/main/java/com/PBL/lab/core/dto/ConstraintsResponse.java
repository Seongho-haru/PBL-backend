package com.PBL.lab.core.dto;

import com.PBL.lab.core.entity.Constraints;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Constraints Response DTO
 * 제약조건 응답 및 요청에 사용되는 DTO
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintsResponse {

    /**
     * 다중 실행 횟수
     * 범위: 1~20, 기본값: 1
     */
    @JsonProperty("number_of_runs")
    @Min(value = 1, message = "실행 횟수는 최소 1회입니다")
    @Max(value = 20, message = "실행 횟수는 최대 20회를 초과할 수 없습니다")
    private Integer numberOfRuns;

    /**
     * CPU 실행 시간 제한 (초)
     * 범위: 0.0~15.0, 기본값: 5.0
     */
    @JsonProperty("cpu_time_limit")
    @DecimalMin(value = "0.0", message = "CPU 시간 제한은 0 이상이어야 합니다")
    @DecimalMax(value = "15.0", message = "CPU 시간 제한은 15초를 초과할 수 없습니다")
    private BigDecimal cpuTimeLimit;

    /**
     * CPU 추가 여유 시간 (초)
     * 범위: 0.0~5.0, 기본값: 1.0
     */
    @JsonProperty("cpu_extra_time")
    @DecimalMin(value = "0.0", message = "CPU 추가 시간은 0 이상이어야 합니다")
    @DecimalMax(value = "5.0", message = "CPU 추가 시간은 5초를 초과할 수 없습니다")
    private BigDecimal cpuExtraTime;

    /**
     * 전체 실행 제한 시간 (초)
     * 범위: 1.0~20.0, 기본값: 10.0
     */
    @JsonProperty("wall_time_limit")
    @DecimalMin(value = "1.0", message = "전체 시간 제한은 최소 1초입니다")
    @DecimalMax(value = "20.0", message = "전체 시간 제한은 20초를 초과할 수 없습니다")
    private BigDecimal wallTimeLimit;

    /**
     * 메모리 사용 제한 (KB)
     * 범위: 2048~512000, 기본값: 128000 (128MB)
     */
    @JsonProperty("memory_limit")
    @Min(value = 2048, message = "메모리 제한은 최소 2048KB입니다")
    @Max(value = 512000, message = "메모리 제한은 512000KB를 초과할 수 없습니다")
    private Integer memoryLimit;

    /**
     * 스택 메모리 제한 (KB)
     * 범위: 0~128000, 기본값: 64000 (64MB)
     */
    @JsonProperty("stack_limit")
    @Min(value = 0, message = "스택 제한은 0 이상이어야 합니다")
    @Max(value = 128000, message = "스택 제한은 128000KB를 초과할 수 없습니다")
    private Integer stackLimit;

    /**
     * 최대 프로세스/스레드 수
     * 범위: 1~120, 기본값: 60
     */
    @JsonProperty("max_processes_and_or_threads")
    @Min(value = 1, message = "프로세스 수는 최소 1개입니다")
    @Max(value = 120, message = "프로세스 수는 120개를 초과할 수 없습니다")
    private Integer maxProcessesAndOrThreads;

    /**
     * 프로세스/스레드별 시간 제한 활성화
     * 기본값: false
     */
    @JsonProperty("enable_per_process_and_thread_time_limit")
    private Boolean enablePerProcessAndThreadTimeLimit;

    /**
     * 프로세스/스레드별 메모리 제한 활성화
     * 기본값: false
     */
    @JsonProperty("enable_per_process_and_thread_memory_limit")
    private Boolean enablePerProcessAndThreadMemoryLimit;

    /**
     * 최대 파일 크기 제한 (KB)
     * 범위: 0~4096, 기본값: 1024 (1MB)
     */
    @JsonProperty("max_file_size")
    @Min(value = 0, message = "파일 크기는 0 이상이어야 합니다")
    @Max(value = 4096, message = "파일 크기는 4096KB를 초과할 수 없습니다")
    private Integer maxFileSize;

    /**
     * 컴파일러 옵션
     * 최대 512자
     */
    @JsonProperty("compiler_options")
    @Size(max = 512, message = "컴파일러 옵션은 512자를 초과할 수 없습니다")
    private String compilerOptions;

    /**
     * 명령행 인수
     * 최대 512자
     */
    @JsonProperty("command_line_arguments")
    @Size(max = 512, message = "명령행 인수는 512자를 초과할 수 없습니다")
    private String commandLineArguments;

    /**
     * 표준 오류를 표준 출력으로 리다이렉션
     * 기본값: false
     */
    @JsonProperty("redirect_stderr_to_stdout")
    private Boolean redirectStderrToStdout;

    /**
     * 웹훅 콜백 URL
     */
    @JsonProperty("callback_url")
    private String callbackUrl;

    /**
     * 추가 파일 (Base64 인코딩)
     */
    @JsonProperty("additional_files")
    private String additionalFiles;

    /**
     * 네트워크 액세스 허용 여부
     * 기본값: false
     */
    @JsonProperty("enable_network")
    private Boolean enableNetwork;

    public static ConstraintsResponse from(Constraints entity) {
        if (entity == null) {
            return null;
        }
        
        return ConstraintsResponse.builder()
                .numberOfRuns(entity.getNumberOfRuns())
                .cpuTimeLimit(entity.getCpuTimeLimit())
                .cpuExtraTime(entity.getCpuExtraTime())
                .wallTimeLimit(entity.getWallTimeLimit())
                .memoryLimit(entity.getMemoryLimit())
                .stackLimit(entity.getStackLimit())
                .maxProcessesAndOrThreads(entity.getMaxProcessesAndOrThreads())
                .enablePerProcessAndThreadTimeLimit(entity.getEnablePerProcessAndThreadTimeLimit())
                .enablePerProcessAndThreadMemoryLimit(entity.getEnablePerProcessAndThreadMemoryLimit())
                .maxFileSize(entity.getMaxFileSize())
                .compilerOptions(entity.getCompilerOptions())
                .commandLineArguments(entity.getCommandLineArguments())
                .redirectStderrToStdout(entity.getRedirectStderrToStdout())
                .callbackUrl(entity.getCallbackUrl())
                .additionalFiles(entity.getAdditionalFiles() != null ? 
                        java.util.Base64.getEncoder().encodeToString(entity.getAdditionalFiles()) : null)
                .enableNetwork(entity.getEnableNetwork())
                .build();
    }
}
