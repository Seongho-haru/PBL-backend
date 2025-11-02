package com.PBL.ai.controller;

import com.PBL.ai.dto.GradingRequest;
import com.PBL.ai.dto.StreamResponse;
import com.PBL.ai.service.AssistantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
class AssistantController {
    private final AssistantService assistantService;
    private final ObjectMapper objectMapper;

    //1. 코드 실행/제출 에대한 해설
    //2. 커리큘럼 생성 또는 조합

//    @PostMapping
//    public Flux<String> chat(@RequestBody Chat requset) {
//    }

    /**
     * 코딩테스트 해설 보기 (JSON 스트리밍 방식)
     * @param request 제출 토큰 및 문제 ID
     * @return Flux<String> - SSE로 JSON 문자열 스트리밍
     */
    @PostMapping(value = "/chat/grading", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> gradingStream(@RequestBody GradingRequest request) {
        try {


            // 3. AI 분석 및 해설 생성 (스트리밍) - 각 청크를 JSON 문자열로 변환
            return assistantService.analyAndExplainStream(request)
                    .map(content -> {
                        try {
                            // 텍스트를 JSON DTO로 변환 후 문자열로 직렬화
                            StreamResponse response = StreamResponse.content(content);
                            return objectMapper.writeValueAsString(response);
                        } catch (JsonProcessingException e) {
                            log.error("JSON 직렬화 실패", e);
                            return "{\"type\":\"error\",\"error_message\":\"JSON 변환 실패\"}";
                        }
                    })
                    .concatWith(Flux.defer(() -> {
                        try {
                            // 마지막에 완료 신호
                            return Flux.just(objectMapper.writeValueAsString(StreamResponse.complete()));
                        } catch (JsonProcessingException e) {
                            return Flux.just("{\"type\":\"complete\"}");
                        }
                    }))
                    .doOnNext(json -> log.debug("JSON 응답 전송: {}", json))
                    .doOnComplete(() -> log.info("스트리밍 완료"))
                    .doOnError(error -> log.error("스트리밍 에러", error))
                    .onErrorResume(error -> {
                        try {
                            return Flux.just(objectMapper.writeValueAsString(StreamResponse.error(error.getMessage())));
                        } catch (JsonProcessingException e) {
                            return Flux.just("{\"type\":\"error\",\"error_message\":\"" + error.getMessage() + "\"}");
                        }
                    });
        } catch (Exception e) {
            log.error("요청 처리 실패", e);
            try {
                return Flux.just(objectMapper.writeValueAsString(StreamResponse.error(e.getMessage())));
            } catch (JsonProcessingException ex) {
                return Flux.just("{\"type\":\"error\",\"error_message\":\"" + e.getMessage() + "\"}");
            }
        }
    }


}
