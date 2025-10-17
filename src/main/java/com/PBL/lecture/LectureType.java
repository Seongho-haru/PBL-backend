package com.PBL.lecture;

/**
 * 강의 유형을 정의하는 열거형 (Enum)
 *
 * === 개요 ===
 * - UI의 "강의 유형 선택" 모달에서 사용되는 타입 정의
 * - Lecture 엔티티의 type 필드에 저장됨
 *
 * === 타입 종류 ===
 * 1. MARKDOWN: 이론 학습용 마크다운 강의
 * 2. PROBLEM: 코딩 문제 + 자동 채점
 *
 * === 통합 작업 시 유의사항 ===
 * - DB에 문자열("MARKDOWN", "PROBLEM")로 저장 (@Enumerated(EnumType.STRING))
 * - 새로운 타입 추가 시 순서 상관없음 (ORDINAL 방식이 아니므로)
 * - 프론트엔드와 타입 이름 일치 필요
 */
public enum LectureType {

    /**
     * 마크다운 강의 (페이지 계획)
     *
     * === 특징 ===
     * - 마크다운 기반의 이론 강의
     * - 텍스트, 이미지, 코드 블록 등을 포함한 학습 자료
     * - 테스트케이스 및 자동 채점 없음
     *
     * === 사용 시나리오 ===
     * - 개념 설명, 튜토리얼
     * - 프로그래밍 이론 강의
     * - 프로젝트 가이드 문서
     */
    MARKDOWN("마크다운 강의", "페이지 계획"),

    /**
     * 문제 강의 (문제 생성)
     *
     * === 특징 ===
     * - 코딩 문제 + 테스트케이스
     * - 시간 제한, 메모리 제한 설정 가능
     * - Judge0 시스템과 연동하여 자동 채점
     *
     * === 사용 시나리오 ===
     * - 알고리즘 문제
     * - 프로그래밍 실습 문제
     * - 코딩 테스트 준비
     *
     * === 필수 구성요소 ===
     * - 테스트케이스 최소 1개 이상 필요
     */
    PROBLEM("문제 강의", "문제 생성");

    /**
     * 화면 표시용 한글 이름
     * 예: "마크다운 강의", "문제 강의"
     */
    private final String displayName;

    /**
     * 강의 유형 설명
     * 예: "페이지 계획", "문제 생성"
     */
    private final String description;

    /**
     * LectureType 생성자
     *
     * @param displayName 화면 표시용 이름
     * @param description 유형 설명
     */
    LectureType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 화면에 표시될 한글 이름 반환
     *
     * @return displayName (예: "마크다운 강의")
     *
     * 활용:
     * - UI 드롭다운 목록 표시
     * - 강의 목록에서 타입 표시
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 강의 유형 설명 반환
     *
     * @return description (예: "페이지 계획")
     *
     * 활용:
     * - 강의 생성 모달의 부가 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 문자열로부터 LectureType 찾기
     *
     * @param type 검색할 타입 문자열 (대소문자 무관)
     * @return 일치하는 LectureType
     * @throws IllegalArgumentException 알 수 없는 타입인 경우
     *
     * 지원 형식:
     * - Enum 이름: "MARKDOWN", "markdown", "Markdown"
     * - DisplayName: "마크다운 강의", "문제 강의"
     *
     * 사용 예시:
     * - LectureType.fromString("PROBLEM") → PROBLEM
     * - LectureType.fromString("문제 강의") → PROBLEM
     *
     * 활용:
     * - API 요청에서 문자열을 Enum으로 변환
     * - 프론트엔드에서 전달받은 타입 파싱
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
     *
     * @return PROBLEM 타입이면 true
     *
     * 활용:
     * - 테스트케이스 관리 기능 활성화 여부 판단
     * - 코드 제출 기능 표시 여부
     * - Lecture.isProblemType()에서 위임 호출
     */
    public boolean isProblemType() {
        return this == PROBLEM;
    }

    /**
     * 해당 타입이 마크다운 유형인지 확인
     *
     * @return MARKDOWN 타입이면 true
     *
     * 활용:
     * - 마크다운 렌더러 활성화 여부
     * - Lecture.isMarkdownType()에서 위임 호출
     */
    public boolean isMarkdownType() {
        return this == MARKDOWN;
    }
}
