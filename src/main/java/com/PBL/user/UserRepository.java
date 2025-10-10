package com.PBL.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 데이터 접근 레포지토리
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 로그인 아이디로 사용자 조회
     */
    Optional<User> findByLoginId(String loginId);

    /**
     * 로그인 아이디 존재 여부 확인
     */
    boolean existsByLoginId(String loginId);

    /**
     * 사용자명으로 사용자 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자명 존재 여부 확인
     */
    boolean existsByUsername(String username);
}
