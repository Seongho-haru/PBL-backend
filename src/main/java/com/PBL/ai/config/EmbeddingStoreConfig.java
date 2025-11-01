package com.PBL.ai.config;

import com.PBL.ai.enums.CollectionType;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingStoreConfig {
    @Value("${langchain4j.qdrant.host}")
    private String host;

    @Value("${langchain4j.qdrant.port}")
    private int port;

    public EmbeddingStore<TextSegment> create(CollectionType collectionName) {
        return QdrantEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName(collectionName.name())
                .build();
    }
}