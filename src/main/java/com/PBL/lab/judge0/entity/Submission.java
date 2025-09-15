package com.PBL.lab.judge0.entity;

import com.PBL.lab.judge0.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Submission Entity - 코드 제출 데이터 모델 엔티티
 * 
 * === 주요 목적 ===
 * - 사용자가 제출한 코드 실행 요청을 나타내는 핵심 JPA 엔티티
 * - 원본 Judge0 Ruby Submission 모델을 Java/JPA로 완전 포팅한 구현체
 * - 코드 실행에 필요한 모든 정보, 제약사항, 결과를 하나의 엔티티에 통합 관리
 * - Judge0 시스템의 가장 중요한 비즈니스 객체로 전체 워크플로우의 중심 역할
 * 
 * === 데이터 구조 분류 ===
 * 
 * 1. 기본 제출 정보 (Submission Core Data):
 *    - sourceCode: 실행할 소스 코드 (TEXT 타입으로 대용량 코드 지원)
 *    - languageId/language: 사용할 프로그래밍 언어 정보 (Java, Python, C++ 등)
 *    - token: 제출 식별을 위한 고유 UUID 토큰 (API 조회 키)
 *    - stdin: 프로그램 실행 시 표준 입력 데이터
 *    - expectedOutput: 예상 출력 결과 (정답 비교용)
 * 
 * 2. 실행 결과 정보 (Execution Results):
 *    - status/statusId: 실행 상태 (QUEUE, PROCESSING, ACCEPTED, WRONG_ANSWER 등)
 *    - stdout: 프로그램의 표준 출력 결과
 *    - stderr: 프로그램의 표준 오류 출력
 *    - compileOutput: 컴파일 과정에서의 출력 및 오류 메시지
 *    - time: CPU 실행 시간 (초 단위, 소수점 6자리)
 *    - wallTime: 전체 소요 시간 (실제 경과 시간)
 *    - memory: 메모리 사용량 (KB 단위)
 *    - exitCode: 프로그램 종료 코드 (0=성공, 기타=오류)
 *    - exitSignal: 프로그램 종료 시그널 (SIGKILL, SIGSEGV 등)
 *    - message: 추가적인 실행 관련 메시지
 * 
 * 3. 실행 제한 설정 (Execution Constraints):
 *    - cpuTimeLimit: CPU 실행 시간 제한 (기본 5초, 최대 15초)
 *    - cpuExtraTime: CPU 추가 여유 시간 (기본 1초, 최대 5초)
 *    - wallTimeLimit: 전체 실행 제한 시간 (기본 10초, 최대 20초)
 *    - memoryLimit: 메모리 사용 제한 (기본 128MB, 최대 512MB)
 *    - stackLimit: 스택 메모리 제한 (기본 64MB, 최대 128MB)
 *    - maxProcessesAndOrThreads: 최대 프로세스/스레드 수 (기본 60, 최대 120)
 *    - maxFileSize: 최대 파일 크기 제한 (기본 1MB, 최대 4MB)
 *    - numberOfRuns: 다중 실행 횟수 (기본 1회, 최대 20회)
 * 
 * 4. 고급 실행 옵션 (Advanced Execution Options):
 *    - compilerOptions: 컴파일러 옵션 설정 (예: -O2, -Wall)
 *    - commandLineArguments: 실행 시 명령어 인수 (argv 배열)
 *    - redirectStderrToStdout: 표준 오류를 표준 출력으로 리다이렉션 여부
 *    - enablePerProcessAndThreadTimeLimit: 프로세스/스레드별 시간 제한 활성화
 *    - enablePerProcessAndThreadMemoryLimit: 프로세스/스레드별 메모리 제한 활성화
 *    - enableNetwork: 네트워크 액세스 허용 여부 (보안상 기본 false)
 * 
 * 5. 파일 및 프로젝트 관리 (File & Project Management):
 *    - additionalFiles: 추가 파일들 (바이너리 데이터, ZIP 형태)
 *    - isProject(): 다중 파일 프로젝트 제출 여부 확인
 *    - hasAdditionalFiles(): 추가 파일 존재 여부 확인
 * 
 * 6. 시간 추적 정보 (Timing Information):
 *    - createdAt: 제출 생성 시간 (@CreationTimestamp 자동 설정)
 *    - queuedAt: 대기열 진입 시간 (큐에 추가된 시점)
 *    - startedAt: 실행 시작 시간 (Docker 컨테이너 실행 시작)
 *    - finishedAt: 실행 완료 시간 (결과 수집 완료)
 *    - updatedAt: 마지막 업데이트 시간 (@UpdateTimestamp 자동 갱신)
 * 
 * 7. 시스템 추적 정보 (System Tracking):
 *    - queueHost: 대기열 처리 서버 호스트명 (로드밸런싱 추적용)
 *    - executionHost: 실제 실행 서버 호스트명 (분산 환경 추적용)
 *    - callbackUrl: 실행 완료 후 웹훅 콜백 URL (비동기 알림용)
 * 
 * === 데이터 유효성 검증 ===
 * - @NotNull: 필수 필드 검증 (languageId, 제한값들)
 * - @Min/@Max: 정수 범위 검증 (메모리, 프로세스 수 등)
 * - @DecimalMin/@DecimalMax: 소수점 값 범위 검증 (시간 제한)
 * - @Size: 문자열 길이 제한 (컴파일러 옵션, 명령어 인수)
 * - 모든 제한값은 시스템 기본값과 최대 제한값을 명시적으로 설정
 * 
 * === 데이터베이스 최적화 ===
 * - 인덱스 설정:
 *   * token: 고유 인덱스 (빠른 API 조회)
 *   * status_id: 상태별 제출 검색 최적화
 *   * created_at: 시간순 정렬 및 시간 기반 쿼리 지원
 * - 대용량 데이터: TEXT 타입 사용 (source_code, stdin, stdout, stderr)
 * - 참조 무결성: language_id -> languages.id (Foreign Key)
 * 
 * === 비즈니스 로직 메서드 ===
 * - isCompleted(): 실행 완료 여부 확인 (finishedAt 존재 여부)
 * - hasErrors(): 오류 상태 여부 확인 (statusId >= 6, CE 이상)
 * - isProject(): 다중 파일 프로젝트 제출 여부 (Language 이름 확인)
 * - hasAdditionalFiles(): 추가 파일 존재 여부 (바이너리 데이터 크기 확인)
 * - getStatus()/setStatus(): Status 열거형과 statusId 동기화
 * 
 * === 데이터베이스 스키마 정보 ===
 * - 테이블명: submissions
 * - 주요 인덱스: token(고유), status_id, created_at
 * - 참조 무결성: language_id -> languages.id (Foreign Key)
 * - 대용량 데이터: TEXT 타입 사용 (source_code, stdin, stdout, stderr)
 * - 자동 타임스탬프: @CreationTimestamp, @UpdateTimestamp 활용
 * 
 * === 분리 가능한 데이터 영역 (리팩토링 고려사항) ===
 * 1. ExecutionConstraints: 실행 제한 설정 (cpuTimeLimit, memoryLimit 등)
 * 2. ExecutionResult: 실행 결과 (stdout, stderr, time, memory 등)
 * 3. SubmissionMetadata: 메타데이터 (token, timestamps, hosts 등)
 * 4. FileManagement: 파일 관련 (additionalFiles, isProject 등)
 */
