package com.PBL.curriculum.controller;

import com.PBL.curriculum.dto.CourseReviewDTOs;
import com.PBL.curriculum.service.CourseReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 리뷰/문의 Controller
 */
@RestController
@RequestMapping("/api/curriculums/{curriculumId}/reviews")
@Tag(name = "Course Review & Inquiry", description = "커리큘럼 리뷰 및 문의 API")
@Slf4j
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    /**
     * 리뷰 작성
     */
    @PostMapping
    @Operation(summary = "리뷰 작성", description = "커리큘럼에 리뷰를 작성합니다.")
    public ResponseEntity<CourseReviewDTOs.CourseReviewResponse> createReview(
            @PathVariable Long curriculumId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CourseReviewDTOs.CreateCourseReviewRequest request
    ) {
        log.info("리뷰 작성 요청 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);
        CourseReviewDTOs.CourseReviewResponse response = courseReviewService.createReview(
                curriculumId, userId, request
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 문의 작성
     */
    @PostMapping("/inquiries")
    @Operation(summary = "문의 작성", description = "커리큘럼에 문의를 작성합니다.")
    public ResponseEntity<CourseReviewDTOs.CourseReviewResponse> createInquiry(
            @PathVariable Long curriculumId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CourseReviewDTOs.CreateCourseReviewRequest request
    ) {
        log.info("문의 작성 요청 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);
        CourseReviewDTOs.CourseReviewResponse response = courseReviewService.createInquiry(
                curriculumId, userId, request
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰 수정
     */
    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    public ResponseEntity<CourseReviewDTOs.CourseReviewResponse> updateReview(
            @PathVariable Long curriculumId,
            @PathVariable Long reviewId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CourseReviewDTOs.UpdateCourseReviewRequest request
    ) {
        log.info("리뷰 수정 요청 - 리뷰 ID: {}, 사용자 ID: {}", reviewId, userId);
        CourseReviewDTOs.CourseReviewResponse response = courseReviewService.updateReview(
                curriculumId, reviewId, userId, request
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 문의 수정
     */
    @PutMapping("/inquiries/{inquiryId}")
    @Operation(summary = "문의 수정", description = "작성한 문의를 수정합니다.")
    public ResponseEntity<CourseReviewDTOs.CourseReviewResponse> updateInquiry(
            @PathVariable Long curriculumId,
            @PathVariable Long inquiryId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CourseReviewDTOs.UpdateCourseReviewRequest request
    ) {
        log.info("문의 수정 요청 - 문의 ID: {}, 사용자 ID: {}", inquiryId, userId);
        CourseReviewDTOs.CourseReviewResponse response = courseReviewService.updateInquiry(
                curriculumId, inquiryId, userId, request
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰/문의 삭제
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰/문의 삭제", description = "작성한 리뷰/문의를 삭제합니다.")
    public ResponseEntity<Void> deleteCourseReview(
            @PathVariable Long curriculumId,
            @PathVariable Long reviewId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("리뷰/문의 삭제 요청 - ID: {}, 사용자 ID: {}", reviewId, userId);
        courseReviewService.deleteCourseReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 커리큘럼 리뷰 목록 조회
     */
    @GetMapping
    @Operation(summary = "커리큘럼 리뷰 목록 조회", description = "커리큘럼의 공개 리뷰 목록을 조회합니다.")
    public ResponseEntity<Page<CourseReviewDTOs.CourseReviewResponse>> getCurriculumReviews(
            @PathVariable Long curriculumId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        log.info("커리큘럼 리뷰 목록 조회 - 커리큘럼 ID: {}", curriculumId);
        Page<CourseReviewDTOs.CourseReviewResponse> reviews = courseReviewService.getCurriculumReviews(
                curriculumId, pageable
        );
        return ResponseEntity.ok(reviews);
    }

    /**
     * 커리큘럼 문의 목록 조회
     * - 일반 사용자: 공개 문의만 조회
     * - 관리자(userId=1) 또는 커리큘럼 작성자: 공개 + 비공개 문의 모두 조회
     */
    @GetMapping("/inquiries")
    @Operation(summary = "커리큘럼 문의 목록 조회", 
               description = "커리큘럼의 문의 목록을 조회합니다. " +
                           "일반 사용자는 공개 문의만, 관리자(userId=1) 또는 커리큘럼 작성자는 비공개 문의도 조회할 수 있습니다.")
    public ResponseEntity<Page<CourseReviewDTOs.CourseReviewResponse>> getCurriculumInquiries(
            @PathVariable Long curriculumId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        log.info("커리큘럼 문의 목록 조회 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);
        Page<CourseReviewDTOs.CourseReviewResponse> inquiries = courseReviewService.getCurriculumInquiries(
                curriculumId, userId, pageable
        );
        return ResponseEntity.ok(inquiries);
    }

    /**
     * 내 리뷰 조회
     */
    @GetMapping("/my")
    @Operation(summary = "내 리뷰 조회", description = "특정 커리큘럼에 내가 작성한 리뷰를 조회합니다.")
    public ResponseEntity<CourseReviewDTOs.CourseReviewResponse> getMyReview(
            @PathVariable Long curriculumId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("내 리뷰 조회 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);
        try {
            CourseReviewDTOs.CourseReviewResponse review = courseReviewService.getMyReview(
                    curriculumId, userId
            );
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            // 리뷰가 없는 경우 404 반환
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 내 문의 목록 조회
     */
    @GetMapping("/my/inquiries")
    @Operation(summary = "내 문의 목록 조회", description = "특정 커리큘럼에 내가 작성한 문의 목록을 조회합니다.")
    public ResponseEntity<List<CourseReviewDTOs.CourseReviewResponse>> getMyInquiries(
            @PathVariable Long curriculumId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("내 문의 목록 조회 - 커리큘럼 ID: {}, 사용자 ID: {}", curriculumId, userId);
        List<CourseReviewDTOs.CourseReviewResponse> inquiries = courseReviewService.getMyInquiries(
                curriculumId, userId
        );
        return ResponseEntity.ok(inquiries);
    }

    /**
     * 커리큘럼 평균 평점 조회
     */
    @GetMapping("/average-rating")
    @Operation(summary = "커리큘럼 평균 평점 조회", description = "커리큘럼의 모든 리뷰 평균 평점을 조회합니다.")
    public ResponseEntity<CourseReviewDTOs.AverageRatingResponse> getCurriculumAverageRating(
            @PathVariable Long curriculumId
    ) {
        log.info("커리큘럼 평균 평점 조회 - 커리큘럼 ID: {}", curriculumId);
        CourseReviewDTOs.AverageRatingResponse response = courseReviewService.getCurriculumAverageRating(curriculumId);
        return ResponseEntity.ok(response);
    }
}

