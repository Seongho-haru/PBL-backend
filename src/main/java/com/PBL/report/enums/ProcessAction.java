package com.PBL.report.enums;

/**
 * 신고 처리 방법 Enum
 */
public enum ProcessAction {
    DELETE_CONTENT,        // 콘텐츠 삭제
    MODIFY_REQUEST,        // 수정 요청
    WARNING,                // 경고
    MUTE_USER,             // 사용자 일시 정지
    DELETE_ACCOUNT,        // 계정 탈퇴
    NO_ACTION,             // 조치 없음 (혐의 없음)
    OTHER                  // 기타
}

