package com.PBL.curriculum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커리큘럼 섹션 Repository
 */
@Repository
public interface CurriculumSectionRepository extends JpaRepository<CurriculumSection, Long> {

    /**
     * 특정 커리큘럼의 섹션들을 순서대로 조회
     */
    List<CurriculumSection> findByCurriculumIdOrderByOrderIndexAsc(Long curriculumId);

    /**
     * 특정 커리큘럼의 섹션 수 조회
     */
    long countByCurriculumId(Long curriculumId);

    /**
     * 강의를 포함한 섹션 조회
     */
    @Query("SELECT DISTINCT s FROM CurriculumSection s " +
           "LEFT JOIN FETCH s.lectures cl " +
           "LEFT JOIN FETCH cl.lecture l " +
           "WHERE s.id = :sectionId")
    CurriculumSection findByIdWithLectures(@Param("sectionId") Long sectionId);

    /**
     * 특정 커리큘럼의 모든 섹션을 강의와 함께 조회
     */
    @Query("SELECT DISTINCT s FROM CurriculumSection s " +
           "LEFT JOIN FETCH s.lectures cl " +
           "LEFT JOIN FETCH cl.lecture l " +
           "WHERE s.curriculum.id = :curriculumId " +
           "ORDER BY s.orderIndex ASC")
    List<CurriculumSection> findByCurriculumIdWithLectures(@Param("curriculumId") Long curriculumId);
}
