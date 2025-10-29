package com.PBL.user.service;

import com.PBL.user.User;
import com.PBL.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 사용자 검증 Service
 * 정지 상태, 경고 횟수 등의 사용자 상태를 검증
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    /**
     * 사용자의 콘텐츠 생성 권한 확인
     * 정지 상태이면 예외 발생
     */
    public void validateUserCanCreateContent(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // 정지 기간 만료 자동 해제
        if (Boolean.TRUE.equals(user.getIsMuted()) && user.getMutedUntil() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(user.getMutedUntil())) {
                // 정지 기간 만료 - 자동 해제
                user.unmute();
                userRepository.save(user);
                log.info("정지 기간 만료로 자동 해제 - 사용자 ID: {}", userId);
                return;
            }
        }

        // 정지 상태 확인
        if (Boolean.TRUE.equals(user.getIsMuted())) {
            log.warn("정지된 사용자 콘텐츠 생성 시도 - 사용자 ID: {}, 정지 해제일: {}", 
                    userId, user.getMutedUntil());
            throw new RuntimeException("정지된 사용자는 콘텐츠를 생성할 수 없습니다.");
        }

        log.debug("사용자 콘텐츠 생성 권한 확인 - 사용자 ID: {}", userId);
    }

    /**
     * 사용자의 콘텐츠 수정 권한 확인
     */
    public void validateUserCanModifyContent(Long userId) {
        validateUserCanCreateContent(userId);
    }

    /**
     * 사용자의 콘텐츠 삭제 권한 확인
     */
    public void validateUserCanDeleteContent(Long userId) {
        validateUserCanCreateContent(userId);
    }
}

