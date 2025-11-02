package com.PBL.lab.judge0.entity;

import com.PBL.lab.core.entity.BaseEntity;
import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.core.webhook.WebhookCallbackEntity;
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
@Table(name = "submissions")
@Data
@EqualsAndHashCode(callSuper = true)
public class Submission extends BaseEntity {

    // ========== 비즈니스 로직 메서드 (Business Logic Methods) ==========

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

}
