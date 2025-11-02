package com.PBL.lab.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * BaseExecutionRequest - 코드 실행 요청의 공통 베이스 DTO
 *
 * SubmissionRequest와 GradingRequest의 공통 필드를 추상화한 베이스 클래스입니다.
 *
 * === 공통 필드 ===
 * - sourceCode: 실행할 소스 코드
 * - languageId: 프로그래밍 언어 ID
 * - additionalFiles: 추가 파일 (Base64 인코딩)
 *
 * === 사용 방법 ===
 * 이 클래스를 상속받아 각 모듈별 특화 필드를 추가합니다:
 * - SubmissionRequest: stdin, expectedOutput, constraintsId
 * - GradingRequest: problemId
 *
 * === SuperBuilder ===
 * @SuperBuilder를 사용하여 부모-자식 클래스 간 Builder 패턴을 지원합니다.
 * 자식 클래스에서도 @SuperBuilder를 사용해야 합니다.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseExecutionRequest {

    /**
     * 실행할 소스 코드
     * - 사용자가 제출한 실제 프로그래밍 코드
     * - 모든 프로그래밍 언어의 소스 코드 저장 가능
     */
    @JsonProperty("source_code")
    protected String sourceCode;

    /**
     * 프로그래밍 언어 ID
     * - languages 테이블의 외래키 참조
     * - 1: C, 2: C++, 4: Java, 71: Python 등
     * - 필수 필드로 반드시 설정되어야 함
     * - 1 이상의 양수여야 함
     */
    @JsonProperty("language_id")
    @NotNull(message = "언어 ID는 필수입니다")
    @Min(value = 1, message = "언어 ID는 양수여야 합니다")
    protected Integer languageId;

    /**
     * 추가 파일 (Base64 인코딩)
     * - 다중 파일 프로젝트나 테스트 데이터 파일 등
     * - Base64로 인코딩된 ZIP 파일 형태
     * - 선택적 필드 (null 가능)
     */
    @JsonProperty("additional_files")
    protected String additionalFiles;
}
