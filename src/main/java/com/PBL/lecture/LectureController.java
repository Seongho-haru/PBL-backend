package com.PBL.lecture;

import com.PBL.lecture.dto.*;
import com.PBL.lecture.entity.Lecture;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import com.PBL.user.service.UserValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 강의 관리 REST API 컨트롤러
 * 프론트엔드의 강의 생성/조회/수정/삭제 API 제공
 */
@RestController
@RequestMapping("/api/lectures")
@CrossOrigin(origins = "*") // 개발용 CORS 설정
@Slf4j
@Tag(name = "Lectures", description = "강의 관리 API - 마크다운 강의와 문제 강의를 통합 관리")
public class LectureController {

    private final LectureService lectureService;
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    public LectureController(LectureService lectureService, UserRepository userRepository, UserValidationService userValidationService) {
        this.lectureService = lectureService;
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
    }

    // === 기본 CRUD API ===

    /**
     * 강의 생성
     * POST /api/lectures
     */
    @PostMapping
    @Operation(summary = "강의 생성", description = "새로운 강의를 생성합니다. 마크다운 강의 또는 문제 강의를 생성할 수 있습니다.")
    public ResponseEntity<?> createLecture(
            @RequestBody CreateLectureRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // 사용자 권한 확인
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "사용자 인증이 필요합니다."));
            }

            // 정지 상태 체크
            userValidationService.validateUserCanCreateContent(userId);

            // 작성자 정보 확인
            User author = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

            // Service에서 트랜잭션 내 생성 및 DTO 변환 (LazyInitializationException 방지)
            LectureResponse response = lectureService.createLectureWithResponse(request, author);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // 정지된 사용자 또는 기타 런타임 예외
            if (e.getMessage() != null && e.getMessage().contains("정지")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.info("Exception: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 생성 중 오류가 발생했습니다."));
        }
    }

    /**
     * 모든 강의 조회
     * GET /api/lectures
     */
    @GetMapping
    @Operation(summary = "모든 강의 조회", description = "시스템에 등록된 모든 강의를 최신순으로 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getAllLectures() {
        // Service에서 트랜잭션 내 DTO 변환된 결과를 받음 (LazyInitializationException 방지)
        List<LectureResponse> responses = lectureService.getAllLectures();
        return ResponseEntity.ok(responses);
    }

    /**
     * 강의 상세 조회
     * GET /api/lectures/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "강의 상세 조회", description = "ID로 특정 강의의 상세 정보를 조회합니다. 문제 강의인 경우 테스트케이스도 포함됩니다.")
    public ResponseEntity<?> getLecture(
            @Parameter(description = "강의 ID", required = true) @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // Service에서 트랜잭션 내 권한 체크 및 DTO 변환 (LazyInitializationException 방지)
            LectureResponse response = lectureService.getLecture(id, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 강의 수정
     * PUT /api/lectures/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLecture(
            @PathVariable Long id,
            @RequestBody CreateLectureRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // 사용자 권한 확인
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "사용자 인증이 필요합니다."));
            }

            // 작성자 권한 확인 (내부적으로 강의 존재 여부도 체크함)
            if (!lectureService.canEditLecture(id, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "이 강의를 수정할 권한이 없거나 강의가 존재하지 않습니다."));
            }

            // Service에서 트랜잭션 내 수정 및 DTO 변환
            LectureResponse response = lectureService.updateLectureWithResponse(id, request, userId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 수정 중 오류가 발생했습니다."));
        }
    }

    /**
     * 강의 삭제
     * DELETE /api/lectures/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLecture(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // 사용자 권한 확인
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "사용자 인증이 필요합니다."));
            }

            // 강의 존재 여부 먼저 확인
            Optional<Lecture> lectureOpt = lectureService.findLectureEntity(id);
            if (lectureOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // 작성자 권한 확인
            if (!lectureService.canDeleteLecture(id, userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "이 강의를 삭제할 권한이 없습니다."));
            }

            lectureService.deleteLecture(id);
            return ResponseEntity.ok(Map.of("message", "강의가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 삭제 중 오류가 발생했습니다."));
        }
    }

    // === 검색 및 필터링 API ===

    /**
     * 강의 검색
     * GET /api/lectures/search?title=검색어&category=카테고리&difficulty=난이도&type=MARKDOWN&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchLectures(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) LectureType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Service에서 트랜잭션 내 DTO 변환된 결과를 받음
        Map<String, Object> response = lectureService.searchLectures(title, category, difficulty, type, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 유형별 강의 조회
     * GET /api/lectures/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<LectureResponse>> getLecturesByType(@PathVariable LectureType type) {
        // Service에서 트랜잭션 내 DTO 변환된 결과를 받음
        List<LectureResponse> responses = lectureService.getLecturesByType(type);
        return ResponseEntity.ok(responses);
    }

    /**
     * 최근 강의 조회
     * GET /api/lectures/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<List<LectureResponse>> getRecentLectures() {
        // Service에서 트랜잭션 내 DTO 변환된 결과를 받음
        List<LectureResponse> responses = lectureService.getRecentLectures(10);
        return ResponseEntity.ok(responses);
    }

    // === 테스트케이스 관리 API ===

    /**
     * 테스트케이스 추가
     * POST /api/lectures/{id}/testcases
     */
    @PostMapping("/{id}/testcases")
    public ResponseEntity<?> addTestCase(
            @PathVariable Long id,
            @RequestBody TestCaseRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // 사용자 권한 확인
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "사용자 인증이 필요합니다."));
            }

            // Service에서 트랜잭션 내 권한 체크, 추가 및 DTO 변환
            LectureResponse response = lectureService.addTestCase(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 모든 테스트케이스 삭제
     * DELETE /api/lectures/{id}/testcases
     */
    @DeleteMapping("/{id}/testcases")
    public ResponseEntity<?> clearTestCases(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // 사용자 권한 확인
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "사용자 인증이 필요합니다."));
            }

            // Service에서 트랜잭션 내 권한 체크, 삭제 및 DTO 변환
            LectureResponse response = lectureService.clearTestCases(id, userId);
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === 통계 API ===

    /**
     * 강의 통계 조회
     * GET /api/lectures/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getLectureStats() {
        List<Object[]> typeStats = lectureService.getLectureStatsByType();
        List<Object[]> categoryStats = lectureService.getCategoryStats();

        Map<String, Object> stats = Map.of(
                "byType", typeStats,
                "byCategory", categoryStats
        );

        return ResponseEntity.ok(stats);
    }

    // === 공개/비공개 설정 API ===

    /**
     * 강의 공개
     * PUT /api/lectures/{id}/publish
     */
    @PutMapping("/{id}/publish")
    @Operation(summary = "강의 공개", description = "강의를 공개 상태로 변경합니다.")
    public ResponseEntity<Map<String, String>> publishLecture(
            @Parameter(description = "강의 ID") @PathVariable Long id) {
        try {
            lectureService.publishLecture(id);
            return ResponseEntity.ok(Map.of("message", "강의가 공개되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 공개 중 오류가 발생했습니다."));
        }
    }

    /**
     * 강의 비공개
     * PUT /api/lectures/{id}/unpublish
     */
    @PutMapping("/{id}/unpublish")
    @Operation(summary = "강의 비공개", description = "강의를 비공개 상태로 변경합니다.")
    public ResponseEntity<Map<String, String>> unpublishLecture(
            @Parameter(description = "강의 ID") @PathVariable Long id) {
        try {
            lectureService.unpublishLecture(id);
            return ResponseEntity.ok(Map.of("message", "강의가 비공개되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "강의 비공개 중 오류가 발생했습니다."));
        }
    }

    /**
     * 공개 강의 조회
     * GET /api/lectures/public
     */
    @GetMapping("/public")
    @Operation(summary = "공개 강의 조회", description = "모든 공개 강의를 최신순으로 조회합니다.")
    public ResponseEntity<Map<String, Object>> getPublicLectures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Service에서 트랜잭션 내 DTO 변환된 결과를 받음
        Map<String, Object> response = lectureService.getPublicLectures(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 공개 강의 검색
     * GET /api/lectures/public/search
     */
    @GetMapping("/public/search")
    @Operation(summary = "공개 강의 검색", description = "공개 강의 중에서 조건에 맞는 강의를 검색합니다.")
    public ResponseEntity<Map<String, Object>> searchPublicLectures(
            @Parameter(description = "제목 검색") @RequestParam(required = false) String title,
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) String category,
            @Parameter(description = "난이도 필터") @RequestParam(required = false) String difficulty,
            @Parameter(description = "강의 유형 필터") @RequestParam(required = false) LectureType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Service에서 트랜잭션 내 DTO 변환된 결과를 받음
        Map<String, Object> response = lectureService.searchPublicLectures(title, category, difficulty, type, page, size);
        return ResponseEntity.ok(response);
    }

    // === 사용자별 강의 관리 API ===

    /**
     * 사용자별 강의 목록 조회
     * GET /api/lectures/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 강의 조회", description = "특정 사용자가 작성한 모든 강의를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getUserLectures(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        // Service에서 트랜잭션 내 DTO 변환 및 페이징 결과를 받음
        Map<String, Object> result = lectureService.getUserLectures(userId, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자별 공개 강의 목록 조회
     * GET /api/lectures/user/{userId}/public
     */
    @GetMapping("/user/{userId}/public")
    @Operation(summary = "사용자별 공개 강의 조회", description = "특정 사용자가 작성한 공개 강의만 조회합니다.")
    public ResponseEntity<Map<String, Object>> getUserPublicLectures(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        // Service에서 트랜잭션 내 DTO 변환 및 페이징 결과를 받음
        Map<String, Object> result = lectureService.getUserPublicLectures(userId, page, size);
        return ResponseEntity.ok(result);
    }

}