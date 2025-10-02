package com.PBL.lab.grading.repository;

import com.PBL.lab.grading.entity.Grading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

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

    @Query(value =  "SELECT g FROM Grading g " +
                    "LEFT JOIN FETCH g.language " +
                    "LEFT JOIN FETCH g.constraints " +
                    "LEFT JOIN FETCH g.testCaseTokens " +
                    "LEFT JOIN FETCH g.inputOutput " +
                    "WHERE g.token = :token")
    Grading findByToken(@Param("token") String token);
    
    /**
     * 특정 문제의 채점 목록을 페이지네이션하여 조회
     * - Fetch Join으로 language, constraints를 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value = "SELECT DISTINCT g FROM Grading g " +
                   "LEFT JOIN FETCH g.language " +
                   "LEFT JOIN FETCH g.constraints " +
                   "WHERE g.problemId = :problemId",
           countQuery = "SELECT COUNT(DISTINCT g) FROM Grading g WHERE g.problemId = :problemId")
    Page<Grading> findByProblemId(@Param("problemId") Long problemId, Pageable pageable);
    
    /**
     * 전체 채점 목록을 페이지네이션하여 조회
     * - Fetch Join으로 language, constraints를 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value = "SELECT DISTINCT g FROM Grading g " +
                   "LEFT JOIN FETCH g.language " +
                   "LEFT JOIN FETCH g.constraints",
           countQuery = "SELECT COUNT(DISTINCT g) FROM Grading g")
    Page<Grading> findAllWithFetch(Pageable pageable);
    
    /**
     * 토큰으로 업데이트 시간만 갱신 (LOB 필드 접근 없이)
     * - LOB 스트림 접근 문제를 피하기 위해 특정 필드만 업데이트
     */
    @Modifying
    @Query("UPDATE Grading g SET g.updatedAt = :updatedAt WHERE g.token = :token")
    void updateUpdatedAtByToken(@Param("token") String token, @Param("updatedAt") LocalDateTime updatedAt);
}
