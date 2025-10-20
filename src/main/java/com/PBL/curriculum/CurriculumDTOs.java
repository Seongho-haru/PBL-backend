package com.PBL.curriculum;

import com.PBL.lecture.entity.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 커리큘럼 관련 DTO 클래스들
 * 순환 참조 방지 및 API 응답 최적화
 */
public class CurriculumDTOs {

    /**
     * 커리큘럼 목록 조회용 DTO (간단한 정보)
     */
    @Getter
    @Setter
    @Schema(description = "커리큘럼 기본 정보")
    public static class CurriculumResponse {
        @Schema(description = "커리큘럼 ID")
        private Long id;
        
        @Schema(description = "커리큘럼 제목")
        private String title;
        
        @Schema(description = "커리큘럼 설명")
        private String description;
        
        @Schema(description = "공개 여부")
        private Boolean isPublic;
        
        @Schema(description = "총 강의 수")
        private Integer totalLectureCount;

        private String category;
        
        @Schema(description = "필수 강의 수")
        private Integer requiredLectureCount;
        
        @Schema(description = "선택 강의 수")
        private Integer optionalLectureCount;
        
        @Schema(description = "생성 시간")
        private LocalDateTime createdAt;
        
        @Schema(description = "수정 시간")
        private LocalDateTime updatedAt;

        @Schema(description = "작성자 정보")
        private AuthorInfo author;

        @Schema(description = "커리큘럼 난이도")
        private String difficulty;

        @Schema(description = "커리큘럼 간단 소개")
        private String summary;

        @Schema(description = "평균 별점")
        private BigDecimal averageRating;

        @Schema(description = "수강생 수")
        private Integer studentCount;

        private List<String> tags;
        private String thumbnailImageUrl;
        private Integer durationMinutes;

        // 생성자
        public CurriculumResponse() {}

        public CurriculumResponse(Curriculum curriculum) {
            this.id = curriculum.getId();
            this.title = curriculum.getTitle();
            this.description = curriculum.getDescription();
            this.isPublic = curriculum.getIsPublic();
            this.difficulty = curriculum.getDifficulty();
            this.summary = curriculum.getSummary();
            this.averageRating = curriculum.getAverageRating();
            this.studentCount = curriculum.getStudentCount();
            this.createdAt = curriculum.getCreatedAt();
            this.updatedAt = curriculum.getUpdatedAt();

            this.tags = curriculum.getTags() != null ? curriculum.getTags() : new ArrayList<>();
            this.thumbnailImageUrl = curriculum.getThumbnailImageUrl();
            this.durationMinutes = curriculum.getDurationMinutes();
            this.category = curriculum.getCategory();
            
            // 작성자 정보 설정
            try {
                if (curriculum.getAuthor() != null) {
                    this.author = new AuthorInfo();
                    this.author.setId(curriculum.getAuthor().getId());
                    this.author.setUsername(curriculum.getAuthor().getUsername());
                    this.author.setLoginId(curriculum.getAuthor().getLoginId());
                }
            } catch (Exception e) {
                // Lazy Loading 실패 시 null로 설정
                this.author = null;
            }
            
            // Lazy Loading 안전 처리
            try {
                if (curriculum.getLectures() != null) {
                    this.totalLectureCount = curriculum.getLectures().size();
                    this.requiredLectureCount = (int) curriculum.getLectures().stream()
                            .filter(cl -> Boolean.TRUE.equals(cl.getIsRequired()))
                            .count();
                    this.optionalLectureCount = this.totalLectureCount - this.requiredLectureCount;
                } else {
                    this.totalLectureCount = 0;
                    this.requiredLectureCount = 0;
                    this.optionalLectureCount = 0;
                }
            } catch (Exception e) {
                // Lazy Loading 실패 시 기본값
                this.totalLectureCount = 0;
                this.requiredLectureCount = 0;
                this.optionalLectureCount = 0;
            }
        }


    }

    /**
     * 커리큘럼 상세 조회용 DTO (강의 목록 포함)
     */
    @Getter
    @Setter
    @Schema(description = "커리큘럼 상세 정보")
    public static class CurriculumDetailResponse {
        @Schema(description = "커리큘럼 ID")
        private Long id;
        
