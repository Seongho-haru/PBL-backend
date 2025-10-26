package com.PBL.recommendation.entity;

import com.PBL.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 선호도 엔티티
 * 사용자의 학습 선호도를 저장하여 추천 알고리즘에 활용
 */
@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * 선호 카테고리
     * 예: ["자료구조", "알고리즘", "네트워크"]
     */
    @Column(name = "preferred_categories", columnDefinition = "TEXT[]")
    @Builder.Default
    private List<String> preferredCategories = new ArrayList<>();

    /**
     * 선호 태그
     * 예: ["Python", "Django", "Web Development"]
     */
    @Column(name = "preferred_tags", columnDefinition = "TEXT[]")
    @Builder.Default
    private List<String> preferredTags = new ArrayList<>();

    /**
     * 선호 난이도
     * BEGINNER, INTERMEDIATE, ADVANCED
     */
    @Column(length = 20)
    private String preferredDifficulty;

    /**
     * 학습 스타일 (JSON)
     * 예: {"preferredTimeOfDay": "evening", "studyDuration": 60}
     */
    @Column(columnDefinition = "JSONB")
    private String learningStyle;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

