package com.PBL.lecture.repository;

import com.PBL.lecture.LectureType;
import com.PBL.lecture.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 강의 Repository (Data Access Layer)
 *
 * === 개요 ===
 * - Lecture 엔티티의 데이터베이스 접근 계층
 * - Spring Data JPA의 JpaRepository 상속으로 기본 CRUD 자동 제공
 * - 커스텀 쿼리 메서드 및 JPQL/@Query 활용한 복잡한 조회 기능
 *
 * === 주요 기능 ===
 * 1. 기본 CRUD: save(), findById(), delete() 등 (JpaRepository 제공)
 * 2. 검색/필터링: 제목, 카테고리, 난이도, 타입별 조회
 * 3. 페이징: Page<Lecture> 반환 메서드들
 * 4. 통계: 타입별/카테고리별 집계
 * 5. N+1 문제 해결: fetch join을 활용한 최적화 쿼리
 * 6. 공개 강의 조회: isPublic 필드 기반 필터링
 * 7. 작성자별 조회: User와의 연관관계 활용
 *
 * === Spring Data JPA 쿼리 메서드 네이밍 규칙 ===
 * - findBy: SELECT 쿼리
 * - And, Or: 조건 결합
 * - OrderBy: 정렬 (Asc/Desc)
 * - Containing: LIKE '%value%'
 * - IgnoreCase: 대소문자 무시
 * - True/False: Boolean 값 비교
 *
 * === 통합 작업 시 유의사항 ===
 * - FetchType.LAZY 필드 접근 시 LazyInitializationException 주의
 * - @Transactional 컨텍스트 내에서 lazy loading 필드 접근 필요
 * - findByIdWithTestCases() 등 fetch join 메서드 활용 권장
 * - 페이징 쿼리 시 카운트 쿼리 자동 실행됨 (성능 고려)
 */
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // ============================================================
    // 기본 조회 메서드 (Spring Data JPA 메서드 네이밍)
    // ============================================================

    /**
     * 강의 유형별 조회
     *
     * @param type 조회할 강의 유형 (MARKDOWN 또는 PROBLEM)
     * @return 해당 타입의 모든 강의 목록
     *
     * 생성 쿼리:
     * SELECT * FROM lectures WHERE type = :type
     *
     * 활용:
     * - 마크다운 강의만 조회
     * - 문제 강의만 조회
     * - 타입별 강의 개수 확인
     */
    List<Lecture> findByType(LectureType type);

    /**
     * 카테고리별 조회
     *
     * @param category 조회할 카테고리명
     * @return 해당 카테고리의 모든 강의 목록
     *
     * 생성 쿼리:
     * SELECT * FROM lectures WHERE category = :category
     *
     * 활용:
     * - "알고리즘" 카테고리 강의만 조회
     * - 카테고리별 강의 목록 표시
     */
    List<Lecture> findByCategory(String category);

    /**
     * 난이도별 조회
     *
     * @param difficulty 조회할 난이도
     * @return 해당 난이도의 모든 강의 목록
     *
     * 생성 쿼리:
     * SELECT * FROM lectures WHERE difficulty = :difficulty
     *
     * 활용:
     * - "초급" 난이도 강의만 조회
     * - 학습자 수준별 필터링
     */
    List<Lecture> findByDifficulty(String difficulty);

    /**
     * 제목으로 검색 (부분 일치, 대소문자 무시)
     *
     * @param title 검색할 제목 키워드
     * @return 제목에 키워드가 포함된 강의 목록
     *
     * 생성 쿼리:
     * SELECT * FROM lectures WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))
     *
     * 활용:
     * - 검색 기능 구현
     * - "자바" 검색 시 "자바 기초", "자바 고급" 등 모두 반환
     */
    List<Lecture> findByTitleContainingIgnoreCase(String title);

    /**
     * 강의 유형과 카테고리로 필터링
     *
     * @param type 강의 유형
     * @param category 카테고리명
     * @return 조건을 모두 만족하는 강의 목록
     *
     * 활용: 문제 강의 중 "알고리즘" 카테고리만 조회
     */
    List<Lecture> findByTypeAndCategory(LectureType type, String category);

    /**
     * 강의 유형과 난이도로 필터링
     *
     * @param type 강의 유형
     * @param difficulty 난이도
     * @return 조건을 모두 만족하는 강의 목록
     *
     * 활용: 문제 강의 중 "초급" 난이도만 조회
     */
    List<Lecture> findByTypeAndDifficulty(LectureType type, String difficulty);

    // ============================================================
    // 페이징 조회 메서드
    // ============================================================

    /**
     * 페이징을 지원하는 강의 유형별 조회
     *
     * @param type 강의 유형
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬)
     * @return Page<Lecture> 객체 (내용 + 페이징 메타데이터)
     *
     * 활용:
     * - 강의 목록 페이지네이션
     * - 페이지당 10개씩 표시
     *
     * 사용 예시:
     * PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending());
     * Page<Lecture> page = findByType(LectureType.PROBLEM, pageRequest);
     */
    Page<Lecture> findByType(LectureType type, Pageable pageable);

    /**
     * 페이징을 지원하는 제목 검색
     *
     * @param title 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과 페이지
     *
     * 활용: 검색 결과 페이지네이션
     */
    Page<Lecture> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // ============================================================
    // 커스텀 쿼리 메서드 (@Query JPQL)
    // ============================================================

    /**
     * 테스트케이스가 있는 문제 강의만 조회
     *
     * @param type 강의 유형 (일반적으로 PROBLEM)
     * @return 테스트케이스가 1개 이상인 강의 목록
     *
     * JPQL 쿼리:
     * - SIZE(l.testCases) > 0: 컬렉션 크기 확인
     *
     * 활용:
     * - 제출 가능한 문제만 조회
     * - 완성된 문제 강의 필터링
     *
     * 주의사항:
     * - SIZE() 함수는 서브쿼리로 변환되어 성능 영향 있을 수 있음
     */
    @Query("SELECT l FROM Lecture l WHERE l.type = :type AND SIZE(l.testCases) > 0")
    List<Lecture> findProblemLecturesWithTestCases(@Param("type") LectureType type);

    /**
     * 강의 통계: 유형별 개수
     *
     * @return List<Object[]> - [0]: LectureType, [1]: count
     *
     * JPQL 쿼리:
     * - GROUP BY로 타입별 집계
     *
     * 활용:
     * - 대시보드 통계
     * - 예: MARKDOWN 10개, PROBLEM 25개
     *
     * 반환 형식:
     * [[MARKDOWN, 10], [PROBLEM, 25]]
     */
    @Query("SELECT l.type, COUNT(l) FROM Lecture l GROUP BY l.type")
    List<Object[]> countByType();

    /**
     * 강의 통계: 카테고리별 개수
     *
     * @return List<Object[]> - [0]: category, [1]: count
     *
     * JPQL 쿼리:
     * - WHERE l.category IS NOT NULL: null 카테고리 제외
     * - GROUP BY로 카테고리별 집계
     *
     * 활용:
     * - 카테고리 태그 클라우드
     * - 인기 카테고리 파악
     *
     * 반환 형식:
     * [["알고리즘", 15], ["자료구조", 8], ...]
     */
    @Query("SELECT l.category, COUNT(l) FROM Lecture l WHERE l.category IS NOT NULL GROUP BY l.category")
    List<Object[]> countByCategory();

    /**
     * 최근 생성된 강의 조회 (최대 10개)
     *
     * @return 최신순으로 정렬된 강의 목록 (최대 10개)
     *
     * Spring Data JPA 특별 키워드:
     * - Top10: 상위 10개만 조회 (LIMIT 10)
     * - OrderByCreatedAtDesc: createdAt 내림차순 정렬
     *
     * 활용:
     * - 메인 페이지 "최근 강의" 섹션
     * - 새로 올라온 강의 표시
     */
    List<Lecture> findTop10ByOrderByCreatedAtDesc();

    /**
     * ID로 조회 (테스트케이스 포함 - fetch join)
     *
     * @param id 조회할 강의 ID
     * @return 강의 + 테스트케이스 (한 번의 쿼리로 로딩)
     *
     * JPQL 쿼리:
     * - LEFT JOIN FETCH: 테스트케이스를 강의와 함께 즉시 로딩
     * - LEFT: 테스트케이스가 없어도 강의는 조회됨
     *
     * N+1 문제 해결:
     * - 일반 findById() 사용 시:
     *   1. SELECT lecture (1번 쿼리)
     *   2. SELECT test_cases (testCases 접근 시 추가 쿼리)
     * - fetch join 사용 시:
     *   1. SELECT lecture LEFT JOIN test_cases (1번 쿼리로 완료)
     *
     * 활용:
     * - 강의 상세 페이지 (테스트케이스 함께 표시)
     * - 코드 제출 전 테스트케이스 로딩
     *
     * 주의사항:
     * - @Transactional 컨텍스트 밖에서도 testCases 접근 가능
     */
    @Query("SELECT DISTINCT l FROM Lecture l " +
            "LEFT JOIN FETCH l.testCases " +
            "WHERE l.id = :id")
    Optional<Lecture> findByIdWithTestCases(@Param("id") Long id);

    /**
     * 모든 강의 조회 (테스트케이스 포함 - fetch join)
     *
     * 주의: DISTINCT 사용으로 중복 제거 (일대다 조인 시 필요)
     * 활용: 전체 강의 목록을 테스트케이스와 함께 조회
     */
    @Query("SELECT DISTINCT l FROM Lecture l LEFT JOIN FETCH l.testCases ORDER BY l.createdAt DESC")
    List<Lecture> findAllWithTestCases();

    /**
     * 모든 강의 조회 최적화 버전 (Batch Size 적용)
     *
     * N+1 쿼리 문제 해결:
     * - Lecture 엔티티의 @BatchSize(size=10) 설정 활용
     * - testCases는 필요 시 배치로 로딩됨
     *
     * 활용: 대량의 강의 조회 시 성능 최적화
     */
    @Query(value = "SELECT l FROM Lecture l ORDER BY l.createdAt DESC")
    List<Lecture> findAllWithTestCasesOptimized();

    /**
     * 기본 강의 조회 (테스트케이스 없이)
     *
     * 활용: 강의 목록만 필요한 경우 (상세 정보 불필요)
     */
    List<Lecture> findAllByOrderByCreatedAtDesc();

    /**
     * 모든 강의 조회 (페이징, 공개 여부 필터 포함)
     */
    @Query(value = "SELECT * FROM lectures l " +
           "WHERE (:isPublic IS NULL OR l.is_public = :isPublic) " +
           "ORDER BY l.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM lectures l WHERE (:isPublic IS NULL OR l.is_public = :isPublic)",
           nativeQuery = true)
    Page<Lecture> findAllWithPagination(@Param("isPublic") Boolean isPublic, Pageable pageable);

    /**
     * 타입별 강의 조회 (페이징, 공개 여부 필터 포함)
     */
    @Query(value = "SELECT * FROM lectures l " +
           "WHERE l.type = :typeStr " +
           "AND (:isPublic IS NULL OR l.is_public = :isPublic) " +
           "ORDER BY l.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM lectures l " +
           "WHERE l.type = :typeStr AND (:isPublic IS NULL OR l.is_public = :isPublic)",
           nativeQuery = true)
    Page<Lecture> findByTypeWithPagination(@Param("type") LectureType type, @Param("typeStr") String typeStr, 
                                          @Param("isPublic") Boolean isPublic, Pageable pageable);

    /**
     * 복합 검색: 제목, 카테고리, 난이도
     */
    @Query(value = "SELECT * FROM lectures l WHERE " +
            "(:title IS NULL OR l.title ILIKE CONCAT('%', :title, '%')) AND " +
            "(:category IS NULL OR l.category = :category) AND " +
            "(:difficulty IS NULL OR l.difficulty = :difficulty) AND " +
            "(:type IS NULL OR l.type = :typeStr) " +
            "ORDER BY l.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM lectures l WHERE " +
            "(:title IS NULL OR l.title ILIKE CONCAT('%', :title, '%')) AND " +
            "(:category IS NULL OR l.category = :category) AND " +
            "(:difficulty IS NULL OR l.difficulty = :difficulty) AND " +
            "(:type IS NULL OR l.type = :typeStr)",
            nativeQuery = true)
    Page<Lecture> findBySearchCriteria(
            @Param("title") String title,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("type") LectureType type,
            @Param("typeStr") String typeStr,
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

    // ============================================================
    // 공개 강의 관련 메서드
    // ============================================================

    /**
     * 모든 공개 강의 조회 (최신순)
     *
     * 메서드 네이밍:
     * - IsPublicTrue: isPublic = true 조건
     * - OrderByCreatedAtDesc: createdAt 내림차순 정렬
     *
     * 활용:
     * - 공개 강의 목록 페이지
     * - 다른 사용자가 만든 강의 둘러보기
     */
    List<Lecture> findByIsPublicTrueOrderByCreatedAtDesc();

    /**
     * 모든 공개 강의 조회 (페이징)
     */
    Page<Lecture> findByIsPublicTrue(Pageable pageable);

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

    /**
     * 공개 강의 복합 검색 (페이징, 공개 여부 필터 포함)
     * PostgreSQL 타입 추론 문제 해결을 위해 LIMIT/OFFSET을 명시적으로 사용
     * Pageable을 제거하여 Spring Data JPA의 자동 페이징 추가를 방지
     * enum 타입 추론 문제를 피하기 위해 type 파라미터 제거하고 typeStr만 사용
     */
    @Query(value = "SELECT * FROM lectures l WHERE " +
            "(:isPublic IS NULL OR l.is_public = :isPublic) AND " +
            "(:title IS NULL OR l.title ILIKE CONCAT('%', :title, '%')) AND " +
            "(:category IS NULL OR l.category = :category) AND " +
            "(:difficulty IS NULL OR l.difficulty = :difficulty) AND " +
            "(:typeStr IS NULL OR l.type = :typeStr) " +
            "ORDER BY l.created_at DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Lecture> findPublicLecturesBySearchCriteria(
            @Param("title") String title,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("typeStr") String typeStr,
            @Param("isPublic") Boolean isPublic,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * 공개 강의 복합 검색 총 개수 조회
     */
    @Query(value = "SELECT COUNT(*) FROM lectures l WHERE " +
            "(:isPublic IS NULL OR l.is_public = :isPublic) AND " +
            "(:title IS NULL OR l.title ILIKE CONCAT('%', :title, '%')) AND " +
            "(:category IS NULL OR l.category = :category) AND " +
            "(:difficulty IS NULL OR l.difficulty = :difficulty) AND " +
            "(:typeStr IS NULL OR l.type = :typeStr)",
            nativeQuery = true)
    long countPublicLecturesBySearchCriteria(
            @Param("title") String title,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("typeStr") String typeStr,
            @Param("isPublic") Boolean isPublic
    );

    // ============================================================
    // 작성자 관련 메서드
    // ============================================================

    /**
     * 작성자 ID로 강의 조회
     *
     * @param authorId 작성자 사용자 ID
     * @return 해당 작성자가 만든 모든 강의 (공개/비공개 모두 포함)
     *
     * 활용:
     * - 내 강의 목록 조회
     * - 특정 사용자의 전체 강의 조회
     */
    List<Lecture> findByAuthorId(Long authorId);

    /**
     * 작성자 ID로 강의 조회 (페이징)
     *
     * @param authorId 작성자 사용자 ID
     * @param pageable 페이징 정보
     * @return 해당 작성자가 만든 강의 페이지
     *
     * 활용:
     * - 내 강의 목록 조회 (페이징)
     * - 특정 사용자의 전체 강의 조회 (페이징)
     */
    Page<Lecture> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * 작성자 ID와 공개 여부로 강의 조회
     *
     * @param authorId 작성자 사용자 ID
     * @return 해당 작성자의 공개 강의만
     *
     * 활용:
     * - 다른 사용자가 특정 작성자의 공개 강의 둘러보기
     * - 작성자별 공개 강의 포트폴리오
     */
    List<Lecture> findByAuthorIdAndIsPublicTrue(Long authorId);

    /**
     * 작성자 ID와 공개 여부로 강의 조회 (페이징)
     *
     * @param authorId 작성자 사용자 ID
     * @param pageable 페이징 정보
     * @return 해당 작성자의 공개 강의 페이지
     *
     * 활용:
     * - 다른 사용자가 특정 작성자의 공개 강의 둘러보기 (페이징)
     * - 작성자별 공개 강의 포트폴리오 (페이징)
     */
    Page<Lecture> findByAuthorIdAndIsPublicTrue(Long authorId, Pageable pageable);

    /**
     * 작성자 ID와 강의 유형으로 조회
     */
    List<Lecture> findByAuthorIdAndType(Long authorId, LectureType type);

    /**
     * 작성자 ID와 카테고리로 조회
     */
    List<Lecture> findByAuthorIdAndCategory(Long authorId, String category);

    /**
     * 작성자 ID와 난이도로 조회
     */
    List<Lecture> findByAuthorIdAndDifficulty(Long authorId, String difficulty);

    /**
     * 작성자 ID로 강의 개수 조회
     */
    long countByAuthorId(Long authorId);

    /**
     * 작성자 ID와 공개 여부로 강의 개수 조회
     */
    long countByAuthorIdAndIsPublic(Long authorId, Boolean isPublic);

    /**
     * 타입, 카테고리, 난이도로 강의 조회
     */
    List<Lecture> findByTypeAndCategoryAndDifficulty(LectureType type, String category, String difficulty);

    /**
     * 공개 문제 강의 중 타입, 카테고리, 난이도로 조회
     */
    List<Lecture> findByTypeAndCategoryAndDifficultyAndIsPublicTrue(LectureType type, String category, String difficulty);
}
