package com.PBL.report.entity;

import com.PBL.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 신고 통합 엔티티
 * 모든 콘텐츠 타입에 대한 신고를 처리
 */
@Entity
@Table(name = "reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고자
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    /**
     * 신고 대상 타입
     * CURRICULUM, LECTURE, QUESTION, ANSWER, COURSE_REVIEW
     */
    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    /**
     * 신고 대상 ID
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /**
     * 신고 사유
     * SPAM, ABUSE, INAPPROPRIATE_CONTENT, COPYRIGHT_VIOLATION, ETC
     */
    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    /**
     * 신고 상세 내용
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 신고 상태
     * PENDING: 접수 (기본값)
     * PROCESSING: 처리 중
     * RESOLVED: 처리 완료
     * REJECTED: 반려
     */
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    /**
     * 처리자 (관리자)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processor_id")
    private User processor;

    /**
     * 처리 방법
     * DELETE_CONTENT: 콘텐츠 삭제
     * MODIFY_REQUEST: 수정 요청
     * WARNING: 경고
     * MUTE_USER: 사용자 일시 정지
     * DELETE_ACCOUNT: 계정 탈퇴
     * NO_ACTION: 조치 없음 (혐의 없음)
     * OTHER: 기타
     */
    @Column(name = "process_action", length = 50)
    private String processAction;

    /**
     * 처리 내용
     */
    @Column(name = "process_note", columnDefinition = "TEXT")
    private String processNote;

    /**
     * 신고 접수 일시
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 처리 일시
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        
        // 기본 상태는 PENDING
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (processedAt == null && processor != null) {
            processedAt = LocalDateTime.now();
        }
    }
}

