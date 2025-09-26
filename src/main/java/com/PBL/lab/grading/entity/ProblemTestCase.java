package com.PBL.lab.grading.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "problem_test_case")
@Data
public class ProblemTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //문제 번호
    @Column
    private Long problemId;

    @Column
    private String strin;

    @Column
    private String strout;
}
