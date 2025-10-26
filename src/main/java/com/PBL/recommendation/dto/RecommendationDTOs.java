package com.PBL.recommendation.dto;

import com.PBL.curriculum.Curriculum;
import com.PBL.lecture.entity.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 추천 시스템 DTOs
 */
public class RecommendationDTOs {

    /**
     * 커리큘럼 추천 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurriculumRecommendationResponse {
        private Long curriculumId;
        private String title;
        private String summary;
        private String difficulty;
        private String category;
        private List<String> tags;
        private BigDecimal averageRating;
        private Integer studentCount;
        private String authorName;
        private String thumbnailImageUrl;
        private BigDecimal recommendationScore; // 추천 점수
        private String recommendationReason; // 추천 이유

        public static CurriculumRecommendationResponse from(Curriculum curriculum, BigDecimal score, String reason) {
            return CurriculumRecommendationResponse.builder()
                    .curriculumId(curriculum.getId())
                    .title(curriculum.getTitle())
                    .summary(curriculum.getSummary())
                    .difficulty(curriculum.getDifficulty())
                    .category(curriculum.getCategory())
                    .tags(curriculum.getTags())
                    .averageRating(curriculum.getAverageRating())
                    .studentCount(curriculum.getStudentCount())
                    .authorName(curriculum.getAuthor() != null ? curriculum.getAuthor().getUsername() : null)
                    .thumbnailImageUrl(curriculum.getThumbnailImageUrl())
                    .recommendationScore(score)
                    .recommendationReason(reason)
                    .build();
        }
    }

    /**
     * 강의 추천 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LectureRecommendationResponse {
        private Long lectureId;
        private String title;
        private String description;
        private String type;
        private String category;
        private String difficulty;
        private BigDecimal recommendationScore;
        private String recommendationReason;
        private Long curriculumId; // 포함된 커리큘럼
        private String curriculumTitle;

        public static LectureRecommendationResponse from(Lecture lecture, BigDecimal score, String reason) {
            return LectureRecommendationResponse.builder()
                    .lectureId(lecture.getId())
                    .title(lecture.getTitle())
                    .description(lecture.getDescription())
                    .type(lecture.getType().toString())
                    .category(lecture.getCategory())
                    .difficulty(lecture.getDifficulty())
                    .recommendationScore(score)
                    .recommendationReason(reason)
                    .build();
        }
    }

    /**
     * 추천 로그 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogRecommendationRequest {
        private Long curriculumId;
        private Long lectureId;
        private String recommendationType;
        private BigDecimal recommendationScore;
        private Integer displayOrder;
    }

    /**
     * 추천 클릭 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogRecommendationClickRequest {
        private Long curriculumId;
        private Long lectureId;
    }

    /**
     * 추천 통계 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationStatsResponse {
        private String recommendationType;
        private Long totalImpressions;
        private Long totalClicks;
        private BigDecimal clickThroughRate; // CTR
    }
}

