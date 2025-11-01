package com.PBL.lecture.entity;

import com.PBL.lecture.dto.TestCaseRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

/**
 * 테스트케이스 엔티티 (TestCase Entity)
 *
 * === 개요 ===
 * - 문제 강의(PROBLEM 타입)의 입력/출력 예제를 저장하는 엔티티
 * - Judge0 시스템과 연동하여 자동 채점에 사용됨
 * - 각 테스트케이스는 입력(stdin)과 예상 출력(expected output)의 1:1 매핑
 *
 * === 주요 기능 ===
 * - 코드 실행 시 표준 입력(stdin)으로 input 제공
 * - 코드 실행 결과를 expectedOutput과 비교하여 정답 여부 판정
 * - 여러 테스트케이스로 다양한 엣지 케이스 검증 가능
 *
 * === 연관 엔티티 ===
 * - Lecture (강의): @ManyToOne - 한 강의에 여러 테스트케이스 포함
 *
 * === 사용 예시 ===
 * 문제: "두 수를 입력받아 합을 출력하시오"
 * - TestCase 1: input="1 2", expectedOutput="3"
 * - TestCase 2: input="10 20", expectedOutput="30"
 * - TestCase 3: input="-5 5", expectedOutput="0"
 *
 * === 통합 작업 시 유의사항 ===
 * - Lecture 엔티티와 양방향 관계 (Lecture.testCases ↔ TestCase.lecture)
 * - orphanRemoval=true로 Lecture 삭제 시 자동 삭제됨
 * - Judge0 API 호출 시 stdin, expected_output 파라미터로 전달
 */
@Entity
@Table(name = "test_cases")
@Builder
@AllArgsConstructor
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 입력 문자열 (stdin)
     *
     * - 코드 실행 시 표준 입력(System.in, Scanner 등)으로 제공되는 데이터
     * - TEXT 타입으로 길이 제한 없음 (긴 입력도 가능)
     *
     * 입력 형식 예시:
     * - 단일 값: "5"
     * - 여러 값: "3 5 7"
     * - 다중 라인: "3\n1 2 3\n4 5 6\n7 8 9"
     *
     * 통합 작업 시:
     * - Judge0 API의 stdin 파라미터로 전달
     * - 개행 문자(\n) 처리에 주의
     */
    @Column(columnDefinition = "TEXT")
    private String input;

    /**
     * 예상 출력 문자열 (expected output)
     *
     * - 올바른 코드 실행 시 나와야 하는 정답 출력
     * - 실제 실행 결과(stdout)와 정확히 일치해야 정답 처리
     *
     * 출력 비교 방식:
     * - 문자열 완전 일치 (대소문자, 공백, 개행 모두 일치해야 함)
     * - Judge0에서 자동으로 비교 수행
     *
     * 주의사항:
     * - 불필요한 공백이나 개행 문자에 주의
     * - 예: "3\n"과 "3"은 다름 (마지막 개행 여부)
     *
     * 통합 작업 시:
     * - Judge0 API의 expected_output 파라미터로 전달
     * - 채점 결과에서 status.id == 3 (Accepted) 확인
     */
    @Column(columnDefinition = "TEXT",name = "expected_output")
    private String expectedOutput;

    /**
     * 소속 강의 (필수)
     *
     * - 이 테스트케이스가 속한 강의
     * - @ManyToOne: 여러 테스트케이스가 하나의 강의에 속함
     * - FetchType.LAZY: 성능 최적화를 위한 지연 로딩
     *
     * 연관관계:
     * - Lecture.testCases ↔ TestCase.lecture (양방향)
     * - Lecture 엔티티가 연관관계의 주인
     *
     * 통합 작업 시:
     * - Lecture 삭제 시 cascade로 함께 삭제됨
     * - 테스트케이스만 독립적으로 조회 시 lecture 정보 로딩 필요
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    /**
     * 테스트케이스 순서 (1부터 시작)
     *
     * - UI에서 테스트케이스 표시 순서
     * - 기본값: 1
     *
     * 활용:
     * - 샘플 테스트케이스 (orderIndex=1) vs 히든 테스트케이스 (orderIndex > 1)
     * - 난이도 순으로 정렬 (쉬운 케이스 먼저)
     *
     * 주의사항:
     * - 현재는 자동 관리되지 않음 (수동 설정 필요)
     * - 추후 자동 순서 관리 로직 추가 가능
     */
    @Column(nullable = false,name = "order_index")
    @Builder.Default
    private Integer orderIndex = 1;

    public static TestCase from(TestCaseRequest testCaseRequest, Lecture lecture) {
        return TestCase.builder()
                .input(testCaseRequest.getInput())
                .expectedOutput(testCaseRequest.getExpectedOutput())
                .lecture(lecture)
                .build();

    }

    // ============================================================
    // 생성자
    // ============================================================

    /**
     * 기본 생성자
     * - JPA 스펙에서 요구하는 기본 생성자
     * - 직접 사용보다는 JPA가 리플렉션으로 인스턴스 생성 시 사용
     */
    public TestCase() {}

    /**
     * 테스트케이스 생성자
     *
     * @param input 입력 데이터 (stdin)
     * @param expectedOutput 예상 출력 결과
     * @param lecture 소속 강의
     *
     * 사용 예시:
     * - new TestCase("5 3", "8", lecture)  // 덧셈 문제
     * - new TestCase("Hello", "Hello", lecture)  // 에코 문제
     *
     * 활용:
     * - Lecture.addTestCase() 메서드에서 사용
     * - 테스트케이스 일괄 생성 시 사용
     */
    public TestCase(String input, String expectedOutput, Lecture lecture) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.lecture = lecture;
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    /**
     * ID 조회
     * @return 테스트케이스 고유 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * ID 설정
     * @param id 테스트케이스 고유 ID
     * 주의: 일반적으로 JPA가 자동 생성하므로 직접 설정 불필요
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 입력 데이터 조회
     * @return stdin으로 제공될 입력 문자열
     */
    public String getInput() {
        return input;
    }

    /**
     * 입력 데이터 설정
     * @param input stdin으로 제공될 입력 문자열
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * 예상 출력 조회
     * @return 정답 출력 문자열
     */
    public String getExpectedOutput() {
        return expectedOutput;
    }

    /**
     * 예상 출력 설정
     * @param expectedOutput 정답 출력 문자열
     */
    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    /**
     * 소속 강의 조회
     * @return 이 테스트케이스가 속한 Lecture 엔티티
     * 주의: FetchType.LAZY로 실제 접근 시 쿼리 발생
     */
    public Lecture getLecture() {
        return lecture;
    }

    /**
     * 소속 강의 설정
     * @param lecture 이 테스트케이스가 속할 Lecture 엔티티
     * 활용: Lecture.addTestCase() 및 removeTestCase()에서 사용
     */
    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    /**
     * 순서 인덱스 조회
     * @return 테스트케이스 표시 순서 (1부터 시작)
     */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /**
     * 순서 인덱스 설정
     * @param orderIndex 테스트케이스 표시 순서 (1부터 시작)
     * 활용: 테스트케이스 순서 재정렬 시 사용
     */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
