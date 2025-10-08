package com.PBL.ai.config;

import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingStores {
    //벡터 DB 관련
    @Value("${langchain4j.chroma.url}")
    private String baseUrl;

    public EmbeddingStore create(String tenantName,
                                 String databaseName,
                                 String collectionName) {
        return ChromaEmbeddingStore.builder()
                .baseUrl(baseUrl)
                .tenantName(tenantName)
                .databaseName(databaseName)
                .collectionName(collectionName)
                .build();
    }
}