@Entity
@Table(name = "submissions", indexes = {
    @Index(name = "idx_submission_token", columnList = "token"),
    @Index(name = "idx_submission_status", columnList = "status_id"),
    @Index(name = "idx_submission_created_at", columnList = "created_at")
})
@Data
@EqualsAndHashCode(exclude = {"language"})
@ToString(exclude = {"sourceCode", "stdin", "stdout", "stderr", "additionalFiles"})
public class Submission {

    /**
     * 제출물 고유 식별자 (Primary Key)
     * - 데이터베이스에서 자동 생성되는 순차적 ID
     * - 내부 시스템에서만 사용되며, 외부 API에서는 token 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 제출물 고유 토큰 (UUID)
     * - API 조회 시 사용되는 공개 식별자
     * - 36자리 UUID 형식 (예: 550e8400-e29b-41d4-a716-446655440000)
     * - 고유 인덱스로 설정되어 빠른 조회 지원
     */
    @Column(unique = true, nullable = false, length = 36)
    private String token;

    /**
     * 실행할 소스 코드
     * - 사용자가 제출한 실제 프로그래밍 코드
     * - TEXT 타입으로 대용량 코드 지원 (최대 1GB)
     * - 모든 프로그래밍 언어의 소스 코드 저장 가능
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String sourceCode;

    /**
     * 프로그래밍 언어 ID
     * - languages 테이블의 외래키
     * - 1: C, 2: C++, 4: Java, 71: Python 등
     * - 필수 필드로 반드시 설정되어야 함
     */
    @NotNull
    @Column(name = "language_id", nullable = false)
    private Integer languageId;

