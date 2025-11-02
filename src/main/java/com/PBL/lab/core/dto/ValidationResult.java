package com.PBL.lab.core.dto;


import lombok.*;

/**
 * 유효성 검증 결과 데이터 클래스
 *
 * 코드 실행 전 제출 내용의 유효성 검증 결과를 담는 클래스입니다.
 * 검증이 성공했는지 여부와 실패 시 오류 메시지를 포함합니다.
 *
 * 사용 목적:
 * - 잘못된 제출을 사전에 차단하여 시스템 리소스 보호
 * - 명확한 오류 메시지 제공으로 사용자 경험 개선
 * - 검증 로직의 결과를 일관된 형태로 반환
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {
    private final boolean valid;  // 검증 성공 여부
    private final String error;   // 검증 실패 시 오류 메시지

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(String error) {
        return new ValidationResult(false, error);
    }
}
