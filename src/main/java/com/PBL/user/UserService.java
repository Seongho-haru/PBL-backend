package com.PBL.user;

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
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
}
