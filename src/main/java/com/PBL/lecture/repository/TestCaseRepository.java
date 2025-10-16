package com.PBL.lecture.repository;

import com.PBL.lecture.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TestCase Repository (Data Access Layer)
 *
 * === 개요 ===
 * - TestCase 엔티티의 데이터베이스 접근 계층
 * - 기본 CRUD는 JpaRepository가 자동 제공
 *
 * === 주요 기능 ===
 * - 특정 강의의 모든 테스트케이스 조회
 *
 * === 통합 작업 시 유의사항 ===
 * - 일반적으로 Lecture.testCases로 접근하므로 직접 사용 빈도는 낮음
 * - 테스트케이스만 독립적으로 관리할 때 사용
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    /**
     * 특정 강의의 모든 테스트케이스 조회
     *
     * @param id 강의 ID (Lecture.id)
     * @return 해당 강의에 속한 모든 테스트케이스
     *
     * 생성 쿼리:
     * SELECT * FROM test_cases WHERE lecture_id = :id
     *
     * 활용:
     * - 특정 강의의 테스트케이스만 조회
     * - 테스트케이스 관리 페이지
     *
     * 주의:
     * - 대부분의 경우 Lecture.getTestCases()로 접근 권장
     * - fetch join을 사용하면 N+1 문제 방지 가능
     */
    List<TestCase> findByLectureId(Long id);
}
