package com.PBL.ai.config;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

@AiService
public interface Assisdent {
    @SystemMessage("""
            당신은 코딩 문제 해설 전문가입니다.

            학생의 코드를 분석하고 상세한 해설을 제공하세요.
            - 코드의 정확성과 테스트 결과 분석
            - 코드 품질 및 모범 사례 제안
            - 개선 방안 제시
            - 필요시 학습 리소스 추천
            """)
    @UserMessage("제출상세 내용 : {{submission}} , 문제 상세 내용 : {{problem}}")
    Flux<String> chat(@V("submission") String submission, @V("problem") String problem);
}
