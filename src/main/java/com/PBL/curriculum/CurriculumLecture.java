package com.PBL.curriculum;

import com.PBL.lecture.entity.Lecture;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 커리큘럼-강의 연결 엔티티 (폴더-파일 관계)
 * 커리큘럼과 강의 간의 다대다 관계를 관리
 */
@Entity
@Table(name = "curriculum_lectures")
public class CurriculumLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 커리큘럼
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    /**
     * 연결된 강의 ID
     * 직접 참조 대신 ID만 저장하여 성능 최적화
     */
    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;

    /**
     * 연결된 강의 (Lazy Loading)
     * 필요할 때만 로드
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", insertable = false, updatable = false)
    private Lecture lecture;

    /**
     * 커리큘럼 내 순서
     */
    @Column(nullable = false)
    private Integer orderIndex = 1;

    /**
     * 필수 강의 여부
     * true: 필수 강의, false: 선택 강의
     */
    @Column(nullable = false)
    private Boolean isRequired = true;

    /**
     * 원본 강의 작성자 (다른 사용자 공개 강의 링크 시)
     */
    @Column(length = 100)
    private String originalAuthor;

    /**
     * 원본 강의 출처 정보
     */
    @Column(length = 500)
    private String sourceInfo;

    /**
     * 생성 시간
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // === 생성자 ===

    public CurriculumLecture() {}

    public CurriculumLecture(Curriculum curriculum, Long lectureId, boolean isRequired) {
        this.curriculum = curriculum;
        this.lectureId = lectureId;
        this.isRequired = isRequired;
    }

    // === 비즈니스 메서드 ===

    /**
     * 필수 강의인지 확인
     */
    public boolean isRequired() {
        return Boolean.TRUE.equals(this.isRequired);
    }

    /**
     * 선택 강의인지 확인
     */
    public boolean isOptional() {
        return !isRequired();
    }

    /**
     * 다른 사용자의 공개 강의인지 확인
     */
    public boolean isLinkedFromOtherUser() {
        return this.originalAuthor != null && !this.originalAuthor.trim().isEmpty();
    }

    // === Getter & Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }

    public Long getLectureId() {
        return lectureId;
    }

    public void setLectureId(Long lectureId) {
        this.lectureId = lectureId;
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

    public void setRequired(Boolean required) {
        this.isRequired = required;
    }

    public String getOriginalAuthor() {
        return originalAuthor;
    }

    public void setOriginalAuthor(String originalAuthor) {
        this.originalAuthor = originalAuthor;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
