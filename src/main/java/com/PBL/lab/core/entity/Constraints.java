package com.PBL.lab.core.entity;

import com.PBL.lab.core.dto.ConstraintsResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Submission Constraints Entity - 제출 실행 제약조건 엔티티
 * 
 * === 주요 목적 ===
 * - 코드 실행 시 적용되는 모든 제약조건과 설정을 관리하는 엔티티
 * - CPU 시간, 메모리, 프로세스 수 등 리소스 제한 설정
 * - 컴파일러 옵션, 명령행 인자 등 고급 실행 옵션
 * 
 * === 포함 데이터 ===
 * - 실행 제한 설정: CPU 시간, 메모리, 프로세스 수 등
 * - 고급 실행 옵션: 컴파일러 옵션, 명령행 인자, 네트워크 접근 등
 * - 파일 관리: 추가 파일, 파일 크기 제한 등
 */
@Entity
@Table(name = "submission_constraints")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Constraints {

    /**
     * 제출물 고유 식별자 (Primary Key)
     * - Submission 엔티티와 1:1 관계
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 실행 제한 설정 (Execution Constraints) ==========
    
    /**
     * 다중 실행 횟수
     * - 동일한 코드를 여러 번 실행하여 안정성 검증
     * - 기본값: 1회, 최대: 20회
     * - 성능 측정의 정확도를 높이기 위해 사용
     */
    @DecimalMin("1")
    @DecimalMax("20")
    @Column(name = "number_of_runs")
    @Builder.Default
    private Integer numberOfRuns = 1;

    /**
     * CPU 실행 시간 제한 (초)
     * - 프로그램이 CPU를 사용할 수 있는 최대 시간
     * - 기본값: 5초, 최대: 15초
     * - 이 시간을 초과하면 Time Limit Exceeded 오류 발생
     */
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("15.0")
    @Column(name = "cpu_time_limit", precision = 10, scale = 6)
    @Builder.Default
    private BigDecimal cpuTimeLimit = BigDecimal.valueOf(5.0);

    /**
     * CPU 추가 여유 시간 (초)
     * - 컴파일, 링크 등 추가 작업을 위한 여유 시간
     * - 기본값: 1초, 최대: 5초
     * - cpuTimeLimit에 추가로 제공되는 시간
     */
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(name = "cpu_extra_time", precision = 10, scale = 6)
    @Builder.Default
    private BigDecimal cpuExtraTime = BigDecimal.valueOf(1.0);

    /**
     * 전체 실행 제한 시간 (초)
     * - 프로그램 실행의 절대적 시간 제한
     * - 기본값: 10초, 최대: 20초
     * - 이 시간을 초과하면 강제 종료
     */
    @DecimalMin("1.0")
    @DecimalMax("20.0")
    @Column(name = "wall_time_limit", precision = 10, scale = 6)
    @Builder.Default
    private BigDecimal wallTimeLimit = BigDecimal.valueOf(10.0);

    /**
     * 메모리 사용 제한 (KB 단위)
     * - 프로그램이 사용할 수 있는 최대 메모리 양
     * - 기본값: 128MB (128000KB), 최대: 512MB (512000KB)
     * - 이 메모리를 초과하면 Memory Limit Exceeded 오류 발생
     */
    @NotNull
    @Min(2048)
    @Max(512000)
    @Column(name = "memory_limit")
    @Builder.Default
    private Integer memoryLimit = 128000; // KB

    /**
     * 스택 메모리 제한 (KB 단위)
     * - 프로그램의 스택 영역 사용 제한
     * - 기본값: 64MB (64000KB), 최대: 128MB (128000KB)
     * - 재귀 호출이나 큰 지역 변수 사용 시 중요
     */
    @Min(0)
    @Max(128000)
    @Column(name = "stack_limit")
    @Builder.Default
    private Integer stackLimit = 64000; // KB

    /**
     * 최대 프로세스/스레드 수
     * - 프로그램이 생성할 수 있는 최대 프로세스 및 스레드 수
     * - 기본값: 60개, 최대: 120개
     * - 멀티프로세싱, 멀티스레딩 프로그램의 리소스 제한
     */
    @Min(1)
    @Max(120)
    @Column(name = "max_processes_and_or_threads")
    @Builder.Default
    private Integer maxProcessesAndOrThreads = 60;

    /**
     * 프로세스/스레드별 시간 제한 활성화
     * - 각 프로세스/스레드에 개별적으로 시간 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 제어
     */
    @Column(name = "enable_per_process_and_thread_time_limit")
    @Builder.Default
    private Boolean enablePerProcessAndThreadTimeLimit = false;

    /**
     * 프로세스/스레드별 메모리 제한 활성화
     * - 각 프로세스/스레드에 개별적으로 메모리 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 메모리 제어
     */
    @Column(name = "enable_per_process_and_thread_memory_limit")
    @Builder.Default
    private Boolean enablePerProcessAndThreadMemoryLimit = false;

    /**
     * 최대 파일 크기 제한 (KB 단위)
     * - 프로그램이 생성할 수 있는 최대 파일 크기
     * - 기본값: 1MB (1024KB), 최대: 4MB (4096KB)
     * - 파일 출력이 많은 프로그램의 리소스 제한
     */
    @Min(0)
    @Max(4096)
    @Column(name = "max_file_size")
    @Builder.Default
    private Integer maxFileSize = 1024; // KB

    // ========== 고급 실행 옵션 (Advanced Execution Options) ==========
    
    /**
     * 컴파일러 옵션
     * - 컴파일 시 사용할 추가 옵션들
     * - 예: "-O2 -Wall -Wextra" (C/C++)
     * - 최대 512자까지 설정 가능
     */
    @Size(max = 512)
    @Column(name = "compiler_options")
    private String compilerOptions;

    /**
     * 명령어 인수 (Command Line Arguments)
     * - 프로그램 실행 시 전달할 명령행 인수들
     * - 예: "arg1 arg2 --option value"
     * - 최대 512자까지 설정 가능
     */
    @Size(max = 512)
    @Column(name = "command_line_arguments")
    private String commandLineArguments;

    /**
     * 표준 오류를 표준 출력으로 리다이렉션
     * - stderr를 stdout으로 합쳐서 출력
     * - 기본값: false (분리 유지)
     * - 디버깅이나 로그 통합 시 유용
     */
    @Column(name = "redirect_stderr_to_stdout")
    @Builder.Default
    private Boolean redirectStderrToStdout = false;

    /**
     * 웹훅 콜백 URL
     * - 실행 완료 후 결과를 전송할 HTTP URL
     * - 비동기 알림을 위한 웹훅 엔드포인트
     * - POST 요청으로 실행 결과 전송
     */
    @Column(name = "callback_url")
    private String callbackUrl;

    /**
     * 추가 파일들 (바이너리 데이터)
     * - 프로젝트 제출 시 포함되는 추가 파일들
     * - ZIP, TAR 등 압축된 바이너리 형태로 저장
     * - 다중 파일 프로젝트 지원
     *
     * Note: @Lob를 제거함 - PostgreSQL에서 byte[]에 @Lob를 사용하면
     * oid 타입으로 매핑되어 bytea와 충돌함. columnDefinition만으로 충분.
     */
    @Column(name = "additional_files", columnDefinition = "bytea")
    private byte[] additionalFiles;

    /**
     * 네트워크 액세스 허용 여부
     * - 프로그램이 네트워크에 접근할 수 있는지 여부
     * - 기본값: false (보안상 차단)
     * - 외부 API 호출이나 네트워크 통신이 필요한 경우에만 활성화
     */
    @Column(name = "enable_network")
    @Builder.Default
    private Boolean enableNetwork = false;

    /**
     * ConstraintsResponse DTO를 Constraints 엔티티로 변환
     *
     * @param constraintsResponse 변환할 DTO 객체
     * @return 변환된 Constraints 엔티티
     */
    public static Constraints build(ConstraintsResponse constraintsResponse) {
        if (constraintsResponse == null) {
            return null;
        }

        return Constraints.builder()
                .numberOfRuns(constraintsResponse.getNumberOfRuns() != null ? constraintsResponse.getNumberOfRuns() : 1)
                .cpuTimeLimit(constraintsResponse.getCpuTimeLimit() != null ? constraintsResponse.getCpuTimeLimit() : BigDecimal.valueOf(5.0))
                .cpuExtraTime(constraintsResponse.getCpuExtraTime() != null ? constraintsResponse.getCpuExtraTime() : BigDecimal.valueOf(1.0))
                .wallTimeLimit(constraintsResponse.getWallTimeLimit() != null ? constraintsResponse.getWallTimeLimit() : BigDecimal.valueOf(10.0))
                .memoryLimit(constraintsResponse.getMemoryLimit() != null ? constraintsResponse.getMemoryLimit() : 128000)
                .stackLimit(constraintsResponse.getStackLimit() != null ? constraintsResponse.getStackLimit() : 64000)
                .maxProcessesAndOrThreads(constraintsResponse.getMaxProcessesAndOrThreads() != null ? constraintsResponse.getMaxProcessesAndOrThreads() : 60)
                .enablePerProcessAndThreadTimeLimit(constraintsResponse.getEnablePerProcessAndThreadTimeLimit() != null ? constraintsResponse.getEnablePerProcessAndThreadTimeLimit() : false)
                .enablePerProcessAndThreadMemoryLimit(constraintsResponse.getEnablePerProcessAndThreadMemoryLimit() != null ? constraintsResponse.getEnablePerProcessAndThreadMemoryLimit() : false)
                .maxFileSize(constraintsResponse.getMaxFileSize() != null ? constraintsResponse.getMaxFileSize() : 1024)
                .compilerOptions(constraintsResponse.getCompilerOptions())
                .commandLineArguments(constraintsResponse.getCommandLineArguments())
                .redirectStderrToStdout(constraintsResponse.getRedirectStderrToStdout() != null ? constraintsResponse.getRedirectStderrToStdout() : false)
                .callbackUrl(constraintsResponse.getCallbackUrl())
                .additionalFiles(constraintsResponse.getAdditionalFiles() != null ?
                        java.util.Base64.getDecoder().decode(constraintsResponse.getAdditionalFiles()) : null)
                .enableNetwork(constraintsResponse.getEnableNetwork() != null ? constraintsResponse.getEnableNetwork() : false)
                .build();
    }
}
