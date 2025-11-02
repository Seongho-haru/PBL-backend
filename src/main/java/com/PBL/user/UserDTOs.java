package com.PBL.user;

import java.time.LocalDateTime;

/**
 * 사용자 관련 DTO 클래스들
 */
public class UserDTOs {

    /**
     * 회원가입 요청 DTO
     */
    public static class SignUpRequest {
        private String username;
        private String loginId;
        private String password;

        // 기본 생성자
        public SignUpRequest() {}

        // 생성자
        public SignUpRequest(String username, String loginId, String password) {
            this.username = username;
            this.loginId = loginId;
            this.password = password;
        }

        // Getter/Setter
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 로그인 요청 DTO
     */
    public static class LoginRequest {
        private String loginId;
        private String password;

        // 기본 생성자
        public LoginRequest() {}

        // 생성자
        public LoginRequest(String loginId, String password) {
            this.loginId = loginId;
            this.password = password;
        }

        // Getter/Setter
        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 사용자 정보 응답 DTO (비밀번호 제외)
     */
    public static class UserResponse {
        private Long id;
        private String username;
        private String loginId;
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // 기본 생성자
        public UserResponse() {}

        // User 엔티티로부터 생성
        public UserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.loginId = user.getLoginId();
            this.profileImageUrl = user.getProfileImageUrl();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
        }

        // Getter/Setter
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    /**
     * 로그인 성공 응답 DTO
     */
    public static class LoginResponse {
        private boolean success;
        private String message;
        private UserResponse user;

        // 기본 생성자
        public LoginResponse() {}

        // 생성자
        public LoginResponse(boolean success, String message, UserResponse user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        // 정적 팩토리 메서드
        public static LoginResponse success(UserResponse user) {
            return new LoginResponse(true, "로그인 성공", user);
        }

        public static LoginResponse failure(String message) {
            return new LoginResponse(false, message, null);
        }

        // Getter/Setter
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public UserResponse getUser() {
            return user;
        }

        public void setUser(UserResponse user) {
            this.user = user;
        }
    }

    /**
     * 닉네임 변경 요청 DTO
     */
    public static class UpdateUsernameRequest {
        private String username;

        public UpdateUsernameRequest() {}

        public UpdateUsernameRequest(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    /**
     * 비밀번호 변경 요청 DTO
     */
    public static class UpdatePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public UpdatePasswordRequest() {}

        public UpdatePasswordRequest(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    /**
     * 제재된 사용자 정보 응답 DTO
     */
    public static class MutedUserResponse {
        private Long id;
        private String username;
        private String loginId;
        private LocalDateTime mutedUntil;
        private Integer warningCount;
        private LocalDateTime createdAt;

        // 기본 생성자
        public MutedUserResponse() {}

        // User 엔티티로부터 생성
        public MutedUserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.loginId = user.getLoginId();
            this.mutedUntil = user.getMutedUntil();
            this.warningCount = user.getWarningCount();
            this.createdAt = user.getCreatedAt();
        }

        // Getter/Setter
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public LocalDateTime getMutedUntil() {
            return mutedUntil;
        }

        public void setMutedUntil(LocalDateTime mutedUntil) {
            this.mutedUntil = mutedUntil;
        }

        public Integer getWarningCount() {
            return warningCount;
        }

        public void setWarningCount(Integer warningCount) {
            this.warningCount = warningCount;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}
