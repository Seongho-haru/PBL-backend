package com.PBL.s3.service;

import com.PBL.s3.config.S3Config;
import com.PBL.s3.entity.ImageMetadata;
import com.PBL.s3.repository.ImageMetadataRepository;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * S3 (MinIO) 서비스 클래스
 * 이미지 업로드, 다운로드, 삭제 등의 기능을 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    
    private final MinioClient minioClient;
    private final S3Config.S3Properties s3Properties;
    private final ImageMetadataRepository imageMetadataRepository;
    
    /**
     * 버킷 존재 여부 확인 및 생성
     */
    public void ensureBucketExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(s3Properties.getBucketName())
                    .build()
            );
            
            if (!bucketExists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(s3Properties.getBucketName())
                        .region(s3Properties.getRegion())
                        .build()
                );
                log.info("버킷 생성 완료: {}", s3Properties.getBucketName());
            }
            
            // 버킷 정책을 공개 읽기로 설정 (기존 버킷도 포함)
            setBucketPublicReadPolicy();
        } catch (Exception e) {
            log.error("버킷 확인/생성 중 오류 발생", e);
            throw new RuntimeException("버킷 확인/생성 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 버킷을 공개 읽기로 설정
     */
    public void setBucketPublicReadPolicy() {
        try {
            String policy = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": "*",
                            "Action": "s3:GetObject",
                            "Resource": "arn:aws:s3:::%s/*"
                        }
                    ]
                }
                """.formatted(s3Properties.getBucketName());
            
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(s3Properties.getBucketName())
                    .config(policy)
                    .build());
            
            log.info("버킷 정책 설정 완료: {} (공개 읽기)", s3Properties.getBucketName());
        } catch (Exception e) {
            log.warn("버킷 정책 설정 실패 (수동 설정 필요): {}", e.getMessage());
        }
    }
    
    /**
     * 이미지 업로드
     */
    public com.PBL.s3.dto.S3DTOs.ImageUploadResponse uploadImage(MultipartFile file, Long uploadedBy, String folder) {
        try {
            // 버킷 존재 확인
            ensureBucketExists();
            
            // 파일 유효성 검사
            validateImageFile(file);
            
            // 고유한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String storedFilename = generateUniqueFilename(extension);
            String objectKey = (folder != null ? folder + "/" : "") + storedFilename;
            
            // S3에 업로드
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(s3Properties.getBucketName())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            // 메타데이터 저장 (이미지 크기는 나중에 필요시 추가)
            ImageMetadata metadata = ImageMetadata.builder()
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .bucketName(s3Properties.getBucketName())
                    .objectKey(objectKey)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .width(0) // 필요시 나중에 추가
                    .height(0) // 필요시 나중에 추가
                    .uploadedBy(uploadedBy)
                    .build();
            
            ImageMetadata savedMetadata = imageMetadataRepository.save(metadata);
            log.info("이미지 업로드 완료 - ID: {}, 파일명: {}", savedMetadata.getId(), originalFilename);
            
            // ImageUploadResponse 생성
            return com.PBL.s3.dto.S3DTOs.ImageUploadResponse.from(savedMetadata, s3Properties.getBaseUrl());
            
        } catch (Exception e) {
            log.error("이미지 업로드 중 오류 발생", e);
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 이미지 다운로드
     */
    public InputStream downloadImage(String objectKey) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(s3Properties.getBucketName())
                    .object(objectKey)
                    .build()
            );
        } catch (Exception e) {
            log.error("이미지 다운로드 중 오류 발생 - ObjectKey: {}", objectKey, e);
            throw new RuntimeException("이미지 다운로드 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 이미지 삭제
     */
    public void deleteImage(Long imageId) {
        try {
            // 메타데이터 조회
            ImageMetadata metadata = imageMetadataRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다."));
            
            // S3에서 이미지 삭제
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(s3Properties.getBucketName())
                    .object(metadata.getObjectKey())
                    .build()
            );
            
            // 메타데이터 삭제
            imageMetadataRepository.delete(metadata);
            
            log.info("이미지 삭제 완료 - ID: {}, ObjectKey: {}", imageId, metadata.getObjectKey());
            
        } catch (Exception e) {
            log.error("이미지 삭제 중 오류 발생 - ID: {}", imageId, e);
            throw new RuntimeException("이미지 삭제 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 이미지 존재 여부 확인
     */
    public boolean imageExists(String objectKey) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(s3Properties.getBucketName())
                    .object(objectKey)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 이미지 파일 유효성 검사
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        
        // 지원하는 이미지 형식 확인
        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"};
        boolean isAllowed = false;
        for (String type : allowedTypes) {
            if (contentType.equals(type)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (JPEG, PNG, GIF, WebP, BMP만 지원)");
        }
        
        // 파일 크기 제한 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    /**
     * 고유한 파일명 생성
     */
    private String generateUniqueFilename(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + extension;
    }
    
    /**
     * 이미지 통계 조회
     */
    public com.PBL.s3.dto.S3DTOs.ImageStatsResponse getImageStats(Long userId) {
        try {
            long totalImages = imageMetadataRepository.count();
            long totalFileSize = imageMetadataRepository.findAll().stream()
                    .mapToLong(ImageMetadata::getFileSize)
                    .sum();
            long averageFileSize = totalImages > 0 ? totalFileSize / totalImages : 0;
            long imagesByUser = userId != null ? imageMetadataRepository.countByUploadedBy(userId) : 0;
            
            return com.PBL.s3.dto.S3DTOs.ImageStatsResponse.create(
                    totalImages, totalFileSize, averageFileSize, imagesByUser);
                    
        } catch (Exception e) {
            log.error("이미지 통계 조회 중 오류 발생", e);
            throw new RuntimeException("통계 조회 중 오류가 발생했습니다.", e);
        }
    }
}
