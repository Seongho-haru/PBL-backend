package com.PBL.lab.core.repository;

import com.PBL.lab.core.entity.Constraints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Constraints Repository (Data Access Layer)
 *
 * === 개요 ===
 * - Constraints 엔티티의 데이터베이스 접근 계층
 * - 코드 실행 시 적용되는 제약조건(CPU, 메모리 등) 관리
 *
 * === 주요 기능 ===
 * - 기본 제약조건 조회
 * - 제약조건 존재 여부 확인
 *
 * === 통합 작업 시 유의사항 ===
 * - Lecture 엔티티와 @OneToOne 관계
 * - 제약조건 미설정 시 Judge0 시스템 기본값 사용
 * - 기본 제약조건(ID=1)은 시스템 기본 설정으로 활용
 */
@Repository
public interface ConstraintsRepository extends JpaRepository<Constraints, Long> {

    /**
     * 기본 제약조건 조회
     *
     * @return ID가 1인 기본 제약조건 (시스템 기본값)
     *
     * JPQL 쿼리:
     * SELECT * FROM submission_constraints WHERE id = 1
     *
     * 활용:
     * - 강의 생성 시 기본 제약조건 적용
     * - 제약조건 미설정 강의의 fallback 값
     *
     * 주의사항:
     * - 시스템 초기화 시 ID=1 제약조건 생성 필요
     * - 기본 제약조건 삭제하지 않도록 주의
     */
    @Query("SELECT sc FROM Constraints sc WHERE sc.id = 1")
    Optional<Constraints> findDefaultConstraints();

    /**
     * 제약조건 존재 여부 확인
     *
     * @param id 확인할 제약조건 ID
     * @return 존재하면 true, 없으면 false
     *
     * 활용:
     * - 제약조건 생성 전 중복 확인
     * - 유효성 검증
     */
    boolean existsById(@org.springframework.lang.NonNull Long id);
}
