package com.PBL.lab.core.repository;

import com.PBL.lab.core.entity.ExecutionInputOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ExecutionInputOutput Repository
 *
 * 코드 실행 입출력 정보 엔티티를 위한 데이터 접근 계층
 * - 기본 CRUD 작업 지원
 * - 입출력 정보 조회 및 관리 기능 제공
 * - Submission과 Grading 모두에서 사용
 */
@Repository
public interface ExecutionInputOutputRepository extends JpaRepository<ExecutionInputOutput, Long> {

    /**
     * ID로 입출력 정보 조회
     * - Spring Data JPA의 기본 findById와 동일한 기능
     * - Optional을 반환
     *
     * @param id 입출력 정보 ID
     * @return 입출력 정보 (Optional)
     */
    Optional<ExecutionInputOutput> findById(Long id);
}
