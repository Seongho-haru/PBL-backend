package com.PBL.lab.LanguageServerProtocol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON-RPC 에러 객체
 * 
 * JSON-RPC 2.0 사양에 따른 에러 정보
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcError {
    
    /**
     * 에러 코드
     * 
     * 표준 에러 코드:
     * -32700: Parse error (파싱 오류)
     * -32600: Invalid Request (잘못된 요청)
     * -32601: Method not found (메서드 없음)
     * -32602: Invalid params (잘못된 파라미터)
     * -32603: Internal error (내부 오류)
     * -32000 ~ -32099: Server error (서버 오류)
     */
    private int code;
    
    /**
     * 에러 메시지
     */
    private String message;
    
    /**
     * 추가 에러 데이터 (선택사항)
     */
    private Object data;
    
    /**
     * data 없이 생성
     */
    public JsonRpcError(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    // ========== 표준 에러 생성 헬퍼 ==========
    
    /**
     * 파싱 오류
     */
    public static JsonRpcError parseError(String detail) {
        return new JsonRpcError(-32700, "Parse error", detail);
    }
    
    /**
     * 잘못된 요청
     */
    public static JsonRpcError invalidRequest(String detail) {
        return new JsonRpcError(-32600, "Invalid Request", detail);
    }
    
    /**
     * 메서드를 찾을 수 없음
     */
    public static JsonRpcError methodNotFound(String method) {
        return new JsonRpcError(-32601, "Method not found: " + method, null);
    }
    
    /**
     * 잘못된 파라미터
     */
    public static JsonRpcError invalidParams(String detail) {
        return new JsonRpcError(-32602, "Invalid params", detail);
    }
    
    /**
     * 내부 오류
     */
    public static JsonRpcError internalError(String detail) {
        return new JsonRpcError(-32603, "Internal error", detail);
    }
}
