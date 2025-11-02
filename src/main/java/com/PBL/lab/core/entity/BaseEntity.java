package com.PBL.lab.core.entity;

import com.PBL.lab.core.dto.CodeExecutionRequest;
import com.PBL.lab.core.dto.SecurityConstraints;
import com.PBL.lab.core.enums.FileSystemAccess;
import com.PBL.lab.core.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BaseEntity - 코드 실행 엔티티의 공통 베이스 클래스
 *
 * Submission과 Grading 엔티티의 공통 필드와 메서드를 추상화한 베이스 엔티티입니다.
 *
 * === 포함 내용 ===
 * 1. 기본 식별 정보: id, token
 * 2. 코드 실행 정보: sourceCode, languageId, statusId
 * 3. 관계 매핑: language, constraints, status
 * 4. 시간 추적: createdAt, finishedAt, startedAt, queuedAt, updatedAt
 * 5. 시스템 추적: queueHost, executionHost
 * 6. 실행 성능: time, memory, wallTime, exitCode, exitSignal
 * 7. 공통 비즈니스 로직: onCreate(), getStatus(), setStatus(), hasAdditionalFiles()
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(exclude = {"language", "constraints"})
@ToString(exclude = {"sourceCode"})
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    // ========== 기본 식별 정보 (Basic Identification) ==========

    /**
     * 엔티티 고유 식별자 (Primary Key)
     * - 데이터베이스에서 자동 생성되는 순차적 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 고유 토큰 (UUID)
     * - API 조회 시 사용되는 공개 식별자
     * - 36자리 UUID 형식
     * - 고유 인덱스로 설정되어 빠른 조회 지원
     */
    @Column(unique = true, nullable = false, length = 36)
    protected String token;

    // ========== 코드 실행 정보 (Code Execution Information) ==========

    /**
     * 실행할 소스 코드
     * - 사용자가 제출한 실제 프로그래밍 코드
     * - TEXT 타입으로 대용량 코드 지원 (최대 1GB)
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    protected String sourceCode;

    /**
     * 프로그래밍 언어 ID
     * - languages 테이블의 외래키
     * - 1: C, 2: C++, 4: Java, 71: Python 등
     * - 필수 필드로 반드시 설정되어야 함
     */
    @NotNull
    @Column(name = "language_id", nullable = false)
    protected Integer languageId;

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
    protected Integer statusId; // 기본값: 대기열

    // ========== 관계 매핑 (Relationships) ==========

    /**
     * 프로그래밍 언어 정보 (지연 로딩)
     * - languageId를 통해 Language 엔티티와 연결
     * - insertable=false, updatable=false로 읽기 전용
     * - FetchType.LAZY로 성능 최적화
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", insertable = false, updatable = false)
    protected Language language;

    /**
     * 실행 제약조건 (1:1 관계)
     * - 시간/메모리 제한, 컴파일러 옵션, 추가 파일 등
     * - 지연 로딩으로 성능 최적화
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "constraints_id", nullable = false)
    protected Constraints constraints;

    /**
     * 실행 상태 열거형 (임시 필드)
     * - statusId와 Status 열거형 간의 변환을 위한 임시 필드
     * - @Transient로 데이터베이스에 저장되지 않음
     * - getStatus()/setStatus() 메서드에서 동기화
     */
    @Transient
    protected Status status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "input_output_id", nullable = false)
    private ExecutionInputOutput inputOutput;

    // ========== 시간 추적 정보 (Timing Information) ==========

    /**
     * 생성 시간
     * - 사용자가 코드를 제출한 시점
     * - @CreationTimestamp로 자동 설정 (수정 불가)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    /**
     * 실행 완료 시간
     * - 코드 실행이 완전히 끝난 시점
     * - null이면 아직 실행 중이거나 대기 중
     */
    @Column(name = "finished_at")
    protected LocalDateTime finishedAt;

    /**
     * 실행 시작 시간
     * - Docker 컨테이너에서 코드 실행을 시작한 시점
     * - null이면 아직 실행 시작 전
     */
    @Column(name = "started_at")
    protected LocalDateTime startedAt;

    /**
     * 대기열 진입 시간
     * - 제출이 큐에 추가된 시점
     * - @PrePersist에서 자동 설정
     */
    @Column(name = "queued_at")
    protected LocalDateTime queuedAt;

    /**
     * 마지막 업데이트 시간
     * - @UpdateTimestamp로 자동 갱신
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    // ========== 시스템 추적 정보 (System Tracking) ==========

    /**
     * 대기열 처리 서버 호스트명
     * - 제출을 큐에 추가한 서버의 호스트명
     */
    @Column(name = "queue_host")
    protected String queueHost;

    /**
     * 실제 실행 서버 호스트명
     * - 코드를 실제로 실행한 서버의 호스트명
     */
    @Column(name = "execution_host")
    protected String executionHost;

    // ========== 실행 성능 정보 (Execution Performance) ==========

    /**
     * CPU 실행 시간 (초)
     * - 소수점 6자리까지 정밀도 지원
     */
    @Column(precision = 10, scale = 6)
    protected BigDecimal time;

    /**
     * 메모리 사용량 (KB 단위)
     */
    protected Integer memory;

    /**
     * 전체 소요 시간 (Wall Time)
     * - 소수점 6자리까지 정밀도 지원
     */
    @Column(name = "wall_time", precision = 10, scale = 6)
    protected BigDecimal wallTime;

    /**
     * 프로그램 종료 코드
     * - 0: 정상 종료, 1-255: 오류 종료, -1: 비정상 종료
     */
    @Column(name = "exit_code")
    protected Integer exitCode;

    /**
     * 프로그램 종료 시그널
     * - SIGKILL (9), SIGSEGV (11), SIGTERM (15) 등
     */
    @Column(name = "exit_signal")
    protected Integer exitSignal;

    // ========== 비즈니스 로직 메서드 (Business Logic Methods) ==========

    /**
     * 엔티티 저장 전 자동 실행 메서드
     * - @PrePersist 어노테이션으로 JPA가 자동 호출
     * - 새 엔티티 저장 시 필수 필드들을 자동 설정
     *
     * 자동 설정 내용:
     * 1. token: 고유 토큰 (null인 경우 UUID 생성)
     * 2. queuedAt: 대기열 진입 시간 (null인 경우 현재 시간으로 설정)
     */
    @PrePersist
    protected void onCreate() {
        // 고유 토큰이 설정되지 않은 경우 UUID 생성
        if (this.token == null) {
            this.token = java.util.UUID.randomUUID().toString();
        }
        // queuedAt이 설정되지 않은 경우 현재 시간으로 설정
        if (this.queuedAt == null) {
            this.queuedAt = LocalDateTime.now();
        }
    }

    /**
     * 추가 파일 존재 여부 확인
     * - constraints.additionalFiles 바이너리 데이터가 존재하고 크기가 0보다 큰지 확인
     * - 프로젝트 제출이나 테스트 데이터 포함 시 사용
     * - 파일 크기로 존재 여부를 간단히 판단
     *
     * @return true: 추가 파일 존재, false: 추가 파일 없음
     */
    public boolean hasAdditionalFiles() {
        return constraints != null &&
               constraints.getAdditionalFiles() != null &&
               constraints.getAdditionalFiles().length > 0;
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

    /**
     * BaseEntity를 CodeExecutionRequest로 변환하는 공통 메서드
     *
     * Docker 컨테이너에서 코드 실행에 필요한 모든 정보를 CodeExecutionRequest DTO로 변환합니다.
     * stdin과 expectedOutput은 자식 클래스에서 결정하도록 추상 메서드로 정의합니다.
     *
     * @param stdin 표준 입력 데이터
     * @param expectedOutput 예상 출력 결과
     * @param enableNetwork 네트워크 활성화 여부
     * @return CodeExecutionRequest Docker 실행용 요청 객체
     */
    public CodeExecutionRequest buildCodeExecutionRequest(BaseEntity baseEntity) {

        // inputOutput은 정답일 때는 null이고, 오답일 때만 채워짐
        String stdin = (baseEntity.getInputOutput() != null) ? baseEntity.getInputOutput().getStdin() : null;
        String expectedOutput = (getInputOutput() != null) ? getInputOutput().getExpectedOutput() : null;

        return CodeExecutionRequest.builder()
                // 기본 실행 정보
                .sourceCode(getSourceCode())
                .language(getLanguage())
                .stdin(stdin)
                .expectedOutput(expectedOutput)
                .additionalFiles(getConstraints().getAdditionalFiles())

                // 컴파일/실행 옵션
                .compilerOptions(getConstraints().getCompilerOptions())
                .commandLineArguments(getConstraints().getCommandLineArguments())
                .redirectStderrToStdout(getConstraints().getRedirectStderrToStdout())
                .enableNetwork(getConstraints().getEnableNetwork())

                // 보안 제약조건
                .constraints(SecurityConstraints.builder()
                        .timeLimit(getConstraints().getCpuTimeLimit())
                        .memoryLimit(getConstraints().getMemoryLimit())
                        .processLimit(getConstraints().getMaxProcessesAndOrThreads())
                        .networkAccess(getConstraints().getEnableNetwork())
                        .fileSystemAccess(FileSystemAccess.READ_ONLY)
                        .build())
                .build();
    }
}
