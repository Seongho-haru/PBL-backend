package com.PBL.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 스트리밍 응답 DTO
 * 프론트엔드에서 구조화된 JSON 형태로 받을 수 있도록 함
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamResponse {
    
    /**
     * 응답 타입
     * - content: 일반 컨텐츠
     * - error: 에러 발생
     * - complete: 스트림 완료
     */
    @JsonProperty("type")
    private String type;
    
    /**
     * 실제 컨텐츠 (AI 응답 텍스트)
     */
    @JsonProperty("content")
    private String content;
    
    /**
     * 에러 메시지 (type이 error일 때만)
     */
    @JsonProperty("error_message")
    private String errorMessage;
    
    /**
     * 타임스탬프
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    /**
     * 메타데이터 (선택적)
     */
    @JsonProperty("metadata")
    private Object metadata;
    
    // === 편의 메서드 ===
    
    /**
     * 일반 컨텐츠 응답 생성
     */
    public static StreamResponse content(String content) {
        return StreamResponse.builder()
                .type("content")
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 에러 응답 생성
     */
    public static StreamResponse error(String errorMessage) {
        return StreamResponse.builder()
                .type("error")
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 완료 응답 생성
     */
    public static StreamResponse complete() {
        return StreamResponse.builder()
                .type("complete")
                .timestamp(LocalDateTime.now())
                .build();
    }
}


