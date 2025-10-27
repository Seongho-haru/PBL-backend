package com.PBL.curriculum.dto;

import com.PBL.curriculum.entity.CourseReview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 리뷰/문의 DTOs
 */
public class CourseReviewDTOs {

    /**
     * 평균 평점 응답
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AverageRatingResponse {
        private Long curriculumId;
        private double averageRating;
        private Long reviewCount;
    }

    /**
     * 리뷰/문의 작성 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCourseReviewRequest {
        private Boolean isReview; // true: 리뷰, false: 문의
        private BigDecimal rating; // 리뷰만 필요, null 가능
        private String content; // 리뷰/문의 내용
        private Boolean isPublic; // 문의만 사용 (리뷰는 항상 공개)
    }

    /**
     * 리뷰/문의 수정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCourseReviewRequest {
        private BigDecimal rating; // 리뷰만
        private String content; // 리뷰/문의 내용
        private Boolean isPublic; // 문의만
    }

    /**
     * 리뷰/문의 응답
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseReviewResponse {
        private Long id;
        private Long curriculumId;
        private String curriculumTitle;
        private Long authorId;
        private String authorUsername;
        private Boolean isReview;
        private BigDecimal rating;
        private String content;
        private Boolean isPublic;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static CourseReviewResponse from(CourseReview review) {
            CourseReviewResponse response = new CourseReviewResponse();
            response.setId(review.getId());
            response.setCurriculumId(review.getCurriculum().getId());
            response.setCurriculumTitle(review.getCurriculum().getTitle());
            response.setAuthorId(review.getAuthor().getId());
            response.setAuthorUsername(review.getAuthor().getUsername());
            response.setIsReview(review.getIsReview());
            response.setRating(review.getRating());
            response.setContent(review.getContent());
            response.setIsPublic(review.getIsPublic());
            response.setCreatedAt(review.getCreatedAt());
            response.setUpdatedAt(review.getUpdatedAt());
            return response;
        }
    }
}