    /**
     * 표준 입력 데이터
     * - 프로그램 실행 시 stdin으로 전달될 입력 데이터
     * - 테스트 케이스의 입력값 저장
     * - TEXT 타입으로 대용량 입력 지원
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String stdin;

    /**
     * 예상 출력 결과
     * - 정답 비교를 위한 기대 출력값
     * - stdout과 비교하여 정답 여부 판단
     * - TEXT 타입으로 대용량 출력 지원
     */
    @Lob
    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    /**
     * 프로그램의 표준 출력 결과
     * - 실행된 프로그램이 stdout으로 출력한 내용
     * - expectedOutput과 비교하여 정답 여부 판단
     * - TEXT 타입으로 대용량 출력 지원
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String stdout;

    /**
     * 프로그램의 표준 오류 출력
     * - 실행된 프로그램이 stderr로 출력한 오류 메시지
     * - 컴파일 오류, 런타임 오류 등 저장
     * - TEXT 타입으로 대용량 오류 메시지 지원
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String stderr;

    /**
     * 실행 상태 ID
     * - 1: In Queue (대기열)
     * - 2: Processing (처리 중)
     * - 3: Accepted (정답)
     * - 4: Wrong Answer (오답)
     * - 5: Time Limit Exceeded (시간 초과)
     * - 6: Compilation Error (컴파일 오류)
     * - 7: Runtime Error (런타임 오류)
     * - 8: Internal Error (내부 오류)
     * - 9: Exec Format Error (실행 형식 오류)
     * - 10: Memory Limit Exceeded (메모리 초과)
     * - 11: Output Limit Exceeded (출력 초과)
     * - 12: Security Violation (보안 위반)
     * - 13: Presentation Error (출력 형식 오류)
     * - 14: Partial Score (부분 점수)
     * - 15: Other (기타)
     */
    @Column(name = "status_id")
    private Integer statusId = 1; // 기본값: 대기열

    /**
     * CPU 실행 시간 (초)
     * - 프로그램이 실제로 CPU를 사용한 시간
     * - 소수점 6자리까지 정밀도 지원
     * - cpuTimeLimit과 비교하여 시간 초과 판단
     */
    @Column(precision = 10, scale = 6)
    private BigDecimal time;

    /**
     * 메모리 사용량 (KB 단위)
     * - 프로그램이 사용한 최대 메모리 양
     * - memoryLimit과 비교하여 메모리 초과 판단
     * - 1MB = 1024KB로 계산
     */
    private Integer memory;

