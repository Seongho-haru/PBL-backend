package com.PBL.lab.grading.repository;

import com.PBL.lab.grading.entity.GradingTestCaseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingTestCaseTokenRepository extends JpaRepository<GradingTestCaseToken, Long> {
    
    /**
     * 특정 Grading에 속한 모든 테스트케이스 토큰을 조회합니다.
     * 
     * @param token Grading ID
     * @return 해당 Grading의 테스트케이스 토큰 목록
     */

    List<GradingTestCaseToken> findByGradingToken(String token);
}
