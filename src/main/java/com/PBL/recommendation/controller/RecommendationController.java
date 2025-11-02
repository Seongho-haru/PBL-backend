package com.PBL.recommendation.controller;

import com.PBL.recommendation.dto.RecommendationDTOs;
import com.PBL.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 추천 시스템 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recommendations", description = "추천 시스템 API")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 개인화된 커리큘럼 추천
     */
    @GetMapping("/curriculums")
    @Operation(summary = "개인화된 커리큘럼 추천", 
               description = "사용자의 수강 이력과 선호도를 기반으로 커리큘럼을 추천합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Map<String, Object>> getPersonalizedCurriculums(
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        log.info("개인화 추천 요청 - 사용자 ID: {}, 페이지: {}, 크기: {}", userId, page, size);
        
        try {
            Map<String, Object> result = recommendationService.getPersonalizedCurriculums(userId, page, size);
            
            log.info("개인화 추천 완료 - 추천 개수: {}", ((List<?>) result.get("curriculums")).size());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("개인화 추천 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * 유사 문제 강의 추천
     * Priority 1 - 가장 중요한 기능
     */
    @GetMapping("/similar-lectures")
    @Operation(summary = "유사 문제 강의 추천", 
               description = "현재 풀고 있는 문제와 유사한 강의를 추천합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (문제 강의가 아님)"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "404", description = "강의를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Map<String, Object>> getSimilarProblemLectures(
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "기준 강의 ID", required = true) @RequestParam Long lectureId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "5") int size) {
        
        log.info("유사 문제 추천 요청 - 사용자 ID: {}, 기준 강의 ID: {}, 페이지: {}, 크기: {}", userId, lectureId, page, size);
        
        try {
            Map<String, Object> result = recommendationService.getSimilarProblemLectures(userId, lectureId, page, size);
            
            log.info("유사 문제 추천 완료 - 추천 개수: {}", ((List<?>) result.get("lectures")).size());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("유사 문제 추천 실패 - 기준 강의 ID: {}, 오류: {}", lectureId, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("유사 문제 추천 실패 - 기준 강의 ID: {}, 오류: {}", lectureId, e.getMessage());
            throw e;
        }
    }

    /**
     * 통합 추천 (커리큘럼 + 강의 혼합)
     */
    @GetMapping("/unified")
    @Operation(summary = "통합 추천", 
               description = "공개된 커리큘럼과 강의를 점수 기준으로 혼합하여 추천합니다. 순서에 상관없이 점수가 높은 순으로 반환됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "X-User-Id 헤더 누락"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Map<String, Object>> getUnifiedRecommendations(
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        log.info("통합 추천 요청 - 사용자 ID: {}, 페이지: {}, 크기: {}", userId, page, size);
        
        try {
            Map<String, Object> result = recommendationService.getUnifiedRecommendations(userId, page, size);
            
            log.info("통합 추천 완료 - 추천 개수: {}", ((List<?>) result.get("recommendations")).size());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("통합 추천 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            throw e;
        }
    }
}

