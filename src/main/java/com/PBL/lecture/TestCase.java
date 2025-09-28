package com.PBL.lecture;

import jakarta.persistence.*;

/**
 * 테스트케이스 엔티티
 * 입력/출력 문자열의 1:1 매핑
 */
@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 입력 문자열
     */
    @Column(columnDefinition = "TEXT")
    private String input;

    /**
     * 예상 출력 문자열
     */
    @Column(columnDefinition = "TEXT")
    private String expectedOutput;

    /**
     * 소속 강의
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    /**
     * 테스트케이스 순서 (1부터 시작)
     */
    @Column(nullable = false)
    private Integer orderIndex = 1;

    // === 생성자 ===

    public TestCase() {}

    public TestCase(String input, String expectedOutput, Lecture lecture) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.lecture = lecture;
    }

    // === Getter & Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
