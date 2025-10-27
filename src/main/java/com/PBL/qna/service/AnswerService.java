package com.PBL.qna.service;

import com.PBL.qna.dto.QnADTOs;
import com.PBL.qna.entity.Answer;
import com.PBL.qna.entity.Question;
import com.PBL.qna.repository.AnswerRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    // 답변 생성
    @Transactional
    public Answer createAnswer(Long questionId, QnADTOs.CreateAnswerRequest request, Long authorId) {
        // 정지 상태 체크
        userValidationService.validateUserCanCreateContent(authorId);
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다: " + questionId));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + authorId));

        Answer parentAnswer = null;
        if (request.getParentAnswerId() != null) {
            parentAnswer = answerRepository.findById(request.getParentAnswerId())
                    .orElseThrow(() -> new RuntimeException("부모 답변을 찾을 수 없습니다: " + request.getParentAnswerId()));
        }

        Answer answer = Answer.builder()
                .content(request.getContent())
                .question(question)
                .author(author)
                .parentAnswer(parentAnswer)
                .build();

        Answer savedAnswer = answerRepository.save(answer);

        // 질문의 댓글 수 증가
        question.incrementCommentCount();
        questionRepository.save(question);

        return savedAnswer;
    }

    // 답변 수정
    @Transactional
    public Answer updateAnswer(Long answerId, QnADTOs.UpdateAnswerRequest request, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다: " + answerId));

        // 작성자 확인
        if (!answer.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("답변을 수정할 권한이 없습니다.");
        }

        if (request.getContent() != null) {
            answer.setContent(request.getContent());
        }

        return answerRepository.save(answer);
    }

    // 답변 삭제
    @Transactional
    public void deleteAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다: " + answerId));

        // 작성자 확인
        if (!answer.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("답변을 삭제할 권한이 없습니다.");
        }

        // 질문의 댓글 수 감소
        Question question = answer.getQuestion();
        question.decrementCommentCount();
        questionRepository.save(question);

        answerRepository.delete(answer);
    }

    // 답변 조회 (상세)
    public Answer getAnswerById(Long answerId) {
        return answerRepository.findByIdWithReplies(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다: " + answerId));
    }

    // 질문별 답변 조회
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionIdWithReplies(questionId);
    }

    // 질문별 답변 조회 (페이지네이션)
    public Page<Answer> getAnswersByQuestionId(Long questionId, Pageable pageable) {
        return answerRepository.findByQuestionIdOrderByCreatedAtAsc(questionId, pageable);
    }

    // 작성자별 답변 조회
    public Page<Answer> getAnswersByAuthor(Long authorId, Pageable pageable) {
        return answerRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
    }

    // 인기 답변 조회
    public List<Answer> getPopularAnswersByQuestionId(Long questionId) {
        return answerRepository.findPopularAnswersByQuestionId(questionId);
    }

    // 최근 답변 조회
    public Page<Answer> getRecentAnswers(Pageable pageable) {
        return answerRepository.findRecentAnswers(pageable);
    }

    // 답변 좋아요
    @Transactional
    public void likeAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다: " + answerId));
        
        answer.incrementLikes();
        answerRepository.save(answer);
    }

    // 답변 좋아요 취소
    @Transactional
    public void unlikeAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다: " + answerId));
        
        answer.decrementLikes();
        answerRepository.save(answer);
    }

    // 답변 채택
    @Transactional
    public void acceptAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다: " + answerId));

        Question question = answer.getQuestion();

        // 질문 작성자 확인
        if (!question.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("답변을 채택할 권한이 없습니다.");
        }

        // 기존 채택된 답변이 있다면 취소
        Optional<Answer> existingAcceptedAnswer = answerRepository.findAcceptedAnswerByQuestionId(question.getId());
        if (existingAcceptedAnswer.isPresent() && !existingAcceptedAnswer.get().getId().equals(answerId)) {
            existingAcceptedAnswer.get().unaccept();
            answerRepository.save(existingAcceptedAnswer.get());
        }

        // 답변 채택
        answer.accept();
        answerRepository.save(answer);

        // 질문을 해결 상태로 변경
        question.markAsResolved();
        questionRepository.save(question);
    }

    // 답변 채택 취소
    @Transactional
    public void unacceptAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다: " + answerId));

        Question question = answer.getQuestion();

        // 질문 작성자 확인
        if (!question.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("답변 채택을 취소할 권한이 없습니다.");
        }

        // 답변 채택 취소
        answer.unaccept();
        answerRepository.save(answer);

        // 질문을 미해결 상태로 변경
        question.markAsUnresolved();
        questionRepository.save(question);
    }

    // 답변 통계 조회
    public Map<String, Object> getAnswerStats() {
        Long totalAnswers = answerRepository.count();
        
        return Map.of(
                "totalAnswers", totalAnswers
        );
    }

    // 사용자별 답변 통계
    public Map<String, Object> getUserAnswerStats(Long userId) {
        Long userAnswers = answerRepository.countByAuthorId(userId);
        Long acceptedAnswers = answerRepository.countAcceptedAnswersByAuthorId(userId);
        
        return Map.of(
                "userAnswers", userAnswers,
                "acceptedAnswers", acceptedAnswers
        );
    }
}
