package com.PBL.qna.service;

import com.PBL.qna.dto.QnADTOs;
import com.PBL.qna.entity.Question;
import com.PBL.qna.enums.QuestionCategory;
import com.PBL.qna.enums.QuestionStatus;
import com.PBL.qna.repository.QuestionRepository;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import com.PBL.user.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    // 질문 생성
    @Transactional
    public Question createQuestion(QnADTOs.CreateQuestionRequest request, Long authorId) {
        // 정지 상태 체크
        userValidationService.validateUserCanCreateContent(authorId);
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + authorId));

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status(QuestionStatus.UNRESOLVED)
                .category(request.getCategory())
                .course(request.getCourse())
                .language(request.getLanguage())
                .author(author)
                .build();

        return questionRepository.save(question);
    }

    // 질문 수정
    @Transactional
    public Question updateQuestion(Long questionId, QnADTOs.UpdateQuestionRequest request, Long userId) {
        // 정지 상태 체크
        userValidationService.validateUserCanModifyContent(userId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));

        // 작성자 확인
        if (!question.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("질문을 수정할 권한이 없습니다.");
        }

        if (request.getTitle() != null) {
            question.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            question.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            question.setCategory(request.getCategory());
        }
        if (request.getCourse() != null) {
            question.setCourse(request.getCourse());
        }
        if (request.getLanguage() != null) {
            question.setLanguage(request.getLanguage());
        }

        return questionRepository.save(question);
    }

    // 질문 삭제
    @Transactional
    public void deleteQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));

        // 작성자 확인
        if (!question.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("질문을 삭제할 권한이 없습니다.");
        }

        questionRepository.delete(question);
    }

    // 질문 조회 (상세)
    public Question getQuestionById(Long questionId) {
        return questionRepository.findByIdWithAnswers(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));
    }

    // 질문 목록 조회 (검색)
    public Page<Question> searchQuestions(QnADTOs.QuestionSearchRequest request, Pageable pageable) {
        return questionRepository.findBySearchCriteria(
                request.getKeyword(),
                request.getStatus(),
                request.getCategory(),
                request.getCourse(),
                request.getLanguage(),
                request.getAuthorId(),
                pageable
        );
    }

    // 전체 질문 목록 조회
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    // 상태별 질문 조회
    public Page<Question> getQuestionsByStatus(QuestionStatus status, Pageable pageable) {
        return questionRepository.findByStatus(status, pageable);
    }

    // 카테고리별 질문 조회
    public Page<Question> getQuestionsByCategory(QuestionCategory category, Pageable pageable) {
        return questionRepository.findByCategory(category, pageable);
    }

    // 강의별 질문 조회
    public Page<Question> getQuestionsByCourse(String course, Pageable pageable) {
        return questionRepository.findByCourse(course, pageable);
    }

    // 언어별 질문 조회
    public Page<Question> getQuestionsByLanguage(String language, Pageable pageable) {
        return questionRepository.findByLanguage(language, pageable);
    }

    // 작성자별 질문 조회
    public Page<Question> getQuestionsByAuthor(Long authorId, Pageable pageable) {
        return questionRepository.findByAuthorId(authorId, pageable);
    }

    // 인기 질문 조회
    public Page<Question> getPopularQuestions(Pageable pageable) {
        return questionRepository.findPopularQuestions(pageable);
    }

    // 최신 질문 조회
    public Page<Question> getRecentQuestions(Pageable pageable) {
        return questionRepository.findRecentQuestions(pageable);
    }

    // 해결된 질문 조회
    public Page<Question> getResolvedQuestions(Pageable pageable) {
        return questionRepository.findResolvedQuestions(pageable);
    }

    // 미해결 질문 조회
    public Page<Question> getUnresolvedQuestions(Pageable pageable) {
        return questionRepository.findUnresolvedQuestions(pageable);
    }

    // 질문 좋아요
    @Transactional
    public void likeQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));
        
        question.incrementLikes();
        questionRepository.save(question);
    }

    // 질문 좋아요 취소
    @Transactional
    public void unlikeQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));
        
        question.decrementLikes();
        questionRepository.save(question);
    }

    // 질문 해결 상태 토글 (해결 <-> 미해결)
    @Transactional
    public boolean toggleQuestionResolution(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));

        // 작성자 확인
        if (!question.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("질문 상태를 변경할 권한이 없습니다.");
        }

        // 현재 상태 확인 후 토글
        boolean isResolved = question.getStatus() == QuestionStatus.RESOLVED;
        if (isResolved) {
            question.markAsUnresolved();
        } else {
            question.markAsResolved();
        }
        questionRepository.save(question);

        // 변경된 상태 반환 (true: 해결됨, false: 미해결)
        return !isResolved;
    }

    // 질문 해결 상태 변경 (하위 호환성을 위해 유지)
    @Transactional
    public void markQuestionAsResolved(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));

        // 작성자 확인
        if (!question.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("질문 상태를 변경할 권한이 없습니다.");
        }

        question.markAsResolved();
        questionRepository.save(question);
    }

    // 질문 미해결 상태 변경 (하위 호환성을 위해 유지)
    @Transactional
    public void markQuestionAsUnresolved(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));

        // 작성자 확인
        if (!question.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("질문 상태를 변경할 권한이 없습니다.");
        }

        question.markAsUnresolved();
        questionRepository.save(question);
    }

    // 질문 통계 조회
    public Map<String, Object> getQuestionStats() {
        Long totalQuestions = questionRepository.count();
        Long unresolvedQuestions = questionRepository.countByStatus(QuestionStatus.UNRESOLVED);
        Long resolvedQuestions = questionRepository.countByStatus(QuestionStatus.RESOLVED);
        
        List<Object[]> courseStats = questionRepository.countByCourse();
        List<Object[]> languageStats = questionRepository.countByLanguage();

        return Map.of(
                "totalQuestions", totalQuestions,
                "unresolvedQuestions", unresolvedQuestions,
                "resolvedQuestions", resolvedQuestions,
                "courseStats", courseStats,
                "languageStats", languageStats
        );
    }

    // 사용자별 질문 통계
    public Map<String, Object> getUserQuestionStats(Long userId) {
        Long userQuestions = questionRepository.countByAuthorId(userId);
        
        return Map.of(
                "userQuestions", userQuestions
        );
    }
}
