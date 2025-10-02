package com.PBL.lab.judge0.entity;

import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lab.core.enums.Status;
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
 * Submission Entity - 코드 제출 핵심 정보 엔티티
 * 
 * === 주요 목적 ===
 * - 코드 실행에 필요한 핵심 정보만을 관리하는 엔티티
 * - 원본 Judge0 Ruby Submission 모델의 핵심 부분을 Java/JPA로 포팅
 * - 다른 세부 정보는 별도 엔티티로 분리하여 관리
 * 
 * === 포함 데이터 (코드 실행 기본 정보) ===
 * 1. 기본 제출 정보:
 *    - sourceCode: 실행할 소스 코드 (TEXT 타입으로 대용량 코드 지원)
 *    - languageId/language: 사용할 프로그래밍 언어 정보 (Java, Python, C++ 등)
 *    - token: 제출 식별을 위한 고유 UUID 토큰 (API 조회 키)
 *    - status/statusId: 실행 상태 (QUEUE, PROCESSING, ACCEPTED, WRONG_ANSWER 등)
 * 
 * === 분리된 엔티티와의 관계 ===
 * - SubmissionInputOutput: 입출력 정보 (stdin, stdout, stderr, compileOutput, message)
 * - Constraints: 실행 제약조건 (시간/메모리 제한, 컴파일러 옵션, 추가 파일 등)
 * 
 * === 데이터베이스 최적화 ===
 * - 인덱스 설정:
 *   * token: 고유 인덱스 (빠른 API 조회)
 *   * status_id: 상태별 제출 검색 최적화
 * - 참조 무결성: language_id -> languages.id (Foreign Key)
 * - 대용량 데이터: TEXT 타입 사용 (source_code)
 * 
 * === 비즈니스 로직 메서드 ===
 * - isCompleted(): 실행 완료 여부 확인 (timing.finishedAt 존재 여부)
 * - hasErrors(): 오류 상태 여부 확인 (statusId >= 6, CE 이상)
 * - isProject(): 다중 파일 프로젝트 제출 여부 (Language 이름 확인)
 * - hasAdditionalFiles(): 추가 파일 존재 여부 (constraints.additionalFiles 확인)
 * - getStatus()/setStatus(): Status 열거형과 statusId 동기화
 */
@Entity
@Table(name = "submissions", indexes = {
    @Index(name = "idx_submission_token", columnList = "token"),
    @Index(name = "idx_submission_status", columnList = "status_id"),
    @Index(name = "idx_submission_created_at", columnList = "created_at")
})
@Data
@EqualsAndHashCode(exclude = {"language", "inputOutput", "constraints"})
@ToString(exclude = {"sourceCode"})
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

    @Column
    private boolean isGrading;

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

    /**
     * 입출력 정보 (1:1 관계)
     * - stdin, stdout, stderr, compileOutput, message 등
     * - 지연 로딩으로 성능 최적화
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "input_output_id", nullable = false)
    private SubmissionInputOutput inputOutput;

    /**
     * 실행 제약조건 (1:1 관계)
     * - 시간/메모리 제한, 컴파일러 옵션, 추가 파일 등
     * - 지연 로딩으로 성능 최적화
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "constraints_id", nullable = false)
    private Constraints constraints;

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
     * - constraints.additionalFiles 바이너리 데이터가 존재하고 크기가 0보다 큰지 확인
     * - 프로젝트 제출이나 테스트 데이터 포함 시 사용
     * - 파일 크기로 존재 여부를 간단히 판단
     * 
     * @return true: 추가 파일 존재, false: 추가 파일 없음
     */
    public boolean hasAdditionalFiles() {
        return constraints != null && constraints.getAdditionalFiles() != null && constraints.getAdditionalFiles().length > 0;
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
