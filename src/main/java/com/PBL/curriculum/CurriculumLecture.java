package com.PBL.curriculum;

import com.PBL.lecture.Lecture;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 커리큘럼 섹션과 강의 간의 연결 엔티티
 * 강의의 순서와 추가 정보를 관리
 */
@Entity
@Table(name = "curriculum_lectures")
public class CurriculumLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 섹션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private CurriculumSection section;

    /**
     * 연결된 강의
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    /**
     * 섹션 내에서의 순서 (0부터 시작)
     */
    @Column(nullable = false)
    private Integer orderIndex;

    /**
     * 필수 강의 여부
     * true: 반드시 완료해야 하는 강의
     * false: 선택적으로 수강할 수 있는 강의
     */
    @Column(nullable = false)
    private Boolean isRequired = true;

    /**
     * 강의별 메모나 추가 설명
     * 커리큘럼 컨텍스트에서의 특별한 안내사항
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

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

    public CurriculumLecture() {}

    public CurriculumLecture(CurriculumSection section, Lecture lecture, Integer orderIndex) {
        this.section = section;
        this.lecture = lecture;
        this.orderIndex = orderIndex;
    }

    public CurriculumLecture(CurriculumSection section, Lecture lecture, Integer orderIndex, Boolean isRequired) {
        this.section = section;
        this.lecture = lecture;
        this.orderIndex = orderIndex;
        this.isRequired = isRequired;
    }

    // === 비즈니스 메서드 ===

    /**
     * 필수 강의로 설정
     */
    public void markAsRequired() {
        this.isRequired = true;
    }

    /**
     * 선택 강의로 설정
     */
    public void markAsOptional() {
        this.isRequired = false;
    }

    /**
     * 강의 제목 반환 (편의 메서드)
     */
    public String getLectureTitle() {
        return lecture != null ? lecture.getTitle() : null;
    }

    /**
     * 강의 유형 반환 (편의 메서드)
     */
    public String getLectureType() {
        return lecture != null && lecture.getType() != null ? 
               lecture.getType().getDisplayName() : null;
    }

    // === Getter & Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurriculumSection getSection() {
        return section;
    }

    public void setSection(CurriculumSection section) {
        this.section = section;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
