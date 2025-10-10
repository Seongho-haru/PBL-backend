package com.PBL.enrollment.controller;

import com.PBL.enrollment.dto.EnrollmentDTOs;
import com.PBL.enrollment.entity.Enrollment;
import com.PBL.enrollment.entity.EnrollmentStatus;
import com.PBL.enrollment.service.EnrollmentService;
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
 * 수강 관리 API 컨트롤러
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "수강 관리", description = "수강 신청/취소, 진도 관리, 조회 및 통계 API")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // === 수강 신청/취소 ===

    /**
     * 커리큘럼 수강 신청
     * POST /api/enrollments
     */
    @PostMapping
    @Operation(summary = "커리큘럼 수강 신청", description = "공개된 커리큘럼에 수강 신청합니다. 작성자는 자신의 커리큘럼을 수강할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "수강 신청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (커리큘럼을 찾을 수 없음)"),
            @ApiResponse(responseCode = "409", description = "이미 수강 중이거나 작성자의 커리큘럼"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> enrollInCurriculum(
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "수강 신청 요청 정보", required = true) @RequestBody EnrollmentDTOs.EnrollRequest request) {
        try {
            log.info("수강 신청 API 호출 - 사용자 ID: {}, 커리큘럼 ID: {}", userId, request.getCurriculumId());
            
            Enrollment enrollment = enrollmentService.enrollInCurriculum(userId, request.getCurriculumId());
            EnrollmentDTOs.EnrollmentResponse response = EnrollmentDTOs.EnrollmentResponse.from(enrollment);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("수강 신청 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("수강 신청 실패 - 비즈니스 규칙 위반: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("수강 신청 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "수강 신청 중 오류가 발생했습니다."));
        }
    }

    /**
     * 수강 취소
     * DELETE /api/enrollments/{enrollmentId}
     */
    @DeleteMapping("/{enrollmentId}")
    @Operation(summary = "수강 취소", description = "수강 신청을 취소하고 관련 데이터를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수강 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (수강 정보를 찾을 수 없음)"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 수강이 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> cancelEnrollment(
            @Parameter(description = "사용자 ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "수강 ID", required = true) @PathVariable Long enrollmentId) {
        try {
            log.info("수강 취소 API 호출 - 사용자 ID: {}, 수강 ID: {}", userId, enrollmentId);
            
            enrollmentService.cancelEnrollment(userId, enrollmentId);
            
            return ResponseEntity.ok(Map.of("message", "수강이 성공적으로 취소되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("수강 취소 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("수강 취소 실패 - 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("수강 취소 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "수강 취소 중 오류가 발생했습니다."));
        }
    }

    // === 진도 관리 ===

    /**
     * 강의 읽기 완료 처리 (마크다운 강의)
     * PUT /api/enrollments/{enrollmentId}/lectures/{lectureId}/read
     */
    @PutMapping("/{enrollmentId}/lectures/{lectureId}/read")
    @Operation(summary = "마크다운 강의 읽기 완료", description = "마크다운 강의를 읽고 완료 상태로 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "강의 읽기 완료 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (수강 정보 또는 강의를 찾을 수 없음)"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 수강이 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> markLectureAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long enrollmentId,
            @PathVariable Long lectureId) {
        try {
            log.info("강의 읽기 완료 API 호출 - 사용자 ID: {}, 수강 ID: {}, 강의 ID: {}", userId, enrollmentId, lectureId);
            
            enrollmentService.markLectureAsRead(userId, enrollmentId, lectureId);
            
            return ResponseEntity.ok(Map.of("message", "강의 읽기가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("강의 읽기 완료 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("강의 읽기 완료 실패 - 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("강의 읽기 완료 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 읽기 완료 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 문제 강의 정답 제출 처리
     * PUT /api/enrollments/{enrollmentId}/lectures/{lectureId}/solve
     */
    @PutMapping("/{enrollmentId}/lectures/{lectureId}/solve")
    @Operation(summary = "문제 강의 정답 제출", description = "문제 강의의 정답을 제출하고 완료 상태로 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문제 해결 완료 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (수강 정보 또는 강의를 찾을 수 없음)"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 수강이 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> markLectureAsSolved(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long enrollmentId,
            @PathVariable Long lectureId) {
        try {
            log.info("문제 강의 정답 제출 API 호출 - 사용자 ID: {}, 수강 ID: {}, 강의 ID: {}", userId, enrollmentId, lectureId);
            
            enrollmentService.markLectureAsSolved(userId, enrollmentId, lectureId);
            
            return ResponseEntity.ok(Map.of("message", "문제가 성공적으로 해결되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("문제 강의 정답 제출 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("문제 강의 정답 제출 실패 - 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("문제 강의 정답 제출 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문제 강의 정답 제출 처리 중 오류가 발생했습니다."));
        }
    }

    // === 조회 기능 ===

    /**
     * 사용자별 수강 목록 조회
     * GET /api/enrollments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 수강 목록 조회", description = "특정 사용자의 모든 수강 목록을 조회합니다. 본인만 조회 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수강 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 수강 목록이 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getUserEnrollments(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long userId) {
        try {
            log.info("사용자 수강 목록 조회 API 호출 - 현재 사용자 ID: {}, 조회 대상 사용자 ID: {}", currentUserId, userId);
            
            // 본인만 조회 가능
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "본인의 수강 목록만 조회할 수 있습니다."));
            }
            
            List<Enrollment> enrollments = enrollmentService.getUserEnrollments(userId);
            List<EnrollmentDTOs.EnrollmentResponse> responses = enrollments.stream()
                    .map(EnrollmentDTOs.EnrollmentResponse::from)
                    .toList();
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("사용자 수강 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "수강 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용자별 수강 상태별 목록 조회
     * GET /api/enrollments/user/{userId}/status
     */
    @GetMapping("/user/{userId}/status")
    @Operation(summary = "사용자별 수강 목록 조회 (상태별)", description = "특정 사용자의 수강 목록을 상태별로 필터링하여 조회합니다. 본인만 조회 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수강 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 수강 목록이 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getUserEnrollmentsByStatus(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long userId,
            @RequestParam(required = false) EnrollmentStatus status) {
        try {
            log.info("사용자 수강 목록 조회 API 호출 (상태별) - 현재 사용자 ID: {}, 조회 대상 사용자 ID: {}, 상태: {}", 
                    currentUserId, userId, status);
            
            // 본인만 조회 가능
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "본인의 수강 목록만 조회할 수 있습니다."));
            }
            
            List<Enrollment> enrollments = (status != null) 
                    ? enrollmentService.getUserEnrollmentsByStatus(userId, status)
                    : enrollmentService.getUserEnrollments(userId);
            
            List<EnrollmentDTOs.EnrollmentResponse> responses = enrollments.stream()
                    .map(EnrollmentDTOs.EnrollmentResponse::from)
                    .toList();
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("사용자 수강 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "수강 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 수강 상세 정보 조회 (강의별 진도 포함)
     * GET /api/enrollments/{enrollmentId}
     */
    @GetMapping("/{enrollmentId}")
    @Operation(summary = "수강 상세 정보 조회", description = "특정 수강의 상세 정보와 강의별 진도를 조회합니다. 본인의 수강만 조회 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수강 상세 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "수강 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 수강이 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getEnrollmentDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long enrollmentId) {
        try {
            log.info("수강 상세 정보 조회 API 호출 - 사용자 ID: {}, 수강 ID: {}", userId, enrollmentId);
            
            EnrollmentDTOs.EnrollmentDetailResponse response = enrollmentService.getEnrollmentDetail(userId, enrollmentId);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("수강 상세 정보 조회 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("수강 상세 정보 조회 실패 - 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("수강 상세 정보 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "수강 상세 정보 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용자별 특정 강의 완료 상태 조회
     * GET /api/enrollments/user/{userId}/lecture/{lectureId}/completed
     */
    @GetMapping("/user/{userId}/lecture/{lectureId}/completed")
    @Operation(summary = "사용자별 강의 완료 상태 조회", description = "특정 사용자가 특정 강의를 완료했는지 확인합니다. 본인만 조회 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "강의 완료 상태 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 강의 완료 상태가 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> isLectureCompletedByUser(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long userId,
            @PathVariable Long lectureId) {
        try {
            log.info("사용자 강의 완료 상태 조회 API 호출 - 현재 사용자 ID: {}, 조회 대상 사용자 ID: {}, 강의 ID: {}", 
                    currentUserId, userId, lectureId);
            
            // 본인만 조회 가능
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "본인의 강의 완료 상태만 조회할 수 있습니다."));
            }
            
            boolean isCompleted = enrollmentService.isLectureCompletedByUser(userId, lectureId);
            
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "lectureId", lectureId,
                    "isCompleted", isCompleted
            ));
        } catch (IllegalArgumentException e) {
            log.warn("강의 완료 상태 조회 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("강의 완료 상태 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 완료 상태 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용자별 특정 강의 진도 상세 조회
     * GET /api/enrollments/user/{userId}/lecture/{lectureId}/progress
     */
    @GetMapping("/user/{userId}/lecture/{lectureId}/progress")
    @Operation(summary = "사용자별 강의 진도 상세 조회", description = "특정 사용자의 특정 강의에 대한 상세 진도 정보를 조회합니다. 본인만 조회 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "강의 진도 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (본인의 강의 진도가 아님)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getUserLectureProgress(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long userId,
            @PathVariable Long lectureId) {
        try {
            log.info("사용자 강의 진도 상세 조회 API 호출 - 현재 사용자 ID: {}, 조회 대상 사용자 ID: {}, 강의 ID: {}", 
                    currentUserId, userId, lectureId);
            
            // 본인만 조회 가능
            if (!currentUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "본인의 강의 진도만 조회할 수 있습니다."));
            }
            
            List<EnrollmentDTOs.LectureProgressResponse> progressList = 
                    enrollmentService.getUserLectureProgress(userId, lectureId);
            
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "lectureId", lectureId,
                    "progressList", progressList
            ));
        } catch (IllegalArgumentException e) {
            log.warn("강의 진도 조회 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("강의 진도 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 진도 조회 중 오류가 발생했습니다."));
        }
    }

    // === 통계 기능 ===

    /**
     * 커리큘럼별 수강자 수 조회
     * GET /api/enrollments/curriculum/{curriculumId}/count
     */
    @GetMapping("/curriculum/{curriculumId}/count")
    @Operation(summary = "커리큘럼별 수강자 수 조회", description = "특정 커리큘럼의 총 수강자 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수강자 수 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getEnrollmentCount(@PathVariable Long curriculumId) {
        try {
            log.info("커리큘럼 수강자 수 조회 API 호출 - 커리큘럼 ID: {}", curriculumId);
            
            long count = enrollmentService.getEnrollmentCount(curriculumId);
            
            return ResponseEntity.ok(Map.of("curriculumId", curriculumId, "enrollmentCount", count));
        } catch (Exception e) {
            log.error("커리큘럼 수강자 수 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "수강자 수 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 커리큘럼별 완료자 수 조회
     * GET /api/enrollments/curriculum/{curriculumId}/completed-count
     */
    @GetMapping("/curriculum/{curriculumId}/completed-count")
    @Operation(summary = "커리큘럼별 완료자 수 조회", description = "특정 커리큘럼을 완료한 수강자 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "완료자 수 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getCompletedCount(@PathVariable Long curriculumId) {
        try {
            log.info("커리큘럼 완료자 수 조회 API 호출 - 커리큘럼 ID: {}", curriculumId);
            
            long count = enrollmentService.getCompletedCount(curriculumId);
            
            return ResponseEntity.ok(Map.of("curriculumId", curriculumId, "completedCount", count));
        } catch (Exception e) {
            log.error("커리큘럼 완료자 수 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "완료자 수 조회 중 오류가 발생했습니다."));
        }
    }
}