        @Schema(description = "커리큘럼 제목")
        private String title;
        
        @Schema(description = "커리큘럼 설명")
        private String description;
        
        @Schema(description = "공개 여부")
        private Boolean isPublic;
        
        @Schema(description = "포함된 강의 목록")
        private List<CurriculumLectureResponse> lectures;
        
        @Schema(description = "총 강의 수")
        private Integer totalLectureCount;
        
        @Schema(description = "필수 강의 수")
        private Integer requiredLectureCount;
        
        @Schema(description = "선택 강의 수")
        private Integer optionalLectureCount;
        
        @Schema(description = "생성 시간")
        private LocalDateTime createdAt;
        
        @Schema(description = "수정 시간")
        private LocalDateTime updatedAt;

        @Schema(description = "작성자 정보")
        private AuthorInfo author;

        @Schema(description = "커리큘럼 난이도")
        private String difficulty;

        @Schema(description = "커리큘럼 간단 소개")
        private String summary;

        @Schema(description = "평균 별점")
        private BigDecimal averageRating;

        @Schema(description = "수강생 수")
        private Integer studentCount;

        @Schema(description = "태그 목록")
        private List<String> tags;

        @Schema(description = "썸네일 이미지 URL")
        private String thumbnailImageUrl;

        @Schema(description = "소요 시간 (분)")
        private Integer durationMinutes;
        private String category;

        // 생성자
        public CurriculumDetailResponse() {}

        public CurriculumDetailResponse(Curriculum curriculum) {
            this.id = curriculum.getId();
            this.title = curriculum.getTitle();
            this.description = curriculum.getDescription();
            this.isPublic = curriculum.getIsPublic();
            this.difficulty = curriculum.getDifficulty();
            this.summary = curriculum.getSummary();
            this.averageRating = curriculum.getAverageRating();
            this.studentCount = curriculum.getStudentCount();
            this.lectures = curriculum.getLectures().stream()
                    .map(CurriculumLectureResponse::new)
                    .collect(Collectors.toList());
            this.totalLectureCount = curriculum.getTotalLectureCount();
            this.requiredLectureCount = curriculum.getRequiredLectureCount();
            this.optionalLectureCount = curriculum.getOptionalLectureCount();
            this.createdAt = curriculum.getCreatedAt();
            this.updatedAt = curriculum.getUpdatedAt();

            // 새로 추가된 필드들
            this.tags = curriculum.getTags() != null ? curriculum.getTags() : new ArrayList<>();
            this.thumbnailImageUrl = curriculum.getThumbnailImageUrl();
            this.durationMinutes = curriculum.getDurationMinutes();
            this.category = curriculum.getCategory();

            // 작성자 정보 설정
            try {
                if (curriculum.getAuthor() != null) {
                    this.author = new AuthorInfo();
                    this.author.setId(curriculum.getAuthor().getId());
                    this.author.setUsername(curriculum.getAuthor().getUsername());
                    this.author.setLoginId(curriculum.getAuthor().getLoginId());
                }
            } catch (Exception e) {
                // Lazy Loading 실패 시 null로 설정
                this.author = null;
            }
        }


    }

    /**
     * 커리큘럼 내 강의 정보 DTO
     */
    @Getter
    @Setter
    @Schema(description = "커리큘럼 내 강의 정보")
    public static class CurriculumLectureResponse {
        @Schema(description = "연결 ID")
        private Long id;
        
        @Schema(description = "강의 ID")
        private Long lectureId;
        
        @Schema(description = "강의 제목")
        private String lectureTitle;
        
        @Schema(description = "강의 설명")
        private String lectureDescription;
        
        @Schema(description = "강의 유형")
        private String lectureType;
        
        @Schema(description = "강의 카테고리")
        private String lectureCategory;
        
        @Schema(description = "강의 난이도")
        private String lectureDifficulty;
        
        @Schema(description = "커리큘럼 내 순서")
        private Integer orderIndex;
        
        @Schema(description = "필수 강의 여부")
        private Boolean isRequired;
        
        @Schema(description = "원본 강의 작성자")
        private String originalAuthor;
        
        @Schema(description = "원본 강의 출처 정보")
        private String sourceInfo;
        
