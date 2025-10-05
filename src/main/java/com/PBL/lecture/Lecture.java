package com.PBL.lecture;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 강의 엔티티
 * UI의 "마크다운 강의"와 "문제 강의" 모두를 포함하는 통합 엔티티
 */
@Entity
@Table(name = "lectures")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 강의 제목 (필수)
     * UI: "문제 제목" 또는 마크다운 강의의 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 강의 설명/내용
     * - MARKDOWN 타입: 마크다운 내용
     * - PROBLEM 타입: 문제 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 강의 유형 (필수)
     * MARKDOWN: 마크다운 강의
     * PROBLEM: 문제 강의
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureType type;

    /**
     * 카테고리 (선택)
     * UI: "카테고리를 선택하세요" 드롭다운
     */
    @Column(length = 100)
    private String category;

    /**
     * 난이도 (선택)
     * UI: "난이도를 선택하세요" 드롭다운
     */
    @Column(length = 50)
    private String difficulty;

    /**
     * 시간 제한 (초 단위)
     * PROBLEM 타입에서만 사용
     * UI: "시간 제한 (초)"
     */
    private Integer timeLimit;

    /**
     * 메모리 제한 (MB 단위)
     * PROBLEM 타입에서만 사용
     * UI: "메모리 제한 (MB)"
     */
    private Integer memoryLimit;

    /**
     * 공개 강의 여부
     * true: 다른 사용자가 커리큘럼에 링크 가능
     * false: 비공개 강의 (본인만 사용 가능)
     */
    @Column(nullable = false)
    private Boolean isPublic = false;

    /**
     * 테스트케이스 목록
     * PROBLEM 타입에서만 사용
     * 입력/출력 문자열의 1:1 매핑 형식
     */
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 10) // N+1 쿼리 문제 해결을 위한 배치 로딩
    private List<TestCase> testCases = new ArrayList<>();

    /**
     * 생성 시간
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === 생성자 ===

    public Lecture() {}

    public Lecture(String title, String description, LectureType type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }

    // === 비즈니스 메서드 ===

    /**
     * 테스트케이스 추가
     */
    public void addTestCase(String input, String expectedOutput) {
        TestCase testCase = new TestCase(input, expectedOutput, this);
        this.testCases.add(testCase);
    }

    /**
     * 테스트케이스 제거
     */
    public void removeTestCase(TestCase testCase) {
        this.testCases.remove(testCase);
        testCase.setLecture(null);
    }

    /**
     * 모든 테스트케이스 제거
     */
    public void clearTestCases() {
        this.testCases.clear();
    }

    /**
     * 문제 타입인지 확인
     */
    public boolean isProblemType() {
        return this.type != null && this.type.isProblemType();
    }

    /**
     * 마크다운 타입인지 확인
     */
    public boolean isMarkdownType() {
        return this.type != null && this.type.isMarkdownType();
    }

    /**
     * 테스트케이스 개수 반환
     */
    public int getTestCaseCount() {
        return this.testCases != null ? this.testCases.size() : 0;
    }

    /**
     * 공개 강의인지 확인
     */
    public boolean isPublicLecture() {
        return Boolean.TRUE.equals(this.isPublic);
    }

    /**
     * 강의를 공개로 설정
     */
    public void makePublic() {
        this.isPublic = true;
    }

    /**
     * 강의를 비공개로 설정
     */
    public void makePrivate() {
        this.isPublic = false;
    }

    // === Getter & Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LectureType getType() {
        return type;
    }

    public void setType(LectureType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

