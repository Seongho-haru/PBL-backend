package com.PBL.recommendation.repository;

import com.PBL.recommendation.entity.RecommendationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 추천 로그 Repository
 */
@Repository
public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, Long> {

    /**
     * 사용자별 추천 로그 조회
     */
    List<RecommendationLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 사용자와 추천 타입으로 로그 조회
     */
    List<RecommendationLog> findByUserIdAndRecommendationTypeOrderByCreatedAtDesc(
            Long userId, String recommendationType);

    /**
     * 추천된 항목(커리큘럼)을 클릭한 로그 조회
     */
    Optional<RecommendationLog> findByUserIdAndCurriculumIdAndIsClickedTrue(
            Long userId, Long curriculumId);

    /**
     * 추천된 항목(강의)을 클릭한 로그 조회
     */
    Optional<RecommendationLog> findByUserIdAndLectureIdAndIsClickedTrue(
            Long userId, Long lectureId);

    /**
     * 최근 일정 기간 내 추천 로그 조회
     */
    @Query("SELECT r FROM RecommendationLog r WHERE r.createdAt >= :since ORDER BY r.createdAt DESC")
    List<RecommendationLog> findRecentLogs(@Param("since") LocalDateTime since);

    /**
     * 특정 기간 클릭률 조회
     */
    @Query("SELECT r.recommendationType, COUNT(r), SUM(CASE WHEN r.isClicked = true THEN 1 ELSE 0 END) " +
           "FROM RecommendationLog r WHERE r.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY r.recommendationType")
    List<Object[]> findClickRateByType(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 커리큘럼의 추천 횟수 조회
     */
    long countByCurriculumId(Long curriculumId);

    /**
     * 특정 강의의 추천 횟수 조회
     */
    long countByLectureId(Long lectureId);
}

