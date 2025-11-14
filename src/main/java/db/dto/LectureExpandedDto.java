package db.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class LectureExpandedDto {
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("type")
    private String type;

    @JsonProperty("category")
    private String category;

    @JsonProperty("difficulty")
    private String difficulty;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("duration_minutes")
    private Integer durationMinutes;

    @JsonProperty("learning_objectives")
    private String learningObjectives;

    @JsonProperty("thumbnail_image_url")
    private String thumbnailImageUrl;

    @JsonProperty("content")
    private String content;

    @JsonProperty("curriculum_id")
    private Long curriculumId;
}
