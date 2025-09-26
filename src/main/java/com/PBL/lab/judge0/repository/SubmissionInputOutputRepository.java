package com.PBL.lab.judge0.repository;

import com.PBL.lab.judge0.entity.SubmissionInputOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SubmissionInputOutput Repository
 * 
 * 제출 입출력 정보 엔티티를 위한 데이터 접근 계층
 * - 기본 CRUD 작업 지원
 * - 입출력 정보 조회 및 관리 기능 제공
 */
@Repository
public interface SubmissionInputOutputRepository extends JpaRepository<SubmissionInputOutput, Long> {

    /**
     * Submission ID로 입출력 정보 조회
     * - Submission과의 관계를 통해 입출력 정보를 조회
     * 
     * @param submissionId Submission ID
     * @return 입출력 정보 (Optional)
     */
    @Query("SELECT sio FROM SubmissionInputOutput sio " +
           "JOIN Submission s ON s.inputOutput.id = sio.id " +
           "WHERE s.id = :submissionId")
    Optional<SubmissionInputOutput> findBySubmissionId(@Param("submissionId") Long submissionId);

    /**
     * Submission Token으로 입출력 정보 조회
     * - Token을 통해 입출력 정보를 조회
     * 
     * @param token Submission Token
     * @return 입출력 정보 (Optional)
     */
    @Query("SELECT sio FROM SubmissionInputOutput sio " +
           "JOIN Submission s ON s.inputOutput.id = sio.id " +
           "WHERE s.token = :token")
    Optional<SubmissionInputOutput> findBySubmissionToken(@Param("token") String token);
}
