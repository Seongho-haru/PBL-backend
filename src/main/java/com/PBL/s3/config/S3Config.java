package com.PBL.s3.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * S3 (MinIO) 설정 클래스
 */
@Configuration
@Slf4j
public class S3Config {
    
    @Value("${s3.endpoint:http://localhost:9000}")
    private String endpoint;
    
    @Value("${s3.access-key:minioadmin}")
    private String accessKey;
    
    @Value("${s3.secret-key:minioadmin123}")
    private String secretKey;
    
    @Value("${s3.bucket-name:pbl-images}")
    private String bucketName;
    
    @Value("${s3.region:us-east-1}")
    private String region;
    
    @Value("${s3.base-url:http://localhost:9000}")
    private String baseUrl;
    
    @Bean
    public MinioClient minioClient() {
        log.info("MinIO 클라이언트 초기화 - Endpoint: {}, Access Key: {}", endpoint, accessKey);
        
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .region(region)
                .build();
    }
    
    @Bean
    public S3Properties s3Properties() {
        return S3Properties.builder()
                .endpoint(endpoint)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .bucketName(bucketName)
                .region(region)
                .baseUrl(baseUrl)
                .build();
    }
    
    /**
     * S3 설정 프로퍼티 클래스
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class S3Properties {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucketName;
        private String region;
        private String baseUrl;
    }
}
