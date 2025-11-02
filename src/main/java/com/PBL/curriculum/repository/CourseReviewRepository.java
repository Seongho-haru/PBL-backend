package com.PBL.curriculum.repository;

import com.PBL.curriculum.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰/문의 Repository
 */
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    /**
     * 특정 커리큘럼의 모든 리뷰 조회 (공개만)
     */
    @Query("SELECT r FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.isReview = true ORDER BY r.createdAt DESC")
    Page<CourseReview> findByCurriculumIdAndIsReviewOrderByCreatedAtDesc(
            @Param("curriculumId") Long curriculumId,
            Pageable pageable
    );

    /**
     * 특정 커리큘럼의 모든 문의 조회 (공개만)
     */
    @Query("SELECT r FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.isReview = false AND r.isPublic = true ORDER BY r.createdAt DESC")
    Page<CourseReview> findByCurriculumIdAndIsInquiryPublicOrderByCreatedAtDesc(
            @Param("curriculumId") Long curriculumId,
            Pageable pageable
    );

    /**
     * 특정 커리큘럼의 모든 문의 조회 (공개 + 관리자/작성자는 비공개 포함)
     */
    @Query("SELECT r FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.isReview = false " +
           "AND (r.isPublic = true OR :userId = 1L OR r.curriculum.author.id = :userId) ORDER BY r.createdAt DESC")
    Page<CourseReview> findInquiriesAccessibleByUser(
            @Param("curriculumId") Long curriculumId,
            @Param("userId") Long userId,
            Pageable pageable
    );

    /**
     * 사용자가 작성한 리뷰 조회 (공개 여부 무관)
     */
    @Query("SELECT r FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.author.id = :userId AND r.isReview = true")
    Optional<CourseReview> findReviewByCurriculumIdAndUserId(
            @Param("curriculumId") Long curriculumId,
            @Param("userId") Long userId
    );

    /**
     * 사용자의 모든 리뷰 조회 (작성자 본인 + 공개 검색)
     */
    @Query("SELECT r FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.isReview = true AND (r.isPublic = true OR r.author.id = :userId)")
    Page<CourseReview> findReviewsAccessibleByUser(
            @Param("curriculumId") Long curriculumId,
            @Param("userId") Long userId,
            Pageable pageable
    );

    /**
     * 사용자의 모든 문의 조회 (작성자 본인만)
     */
    @Query("SELECT r FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.isReview = false AND r.author.id = :userId ORDER BY r.createdAt DESC")
    List<CourseReview> findInquiriesByCurriculumIdAndUserId(
            @Param("curriculumId") Long curriculumId,
            @Param("userId") Long userId
    );

    /**
     * 커리큘럼의 평균 평점 계산
     */
    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.isReview = true AND r.rating IS NOT NULL")
    Double calculateAverageRating(@Param("curriculumId") Long curriculumId);

    /**
     * 커리큘럼의 리뷰 개수
     */
    @Query("SELECT COUNT(r) FROM CourseReview r WHERE r.curriculum.id = :curriculumId AND r.isReview = true")
    Long countByCurriculumId(@Param("curriculumId") Long curriculumId);
}

