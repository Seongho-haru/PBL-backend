package com.PBL.lab.judge0.controller;

import com.PBL.lab.core.config.FeatureFlagsConfig;
import com.PBL.lab.core.config.SystemConfig;
import com.PBL.lab.judge0.dto.SubmissionRequest;
import com.PBL.lab.judge0.dto.SubmissionResponse;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.service.SubmissionExecutionService;
import com.PBL.lab.judge0.service.SubmissionService;
import com.PBL.lab.core.service.Base64Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Submissions Controller - 코드 제출 및 관리 REST API 컨트롤러
 *
 * 목적:
 * - 코드 제출(submission)과 관련된 모든 REST API 엔드포인트 제공
 * - 원본 Judge0 Ruby SubmissionsController의 완전한 Java 포팅
 * - 단일 및 배치 제출 처리, 동기/비동기 실행 지원
 *
 * 핵심 기능:
 * - POST /submissions: 새로운 코드 제출 생성 (동기/비동기 실행 선택 가능)
 * - GET /submissions/{token}: 특정 제출의 결과 조회
 * - GET /submissions: 페이지네이션으로 제출 목록 조회
 * - DELETE /submissions/{token}: 제출 삭제 (설정에 따라 활성화)
 * - POST /submissions/batch: 다중 제출 일괄 생성
 * - GET /submissions/batch: 다중 제출 결과 일괄 조회
 *
 * 주요 특징:
 * - Base64 인코딩/디코딩 지원으로 UTF-8 문자 처리
 * - 유연한 필드 선택(fields 파라미터)을 통한 응답 커스터마이징
 * - 자동 페이지네이션 및 메타데이터 제공
 * - 포괄적인 오류 처리 및 사용자 친화적 오류 메시지
 * - 시스템 자원 보호를 위한 대기열 크기 제한
 *
 * 보안 및 제한 사항:
 * - 유지보수 모드 및 대기열 포화 상태 점검
 * - 제출 처리량 제한을 통한 시스템 보호
 * - 입력 데이터 유효성 검증 및 상세한 오류 보고
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class SubmissionsController {
    private final SubmissionService submissionService;
    private final SubmissionExecutionService submissionExecutionService;
    private final SystemConfig systemConfig;
    private final FeatureFlagsConfig featureFlagsConfig;
    private final Base64Service base64Service;

    /**
     * GET /submissions
     * 제출 목록을 페이지네이션하여 조회
     */
    @GetMapping("/submissions")
    public ResponseEntity<?> index(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        try {
            Page<Submission> submissionPage = submissionService.findAll(pageable);

            List<SubmissionResponse> submissions = submissionPage.getContent().stream()
                    .map(submission -> SubmissionResponse.from(submission, base64_encoded, parseFields(fields)))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("submissions", submissions);
            response.put("meta", createPaginationMeta(submissionPage));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "some attributes for one or more submissions cannot be converted to UTF-8, use base64_encoded=true query parameter"
            ));
        }
    }

    /**
     * GET /submissions/{token}
     * 토큰으로 단일 제출 조회
     */
    @GetMapping("/submissions/{token}")
    public ResponseEntity<?> show(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable String token,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        try {
            Submission submission = submissionService.findByToken(token);

            // 접근 권한 검증
            submissionService.validateAccess(submission, userId);

            SubmissionResponse response = SubmissionResponse.from(submission, base64_encoded, parseFields(fields));
            return ResponseEntity.ok(response);
        } catch (com.PBL.lab.core.exception.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "some attributes for this submission cannot be converted to UTF-8, use base64_encoded=true query parameter"
            ));
        }
    }

    /**
     * POST /submissions
     * 단일 새로운 제출 생성
     */
    @PostMapping("/submissions")
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody SubmissionRequest request,
            @RequestParam(defaultValue = "false") boolean wait,
            @RequestParam(defaultValue = "false") boolean base64_encoded,
            @RequestParam(required = false) String fields) {

        // 유지보수 모드 확인
        if (systemConfig.isMaintenanceMode()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", systemConfig.getMaintenanceMessage()));
        }

        // wait 파라미터 허용 여부 확인
        if (wait && !featureFlagsConfig.isEnableWaitResult()) {
            return ResponseEntity.badRequest().body(Map.of("error", "wait not allowed"));
        }

        // 대기열 크기 확인
        if (submissionService.countSubmissionsInQueue() >= systemConfig.getMaxQueueSize()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "queue is full"));
        }

        try {
            // Base64 디코딩 처리 (요청 시)
            if (base64_encoded) {
                request.setSourceCode(base64Service.decode(request.getSourceCode()));
                request.setStdin(base64Service.decode(request.getStdin()));
                request.setExpectedOutput(base64Service.decode(request.getExpectedOutput()));
            }

            // 제출 생성
            Submission submission = submissionService.createSubmission(request, userId);

            if (wait) {
                // 동기 실행
                try {
                    submissionExecutionService.executeSync(submission);
                    submission = submissionService.findByToken(submission.getToken());
                    SubmissionResponse response = SubmissionResponse.from(submission, base64_encoded, parseFields(fields));
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                } catch (Exception e) {
                    log.error("동기 실행 실패 - submission: {}", submission.getToken(), e);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.of("token", submission.getToken(),
                                    "error", "execution failed, check submission status"));
                }
            } else {
                // 비동기 실행
                submissionExecutionService.schedule(submission.getToken());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(SubmissionResponse.minimal(submission.getToken()));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("제출 생성 실패", e);
            log.info(e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    /**
     * DELETE /submissions/{token}
     * 제출 삭제
     */
    @DeleteMapping("/submissions/{token}")
    public ResponseEntity<?> destroy(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable String token,
            @RequestParam(required = false) String fields) {

        // 삭제 기능 활성화 여부 확인
        if (!featureFlagsConfig.isEnableSubmissionDelete()) {
            return ResponseEntity.badRequest().body(Map.of("error", "delete not allowed"));
        }

        try {
            Submission submission = submissionService.findByToken(token);

            // 접근 권한 검증
            submissionService.validateAccess(submission, userId);

            // 삭제 응답에서는 base64_encoded=true 강제 적용
            SubmissionResponse response = SubmissionResponse.from(submission, true, parseFields(fields));

            submissionService.deleteSubmission(token);

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
     * POST /submissions/batch
     * 다중 제출 일괄 생성
     * 다중 제출 필요성 필요없다고 생각되어서 주석처리 다양한언어를 일괄처리시 사용
     */
//    @PostMapping("/submissions/batch")
//    public ResponseEntity<?> batchCreate(@RequestBody Map<String, List<SubmissionRequest>> requestBody) {
//
//        // 배치 제출 기능 활성화 여부 확인
//        if (!configService.isBatchedSubmissionsEnabled()) {
//            return ResponseEntity.badRequest().body(Map.of("error", "batched submissions are not allowed"));
//        }
//
//        List<SubmissionRequest> submissions = requestBody.get("submissions");
//        if (submissions == null) {
//            submissions = new ArrayList<>();
//        }
//
//        int numberOfSubmissions = submissions.size();
//
//        // 배치 크기 제한 확인
//        if (numberOfSubmissions > configService.getMaxSubmissionBatchSize()) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", "number of submissions in a batch should be less than or equal to " +
//                            configService.getMaxSubmissionBatchSize()
//            ));
//        }
//
//        if (numberOfSubmissions == 0) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", "there should be at least one submission in a batch"
//            ));
//        }
//
//        // 전체 대기열 크기 확인
//        if (submissionService.countSubmissionsInQueue() + numberOfSubmissions > configService.getMaxQueueSize()) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//                    .body(Map.of("error", "queue is full"));
//        }
//
//        List<Object> response = new ArrayList<>();
//        boolean hasValidSubmission = false;
//
//        for (SubmissionRequest request : submissions) {
//            try {
//                Submission submission = submissionService.createSubmission(request);
//                executionService.executeAsync(submission);
//                response.add(Map.of("token", submission.getToken()));
//                hasValidSubmission = true;
//            } catch (Exception e) {
//                response.add(Map.of("error", e.getMessage()));
//            }
//        }
//
//        HttpStatus status = hasValidSubmission ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;
//        return ResponseEntity.status(status).body(response);
//    }

    /**
     * GET /submissions/batch
     * 다중 제출 일괄 조회
     */
//    @GetMapping("/submissions/batch")
//    public ResponseEntity<?> batchShow(
//            @RequestParam(required = false) String tokens,
//            @RequestHeader(value = "tokens", required = false) String headerTokens,
//            @RequestParam(defaultValue = "false") boolean base64_encoded,
//            @RequestParam(required = false) String fields) {
//
//        // 배치 조회 기능 활성화 여부 확인
//        if (!configService.isBatchedSubmissionsEnabled()) {
//            return ResponseEntity.badRequest().body(Map.of("error", "batched submissions are not allowed"));
//        }
//
//        String tokenStr = tokens != null ? tokens : headerTokens;
//        if (tokenStr == null) {
//            tokenStr = "";
//        }
//
//        List<String> tokenList = Arrays.stream(tokenStr.split(","))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .collect(Collectors.toList());
//
//        // 배치 크기 제한 확인
//        if (tokenList.size() > configService.getMaxSubmissionBatchSize()) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", "number of submissions in a batch should be less than or equal to " +
//                            configService.getMaxSubmissionBatchSize()
//            ));
//        }
//
//        if (tokenList.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", "there should be at least one submission in a batch"
//            ));
//        }
//
//        try {
//            Map<String, Submission> submissionMap = submissionService.findByTokens(tokenList);
//
//            List<Object> submissions = new ArrayList<>();
//            for (String token : tokenList) {
//                if (submissionMap.containsKey(token)) {
//                    Submission submission = submissionMap.get(token);
//                    submissions.add(SubmissionResponse.from(submission, base64_encoded, parseFields(fields)));
//                } else {
//                    submissions.add(null);
//                }
//            }
//
//            return ResponseEntity.ok(Map.of("submissions", submissions));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "error", "some attributes for one or more submissions cannot be converted to UTF-8, use base64_encoded=true query parameter"
//            ));
//        }
//    }


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
    private Map<String, Object> createPaginationMeta(Page<Submission> page) {
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