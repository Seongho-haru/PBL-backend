package com.PBL.ai.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingModelConfig {
    @Value("${langchain4j.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.chat-model.embedding-model-name}")
    private String modelName;

    public EmbeddingModel  getEmbeddingModel () {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
