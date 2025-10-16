package com.PBL.enrollment.entity;

import com.PBL.curriculum.CurriculumLecture;
import com.PBL.lecture.entity.Lecture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 강의별 진도 엔티티
 * 사용자의 특정 강의에 대한 수강 진도를 나타냄
 */
@Entity
@Table(name = "lecture_progress")
@Getter
@Setter
@NoArgsConstructor
public class LectureProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_lecture_id", nullable = false)
    private CurriculumLecture curriculumLecture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public LectureProgress(Enrollment enrollment, Lecture lecture, CurriculumLecture curriculumLecture) {
        this.enrollment = enrollment;
        this.lecture = lecture;
        this.curriculumLecture = curriculumLecture;
    }

    /**
     * 강의 시작 처리
     */
    public void start() {
        if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
            this.startedAt = LocalDateTime.now();
        }
    }

    /**
     * 강의 완료 처리
     */
    public void complete() {
        this.status = ProgressStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 마크다운 강의 읽기 완료 처리
     */
    public void markAsRead() {
        if (lecture.getType().name().equals("MARKDOWN")) {
            complete();
        }
    }

    /**
     * 문제 강의 정답 제출 처리
     */
    public void markAsSolved() {
        if (lecture.getType().name().equals("PROBLEM")) {
            complete();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LectureProgress)) return false;
        LectureProgress that = (LectureProgress) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
