package com.PBL.curriculum;

import com.PBL.curriculum.CurriculumDTOs.*;
import com.PBL.user.service.UserValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 커리큘럼 관리 REST API Controller
 * DTO 패턴 사용으로 순환 참조 방지
 */
@RestController
@RequestMapping("/api/curriculums")
@Slf4j
@Tag(name = "Curriculums", description = "커리큘럼 관리 API - 강의들을 폴더처럼 묶어서 관리")
public class CurriculumController {

    private final CurriculumService curriculumService;
    private final UserValidationService userValidationService;

    @Autowired
    public CurriculumController(CurriculumService curriculumService, UserValidationService userValidationService) {
        this.curriculumService = curriculumService;
        this.userValidationService = userValidationService;
    }

    // === 커리큘럼 기본 CRUD ===

    @GetMapping
    @Operation(summary = "모든 커리큘럼 조회", description = "시스템에 등록된 모든 커리큘럼을 최신순으로 조회합니다.")
    public ResponseEntity<List<CurriculumResponse>> getAllCurriculums() {
        List<CurriculumResponse> responses = curriculumService.getAllCurriculums();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public")
    @Operation(summary = "공개 커리큘럼 조회", description = "공개된 커리큘럼만 최신순으로 조회합니다.")
    public ResponseEntity<Map<String, Object>> getPublicCurriculums(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = curriculumService.getPublicCurriculums(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "커리큘럼 상세 조회", description = "ID로 특정 커리큘럼의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getCurriculumById(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            CurriculumDetailResponse response = curriculumService.getCurriculumByIdWithAuth(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "커리큘럼 생성", description = "새로운 커리큘럼을 생성합니다.")
    public ResponseEntity<?> createCurriculum(
            @RequestBody CreateCurriculumRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // 정지 상태 체크
            if (userId != null) {
                userValidationService.validateUserCanCreateContent(userId);
            }
            
            CurriculumResponse response = curriculumService.createCurriculumWithAuth(request, userId);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // 정지된 사용자 또는 기타 런타임 예외
            if (e.getMessage() != null && e.getMessage().contains("정지")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "커리큘럼 수정", description = "기존 커리큘럼의 정보를 수정합니다.")
    public ResponseEntity<?> updateCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id,
            @RequestBody UpdateCurriculumRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            CurriculumResponse response = curriculumService.updateCurriculumWithAuth(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            if (e.getMessage().contains("인증")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "커리큘럼 삭제", description = "커리큘럼을 삭제합니다. 연결된 강의는 삭제되지 않습니다.")
    public ResponseEntity<?> deleteCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            curriculumService.deleteCurriculumWithAuth(id, userId);
            return ResponseEntity.ok(Map.of("message", "커리큘럼이 성공적으로 삭제되었습니다."));
        } catch (SecurityException e) {
            if (e.getMessage().contains("인증")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "커리큘럼 삭제 중 오류가 발생했습니다."));
        }
    }

    // === 강의 연결 관리 ===

    @PostMapping("/{id}/lectures")
    @Operation(summary = "커리큘럼에 강의 추가", description = "커리큘럼에 강의를 추가합니다. 공개 강의의 경우 원작자 정보를 포함할 수 있습니다.")
    public ResponseEntity<Map<String, String>> addLectureToCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id,
            @RequestBody AddLectureRequest request) {
        try {
            curriculumService.addLectureToCurriculum(
                    id,
                    request.getLectureId(),
                    request.isRequired(),
                    request.getOriginalAuthor(),
                    request.getSourceInfo()
            );
            return ResponseEntity.ok(Map.of("message", "강의가 성공적으로 추가되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{curriculumId}/lectures/{lectureId}")
    @Operation(summary = "커리큘럼에서 강의 제거", description = "커리큘럼에서 특정 강의를 제거합니다.")
    public ResponseEntity<Map<String, String>> removeLectureFromCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long curriculumId,
            @Parameter(description = "강의 ID") @PathVariable Long lectureId) {
        try {
            curriculumService.removeLectureFromCurriculum(curriculumId, lectureId);
            return ResponseEntity.ok(Map.of("message", "강의가 성공적으로 제거되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/lectures/reorder")
    @Operation(summary = "커리큘럼 내 강의 순서 변경", description = "커리큘럼 내 강의들의 순서를 변경합니다.")
    public ResponseEntity<Map<String, String>> reorderLectures(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id,
            @RequestBody ReorderLecturesRequest request) {
        try {
            curriculumService.reorderLecturesInCurriculum(id, request.getLectureIds());
            return ResponseEntity.ok(Map.of("message", "강의 순서가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // === 공개/비공개 설정 ===

    @PutMapping("/{id}/publish")
    @Operation(summary = "커리큘럼 공개", description = "커리큘럼을 공개 상태로 변경합니다.")
    public ResponseEntity<Map<String, String>> publishCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id) {
        try {
            curriculumService.publishCurriculum(id);
            return ResponseEntity.ok(Map.of("message", "커리큘럼이 공개되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/unpublish")
    @Operation(summary = "커리큘럼 비공개", description = "커리큘럼을 비공개 상태로 변경합니다.")
    public ResponseEntity<Map<String, String>> unpublishCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id) {
        try {
            curriculumService.unpublishCurriculum(id);
            return ResponseEntity.ok(Map.of("message", "커리큘럼이 비공개되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // === 검색 기능 ===

    @GetMapping("/search")
    @Operation(summary = "커리큘럼 검색", description = "제목으로 커리큘럼을 검색합니다.")
    public ResponseEntity<List<CurriculumResponse>> searchCurriculums(
            @Parameter(description = "검색할 제목 (부분 일치)") @RequestParam String title) {
        List<CurriculumResponse> responses = curriculumService.searchCurriculums(title);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public/search")
    @Operation(summary = "공개 커리큘럼 검색", description = "공개 커리큘럼 중에서 제목으로 검색합니다.")
    public ResponseEntity<Map<String, Object>> searchPublicCurriculums(
            @Parameter(description = "검색할 제목 (부분 일치)") @RequestParam String title,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = curriculumService.searchPublicCurriculums(title, page, size);
        return ResponseEntity.ok(result);
    }

    // === 공개 강의 조회 (커리큘럼 생성 시 사용) ===

    @GetMapping("/lectures/public")
    @Operation(summary = "공개 강의 조회", description = "커리큘럼에 추가할 수 있는 모든 공개 강의를 조회합니다.")
    public ResponseEntity<List<Map<String, Object>>> getPublicLectures() {
        List<Map<String, Object>> responses = curriculumService.getPublicLectures();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/lectures/public/search")
    @Operation(summary = "공개 강의 검색", description = "공개 강의 중에서 조건에 맞는 강의를 검색합니다.")
    public ResponseEntity<List<Map<String, Object>>> searchPublicLectures(
            @Parameter(description = "제목 검색") @RequestParam(required = false) String title,
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) String category,
            @Parameter(description = "난이도 필터") @RequestParam(required = false) String difficulty,
            @Parameter(description = "강의 유형 필터") @RequestParam(required = false) String type) {
        List<Map<String, Object>> responses = curriculumService.searchPublicLectures(title, category, difficulty, type);
        return ResponseEntity.ok(responses);
    }

    // === 사용자별 커리큘럼 관리 API ===

    /**
     * 사용자별 커리큘럼 목록 조회
     * GET /api/curriculums/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자별 커리큘럼 조회", description = "특정 사용자가 작성한 모든 커리큘럼을 조회합니다.")
    public ResponseEntity<Map<String, Object>> getUserCurriculums(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = curriculumService.getUserCurriculums(userId, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자별 공개 커리큘럼 목록 조회
     * GET /api/curriculums/user/{userId}/public
     */
    @GetMapping("/user/{userId}/public")
    @Operation(summary = "사용자별 공개 커리큘럼 조회", description = "특정 사용자가 작성한 공개 커리큘럼만 조회합니다.")
    public ResponseEntity<Map<String, Object>> getUserPublicCurriculums(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = curriculumService.getUserPublicCurriculums(userId, page, size);
        return ResponseEntity.ok(result);
    }

    /*
     * 다음 강의, 이전강의 조회
     */
    @GetMapping("/{curriculumId}/lectures/{lectureId}/navigation")
    @Operation
    public ResponseEntity<CurriculumDTOs.CurriculumNextLecture> getLectureNavigation(
            @PathVariable Long curriculumId, @PathVariable Long lectureId
    ){
        return ResponseEntity.ok(curriculumService.navigationLectures(curriculumId, lectureId));
    }
}