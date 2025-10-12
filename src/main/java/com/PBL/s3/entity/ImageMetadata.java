package com.PBL.s3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 이미지 메타데이터 엔티티
 * S3에 저장된 이미지의 정보를 관리
 */
@Entity
@Table(name = "image_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetadata {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "stored_filename", nullable = false, unique = true)
    private String storedFilename;
    
    @Column(name = "bucket_name", nullable = false)
    private String bucketName;
    
    @Column(name = "object_key", nullable = false)
    private String objectKey;
    
    @Column(name = "content_type", nullable = false)
    private String contentType;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "width")
    private Integer width;
    
    @Column(name = "height")
    private Integer height;
    
    @Column(name = "uploaded_by")
    private Long uploadedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 이미지 URL 생성
     */
    public String getImageUrl(String baseUrl) {
        return baseUrl + "/" + bucketName + "/" + objectKey;
    }
}
