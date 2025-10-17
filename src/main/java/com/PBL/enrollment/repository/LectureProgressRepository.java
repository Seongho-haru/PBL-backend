package com.PBL.enrollment.repository;

import com.PBL.enrollment.entity.Enrollment;
import com.PBL.enrollment.entity.LectureProgress;
import com.PBL.enrollment.entity.ProgressStatus;
import com.PBL.lecture.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 강의 진도 Repository
 */
@Repository
public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {

    /**
     * 수강 정보와 강의로 진도 조회
     */
    Optional<LectureProgress> findByEnrollmentAndLecture(Enrollment enrollment, Lecture lecture);

    /**
     * 수강 ID와 강의 ID로 진도 조회
     */
    @Query("SELECT lp FROM LectureProgress lp WHERE lp.enrollment.id = :enrollmentId AND lp.lecture.id = :lectureId")
    Optional<LectureProgress> findByEnrollmentIdAndLectureId(@Param("enrollmentId") Long enrollmentId, @Param("lectureId") Long lectureId);

    /**
     * 수강별 모든 강의 진도 조회
     */
    List<LectureProgress> findByEnrollmentOrderByCurriculumLectureOrderIndex(Enrollment enrollment);

    /**
     * 수강 ID로 모든 강의 진도 조회
     */
    @Query("SELECT lp FROM LectureProgress lp WHERE lp.enrollment.id = :enrollmentId ORDER BY lp.curriculumLecture.orderIndex")
    List<LectureProgress> findByEnrollmentIdOrderByOrder(@Param("enrollmentId") Long enrollmentId);

    /**
     * 수강별 상태별 강의 진도 조회
     */
    List<LectureProgress> findByEnrollmentAndStatus(Enrollment enrollment, ProgressStatus status);

    /**
     * 수강 ID와 상태로 강의 진도 조회
     */
    @Query("SELECT lp FROM LectureProgress lp WHERE lp.enrollment.id = :enrollmentId AND lp.status = :status")
    List<LectureProgress> findByEnrollmentIdAndStatus(@Param("enrollmentId") Long enrollmentId, @Param("status") ProgressStatus status);

    /**
     * 수강별 완료된 강의 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.enrollment = :enrollment AND lp.status = 'COMPLETED'")
    long countByEnrollmentAndStatusCompleted(@Param("enrollment") Enrollment enrollment);

    /**
     * 수강 ID로 완료된 강의 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.enrollment.id = :enrollmentId AND lp.status = 'COMPLETED'")
    long countByEnrollmentIdAndStatusCompleted(@Param("enrollmentId") Long enrollmentId);

    /**
     * 수강별 전체 강의 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.enrollment = :enrollment")
    long countByEnrollment(@Param("enrollment") Enrollment enrollment);

    /**
     * 수강 ID로 전체 강의 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.enrollment.id = :enrollmentId")
    long countByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    /**
     * 강의별 수강자 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.lecture = :lecture")
    long countByLecture(@Param("lecture") Lecture lecture);

    /**
     * 강의 ID로 수강자 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.lecture.id = :lectureId")
    long countByLectureId(@Param("lectureId") Long lectureId);

    /**
     * 강의별 완료자 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.lecture = :lecture AND lp.status = 'COMPLETED'")
    long countByLectureAndStatusCompleted(@Param("lecture") Lecture lecture);

    /**
     * 강의 ID로 완료자 수 조회
     */
    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.lecture.id = :lectureId AND lp.status = 'COMPLETED'")
    long countByLectureIdAndStatusCompleted(@Param("lectureId") Long lectureId);
}
