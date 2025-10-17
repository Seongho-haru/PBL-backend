package com.PBL.lecture;

/**
 * 권한 거부 예외
 * 사용자가 특정 리소스에 접근할 권한이 없을 때 발생
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
