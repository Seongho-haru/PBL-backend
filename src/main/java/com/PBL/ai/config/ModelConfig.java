package com.PBL.ai.config;

import dev.langchain4j.http.client.spring.restclient.SpringRestClient;
import dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilder;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ModelConfig {
    // AI 모델 관련
    @Value("${langchain4j.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.chat-model.model-name}")
    private String modelName;

    @Bean
    public ChatModel getChatModel() {
         RestClient.Builder restClientBuilder = RestClient.builder();

         SpringRestClientBuilder springRestClientBuilder = SpringRestClient.builder()
         .restClientBuilder(restClientBuilder);

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .httpClientBuilder(springRestClientBuilder)
                .build();
    }

    @Bean
    public StreamingChatModel getStreamingChatModel() {
        RestClient.Builder restClientBuilder = RestClient.builder();

        SpringRestClientBuilder springRestClientBuilder = SpringRestClient.builder()
                .restClientBuilder(restClientBuilder);

        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .httpClientBuilder(springRestClientBuilder)
                .build();
    }

}
