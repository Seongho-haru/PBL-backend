package com.PBL.enrollment.service;

import com.PBL.curriculum.Curriculum;
import com.PBL.curriculum.CurriculumLecture;
import com.PBL.curriculum.CurriculumService;
import com.PBL.enrollment.dto.EnrollmentDTOs;
import com.PBL.enrollment.entity.Enrollment;
import com.PBL.enrollment.entity.EnrollmentStatus;
import com.PBL.enrollment.entity.LectureProgress;
import com.PBL.enrollment.entity.ProgressStatus;
import com.PBL.enrollment.repository.EnrollmentRepository;
import com.PBL.enrollment.repository.LectureProgressRepository;
import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureService;
import com.PBL.user.User;
import com.PBL.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * 수강 관리 서비스
 * 수강 신청/취소, 진도 관리, 조회 기능 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final UserService userService;
    private final CurriculumService curriculumService;
    private final LectureService lectureService;

    // === 수강 신청/취소 ===

    /**
     * 커리큘럼 수강 신청
     */
    @Transactional
    public Enrollment enrollInCurriculum(Long userId, Long curriculumId) {
        log.info("수강 신청 시작 - 사용자 ID: {}, 커리큘럼 ID: {}", userId, curriculumId);

        // 1. 사용자 조회
        User user = userService.findUserById(userId);

        // 2. 커리큘럼 조회 (권한 체크 포함)
        Curriculum curriculum = curriculumService.getCurriculumByIdWithPermission(curriculumId, userId)
                .orElseThrow(() -> new IllegalArgumentException("커리큘럼을 찾을 수 없습니다: " + curriculumId));

        // 3. 수강 가능 여부 검증 (작성자 체크 포함)
        validateEnrollmentEligibility(user, curriculum);

        // 4. 중복 수강 방지
        if (enrollmentRepository.existsByUserAndCurriculum(user, curriculum)) {
            throw new IllegalStateException("이미 수강 중인 커리큘럼입니다.");
        }

        // 5. 수강 생성
        Enrollment enrollment = new Enrollment(user, curriculum);
        enrollment = enrollmentRepository.save(enrollment);

        // 6. 강의별 진도 초기화
        initializeLectureProgress(enrollment, curriculum);

        log.info("수강 신청 완료 - 수강 ID: {}", enrollment.getId());
        return enrollment;
    }

    /**
     * 수강 취소
     */
    @Transactional
    public void cancelEnrollment(Long userId, Long enrollmentId) {
        log.info("수강 취소 시작 - 사용자 ID: {}, 수강 ID: {}", userId, enrollmentId);

        // 1. 수강 정보 조회 및 권한 확인
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다: " + enrollmentId));

        if (!enrollment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 수강 정보만 취소할 수 있습니다.");
        }

        // 2. 강의 진도 삭제
        List<LectureProgress> lectureProgresses = lectureProgressRepository.findByEnrollmentIdOrderByOrder(enrollment.getId());
        lectureProgressRepository.deleteAll(lectureProgresses);

        // 3. 수강 정보 삭제
        enrollmentRepository.delete(enrollment);

        log.info("수강 취소 완료 - 수강 ID: {}", enrollmentId);
    }

    // === 진도 관리 ===

    /**
     * 강의 진도 업데이트 (마크다운 강의 읽기 완료)
     */
    @Transactional
    public void markLectureAsRead(Long userId, Long enrollmentId, Long lectureId) {
        log.info("강의 읽기 완료 처리 - 사용자 ID: {}, 수강 ID: {}, 강의 ID: {}", userId, enrollmentId, lectureId);

        // 1. 수강 정보 조회 및 권한 확인
        Enrollment enrollment = getEnrollmentWithPermissionCheck(userId, enrollmentId);

        // 2. 강의 진도 조회
        LectureProgress lectureProgress = lectureProgressRepository
                .findByEnrollmentAndLecture(enrollment, lectureService.getLectureById(lectureId)
                        .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId)))
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 진도 정보를 찾을 수 없습니다."));

        // 3. 마크다운 강의 읽기 완료 처리
        lectureProgress.markAsRead();
        lectureProgressRepository.save(lectureProgress);

        // 4. 수강 진도율 업데이트
        updateEnrollmentProgress(enrollment);

        log.info("강의 읽기 완료 처리 완료 - 강의 ID: {}", lectureId);
    }

    /**
     * 강의 진도 업데이트 (문제 강의 정답 제출)
     */
    @Transactional
    public void markLectureAsSolved(Long userId, Long enrollmentId, Long lectureId) {
        log.info("문제 강의 정답 제출 처리 - 사용자 ID: {}, 수강 ID: {}, 강의 ID: {}", userId, enrollmentId, lectureId);

        // 1. 수강 정보 조회 및 권한 확인
        Enrollment enrollment = getEnrollmentWithPermissionCheck(userId, enrollmentId);

        // 2. 강의 진도 조회
        LectureProgress lectureProgress = lectureProgressRepository
                .findByEnrollmentAndLecture(enrollment, lectureService.getLectureById(lectureId)
                        .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId)))
                .orElseThrow(() -> new IllegalArgumentException("해당 강의의 진도 정보를 찾을 수 없습니다."));

        // 3. 문제 강의 정답 제출 처리
        lectureProgress.markAsSolved();
        lectureProgressRepository.save(lectureProgress);

        // 4. 수강 진도율 업데이트
        updateEnrollmentProgress(enrollment);

        log.info("문제 강의 정답 제출 처리 완료 - 강의 ID: {}", lectureId);
    }

    // === 조회 기능 ===

    /**
     * 사용자별 수강 목록 조회
     */
    public List<Enrollment> getUserEnrollments(Long userId) {
        log.info("사용자 수강 목록 조회 - 사용자 ID: {}", userId);
        return enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
    }

    /**
     * 사용자별 수강 상태별 목록 조회
     */
    public List<Enrollment> getUserEnrollmentsByStatus(Long userId, EnrollmentStatus status) {
        log.info("사용자 수강 목록 조회 (상태별) - 사용자 ID: {}, 상태: {}", userId, status);
        return enrollmentRepository.findByUserIdAndStatusOrderByEnrolledAtDesc(userId, status);
    }

    /**
     * 수강 상세 정보 조회 (강의별 진도 포함)
     */
    public EnrollmentDTOs.EnrollmentDetailResponse getEnrollmentDetail(Long userId, Long enrollmentId) {
        log.info("수강 상세 정보 조회 - 사용자 ID: {}, 수강 ID: {}", userId, enrollmentId);

        // 1. 수강 정보 조회 및 권한 확인
        Enrollment enrollment = getEnrollmentWithPermissionCheck(userId, enrollmentId);

        // 2. 강의별 진도 조회
        List<LectureProgress> lectureProgresses = lectureProgressRepository
                .findByEnrollmentIdOrderByOrder(enrollmentId);

        return EnrollmentDTOs.EnrollmentDetailResponse.from(enrollment, lectureProgresses);
    }

    /**
     * 수강 정보 조회 (권한 확인 포함)
     */
    public Optional<Enrollment> getEnrollment(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }

    // === 통계 기능 ===

    /**
     * 커리큘럼별 수강자 수 조회
     */
    public long getEnrollmentCount(Long curriculumId) {
        return enrollmentRepository.countByCurriculumId(curriculumId);
    }

    /**
     * 커리큘럼별 완료자 수 조회
     */
    public long getCompletedCount(Long curriculumId) {
        return enrollmentRepository.countByCurriculumIdAndStatusCompleted(curriculumId);
    }

    // === 비즈니스 로직 메서드 ===

    /**
     * 수강 가능 여부 검증
     */
    private void validateEnrollmentEligibility(User user, Curriculum curriculum) {
        // 1. 공개 커리큘럼만 수강 가능
        if (!curriculum.getIsPublic()) {
            throw new IllegalStateException("공개되지 않은 커리큘럼은 수강할 수 없습니다.");
        }

        // 2. 작성자는 자신의 커리큘럼 수강 불가
        if (curriculum.getAuthor() != null && curriculum.getAuthor().getId().equals(user.getId())) {
            throw new IllegalStateException("본인이 작성한 커리큘럼은 수강할 수 없습니다.");
        }
    }

    /**
     * 강의별 진도 초기화
     */
    private void initializeLectureProgress(Enrollment enrollment, Curriculum curriculum) {
        List<CurriculumLecture> curriculumLectures = curriculum.getLectures();
        
        for (CurriculumLecture curriculumLecture : curriculumLectures) {
            LectureProgress lectureProgress = new LectureProgress(
                    enrollment, 
                    curriculumLecture.getLecture(), 
                    curriculumLecture
            );
            lectureProgressRepository.save(lectureProgress);
        }
        
        log.info("강의별 진도 초기화 완료 - 강의 수: {}", curriculumLectures.size());
    }

    /**
     * 수강 진도율 업데이트
     */
    private void updateEnrollmentProgress(Enrollment enrollment) {
        long totalLectures = lectureProgressRepository.countByEnrollment(enrollment);
        long completedLectures = lectureProgressRepository.countByEnrollmentAndStatusCompleted(enrollment);
        
        int progressPercentage = totalLectures > 0 ? (int) ((completedLectures * 100) / totalLectures) : 0;
        
        // 진도율 업데이트
        enrollment.setProgressPercentage(progressPercentage);
        
        // 모든 강의가 완료되었는지 확인하여 수강 상태 업데이트
        if (totalLectures > 0 && completedLectures == totalLectures) {
            // 모든 강의가 완료된 경우 수강 완료 처리
            enrollment.complete();
            log.info("수강 완료 - 수강 ID: {}, 사용자 ID: {}, 커리큘럼 ID: {}", 
                    enrollment.getId(), enrollment.getUser().getId(), enrollment.getCurriculum().getId());
        }
        
        enrollmentRepository.save(enrollment);
        
        log.debug("수강 진도율 업데이트 - 수강 ID: {}, 진도율: {}%, 완료 강의: {}/{}", 
                enrollment.getId(), progressPercentage, completedLectures, totalLectures);
    }

    /**
     * 사용자별 특정 강의 완료 상태 조회
     */
    public boolean isLectureCompletedByUser(Long userId, Long lectureId) {
        log.info("사용자 강의 완료 상태 조회 - 사용자 ID: {}, 강의 ID: {}", userId, lectureId);
        
        // 사용자의 모든 수강 정보에서 해당 강의의 완료 상태 확인
        List<Enrollment> userEnrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        
        for (Enrollment enrollment : userEnrollments) {
            Optional<LectureProgress> lectureProgress = lectureProgressRepository
                    .findByEnrollmentAndLecture(enrollment, lectureService.getLectureById(lectureId)
                            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId)));
            
            if (lectureProgress.isPresent() && lectureProgress.get().getStatus() == ProgressStatus.COMPLETED) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 사용자별 강의 완료 상태 상세 조회
     */
    public List<EnrollmentDTOs.LectureProgressResponse> getUserLectureProgress(Long userId, Long lectureId) {
        log.info("사용자 강의 진도 상세 조회 - 사용자 ID: {}, 강의 ID: {}", userId, lectureId);
        
        List<EnrollmentDTOs.LectureProgressResponse> responses = new ArrayList<>();
        List<Enrollment> userEnrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        
        for (Enrollment enrollment : userEnrollments) {
            Optional<LectureProgress> lectureProgress = lectureProgressRepository
                    .findByEnrollmentAndLecture(enrollment, lectureService.getLectureById(lectureId)
                            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId)));
            
            if (lectureProgress.isPresent()) {
                responses.add(EnrollmentDTOs.LectureProgressResponse.from(lectureProgress.get()));
            }
        }
        
        return responses;
    }

    /**
     * 수강 정보 조회 (권한 확인 포함)
     */
    private Enrollment getEnrollmentWithPermissionCheck(Long userId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다: " + enrollmentId));

        if (!enrollment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 수강 정보만 조회할 수 있습니다.");
        }

        return enrollment;
    }
}
