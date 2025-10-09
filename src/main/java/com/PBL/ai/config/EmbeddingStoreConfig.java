package com.PBL.ai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.store.embedding.chroma.ChromaApiVersion.V2;

@Configuration
public class EmbeddingStoreConfig {
    @Value("${langchain4j.chroma.url}")
    private String baseUrl;

    // 강의 임베딩 스토어
    public EmbeddingStore<TextSegment> createLectureStore() {
        return create("lecture_embeddings");
    }

    // 커리큘럼 임베딩 스토어
    public EmbeddingStore<TextSegment> createCurriculumStore() {
        return create("curriculum_embeddings");
    }

    private EmbeddingStore<TextSegment> create(String collectionName) {
        return ChromaEmbeddingStore.builder()
                .baseUrl(baseUrl)
                .collectionName(collectionName)
                .apiVersion(V2)  // ✅ V2 API 사용!
                .build();
    }
}