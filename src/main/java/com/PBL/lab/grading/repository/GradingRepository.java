package com.PBL.lab.grading.repository;

import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.judge0.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradingRepository extends JpaRepository<Grading, Long> {

    /**
     * 큐에 대기 중인 채점 개수 조회
     * - statusId = 1 (QUEUE)인 채점들의 개수
     */
    @Query("SELECT COUNT(g) FROM Grading g WHERE g.statusId = 1")
    Long countGradingsInQueue();

    /**
     * 처리 중인 채점 개수 조회
     * - statusId = 2 (PROCESS)인 채점들의 개수
     */
    @Query("SELECT COUNT(g) FROM Grading g WHERE g.statusId = 2")
    Long countGradingsInProcess();

    /**
     * 특정 상태의 채점 개수 조회
     * - statusId로 필터링하여 개수 반환
     */
    @Query("SELECT COUNT(g) FROM Grading g WHERE g.statusId = :statusId")
    Long countByStatusId(Integer statusId);

    @Query("SELECT g FROM Grading g LEFT JOIN FETCH g.language LEFT JOIN FETCH g.constraints LEFT JOIN FETCH g.testCaseTokens WHERE g.token = :token")
    Grading findByToken(@Param("token") String token);
}