    /**
     * 컴파일 출력 및 오류 메시지
     * - 컴파일 과정에서 발생한 모든 출력
     * - 컴파일 경고, 오류 메시지, 링크 정보 등
     * - TEXT 타입으로 대용량 컴파일 로그 지원
     */
    @Lob
    @Column(name = "compile_output", columnDefinition = "TEXT")
    private String compileOutput;

    /**
     * 프로그램 종료 코드
     * - 0: 정상 종료 (성공)
     * - 1-255: 오류 종료 (프로그램에서 설정한 오류 코드)
     * - -1: 비정상 종료 (시그널에 의한 종료)
     */
    @Column(name = "exit_code")
    private Integer exitCode;

    /**
     * 프로그램 종료 시그널
     * - SIGKILL (9): 강제 종료
     * - SIGSEGV (11): 세그멘테이션 폴트
     * - SIGTERM (15): 정상 종료 요청
     * - SIGXCPU (24): CPU 시간 초과
     * - SIGXFSZ (25): 파일 크기 초과
     */
    @Column(name = "exit_signal")
    private Integer exitSignal;

    /**
     * 추가적인 실행 관련 메시지
     * - 시스템에서 생성한 추가 정보나 설명
     * - 오류 발생 시 상세한 원인 설명
     * - 디버깅을 위한 추가 컨텍스트 정보
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;

    /**
     * 전체 소요 시간 (Wall Time)
     * - 프로그램 실행 시작부터 종료까지의 실제 경과 시간
     * - CPU 시간과 달리 대기 시간, I/O 시간 등 모든 시간 포함
     * - 소수점 6자리까지 정밀도 지원
     */
    @Column(name = "wall_time", precision = 10, scale = 6)
    private BigDecimal wallTime;

    // ========== 실행 제한 설정 (Execution Constraints) ==========
    
    /**
     * 다중 실행 횟수
     * - 동일한 코드를 여러 번 실행하여 안정성 검증
     * - 기본값: 1회, 최대: 20회
     * - 성능 측정의 정확도를 높이기 위해 사용
     */
    @NotNull
    @DecimalMin("1")
    @DecimalMax("20")
    @Column(name = "number_of_runs")
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
    private BigDecimal cpuTimeLimit = BigDecimal.valueOf(5.0);

    /**
     * CPU 추가 여유 시간 (초)
     * - 컴파일, 링크 등 추가 작업을 위한 여유 시간
     * - 기본값: 1초, 최대: 5초
     * - cpuTimeLimit에 추가로 제공되는 시간
     */
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(name = "cpu_extra_time", precision = 10, scale = 6)
    private BigDecimal cpuExtraTime = BigDecimal.valueOf(1.0);

    /**
     * 전체 실행 제한 시간 (초)
     * - 프로그램 실행의 절대적 시간 제한
     * - 기본값: 10초, 최대: 20초
     * - 이 시간을 초과하면 강제 종료
     */
    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("20.0")
    @Column(name = "wall_time_limit", precision = 10, scale = 6)
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
    private Integer memoryLimit = 128000; // KB

    /**
     * 스택 메모리 제한 (KB 단위)
     * - 프로그램의 스택 영역 사용 제한
     * - 기본값: 64MB (64000KB), 최대: 128MB (128000KB)
     * - 재귀 호출이나 큰 지역 변수 사용 시 중요
     */
    @NotNull
    @Min(0)
    @Max(128000)
    @Column(name = "stack_limit")
    private Integer stackLimit = 64000; // KB

    /**
     * 최대 프로세스/스레드 수
     * - 프로그램이 생성할 수 있는 최대 프로세스 및 스레드 수
     * - 기본값: 60개, 최대: 120개
     * - 멀티프로세싱, 멀티스레딩 프로그램의 리소스 제한
     */
    @NotNull
    @Min(1)
    @Max(120)
    @Column(name = "max_processes_and_or_threads")
    private Integer maxProcessesAndOrThreads = 60;

