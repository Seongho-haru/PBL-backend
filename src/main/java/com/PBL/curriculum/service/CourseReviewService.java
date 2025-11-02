package com.PBL.curriculum.service;

import com.PBL.curriculum.dto.CourseReviewDTOs;
import com.PBL.curriculum.entity.CourseReview;
import com.PBL.curriculum.Curriculum;
import com.PBL.curriculum.CurriculumService;
import com.PBL.curriculum.repository.CourseReviewRepository;
import com.PBL.curriculum.CurriculumRepository;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import com.PBL.user.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰/문의 Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final CurriculumRepository curriculumRepository;
    private final UserRepository userRepository;
    private final CurriculumService curriculumService;
    private final UserValidationService userValidationService;

    /**
     * 리뷰 작성
     */
    public CourseReviewDTOs.CourseReviewResponse createReview(Long curriculumId, Long userId, CourseReviewDTOs.CreateCourseReviewRequest request) {
        log.info("리뷰 작성 요청 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);

        // 정지 상태 체크
        userValidationService.validateUserCanCreateContent(userId);

        // 커리큘럼 조회
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId));

        // 사용자 조회
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // 리뷰는 항상 공개
        CourseReview review = CourseReview.builder()
                .curriculum(curriculum)
                .author(author)
                .isReview(true)
                .rating(request.getRating())
                .content(request.getContent())
                .isPublic(true)
                .build();

        CourseReview saved = courseReviewRepository.save(review);
        log.info("리뷰 작성 완료 - ID: {}", saved.getId());

        // 커리큘럼 평균 평점 업데이트
        updateCurriculumAverageRating(curriculumId);

        return CourseReviewDTOs.CourseReviewResponse.from(saved);
    }

    /**
     * 문의 작성
     */
    public CourseReviewDTOs.CourseReviewResponse createInquiry(Long curriculumId, Long userId, CourseReviewDTOs.CreateCourseReviewRequest request) {
        log.info("문의 작성 요청 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);

        // 정지 상태 체크
        userValidationService.validateUserCanCreateContent(userId);

        // 커리큘럼 조회
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("커리큘럼을 찾을 수 없습니다: " + curriculumId));

        // 사용자 조회
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // 문의 생성
        CourseReview inquiry = CourseReview.builder()
                .curriculum(curriculum)
                .author(author)
                .isReview(false)
                .rating(null) // 문의는 별점 없음
                .content(request.getContent())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        CourseReview saved = courseReviewRepository.save(inquiry);
        log.info("문의 작성 완료 - ID: {}", saved.getId());

        return CourseReviewDTOs.CourseReviewResponse.from(saved);
    }

    /**
     * 리뷰 수정
     */
    public CourseReviewDTOs.CourseReviewResponse updateReview(Long curriculumId, Long reviewId, Long userId, CourseReviewDTOs.UpdateCourseReviewRequest request) {
        log.info("리뷰 수정 요청 - 리뷰 ID: {}, 사용자 ID: {}", reviewId, userId);

        // 정지 상태 체크
        userValidationService.validateUserCanModifyContent(userId);

        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다: " + reviewId));

        // 작성자 본인만 수정 가능
        if (!review.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 리뷰 타입 체크
        if (!review.getIsReview()) {
            throw new RuntimeException("문의는 수정할 수 없습니다.");
        }

        // 수정 내용 반영
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        review.setUpdatedAt(LocalDateTime.now());

        CourseReview saved = courseReviewRepository.save(review);
        log.info("리뷰 수정 완료 - ID: {}", saved.getId());

        // 커리큘럼 평균 평점 업데이트
        updateCurriculumAverageRating(curriculumId);

        return CourseReviewDTOs.CourseReviewResponse.from(saved);
    }

    /**
     * 문의 수정
     */
    public CourseReviewDTOs.CourseReviewResponse updateInquiry(Long curriculumId, Long inquiryId, Long userId, CourseReviewDTOs.UpdateCourseReviewRequest request) {
        log.info("문의 수정 요청 - 문의 ID: {}, 사용자 ID: {}", inquiryId, userId);

        // 정지 상태 체크
        userValidationService.validateUserCanModifyContent(userId);

        CourseReview inquiry = courseReviewRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다: " + inquiryId));

        // 작성자 본인만 수정 가능
        if (!inquiry.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 문의만 수정할 수 있습니다.");
        }

        // 문의 타입 체크
        if (inquiry.getIsReview()) {
            throw new RuntimeException("리뷰는 수정할 수 없습니다.");
        }

        // 수정 내용 반영
        if (request.getContent() != null) {
            inquiry.setContent(request.getContent());
        }
        if (request.getIsPublic() != null) {
            inquiry.setIsPublic(request.getIsPublic());
        }
        inquiry.setUpdatedAt(LocalDateTime.now());

        CourseReview saved = courseReviewRepository.save(inquiry);
        log.info("문의 수정 완료 - ID: {}", saved.getId());

        return CourseReviewDTOs.CourseReviewResponse.from(saved);
    }

    /**
     * 리뷰/문의 삭제
     */
    public void deleteCourseReview(Long reviewId, Long userId) {
        log.info("리뷰/문의 삭제 요청 - ID: {}, 사용자 ID: {}", reviewId, userId);

        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰/문의를 찾을 수 없습니다: " + reviewId));

        // 작성자 본인만 삭제 가능
        if (!review.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 리뷰/문의만 삭제할 수 있습니다.");
        }

        Long curriculumId = review.getCurriculum().getId();
        courseReviewRepository.delete(review);
        log.info("리뷰/문의 삭제 완료 - ID: {}", reviewId);

        // 리뷰인 경우 평균 평점 업데이트
        if (review.getIsReview()) {
            updateCurriculumAverageRating(curriculumId);
        }
    }

    /**
     * 커리큘럼의 리뷰 목록 조회 (공개만)
     */
    @Transactional(readOnly = true)
    public Page<CourseReviewDTOs.CourseReviewResponse> getCurriculumReviews(Long curriculumId, Pageable pageable) {
        log.info("커리큘럼 리뷰 목록 조회 - 커리큘럼 ID: {}", curriculumId);

        Page<CourseReview> reviews = courseReviewRepository.findByCurriculumIdAndIsReviewOrderByCreatedAtDesc(curriculumId, pageable);

        return reviews.map(CourseReviewDTOs.CourseReviewResponse::from);
    }

    /**
     * 커리큘럼의 문의 목록 조회
     * - 일반 사용자 (userId가 null이거나 권한 없음): 공개 문의만 조회
     * - 관리자 (userId = 1) 또는 커리큘럼 작성자: 공개 + 비공개 문의 모두 조회
     */
    @Transactional(readOnly = true)
    public Page<CourseReviewDTOs.CourseReviewResponse> getCurriculumInquiries(
            Long curriculumId, Long userId, Pageable pageable) {
        log.info("커리큘럼 문의 목록 조회 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);

        Page<CourseReview> inquiries;
        
        // 사용자 ID가 있고 (관리자이거나 커리큘럼 작성자인 경우) 비공개 문의도 조회 가능
        if (userId != null && (userId == 1L || isCurriculumAuthor(curriculumId, userId))) {
            log.info("관리자 또는 커리큘럼 작성자로 인식 - 공개 및 비공개 문의 모두 조회");
            inquiries = courseReviewRepository.findInquiriesAccessibleByUser(curriculumId, userId, pageable);
        } else {
            // 일반 사용자는 공개 문의만 조회
            inquiries = courseReviewRepository.findByCurriculumIdAndIsInquiryPublicOrderByCreatedAtDesc(curriculumId, pageable);
        }

        return inquiries.map(CourseReviewDTOs.CourseReviewResponse::from);
    }

    /**
     * 특정 사용자가 커리큘럼의 작성자인지 확인
     */
    private boolean isCurriculumAuthor(Long curriculumId, Long userId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId).orElse(null);
        return curriculum != null && curriculum.isAuthor(userId);
    }

    /**
     * 사용자의 내 리뷰 조회
     */
    @Transactional(readOnly = true)
    public CourseReviewDTOs.CourseReviewResponse getMyReview(Long curriculumId, Long userId) {
        log.info("내 리뷰 조회 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);

        CourseReview review = courseReviewRepository.findReviewByCurriculumIdAndUserId(curriculumId, userId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        return CourseReviewDTOs.CourseReviewResponse.from(review);
    }

    /**
     * 사용자의 내 문의 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CourseReviewDTOs.CourseReviewResponse> getMyInquiries(Long curriculumId, Long userId) {
        log.info("내 문의 목록 조회 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);

        List<CourseReview> inquiries = courseReviewRepository.findInquiriesByCurriculumIdAndUserId(curriculumId, userId);

        return inquiries.stream()
                .map(CourseReviewDTOs.CourseReviewResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 커리큘럼 평균 평점 조회
     */
    @Transactional(readOnly = true)
    public CourseReviewDTOs.AverageRatingResponse getCurriculumAverageRating(Long curriculumId) {
        log.info("커리큘럼 평균 평점 조회 - 커리큘럼 ID: {}", curriculumId);
        
        Double avgRating = courseReviewRepository.calculateAverageRating(curriculumId);
        Long reviewCount = courseReviewRepository.countByCurriculumId(curriculumId);
        
        if (avgRating == null || reviewCount == 0) {
            return new CourseReviewDTOs.AverageRatingResponse(curriculumId, 0.0, 0L);
        }
        
        return new CourseReviewDTOs.AverageRatingResponse(curriculumId, avgRating, reviewCount);
    }

    /**
     * 커리큘럼 평균 평점 업데이트
     */
    private void updateCurriculumAverageRating(Long curriculumId) {
        try {
            Double avgRating = courseReviewRepository.calculateAverageRating(curriculumId);
            if (avgRating != null) {
                curriculumService.updateAverageRating(curriculumId, BigDecimal.valueOf(avgRating));
                log.debug("커리큘럼 {} 평균 평점 업데이트: {}", curriculumId, avgRating);
            }
        } catch (Exception e) {
            log.error("커리큘럼 평균 평점 업데이트 실패: {}", e.getMessage());
        }
    }
}

