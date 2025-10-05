package com.PBL.curriculum;

import com.PBL.curriculum.CurriculumDTOs.*;
import com.PBL.lecture.Lecture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 커리큘럼 관리 REST API Controller
 * DTO 패턴 사용으로 순환 참조 방지
 */
@RestController
@RequestMapping("/api/curriculums")
@Tag(name = "Curriculums", description = "커리큘럼 관리 API - 강의들을 폴더처럼 묶어서 관리")
public class CurriculumController {

    private final CurriculumService curriculumService;

    @Autowired
    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    // === 커리큘럼 기본 CRUD ===

    @GetMapping
    @Operation(summary = "모든 커리큘럼 조회", description = "시스템에 등록된 모든 커리큘럼을 최신순으로 조회합니다.")
    public ResponseEntity<List<CurriculumResponse>> getAllCurriculums() {
        List<Curriculum> curriculums = curriculumService.getAllCurriculums();
        List<CurriculumResponse> responses = curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public")
    @Operation(summary = "공개 커리큘럼 조회", description = "공개된 커리큘럼만 최신순으로 조회합니다.")
    public ResponseEntity<List<CurriculumResponse>> getPublicCurriculums() {
        List<Curriculum> curriculums = curriculumService.getPublicCurriculums();
        List<CurriculumResponse> responses = curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "커리큘럼 상세 조회", description = "ID로 특정 커리큘럼의 상세 정보를 조회합니다.")
    public ResponseEntity<CurriculumDetailResponse> getCurriculumById(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id) {
        return curriculumService.getCurriculumById(id)
                .map(curriculum -> ResponseEntity.ok(new CurriculumDetailResponse(curriculum)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "커리큘럼 생성", description = "새로운 커리큘럼을 생성합니다.")
    public ResponseEntity<CurriculumResponse> createCurriculum(@RequestBody CreateCurriculumRequest request) {
        try {
            Curriculum curriculum = curriculumService.createCurriculum(
                    request.getTitle(),
                    request.getDescription(),
                    request.isPublic()
            );
            return ResponseEntity.ok(new CurriculumResponse(curriculum));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "커리큘럼 수정", description = "기존 커리큘럼의 정보를 수정합니다.")
    public ResponseEntity<CurriculumResponse> updateCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id,
            @RequestBody UpdateCurriculumRequest request) {
        try {
            Curriculum curriculum = curriculumService.updateCurriculum(
                    id,
                    request.getTitle(),
                    request.getDescription(),
                    request.getIsPublic()
            );
            return ResponseEntity.ok(new CurriculumResponse(curriculum));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "커리큘럼 삭제", description = "커리큘럼을 삭제합니다. 연결된 강의는 삭제되지 않습니다.")
    public ResponseEntity<Void> deleteCurriculum(
            @Parameter(description = "커리큘럼 ID") @PathVariable Long id) {
        try {
            curriculumService.deleteCurriculum(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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
        List<Curriculum> curriculums = curriculumService.searchCurriculums(title);
        List<CurriculumResponse> responses = curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public/search")
    @Operation(summary = "공개 커리큘럼 검색", description = "공개 커리큘럼 중에서 제목으로 검색합니다.")
    public ResponseEntity<List<CurriculumResponse>> searchPublicCurriculums(
            @Parameter(description = "검색할 제목 (부분 일치)") @RequestParam String title) {
        List<Curriculum> curriculums = curriculumService.searchPublicCurriculums(title);
        List<CurriculumResponse> responses = curriculums.stream()
                .map(CurriculumResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // === 공개 강의 조회 (커리큘럼 생성 시 사용) ===

    @GetMapping("/lectures/public")
    @Operation(summary = "공개 강의 조회", description = "커리큘럼에 추가할 수 있는 모든 공개 강의를 조회합니다.")
    public ResponseEntity<List<Map<String, Object>>> getPublicLectures() {
        List<Lecture> lectures = curriculumService.getPublicLectures();
        List<Map<String, Object>> responses = lectures.stream()
                .map(this::toLectureMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/lectures/public/search")
    @Operation(summary = "공개 강의 검색", description = "공개 강의 중에서 조건에 맞는 강의를 검색합니다.")
    public ResponseEntity<List<Map<String, Object>>> searchPublicLectures(
            @Parameter(description = "제목 검색") @RequestParam(required = false) String title,
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) String category,
            @Parameter(description = "난이도 필터") @RequestParam(required = false) String difficulty,
            @Parameter(description = "강의 유형 필터") @RequestParam(required = false) String type) {
        List<Lecture> lectures = curriculumService.searchPublicLectures(title, category, difficulty, type);
        List<Map<String, Object>> responses = lectures.stream()
                .map(this::toLectureMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // === Helper 메서드 ===

    /**
     * Lecture 엔티티를 안전한 Map으로 변환 (Lazy Loading 방지)
     */
    private Map<String, Object> toLectureMap(Lecture lecture) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", lecture.getId());
        map.put("title", lecture.getTitle());
        map.put("description", lecture.getDescription());
        map.put("type", lecture.getType());
        map.put("category", lecture.getCategory());
        map.put("difficulty", lecture.getDifficulty());
        map.put("timeLimit", lecture.getTimeLimit());
        map.put("memoryLimit", lecture.getMemoryLimit());
        map.put("isPublic", lecture.getIsPublic());
        map.put("createdAt", lecture.getCreatedAt());
        map.put("updatedAt", lecture.getUpdatedAt());
        
        // 테스트케이스는 개수만 포함 (Lazy Loading 방지)
        try {
            map.put("testCaseCount", lecture.getTestCases() != null ? lecture.getTestCases().size() : 0);
        } catch (Exception e) {
            map.put("testCaseCount", 0);
        }
        
        return map;
    }
}