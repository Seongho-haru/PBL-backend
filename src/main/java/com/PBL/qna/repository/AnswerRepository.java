package com.PBL.qna.repository;

import com.PBL.qna.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
    // 질문별 답변 조회
    List<Answer> findByQuestionIdOrderByCreatedAtAsc(Long questionId);
    
    // 질문별 답변 조회 (페이지네이션)
    Page<Answer> findByQuestionIdOrderByCreatedAtAsc(Long questionId, Pageable pageable);
    
    // 질문별 답변 조회 (채택된 답변 우선)
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId ORDER BY a.isAccepted DESC, a.createdAt ASC")
    List<Answer> findByQuestionIdOrderByAcceptedAndCreatedAt(@Param("questionId") Long questionId);
    
    // 답글 조회 (부모 답변의 답글들)
    List<Answer> findByParentAnswerIdOrderByCreatedAtAsc(Long parentAnswerId);
    
    // 작성자별 답변 조회
    Page<Answer> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // 채택된 답변 조회
    @Query("SELECT a FROM Answer a WHERE a.isAccepted = true AND a.question.id = :questionId")
    Optional<Answer> findAcceptedAnswerByQuestionId(@Param("questionId") Long questionId);
    
    // 질문별 답변 수 조회
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId")
    Long countByQuestionId(@Param("questionId") Long questionId);
    
    // 답글 수 조회
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.parentAnswer.id = :parentAnswerId")
    Long countByParentAnswerId(@Param("parentAnswerId") Long parentAnswerId);
    
    // 작성자별 답변 수 조회
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.author.id = :authorId")
    Long countByAuthorId(@Param("authorId") Long authorId);
    
    // 채택된 답변 수 조회
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.author.id = :authorId AND a.isAccepted = true")
    Long countAcceptedAnswersByAuthorId(@Param("authorId") Long authorId);
    
    // 인기 답변 조회 (좋아요 순)
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId ORDER BY a.likes DESC, a.createdAt ASC")
    List<Answer> findPopularAnswersByQuestionId(@Param("questionId") Long questionId);
    
    // 최근 답변 조회
    @Query("SELECT a FROM Answer a ORDER BY a.createdAt DESC")
    Page<Answer> findRecentAnswers(Pageable pageable);
    
    // 답변과 답글을 함께 조회
    @Query("SELECT DISTINCT a FROM Answer a WHERE a.id = :id")
    Optional<Answer> findByIdWithReplies(@Param("id") Long id);
    
    // 질문의 모든 답변과 답글을 함께 조회
    @Query("SELECT DISTINCT a FROM Answer a WHERE a.question.id = :questionId ORDER BY a.createdAt ASC")
    List<Answer> findByQuestionIdWithReplies(@Param("questionId") Long questionId);
}
