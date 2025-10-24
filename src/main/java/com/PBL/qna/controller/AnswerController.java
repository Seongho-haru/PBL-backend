package com.PBL.qna.controller;

import com.PBL.qna.dto.QnADTOs;
import com.PBL.qna.entity.Answer;
import com.PBL.qna.service.AnswerService;
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

@RestController
@RequestMapping("/api/qna/answers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Q&A Answers", description = "답변 관련 API")
public class AnswerController {

    private final AnswerService answerService;

    // 답변 생성
    @PostMapping("/questions/{questionId}")
    @Operation(summary = "답변 생성", description = "질문에 답변을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답변 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> createAnswer(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId,
            @RequestBody QnADTOs.CreateAnswerRequest request,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("답변 생성 요청 - 질문 ID: {}, 사용자: {}", questionId, userId);
            Answer answer = answerService.createAnswer(questionId, request, userId);
            QnADTOs.AnswerResponse response = QnADTOs.AnswerResponse.from(answer);
            log.info("답변 생성 완료 - ID: {}", answer.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.warn("답변 생성 실패 - 질문 ID: {}, 오류: {}", questionId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("답변 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 생성 중 오류가 발생했습니다."));
        }
    }

    // 답변 수정
    @PutMapping("/{answerId}")
    @Operation(summary = "답변 수정", description = "답변을 수정합니다. 본인이 작성한 답변만 수정 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 답변이 아님)"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> updateAnswer(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId,
            @RequestBody QnADTOs.UpdateAnswerRequest request,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("답변 수정 요청 - ID: {}, 사용자: {}", answerId, userId);
            Answer answer = answerService.updateAnswer(answerId, request, userId);
            QnADTOs.AnswerResponse response = QnADTOs.AnswerResponse.from(answer);
            log.info("답변 수정 완료 - ID: {}", answerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("답변 수정 실패 - ID: {}, 오류: {}", answerId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("답변 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 수정 중 오류가 발생했습니다."));
        }
    }

    // 답변 삭제
    @DeleteMapping("/{answerId}")
    @Operation(summary = "답변 삭제", description = "답변을 삭제합니다. 본인이 작성한 답변만 삭제 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 답변이 아님)"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> deleteAnswer(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("답변 삭제 요청 - ID: {}, 사용자: {}", answerId, userId);
            answerService.deleteAnswer(answerId, userId);
            log.info("답변 삭제 완료 - ID: {}", answerId);
            return ResponseEntity.ok(Map.of("message", "답변이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            log.warn("답변 삭제 실패 - ID: {}, 오류: {}", answerId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("답변 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 삭제 중 오류가 발생했습니다."));
        }
    }

    // 답변 조회 (상세)
    @GetMapping("/{answerId}")
    @Operation(summary = "답변 상세 조회", description = "답변의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 조회 성공"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getAnswer(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId) {
        try {
            log.info("답변 조회 요청 - ID: {}", answerId);
            Answer answer = answerService.getAnswerById(answerId);
            QnADTOs.AnswerResponse response = QnADTOs.AnswerResponse.from(answer);
            log.info("답변 조회 완료 - ID: {}", answerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("답변 조회 실패 - ID: {}, 오류: {}", answerId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("답변 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 조회 중 오류가 발생했습니다."));
        }
    }

    // 질문별 답변 조회
    @GetMapping("/questions/{questionId}")
    @Operation(summary = "질문별 답변 조회", description = "특정 질문의 모든 답변을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getAnswersByQuestion(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId) {
        try {
            log.info("질문별 답변 조회 요청 - 질문 ID: {}", questionId);
            List<Answer> answers = answerService.getAnswersByQuestionId(questionId);
            List<QnADTOs.AnswerResponse> response = answers.stream()
                    .map(QnADTOs.AnswerResponse::from)
                    .toList();
            log.info("질문별 답변 조회 완료 - 질문 ID: {}, 답변 수: {}", questionId, answers.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("질문별 답변 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "질문별 답변 조회 중 오류가 발생했습니다."));
        }
    }

    // 답변 좋아요
    @PostMapping("/{answerId}/like")
    @Operation(summary = "답변 좋아요", description = "답변에 좋아요를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 성공"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> likeAnswer(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId) {
        try {
            log.info("답변 좋아요 요청 - ID: {}", answerId);
            answerService.likeAnswer(answerId);
            log.info("답변 좋아요 완료 - ID: {}", answerId);
            return ResponseEntity.ok(Map.of("message", "좋아요가 추가되었습니다."));
        } catch (RuntimeException e) {
            log.warn("답변 좋아요 실패 - ID: {}, 오류: {}", answerId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("답변 좋아요 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 좋아요 중 오류가 발생했습니다."));
        }
    }

    // 답변 좋아요 취소
    @DeleteMapping("/{answerId}/like")
    @Operation(summary = "답변 좋아요 취소", description = "답변의 좋아요를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> unlikeAnswer(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId) {
        try {
            log.info("답변 좋아요 취소 요청 - ID: {}", answerId);
            answerService.unlikeAnswer(answerId);
            log.info("답변 좋아요 취소 완료 - ID: {}", answerId);
            return ResponseEntity.ok(Map.of("message", "좋아요가 취소되었습니다."));
        } catch (RuntimeException e) {
            log.warn("답변 좋아요 취소 실패 - ID: {}, 오류: {}", answerId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("답변 좋아요 취소 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 좋아요 취소 중 오류가 발생했습니다."));
        }
    }

    // 답변 채택
    @PostMapping("/{answerId}/accept")
    @Operation(summary = "답변 채택", description = "답변을 채택합니다. 질문 작성자만 채택 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 채택 성공"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (질문 작성자가 아님)"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> acceptAnswer(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("답변 채택 요청 - ID: {}, 사용자: {}", answerId, userId);
            answerService.acceptAnswer(answerId, userId);
            log.info("답변 채택 완료 - ID: {}", answerId);
            return ResponseEntity.ok(Map.of("message", "답변이 채택되었습니다."));
        } catch (RuntimeException e) {
            log.warn("답변 채택 실패 - ID: {}, 오류: {}", answerId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("답변 채택 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 채택 중 오류가 발생했습니다."));
        }
    }

    // 답변 채택 취소
    @DeleteMapping("/{answerId}/accept")
    @Operation(summary = "답변 채택 취소", description = "답변의 채택을 취소합니다. 질문 작성자만 취소 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 채택 취소 성공"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (질문 작성자가 아님)"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> unacceptAnswer(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId,
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId) {
        try {
            log.info("답변 채택 취소 요청 - ID: {}, 사용자: {}", answerId, userId);
            answerService.unacceptAnswer(answerId, userId);
            log.info("답변 채택 취소 완료 - ID: {}", answerId);
            return ResponseEntity.ok(Map.of("message", "답변 채택이 취소되었습니다."));
        } catch (RuntimeException e) {
            log.warn("답변 채택 취소 실패 - ID: {}, 오류: {}", answerId, e.getMessage());
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("답변 채택 취소 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 채택 취소 중 오류가 발생했습니다."));
        }
    }

    // 답변 통계 조회
    @GetMapping("/stats")
    @Operation(summary = "답변 통계 조회", description = "답변 관련 통계 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getAnswerStats() {
        try {
            log.info("답변 통계 조회 요청");
            Map<String, Object> stats = answerService.getAnswerStats();
            log.info("답변 통계 조회 완료");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("답변 통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "답변 통계 조회 중 오류가 발생했습니다."));
        }
    }
}
