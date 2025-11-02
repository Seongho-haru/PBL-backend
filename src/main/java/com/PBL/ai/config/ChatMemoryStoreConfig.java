package com.PBL.ai.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Value;

public class ChatMemoryStoreConfig {

    @Value("${langchain4j.redis.host}")
    private String host;

    @Value("${langchain4j.redis.port}")
    private Integer port;

    public ChatMemoryStore getChatMemoryStore () {
        return RedisChatMemoryStore.builder()
                .port(port)
                .host(host)
                .build();
    }
}
