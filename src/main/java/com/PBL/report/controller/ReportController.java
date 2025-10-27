package com.PBL.report.controller;

import com.PBL.report.dto.ReportDTOs;
import com.PBL.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 신고 Controller
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report Management", description = "신고 관리 API")
@Slf4j
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 임시 사용자 ID (실제로는 JWT 토큰에서 추출)
    private Long getCurrentUserId() {
        return 1L; // TODO: 실제 인증 구현
    }

    /**
     * 신고 작성
     */
    @PostMapping
    @Operation(summary = "신고 작성", description = "부적절한 콘텐츠를 신고합니다.")
    public ResponseEntity<ReportDTOs.ReportResponse> createReport(
            @RequestBody ReportDTOs.CreateReportRequest request
    ) {
        log.info("신고 작성 요청");
        ReportDTOs.ReportResponse response = reportService.createReport(getCurrentUserId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자 - 신고 목록 조회
     */
    @GetMapping
    @Operation(summary = "신고 목록 조회 (관리자)", description = "관리자가 신고 목록을 조회합니다.")
    public ResponseEntity<Page<ReportDTOs.ReportResponse>> getReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String targetType,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("신고 목록 조회 - status: {}, targetType: {}", status, targetType);
        
        // 관리자 권한 체크
        if (!isAdmin(getCurrentUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        Page<ReportDTOs.ReportResponse> reports = reportService.getReports(status, targetType, pageable);
        return ResponseEntity.ok(reports);
    }

    /**
     * 관리자 - 신고 상세 조회
     */
    @GetMapping("/{id}")
    @Operation(summary = "신고 상세 조회 (관리자)", description = "관리자가 신고 상세 정보를 조회합니다.")
    public ResponseEntity<ReportDTOs.ReportResponse> getReport(@PathVariable Long id) {
        log.info("신고 상세 조회 - ID: {}", id);
        
        // 관리자 권한 체크
        if (!isAdmin(getCurrentUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        ReportDTOs.ReportResponse report = reportService.getReport(id);
        return ResponseEntity.ok(report);
    }

    /**
     * 관리자 - 신고 처리
     */
    @PutMapping("/{id}/process")
    @Operation(summary = "신고 처리 (관리자)", description = "관리자가 신고를 처리합니다.")
    public ResponseEntity<ReportDTOs.ReportResponse> processReport(
            @PathVariable Long id,
            @RequestBody ReportDTOs.ProcessReportRequest request
    ) {
        log.info("신고 처리 요청 - ID: {}", id);
        
        // 관리자 권한 체크
        if (!isAdmin(getCurrentUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        ReportDTOs.ReportResponse response = reportService.processReport(id, getCurrentUserId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자 - 신고 통계 조회
     */
    @GetMapping("/stats")
    @Operation(summary = "신고 통계 조회 (관리자)", description = "관리자가 신고 통계를 조회합니다.")
    public ResponseEntity<ReportDTOs.ReportStatsResponse> getReportStats() {
        log.info("신고 통계 조회");
        
        // 관리자 권한 체크
        if (!isAdmin(getCurrentUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        ReportDTOs.ReportStatsResponse stats = reportService.getReportStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 사용자 - 내 신고 목록 조회
     */
    @GetMapping("/my")
    @Operation(summary = "내 신고 목록 조회", description = "본인이 작성한 신고 목록을 조회합니다.")
    public ResponseEntity<List<ReportDTOs.ReportResponse>> getMyReports() {
        log.info("내 신고 목록 조회");
        List<ReportDTOs.ReportResponse> reports = reportService.getMyReports(getCurrentUserId());
        return ResponseEntity.ok(reports);
    }

    /**
     * 사용자 - 신고 취소
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "신고 취소", description = "본인이 작성한 신고를 취소합니다 (PENDING 상태만).")
    public ResponseEntity<Void> cancelReport(@PathVariable Long id) {
        log.info("신고 취소 요청 - ID: {}", id);
        reportService.cancelReport(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자 권한 체크 (임시: user ID가 1인 경우)
     */
    private boolean isAdmin(Long userId) {
        return Long.valueOf(1L).equals(userId);
    }
}

