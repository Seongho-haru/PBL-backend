package com.PBL.ai.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingModels {
    @Value("${langchain4j.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.chat-model.model-name}")
    private String modelName;

    public EmbeddingModel  embeddingModel () {
        EmbeddingModel model = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
        return model;
    }
}
