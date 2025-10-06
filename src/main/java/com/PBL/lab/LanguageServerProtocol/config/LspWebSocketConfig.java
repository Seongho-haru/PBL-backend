package com.PBL.lab.LanguageServerProtocol.config;

import com.PBL.lab.LanguageServerProtocol.controller.LspWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * LSP WebSocket 설정 - Raw WebSocket 방식
 * 
 * 언어별 독립적인 WebSocket 엔드포인트:
 * - ws://localhost:2358/python38
 * - ws://localhost:2358/python311
 * - ws://localhost:2358/javascript
 * - ws://localhost:2358/typescript
 * - ws://localhost:2358/java
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class LspWebSocketConfig implements WebSocketConfigurer {
    
    private final LspWebSocketHandler lspWebSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Python 38
        registry.addHandler(lspWebSocketHandler, "/python")
            .setAllowedOrigins("*");
        
        // JavaScript
        registry.addHandler(lspWebSocketHandler, "/javascript")
            .setAllowedOrigins("*");
        
        // TypeScript
        registry.addHandler(lspWebSocketHandler, "/typescript")
            .setAllowedOrigins("*");
        
        // Java
        registry.addHandler(lspWebSocketHandler, "/java")
            .setAllowedOrigins("*");
    }
}
