package com.PBL.lab.grade.entity;

import com.PBL.lab.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "grade", indexes = {
        @Index(name = "idx_grade_token", columnList = "token"),
        @Index(name = "idx_grade_status", columnList = "status_id"),
        @Index(name = "idx_grade_created_at", columnList = "created_at")
})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Grade extends BaseEntity {

    @NotNull
    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    /**
     * 추가적인 실행 관련 메시지
     * - 시스템에서 생성한 추가 정보나 설명
     * - 오류 발생 시 상세한 원인 설명
     * - 디버깅을 위한 추가 컨텍스트 정보
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;

}
