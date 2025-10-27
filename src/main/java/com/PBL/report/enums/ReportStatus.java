package com.PBL.report.enums;

/**
 * 신고 상태 Enum
 */
public enum ReportStatus {
    PENDING,       // 접수됨 (처리 대기)
    PROCESSING,    // 처리 중
    RESOLVED,      // 처리 완료
    REJECTED       // 반려됨
}

