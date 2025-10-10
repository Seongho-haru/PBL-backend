package com.PBL.enrollment.repository;

import com.PBL.enrollment.entity.Enrollment;
import com.PBL.enrollment.entity.EnrollmentStatus;
import com.PBL.curriculum.Curriculum;
import com.PBL.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 수강 Repository
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * 사용자와 커리큘럼으로 수강 정보 조회
     */
    Optional<Enrollment> findByUserAndCurriculum(User user, Curriculum curriculum);

    /**
     * 사용자 ID와 커리큘럼 ID로 수강 정보 조회
     */
    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.curriculum.id = :curriculumId")
    Optional<Enrollment> findByUserIdAndCurriculumId(@Param("userId") Long userId, @Param("curriculumId") Long curriculumId);

    /**
     * 사용자별 수강 목록 조회
     */
    List<Enrollment> findByUserOrderByEnrolledAtDesc(User user);

    /**
     * 사용자 ID로 수강 목록 조회 (User, Curriculum 엔티티 포함)
     */
    @Query("SELECT e FROM Enrollment e LEFT JOIN FETCH e.user LEFT JOIN FETCH e.curriculum WHERE e.user.id = :userId ORDER BY e.enrolledAt DESC")
    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(@Param("userId") Long userId);

    /**
     * 사용자별 수강 상태별 목록 조회
     */
    List<Enrollment> findByUserAndStatusOrderByEnrolledAtDesc(User user, EnrollmentStatus status);

    /**
     * 사용자 ID와 상태로 수강 목록 조회 (User, Curriculum 엔티티 포함)
     */
    @Query("SELECT e FROM Enrollment e LEFT JOIN FETCH e.user LEFT JOIN FETCH e.curriculum WHERE e.user.id = :userId AND e.status = :status ORDER BY e.enrolledAt DESC")
    List<Enrollment> findByUserIdAndStatusOrderByEnrolledAtDesc(@Param("userId") Long userId, @Param("status") EnrollmentStatus status);

    /**
     * 커리큘럼별 수강자 수 조회
     */
    long countByCurriculum(Curriculum curriculum);

    /**
     * 커리큘럼 ID로 수강자 수 조회
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.curriculum.id = :curriculumId")
    long countByCurriculumId(@Param("curriculumId") Long curriculumId);

    /**
     * 커리큘럼별 완료자 수 조회
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.curriculum = :curriculum AND e.status = 'COMPLETED'")
    long countByCurriculumAndStatusCompleted(@Param("curriculum") Curriculum curriculum);

    /**
     * 커리큘럼 ID로 완료자 수 조회
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.curriculum.id = :curriculumId AND e.status = com.PBL.enrollment.entity.EnrollmentStatus.COMPLETED")
    long countByCurriculumIdAndStatusCompleted(@Param("curriculumId") Long curriculumId);

    /**
     * 사용자가 특정 커리큘럼을 수강 중인지 확인
     */
    boolean existsByUserAndCurriculum(User user, Curriculum curriculum);

    /**
     * 사용자 ID와 커리큘럼 ID로 수강 중인지 확인
     */
    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.user.id = :userId AND e.curriculum.id = :curriculumId")
    boolean existsByUserIdAndCurriculumId(@Param("userId") Long userId, @Param("curriculumId") Long curriculumId);
}
