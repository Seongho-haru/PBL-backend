package com.PBL.curriculum;

import com.PBL.lecture.Lecture;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 커리큘럼 섹션 엔티티
 * 커리큘럼 내의 단계별 구분 (예: "1주차", "기초 문법")
 */
@Entity
@Table(name = "curriculum_sections")
public class CurriculumSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 섹션 제목 (필수)
     * 예: "1주차: Java 기초", "알고리즘 기본 개념"
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 섹션 설명
     * 해당 섹션의 학습 목표나 내용 요약
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 소속 커리큘럼
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    /**
     * 섹션 순서 (0부터 시작)
     */
    @Column(nullable = false)
    private Integer orderIndex;

    /**
     * 섹션 내 강의 목록
     */
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @BatchSize(size = 10)
    private List<CurriculumLecture> lectures = new ArrayList<>();

    /**
     * 생성 시간
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === 생성자 ===

    public CurriculumSection() {}

    public CurriculumSection(String title, String description, Curriculum curriculum, Integer orderIndex) {
        this.title = title;
        this.description = description;
        this.curriculum = curriculum;
        this.orderIndex = orderIndex;
    }

    // === 비즈니스 메서드 ===

    /**
     * 강의 추가
     */
    public void addLecture(Lecture lecture) {
        int nextOrder = lectures.size();
        CurriculumLecture curriculumLecture = new CurriculumLecture(this, lecture, nextOrder);
        this.lectures.add(curriculumLecture);
    }

    /**
     * 강의 제거
     */
    public void removeLecture(CurriculumLecture curriculumLecture) {
        this.lectures.remove(curriculumLecture);
        curriculumLecture.setSection(null);
        // 순서 재정렬
        reorderLectures();
    }

    /**
     * 강의 순서 재정렬
     */
    private void reorderLectures() {
        for (int i = 0; i < lectures.size(); i++) {
            lectures.get(i).setOrderIndex(i);
        }
    }

    /**
     * 강의 순서 변경
     */
    public void moveLecture(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= lectures.size() || 
            toIndex < 0 || toIndex >= lectures.size()) {
            throw new IllegalArgumentException("잘못된 인덱스입니다.");
        }

        CurriculumLecture lecture = lectures.remove(fromIndex);
        lectures.add(toIndex, lecture);
        reorderLectures();
    }

    /**
     * 강의 수 반환
     */
    public int getLectureCount() {
        return lectures != null ? lectures.size() : 0;
    }

    /**
     * 특정 강의가 포함되어 있는지 확인
     */
    public boolean containsLecture(Long lectureId) {
        return lectures.stream()
                .anyMatch(cl -> cl.getLecture().getId().equals(lectureId));
    }

    // === Getter & Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<CurriculumLecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<CurriculumLecture> lectures) {
        this.lectures = lectures;
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
