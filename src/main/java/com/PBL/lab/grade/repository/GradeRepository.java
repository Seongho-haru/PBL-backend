package com.PBL.lab.grade.repository;

import com.PBL.lab.grade.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    /**
     * 큐에 대기 중인 채점 개수 조회
     * - statusId = 1 (QUEUE)인 채점들의 개수
     */
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.statusId = 1")
    Long countGradesInQueue();

    /**
     * 처리 중인 채점 개수 조회
     * - statusId = 2 (PROCESS)인 채점들의 개수
     */
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.statusId = 2")
    Long countGradesInProcess();

    /**
     * 특정 상태의 채점 개수 조회
     * - statusId로 필터링하여 개수 반환
     */
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.statusId = :statusId")
    Long countByStatusId(Integer statusId);

    @Query(value =  "SELECT g FROM Grade g " +
                    "LEFT JOIN FETCH g.language " +
                    "LEFT JOIN FETCH g.constraints " +
//                    "LEFT JOIN FETCH g.testCaseTokens " +
                    "LEFT JOIN FETCH g.inputOutput " +
                    "WHERE g.token = :token "+
                    "ORDER BY g.createdAt DESC ")
    Grade findByToken(@Param("token") String token);

    /**
     * 특정 문제의 채점 목록을 페이지네이션하여 조회
     * - Fetch Join으로 language, constraints를 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value =  "SELECT DISTINCT g FROM Grade g " +
                    "LEFT JOIN FETCH g.language " +
                    "LEFT JOIN FETCH g.constraints " +
//                    "LEFT JOIN FETCH g.testCaseTokens " +
                    "LEFT JOIN FETCH g.inputOutput " +
                    "WHERE g.problemId = :problemId "+
                    "ORDER BY g.createdAt DESC ",
           countQuery = "SELECT COUNT(DISTINCT g) FROM Grade g WHERE g.problemId = :problemId")
    Page<Grade> findByProblemId(@Param("problemId") Long problemId, Pageable pageable);

    /**
     * 전체 채점 목록을 페이지네이션하여 조회
     * - Fetch Join으로 language, constraints를 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value =  "SELECT DISTINCT g FROM Grade g " +
                    "LEFT JOIN FETCH g.language " +
                    "LEFT JOIN FETCH g.constraints "+
//                    "LEFT JOIN FETCH g.testCaseTokens " +
                    "LEFT JOIN FETCH g.inputOutput " +
                    "ORDER BY g.createdAt DESC " ,
       countQuery = "SELECT COUNT(DISTINCT g) FROM Grade g")
    Page<Grade> findAllWithFetch(Pageable pageable);

    /**
     * 토큰으로 업데이트 시간만 갱신 (LOB 필드 접근 없이)
     * - LOB 스트림 접근 문제를 피하기 위해 특정 필드만 업데이트
     */
    @Modifying
    @Query("UPDATE Grade g SET g.updatedAt = :updatedAt WHERE g.token = :token")
    void updateUpdatedAtByToken(@Param("token") String token, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 토큰 존재 여부 확인
     */
    boolean existsByToken(String token);

    /**
     * 특정 사용자의 채점 목록을 페이지네이션하여 조회
     * - Fetch Join으로 language, constraints, inputOutput을 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value = "SELECT DISTINCT g FROM Grade g " +
                   "LEFT JOIN FETCH g.language " +
                   "LEFT JOIN FETCH g.constraints " +
                   "LEFT JOIN FETCH g.inputOutput " +
                   "WHERE g.user.id = :userId " +
                   "ORDER BY g.createdAt DESC",
           countQuery = "SELECT COUNT(DISTINCT g) FROM Grade g WHERE g.user.id = :userId")
    Page<Grade> findByUser_Id(@Param("userId") Long userId, Pageable pageable);

    /**
     * 익명 채점 목록을 페이지네이션하여 조회 (user가 null인 채점만)
     * - Fetch Join으로 language, constraints, inputOutput을 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value = "SELECT DISTINCT g FROM Grade g " +
                   "LEFT JOIN FETCH g.language " +
                   "LEFT JOIN FETCH g.constraints " +
                   "LEFT JOIN FETCH g.inputOutput " +
                   "WHERE g.user IS NULL " +
                   "ORDER BY g.createdAt DESC",
           countQuery = "SELECT COUNT(DISTINCT g) FROM Grade g WHERE g.user IS NULL")
    Page<Grade> findByUserIsNull(Pageable pageable);

    /**
     * 특정 문제의 익명 채점 목록을 페이지네이션하여 조회 (user가 null인 채점만)
     * - Fetch Join으로 language, constraints, inputOutput을 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value = "SELECT DISTINCT g FROM Grade g " +
                   "LEFT JOIN FETCH g.language " +
                   "LEFT JOIN FETCH g.constraints " +
                   "LEFT JOIN FETCH g.inputOutput " +
                   "WHERE g.user IS NULL AND g.problemId = :problemId " +
                   "ORDER BY g.createdAt DESC",
           countQuery = "SELECT COUNT(DISTINCT g) FROM Grade g WHERE g.user IS NULL AND g.problemId = :problemId")
    Page<Grade> findByUserIsNullAndProblemId(@Param("problemId") Long problemId, Pageable pageable);

    /**
     * 특정 사용자의 특정 문제 채점 목록을 페이지네이션하여 조회
     * - Fetch Join으로 language, constraints, inputOutput을 미리 로딩하여 Lazy Loading 문제 방지
     */
    @Query(value = "SELECT DISTINCT g FROM Grade g " +
                   "LEFT JOIN FETCH g.language " +
                   "LEFT JOIN FETCH g.constraints " +
                   "LEFT JOIN FETCH g.inputOutput " +
                   "WHERE g.user.id = :userId AND g.problemId = :problemId " +
                   "ORDER BY g.createdAt DESC",
           countQuery = "SELECT COUNT(DISTINCT g) FROM Grade g WHERE g.user.id = :userId AND g.problemId = :problemId")
    Page<Grade> findByUser_IdAndProblemId(@Param("userId") Long userId, @Param("problemId") Long problemId, Pageable pageable);
}
