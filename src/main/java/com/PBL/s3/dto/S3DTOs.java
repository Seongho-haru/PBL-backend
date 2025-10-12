package com.PBL.s3.dto;

import com.PBL.s3.entity.ImageMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * S3 관련 DTO 클래스들
 */
public class S3DTOs {
    
    /**
     * 이미지 업로드 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageUploadRequest {
        private String folder; // 업로드할 폴더 (선택사항)
    }
    
    /**
     * 이미지 업로드 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageUploadResponse {
        private Long id;
        private String originalFilename;
        private String storedFilename;
        private String imageUrl;
        private String contentType;
        private Long fileSize;
        private Integer width;
        private Integer height;
        private LocalDateTime uploadedAt;
        
        public static ImageUploadResponse from(ImageMetadata metadata, String baseUrl) {
            return ImageUploadResponse.builder()
                    .id(metadata.getId())
                    .originalFilename(metadata.getOriginalFilename())
                    .storedFilename(metadata.getStoredFilename())
                    .imageUrl(metadata.getImageUrl(baseUrl))
                    .contentType(metadata.getContentType())
                    .fileSize(metadata.getFileSize())
                    .width(metadata.getWidth())
                    .height(metadata.getHeight())
                    .uploadedAt(metadata.getCreatedAt())
                    .build();
        }
    }
    
    
    /**
     * 이미지 통계 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageStatsResponse {
        private Long totalImages;
        private Long totalFileSize;
        private Long averageFileSize;
        private Long imagesByUser;
        
        public static ImageStatsResponse create(Long totalImages, Long totalFileSize, Long averageFileSize, Long imagesByUser) {
            return ImageStatsResponse.builder()
                    .totalImages(totalImages)
                    .totalFileSize(totalFileSize)
                    .averageFileSize(averageFileSize)
                    .imagesByUser(imagesByUser)
                    .build();
        }
    }
}
