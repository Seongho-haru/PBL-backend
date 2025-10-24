package com.PBL.qna.dto;

import com.PBL.qna.entity.Answer;
import com.PBL.qna.entity.Question;
import com.PBL.qna.enums.QuestionCategory;
import com.PBL.qna.enums.QuestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class QnADTOs {

    // 질문 생성 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateQuestionRequest {
        @Schema(description = "질문 제목", example = "Spring Boot 설정 관련 질문")
        private String title;

        @Schema(description = "질문 내용", example = "Spring Boot에서 JPA 설정을 어떻게 해야 하나요?")
        private String content;

        @Schema(description = "질문 카테고리", example = "QUESTION")
        private QuestionCategory category;

        @Schema(description = "강의명", example = "자바스프링")
        private String course;

        @Schema(description = "프로그래밍 언어", example = "Java")
        private String language;
    }

    // 질문 수정 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateQuestionRequest {
        @Schema(description = "질문 제목")
        private String title;

        @Schema(description = "질문 내용")
        private String content;

        @Schema(description = "질문 카테고리")
        private QuestionCategory category;

        @Schema(description = "강의명")
        private String course;

        @Schema(description = "프로그래밍 언어")
        private String language;
    }

    // 답변 생성 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAnswerRequest {
        @Schema(description = "답변 내용", example = "JPA 설정은 다음과 같이 하시면 됩니다...")
        private String content;

        @Schema(description = "부모 답변 ID (답글인 경우)", example = "1")
        private Long parentAnswerId;
    }

    // 답변 수정 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAnswerRequest {
        @Schema(description = "답변 내용")
        private String content;
    }

    // 질문 목록 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionListResponse {
        @Schema(description = "질문 ID")
        private Long id;

        @Schema(description = "질문 제목")
        private String title;

        @Schema(description = "질문 상태")
        private QuestionStatus status;

        @Schema(description = "질문 카테고리")
        private QuestionCategory category;

        @Schema(description = "강의명")
        private String course;

        @Schema(description = "프로그래밍 언어")
        private String language;

        @Schema(description = "작성자명")
        private String authorName;

        @Schema(description = "댓글 수")
        private Integer commentCount;

        @Schema(description = "좋아요 수")
        private Integer likes;

        @Schema(description = "작성일")
        private LocalDateTime createdAt;

        public static QuestionListResponse from(Question question) {
            return QuestionListResponse.builder()
                    .id(question.getId())
                    .title(question.getTitle())
                    .status(question.getStatus())
                    .category(question.getCategory())
                    .course(question.getCourse())
                    .language(question.getLanguage())
                    .authorName(question.getAuthor().getUsername())
                    .commentCount(question.getCommentCount())
                    .likes(question.getLikes())
                    .createdAt(question.getCreatedAt())
                    .build();
        }
    }

    // 질문 상세 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDetailResponse {
        @Schema(description = "질문 ID")
        private Long id;

        @Schema(description = "질문 제목")
        private String title;

        @Schema(description = "질문 내용")
        private String content;

        @Schema(description = "질문 상태")
        private QuestionStatus status;

        @Schema(description = "질문 카테고리")
        private QuestionCategory category;

        @Schema(description = "강의명")
        private String course;

        @Schema(description = "프로그래밍 언어")
        private String language;

        @Schema(description = "작성자명")
        private String authorName;

        @Schema(description = "좋아요 수")
        private Integer likes;

        @Schema(description = "작성일")
        private LocalDateTime createdAt;

        @Schema(description = "수정일")
        private LocalDateTime updatedAt;

        @Schema(description = "답변 목록")
        private List<AnswerResponse> answers;

        public static QuestionDetailResponse from(Question question) {
            return QuestionDetailResponse.builder()
                    .id(question.getId())
                    .title(question.getTitle())
                    .content(question.getContent())
                    .status(question.getStatus())
                    .category(question.getCategory())
                    .course(question.getCourse())
                    .language(question.getLanguage())
                    .authorName(question.getAuthor().getUsername())
                    .likes(question.getLikes())
                    .createdAt(question.getCreatedAt())
                    .updatedAt(question.getUpdatedAt())
                    .answers(question.getAnswers().stream()
                            .map(AnswerResponse::from)
                            .toList())
                    .build();
        }
    }

    // 답변 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerResponse {
        @Schema(description = "답변 ID")
        private Long id;

        @Schema(description = "답변 내용")
        private String content;

        @Schema(description = "작성자명")
        private String authorName;

        @Schema(description = "좋아요 수")
        private Integer likes;

        @Schema(description = "채택 여부")
        private Boolean isAccepted;

        @Schema(description = "부모 답변 ID")
        private Long parentAnswerId;

        @Schema(description = "작성일")
        private LocalDateTime createdAt;

        @Schema(description = "수정일")
        private LocalDateTime updatedAt;

        @Schema(description = "답글 목록")
        private List<AnswerResponse> replies;

        public static AnswerResponse from(Answer answer) {
            return AnswerResponse.builder()
                    .id(answer.getId())
                    .content(answer.getContent())
                    .authorName(answer.getAuthor().getUsername())
                    .likes(answer.getLikes())
                    .isAccepted(answer.getIsAccepted())
                    .parentAnswerId(answer.getParentAnswer() != null ? answer.getParentAnswer().getId() : null)
                    .createdAt(answer.getCreatedAt())
                    .updatedAt(answer.getUpdatedAt())
                    .replies(answer.getReplies().stream()
                            .map(AnswerResponse::from)
                            .toList())
                    .build();
        }
    }

    // 질문 검색 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionSearchRequest {
        @Schema(description = "검색 키워드")
        private String keyword;

        @Schema(description = "질문 상태")
        private QuestionStatus status;

        @Schema(description = "질문 카테고리")
        private QuestionCategory category;

        @Schema(description = "강의명")
        private String course;

        @Schema(description = "프로그래밍 언어")
        private String language;

        @Schema(description = "작성자 ID")
        private Long authorId;
    }

    // 좋아요 응답
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResponse {
        @Schema(description = "좋아요 수")
        private Integer likes;

        @Schema(description = "좋아요 여부")
        private Boolean isLiked;
    }
}
