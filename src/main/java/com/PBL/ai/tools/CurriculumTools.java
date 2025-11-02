package com.PBL.ai.tools;

import com.PBL.curriculum.Curriculum;
import com.PBL.curriculum.CurriculumService;
import com.PBL.curriculum.dto.CourseReviewDTOs;
import com.PBL.curriculum.service.CourseReviewService;
import com.PBL.enrollment.dto.EnrollmentDTOs;
import com.PBL.enrollment.entity.Enrollment;
import com.PBL.enrollment.entity.EnrollmentStatus;
import com.PBL.enrollment.service.EnrollmentService;
import com.PBL.recommendation.dto.RecommendationDTOs;
import com.PBL.recommendation.service.RecommendationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI가 사용할 커리큘럼 및 수강 관리 도구
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CurriculumTools {

    private final CurriculumService curriculumService;
    private final EnrollmentService enrollmentService;
    private final RecommendationService recommendationService;
    private final CourseReviewService courseReviewService;

    // ========================================
    // 커리큘럼 조회 도구
    // ========================================

    @Tool("커리큘럼 ID로 상세 정보를 조회합니다. 포함된 강의 목록과 순서를 확인할 수 있습니다.")
    public Curriculum getCurriculum(@P("조회할 커리큘럼 ID") Long curriculumId) {
        log.debug("🔧 [도구 호출] getCurriculum - curriculumId:{}", curriculumId);
        Curriculum result = curriculumService.getCurriculumById(curriculumId)
                .orElseThrow(() -> new IllegalArgumentException("커리큘럼을 찾을 수 없습니다: " + curriculumId));
        log.debug("✅ [도구 결과] getCurriculum - 커리큘럼명: {}", result.getTitle());
        return result;
    }

    @Tool("공개된 커리큘럼만 조회합니다. 학생들이 접근 가능한 학습 과정입니다.")
    public Map<String, Object> getPublicCurriculums(@P("페이지 번호 (0부터 시작)") int page, @P("페이지 크기") int size) {
        log.debug("🔧 [도구 호출] getPublicCurriculums - 페이지: {}, 크기: {}", page, size);
        Map<String, Object> result = curriculumService.getPublicCurriculums(page, size);
        log.debug("✅ [도구 결과] getPublicCurriculums - 결과 반환");
        return result;
    }

    @Tool("커리큘럼 제목으로 검색합니다. 부분 일치로 검색되며, 대소문자를 구분하지 않습니다.")
    public Map<String, Object> searchCurriculums(
            @P("검색할 커리큘럼 제목") String title,
            @P("페이지 번호 (0부터 시작)") int page,
            @P("페이지 크기") int size) {

        log.debug("🔧 [도구 호출] searchCurriculums - title:{}, page:{}, size:{}", title, page, size);
        Map<String, Object> result = curriculumService.searchPublicCurriculums(title, page, size);
        log.debug("✅ [도구 결과] searchCurriculums - 검색 완료");
        return result;
    }

    // ========================================
    // 추천 시스템 도구
    // ========================================

    @Tool("사용자 맞춤형 커리큘럼을 추천합니다. 학습 이력을 분석하여 관심 분야와 난이도에 맞는 학습 과정을 제안합니다.")
    public List<RecommendationDTOs.CurriculumRecommendationResponse> getPersonalizedCurriculums(
            @P("사용자 ID") Long userId,
            @P("추천 개수 (기본 5개)") int limit) {

        log.debug("🔧 [도구 호출] getPersonalizedCurriculums - userId:{}, limit:{}", userId, limit);
        Map<String, Object> resultMap = recommendationService.getPersonalizedCurriculums(userId, 0, limit > 0 ? limit : 5);
        @SuppressWarnings("unchecked")
        List<RecommendationDTOs.CurriculumRecommendationResponse> result = (List<RecommendationDTOs.CurriculumRecommendationResponse>) resultMap.get("curriculums");
        log.debug("✅ [도구 결과] getPersonalizedCurriculums - 추천 개수: {}", result != null ? result.size() : 0);
        return result != null ? result : new ArrayList<>();
    }

    @Tool("특정 문제와 유사한 다른 문제들을 추천합니다. 같은 주제나 알고리즘 유형의 문제를 찾을 때 유용합니다.")
    public List<RecommendationDTOs.LectureRecommendationResponse> getSimilarProblems(
            @P("사용자 ID") Long userId,
            @P("기준 강의 ID") Long lectureId,
            @P("추천 개수 (기본 5개)") int limit) {

        log.debug("🔧 [도구 호출] getSimilarProblems - userId:{}, lectureId:{}, limit:{}", userId, lectureId, limit);
        Map<String, Object> resultMap = recommendationService.getSimilarProblemLectures(userId, lectureId, 0, limit > 0 ? limit : 5);
        @SuppressWarnings("unchecked")
        List<RecommendationDTOs.LectureRecommendationResponse> result = (List<RecommendationDTOs.LectureRecommendationResponse>) resultMap.get("lectures");
        log.debug("✅ [도구 결과] getSimilarProblems - 추천 개수: {}", result != null ? result.size() : 0);
        return result != null ? result : new ArrayList<>();
    }

    @Tool("커리큘럼과 강의를 모두 포함한 통합 추천을 제공합니다.")
    public List<RecommendationDTOs.UnifiedRecommendationResponse> getUnifiedRecommendations(
            @P("사용자 ID") Long userId,
            @P("추천 개수 (기본 10개)") int limit) {

        log.debug("🔧 [도구 호출] getUnifiedRecommendations - userId:{}, limit:{}", userId, limit);
        Map<String, Object> resultMap = recommendationService.getUnifiedRecommendations(userId, 0, limit > 0 ? limit : 10);
        @SuppressWarnings("unchecked")
        List<RecommendationDTOs.UnifiedRecommendationResponse> result = (List<RecommendationDTOs.UnifiedRecommendationResponse>) resultMap.get("recommendations");
        log.debug("✅ [도구 결과] getUnifiedRecommendations - 추천 개수: {}", result != null ? result.size() : 0);
        return result != null ? result : new ArrayList<>();
    }

    // ========================================
    // 수강 관리 도구
    // ========================================

    @Tool("사용자의 수강 정보를 조회합니다. 전체 수강 목록 또는 특정 상태의 수강만 필터링할 수 있습니다.")
    public List<Enrollment> getUserEnrollments(
            @P("사용자 ID") Long userId,
            @P("수강 상태 (선택): IN_PROGRESS, COMPLETED, DROPPED") String status) {

        log.debug("🔧 [도구 호출] getUserEnrollments - userId:{}, status:{}", userId, status);

        List<Enrollment> result;
        if (status != null && !status.trim().isEmpty()) {
            try {
                EnrollmentStatus enrollmentStatus = EnrollmentStatus.valueOf(status.toUpperCase());
                result = enrollmentService.getUserEnrollmentsByStatus(userId, enrollmentStatus);
            } catch (IllegalArgumentException e) {
                log.error("❌ [도구 오류] 잘못된 수강 상태: {}", status);
                throw new IllegalArgumentException("올바른 수강 상태: IN_PROGRESS, COMPLETED, DROPPED");
            }
        } else {
            result = enrollmentService.getUserEnrollments(userId);
        }

        log.debug("✅ [도구 결과] getUserEnrollments - 수강 개수: {}", result.size());
        return result;
    }

    @Tool("특정 수강의 상세 진도를 조회합니다. 각 강의별 완료 여부와 학습 상태를 확인할 수 있습니다.")
    public EnrollmentDTOs.EnrollmentDetailResponse getEnrollmentDetail(
            @P("사용자 ID") Long userId,
            @P("수강 ID") Long enrollmentId) {

        log.debug("🔧 [도구 호출] getEnrollmentDetail - userId:{}, enrollmentId:{}", userId, enrollmentId);
        EnrollmentDTOs.EnrollmentDetailResponse result = enrollmentService.getEnrollmentDetail(userId, enrollmentId);
        log.debug("✅ [도구 결과] getEnrollmentDetail - 진도율: {}%", result.getProgressPercentage());
        return result;
    }

    // ========================================
    // 리뷰 시스템 도구
    // ========================================

    @Tool("커리큘럼의 리뷰와 평점을 조회합니다. 수강생들의 평가와 후기를 확인할 수 있습니다.")
    public Map<String, Object> getCurriculumReviews(
            @P("커리큘럼 ID") Long curriculumId,
            @P("페이지 번호 (0부터 시작)") int page,
            @P("페이지 크기 (기본 20)") int size) {

        log.debug("🔧 [도구 호출] getCurriculumReviews - curriculumId:{}, page:{}, size:{}",
                curriculumId, page, size);

        Pageable pageable = PageRequest.of(page, size > 0 ? size : 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CourseReviewDTOs.CourseReviewResponse> reviews = courseReviewService.getCurriculumReviews(curriculumId, pageable);
        CourseReviewDTOs.AverageRatingResponse avgRating = courseReviewService.getCurriculumAverageRating(curriculumId);

        Map<String, Object> result = Map.of(
                "reviews", reviews.getContent(),
                "average_rating", avgRating.getAverageRating(),
                "total_reviews", reviews.getTotalElements(),
                "total_pages", reviews.getTotalPages(),
                "current_page", page
        );

        log.debug("✅ [도구 결과] getCurriculumReviews - 평균 평점: {}, 리뷰 수: {}",
                avgRating.getAverageRating(), reviews.getTotalElements());
        return result;
    }
}
