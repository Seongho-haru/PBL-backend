package com.PBL.curriculum.service;

import com.PBL.curriculum.dto.CourseReviewDTOs;
import com.PBL.curriculum.entity.CourseReview;
import com.PBL.curriculum.entity.InquiryReply;
import com.PBL.curriculum.repository.CourseReviewRepository;
import com.PBL.curriculum.repository.InquiryReplyRepository;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import com.PBL.user.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰/문의 답글 Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class InquiryReplyService {

    private final InquiryReplyRepository inquiryReplyRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    /**
     * 리뷰/문의 답글 작성
     */
    @Transactional
    public CourseReviewDTOs.InquiryReplyResponse createReply(
            Long curriculumId, Long reviewId, Long userId, CourseReviewDTOs.CreateInquiryReplyRequest request) {
        log.info("리뷰/문의 답글 작성 요청 - 커리큘럼 ID: {}, 리뷰/문의 ID: {}, 사용자 ID: {}", curriculumId, reviewId, userId);

        // 정지 상태 체크
        userValidationService.validateUserCanCreateContent(userId);

        // 리뷰/문의 확인
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰/문의를 찾을 수 없습니다: " + reviewId));

        // 커리큘럼 확인
        if (!review.getCurriculum().getId().equals(curriculumId)) {
            throw new RuntimeException("커리큘럼 ID가 일치하지 않습니다.");
        }

        // 비공개 문의인 경우 권한 확인 (관리자 또는 커리큘럼 작성자만 답글 작성 가능)
        if (!review.getIsReview() && !review.getIsPublic()) {
            if (userId != 1L && !review.getCurriculum().isAuthor(userId)) {
                throw new RuntimeException("비공개 문의에 답글을 작성할 권한이 없습니다.");
            }
        }

        // 작성자 확인
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // 답글 생성
        InquiryReply reply = InquiryReply.builder()
                .review(review)
                .author(author)
                .content(request.getContent())
                .build();

        InquiryReply savedReply = inquiryReplyRepository.save(reply);
        log.info("리뷰/문의 답글 작성 완료 - ID: {}", savedReply.getId());

        return CourseReviewDTOs.InquiryReplyResponse.from(savedReply);
    }

    /**
     * 리뷰/문의 답글 목록 조회
     * 비공개 문의의 경우 관리자 또는 커리큘럼 작성자만 조회 가능
     */
    @Transactional(readOnly = true)
    public List<CourseReviewDTOs.InquiryReplyResponse> getReplies(Long curriculumId, Long reviewId, Long userId) {
        log.info("리뷰/문의 답글 목록 조회 - 커리큘럼 ID: {}, 리뷰/문의 ID: {}, 사용자 ID: {}", curriculumId, reviewId, userId);

        // 리뷰/문의 확인
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰/문의를 찾을 수 없습니다: " + reviewId));

        // 커리큘럼 확인
        if (!review.getCurriculum().getId().equals(curriculumId)) {
            throw new RuntimeException("커리큘럼 ID가 일치하지 않습니다.");
        }

        // 비공개 문의인 경우 권한 확인
        if (!review.getIsReview() && !review.getIsPublic()) {
            // 문의가 비공개인 경우, 관리자(userId=1) 또는 커리큘럼 작성자만 조회 가능
            if (userId == null || (userId != 1L && !review.getCurriculum().isAuthor(userId))) {
                throw new RuntimeException("비공개 문의의 답글을 조회할 권한이 없습니다.");
            }
        }

        // 답글 목록 조회
        List<InquiryReply> replies = inquiryReplyRepository.findByReviewIdOrderByCreatedAtAsc(reviewId);

        return replies.stream()
                .map(CourseReviewDTOs.InquiryReplyResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 리뷰/문의 답글 단건 조회
     * 비공개 문의의 경우 관리자 또는 커리큘럼 작성자만 조회 가능
     */
    @Transactional(readOnly = true)
    public CourseReviewDTOs.InquiryReplyResponse getReply(Long curriculumId, Long reviewId, Long replyId, Long userId) {
        log.info("리뷰/문의 답글 단건 조회 - 커리큘럼 ID: {}, 리뷰/문의 ID: {}, 답글 ID: {}, 사용자 ID: {}", curriculumId, reviewId, replyId, userId);

        // 답글 확인
        InquiryReply reply = inquiryReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다: " + replyId));

        // 리뷰/문의 및 커리큘럼 확인
        CourseReview review = reply.getReview();
        if (!review.getId().equals(reviewId)) {
            throw new RuntimeException("리뷰/문의 ID가 일치하지 않습니다.");
        }
        if (!review.getCurriculum().getId().equals(curriculumId)) {
            throw new RuntimeException("커리큘럼 ID가 일치하지 않습니다.");
        }

        // 비공개 문의인 경우 권한 확인
        if (!review.getIsReview() && !review.getIsPublic()) {
            // 문의가 비공개인 경우, 관리자(userId=1) 또는 커리큘럼 작성자만 조회 가능
            if (userId == null || (userId != 1L && !review.getCurriculum().isAuthor(userId))) {
                throw new RuntimeException("비공개 문의의 답글을 조회할 권한이 없습니다.");
            }
        }

        return CourseReviewDTOs.InquiryReplyResponse.from(reply);
    }

    /**
     * 리뷰/문의 답글 수정
     */
    @Transactional
    public CourseReviewDTOs.InquiryReplyResponse updateReply(
            Long curriculumId, Long reviewId, Long replyId, Long userId,
            CourseReviewDTOs.UpdateInquiryReplyRequest request) {
        log.info("리뷰/문의 답글 수정 요청 - 답글 ID: {}, 사용자 ID: {}", replyId, userId);

        // 정지 상태 체크
        userValidationService.validateUserCanModifyContent(userId);

        // 답글 확인
        InquiryReply reply = inquiryReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다: " + replyId));

        // 작성자 확인
        if (!reply.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 답글만 수정할 수 있습니다.");
        }

        // 리뷰/문의 및 커리큘럼 확인
        CourseReview review = reply.getReview();
        if (!review.getId().equals(reviewId) || !review.getCurriculum().getId().equals(curriculumId)) {
            throw new RuntimeException("리뷰/문의 ID 또는 커리큘럼 ID가 일치하지 않습니다.");
        }

        // 답글 수정
        reply.setContent(request.getContent());
        InquiryReply updatedReply = inquiryReplyRepository.save(reply);
        log.info("리뷰/문의 답글 수정 완료 - ID: {}", replyId);

        return CourseReviewDTOs.InquiryReplyResponse.from(updatedReply);
    }

    /**
     * 리뷰/문의 답글 삭제
     */
    @Transactional
    public void deleteReply(Long curriculumId, Long reviewId, Long replyId, Long userId) {
        log.info("리뷰/문의 답글 삭제 요청 - 답글 ID: {}, 사용자 ID: {}", replyId, userId);

        // 정지 상태 체크
        userValidationService.validateUserCanDeleteContent(userId);

        // 답글 확인
        InquiryReply reply = inquiryReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다: " + replyId));

        // 작성자 확인
        if (!reply.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 답글만 삭제할 수 있습니다.");
        }

        // 리뷰/문의 및 커리큘럼 확인
        CourseReview review = reply.getReview();
        if (!review.getId().equals(reviewId) || !review.getCurriculum().getId().equals(curriculumId)) {
            throw new RuntimeException("리뷰/문의 ID 또는 커리큘럼 ID가 일치하지 않습니다.");
        }

        inquiryReplyRepository.delete(reply);
        log.info("리뷰/문의 답글 삭제 완료 - ID: {}", replyId);
    }
}

