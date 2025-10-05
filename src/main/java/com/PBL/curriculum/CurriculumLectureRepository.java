package com.PBL.curriculum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커리큘럼 강의 Repository
 */
@Repository
public interface CurriculumLectureRepository extends JpaRepository<CurriculumLecture, Long> {

    /**
     * 특정 섹션의 강의들을 순서대로 조회
     */
    List<CurriculumLecture> findBySectionIdOrderByOrderIndexAsc(Long sectionId);

    /**
     * 특정 섹션의 강의 수 조회
     */
    long countBySectionId(Long sectionId);

    /**
     * 특정 강의가 사용된 모든 커리큘럼 강의 조회
     */
    List<CurriculumLecture> findByLectureId(Long lectureId);

    /**
     * 특정 커리큘럼에서 특정 강의가 사용된 횟수
     */
    @Query("SELECT COUNT(cl) FROM CurriculumLecture cl " +
           "WHERE cl.section.curriculum.id = :curriculumId " +
           "AND cl.lecture.id = :lectureId")
    long countByCurriculumIdAndLectureId(@Param("curriculumId") Long curriculumId, 
                                        @Param("lectureId") Long lectureId);

    /**
     * 특정 커리큘럼의 모든 강의 조회 (섹션 순서, 강의 순서대로)
     */
    @Query("SELECT cl FROM CurriculumLecture cl " +
           "WHERE cl.section.curriculum.id = :curriculumId " +
           "ORDER BY cl.section.orderIndex ASC, cl.orderIndex ASC")
    List<CurriculumLecture> findByCurriculumIdOrderBySectionAndLecture(@Param("curriculumId") Long curriculumId);

    /**
     * 필수 강의만 조회
     */
    List<CurriculumLecture> findBySectionIdAndIsRequiredTrueOrderByOrderIndexAsc(Long sectionId);

    /**
     * 선택 강의만 조회
     */
    List<CurriculumLecture> findBySectionIdAndIsRequiredFalseOrderByOrderIndexAsc(Long sectionId);
}
