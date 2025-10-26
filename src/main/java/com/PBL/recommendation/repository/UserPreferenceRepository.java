package com.PBL.recommendation.repository;

import com.PBL.recommendation.entity.UserPreference;
import com.PBL.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 선호도 Repository
 */
@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    /**
     * 사용자 ID로 선호도 조회
     */
    Optional<UserPreference> findByUserId(Long userId);

    /**
     * 사용자로 선호도 조회
     */
    Optional<UserPreference> findByUser(User user);

    /**
     * 사용자 선호도 존재 여부
     */
    boolean existsByUserId(Long userId);
}

