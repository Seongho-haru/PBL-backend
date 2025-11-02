package com.PBL.lab.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(name = "product_id", unique = true, nullable = false)
    private String productId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, length = 1000)
    private String title;

    @ElementCollection
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author")
    @Builder.Default
    private List<String> authors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "book_publishers", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publisher")
    @Builder.Default
    private List<String> publishers = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 10)
    private String language;

    @ElementCollection
    @CollectionTable(name = "book_content_levels", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "level")
    @Builder.Default
    private List<String> contentLevels = new ArrayList<>();

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(length = 500)
    private String url;

    @Column(name = "web_url", length = 500)
    private String webUrl;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @ElementCollection
    @CollectionTable(name = "book_topics", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "topic")
    @Builder.Default
    private List<String> topics = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "book_categories", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "category")
    @Builder.Default
    private List<String> categories = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "book_tags", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // TOC (Table of Contents) - RAG에서 중요한 메타정보
    @ElementCollection
    @CollectionTable(name = "book_toc", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "toc_item", columnDefinition = "TEXT")
    @OrderColumn(name = "toc_order")
    @Builder.Default
    private List<String> toc = new ArrayList<>();

    @Column(name = "has_sandbox")
    private Boolean hasSandbox;

    @Column(name = "has_quiz_question_bank")
    private Boolean hasQuizQuestionBank;

    @Column(name = "display_format", length = 50)
    private String displayFormat;

    @Column(name = "marketing_type_name", length = 50)
    private String marketingTypeName;

    @Column(length = 200)
    private String ourn;

    @Column(name = "epub_file_path", length = 1000)
    private String epubFilePath;
}
