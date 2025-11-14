package db.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDto {

    @JsonProperty("product_id")
    private String productId;

    private String isbn;
    private String type;
    private String title;
    private List<String> authors = new ArrayList<>();
    private List<String> publishers = new ArrayList<>();

    @JsonProperty("publisher_details")
    private List<Map<String, Object>> publisherDetails = new ArrayList<>();

    private String description;
    private String language;

    @JsonProperty("content_levels")
    private List<String> contentLevels = new ArrayList<>();

    @JsonProperty("publication_date")
    private String publicationDate;

    @JsonProperty("page_count")
    private Integer pageCount;

    @JsonProperty("duration_seconds")
    private Integer durationSeconds;

    @JsonProperty("average_rating")
    private Double averageRating;

    private String url;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("cover_image")
    private String coverImage;

    private List<String> topics = new ArrayList<>();

    @JsonProperty("topic_hierarchy")
    private List<Map<String, Object>> topicHierarchy = new ArrayList<>();

    private List<String> toc = new ArrayList<>();

    @JsonProperty("has_sandbox")
    private Boolean hasSandbox;

    @JsonProperty("has_quiz_question_bank")
    private Boolean hasQuizQuestionBank;

    @JsonProperty("display_format")
    private String displayFormat;

    @JsonProperty("marketing_type_name")
    private String marketingTypeName;

    private String ourn;

    @JsonProperty("epub_file_path")
    private String epubFilePath;
}
