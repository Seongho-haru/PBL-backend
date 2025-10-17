package com.PBL.lab.grading.entity;

import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.judge0.entity.SubmissionInputOutput;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grading", indexes = {
        @Index(name = "idx_submission_token", columnList = "token"),
        @Index(name = "idx_submission_status", columnList = "status_id"),
        @Index(name = "idx_submission_created_at", columnList = "created_at")
})
@Data
@EqualsAndHashCode(exclude = {"language", "constraints"})
@ToString(exclude = {"sourceCode"})
public class Grading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String token;

    /**
     * 실행할 소스 코드
     * - 사용자가 제출한 실제 프로그래밍 코드
     * - TEXT 타입으로 대용량 코드 지원 (최대 1GB)
     * - 모든 프로그래밍 언어의 소스 코드 저장 가능
     */
    @Lob
    @NotNull
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
     * 프로그래밍 언어 정보 (지연 로딩)
     * - languageId를 통해 Language 엔티티와 연결
     * - insertable=false, updatable=false로 읽기 전용
     * - FetchType.LAZY로 성능 최적화
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", insertable = false, updatable = false)
    private Language language;

    @NotNull
    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    /**
     * 실행 제약조건 (1:1 관계)
     * - 시간/메모리 제한, 컴파일러 옵션, 추가 파일 등
     * - 지연 로딩으로 성능 최적화
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "constraints_id", nullable = false)
    private Constraints constraints;


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


    // ========== 관계 매핑 (Relationships) ==========



    /**
     * 실행 상태 열거형 (임시 필드)
     * - statusId와 Status 열거형 간의 변환을 위한 임시 필드
     * - @Transient로 데이터베이스에 저장되지 않음
     * - getStatus()/setStatus() 메서드에서 동기화
     */
    @Transient
    private Status status;
    
    /**
     * Status 열거형을 반환합니다.
     * statusId를 기반으로 Status 열거형을 생성합니다.
     */
    public Status getStatus() {
        if (status == null && statusId != null) {
            status = Status.fromId(statusId);
        }
        return status;
    }
    
    /**
     * Status 열거형을 설정합니다.
     * statusId도 함께 업데이트됩니다.
     */
    public void setStatus(Status status) {
        this.status = status;
        if (status != null) {
            this.statusId = status.getId();
        }
    }


    /**
     * 추가적인 실행 관련 메시지
     * - 시스템에서 생성한 추가 정보나 설명
     * - 오류 발생 시 상세한 원인 설명
     * - 디버깅을 위한 추가 컨텍스트 정보
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;

    //submission 틀린/에러 id을 넣으면 알아서 submissionInputOutput이랑 조인
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "error_id", nullable = false)
    private SubmissionInputOutput inputOutput;

    // ========== 시간 추적 정보 (Timing Information) ==========

    /**
     * 제출 생성 시간
     * - 사용자가 코드를 제출한 시점
     * - @CreationTimestamp로 자동 설정 (수정 불가)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 실행 완료 시간
     * - 코드 실행이 완전히 끝난 시점
     * - null이면 아직 실행 중이거나 대기 중
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * 실행 시작 시간
     * - Docker 컨테이너에서 코드 실행을 시작한 시점
     * - null이면 아직 실행 시작 전
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * 대기열 진입 시간
     * - 제출이 큐에 추가된 시점
     * - @PrePersist에서 자동 설정
     */
    @Column(name = "queued_at")
    private LocalDateTime queuedAt;

    /**
     * 마지막 업데이트 시간
     * - @UpdateTimestamp로 자동 갱신
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // ========== 시스템 추적 정보 (System Tracking) ==========

    /**
     * 대기열 처리 서버 호스트명
     * - 제출을 큐에 추가한 서버의 호스트명
     */
    @Column(name = "queue_host")
    private String queueHost;

    /**
     * 실제 실행 서버 호스트명
     * - 코드를 실제로 실행한 서버의 호스트명
     */
    @Column(name = "execution_host")
    private String executionHost;

    // ========== 실행 성능 정보 (Execution Performance) ==========

    /**
     * CPU 실행 시간 (초)
     * - 소수점 6자리까지 정밀도 지원
     */
    @Column(precision = 10, scale = 6)
    private BigDecimal time;

    /**
     * 메모리 사용량 (KB 단위)
     */
    private Integer memory;

    /**
     * 전체 소요 시간 (Wall Time)
     * - 소수점 6자리까지 정밀도 지원
     */
    @Column(name = "wall_time", precision = 10, scale = 6)
    private BigDecimal wallTime;

    /**
     * 프로그램 종료 코드
     * - 0: 정상 종료, 1-255: 오류 종료, -1: 비정상 종료
     */
    @Column(name = "exit_code")
    private Integer exitCode;

    /**
     * 프로그램 종료 시그널
     * - SIGKILL (9), SIGSEGV (11), SIGTERM (15) 등
     */
    @Column(name = "exit_signal")
    private Integer exitSignal;


//    /**
//     * 테스트케이스 토큰들 (1:N 관계)
//     * - 각 테스트케이스별 실행 토큰 저장
//     * - 진행률 계산 및 결과 조회용
//     */
//    @OneToMany(mappedBy = "grading", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<GradingTestCaseToken> testCaseTokens;

    /**
     * 추가 파일 존재 여부 확인
     * - constraints.additionalFiles 바이너리 데이터가 존재하고 크기가 0보다 큰지 확인
     * - 프로젝트 제출이나 테스트 데이터 포함 시 사용
     * - 파일 크기로 존재 여부를 간단히 판단
     * 
     * @return true: 추가 파일 존재, false: 추가 파일 없음
     */
    public boolean hasAdditionalFiles() {
        return constraints != null && constraints.getAdditionalFiles() != null && constraints.getAdditionalFiles().length > 0;
    }

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
}
