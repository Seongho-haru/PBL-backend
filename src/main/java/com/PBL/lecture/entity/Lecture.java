package com.PBL.lecture.entity;

import com.PBL.lab.core.entity.Constraints;
import com.PBL.lecture.LectureType;
import com.PBL.lecture.dto.CreateLectureRequest;
import com.PBL.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 강의 엔티티 (Lecture Entity)
 *
 * === 개요 ===
 * - UI의 "마크다운 강의"와 "문제 강의" 모두를 포함하는 통합 엔티티
 * - 온라인 코딩 교육 플랫폼의 핵심 도메인 객체
 *
 * === 주요 기능 ===
 * 1. 마크다운 강의 (MARKDOWN 타입)
 *    - 이론 학습을 위한 마크다운 기반 콘텐츠
 *    - 텍스트, 이미지, 코드 블록 등 포함 가능
 *
 * 2. 문제 강의 (PROBLEM 타입)
 *    - 코딩 문제 + 테스트케이스 포함
 *    - Judge0 시스템과 연동하여 자동 채점
 *    - 시간 제한, 메모리 제한 설정 가능
 *
 * === 연관 엔티티 ===
 * - User (작성자): @ManyToOne - 한 명의 작성자가 여러 강의 작성
 * - TestCase (테스트케이스): @OneToMany - 한 강의에 여러 테스트케이스
 * - Constraints (제약조건): @OneToOne - 한 강의에 하나의 제약조건 설정
 *
 * === 통합 작업 시 유의사항 ===
 * - User 엔티티와의 양방향 관계 설정 시 순환참조 주의
 * - TestCase는 orphanRemoval=true로 강의 삭제 시 자동 삭제됨
 * - FetchType.LAZY 사용으로 N+1 문제 방지 (BatchSize 설정됨)
 * - Curriculum 엔티티와 연동 시 isPublic 필드 활용
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lectures")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 강의 제목 (필수)
     *
     * - UI: "문제 제목" 또는 마크다운 강의의 제목
     * - 최대 200자까지 입력 가능
     * - 검색 시 주요 키워드로 사용됨
     *
     * 통합 작업 시:
     * - Curriculum에서 강의 목록 표시 시 사용
     * - 검색 기능(findByTitleContainingIgnoreCase)의 대상 필드
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 강의 설명/내용
     *
     * - MARKDOWN 타입: 마크다운 형식의 강의 내용 전체
     * - PROBLEM 타입: 문제 설명, 입출력 형식, 제약사항 등
     * - TEXT 타입으로 길이 제한 없음
     *
     * 활용:
     * - 프론트엔드에서 마크다운 렌더링
     * - 문제 설명 페이지에 표시
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 강의 유형 (필수)
     *
     * - MARKDOWN: 마크다운 기반 이론 강의
     * - PROBLEM: 코딩 문제 + 자동 채점
     *
     * 저장 방식:
     * - @Enumerated(EnumType.STRING): DB에 "MARKDOWN", "PROBLEM" 문자열로 저장
     * - EnumType.ORDINAL 사용 시 순서 변경에 취약하므로 STRING 권장
     *
     * 통합 작업 시:
     * - 타입에 따라 UI 렌더링 방식 결정
     * - PROBLEM 타입만 테스트케이스 및 제출 기능 활성화
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureType type;

    /**
     * 카테고리 (선택)
     *
     * - UI: "카테고리를 선택하세요" 드롭다운
     * - 예시: "자료구조", "알고리즘", "웹개발", "데이터베이스" 등
     * - 최대 100자
     *
     * 활용:
     * - 강의 필터링 및 그룹화
     * - 통계 데이터 생성 (countByCategory)
     * - null 허용: 카테고리 미지정 강의 가능
     */
    @Column(length = 100)
    private String category;

    /**
     * 난이도 (선택)
     *
     * - UI: "난이도를 선택하세요" 드롭다운
     * - 예시: "입문", "초급", "중급", "고급" 등
     * - 최대 50자
     *
     * 활용:
     * - 학습자 수준별 강의 필터링
     * - Curriculum 구성 시 난이도 순 정렬
     * - null 허용: 난이도 미지정 강의 가능
     */
    @Column(length = 50)
    private String difficulty;

    /**
     * 강의 본문 내용
     *
     * - MARKDOWN 타입: 강의 전체 내용 (마크다운 형식)
     * - PROBLEM 타입: 문제 전체 설명
     * - TEXT 타입으로 길이 제한 없음
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 문제 입력 형식 설명 (PROBLEM 타입만 사용)
     *
     * - 입력 데이터 형식 및 범위 설명
     * - 예: "- 첫 줄: 배열의 크기 N (1 ≤ N ≤ 100)"
     * - MARKDOWN 타입에서는 NULL
     */
    @Column(name = "input_content", columnDefinition = "TEXT")
    private String inputContent;

    /**
     * 문제 출력 형식 설명 (PROBLEM 타입만 사용)
     *
     * - 출력 데이터 형식 설명
     * - 예: "- 배열의 최댓값을 출력"
     * - MARKDOWN 타입에서는 NULL
     */
    @Column(name = "output_content", columnDefinition = "TEXT")
    private String outputContent;

    /**
     * 공개 강의 여부 (필수)
     *
     * - true: 공개 강의 - 다른 사용자가 조회 및 커리큘럼에 추가 가능
     * - false: 비공개 강의 - 작성자만 조회 및 사용 가능
     * - 기본값: false (생성 시 비공개)
     *
     * 권한 체크:
     * - canViewLecture(): 공개 강의이거나 작성자인 경우만 조회 가능
     * - publishLecture()/unpublishLecture(): 공개/비공개 전환
     *
     * 통합 작업 시:
     * - Curriculum 생성 시 공개 강의 목록만 선택 가능하도록 필터링
     * - 강의 공유 기능 구현 시 활용
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lecture_tags", joinColumns = @JoinColumn(name = "lecture_id"))
    @Builder.Default
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();

    @Column(name = "duration_minutes")
    private Integer durationMinutes;  // 강의 소요시간 (분 단위)

    @Column
    private String thumbnailImageUrl;

    /**
     * 강의 작성자 (필수)
     *
     * - 강의를 생성한 사용자 정보
     * - @ManyToOne: 한 명의 사용자(User)가 여러 강의(Lecture) 작성 가능
     * - FetchType.LAZY: 성능 최적화를 위한 지연 로딩
     *   (Lecture 조회 시 User는 실제 접근할 때만 로딩)
     *
     * 연관관계 설정:
     * - User 엔티티에 List<Lecture> lectures 필드 추가 시 양방향 관계 구성 가능
     * - 양방향 설정 시 @ToString.Exclude, @EqualsAndHashCode.Exclude 필수 (순환참조 방지)
     *
     * 권한 체크:
     * - isAuthor(User), isAuthor(Long): 작성자 확인
     * - canEditLecture(), canDeleteLecture(): 수정/삭제 권한은 작성자만
     *
     * 통합 작업 시:
     * - User 엔티티의 ID, username 필드 필수
     * - 강의 목록 조회 시 작성자 정보 함께 표시
     * - Controller에서 X-User-Id 헤더로 작성자 인증
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * 테스트케이스 목록
     *
     * - PROBLEM 타입 강의에서만 사용
     * - 각 테스트케이스는 입력(input)과 예상 출력(expectedOutput)의 1:1 매핑
     *
     * 연관관계 설정:
     * - @OneToMany: 한 강의(Lecture)에 여러 테스트케이스(TestCase)
     * - mappedBy = "lecture": TestCase 엔티티의 lecture 필드가 주인
     * - cascade = CascadeType.ALL: 강의 저장/삭제 시 테스트케이스도 함께 처리
     * - orphanRemoval = true: 관계가 끊긴 테스트케이스 자동 삭제
     * - FetchType.LAZY: 필요할 때만 로딩 (성능 최적화)
     *
     * N+1 문제 해결:
     * - @BatchSize(size = 10): 테스트케이스 조회 시 최대 10개씩 배치로 로딩
     * - findByIdWithTestCases(): LEFT JOIN FETCH로 한 번에 조회
     *
     * 비즈니스 로직:
     * - addTestCase(): 새 테스트케이스 추가
     * - removeTestCase(): 특정 테스트케이스 제거
     * - clearTestCases(): 모든 테스트케이스 제거
     *
     * 통합 작업 시:
     * - Judge0 제출 시 이 테스트케이스들로 자동 채점
     * - LectureSubmissionService.submitCodeForAllTestCases() 참고
     */
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 10) // N+1 쿼리 문제 해결을 위한 배치 로딩
    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();

    /**
     * 생성 시간 (자동 설정)
     *
     * - @CreationTimestamp: 엔티티 최초 저장 시 현재 시간 자동 설정
     * - updatable = false: 생성 후 수정 불가
     *
     * 활용:
     * - 강의 목록 정렬 (최신순, 오래된 순)
     * - 통계 데이터: 기간별 강의 생성 추이
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시간 (자동 갱신)
     *
     * - @UpdateTimestamp: 엔티티 수정 시 현재 시간 자동 갱신
     *
     * 활용:
     * - 마지막 수정 시각 표시
     * - 최근 수정된 강의 조회
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Long languageId;

    /**
     * 실행 제약조건 (1:1 관계)
     * - 시간/메모리 제한, 컴파일러 옵션, 추가 파일 등
     * - 지연 로딩으로 성능 최적화
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "constraints_id")
    private Constraints constraints;

    // ============================================================
    // 생성자
    // ============================================================

    /**
     * 강의 생성자
     *
     * @param title 강의 제목 (필수)
     * @param description 강의 설명/내용
     * @param type 강의 유형 (MARKDOWN 또는 PROBLEM)
     *
     * 사용 예시:
     * - new Lecture("자바 기초", "자바 문법 설명", LectureType.MARKDOWN)
     * - new Lecture("두 수의 합", "A+B 문제", LectureType.PROBLEM)
     */
    public Lecture(String title, String description, LectureType type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public static Lecture from(CreateLectureRequest request, User author) {
        Lecture lecture = Lecture.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .category(request.getCategory())
                .difficulty(request.getDifficulty())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .author(author)
                .testCases(new ArrayList<>())
                .languageId(request.getLanguageId())

                .tags(request.getTags())
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .content(request.getContent())
                .inputContent(request.getInput_content())
                .outputContent(request.getInput_content())
                .durationMinutes(request.getDurationMinutes())
                .build();

        // TestCase 처리
        if (request.getTestCases() != null) {
            for (int i = 1; i <=request.getTestCases().size(); i++) {
                TestCase testCase = TestCase.builder()
                        .input(request.getTestCases().get(i-1).getInput())
                        .expectedOutput(request.getTestCases().get(i-1).getExpectedOutput())
                        .orderIndex(i)
                        .lecture(lecture)
                        .build();
                lecture.getTestCases().add(testCase);
            }
        }

        // Constraints 처리
        if (request.getConstraints() != null) {
            lecture.setConstraints(Constraints.build(request.getConstraints()));
        }

        return lecture;
    }

    // ============================================================
    // 비즈니스 메서드 - 테스트케이스 관리
    // ============================================================

    /**
     * 테스트케이스 추가
     *
     * - PROBLEM 타입 강의에 새로운 테스트케이스 추가
     * - 양방향 연관관계 자동 설정 (TestCase -> Lecture)
     *
     * @param input 입력 데이터
     * @param expectedOutput 예상 출력 결과
     *
     * 사용 시나리오:
     * - 문제 생성 시 입출력 예제 추가
     * - 히든 테스트케이스 추가
     *
     * 주의사항:
     * - MARKDOWN 타입에서도 호출은 가능하나 의미 없음
     * - Service 계층에서 타입 검증 후 호출 권장
     */
    public void addTestCase(String input, String expectedOutput) {
        TestCase testCase = new TestCase(input, expectedOutput, this);
        this.testCases.add(testCase);
    }

    /**
     * 특정 테스트케이스 제거
     *
     * - 지정된 테스트케이스를 목록에서 제거
     * - 양방향 연관관계 해제 (testCase.lecture = null)
     *
     * @param testCase 제거할 테스트케이스
     *
     * 사용 시나리오:
     * - 잘못된 테스트케이스 삭제
     * - 문제 난이도 조정 시 일부 케이스 제거
     */
    public void removeTestCase(TestCase testCase) {
        this.testCases.remove(testCase);
        testCase.setLecture(null);
    }

    /**
     * 모든 테스트케이스 제거
     *
     * - orphanRemoval=true 설정으로 DB에서도 함께 삭제됨
     *
     * 사용 시나리오:
     * - 문제 재작성 시 테스트케이스 전체 초기화
     * - 강의 타입 변경 시 (PROBLEM -> MARKDOWN)
     */
    public void clearTestCases() {
        this.testCases.clear();
    }

    // ============================================================
    // 비즈니스 메서드 - 타입 확인
    // ============================================================

    /**
     * 문제 타입 강의인지 확인
     *
     * @return PROBLEM 타입이면 true, 아니면 false
     *
     * 활용:
     * - 테스트케이스 UI 표시 여부 결정
     * - 코드 제출 기능 활성화 여부
     * - LectureType.PROBLEM과 동일한지 체크
     */
    public boolean isProblemType() {
        return this.type != null && this.type.isProblemType();
    }

    /**
     * 마크다운 타입 강의인지 확인
     *
     * @return MARKDOWN 타입이면 true, 아니면 false
     *
     * 활용:
     * - 마크다운 렌더링 UI 표시
     * - LectureType.MARKDOWN과 동일한지 체크
     */
    public boolean isMarkdownType() {
        return this.type != null && this.type.isMarkdownType();
    }

    /**
     * 테스트케이스 개수 반환
     *
     * @return 테스트케이스 개수 (없으면 0)
     *
     * 활용:
     * - UI에 "테스트케이스 N개" 표시
     * - 문제 제출 전 테스트케이스 존재 여부 확인
     * - null-safe: testCases가 null이어도 0 반환
     */
    public int getTestCaseCount() {
        return this.testCases != null ? this.testCases.size() : 0;
    }

    // ============================================================
    // 비즈니스 메서드 - 공개/비공개 관리
    // ============================================================

    /**
     * 공개 강의인지 확인
     *
     * @return 공개 강의면 true, 비공개면 false
     *
     * 활용:
     * - 강의 목록 필터링
     * - 접근 권한 체크 (canViewLecture)
     * - Boolean.TRUE.equals() 사용으로 null-safe
     */
    public boolean isPublicLecture() {
        return Boolean.TRUE.equals(this.isPublic);
    }

    /**
     * 강의를 공개로 설정
     *
     * - LectureService.publishLecture()에서 호출
     *
     * 사용 시나리오:
     * - 작성 완료 후 다른 사용자에게 공개
     * - 커리큘럼 공유를 위해 강의 공개
     */
    public void makePublic() {
        this.isPublic = true;
    }

    /**
     * 강의를 비공개로 설정
     *
     * - LectureService.unpublishLecture()에서 호출
     *
     * 사용 시나리오:
     * - 수정 중인 강의 임시 비공개
     * - 더 이상 사용하지 않는 강의 비공개 전환
     */
    public void makePrivate() {
        this.isPublic = false;
    }

    // ============================================================
    // 비즈니스 메서드 - 권한 확인
    // ============================================================

    /**
     * 작성자인지 확인 (User 객체로)
     *
     * @param user 확인할 사용자 객체
     * @return 작성자면 true, 아니면 false
     *
     * 활용:
     * - 강의 수정/삭제 권한 체크
     * - User 엔티티의 equals() 메서드 사용
     *
     * 주의사항:
     * - User 엔티티에 올바른 equals() 구현 필요
     * - 일반적으로 ID 기반 비교 권장
     */
    public boolean isAuthor(User user) {
        return this.author != null && this.author.equals(user);
    }

    /**
     * 작성자인지 확인 (ID로)
     *
     * @param userId 확인할 사용자 ID
     * @return 작성자면 true, 아니면 false
     *
     * 활용:
     * - Controller에서 X-User-Id 헤더와 비교
     * - LectureService.canEditLecture() 등에서 사용
     *
     * 장점:
     * - User 객체 로딩 없이 ID만으로 확인 가능
     * - 지연 로딩으로 인한 쿼리 발생 방지
     */
    public boolean isAuthor(Long userId) {
        return this.author != null && this.author.getId().equals(userId);
    }
}