    /**
     * 프로세스/스레드별 시간 제한 활성화
     * - 각 프로세스/스레드에 개별적으로 시간 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 제어
     */
    @Column(name = "enable_per_process_and_thread_time_limit")
    private Boolean enablePerProcessAndThreadTimeLimit = false;

    /**
     * 프로세스/스레드별 메모리 제한 활성화
     * - 각 프로세스/스레드에 개별적으로 메모리 제한 적용
     * - 기본값: false (비활성화)
     * - 멀티프로세싱 환경에서 개별 프로세스 메모리 제어
     */
    @Column(name = "enable_per_process_and_thread_memory_limit")
    private Boolean enablePerProcessAndThreadMemoryLimit = false;

    /**
     * 최대 파일 크기 제한 (KB 단위)
     * - 프로그램이 생성할 수 있는 최대 파일 크기
     * - 기본값: 1MB (1024KB), 최대: 4MB (4096KB)
     * - 파일 출력이 많은 프로그램의 리소스 제한
     */
    @NotNull
    @Min(0)
    @Max(4096)
    @Column(name = "max_file_size")
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
     */
    @Lob
    @Column(name = "additional_files")
    private byte[] additionalFiles;

    /**
     * 네트워크 액세스 허용 여부
     * - 프로그램이 네트워크에 접근할 수 있는지 여부
     * - 기본값: false (보안상 차단)
     * - 외부 API 호출이나 네트워크 통신이 필요한 경우에만 활성화
     */
    @Column(name = "enable_network")
    private Boolean enableNetwork = false;

    // ========== 시간 추적 정보 (Timing Information) ==========
    
    /**
     * 제출 생성 시간
     * - 사용자가 코드를 제출한 시점
     * - @CreationTimestamp로 자동 설정 (수정 불가)
     * - 데이터베이스 레벨에서 자동 생성
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 실행 완료 시간
     * - 코드 실행이 완전히 끝난 시점
     * - 결과 수집, 상태 업데이트 완료 후 설정
     * - null이면 아직 실행 중이거나 대기 중
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * 실행 시작 시간
     * - Docker 컨테이너에서 코드 실행을 시작한 시점
     * - 큐에서 꺼내어 실제 실행을 시작할 때 설정
     * - null이면 아직 실행 시작 전
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * 대기열 진입 시간
     * - 제출이 큐에 추가된 시점
     * - @PrePersist에서 자동 설정 (기본값: 현재 시간)
     * - 큐 대기 시간 계산에 사용
     */
    @Column(name = "queued_at")
    private LocalDateTime queuedAt;

    /**
     * 마지막 업데이트 시간
     * - 레코드가 마지막으로 수정된 시점
     * - @UpdateTimestamp로 자동 갱신
     * - 상태 변경, 결과 업데이트 시마다 갱신
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== 시스템 추적 정보 (System Tracking) ==========
    
    /**
     * 대기열 처리 서버 호스트명
     * - 제출을 큐에 추가한 서버의 호스트명
     * - 로드밸런싱 환경에서 요청 처리 서버 추적
     * - 분산 환경에서 디버깅 및 모니터링에 사용
     */
    @Column(name = "queue_host")
    private String queueHost;

    /**
     * 실제 실행 서버 호스트명
     * - 코드를 실제로 실행한 서버의 호스트명
     * - Docker 컨테이너가 실행된 서버 정보
     * - 분산 환경에서 실행 서버 추적 및 부하 분산 모니터링
     */
    @Column(name = "execution_host")
    private String executionHost;

    // ========== 관계 매핑 (Relationships) ==========
    
    /**
     * 프로그래밍 언어 정보 (지연 로딩)
     * - languageId를 통해 Language 엔티티와 연결
     * - insertable=false, updatable=false로 읽기 전용
     * - FetchType.LAZY로 성능 최적화
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", insertable = false, updatable = false)
    private Language language;

    /**
     * 실행 상태 열거형 (임시 필드)
     * - statusId와 Status 열거형 간의 변환을 위한 임시 필드
     * - @Transient로 데이터베이스에 저장되지 않음
     * - getStatus()/setStatus() 메서드에서 동기화
     */
    @Transient
    private Status status;

