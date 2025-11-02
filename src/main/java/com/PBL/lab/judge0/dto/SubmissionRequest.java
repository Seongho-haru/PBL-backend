package com.PBL.lab.judge0.dto;

import com.PBL.lab.core.dto.BaseExecutionRequest;
import com.PBL.lab.core.dto.ExecutionResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Submission Request DTO
 *
 * 제출 생성을 위한 데이터 전송 객체
 * POST /submissions 엔드포인트의 요청 페이로드를 나타냄
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubmissionRequest extends BaseExecutionRequest {
    // Submission 전용필드

    //입출력 필드
    private String stdin; // 표준 입력

    @JsonProperty("expected_output")
    private String expectedOutput; // 예상 출력

    /**
     * 제약조건 ID
     * - 기존에 저장된 제약조건 조합을 사용할 때 지정
     * - null이면 기본 제약조건(id=1) 사용
     */
    @JsonProperty("constraints_id")
    private Long constraintsId;
}
