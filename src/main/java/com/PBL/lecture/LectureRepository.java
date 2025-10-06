package com.PBL.lecture;

import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 강의 Repository
 * 기본 CRUD + 강의 검색/필터링 기능
 */
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    /**
     * 강의 유형별 조회
     */
    List<Lecture> findByType(LectureType type);

    /**
     * 카테고리별 조회
     */
    List<Lecture> findByCategory(String category);

    /**
     * 난이도별 조회
     */
    List<Lecture> findByDifficulty(String difficulty);

    /**
     * 제목으로 검색 (부분 일치)
     */
    List<Lecture> findByTitleContainingIgnoreCase(String title);

    /**
     * 강의 유형과 카테고리로 필터링
     */
    List<Lecture> findByTypeAndCategory(LectureType type, String category);

    /**
     * 강의 유형과 난이도로 필터링
     */
    List<Lecture> findByTypeAndDifficulty(LectureType type, String difficulty);

    /**
     * 페이징을 지원하는 강의 유형별 조회
     */
    Page<Lecture> findByType(LectureType type, Pageable pageable);

    /**
     * 페이징을 지원하는 제목 검색
     */
    Page<Lecture> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * 테스트케이스가 있는 문제 강의만 조회
     */
    @Query("SELECT l FROM Lecture l WHERE l.type = :type AND SIZE(l.testCases) > 0")
    List<Lecture> findProblemLecturesWithTestCases(@Param("type") LectureType type);

    /**
     * 강의 통계: 유형별 개수
     */
    @Query("SELECT l.type, COUNT(l) FROM Lecture l GROUP BY l.type")
    List<Object[]> countByType();

    /**
     * 강의 통계: 카테고리별 개수
     */
    @Query("SELECT l.category, COUNT(l) FROM Lecture l WHERE l.category IS NOT NULL GROUP BY l.category")
    List<Object[]> countByCategory();

    /**
     * 최근 생성된 강의 조회
     */
    List<Lecture> findTop10ByOrderByCreatedAtDesc();

    /**
     * ID로 조회 (테스트케이스 포함 - fetch join)
     */
    @Query("SELECT l FROM Lecture l LEFT JOIN FETCH l.testCases WHERE l.id = :id")
    Optional<Lecture> findByIdWithTestCases(@Param("id") Long id);

    /**
     * 모든 강의 조회 (테스트케이스 포함 - fetch join)
     */
    @Query("SELECT DISTINCT l FROM Lecture l LEFT JOIN FETCH l.testCases ORDER BY l.createdAt DESC")
    List<Lecture> findAllWithTestCases();

    /**
     * 모든 강의 조회 최적화 버전 (Batch Size 적용)
     * N+1 쿼리 문제 해결을 위한 최적화된 쿼리
     */
    @Query(value = "SELECT l FROM Lecture l ORDER BY l.createdAt DESC")
    List<Lecture> findAllWithTestCasesOptimized();

    /**
     * 기본 강의 조회 (테스트케이스 없이)
     */
    List<Lecture> findAllByOrderByCreatedAtDesc();

    /**
     * 복합 검색: 제목, 카테고리, 난이도
     */
    @Query("SELECT l FROM Lecture l WHERE " +
            "(:title IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:category IS NULL OR l.category = :category) AND " +
            "(:difficulty IS NULL OR l.difficulty = :difficulty) AND " +
            "(:type IS NULL OR l.type = :type)")
    Page<Lecture> findBySearchCriteria(
            @Param("title") String title,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("type") LectureType type,
            Pageable pageable
    );

    /**
     * 특정 카테고리가 존재하는지 확인
     */
    boolean existsByCategory(String category);

    /**
     * 특정 난이도가 존재하는지 확인
     */
    boolean existsByDifficulty(String difficulty);

    // === 공개 강의 관련 메소드 ===

    /**
     * 모든 공개 강의 조회 (최신순)
     */
    List<Lecture> findByIsPublicTrueOrderByCreatedAtDesc();

    /**
     * 공개 강의 중 제목으로 검색
     */
    List<Lecture> findByIsPublicTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    /**
     * 공개 강의 중 카테고리별 조회
     */
    List<Lecture> findByIsPublicTrueAndCategoryOrderByCreatedAtDesc(String category);

    /**
     * 공개 강의 중 난이도별 조회
     */
    List<Lecture> findByIsPublicTrueAndDifficultyOrderByCreatedAtDesc(String difficulty);

    /**
     * 공개 강의 중 유형별 조회
     */
    List<Lecture> findByIsPublicTrueAndTypeOrderByCreatedAtDesc(LectureType type);

    /**
     * 공개 강의 복합 검색
     */
    @Query(value = "SELECT * FROM lectures l WHERE l.is_public = true AND " +
            "(:title IS NULL OR l.title ILIKE CONCAT('%', :title, '%')) AND " +
            "(:category IS NULL OR l.category = :category) AND " +
            "(:difficulty IS NULL OR l.difficulty = :difficulty) AND " +
            "(:type IS NULL OR l.type = :typeStr) " +
            "ORDER BY l.created_at DESC", nativeQuery = true)
    List<Lecture> findPublicLecturesBySearchCriteria(
            @Param("title") String title,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("type") LectureType type,
            @Param("typeStr") String typeStr
    );
}
