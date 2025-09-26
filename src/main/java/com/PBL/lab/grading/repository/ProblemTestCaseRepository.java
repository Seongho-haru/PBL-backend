package com.PBL.lab.grading.repository;

import com.PBL.lab.grading.entity.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemTestCaseRepository extends JpaRepository<ProblemTestCase, Long> {
    
    /**
     * 특정 문제 ID에 해당하는 모든 테스트 케이스를 조회합니다.
     * 
     * @param problemId 문제 ID
     * @return 해당 문제의 테스트 케이스 목록
     */
    List<ProblemTestCase> findByProblemId(Long problemId);
}
