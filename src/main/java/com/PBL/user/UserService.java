package com.PBL.user;

import com.PBL.recommendation.job.RecommendationWarmupJob;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자 비즈니스 로직 서비스
 */
@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired(required = false)
    private JobScheduler jobScheduler;
    
    @Autowired(required = false)
    private RecommendationWarmupJob recommendationWarmupJob;

    // === 회원가입 ===

    /**
     * 회원가입
     */
    public UserDTOs.UserResponse signUp(UserDTOs.SignUpRequest request) {
        // 입력값 검증
        validateSignUpRequest(request);

        // 중복 검사
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }

        // 사용자 생성
        User user = new User(request.getUsername(), request.getLoginId(), request.getPassword());
        user.setPassword(request.getPassword()); // 비밀번호 해시화

        // 저장
        User savedUser = userRepository.save(user);

        // 응답 반환 (비밀번호 제외)
        return new UserDTOs.UserResponse(savedUser);
    }

    /**
     * 회원가입 요청 검증
     */
    private void validateSignUpRequest(UserDTOs.SignUpRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명을 입력해주세요.");
        }
        if (request.getUsername().length() < 2 || request.getUsername().length() > 50) {
            throw new IllegalArgumentException("사용자명은 2자 이상 50자 이하여야 합니다.");
        }

        if (request.getLoginId() == null || request.getLoginId().trim().isEmpty()) {
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }
        if (request.getLoginId().length() < 4 || request.getLoginId().length() > 30) {
            throw new IllegalArgumentException("아이디는 4자 이상 30자 이하여야 합니다.");
        }
        if (!request.getLoginId().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("아이디는 영문자와 숫자만 사용할 수 있습니다.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (request.getPassword().length() < 6 || request.getPassword().length() > 50) {
            throw new IllegalArgumentException("비밀번호는 6자 이상 50자 이하여야 합니다.");
        }
    }

    // === 로그인 ===

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public UserDTOs.LoginResponse login(UserDTOs.LoginRequest request) {
        // 입력값 검증
        validateLoginRequest(request);

        // 사용자 조회
        Optional<User> userOpt = userRepository.findByLoginId(request.getLoginId());
        if (userOpt.isEmpty()) {
            return UserDTOs.LoginResponse.failure("존재하지 않는 아이디입니다.");
        }

        User user = userOpt.get();

        // 비밀번호 검증
        if (!user.checkPassword(request.getPassword())) {
            return UserDTOs.LoginResponse.failure("비밀번호가 올바르지 않습니다.");
        }

        // 로그인 성공
        UserDTOs.UserResponse userResponse = new UserDTOs.UserResponse(user);
        
        // 추천 결과 백그라운드 워밍업 스케줄링 (로그인 직후 즉시 처리)
        scheduleRecommendationWarmup(user.getId());
        
        return UserDTOs.LoginResponse.success(userResponse);
    }

    /**
     * 로그인 요청 검증
     */
    private void validateLoginRequest(UserDTOs.LoginRequest request) {
        if (request.getLoginId() == null || request.getLoginId().trim().isEmpty()) {
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
    }

    // === 사용자 조회 ===

    /**
     * ID로 사용자 조회 (DTO 반환)
     */
    @Transactional(readOnly = true)
    public UserDTOs.UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserDTOs.UserResponse(user);
    }

    /**
     * ID로 사용자 조회 (엔티티 반환)
     */
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 로그인 ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public UserDTOs.UserResponse getUserByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserDTOs.UserResponse(user);
    }

    // === 중복 검사 ===

    /**
     * 아이디 중복 검사
     */
    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    /**
     * 사용자명 중복 검사
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    // === 제재 관리 ===

    /**
     * 제재된 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserDTOs.MutedUserResponse> getMutedUsers() {
        List<User> mutedUsers = userRepository.findByIsMutedTrue();
        
        // 정지 기간 만료된 사용자는 자동으로 제외
        return mutedUsers.stream()
                .filter(user -> user.getMutedUntil() == null || LocalDateTime.now().isBefore(user.getMutedUntil()))
                .map(UserDTOs.MutedUserResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 추천 결과 워밍업 작업 스케줄링
     * 로그인 시 백그라운드에서 추천 결과를 미리 계산하여 캐시에 저장
     * 리소스 관리를 위해 즉시 실행하지 않고 약간의 지연을 두어 시스템 부하를 분산시킵니다.
     */
    private void scheduleRecommendationWarmup(Long userId) {
        if (jobScheduler != null && recommendationWarmupJob != null) {
            try {
                // 백그라운드에서 약간의 지연 후 실행 (시스템 부하 분산)
                // 2-5초 사이의 랜덤 지연으로 동시 로그인 시 부하 분산
                long delayMs = 2000 + (long)(Math.random() * 3000); // 2-5초
                jobScheduler.schedule(
                        java.time.Instant.now().plusMillis(delayMs),
                        () -> recommendationWarmupJob.warmupRecommendations(userId));
                
                log.debug("추천 워밍업 작업 스케줄링 완료 - 사용자 ID: {}, {}ms 후 실행 예정", userId, delayMs);
            } catch (Exception e) {
                // 워밍업 실패는 로그인을 막지 않음
                log.warn("추천 워밍업 작업 스케줄링 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            }
        } else {
            log.debug("JobScheduler 또는 RecommendationWarmupJob이 없어 워밍업을 스케줄링하지 않음 - 사용자 ID: {}", userId);
        }
    }

    /**
     * 사용자 제재 해제
     */
    @Transactional
    public void unmuteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        if (!Boolean.TRUE.equals(user.getIsMuted())) {
            throw new IllegalArgumentException("제재되지 않은 사용자입니다.");
        }

        user.unmute();
        userRepository.save(user);
    }

    // === 프로필 관리 ===

    /**
     * 닉네임 변경
     */
    @Transactional
    public UserDTOs.UserResponse updateUsername(Long userId, UserDTOs.UpdateUsernameRequest request) {
        log.info("닉네임 변경 요청 - 사용자 ID: {}", userId);

        // 입력값 검증
        validateUpdateUsernameRequest(request);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 닉네임과 동일한지 확인
        if (user.getUsername().equals(request.getUsername())) {
            throw new IllegalArgumentException("현재 사용 중인 닉네임입니다.");
        }

        // 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 닉네임 변경
        user.setUsername(request.getUsername());
        User updatedUser = userRepository.save(user);

        log.info("닉네임 변경 완료 - 사용자 ID: {}, 새 닉네임: {}", userId, request.getUsername());
        return new UserDTOs.UserResponse(updatedUser);
    }

    /**
     * 닉네임 변경 요청 검증
     */
    private void validateUpdateUsernameRequest(UserDTOs.UpdateUsernameRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }
        if (request.getUsername().length() < 2 || request.getUsername().length() > 50) {
            throw new IllegalArgumentException("닉네임은 2자 이상 50자 이하여야 합니다.");
        }
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(Long userId, UserDTOs.UpdatePasswordRequest request) {
        log.info("비밀번호 변경 요청 - 사용자 ID: {}", userId);

        // 입력값 검증
        validateUpdatePasswordRequest(request);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!user.checkPassword(request.getCurrentPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호와 현재 비밀번호가 동일한지 확인
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 비밀번호 변경
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        log.info("비밀번호 변경 완료 - 사용자 ID: {}", userId);
    }

    /**
     * 비밀번호 변경 요청 검증
     */
    private void validateUpdatePasswordRequest(UserDTOs.UpdatePasswordRequest request) {
        if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
        }
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호를 입력해주세요.");
        }
        if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 50) {
            throw new IllegalArgumentException("비밀번호는 6자 이상 50자 이하여야 합니다.");
        }
    }
}
