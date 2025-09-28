package com.PBL.lecture;

/**
 * 강의 유형을 정의하는 열거형
 *
 * UI의 "강의 유형 선택" 모달에서 사용되는 타입들
 */
public enum LectureType {

    /**
     * 마크다운 강의 (페이지 계획)
     * - 마크다운 기반의 이론 강의
     * - 텍스트, 이미지, 코드 블록 등을 포함한 학습 자료
     */
    MARKDOWN("마크다운 강의", "페이지 계획"),

    /**
     * 문제 강의 (문제 생성)
     * - 코딩 문제 + 테스트케이스
     * - 시간 제한, 메모리 제한 포함
     * - 자동 채점 기능
     */
    PROBLEM("문제 강의", "문제 생성");

    private final String displayName;
    private final String description;

    LectureType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 화면에 표시될 이름 반환
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 강의 유형 설명 반환
     */
    public String getDescription() {
        return description;
    }

    /**
     * 문자열로부터 LectureType 찾기
     */
    public static LectureType fromString(String type) {
        for (LectureType lectureType : LectureType.values()) {
            if (lectureType.name().equalsIgnoreCase(type) ||
                    lectureType.displayName.equals(type)) {
                return lectureType;
            }
        }
        throw new IllegalArgumentException("Unknown lecture type: " + type);
    }

    /**
     * 해당 타입이 문제 유형인지 확인
     */
    public boolean isProblemType() {
        return this == PROBLEM;
    }

    /**
     * 해당 타입이 마크다운 유형인지 확인
     */
    public boolean isMarkdownType() {
        return this == MARKDOWN;
    }
}