    // ========== 비즈니스 로직 메서드 (Business Logic Methods) ==========
    
    /**
     * 엔티티 저장 전 자동 실행 메서드
     * - @PrePersist 어노테이션으로 JPA가 자동 호출
     * - 새 엔티티 저장 시 필수 필드들을 자동 설정
     * 
     * 자동 설정 내용:
     * 1. queuedAt: 대기열 진입 시간 (null인 경우 현재 시간으로 설정)
     * 2. token: 고유 토큰 (null인 경우 UUID 생성)
     */
    @PrePersist
    protected void onCreate() {
        // 대기열 진입 시간이 설정되지 않은 경우 현재 시간으로 설정
        if (this.queuedAt == null) {
            this.queuedAt = LocalDateTime.now();
        }
        // 고유 토큰이 설정되지 않은 경우 UUID 생성
        if (this.token == null) {
            this.token = java.util.UUID.randomUUID().toString();
        }
    }

    /**
     * 실행 완료 여부 확인
     * - finishedAt 필드가 null이 아니면 실행 완료로 판단
     * - 실행 완료는 결과 수집까지 모두 끝난 상태를 의미
     * 
     * @return true: 실행 완료, false: 실행 중이거나 대기 중
     */
    public boolean isCompleted() {
        return finishedAt != null;
    }

    /**
     * 오류 상태 여부 확인
     * - statusId가 6 이상이면 오류 상태로 판단
     * - 6: Compilation Error, 7: Runtime Error, 8: Internal Error 등
     * - 정상 완료(3: Accepted)나 대기/처리 중(1,2)은 오류가 아님
     * 
     * @return true: 오류 상태, false: 정상 상태
     */
    public boolean hasErrors() {
        return statusId != null && statusId >= 6; // CE(6) 이상은 모두 오류
    }

    /**
     * 다중 파일 프로젝트 제출 여부 확인
     * - Language 엔티티의 이름이 "Multi-file program"인지 확인
     * - 프로젝트 제출은 여러 파일을 포함하는 복잡한 구조
     * - additionalFiles와 함께 사용되어 프로젝트 관리
     * 
     * @return true: 다중 파일 프로젝트, false: 단일 파일 제출
     */
    public boolean isProject() {
        return language != null && "Multi-file program".equals(language.getName());
    }

    /**
     * 추가 파일 존재 여부 확인
     * - additionalFiles 바이너리 데이터가 존재하고 크기가 0보다 큰지 확인
     * - 프로젝트 제출이나 테스트 데이터 포함 시 사용
     * - 파일 크기로 존재 여부를 간단히 판단
     * 
     * @return true: 추가 파일 존재, false: 추가 파일 없음
     */
    public boolean hasAdditionalFiles() {
        return additionalFiles != null && additionalFiles.length > 0;
    }

    /**
     * Status 열거형 객체 반환
     * - statusId를 Status 열거형으로 변환하여 반환
     * - 캐싱을 통해 동일한 statusId에 대해 재계산 방지
     * - status 필드가 null인 경우에만 Status.fromId() 호출
     * 
     * @return Status 열거형 객체 (statusId가 null이면 null 반환)
     */
    public Status getStatus() {
        if (status == null && statusId != null) {
            status = Status.fromId(statusId);
        }
        return status;
    }

    /**
     * Status 열거형 설정
     * - Status 열거형을 받아서 statusId와 status 필드 동기화
     * - 양방향 동기화로 데이터 일관성 보장
     * - null 설정 시 statusId도 null로 설정
     * 
     * @param status 설정할 Status 열거형 객체
     */
    public void setStatus(Status status) {
        this.status = status;
        this.statusId = status != null ? status.getId() : null;
    }
}
