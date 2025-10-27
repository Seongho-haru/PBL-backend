package com.PBL.report.service;

import com.PBL.report.dto.ReportDTOs;
import com.PBL.report.entity.Report;
import com.PBL.report.repository.ReportRepository;
import com.PBL.report.enums.ReportStatus;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 신고 Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    
    // 임시 관리자 ID (실제로는 role 기반으로 체크)
    private static final Long ADMIN_USER_ID = 1L;
    
    /**
     * 신고 작성
     */
    public ReportDTOs.ReportResponse createReport(Long userId, ReportDTOs.CreateReportRequest request) {
        log.info("신고 작성 요청 - 사용자 ID: {}, 대상: {}/{}", userId, request.getTargetType(), request.getTargetId());

        // 신고자 조회
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // 중복 신고 확인
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                userId, request.getTargetType(), request.getTargetId())) {
            throw new RuntimeException("이미 신고한 콘텐츠입니다.");
        }

        // 신고 생성
        Report report = Report.builder()
                .reporter(reporter)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .content(request.getContent())
                .status(ReportStatus.PENDING.name())
                .build();

        Report saved = reportRepository.save(report);
        log.info("신고 작성 완료 - ID: {}", saved.getId());

        return ReportDTOs.ReportResponse.from(saved);
    }

    /**
     * 관리자 - 신고 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReportDTOs.ReportResponse> getReports(String status, String targetType, Pageable pageable) {
        log.info("신고 목록 조회 - status: {}, targetType: {}", status, targetType);

        Page<Report> reports;
        
        if (status != null && targetType != null) {
            reports = reportRepository.findByStatusAndTargetTypeOrderByCreatedAtDesc(status, targetType, pageable);
        } else if (status != null) {
            reports = reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (targetType != null) {
            reports = reportRepository.findByTargetTypeOrderByCreatedAtDesc(targetType, pageable);
        } else {
            reports = reportRepository.findAll(pageable);
        }

        return reports.map(ReportDTOs.ReportResponse::from);
    }

    /**
     * 관리자 - 신고 상세 조회
     */
    @Transactional(readOnly = true)
    public ReportDTOs.ReportResponse getReport(Long reportId) {
        log.info("신고 상세 조회 - ID: {}", reportId);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다: " + reportId));

        return ReportDTOs.ReportResponse.from(report);
    }

    /**
     * 관리자 - 신고 처리
     */
    public ReportDTOs.ReportResponse processReport(Long reportId, Long processorId, ReportDTOs.ProcessReportRequest request) {
        log.info("신고 처리 요청 - ID: {}, 처리자: {}", reportId, processorId);

        // 관리자 권한 체크
        if (!isAdmin(processorId)) {
            throw new RuntimeException("관리자만 신고를 처리할 수 있습니다.");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다: " + reportId));

        User processor = userRepository.findById(processorId)
                .orElseThrow(() -> new RuntimeException("처리자를 찾을 수 없습니다: " + processorId));

        // 상태 업데이트
        report.setStatus(request.getStatus());
        report.setProcessor(processor);
        report.setProcessAction(request.getProcessAction());
        report.setProcessNote(request.getProcessNote());
        report.setProcessedAt(LocalDateTime.now());

        // 처리 방법에 따라 자동 실행
        executeProcessAction(request.getProcessAction(), report.getTargetType(), report.getTargetId());

        Report saved = reportRepository.save(report);
        log.info("신고 처리 완료 - ID: {}", saved.getId());

        return ReportDTOs.ReportResponse.from(saved);
    }

    /**
     * 처리 방법별 자동 실행
     */
    private void executeProcessAction(String processAction, String targetType, Long targetId) {
        if (processAction == null) {
            return;
        }

        switch (processAction) {
            case "MUTE_USER":
                // TODO: 대상 사용자 찾아서 정지 처리
                // 사용자 타입에 따라 targetType을 사용해 사용자 찾기
                log.info("사용자 정지 처리 - {}: {}", targetType, targetId);
                break;
                
            case "WARNING":
                // TODO: 대상 사용자에게 경고 추가
                log.info("경고 추가 - {}: {}", targetType, targetId);
                break;
                
            case "DELETE_CONTENT":
                // TODO: 대상 콘텐츠 삭제
                log.info("콘텐츠 삭제 - {}: {}", targetType, targetId);
                break;
                
            case "DELETE_ACCOUNT":
                // TODO: 사용자 계정 삭제
                log.info("계정 삭제 - {}: {}", targetType, targetId);
                break;
                
            default:
                log.debug("처리 방법: {} - {}: {}", processAction, targetType, targetId);
        }
    }

    /**
     * 관리자 - 신고 통계 조회
     */
    @Transactional(readOnly = true)
    public ReportDTOs.ReportStatsResponse getReportStats() {
        log.info("신고 통계 조회");

        List<Report> allReports = reportRepository.findAll();
        
        // 전체 통계
        long totalReports = allReports.size();
        long pendingCount = allReports.stream().filter(r -> r.getStatus().equals("PENDING")).count();
        long processingCount = allReports.stream().filter(r -> r.getStatus().equals("PROCESSING")).count();
        long resolvedCount = allReports.stream().filter(r -> r.getStatus().equals("RESOLVED")).count();
        long rejectedCount = allReports.stream().filter(r -> r.getStatus().equals("REJECTED")).count();

        // 타입별 통계
        Map<String, Long> byTargetType = allReports.stream()
                .collect(Collectors.groupingBy(Report::getTargetType, Collectors.counting()));

        // 사유별 통계
        Map<String, Long> byReason = allReports.stream()
                .collect(Collectors.groupingBy(Report::getReason, Collectors.counting()));

        // 처리 방법별 통계
        Map<String, Long> byProcessAction = allReports.stream()
                .filter(r -> r.getProcessAction() != null)
                .collect(Collectors.groupingBy(Report::getProcessAction, Collectors.counting()));

        return ReportDTOs.ReportStatsResponse.builder()
                .totalReports(totalReports)
                .pendingCount(pendingCount)
                .processingCount(processingCount)
                .resolvedCount(resolvedCount)
                .rejectedCount(rejectedCount)
                .byTargetType(byTargetType)
                .byReason(byReason)
                .byProcessAction(byProcessAction)
                .build();
    }

    /**
     * 사용자 - 내 신고 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReportDTOs.ReportResponse> getMyReports(Long userId) {
        log.info("내 신고 목록 조회 - 사용자 ID: {}", userId);

        List<Report> reports = reportRepository.findByReporterIdOrderByCreatedAtDesc(userId);

        return reports.stream()
                .map(ReportDTOs.ReportResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 - 신고 취소
     */
    public void cancelReport(Long reportId, Long userId) {
        log.info("신고 취소 요청 - 신고 ID: {}, 사용자 ID: {}", reportId, userId);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다: " + reportId));

        // 본인이 작성한 신고만 취소 가능
        if (!report.getReporter().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 신고만 취소할 수 있습니다.");
        }

        // PENDING 상태만 취소 가능
        if (!"PENDING".equals(report.getStatus())) {
            throw new RuntimeException("처리 중인 신고는 취소할 수 없습니다.");
        }

        reportRepository.delete(report);
        log.info("신고 취소 완료 - ID: {}", reportId);
    }

    /**
     * 관리자 권한 체크 (임시: user ID가 1인 경우)
     */
    private boolean isAdmin(Long userId) {
        return ADMIN_USER_ID.equals(userId);
    }
}

