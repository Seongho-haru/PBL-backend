package com.PBL.curriculum.controller;

import com.PBL.curriculum.dto.CourseReviewDTOs;
import com.PBL.curriculum.service.InquiryReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 문의 답글 Controller
 */
@RestController
@RequestMapping("/api/curriculums/{curriculumId}/reviews/inquiries/{inquiryId}/replies")
@Tag(name = "Inquiry Reply", description = "커리큘럼 문의 답글 API")
@Slf4j
@RequiredArgsConstructor
public class InquiryReplyController {

    private final InquiryReplyService inquiryReplyService;

    /**
     * 문의 답글 목록 조회
     */
    @GetMapping
    @Operation(summary = "문의 답글 목록 조회", description = "특정 문의에 대한 답글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<CourseReviewDTOs.InquiryReplyResponse>> getReplies(
            @Parameter(description = "커리큘럼 ID", required = true) @PathVariable Long curriculumId,
            @Parameter(description = "문의 ID", required = true) @PathVariable Long inquiryId,
            @Parameter(description = "사용자 ID (선택사항, 비공개 문의 조회 시 필요)") @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        log.info("문의 답글 목록 조회 - 커리큘럼 ID: {}, 문의 ID: {}, 사용자 ID: {}", curriculumId, inquiryId, userId);
        try {
            List<CourseReviewDTOs.InquiryReplyResponse> replies = inquiryReplyService.getReplies(curriculumId, inquiryId, userId);
            return ResponseEntity.ok(replies);
        } catch (RuntimeException e) {
            log.warn("문의 답글 목록 조회 실패 - 커리큘럼 ID: {}, 문의 ID: {}, 오류: {}", curriculumId, inquiryId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("문의 답글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 문의 답글 단건 조회
     */
    @GetMapping("/{replyId}")
    @Operation(summary = "문의 답글 단건 조회", description = "특정 답글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getReply(
            @Parameter(description = "커리큘럼 ID", required = true) @PathVariable Long curriculumId,
            @Parameter(description = "문의 ID", required = true) @PathVariable Long inquiryId,
            @Parameter(description = "답글 ID", required = true) @PathVariable Long replyId,
            @Parameter(description = "사용자 ID (선택사항, 비공개 문의 조회 시 필요)") @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        log.info("문의 답글 단건 조회 - 커리큘럼 ID: {}, 문의 ID: {}, 답글 ID: {}, 사용자 ID: {}", curriculumId, inquiryId, replyId, userId);
        try {
            CourseReviewDTOs.InquiryReplyResponse reply = inquiryReplyService.getReply(curriculumId, inquiryId, replyId, userId);
            return ResponseEntity.ok(reply);
        } catch (RuntimeException e) {
            log.warn("문의 답글 단건 조회 실패 - 커리큘럼 ID: {}, 문의 ID: {}, 답글 ID: {}, 오류: {}",
                    curriculumId, inquiryId, replyId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("문의 답글 단건 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문의 답글 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 문의 답글 작성
     */
    @PostMapping
    @Operation(summary = "문의 답글 작성", description = "문의에 답글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (정지된 사용자 또는 본인의 문의가 아님)"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> createReply(
            @Parameter(description = "커리큘럼 ID", required = true) @PathVariable Long curriculumId,
            @Parameter(description = "문의 ID", required = true) @PathVariable Long inquiryId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @RequestBody CourseReviewDTOs.CreateInquiryReplyRequest request
    ) {
        log.info("문의 답글 작성 요청 - 커리큘럼 ID: {}, 문의 ID: {}, 사용자 ID: {}", curriculumId, inquiryId, userId);
        try {
            CourseReviewDTOs.InquiryReplyResponse reply = inquiryReplyService.createReply(
                    curriculumId, inquiryId, userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reply);
        } catch (RuntimeException e) {
            log.warn("문의 답글 작성 실패 - 커리큘럼 ID: {}, 문의 ID: {}, 사용자 ID: {}, 오류: {}",
                    curriculumId, inquiryId, userId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한") || e.getMessage().contains("정지")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("문의 답글 작성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문의 답글 작성 중 오류가 발생했습니다."));
        }
    }

    /**
     * 문의 답글 수정
     */
    @PutMapping("/{replyId}")
    @Operation(summary = "문의 답글 수정", description = "작성한 답글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 답글이 아님)"),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> updateReply(
            @Parameter(description = "커리큘럼 ID", required = true) @PathVariable Long curriculumId,
            @Parameter(description = "문의 ID", required = true) @PathVariable Long inquiryId,
            @Parameter(description = "답글 ID", required = true) @PathVariable Long replyId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @RequestBody CourseReviewDTOs.UpdateInquiryReplyRequest request
    ) {
        log.info("문의 답글 수정 요청 - 답글 ID: {}, 사용자 ID: {}", replyId, userId);
        try {
            CourseReviewDTOs.InquiryReplyResponse reply = inquiryReplyService.updateReply(
                    curriculumId, inquiryId, replyId, userId, request);
            return ResponseEntity.ok(reply);
        } catch (RuntimeException e) {
            log.warn("문의 답글 수정 실패 - 답글 ID: {}, 오류: {}", replyId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한") || e.getMessage().contains("본인")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("문의 답글 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문의 답글 수정 중 오류가 발생했습니다."));
        }
    }

    /**
     * 문의 답글 삭제
     */
    @DeleteMapping("/{replyId}")
    @Operation(summary = "문의 답글 삭제", description = "작성한 답글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "답글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 답글이 아님)"),
            @ApiResponse(responseCode = "404", description = "답글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> deleteReply(
            @Parameter(description = "커리큘럼 ID", required = true) @PathVariable Long curriculumId,
            @Parameter(description = "문의 ID", required = true) @PathVariable Long inquiryId,
            @Parameter(description = "답글 ID", required = true) @PathVariable Long replyId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("문의 답글 삭제 요청 - 답글 ID: {}, 사용자 ID: {}", replyId, userId);
        try {
            inquiryReplyService.deleteReply(curriculumId, inquiryId, replyId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("문의 답글 삭제 실패 - 답글 ID: {}, 오류: {}", replyId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한") || e.getMessage().contains("본인")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("문의 답글 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문의 답글 삭제 중 오류가 발생했습니다."));
        }
    }
}

