package com.PBL.lab.core.repository;

import com.PBL.lab.core.entity.Constraints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SubmissionConstraints Repository
 * 
 * 제출 실행 제약조건 엔티티를 위한 데이터 접근 계층
 * - 기본 CRUD 작업 지원
 * - 제약조건 조회 및 관리 기능 제공
 */
@Repository
public interface ConstraintsRepository extends JpaRepository<Constraints, Long> {

    /**
     * 기본 제약조건 조회
     * - ID가 1인 기본 제약조건을 조회
     * - 시스템 기본값으로 사용되는 제약조건
     * 
     * @return 기본 제약조건 (Optional)
     */
    @Query("SELECT sc FROM Constraints sc WHERE sc.id = 1")
    Optional<Constraints> findDefaultConstraints();

    /**
     * 제약조건 존재 여부 확인
     * - 특정 ID의 제약조건이 존재하는지 확인
     * 
     * @param id 확인할 제약조건 ID
     * @return 존재 여부
     */
    boolean existsById(@org.springframework.lang.NonNull Long id);
}
