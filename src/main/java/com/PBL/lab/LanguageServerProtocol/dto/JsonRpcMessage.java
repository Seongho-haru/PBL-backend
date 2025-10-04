package com.PBL.lab.LanguageServerProtocol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * JSON-RPC 2.0 메시지 DTO
 * 
 * LSP(Language Server Protocol) 통신에 사용되는 JSON-RPC 메시지 형식
 * 요청, 응답, Notification 모두 이 DTO 하나로 처리
 * 
 * null 필드는 JSON 직렬화 시 자동으로 제외됨
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에서 제외
public class JsonRpcMessage {
    
    /**
     * JSON-RPC 버전 (항상 "2.0")
     */
    private String jsonrpc = "2.0";
    
    /**
     * 요청/응답 매칭용 ID
     * - 요청: 클라이언트가 생성
     * - 응답: 요청과 동일한 ID 사용
     * - Notification: null
     */
    private Object id;
    
    /**
     * 호출할 메서드명 (요청/Notification에만 존재)
     * 
     * 예시:
     * - "initialize"
     * - "textDocument/completion"
     * - "textDocument/didChange"
     */
    private String method;
    
    /**
     * 메서드 파라미터 (요청/Notification에만 존재)
     */
    private Object params;
    
    /**
     * 성공 응답 결과 (응답에만 존재)
     */
    private Object result;
    
    /**
     * 실패 응답 에러 (응답에만 존재)
     */
    private JsonRpcError error;
    
    // ========== 헬퍼 메서드 ==========
    
    /**
     * 요청 메시지 생성
     */
    public static JsonRpcMessage request(Object id, String method, Object params) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.setId(id);
        msg.setMethod(method);
        msg.setParams(params);
        return msg;
    }
    
    /**
     * 성공 응답 메시지 생성
     */
    public static JsonRpcMessage response(Object id, Object result) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.setId(id);
        msg.setResult(result);
        return msg;
    }
    
    /**
     * 에러 응답 메시지 생성
     */
    public static JsonRpcMessage error(Object id, int code, String message) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.setId(id);
        msg.setError(new JsonRpcError(code, message));
        return msg;
    }
    
    /**
     * Notification 메시지 생성 (응답 불필요)
     */
    public static JsonRpcMessage notification(String method, Object params) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.setMethod(method);
        msg.setParams(params);
        return msg;
    }
    
    // ========== 메시지 타입 판별 ==========
    
    /**
     * 요청 메시지인지 확인
     */
    public boolean isRequest() {
        return method != null && id != null;
    }
    
    /**
     * Notification인지 확인
     */
    public boolean isNotification() {
        return method != null && id == null;
    }
    
    /**
     * 응답 메시지인지 확인
     */
    public boolean isResponse() {
        return method == null && (result != null || error != null);
    }
    
    /**
     * 에러 응답인지 확인
     */
    public boolean isError() {
        return error != null;
    }
}
