package com.PBL.lab.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BaseExecutionResponse - 코드 실행 응답의 공통 베이스 DTO
 *
 * SubmissionResponse와 GradingResponse의 공통 필드를 추상화한 베이스 클래스입니다.
 *
 * === 공통 필드 ===
 * 1. 기본 정보: id, token, sourceCode, languageId
 * 2. 상태 정보: status, constraints
 * 3. 시간 정보: createdAt, finishedAt
 * 4. 성능 정보: time, wallTime, memory, exitCode, exitSignal
 *
 * === 사용 방법 ===
 * 이 클래스를 상속받아 각 모듈별 특화 필드를 추가합니다:
 * - SubmissionResponse: inputOutput
 * - GradingResponse: inputOutput, message, updatedAt, problemId, progress
 *
 * === SuperBuilder ===
 * @SuperBuilder를 사용하여 부모-자식 클래스 간 Builder 패턴을 지원합니다.
 * 자식 클래스에서도 @SuperBuilder를 사용해야 합니다.
 *
 * === JSON 직렬화 ===
 * @JsonInclude(NON_NULL)로 null 값은 JSON에서 제외됩니다.
 */
@Data
@SuperBuilder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseExecutionResponse {

    // ========== 기본 정보 (Basic Information) ==========

    /**
     * 엔티티 고유 식별자
     * - 데이터베이스의 Primary Key
     * - 내부 시스템에서만 사용
     */
    protected Long id;

    /**
     * 고유 토큰 (UUID)
     * - API 조회 시 사용되는 공개 식별자
     * - 36자리 UUID 형식 (예: 550e8400-e29b-41d4-a716-446655440000)
     */
    protected String token;

    /**
     * 소스 코드
     * - 사용자가 제출한 실제 프로그래밍 코드
     */
    @JsonProperty("source_code")
    protected String sourceCode;

    /**
     * 프로그래밍 언어 ID
     * - 1: C, 2: C++, 4: Java, 71: Python 등
     */
    @JsonProperty("language_id")
    protected Integer languageId;

    // inputOutput 필드는 자식 클래스에서 각각 JsonProperty 지정
    protected ExecutionInputOutputDTO inputOutput;

    // ========== 상태 정보 (Status Information) ==========

    /**
     * 실행 상태
     * - QUEUE, PROCESSING, ACCEPTED, WRONG_ANSWER 등
     */
    protected StatusResponse status;

    /**
     * 실행 제약조건
     * - 시간/메모리 제한, 컴파일러 옵션 등
     */
    protected ConstraintsResponse constraints;

    // ========== 시간 정보 (Timing Information) ==========

    /**
     * 생성 시간
     * - 사용자가 코드를 제출한 시점
     * - ISO 8601 형식 (예: 2024-01-15T10:30:45.123Z)
     */
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    protected LocalDateTime createdAt;

    /**
     * 실행 완료 시간
     * - 코드 실행이 완전히 끝난 시점
     * - null이면 아직 실행 중이거나 대기 중
     */
    @JsonProperty("finished_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    protected LocalDateTime finishedAt;

    // ========== 성능 정보 (Performance Information) ==========

    /**
     * CPU 실행 시간 (초)
     * - 실제 CPU가 프로그램을 실행한 시간
     * - 소수점 6자리까지 정밀도 지원
     * - 예: 0.123456
     */
    protected BigDecimal time;

    /**
     * 전체 소요 시간 (Wall Time, 초)
     * - 프로그램 시작부터 종료까지의 실제 경과 시간
     * - I/O 대기 시간 포함
     * - 소수점 6자리까지 정밀도 지원
     */
    @JsonProperty("wall_time")
    protected BigDecimal wallTime;

    /**
     * 메모리 사용량 (KB 단위)
     * - 프로그램이 사용한 최대 메모리
     * - 예: 12345 (약 12MB)
     */
    protected Integer memory;

    /**
     * 프로그램 종료 코드
     * - 0: 정상 종료
     * - 1-255: 오류 종료
     * - null: 아직 종료되지 않음
     */
    @JsonProperty("exit_code")
    protected Integer exitCode;

    /**
     * 프로그램 종료 시그널
     * - SIGKILL (9): 강제 종료
     * - SIGSEGV (11): 세그멘테이션 오류
     * - SIGTERM (15): 정상 종료 요청
     * - null: 시그널 없이 종료
     */
    @JsonProperty("exit_signal")
    protected Integer exitSignal;
}
