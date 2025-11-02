package com.PBL.qna.controller;

import com.PBL.qna.dto.QnADTOs;
import com.PBL.qna.entity.Question;
import com.PBL.qna.enums.QuestionCategory;
import com.PBL.qna.enums.QuestionStatus;
import com.PBL.qna.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/qna/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Q&A Questions", description = "질문 관련 API")
public class QuestionController {

    private final QuestionService questionService;

    // 질문 생성
    @PostMapping
    @Operation(summary = "질문 생성", description = "새로운 질문을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "질문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> createQuestion(
            @RequestBody QnADTOs.CreateQuestionRequest request,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("질문 생성 요청 - 사용자: {}, 제목: {}", userId, request.getTitle());
            Question question = questionService.createQuestion(request, userId);
            QnADTOs.QuestionDetailResponse response = QnADTOs.QuestionDetailResponse.from(question);
            log.info("질문 생성 완료 - ID: {}", question.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("질문 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 생성 중 오류가 발생했습니다."));
        }
    }

    // 질문 수정
    @PutMapping("/{questionId}")
    @Operation(summary = "질문 수정", description = "질문을 수정합니다. 본인이 작성한 질문만 수정 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "질문 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 질문이 아님)"),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> updateQuestion(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId,
            @RequestBody QnADTOs.UpdateQuestionRequest request,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("질문 수정 요청 - ID: {}, 사용자: {}", questionId, userId);
            Question question = questionService.updateQuestion(questionId, request, userId);
            QnADTOs.QuestionDetailResponse response = QnADTOs.QuestionDetailResponse.from(question);
            log.info("질문 수정 완료 - ID: {}", questionId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("질문 수정 실패 - ID: {}, 오류: {}", questionId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("질문 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 수정 중 오류가 발생했습니다."));
        }
    }

    // 질문 삭제
    @DeleteMapping("/{questionId}")
    @Operation(summary = "질문 삭제", description = "질문을 삭제합니다. 본인이 작성한 질문만 삭제 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "질문 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 질문이 아님)"),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> deleteQuestion(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("질문 삭제 요청 - ID: {}, 사용자: {}", questionId, userId);
            questionService.deleteQuestion(questionId, userId);
            log.info("질문 삭제 완료 - ID: {}", questionId);
            return ResponseEntity.ok(Map.of("message", "질문이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            log.warn("질문 삭제 실패 - ID: {}, 오류: {}", questionId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("질문 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 삭제 중 오류가 발생했습니다."));
        }
    }

    // 질문 상세 조회
    @GetMapping("/{questionId}")
    @Operation(summary = "질문 상세 조회", description = "질문의 상세 정보와 답변들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "질문 조회 성공"),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getQuestion(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId) {
        try {
            log.info("질문 조회 요청 - ID: {}", questionId);
            Question question = questionService.getQuestionById(questionId);
            QnADTOs.QuestionDetailResponse response = QnADTOs.QuestionDetailResponse.from(question);
            log.info("질문 조회 완료 - ID: {}", questionId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("질문 조회 실패 - ID: {}, 오류: {}", questionId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("질문 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 조회 중 오류가 발생했습니다."));
        }
    }

    // 질문 목록 조회 (검색)
    @GetMapping
    @Operation(summary = "질문 목록 조회", description = "질문 목록을 검색 조건에 따라 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "질문 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getQuestions(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "질문 상태") @RequestParam(required = false) QuestionStatus status,
            @Parameter(description = "질문 카테고리") @RequestParam(required = false) QuestionCategory category,
            @Parameter(description = "강의명") @RequestParam(required = false) String course,
            @Parameter(description = "프로그래밍 언어") @RequestParam(required = false) String language,
            @Parameter(description = "작성자 ID") @RequestParam(required = false) Long authorId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        try {
            log.info("질문 목록 조회 요청 - 키워드: {}, 상태: {}, 카테고리: {}", keyword, status, category);
            
            QnADTOs.QuestionSearchRequest searchRequest = QnADTOs.QuestionSearchRequest.builder()
                    .keyword(keyword)
                    .status(status)
                    .category(category)
                    .course(course)
                    .language(language)
                    .authorId(authorId)
                    .build();
            
            Page<Question> questions = questionService.searchQuestions(searchRequest, pageable);
            Page<QnADTOs.QuestionListResponse> response = questions.map(QnADTOs.QuestionListResponse::from);
            
            log.info("질문 목록 조회 완료 - 총 {}개", questions.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("질문 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 인기 질문 조회
    @GetMapping("/popular")
    @Operation(summary = "인기 질문 조회", description = "좋아요가 많은 인기 질문들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 질문 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getPopularQuestions(
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("인기 질문 조회 요청");
            Page<Question> questions = questionService.getPopularQuestions(pageable);
            Page<QnADTOs.QuestionListResponse> response = questions.map(QnADTOs.QuestionListResponse::from);
            log.info("인기 질문 조회 완료 - 총 {}개", questions.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("인기 질문 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "인기 질문 조회 중 오류가 발생했습니다."));
        }
    }

    // 질문 좋아요
    @PostMapping("/{questionId}/like")
    @Operation(summary = "질문 좋아요", description = "질문에 좋아요를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 성공"),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> likeQuestion(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId) {
        try {
            log.info("질문 좋아요 요청 - ID: {}", questionId);
            questionService.likeQuestion(questionId);
            log.info("질문 좋아요 완료 - ID: {}", questionId);
            return ResponseEntity.ok(Map.of("message", "좋아요가 추가되었습니다."));
        } catch (RuntimeException e) {
            log.warn("질문 좋아요 실패 - ID: {}, 오류: {}", questionId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("질문 좋아요 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 좋아요 중 오류가 발생했습니다."));
        }
    }

    // 질문 좋아요 취소
    @DeleteMapping("/{questionId}/like")
    @Operation(summary = "질문 좋아요 취소", description = "질문의 좋아요를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> unlikeQuestion(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId) {
        try {
            log.info("질문 좋아요 취소 요청 - ID: {}", questionId);
            questionService.unlikeQuestion(questionId);
            log.info("질문 좋아요 취소 완료 - ID: {}", questionId);
            return ResponseEntity.ok(Map.of("message", "좋아요가 취소되었습니다."));
        } catch (RuntimeException e) {
            log.warn("질문 좋아요 취소 실패 - ID: {}, 오류: {}", questionId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("질문 좋아요 취소 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 좋아요 취소 중 오류가 발생했습니다."));
        }
    }

    // 질문 해결 상태 토글
    @PutMapping("/{questionId}/resolve")
    @Operation(summary = "질문 해결 상태 토글", description = "질문의 해결 상태를 토글합니다. 해결 상태면 미해결로, 미해결 상태면 해결로 변경됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "질문 상태 토글 성공"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 질문이 아님)"),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> toggleQuestionResolution(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("질문 해결 상태 토글 요청 - ID: {}, 사용자: {}", questionId, userId);
            boolean isNowResolved = questionService.toggleQuestionResolution(questionId, userId);
            String message = isNowResolved ? "질문이 해결 상태로 변경되었습니다." : "질문이 미해결 상태로 변경되었습니다.";
            log.info("질문 해결 상태 토글 완료 - ID: {}, 상태: {}", questionId, isNowResolved ? "해결" : "미해결");
            return ResponseEntity.ok(Map.of(
                    "message", message,
                    "status", isNowResolved ? "RESOLVED" : "UNRESOLVED"
            ));
        } catch (RuntimeException e) {
            log.warn("질문 해결 상태 토글 실패 - ID: {}, 오류: {}", questionId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("질문 해결 상태 토글 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 해결 상태 토글 중 오류가 발생했습니다."));
        }
    }

    // 질문 통계 조회
    @GetMapping("/stats")
    @Operation(summary = "질문 통계 조회", description = "질문 관련 통계 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getQuestionStats() {
        try {
            log.info("질문 통계 조회 요청");
            Map<String, Object> stats = questionService.getQuestionStats();
            log.info("질문 통계 조회 완료");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("질문 통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문 통계 조회 중 오류가 발생했습니다."));
        }
    }
}