        @Schema(description = "연결 생성 시간")
        private LocalDateTime createdAt;

        // 생성자
        public CurriculumLectureResponse() {}

        public CurriculumLectureResponse(CurriculumLecture curriculumLecture) {
            this.id = curriculumLecture.getId();
            this.lectureId = curriculumLecture.getLectureId();
            this.orderIndex = curriculumLecture.getOrderIndex();
            this.isRequired = curriculumLecture.getIsRequired();
            this.originalAuthor = curriculumLecture.getOriginalAuthor();
            this.sourceInfo = curriculumLecture.getSourceInfo();
            this.createdAt = curriculumLecture.getCreatedAt();
            
            // 강의 정보는 별도로 조회하거나 Lazy Loading 처리
            Lecture lecture = curriculumLecture.getLecture();
            if (lecture != null) {
                this.lectureTitle = lecture.getTitle();
                this.lectureDescription = lecture.getDescription();
                this.lectureType = lecture.getType() != null ? lecture.getType().toString() : null;
                this.lectureCategory = lecture.getCategory();
                this.lectureDifficulty = lecture.getDifficulty();
            }
        }


    }

    /**
     * 커리큘럼 생성 요청 DTO
     */
    @Getter
    @Setter
    @Schema(description = "커리큘럼 생성 요청")
    public static class CreateCurriculumRequest {
        @Schema(description = "커리큘럼 제목", required = true)
        private String title;
        
        @Schema(description = "커리큘럼 설명")
        private String description;
        
        @Schema(description = "공개 여부")
        private boolean isPublic = false;

        @Schema(description = "작성자 ID")
        private Long authorId;

        @Schema(description = "커리큘럼 난이도")
        private String difficulty;

        @Schema(description = "커리큘럼 간단 소개")
        private String summary;

        private List<String> tags;
        private String thumbnailImageUrl;
        private Integer durationMinutes;
        private String category;

    }

    /**
     * 커리큘럼 수정 요청 DTO
     */
    @Getter
    @Setter
    @Schema(description = "커리큘럼 수정 요청")
    public static class UpdateCurriculumRequest {
        @Schema(description = "커리큘럼 제목")
        private String title;

        @Schema(description = "커리큘럼 설명")
        private String description;

        @Schema(description = "공개 여부")
        private Boolean isPublic;

        @Schema(description = "커리큘럼 난이도")
        private String difficulty;

        @Schema(description = "커리큘럼 간단 소개")
        private String summary;

        @Schema(description = "태그 목록")
        private List<String> tags;

        @Schema(description = "썸네일 이미지 URL")
        private String thumbnailImageUrl;

        @Schema(description = "소요 시간 (분)")
        private Integer durationMinutes;

        private String category;

    }

    /**
     * 강의 추가 요청 DTO
     */
    @Getter
    @Setter
    @Schema(description = "커리큘럼에 강의 추가 요청")
    public static class AddLectureRequest {
        @Schema(description = "강의 ID", required = true)
        private Long lectureId;
        
        @Schema(description = "필수 강의 여부")
        private boolean isRequired = true;
        
        @Schema(description = "원본 강의 작성자")
        private String originalAuthor;
        
        @Schema(description = "원본 강의 출처 정보")
        private String sourceInfo;

    }

    @Data
    @Builder
    public static class CurriculumNextLecture {
        //커리큘럼
        private Long curriculumId;
        private Long currentLectureId;
        private Long nextLectureId;
        private Long preLectureId;

    }

    /**
     * 강의 순서 변경 요청 DTO
     */
    @Getter
    @Setter
    @Schema(description = "강의 순서 변경 요청")
    public static class ReorderLecturesRequest {
        @Schema(description = "강의 ID 목록 (순서대로)", required = true)
        private List<Long> lectureIds;

    }

    /**
     * 작성자 정보 DTO
     */
    @Getter
    @Setter
    @Schema(description = "작성자 정보")
    public static class AuthorInfo {
        @Schema(description = "사용자 ID")
        private Long id;
        
        @Schema(description = "사용자명")
        private String username;
        
        @Schema(description = "로그인 ID")
        private String loginId;

    }
}