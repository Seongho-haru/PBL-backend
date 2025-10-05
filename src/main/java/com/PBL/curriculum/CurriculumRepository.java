package com.PBL.curriculum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 커리큘럼 Repository
 * 기본 CRUD + 커리큘럼 검색/필터링 기능
 */
@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    /**
     * 공개된 커리큘럼만 조회
     */
    List<Curriculum> findByIsPublicTrueOrderByCreatedAtDesc();

    /**
     * 상태별 커리큘럼 조회
     */
    List<Curriculum> findByStatus(CurriculumStatus status);

    /**
     * 카테고리별 커리큘럼 조회
     */
    List<Curriculum> findByCategory(String category);

    /**
     * 난이도별 커리큘럼 조회
     */
    List<Curriculum> findByDifficulty(String difficulty);

    /**
     * 제목으로 검색 (부분 일치)
     */
    List<Curriculum> findByTitleContainingIgnoreCase(String title);

    /**
     * 공개된 커리큘럼 중 제목으로 검색
     */
    List<Curriculum> findByIsPublicTrueAndTitleContainingIgnoreCase(String title);

    /**
     * 상태와 카테고리로 필터링
     */
    List<Curriculum> findByStatusAndCategory(CurriculumStatus status, String category);

    /**
     * 상태와 난이도로 필터링
     */
    List<Curriculum> findByStatusAndDifficulty(CurriculumStatus status, String difficulty);

    /**
     * 페이징을 지원하는 공개 커리큘럼 조회
     */
    Page<Curriculum> findByIsPublicTrue(Pageable pageable);

    /**
     * 페이징을 지원하는 상태별 조회
     */
    Page<Curriculum> findByStatus(CurriculumStatus status, Pageable pageable);

    /**
     * 페이징을 지원하는 제목 검색
     */
    Page<Curriculum> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * 섹션과 강의를 포함한 커리큘럼 조회 (N+1 문제 해결)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.sections s " +
           "LEFT JOIN FETCH s.lectures cl " +
           "LEFT JOIN FETCH cl.lecture l " +
           "WHERE c.id = :id")
    Optional<Curriculum> findByIdWithSectionsAndLectures(@Param("id") Long id);

    /**
     * 모든 커리큘럼을 섹션과 함께 조회 (최신순)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.sections " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findAllWithSections();

    /**
     * 공개된 커리큘럼을 섹션과 함께 조회
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.sections " +
           "WHERE c.isPublic = true " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findPublicCurriculumsWithSections();

    /**
     * 특정 강의가 포함된 커리큘럼들 조회
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "JOIN c.sections s " +
           "JOIN s.lectures cl " +
           "WHERE cl.lecture.id = :lectureId")
    List<Curriculum> findCurriculumsContainingLecture(@Param("lectureId") Long lectureId);

    /**
     * 커리큘럼 통계: 상태별 개수
     */
    @Query("SELECT c.status, COUNT(c) FROM Curriculum c GROUP BY c.status")
    List<Object[]> getCurriculumStatsByStatus();

    /**
     * 커리큘럼 통계: 카테고리별 개수
     */
    @Query("SELECT c.category, COUNT(c) FROM Curriculum c WHERE c.category IS NOT NULL GROUP BY c.category")
    List<Object[]> getCurriculumStatsByCategory();

    /**
     * 커리큘럼 통계: 난이도별 개수
     */
    @Query("SELECT c.difficulty, COUNT(c) FROM Curriculum c WHERE c.difficulty IS NOT NULL GROUP BY c.difficulty")
    List<Object[]> getCurriculumStatsByDifficulty();

    /**
     * 최근 생성된 공개 커리큘럼 조회 (제한된 개수)
     */
    List<Curriculum> findTop10ByIsPublicTrueOrderByCreatedAtDesc();

    /**
     * 인기 커리큘럼 조회 (섹션 수 기준)
     */
    @Query("SELECT c FROM Curriculum c " +
           "WHERE c.isPublic = true " +
           "ORDER BY SIZE(c.sections) DESC")
    List<Curriculum> findPopularCurriculums(Pageable pageable);
}
