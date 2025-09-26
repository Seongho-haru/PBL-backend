package com.PBL.lab.grading.controller;

import com.PBL.lab.grading.dto.GradingRequest;
import com.PBL.lab.grading.dto.GradingResponse;
import com.PBL.lab.grading.service.ExecutionGradingService;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.core.service.Base64Service;
import com.PBL.lab.core.service.ConfigService;
import com.PBL.lab.grading.service.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class GradingController {
    private final GradingService gradingService;
    private final ExecutionGradingService executionGradingService;
    private final ConfigService configService;
    private final Base64Service base64Service;

    /**
     * GET /grading
     * 코드 채점 목록을 페이지네이션하여 조회
     */
    @GetMapping("/grading")
    public ResponseEntity<?> index(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "20") int perPage,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        if (page <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid page: " + page));
        }
        if (perPage < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid per_page: " + perPage));
        }

        try {
            Pageable pageable = PageRequest.of(page - 1, perPage);
            Page<Grading> GradingPage = gradingService.findAll(pageable);

            List<GradingResponse> gradings = GradingPage.getContent().stream()
                    .map(grading -> GradingResponse.from(grading, base64_encoded, parseFields(fields)))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("grading", gradings);
            response.put("meta", createPaginationMeta(GradingPage));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "some attributes for one or more gradings cannot be converted to UTF-8, use base64_encoded=true query parameter"
            ));
        }
    }

    /**
     * GET /grading/{token}
     * 토큰으로 체점 조회
     * 1. 이벤트 리스너를 통해서 SSE를 전송
     */
    @GetMapping("/grading/{token}")
    public ResponseEntity<?> show(
            @PathVariable String token,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        try {
            Grading grading = gradingService.findByToken(token);
            //이벤트 리스너 를 통해서 SSE로 메세지 보내기
            //TODO 이벤트 리스너 SSE 구현
            GradingResponse response = GradingResponse.from(grading,base64_encoded, parseFields(fields));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * POST /grading
     * 새로운 체점 생성 
     * 항상 비동기 실행으로 한다.
     */
    @PostMapping("/grading/{problemId}")
    public ResponseEntity<?> create(
            @Valid @RequestBody GradingRequest request,
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "false") boolean wait,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        // 유지보수 모드 확인
        if (configService.isMaintenanceMode()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", configService.getMaintenanceMessage()));
        }

        // wait 파라미터 허용 여부 확인
        if (!configService.isEnableWaitResult()) {
            return ResponseEntity.badRequest().body(Map.of("error", "wait not allowed"));
        }

        // 대기열 크기 확인 (Grading 전용 큐 사용)
        if (gradingService.countGradingInQueue() >= configService.getMaxQueueSize()) {
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
            Grading grading = gradingService.createGrading(request);

            /*
             * 동기 실행
             * 1. 비동기로 실행으로 넘김
             * 2. 기달리면서 이벤트 리스너 와 SSE를 통해서 실시간으로 프로그래스 반영
             * 3. 채점 완료
             */
            if (wait) try {
                // 1. 비동기로 실행을 넘김
                executionGradingService.executeAsync(grading);

                // TODO 2. 이벤트 리스너를 통해 SSE로 요청 보냄
                grading = gradingService.findByToken(grading.getToken());
                GradingResponse response = GradingResponse.from(grading, base64_encoded, parseFields(fields));
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (Exception e) {

                log.error("동기 실행 실패 - grading: {}", grading.getToken(), e);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("token", grading.getToken(),
                                "error", "execution failed, check grading status"));
            }
            else {
                /*
                 * 1. 비동기로 실행을 넘김
                 * 2. 토큰을 반환을 통해서 추후 직접 SSE를 연결할 수있는 토큰 반환
                 */
                executionGradingService.executeAsync(grading);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(GradingResponse.minimal(grading.getToken()));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("채점 생성 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * DELETE /grading/{token}
     * 채점 제출 삭제
     */
    @DeleteMapping("/grading/{token}")
    public ResponseEntity<?> destroy(
            @PathVariable String token,
            @RequestParam(required = false) String fields) {

        // 삭제 기능 활성화 여부 확인
        if (!configService.isSubmissionDeleteEnabled()) {
            return ResponseEntity.badRequest().body(Map.of("error", "delete not allowed"));
        }

        try {
            Grading grading = gradingService.findByToken(token);

            // 삭제 응답에서는 base64_encoded=true 강제 적용
            GradingResponse response = GradingResponse.from(grading, true, parseFields(fields));

            gradingService.deleteGrading(token);

            return ResponseEntity.ok(response);
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
    private Map<String, Object> createPaginationMeta(Page<Grading> page) {
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
