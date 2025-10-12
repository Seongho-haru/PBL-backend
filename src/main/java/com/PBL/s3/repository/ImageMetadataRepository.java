package com.PBL.s3.repository;

import com.PBL.s3.entity.ImageMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 이미지 메타데이터 리포지토리
 */
@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Long> {
    
    /**
     * 저장된 파일명으로 이미지 메타데이터 조회
     */
    Optional<ImageMetadata> findByStoredFilename(String storedFilename);
    
    /**
     * 오브젝트 키로 이미지 메타데이터 조회
     */
    Optional<ImageMetadata> findByObjectKey(String objectKey);
    
    /**
     * 업로더별 이미지 목록 조회
     */
    Page<ImageMetadata> findByUploadedBy(Long uploadedBy, Pageable pageable);
    
    
    /**
     * 컨텐츠 타입별 이미지 목록 조회
     */
    Page<ImageMetadata> findByContentType(String contentType, Pageable pageable);
    
    /**
     * 파일 크기 범위로 이미지 목록 조회
     */
    @Query("SELECT i FROM ImageMetadata i WHERE i.fileSize BETWEEN :minSize AND :maxSize")
    Page<ImageMetadata> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize, Pageable pageable);
    
    /**
     * 이미지 검색 (파일명)
     */
    @Query("SELECT i FROM ImageMetadata i WHERE " +
           "LOWER(i.originalFilename) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ImageMetadata> searchImages(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 사용자별 이미지 검색
     */
    @Query("SELECT i FROM ImageMetadata i WHERE " +
           "i.uploadedBy = :uploadedBy AND " +
           "LOWER(i.originalFilename) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ImageMetadata> searchUserImages(@Param("uploadedBy") Long uploadedBy, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 업로더별 이미지 개수 조회
     */
    long countByUploadedBy(Long uploadedBy);
    
    
    /**
     * 컨텐츠 타입별 이미지 개수 조회
     */
    long countByContentType(String contentType);
}
