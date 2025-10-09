package com.PBL.ai.service;

import com.PBL.ai.config.Assisdent;
import com.PBL.ai.config.RAGconfig;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AssistantService {
    @Autowired
    private final StreamingChatModel streamingChatModel; // 스트리밍용 모델

    private final ToolService toolService;
    private final RAGconfig ragconfig;
    private Assisdent assisdent;

    @PostConstruct
    public void init(){
        this.assisdent = AiServices.builder(Assisdent.class)
                .streamingChatModel(streamingChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(ragconfig.lectureContentRetriever())
                .tools(toolService)
                .build();
    }

    /**
     * 스트리밍 방식으로 분석 및 해설 제공
     * @param submission 제출 정보
     * @param problem 문제 정보
     * @return Flux<String> 스트림
     */
    public Flux<String> analyAndExplainStream(Object submission, Object problem) {
        return assisdent.chat(submission.toString(), problem.toString());
    }

}
