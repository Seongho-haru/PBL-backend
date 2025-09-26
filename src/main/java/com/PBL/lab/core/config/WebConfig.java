package com.PBL.lab.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Web Configuration
 * 
 * Configures web-related beans and CORS settings.
 * Includes REST template and JSON mapping configuration.
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${judge0.cors.allowed-origins:}")
    private String allowedOrigins;

    @Value("${judge0.cors.disallowed-origins:}")
    private String disallowedOrigins;

    @Value("${judge0.cors.allowed-ips:}")
    private String allowedIps;

    @Value("${judge0.cors.disallowed-ips:}")
    private String disallowedIps;

    @Value("${judge0.web.request-timeout:30}")
    private Integer requestTimeoutSeconds;

    @Value("${judge0.web.connection-timeout:10}")
    private Integer connectionTimeoutSeconds;

    /**
     * Configure CORS settings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS settings");

        var corsRegistration = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        // Configure allowed origins
        List<String> allowedOriginsList = parseCommaSeparatedList(allowedOrigins);
        if (allowedOriginsList.isEmpty()) {
            // If no specific origins configured, allow all
            corsRegistration.allowedOriginPatterns("*");
            log.info("CORS: Allowing all origins");
        } else {
            corsRegistration.allowedOrigins(allowedOriginsList.toArray(new String[0]));
            log.info("CORS: Allowing specific origins: {}", allowedOriginsList);
        }

        // Log disallowed origins (these would need custom filter implementation)
        List<String> disallowedOriginsList = parseCommaSeparatedList(disallowedOrigins);
        if (!disallowedOriginsList.isEmpty()) {
            log.warn("CORS: Disallowed origins configured but not implemented: {}", disallowedOriginsList);
        }

        log.info("CORS configuration completed");
    }

    /**
     * Configure REST template for external HTTP calls
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(connectionTimeoutSeconds))
                .setReadTimeout(Duration.ofSeconds(requestTimeoutSeconds))
                .build();
    }

    /**
     * Configure ObjectMapper for JSON serialization
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure for ISO 8601 datetime format
        mapper.findAndRegisterModules();
        
        // Configure to handle missing properties gracefully
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Configure to include non-null values only
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        
        return mapper;
    }

    /**
     * Parse comma-separated list
     */
    private List<String> parseCommaSeparatedList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return List.of();
        }
        
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Web configuration properties
     */
    @Bean
    public WebProperties webProperties() {
        return WebProperties.builder()
                .allowedOrigins(parseCommaSeparatedList(allowedOrigins))
                .disallowedOrigins(parseCommaSeparatedList(disallowedOrigins))
                .allowedIps(parseCommaSeparatedList(allowedIps))
                .disallowedIps(parseCommaSeparatedList(disallowedIps))
                .requestTimeoutSeconds(requestTimeoutSeconds)
                .connectionTimeoutSeconds(connectionTimeoutSeconds)
                .build();
    }

    /**
     * Web properties data class
     */
    @lombok.Data
    @lombok.Builder
    public static class WebProperties {
        private List<String> allowedOrigins;
        private List<String> disallowedOrigins;
        private List<String> allowedIps;
        private List<String> disallowedIps;
        private Integer requestTimeoutSeconds;
        private Integer connectionTimeoutSeconds;
    }
}
