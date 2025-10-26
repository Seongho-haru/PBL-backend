package com.PBL.recommendation.entity;

import com.PBL.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 추천 로그 엔티티
 * 추천 내역을 기록하여 클릭율 분석 및 추천 알고리즘 개선에 활용
 */
@Entity
@Table(name = "recommendation_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 추천된 커리큘럼 ID (선택)
     */
    @Column(name = "curriculum_id")
    private Long curriculumId;

    /**
     * 추천된 강의 ID (선택)
     */
    @Column(name = "lecture_id")
    private Long lectureId;

    /**
     * 추천 타입
     * PERSONALIZED, SIMILAR_PROBLEM, TRENDING, POPULAR 등
     */
    @Column(length = 50, nullable = false)
    private String recommendationType;

    /**
     * 추천 점수
     * 0.00 ~ 100.00
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal recommendationScore;

    /**
     * 클릭 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isClicked = false;

    /**
     * 클릭 시간
     */
    @Column
    private LocalDateTime clickedAt;

    /**
     * 표시된 순서
     */
    @Column
    private Integer displayOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 클릭 처리
     */
    public void markAsClicked() {
        this.isClicked = true;
        this.clickedAt = LocalDateTime.now();
    }
}

