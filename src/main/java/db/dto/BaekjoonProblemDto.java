package db.dto;

import com.PBL.lab.core.dto.ConstraintsResponse;
import com.PBL.lecture.LectureType;
import com.PBL.lecture.dto.TestCaseRequest;
import com.PBL.lecture.dto.TestCaseResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaekjoonProblemDto {
    private String title;
    private String description;

    private LectureType type;
    private String category;
    private String difficulty;
    private Boolean isPublic;
    private ConstraintsResponse constraints;
    private List<TestCaseResponse> testCases;
    private List<String> tags;

    private String content;
    @JsonProperty("inputDescription")
    private String input_content;
    @JsonProperty("outputDescription")
    private String output_content;

    private Double timeLimit;
    private Integer memoryLimit;
    private Long languageId;

    @JsonProperty("metadata")
    private Map<String, Object> metadata; // metadata 필드 추가
}
