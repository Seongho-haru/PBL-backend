package com.PBL.curriculum;

/**
 * 커리큘럼 상태를 정의하는 열거형
 */
public enum CurriculumStatus {

    /**
     * 초안 상태
     * - 작성 중이거나 검토 중인 상태
     * - 공개되지 않음
     */
    DRAFT("초안", "작성 중"),

    /**
     * 발행된 상태
     * - 완성되어 공개된 상태
     * - 학습자들이 접근 가능
     */
    PUBLISHED("발행됨", "공개 중"),

    /**
     * 보관된 상태
     * - 더 이상 사용하지 않는 상태
     * - 기존 학습자는 접근 가능하지만 신규 등록 불가
     */
    ARCHIVED("보관됨", "사용 중단");

    private final String displayName;
    private final String description;

    CurriculumStatus(String displayName, String description) {
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
     * 상태 설명 반환
     */
    public String getDescription() {
        return description;
    }

    /**
     * 문자열로부터 CurriculumStatus 찾기
     */
    public static CurriculumStatus fromString(String status) {
        for (CurriculumStatus curriculumStatus : CurriculumStatus.values()) {
            if (curriculumStatus.name().equalsIgnoreCase(status) ||
                    curriculumStatus.displayName.equals(status)) {
                return curriculumStatus;
            }
        }
        throw new IllegalArgumentException("Unknown curriculum status: " + status);
    }

    /**
     * 공개 가능한 상태인지 확인
     */
    public boolean isPublishable() {
        return this == DRAFT;
    }

    /**
     * 활성 상태인지 확인 (학습자가 접근 가능한 상태)
     */
    public boolean isActive() {
        return this == PUBLISHED;
    }
}
