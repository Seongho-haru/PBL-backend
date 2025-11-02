package com.PBL.ai.tools;

import com.PBL.qna.dto.QnADTOs;
import com.PBL.qna.entity.Question;
import com.PBL.qna.enums.QuestionCategory;
import com.PBL.qna.enums.QuestionStatus;
import com.PBL.qna.service.QuestionService;
import com.PBL.search.SearchService;
import com.PBL.lecture.LectureType;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI가 사용할 Q&A 커뮤니티 및 검색 도구
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommunityTools {

    private final QuestionService questionService;
    private final SearchService searchService;

    // ========================================
    // Q&A 검색 도구
    // ========================================

    @Tool("질문과 답변을 검색합니다. 키워드, 해결 상태, 카테고리, 강의, 언어로 필터링할 수 있습니다.")
    public Page<Question> searchQuestions(
            @P("검색 키워드 (선택)") String keyword,
            @P("해결 상태 (선택): RESOLVED, UNRESOLVED") String status,
            @P("카테고리 (선택): GENERAL, BUG, CONCEPT, CODE_REVIEW") String category,
            @P("강의명 (선택)") String course,
            @P("프로그래밍 언어 (선택)") String language,
            @P("페이지 번호 (0부터 시작)") int page,
            @P("페이지 크기") int size) {

        log.debug("🔧 [도구 호출] searchQuestions - keyword:{}, status:{}", keyword, status);

        Pageable pageable = PageRequest.of(page, size > 0 ? size : 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        QuestionCategory questionCategory = null;
        if (category != null && !category.trim().isEmpty()) {
            try {
                questionCategory = QuestionCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ [도구 경고] searchQuestions - 잘못된 카테고리 무시: {}", category);
            }
        }

        QnADTOs.QuestionSearchRequest request = QnADTOs.QuestionSearchRequest.builder()
                .keyword(keyword)
                .status(status != null && !status.trim().isEmpty() ? QuestionStatus.valueOf(status.toUpperCase()) : null)
                .category(questionCategory)
                .course(course)
                .language(language)
                .build();

        Page<Question> result = questionService.searchQuestions(request, pageable);
        log.debug("✅ [도구 결과] searchQuestions - 검색 결과: {}개", result.getTotalElements());
        return result;
    }

    @Tool("질문 ID로 상세 정보를 조회합니다. 질문 내용과 모든 답변을 확인할 수 있습니다.")
    public Question getQuestionById(@P("질문 ID") Long questionId) {
        log.debug("🔧 [도구 호출] getQuestionById - questionId:{}", questionId);
        Question result = questionService.getQuestionById(questionId);
        log.debug("✅ [도구 결과] getQuestionById - 질문 제목: {}", result.getTitle());
        return result;
    }

    @Tool("최근에 등록된 질문 목록을 조회합니다. 커뮤니티의 최신 활동을 파악할 수 있습니다.")
    public Page<Question> getRecentQuestions(
            @P("페이지 번호 (0부터 시작)") int page,
            @P("페이지 크기") int size) {

        log.debug("🔧 [도구 호출] getRecentQuestions - page:{}, size:{}", page, size);
        Pageable pageable = PageRequest.of(page, size > 0 ? size : 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Question> result = questionService.searchQuestions(
                QnADTOs.QuestionSearchRequest.builder().build(), pageable);
        log.debug("✅ [도구 결과] getRecentQuestions - 질문 개수: {}개", result.getTotalElements());
        return result;
    }

    @Tool("아직 해결되지 않은 질문 목록을 조회합니다. 도움이 필요한 질문을 찾을 때 유용합니다.")
    public Page<Question> getUnresolvedQuestions(
            @P("페이지 번호 (0부터 시작)") int page,
            @P("페이지 크기") int size) {

        log.debug("🔧 [도구 호출] getUnresolvedQuestions - page:{}, size:{}", page, size);
        Pageable pageable = PageRequest.of(page, size > 0 ? size : 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Question> result = questionService.getQuestionsByStatus(QuestionStatus.UNRESOLVED, pageable);
        log.debug("✅ [도구 결과] getUnresolvedQuestions - 미해결 질문: {}개", result.getTotalElements());
        return result;
    }

    // ========================================
    // 통합 검색 도구
    // ========================================

    @Tool("커리큘럼과 강의를 동시에 검색합니다. 제목으로 검색하며, 강의는 카테고리와 난이도로 추가 필터링할 수 있습니다.")
    public Map<String, Object> unifiedSearch(
            @P("검색할 제목") String title,
            @P("카테고리 (강의만, 선택)") String category,
            @P("난이도 (강의만, 선택)") String difficulty,
            @P("강의 타입 (강의만, 선택): THEORY, PRACTICE, PROBLEM") String type,
            @P("페이지 번호 (0부터 시작)") int page,
            @P("페이지 크기") int size) {

        log.debug("🔧 [도구 호출] unifiedSearch - title:{}, page:{}, size:{}", title, page, size);

        LectureType lectureType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                lectureType = LectureType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ [도구 경고] unifiedSearch - 잘못된 타입 무시: {}", type);
            }
        }

        // isPublic이 null이면 공개만 검색 (기존 동작 유지)
        Map<String, Object> result = searchService.unifiedSearch(title, category, difficulty, lectureType, true, page, size);
        log.debug("✅ [도구 결과] unifiedSearch - 검색 완료");
        return result;
    }
}
