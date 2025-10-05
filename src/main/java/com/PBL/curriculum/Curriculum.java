package com.PBL.curriculum;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 커리큘럼 엔티티
 * 여러 강의들을 체계적으로 구성한 학습 과정
 */
@Entity
@Table(name = "curriculums")
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 커리큘럼 제목 (필수)
     * 예: "Java 기초 과정", "알고리즘 마스터 클래스"
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 커리큘럼 설명
     * 학습 목표, 대상, 기대 효과 등
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 카테고리 (선택)
     * 예: "프로그래밍 기초", "알고리즘", "웹 개발"
     */
    @Column(length = 100)
    private String category;

    /**
     * 난이도 (선택)
     * 예: "초급", "중급", "고급"
     */
    @Column(length = 50)
    private String difficulty;

    /**
     * 예상 학습 시간 (시간 단위)
     * 전체 커리큘럼 완주에 필요한 예상 시간
     */
    private Integer estimatedHours;

    /**
     * 커리큘럼 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurriculumStatus status = CurriculumStatus.DRAFT;

    /**
     * 공개 여부
     */
    @Column(nullable = false)
    private Boolean isPublic = false;

    /**
     * 커리큘럼 섹션들
     * 순서를 가진 섹션 목록
     */
    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @BatchSize(size = 10)
    private List<CurriculumSection> sections = new ArrayList<>();

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
     * 섹션 추가
     */
    public void addSection(String sectionTitle, String sectionDescription) {
        int nextOrder = sections.size();
        CurriculumSection section = new CurriculumSection(sectionTitle, sectionDescription, this, nextOrder);
        this.sections.add(section);
    }

    /**
     * 섹션 제거
     */
    public void removeSection(CurriculumSection section) {
        this.sections.remove(section);
        section.setCurriculum(null);
        // 순서 재정렬
        reorderSections();
    }

    /**
     * 섹션 순서 재정렬
     */
    private void reorderSections() {
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setOrderIndex(i);
        }
    }

    /**
     * 커리큘럼 발행
     */
    public void publish() {
        if (sections.isEmpty()) {
            throw new IllegalStateException("섹션이 없는 커리큘럼은 발행할 수 없습니다.");
        }
        this.status = CurriculumStatus.PUBLISHED;
        this.isPublic = true;
    }

    /**
     * 커리큘럼 비공개 처리
     */
    public void unpublish() {
        this.status = CurriculumStatus.DRAFT;
        this.isPublic = false;
    }

    /**
     * 총 강의 수 계산
     */
    public int getTotalLectureCount() {
        return sections.stream()
                .mapToInt(CurriculumSection::getLectureCount)
                .sum();
    }

    /**
     * 총 섹션 수 반환
     */
    public int getSectionCount() {
        return sections != null ? sections.size() : 0;
    }

    /**
     * 발행된 커리큘럼인지 확인
     */
    public boolean isPublished() {
        return status == CurriculumStatus.PUBLISHED;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public CurriculumStatus getStatus() {
        return status;
    }

    public void setStatus(CurriculumStatus status) {
        this.status = status;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<CurriculumSection> getSections() {
        return sections;
    }

    public void setSections(List<CurriculumSection> sections) {
        this.sections = sections;
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
