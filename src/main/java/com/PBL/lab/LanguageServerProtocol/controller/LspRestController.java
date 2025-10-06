package com.PBL.lab.LanguageServerProtocol.controller;

import com.PBL.lab.LanguageServerProtocol.service.LspContainerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * LSP 관리용 REST API Controller
 * 
 * WebSocket과 별개로 LSP 컨테이너 상태 확인 및 관리
 */
@RestController
@RequestMapping("/api/lsp")
@RequiredArgsConstructor
@Slf4j
public class LspRestController {
    
    private final LspContainerManager containerPool;
    
    /**
     * LSP 컨테이너 생성
     * 
     * POST /api/lsp/containers/{language}?sessionId=xxx
     */
    @PostMapping("/containers/{language}")
    public ResponseEntity<?> createContainer(
        @PathVariable String language,
        @RequestParam String sessionId
    ) {
        try {
            log.debug("LSP 컨테이너 생성 요청 - language: {}, sessionId: {}", language, sessionId);
            
            LspContainerManager.LspContainer container = containerPool.createContainer(language, sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("containerId", container.getContainerId());
            response.put("containerName", container.getContainerName());
            response.put("language", container.getLanguage());
            response.put("sessionId", container.getSessionId());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("지원하지 않는 언어: {}", language);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("컨테이너 생성 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * LSP 컨테이너 삭제
     * 
     * DELETE /api/lsp/containers?sessionId=xxx
     */
    @DeleteMapping("/containers")
    public ResponseEntity<?> removeContainer(@RequestParam String sessionId) {
        try {
            log.debug("LSP 컨테이너 삭제 요청 - sessionId: {}", sessionId);
            
            containerPool.removeContainer(sessionId);
            
            return ResponseEntity.ok(Map.of("success", true));
            
        } catch (Exception e) {
            log.error("컨테이너 삭제 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * LSP 컨테이너 조회
     * 
     * GET /api/lsp/containers?sessionId=xxx
     */
    @GetMapping("/containers")
    public ResponseEntity<?> getContainer(@RequestParam String sessionId) {
        try {
            LspContainerManager.LspContainer container = containerPool.getContainer(sessionId);
            
            if (container == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("containerId", container.getContainerId());
            response.put("containerName", container.getContainerName());
            response.put("language", container.getLanguage());
            response.put("sessionId", container.getSessionId());
            response.put("createdAt", container.getCreatedAt());
            response.put("running", container.isRunning());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("컨테이너 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * 모든 활성 컨테이너 조회
     * 
     * GET /api/lsp/containers/all
     */
    @GetMapping("/containers/all")
    public ResponseEntity<?> getAllContainers() {
        try {
            return ResponseEntity.ok(containerPool.getAllContainers());
        } catch (Exception e) {
            log.error("컨테이너 목록 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * 통계 조회
     * 
     * GET /api/lsp/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        try {
            return ResponseEntity.ok(containerPool.getStatistics());
        } catch (Exception e) {
            log.error("통계 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * 지원하는 언어 목록
     * 
     * GET /api/lsp/languages
     */
    @GetMapping("/languages")
    public ResponseEntity<?> getSupportedLanguages() {
        try {
            return ResponseEntity.ok(Map.of(
                "languages", containerPool.getSupportedLanguages()
            ));
        } catch (Exception e) {
            log.error("언어 목록 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}
