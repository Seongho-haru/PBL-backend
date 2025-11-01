package db.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaekjoonCurriclumDTO {
    // 필수 필드
    @JsonProperty("step_title")
    private String title; // 커리큘럼 제목 (필수)

    @JsonProperty("description")
    private String description; // 커리큘럼 설명

    @JsonProperty("is_public")
    private Boolean isPublic = true; // 공개 여부 (백준 커리큘럼은 기본 공개)

    @JsonProperty("author_id")
    private Long authorId = 1L; // 기본 = 1L (시스템 관리자)

    // 선택 필드
    @JsonProperty("category")
    private String category = "알고리즘"; // 카테고리

    @JsonProperty("difficulty")
    private String difficulty; // 난이도

    @JsonProperty("summary")
    private String summary; // 간단 소개

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<>(); // 태그

    @JsonProperty("thumbnail_image_url")
    private String thumbnailImageUrl; // 썸네일 이미지 - 필수아님

    @JsonProperty("duration_minutes")
    private Integer durationMinutes = 30; // 소요 시간

    @JsonProperty("problem_ids")
    private List<Long> problemIds = new ArrayList<>(); // 백준 문제 ID 목록

    @JsonProperty("toc")
    private List<String> toc = new ArrayList<>(); // 문제 제목 목록 (Table of Contents)
}
