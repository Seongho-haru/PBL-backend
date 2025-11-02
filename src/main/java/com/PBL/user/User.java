package com.PBL.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 * 간단한 회원가입을 위한 기본 필드만 포함
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자명 (표시용)
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * 로그인 아이디 (중복 불가)
     */
    @Column(nullable = false, unique = true, length = 30)
    private String loginId;

    /**
     * 비밀번호 (해시화되어 저장)
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 계정 생성 시간
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 계정 수정 시간
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 일시 정지 여부
     */
    @Column(name = "is_muted")
    private Boolean isMuted = false;

    /**
     * 정지 해제 일시
     */
    @Column(name = "muted_until")
    private LocalDateTime mutedUntil;

    /**
     * 경고 횟수
     */
    @Column(name = "warning_count")
    private Integer warningCount = 0;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    // 기본 생성자
    public User() {}

    // 생성자
    public User(String username, String loginId, String password) {
        this.username = username;
        this.loginId = loginId;
        this.password = password;
    }

    // === 비즈니스 메서드 ===

    /**
     * 비밀번호 검증
     */
    public boolean checkPassword(String rawPassword) {
        // TODO: 실제 비밀번호 해시 검증 로직 구현
        return this.password.equals(rawPassword);
    }

    /**
     * 비밀번호 설정 (해시화)
     */
    public void setPassword(String rawPassword) {
        // TODO: 실제 비밀번호 해시 로직 구현
        this.password = rawPassword;
    }

    /**
     * 일시 정지 적용
     */
    public void mute(int days) {
        this.isMuted = true;
        this.mutedUntil = LocalDateTime.now().plusDays(days);
    }

    /**
     * 일시 정지 해제
     */
    public void unmute() {
        this.isMuted = false;
        this.mutedUntil = null;
    }

    /**
     * 현재 정지 상태인지 확인
     */
    public boolean isCurrentlyMuted() {
        if (!Boolean.TRUE.equals(isMuted)) {
            return false;
        }
        
        if (mutedUntil == null) {
            return true; // 영구 정지
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(mutedUntil)) {
            // 정지 기간 만료
            unmute();
            return false;
        }
        
        return true;
    }

    /**
     * 경고 횟수 증가
     */
    public void addWarning() {
        this.warningCount++;
        
        // 경고 3회 이상 시 자동 정지 (1일)
        if (this.warningCount >= 3) {
            mute(1);
            this.warningCount = 0; // 경고 횟수 초기화
        }
    }

    // === Getter/Setter ===

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

    public String getPassword() {
        return password;
    }

    public void setPasswordHash(String password) {
        this.password = password;
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

    public Boolean getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(Boolean isMuted) {
        this.isMuted = isMuted;
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", loginId='" + loginId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
