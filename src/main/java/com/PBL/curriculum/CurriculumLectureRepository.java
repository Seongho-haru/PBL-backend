package com.PBL.curriculum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커리큘럼-강의 연결 Repository
 */
@Repository
public interface CurriculumLectureRepository extends JpaRepository<CurriculumLecture, Long> {

    /**
     * 특정 커리큘럼의 모든 강의 조회 (순서대로)
     */
    List<CurriculumLecture> findByCurriculumIdOrderByOrderIndexAsc(Long curriculumId);

    /**
     * 특정 강의를 포함하는 모든 커리큘럼 연결 조회
     */
    List<CurriculumLecture> findByLectureId(Long lectureId);

    /**
     * 특정 커리큘럼에서 특정 강의 연결 조회
     */
    @Query("SELECT cl FROM CurriculumLecture cl WHERE cl.curriculum.id = :curriculumId AND cl.lectureId = :lectureId")
    CurriculumLecture findByCurriculumIdAndLectureId(@Param("curriculumId") Long curriculumId, 
                                                      @Param("lectureId") Long lectureId);

    /**
     * 특정 강의가 삭제될 때 모든 커리큘럼에서 해당 강의 제거
     */
    @Modifying
    @Query("DELETE FROM CurriculumLecture cl WHERE cl.lectureId = :lectureId")
    void deleteByLectureId(@Param("lectureId") Long lectureId);

    /**
     * 특정 커리큘럼의 강의 개수 조회
     */
    @Query("SELECT COUNT(cl) FROM CurriculumLecture cl WHERE cl.curriculum.id = :curriculumId")
    int countByCurriculumId(@Param("curriculumId") Long curriculumId);

    /**
     * 특정 커리큘럼의 필수 강의 개수 조회
     */
    @Query("SELECT COUNT(cl) FROM CurriculumLecture cl WHERE cl.curriculum.id = :curriculumId AND cl.isRequired = true")
    int countRequiredByCurriculumId(@Param("curriculumId") Long curriculumId);
}
