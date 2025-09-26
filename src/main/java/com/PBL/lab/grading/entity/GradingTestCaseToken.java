package com.PBL.lab.grading.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "grading_testcase_tokens")
@Data
public class GradingTestCaseToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "grading_id")
    private Grading grading;
    
    @Column(name = "submission_token")
    private String submissionToken;
}
