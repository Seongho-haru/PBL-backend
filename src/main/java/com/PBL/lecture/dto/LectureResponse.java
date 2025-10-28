package com.PBL.lecture.dto;

import com.PBL.lab.core.dto.ConstraintsResponse;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lecture.LectureType;
import com.PBL.lecture.entity.Lecture;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 강의 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // 클래스 레벨
public class LectureResponse {
    private Long id;
    private String title;
    private String description;
    private LectureType type;
    private String category;
    private String difficulty;
    private ConstraintsResponse constraints;
    private Boolean isPublic;
    private Integer testCaseCount;
    private List<TestCaseResponse> testCases;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuthorInfo author;

    private List<String> tags;
    private String thumbnailImageUrl;
    private Integer durationMinutes;
    private String content;
    private String input_content;
    private String output_content;

    private Long languageId;


    public static  LectureResponse from(Lecture lecture) {

        if (lecture.getType() == LectureType.MARKDOWN) {
            return LectureResponse.builder()
                    .id(lecture.getId())
                    .title(lecture.getTitle())
                    .description(lecture.getDescription())
                    .type(lecture.getType())
                    .category(lecture.getCategory())
                    .difficulty(lecture.getDifficulty())
                    .isPublic(lecture.getIsPublic())
                    .createdAt(lecture.getCreatedAt())
                    .updatedAt(lecture.getUpdatedAt())
                    .author(AuthorInfo.from(lecture.getAuthor()))
                    .languageId(lecture.getLanguageId())

                    .tags(lecture.getTags())
                    .thumbnailImageUrl(lecture.getThumbnailImageUrl())
                    .content(lecture.getContent())
                    .durationMinutes(lecture.getDurationMinutes())
                    .build();
        } else if(lecture.getType() == LectureType.PROBLEM){
            List<TestCaseResponse> testCaseResponses = lecture.getTestCases() != null ?
                    lecture.getTestCases().stream()
                            .map(testCase -> new TestCaseResponse(
                                    testCase.getInput(),
                                    testCase.getExpectedOutput(),
                                    testCase.getOrderIndex()
                            )).toList()
                    : Collections.emptyList();

            return LectureResponse.builder()
                    .id(lecture.getId())
                    .title(lecture.getTitle())
                    .description(lecture.getDescription())
                    .type(lecture.getType())
                    .category(lecture.getCategory())
                    .difficulty(lecture.getDifficulty())
                    .isPublic(lecture.getIsPublic())
                    .testCaseCount(testCaseResponses.size())
                    .createdAt(lecture.getCreatedAt())
                    .updatedAt(lecture.getUpdatedAt())
                    .author(AuthorInfo.from(lecture.getAuthor()))
                    .constraints(ConstraintsResponse.from(lecture.getConstraints()))
                    .testCases(testCaseResponses)

                    .tags(lecture.getTags())
                    .thumbnailImageUrl(lecture.getThumbnailImageUrl())
                    .content(lecture.getContent())
                    .durationMinutes(lecture.getDurationMinutes())
                    .input_content(lecture.getInputContent())
                    .output_content(lecture.getOutputContent())
                    .languageId(lecture.getLanguageId())
                    .build();
        }else{
            return null;
        }

    }
}
