package com.PBL.s3.controller;

import com.PBL.s3.dto.S3DTOs;
import com.PBL.s3.entity.ImageMetadata;
import com.PBL.s3.repository.ImageMetadataRepository;
import com.PBL.s3.service.S3Service;
import com.PBL.s3.config.S3Config;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

/**
 * S3 이미지 관리 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "S3 Image Management", description = "이미지 업로드, 삭제, 통계 API")
public class S3Controller {
    
    private final S3Service s3Service;
    private final ImageMetadataRepository imageMetadataRepository;
    private final S3Config.S3Properties s3Properties;
    
    // === 이미지 업로드 ===
    
    /**
     * 이미지 업로드
     */
    @PostMapping("/upload")
    @Operation(summary = "이미지 업로드", description = "이미지 파일을 업로드하고 메타데이터를 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "이미지 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 형식, 크기 등)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            log.info("이미지 업로드 요청 - 파일명: {}, 사용자: {}", file.getOriginalFilename(), userId);
            
            S3DTOs.ImageUploadResponse response = s3Service.uploadImage(file, userId, folder);
            
            log.info("이미지 업로드 완료 - ID: {}, 파일명: {}", response.getId(), response.getStoredFilename());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("이미지 업로드 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("이미지 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드 중 오류가 발생했습니다."));
        }
    }
    
    // === 이미지 삭제 ===
    
    /**
     * 이미지 삭제
     */
    @DeleteMapping("/{imageId}")
    @Operation(summary = "이미지 삭제", description = "이미지를 삭제합니다. 본인이 업로드한 이미지만 삭제 가능합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이미지 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "본인의 이미지가 아님"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> deleteImage(
            @PathVariable("imageId") Long imageId,
            @RequestHeader("X-User-Id") Long userId) {
        
        try {
            log.info("이미지 삭제 요청 - ID: {}, 사용자: {}", imageId, userId);
            
            // 이미지 존재 여부 및 권한 확인
            Optional<ImageMetadata> imageOpt = imageMetadataRepository.findById(imageId);
            if (imageOpt.isEmpty()) {
                log.warn("이미지를 찾을 수 없음 - ID: {}", imageId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "이미지를 찾을 수 없습니다."));
            }
            
            ImageMetadata image = imageOpt.get();
            if (!image.getUploadedBy().equals(userId)) {
                log.warn("이미지 삭제 권한 없음 - ID: {}, 요청자: {}, 업로더: {}", 
                        imageId, userId, image.getUploadedBy());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "본인이 업로드한 이미지만 삭제할 수 있습니다."));
            }
            
            // 이미지 삭제
            s3Service.deleteImage(imageId);
            
            log.info("이미지 삭제 완료 - ID: {}", imageId);
            return ResponseEntity.ok(Map.of("message", "이미지가 성공적으로 삭제되었습니다."));
            
        } catch (Exception e) {
            log.error("이미지 삭제 중 오류 발생 - ID: {}", imageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 삭제 중 오류가 발생했습니다."));
        }
    }
    
    // === 통계 조회 ===
    
    /**
     * 이미지 통계 조회
     */
    @GetMapping("/stats")
    @Operation(summary = "이미지 통계 조회", description = "이미지 관련 통계 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "통계 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getImageStats(
            @RequestParam(value = "userId", required = false) Long userId) {
        
        try {
            log.info("이미지 통계 조회 요청 - 사용자: {}", userId);
            
            S3DTOs.ImageStatsResponse stats = s3Service.getImageStats(userId);
            
            log.info("이미지 통계 조회 완료 - 총 이미지: {}", stats.getTotalImages());
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("이미지 통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "통계 조회 중 오류가 발생했습니다."));
        }
    }
    
    // === 관리자 기능 ===
    
    /**
     * 버킷 정책 설정 (관리자)
     */
    @PostMapping("/admin/set-public-policy")
    @Operation(summary = "버킷 정책 설정", description = "버킷을 공개 읽기로 설정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "버킷 정책 설정 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> setBucketPublicPolicy() {
        
        try {
            log.info("버킷 정책 설정 요청");
            
            s3Service.setBucketPublicReadPolicy();
            
            log.info("버킷 정책 설정 완료");
            return ResponseEntity.ok(Map.of("message", "버킷 정책이 공개 읽기로 설정되었습니다."));
            
        } catch (Exception e) {
            log.error("버킷 정책 설정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "버킷 정책 설정 중 오류가 발생했습니다."));
        }
    }
}