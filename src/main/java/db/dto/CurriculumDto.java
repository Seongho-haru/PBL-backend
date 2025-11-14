package db.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class CurriculumDto {
    @JsonProperty("step_number")
    private Integer stepNumber;

    @JsonProperty("step_title")
    private String stepTitle;

    @JsonProperty("description")
    private String description;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("category")
    private String category;

    @JsonProperty("difficulty")
    private String difficulty;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("duration_minutes")
    private Integer durationMinutes;

    @JsonProperty("learning_objectives")
    private String learningObjectives;

    @JsonProperty("book_ids")
    private List<Integer> bookIds;

    @JsonProperty("thumbnail_image_url")
    private String thumbnailImageUrl;
}
