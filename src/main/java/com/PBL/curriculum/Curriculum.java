package com.PBL.curriculum;

import com.PBL.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 커리큘럼 엔티티 (폴더 역할)
 * 여러 강의들을 묶어서 관리하는 컨테이너
 */
@Entity
@Table(name = "curriculums")
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 커리큘럼 제목 (필수)
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 커리큘럼 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 공개 커리큘럼 여부
     * true: 다른 사용자가 볼 수 있음
     * false: 비공개 커리큘럼
     */
    @Column(nullable = false)
    private Boolean isPublic = false;

    /**
     * 커리큘럼 난이도
     * 기초, 중급, 고급, 전문가
     */
    @Column(length = 20)
    private String difficulty;

    /**
     * 커리큘럼 간단 소개
     * 목록에서 보여줄 짧은 설명
     */
    @Column(length = 500)
    private String summary;

    /**
     * 평균 별점
     * 0.0 ~ 5.0
     */
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    /**
     * 수강생 수
     * 현재 수강 중인 학생 수
     */
    @Column
    private Integer studentCount = 0;

    /**
     * 커리큘럼 작성자
     * 커리큘럼을 생성한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * 커리큘럼에 포함된 강의들 (폴더-파일 관계)
     */
    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @BatchSize(size = 20)
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

    public Curriculum() {}

    public Curriculum(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // === 비즈니스 메서드 ===

    /**
     * 강의 추가
     */
    public void addLecture(Long lectureId, boolean isRequired, String originalAuthor, String sourceInfo) {
        CurriculumLecture curriculumLecture = new CurriculumLecture();
        curriculumLecture.setCurriculum(this);
        curriculumLecture.setLectureId(lectureId);
        curriculumLecture.setRequired(isRequired);
        curriculumLecture.setOriginalAuthor(originalAuthor);
        curriculumLecture.setSourceInfo(sourceInfo);
        curriculumLecture.setOrderIndex(this.lectures.size() + 1);
        this.lectures.add(curriculumLecture);
    }

    /**
     * 강의 제거
     */
    public void removeLecture(Long lectureId) {
        this.lectures.removeIf(cl -> cl.getLectureId().equals(lectureId));
        reorderLectures();
    }

    /**
     * 강의 순서 재정렬
     */
    public void reorderLectures() {
        for (int i = 0; i < this.lectures.size(); i++) {
            this.lectures.get(i).setOrderIndex(i + 1);
        }
    }

    /**
     * 커리큘럼 공개
     */
    public void publish() {
        this.isPublic = true;
    }

    /**
     * 커리큘럼 비공개
     */
    public void unpublish() {
        this.isPublic = false;
    }

    /**
     * 공개 커리큘럼인지 확인
     */
    public boolean isPublicCurriculum() {
        return Boolean.TRUE.equals(this.isPublic);
    }

    /**
     * 총 강의 수
     */
    public int getTotalLectureCount() {
        return this.lectures != null ? this.lectures.size() : 0;
    }

    /**
     * 필수 강의 수
     */
    public int getRequiredLectureCount() {
        return this.lectures != null ? 
            (int) this.lectures.stream().filter(CurriculumLecture::isRequired).count() : 0;
    }

    /**
     * 선택 강의 수
     */
    public int getOptionalLectureCount() {
        return getTotalLectureCount() - getRequiredLectureCount();
    }

    /**
     * 특정 강의가 포함되어 있는지 확인
     */
    public boolean containsLecture(Long lectureId) {
        return this.lectures != null && 
            this.lectures.stream().anyMatch(cl -> cl.getLectureId().equals(lectureId));
    }

    /**
     * 작성자인지 확인
     */
    public boolean isAuthor(User user) {
        return this.author != null && this.author.equals(user);
    }

    /**
     * 작성자인지 확인 (ID로)
     */
    public boolean isAuthor(Long userId) {
        return this.author != null && this.author.getId().equals(userId);
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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}
