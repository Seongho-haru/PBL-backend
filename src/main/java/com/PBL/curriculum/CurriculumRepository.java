package com.PBL.curriculum;

import org.springframework.data.jpa.repository.JpaRepository;
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

    /**
     * 공개 커리큘럼만 조회
     */
    List<Curriculum> findByIsPublicTrueOrderByCreatedAtDesc();

    /**
     * 제목으로 검색 (부분 일치)
     */
    List<Curriculum> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    /**
     * 공개 커리큘럼에서 제목으로 검색
     */
    List<Curriculum> findByIsPublicTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

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
     * 작성자 ID와 공개 여부로 커리큘럼 조회 (강의 포함)
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "LEFT JOIN FETCH cl.lecture " +
           "WHERE c.author.id = :authorId AND c.isPublic = :isPublic " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findByAuthorIdAndIsPublicWithLectures(@Param("authorId") Long authorId, @Param("isPublic") Boolean isPublic);
}
