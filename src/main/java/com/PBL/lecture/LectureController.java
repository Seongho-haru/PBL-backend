package com.PBL.lecture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@Tag(name = "Lectures", description = "강의 관리 API - 마크다운 강의와 문제 강의를 통합 관리")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // === 기본 CRUD API ===

    /**
     * 강의 생성
     * POST /api/lectures
     */
    @PostMapping
    @Operation(summary = "강의 생성", description = "새로운 강의를 생성합니다. 마크다운 강의 또는 문제 강의를 생성할 수 있습니다.")
    public ResponseEntity<?> createLecture(@RequestBody CreateLectureRequest request) {
        try {
            Lecture lecture;

            if (request.getType() == LectureType.PROBLEM) {
                // 문제 강의 생성
                lecture = lectureService.createProblemLecture(
                        request.getTitle(),
                        request.getDescription(),
                        request.getCategory(),
                        request.getDifficulty(),
                        request.getTimeLimit(),
                        request.getMemoryLimit()
                );

                // 테스트케이스 추가
                if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
                    for (TestCaseRequest testCase : request.getTestCases()) {
                        lectureService.addTestCase(lecture.getId(), testCase.getInput(), testCase.getExpectedOutput());
                    }
                    // 테스트케이스 포함해서 다시 조회
                    lecture = lectureService.getLectureWithTestCases(lecture.getId()).orElse(lecture);
                }
            } else {
                // 마크다운 강의 생성
                lecture = lectureService.createLecture(
                        request.getTitle(),
                        request.getDescription(),
                        request.getType(),
                        request.getCategory(),
                        request.getDifficulty()
                );
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(toLectureResponse(lecture));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
        List<Lecture> lectures = lectureService.getAllLectures();
        List<LectureResponse> responses = lectures.stream()
                .map(this::toLectureResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 강의 상세 조회
     * GET /api/lectures/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "강의 상세 조회", description = "ID로 특정 강의의 상세 정보를 조회합니다. 문제 강의인 경우 테스트케이스도 포함됩니다.")
    public ResponseEntity<?> getLecture(
            @Parameter(description = "강의 ID", required = true) @PathVariable Long id) {
        Optional<Lecture> lecture = lectureService.getLectureWithTestCases(id);

        if (lecture.isPresent()) {
            return ResponseEntity.ok(toLectureResponse(lecture.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 강의 수정
     * PUT /api/lectures/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLecture(@PathVariable Long id, @RequestBody CreateLectureRequest request) {
        try {
            Lecture updateData = new Lecture(request.getTitle(), request.getDescription(), request.getType());
            updateData.setCategory(request.getCategory());
            updateData.setDifficulty(request.getDifficulty());
            updateData.setTimeLimit(request.getTimeLimit());
            updateData.setMemoryLimit(request.getMemoryLimit());

            Lecture updatedLecture = lectureService.updateLecture(id, updateData);
            return ResponseEntity.ok(toLectureResponse(updatedLecture));

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
    public ResponseEntity<?> deleteLecture(@PathVariable Long id) {
        try {
            lectureService.deleteLecture(id);
            return ResponseEntity.ok(Map.of("message", "강의가 성공적으로 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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

        Page<Lecture> lecturesPage = lectureService.searchLectures(title, category, difficulty, type, page, size);

        List<LectureResponse> responses = lecturesPage.getContent().stream()
                .map(this::toLectureResponse)
                .toList();

        Map<String, Object> response = Map.of(
                "lectures", responses,
                "currentPage", lecturesPage.getNumber(),
                "totalElements", lecturesPage.getTotalElements(),
                "totalPages", lecturesPage.getTotalPages(),
                "hasNext", lecturesPage.hasNext(),
                "hasPrevious", lecturesPage.hasPrevious()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 유형별 강의 조회
     * GET /api/lectures/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<LectureResponse>> getLecturesByType(@PathVariable LectureType type) {
        List<Lecture> lectures = lectureService.getLecturesByType(type);
        List<LectureResponse> responses = lectures.stream()
                .map(this::toLectureResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 최근 강의 조회
     * GET /api/lectures/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<List<LectureResponse>> getRecentLectures() {
        List<Lecture> lectures = lectureService.getRecentLectures();
        List<LectureResponse> responses = lectures.stream()
                .map(this::toLectureResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // === 테스트케이스 관리 API ===

    /**
     * 테스트케이스 추가
     * POST /api/lectures/{id}/testcases
     */
    @PostMapping("/{id}/testcases")
    public ResponseEntity<?> addTestCase(@PathVariable Long id, @RequestBody TestCaseRequest request) {
        try {
            Lecture lecture = lectureService.addTestCase(id, request.getInput(), request.getExpectedOutput());
            return ResponseEntity.ok(toLectureResponse(lecture));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 모든 테스트케이스 삭제
     * DELETE /api/lectures/{id}/testcases
     */
    @DeleteMapping("/{id}/testcases")
    public ResponseEntity<?> clearTestCases(@PathVariable Long id) {
        try {
            Lecture lecture = lectureService.clearTestCases(id);
            return ResponseEntity.ok(toLectureResponse(lecture));
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

    // === DTO 변환 메서드 ===

    private LectureResponse toLectureResponse(Lecture lecture) {
        LectureResponse response = new LectureResponse();
        response.setId(lecture.getId());
        response.setTitle(lecture.getTitle());
        response.setDescription(lecture.getDescription());
        response.setType(lecture.getType());
        response.setCategory(lecture.getCategory());
        response.setDifficulty(lecture.getDifficulty());
        response.setTimeLimit(lecture.getTimeLimit());
        response.setMemoryLimit(lecture.getMemoryLimit());
        response.setCreatedAt(lecture.getCreatedAt());
        response.setUpdatedAt(lecture.getUpdatedAt());

        // 테스트케이스 안전하게 처리 - Lazy Loading 방지
        try {
            if (lecture.getTestCases() != null) {
                response.setTestCaseCount(lecture.getTestCases().size());
                List<TestCaseResponse> testCases = lecture.getTestCases().stream()
                        .map(tc -> new TestCaseResponse(tc.getInput(), tc.getExpectedOutput(), tc.getOrderIndex()))
                        .toList();
                response.setTestCases(testCases);
            } else {
                response.setTestCaseCount(0);
                response.setTestCases(new ArrayList<>());
            }
        } catch (Exception e) {
            // Lazy Loading 실패 시 기본값으로 설정
            response.setTestCaseCount(0);
            response.setTestCases(new ArrayList<>());
        }

        return response;
    }
}

// === Request/Response DTO 클래스들 ===

/**
 * 강의 생성/수정 요청 DTO
 */
class CreateLectureRequest {
    private String title;
    private String description;
    private LectureType type;
    private String category;
    private String difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<TestCaseRequest> testCases;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LectureType getType() { return type; }
    public void setType(LectureType type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }
    public Integer getMemoryLimit() { return memoryLimit; }
    public void setMemoryLimit(Integer memoryLimit) { this.memoryLimit = memoryLimit; }
    public List<TestCaseRequest> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseRequest> testCases) { this.testCases = testCases; }
}

/**
 * 강의 응답 DTO
 */
class LectureResponse {
    private Long id;
    private String title;
    private String description;
    private LectureType type;
    private String category;
    private String difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private int testCaseCount;
    private List<TestCaseResponse> testCases;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LectureType getType() { return type; }
    public void setType(LectureType type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }
    public Integer getMemoryLimit() { return memoryLimit; }
    public void setMemoryLimit(Integer memoryLimit) { this.memoryLimit = memoryLimit; }
    public int getTestCaseCount() { return testCaseCount; }
    public void setTestCaseCount(int testCaseCount) { this.testCaseCount = testCaseCount; }
    public List<TestCaseResponse> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseResponse> testCases) { this.testCases = testCases; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * 테스트케이스 요청 DTO
 */
class TestCaseRequest {
    private String input;
    private String expectedOutput;

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
}

/**
 * 테스트케이스 응답 DTO
 */
class TestCaseResponse {
    private String input;
    private String expectedOutput;
    private Integer orderIndex;

    public TestCaseResponse() {}

    public TestCaseResponse(String input, String expectedOutput, Integer orderIndex) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.orderIndex = orderIndex;
    }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}

// === 강의 공개/비공개 설정 API 추가 ===
// LectureController 클래스 내부에 다음 메소드들을 추가해야 합니다:
/*
    @PutMapping("/{id}/publish")
    @Operation(summary = "강의 공개", description = "강의를 공개 상태로 변경합니다.")
    public ResponseEntity<Map<String, String>> publishLecture(@PathVariable Long id) {
        // 구현 필요
    }

    @PutMapping("/{id}/unpublish") 
    @Operation(summary = "강의 비공개", description = "강의를 비공개 상태로 변경합니다.")
    public ResponseEntity<Map<String, String>> unpublishLecture(@PathVariable Long id) {
        // 구현 필요
    }

    @GetMapping("/public")
    @Operation(summary = "공개 강의 조회", description = "모든 공개 강의를 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getPublicLectures() {
        // 구현 필요
    }
*/
