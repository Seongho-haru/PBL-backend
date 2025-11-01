package com.PBL.ai.config;

import com.PBL.ai.enums.CollectionType;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RAGconfig {

    private final EmbeddingStoreConfig embeddingStoreConfig;
    private final EmbeddingModelConfig embeddingModelConfig;

    @Bean
    public ContentRetriever lectureContentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStoreConfig.create(CollectionType.PYTHON))
                .embeddingModel(embeddingModelConfig.getEmbeddingModel())
                .maxResults(5)      // 상위 5개 결과
                .minScore(0.6)      // 유사도 60% 이상만
                .build();
    }
}
