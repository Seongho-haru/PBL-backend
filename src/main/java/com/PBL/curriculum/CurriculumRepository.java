package com.PBL.curriculum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 커리큘럼 Repository
 */
@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    /**
     * 모든 커리큘럼을 강의와 함께 조회 (최신순)
     * MultipleBagFetchException 방지를 위해 단계별 로딩
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findAllWithLectures();

    /**
     * 공개 커리큘럼만 강의와 함께 조회 (최신순)
     * MultipleBagFetchException 방지를 위해 단계별 로딩
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE c.isPublic = true " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findPublicCurriculumsWithLectures();

    /**
     * ID로 커리큘럼을 강의와 함께 조회
     * MultipleBagFetchException 방지를 위해 단계별 로딩
     */
    @Query("SELECT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE c.id = :id")
    Optional<Curriculum> findByIdWithLectures(@Param("id") Long id);

    // === 작성자 관련 메서드 ===

    /**
     * 작성자 ID로 커리큘럼 조회
     */
    List<Curriculum> findByAuthorId(Long authorId);

    /**
     * 작성자 ID와 공개 여부로 커리큘럼 조회
     */
    List<Curriculum> findByAuthorIdAndIsPublicTrue(Long authorId);

    /**
     * 작성자 ID로 커리큘럼 개수 조회
     */
    long countByAuthorId(Long authorId);

    /**
     * 작성자 ID와 공개 여부로 커리큘럼 개수 조회
     */
    long countByAuthorIdAndIsPublic(Long authorId, Boolean isPublic);

    /**
     * 작성자 ID로 커리큘럼 조회 (강의 포함)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE c.author.id = :authorId " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findByAuthorIdWithLectures(@Param("authorId") Long authorId);

    /**
     * 작성자 ID로 커리큘럼 조회 (강의 포함, 페이징)
     *
     * 주의: fetch join과 페이징을 함께 사용할 때는 두 단계 쿼리 필요
     * 1단계: ID만 페이징하여 조회
     * 2단계: 조회된 ID로 fetch join
     */
    @Query(value = "SELECT c.id FROM curriculums c " +
           "WHERE c.author_id = :authorId " +
           "ORDER BY c.created_at DESC",
           countQuery = "SELECT COUNT(c.id) FROM curriculums c WHERE c.author_id = :authorId",
           nativeQuery = true)
    Page<Long> findIdsByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    /**
     * ID 리스트로 커리큘럼 조회 (강의 포함)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE c.id IN :ids " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findByIdInWithLectures(@Param("ids") List<Long> ids);

    /**
     * 작성자 ID와 공개 여부로 커리큘럼 조회 (강의 포함)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE c.author.id = :authorId AND c.isPublic = :isPublic " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findByAuthorIdAndIsPublicWithLectures(@Param("authorId") Long authorId, @Param("isPublic") Boolean isPublic);

    /**
     * 작성자 ID와 공개 여부로 커리큘럼 ID 조회 (페이징)
     */
    @Query(value = "SELECT c.id FROM curriculums c " +
           "WHERE c.author_id = :authorId AND c.is_public = :isPublic " +
           "ORDER BY c.created_at DESC",
           countQuery = "SELECT COUNT(c.id) FROM curriculums c WHERE c.author_id = :authorId AND c.is_public = :isPublic",
           nativeQuery = true)
    Page<Long> findIdsByAuthorIdAndIsPublic(@Param("authorId") Long authorId, @Param("isPublic") Boolean isPublic, Pageable pageable);

    /**
     * 제목으로 검색 (강의 포함)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findByTitleContainingIgnoreCaseWithLectures(@Param("title") String title);

    /**
     * 공개 커리큘럼에서 제목으로 검색 (강의 포함)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE c.isPublic = true AND LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findPublicByTitleContainingIgnoreCaseWithLectures(@Param("title") String title);

    /**
     * 공개 커리큘럼 ID 조회 (페이징)
     */
    @Query(value = "SELECT c.id FROM curriculums c " +
           "WHERE c.is_public = true " +
           "ORDER BY c.created_at DESC",
           countQuery = "SELECT COUNT(c.id) FROM curriculums c WHERE c.is_public = true",
           nativeQuery = true)
    Page<Long> findPublicCurriculumIds(Pageable pageable);

    /**
     * 공개 커리큘럼에서 제목으로 검색 (페이징)
     */
    @Query(value = "SELECT c.id FROM curriculums c " +
           "WHERE c.is_public = true AND LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "ORDER BY c.created_at DESC",
           countQuery = "SELECT COUNT(c.id) FROM curriculums c WHERE c.is_public = true AND LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))",
           nativeQuery = true)
    Page<Long> findPublicCurriculumIdsByTitle(@Param("title") String title, Pageable pageable);

    /**
     * 수강생 수 증가 (Atomic)
     */
    @Modifying
    @Query("UPDATE Curriculum c SET c.studentCount = c.studentCount + 1 WHERE c.id = :id")
    int incrementStudentCountAtomic(@Param("id") Long id);

    /**
     * 수강생 수 감소 (Atomic)
     */
    @Modifying
    @Query("UPDATE Curriculum c SET c.studentCount = c.studentCount - 1 WHERE c.id = :id AND c.studentCount > 0")
    int decrementStudentCountAtomic(@Param("id") Long id);

    /**
     * 공개 커리큘럼만 조회
     */
    List<Curriculum> findByIsPublicTrue();

    /**
     * 공개 커리큘럼만 조회 (작성자 포함)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.author " +
           "WHERE c.isPublic = true " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findPublicCurriculumsWithAuthor();
}
