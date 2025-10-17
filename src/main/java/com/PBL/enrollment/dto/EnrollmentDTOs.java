package com.PBL.enrollment.dto;

import com.PBL.enrollment.entity.Enrollment;
import com.PBL.enrollment.entity.EnrollmentStatus;
import com.PBL.enrollment.entity.LectureProgress;
import com.PBL.enrollment.entity.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 수강 관련 DTO 클래스들
 */
public class EnrollmentDTOs {

    /**
     * 수강 신청 요청 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollRequest {
        private Long curriculumId;
    }

    /**
     * 수강 응답 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentResponse {
        private Long id;
        private Long userId;
        private String username;
        private Long curriculumId;
        private String curriculumTitle;
        private String curriculumDescription;
        private EnrollmentStatus status;
        private Integer progressPercentage;
        private LocalDateTime enrolledAt;
        private LocalDateTime completedAt;
        private Integer totalLectures;
        private Integer completedLectures;

        public static EnrollmentResponse from(Enrollment enrollment) {
            EnrollmentResponse response = new EnrollmentResponse();
            response.setId(enrollment.getId());
            response.setUserId(enrollment.getUser().getId());
            response.setUsername(enrollment.getUser().getUsername());
            response.setCurriculumId(enrollment.getCurriculum().getId());
            response.setCurriculumTitle(enrollment.getCurriculum().getTitle());
            response.setCurriculumDescription(enrollment.getCurriculum().getDescription());
            response.setStatus(enrollment.getStatus());
            response.setProgressPercentage(enrollment.getProgressPercentage());
            response.setEnrolledAt(enrollment.getEnrolledAt());
            response.setCompletedAt(enrollment.getCompletedAt());
            return response;
        }
    }

    /**
     * 수강 상세 응답 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentDetailResponse {
        private Long id;
        private Long userId;
        private String username;
        private Long curriculumId;
        private String curriculumTitle;
        private String curriculumDescription;
        private EnrollmentStatus status;
        private Integer progressPercentage;
        private LocalDateTime enrolledAt;
        private LocalDateTime completedAt;
        private List<LectureProgressResponse> lectures;

        public static EnrollmentDetailResponse from(Enrollment enrollment, List<LectureProgress> lectureProgresses) {
            EnrollmentDetailResponse response = new EnrollmentDetailResponse();
            response.setId(enrollment.getId());
            response.setUserId(enrollment.getUser().getId());
            response.setUsername(enrollment.getUser().getUsername());
            response.setCurriculumId(enrollment.getCurriculum().getId());
            response.setCurriculumTitle(enrollment.getCurriculum().getTitle());
            response.setCurriculumDescription(enrollment.getCurriculum().getDescription());
            response.setStatus(enrollment.getStatus());
            response.setProgressPercentage(enrollment.getProgressPercentage());
            response.setEnrolledAt(enrollment.getEnrolledAt());
            response.setCompletedAt(enrollment.getCompletedAt());
            response.setLectures(lectureProgresses.stream()
                    .map(LectureProgressResponse::from)
                    .collect(Collectors.toList()));
            return response;
        }
    }

    /**
     * 강의 진도 응답 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LectureProgressResponse {
        private Long id;
        private Long lectureId;
        private String lectureTitle;
        private String lectureType;
        private String category;
        private String difficulty;
        private ProgressStatus status;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private Integer order;
        private Boolean isRequired;

        public static LectureProgressResponse from(LectureProgress lectureProgress) {
            LectureProgressResponse response = new LectureProgressResponse();
            response.setId(lectureProgress.getId());
            response.setLectureId(lectureProgress.getLecture().getId());
            response.setLectureTitle(lectureProgress.getLecture().getTitle());
            response.setLectureType(lectureProgress.getLecture().getType().name());
            response.setCategory(lectureProgress.getLecture().getCategory());
            response.setDifficulty(lectureProgress.getLecture().getDifficulty());
            response.setStatus(lectureProgress.getStatus());
            response.setStartedAt(lectureProgress.getStartedAt());
            response.setCompletedAt(lectureProgress.getCompletedAt());
            response.setOrder(lectureProgress.getCurriculumLecture().getOrderIndex());
            response.setIsRequired(lectureProgress.getCurriculumLecture().getIsRequired());
            return response;
        }
    }

    /**
     * 강의 완료 요청 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteLectureRequest {
        private Long lectureId;
    }

    /**
     * 수강 통계 응답 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentStatsResponse {
        private Long totalEnrollments;
        private Long completedEnrollments;
        private Long inProgressEnrollments;
        private Double averageProgress;
        private List<CurriculumStats> curriculumStats;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CurriculumStats {
            private Long curriculumId;
            private String curriculumTitle;
            private Long totalEnrollments;
            private Long completedEnrollments;
            private Double completionRate;
        }
    }
}
