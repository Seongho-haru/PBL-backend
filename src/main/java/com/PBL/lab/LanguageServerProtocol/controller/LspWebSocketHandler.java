package com.PBL.lab.LanguageServerProtocol.controller;

import com.PBL.lab.LanguageServerProtocol.dto.JsonRpcMessage;
import com.PBL.lab.LanguageServerProtocol.service.LspContainerManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LSP WebSocket Handler - Raw WebSocket 방식
 * 
 * LSP 서버는 비동기로 여러 메시지를 보내므로,
 * 별도 스레드에서 지속적으로 메시지를 받아 클라이언트로 전달
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LspWebSocketHandler extends TextWebSocketHandler {
    
    private final LspContainerManager containerManager;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    // 세션별 언어 매핑
    private final Map<String, String> sessionLanguages = new ConcurrentHashMap<>();
    // 세션별 메시지 수신 스레드 종료 플래그
    private final Map<String, Boolean> sessionActive = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String language = extractLanguageFromPath(session.getUri().getPath());
        String sessionId = session.getId();
        
        sessionLanguages.put(sessionId, language);
        sessionActive.put(sessionId, true);
        
        log.info("LSP WebSocket 연결: sessionId={}, language={}, uri={}", 
                sessionId, language, session.getUri());
        
        try {
            LspContainerManager.LspContainer container = containerManager.createContainer(language, sessionId);
            log.info("LSP 컨테이너 생성 완료: sessionId={}, language={}", sessionId, language);
            
            // LSP로부터 메시지를 지속적으로 받아서 클라이언트로 전달하는 스레드 시작
            startMessageRelay(session, container);
            
        } catch (Exception e) {
            log.error("LSP 컨테이너 생성 실패: sessionId={}, language={}", sessionId, language, e);
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    
    /**
     * LSP → 클라이언트 메시지 중계 스레드 시작
     */
    private void startMessageRelay(WebSocketSession session, LspContainerManager.LspContainer container) {
        String sessionId = session.getId();
        String language = container.getLanguage();
        
        executorService.submit(() -> {
            log.info("[{}][{}] LSP 메시지 수신 스레드 시작", sessionId, language);
            
            while (sessionActive.getOrDefault(sessionId, false) && session.isOpen()) {
                try {
                    // LSP로부터 메시지 대기 (1초 타임아웃)
                    String message = container.getResponseQueue().poll(1, java.util.concurrent.TimeUnit.SECONDS);
                    
                    if (message != null && session.isOpen()) {
                        // 클라이언트로 전달
                        session.sendMessage(new TextMessage(message));
                        log.debug("[{}][{}] ← LSP: {}", sessionId, language, 
                                message.length() > 200 ? message.substring(0, 200) + "..." : message);
                    }
                    
                } catch (InterruptedException e) {
                    log.info("[{}][{}] LSP 메시지 수신 스레드 중단", sessionId, language);
                    break;
                } catch (Exception e) {
                    if (session.isOpen()) {
                        log.error("[{}][{}] LSP 메시지 전달 오류", sessionId, language, e);
                    }
                }
            }
            
            log.info("[{}][{}] LSP 메시지 수신 스레드 종료", sessionId, language);
        });
    }
    
    /**
     * 클라이언트 → LSP 메시지 전송
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String language = sessionLanguages.get(sessionId);
        String payload = message.getPayload();
        
        if (!session.isOpen()) {
            log.warn("[{}][{}] WebSocket 세션이 닫혀있어 메시지 무시", sessionId, language);
            return;
        }
        
        try {
            // JSON-RPC 메시지 파싱 (로깅용)
            JsonRpcMessage jsonRpcMessage = objectMapper.readValue(payload, JsonRpcMessage.class);
            
            if (jsonRpcMessage.isRequest()) {
                log.info("[{}][{}] → LSP 요청: method={}, id={}", 
                        sessionId, language, jsonRpcMessage.getMethod(), jsonRpcMessage.getId());
            } else if (jsonRpcMessage.isNotification()) {
                log.info("[{}][{}] → LSP Notification: method={}", 
                        sessionId, language, jsonRpcMessage.getMethod());
            }
            
            // LSP로 메시지 전송 (응답은 별도 스레드에서 받음)
            containerManager.sendToLsp(language, sessionId, payload);
            
        } catch (Exception e) {
            log.error("[{}][{}] 메시지 전송 오류: {}", sessionId, language, e.getMessage());
            
            if (session.isOpen()) {
                sendErrorResponse(session, null, e.getMessage());
            }
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        String language = sessionLanguages.remove(sessionId);
        sessionActive.put(sessionId, false);  // 스레드 종료 플래그
        
        log.info("LSP WebSocket 연결 종료: sessionId={}, language={}, status={}", 
                sessionId, language, status);
        
        try {
            containerManager.removeContainer(sessionId);
            log.info("LSP 컨테이너 제거 완료: sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("LSP 컨테이너 제거 실패: sessionId={}", sessionId, e);
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        String language = sessionLanguages.get(sessionId);
        
        log.error("LSP WebSocket 에러: sessionId={}, language={}", sessionId, language, exception);
        
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    
    private String extractLanguageFromPath(String path) {
        return path.substring(1);
    }
    
    private void sendErrorResponse(WebSocketSession session, Object requestId, String errorMessage) {
        try {
            JsonRpcMessage errorResponse = JsonRpcMessage.error(
                requestId,
                -32603,
                errorMessage
            );
            
            String errorJson = objectMapper.writeValueAsString(errorResponse);
            session.sendMessage(new TextMessage(errorJson));
            
        } catch (Exception e) {
            log.error("에러 응답 전송 실패: sessionId={}", session.getId(), e);
        }
    }
}
