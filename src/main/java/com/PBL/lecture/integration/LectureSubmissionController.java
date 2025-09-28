package com.PBL.lecture.integration;

import com.PBL.lab.judge0.dto.SubmissionResponse;
import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 강의-제출 통합 API 컨트롤러
 * 강의 조회와 코드 제출을 통합하여 제공하는 API
 */
@RestController
@RequestMapping("/api/lectures")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lecture Submissions", description = "강의 문제 제출 API - 강의와 Judge0 시스템 통합")
public class LectureSubmissionController {

    private final LectureService lectureService;
    private final LectureSubmissionService lectureSubmissionService;

    /**
     * 강의 문제에 코드 제출
     * POST /api/lectures/{id}/submit
     */
    @PostMapping("/{id}/submit")
    @Operation(summary = "강의 문제에 코드 제출", description = "특정 강의의 첫 번째 테스트케이스로 코드를 제출합니다.")
    public ResponseEntity<?> submitCode(
            @Parameter(description = "강의 ID", required = true) @PathVariable Long id,
            @RequestBody CodeSubmissionRequest request) {
        try {
            SubmissionResponse response = lectureSubmissionService.submitCodeForLecture(
                    id, 
                    request.getSourceCode(), 
                    request.getLanguageId()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "코드가 성공적으로 제출되었습니다.",
                    "submission", response
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("코드 제출 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "코드 제출 중 오류가 발생했습니다."));
        }
    }

    /**
     * 강의 문제에 모든 테스트케이스로 코드 제출
     * POST /api/lectures/{id}/submit-all
     */
    @PostMapping("/{id}/submit-all")
    public ResponseEntity<?> submitCodeForAllTestCases(
            @PathVariable Long id,
            @RequestBody CodeSubmissionRequest request) {
        try {
            List<SubmissionResponse> responses = lectureSubmissionService.submitCodeForAllTestCases(
                    id, 
                    request.getSourceCode(), 
                    request.getLanguageId()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "모든 테스트케이스에 대해 코드가 제출되었습니다.",
                    "submissions", responses,
                    "totalTestCases", responses.size()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("배치 코드 제출 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "코드 제출 중 오류가 발생했습니다."));
        }
    }

    /**
     * 강의 문제 정보 조회 (제출용)
     * GET /api/lectures/{id}/problem-info
     */
    @GetMapping("/{id}/problem-info")
    public ResponseEntity<?> getProblemInfo(@PathVariable Long id) {
        try {
            Optional<Lecture> lectureOpt = lectureService.getLectureWithTestCases(id);
            if (lectureOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Lecture lecture = lectureOpt.get();
            if (!lecture.isProblemType()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "문제 타입 강의가 아닙니다."));
            }

            var limits = lectureSubmissionService.getLectureLimits(id);

            return ResponseEntity.ok(Map.of(
                    "id", lecture.getId(),
                    "title", lecture.getTitle(),
                    "description", lecture.getDescription(),
                    "category", lecture.getCategory(),
                    "difficulty", lecture.getDifficulty(),
                    "timeLimit", limits.timeLimit(),
                    "memoryLimit", limits.memoryLimit(),
                    "testCaseCount", limits.testCaseCount(),
                    "sampleInput", lecture.getTestCases().isEmpty() ? "" : lecture.getTestCases().get(0).getInput(),
                    "sampleOutput", lecture.getTestCases().isEmpty() ? "" : lecture.getTestCases().get(0).getExpectedOutput()
            ));

        } catch (Exception e) {
            log.error("문제 정보 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문제 정보 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 문제 타입 강의 목록 조회
     * GET /api/lectures/problems
     */
    @GetMapping("/problems")
    public ResponseEntity<?> getProblemLectures() {
        try {
            List<Lecture> problemLectures = lectureService.getProblemLecturesWithTestCases();
            
            List<Map<String, Object>> response = problemLectures.stream()
                    .<Map<String, Object>>map(lecture -> Map.of(
                            "id", lecture.getId(),
                            "title", lecture.getTitle(),
                            "category", lecture.getCategory() != null ? lecture.getCategory() : "",
                            "difficulty", lecture.getDifficulty() != null ? lecture.getDifficulty() : "",
                            "timeLimit", lecture.getTimeLimit() != null ? lecture.getTimeLimit() : 0,
                            "memoryLimit", lecture.getMemoryLimit() != null ? lecture.getMemoryLimit() : 0,
                            "testCaseCount", lecture.getTestCaseCount(),
                            "createdAt", lecture.getCreatedAt()
                    ))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("문제 강의 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문제 강의 목록 조회 중 오류가 발생했습니다."));
        }
    }
}

/**
 * 코드 제출 요청 DTO
 */
class CodeSubmissionRequest {
    private String sourceCode;
    private Integer languageId;

    // Getters and Setters
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
    public Integer getLanguageId() { return languageId; }
    public void setLanguageId(Integer languageId) { this.languageId = languageId; }
}
