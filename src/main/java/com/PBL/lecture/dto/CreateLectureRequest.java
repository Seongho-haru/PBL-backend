package com.PBL.lecture.dto;

import com.PBL.lab.core.dto.ConstraintsResponse;
import com.PBL.lecture.LectureType;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lecture.entity.Lecture;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 강의 생성/수정 요청 DTO
 */
@Data
@Builder
public class CreateLectureRequest {
    private String title;
    private String description;
    private LectureType type;
    private String category;
    private String difficulty;
    private Boolean isPublic;
    private ConstraintsResponse constraints;
    private List<TestCaseRequest> testCases;
}