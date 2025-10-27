package com.PBL.curriculum.entity;

import com.PBL.curriculum.Curriculum;
import com.PBL.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 커리큘럼 리뷰/문의 통합 엔티티
 * - 리뷰: 별점 있음, 항상 공개
 * - 문의: 별점 없음, 공개/비공개 선택 가능
 */
@Entity
@Table(name = "course_reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "is_review", nullable = false)
    private Boolean isReview; // true: 리뷰, false: 문의

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating; // 리뷰만 사용, null 가능

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic; // true: 공개, false: 비공개 (작성자와 관리자만)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        
        // 리뷰는 항상 공개, 문의는 기본값 공개
        if (isPublic == null) {
            isPublic = isReview ? true : true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

