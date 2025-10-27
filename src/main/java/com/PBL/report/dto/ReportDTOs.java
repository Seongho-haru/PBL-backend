package com.PBL.report.dto;

import com.PBL.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 신고 DTOs
 */
public class ReportDTOs {

    /**
     * 신고 작성 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReportRequest {
        private String targetType;  // CURRICULUM, LECTURE, QUESTION, ANSWER, COURSE_REVIEW
        private Long targetId;
        private String reason;      // SPAM, ABUSE, INAPPROPRIATE_CONTENT, COPYRIGHT_VIOLATION, ETC
        private String content;     // 신고 상세 내용
    }

    /**
     * 신고 처리 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessReportRequest {
        private String status;           // RESOLVED, REJECTED
        private String processAction;   // DELETE_CONTENT, MODIFY_REQUEST, WARNING, MUTE_USER, DELETE_ACCOUNT, NO_ACTION, OTHER
        private String processNote;     // 처리 내용
    }

    /**
     * 신고 응답
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportResponse {
        private Long id;
        private Long reporterId;
        private String reporterUsername;
        private String targetType;
        private Long targetId;
        private String reason;
        private String content;
        private String status;
        private Long processorId;
        private String processorUsername;
        private String processAction;
        private String processNote;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public static ReportResponse from(Report report) {
            ReportResponse response = new ReportResponse();
            response.setId(report.getId());
            response.setReporterId(report.getReporter().getId());
            response.setReporterUsername(report.getReporter().getUsername());
            response.setTargetType(report.getTargetType());
            response.setTargetId(report.getTargetId());
            response.setReason(report.getReason());
            response.setContent(report.getContent());
            response.setStatus(report.getStatus());
            
            if (report.getProcessor() != null) {
                response.setProcessorId(report.getProcessor().getId());
                response.setProcessorUsername(report.getProcessor().getUsername());
            }
            
            response.setProcessAction(report.getProcessAction());
            response.setProcessNote(report.getProcessNote());
            response.setCreatedAt(report.getCreatedAt());
            response.setProcessedAt(report.getProcessedAt());
            
            return response;
        }
    }

    /**
     * 신고 통계 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportStatsResponse {
        private Long totalReports;
        private Long pendingCount;
        private Long processingCount;
        private Long resolvedCount;
        private Long rejectedCount;
        private Map<String, Long> byTargetType;
        private Map<String, Long> byReason;
        private Map<String, Long> byProcessAction;
    }
}

