package com.PBL.lab.grade.controller;

import com.PBL.lab.core.config.FeatureFlagsConfig;
import com.PBL.lab.core.config.SystemConfig;
import com.PBL.lab.grade.dto.GradeRequest;
import com.PBL.lab.grade.dto.GradeResponse;
import com.PBL.lab.grade.service.GradeExecutionService;
import com.PBL.lab.grade.entity.Grade;
import com.PBL.lab.core.service.Base64Service;
import com.PBL.lab.grade.service.GradeService;
import com.PBL.lab.grade.service.GradeProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class GradeController {
    private final GradeService gradeService;
    private final GradeExecutionService gradeExecutionService;
    private final SystemConfig systemConfig;
    private final FeatureFlagsConfig featureFlagsConfig;
    private final Base64Service base64Service;
    private final GradeProgressService gradeProgressService;

    /**
     * GET /grade, /grading
     * 코드 채점 목록을 페이지네이션하여 조회
     *
     * 필터링 규칙:
     * - X-User-Id 없음: user가 null인 채점만 (익명 채점만)
     * - X-User-Id: 1: User 1의 채점만
     * - X-User-Id 없음 + problem_id=5: 문제 5의 익명 채점만
     * - X-User-Id: 1 + problem_id=5: User 1의 문제 5 채점만
     */
    @GetMapping({"/grade", "/grading"})
    public ResponseEntity<?> index(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "problem_id" , required = false) Long problemId,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        try {
            Page<Grade> gradePage;

            // 1. X-User-Id 있음 + problem_id 있음: 특정 사용자의 특정 문제 채점만
            if (userId != null && problemId != null) {
                gradePage = gradeService.findByUserIdAndProblemId(userId, problemId, pageable);
            }
            // 2. X-User-Id 있음 + problem_id 없음: 특정 사용자의 전체 채점
            else if (userId != null) {
                gradePage = gradeService.findByUserId(userId, pageable);
            }
            // 3. X-User-Id 없음 + problem_id 있음: 특정 문제의 익명 채점만
            else if (problemId != null) {
                gradePage = gradeService.findAnonymousGradesByProblemId(problemId, pageable);
            }
            // 4. X-User-Id 없음 + problem_id 없음: 익명 채점만
            else {
                gradePage = gradeService.findAnonymousGrades(pageable);
            }

            List<GradeResponse> grades = gradePage.getContent().stream()
                    .map(grade -> GradeResponse.from(grade, base64_encoded, parseFields(fields)))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("grade", grades);
            response.put("meta", createPaginationMeta(gradePage));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "some attributes for one or more grades cannot be converted to UTF-8, use base64_encoded=true query parameter"
            ));
        }
    }

    /**
     * GET /grade/{token}, /grading/{token}
     * 토큰으로 채점 조회
     * progress=true 파라미터가 있으면 SSE로 실시간 진행상황 전송
     */
    @GetMapping({"/grade/{token}", "/grading/{token}"})
    public ResponseEntity<?> show(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable String token,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(defaultValue = "false") boolean progress,
            @RequestParam(required = false) String fields) {

        try {
            Grade grade = gradeService.findByToken(token);

            // 접근 권한 검증
            gradeService.validateAccess(grade, userId);

            // progress=true이고 채점이 아직 완료되지 않은 경우 SSE로 전환
            if (progress && !grade.getStatus().isTerminal()) {
                // SSE 엔드포인트로 리다이렉트
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", "/grade/" + token + "/progress")
                        .build();
            }

            // 일반 JSON 응답
            GradeResponse response = GradeResponse.from(grade, base64_encoded, parseFields(fields));
            return ResponseEntity.ok(response);
        } catch (com.PBL.lab.core.exception.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * POST /grade/{problemId}, /grading/{problemId}
     * 새로운 채점 생성
     * 항상 비동기 실행으로 한다.
     */
    @PostMapping({"/grade/{problemId}", "/grading/{problemId}"})
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody GradeRequest request,
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "false") boolean wait,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        // 유지보수 모드 확인
        if (systemConfig.isMaintenanceMode()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", systemConfig.getMaintenanceMessage()));
        }

        // wait 파라미터 허용 여부 확인
        if (!featureFlagsConfig.isEnableWaitResult()) {
            return ResponseEntity.badRequest().body(Map.of("error", "wait not allowed"));
        }

        // 대기열 크기 확인 (Grade 전용 큐 사용)
        if (gradeService.countGradeInQueue() >= systemConfig.getMaxQueueSize()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "queue is full"));
        }

        try {
            // Base64 디코딩 처리 (요청 시)
            if (base64_encoded) {
                request.setSourceCode(base64Service.decode(request.getSourceCode()));
            }
            //제출 토큰 생성
            request.setProblemId(problemId);
            Grade grade = gradeService.createGrade(request, userId);

            /*
             * 동기 실행 (wait=true)
             * 1. 비동기로 실행을 넘김
             * 2. SSE를 통해 실시간으로 프로그래스 반영
             * 3. 채점 완료 시 최종 결과 반환
             */
            if (wait) {
                try {
                    // 1. 비동기로 실행을 넘김
                    gradeExecutionService.schedule(grade.getToken());

                    // 2. SSE 엔드포인트로 리다이렉트
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .header("Location", "/grade/" + grade.getToken() + "/progress")
                            .build();
                } catch (Exception e) {
                    log.error("동기 실행 실패 - grade: {}", grade.getToken(), e);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.of("token", grade.getToken(),
                                    "error", "execution failed, check grade status"));
                }
            }
            else {
                /*
                 * 1. 비동기로 실행을 넘김
                 * 2. 토큰을 반환을 통해서 추후 직접 SSE를 연결할 수있는 토큰 반환
                 */
                gradeExecutionService.schedule(grade.getToken());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(GradeResponse.minimal(grade.getToken()));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("채점 생성 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * SSE를 통한 채점 진행상황 실시간 스트림
     */
    @GetMapping(value = {"/grade/{token}/progress", "/grading/{token}/progress"}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getGradeProgressSSE(
            @PathVariable String token,
            boolean base64_encoded,
            String fields) {
        try {
            // 채점이 존재하는지 확인 (존재하지 않으면 IllegalArgumentException 발생)
            gradeService.findByToken(token);

            // SSE 연결 등록 및 Emitter 반환
            // (내부적으로 onCompletion, onTimeout, onError 핸들러 설정됨)
            return gradeProgressService.registerProgressListener(token);

        } catch (com.PBL.lab.core.exception.AccessDeniedException e) {
            log.error("Access denied for grade: {}", token);
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(new RuntimeException("Access denied: " + e.getMessage()));
            return emitter;
        } catch (IllegalArgumentException e) {
            log.error("Grade not found: {}", token);
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(new RuntimeException("Grade not found: " + token));
            return emitter;
        } catch (Exception e) {
            log.error("SSE connection setup failed for grade: {}", token, e);
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }

    /**
     * DELETE /grade/{token}, /grading/{token}
     * 채점 제출 삭제
     */
    @DeleteMapping({"/grade/{token}", "/grading/{token}"})
    public ResponseEntity<?> destroy(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable String token,
            @RequestParam(required = false) String fields) {

        // 삭제 기능 활성화 여부 확인
        if (!featureFlagsConfig.isEnableSubmissionDelete()) {
            return ResponseEntity.badRequest().body(Map.of("error", "delete not allowed"));
        }

        try {
            Grade grade = gradeService.findByToken(token);

            // 접근 권한 검증
            gradeService.validateAccess(grade, userId);

            // 삭제 응답에서는 base64_encoded=true 강제 적용
            GradeResponse response = GradeResponse.from(grade, true, parseFields(fields));

            gradeService.deleteGrade(token);

            return ResponseEntity.ok(response);
        } catch (com.PBL.lab.core.exception.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * fields 파라미터 파싱
     */
    private String[] parseFields(String fields) {
        if (fields == null || fields.trim().isEmpty()) {
            return null;
        }
        return fields.split(",");
    }

    /**
     * 페이지네이션 메타데이터 생성
     */
    private Map<String, Object> createPaginationMeta(Page<Grade> page) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("current_page", page.getNumber() + 1);
        meta.put("next_page", page.hasNext() ? page.getNumber() + 2 : null);
        meta.put("prev_page", page.hasPrevious() ? page.getNumber() : null);
        meta.put("total_pages", page.getTotalPages());
        meta.put("total_count", page.getTotalElements());
        meta.put("per_page", page.getSize());
        return meta;
    }

}
