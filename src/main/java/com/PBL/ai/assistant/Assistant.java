package com.PBL.ai.assistant;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;

@AiService
public interface Assistant {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);
}
