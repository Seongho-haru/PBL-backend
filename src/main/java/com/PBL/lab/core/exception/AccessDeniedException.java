package com.PBL.lab.core.exception;

/**
 * AccessDeniedException - 접근 권한 거부 예외
 *
 * === 사용 시나리오 ===
 * - 회원 제출(user != null)에 대해 다른 사용자가 접근 시도
 * - 익명 사용자가 회원 제출에 접근 시도
 *
 * === HTTP 응답 ===
 * - 403 Forbidden 상태 코드로 반환
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
