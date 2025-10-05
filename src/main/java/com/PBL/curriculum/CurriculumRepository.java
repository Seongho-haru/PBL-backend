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
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findAllWithLectures();

    /**
     * 공개 커리큘럼만 강의와 함께 조회 (최신순)
     * MultipleBagFetchException 방지를 위해 단계별 로딩
     */
    @Query("SELECT DISTINCT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
           "WHERE c.isPublic = true " +
           "ORDER BY c.createdAt DESC")
    List<Curriculum> findPublicCurriculumsWithLectures();

    /**
     * ID로 커리큘럼을 강의와 함께 조회
     * MultipleBagFetchException 방지를 위해 단계별 로딩
     */
    @Query("SELECT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.lectures cl " +
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
}
