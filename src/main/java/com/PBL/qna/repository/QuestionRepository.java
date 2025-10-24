package com.PBL.qna.repository;

import com.PBL.qna.entity.Question;
import com.PBL.qna.enums.QuestionCategory;
import com.PBL.qna.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // 제목으로 검색
    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword%")
    Page<Question> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);
    
    // 내용으로 검색
    @Query("SELECT q FROM Question q WHERE q.content LIKE %:keyword%")
    Page<Question> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);
    
    // 제목 또는 내용으로 검색
    @Query("SELECT q FROM Question q WHERE " +
           "q.title LIKE %:keyword% OR " +
           "q.content LIKE %:keyword%")
    Page<Question> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);
    
    // 상태별 조회
    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);
    
    // 카테고리별 조회
    Page<Question> findByCategory(QuestionCategory category, Pageable pageable);
    
    // 강의별 조회
    Page<Question> findByCourse(String course, Pageable pageable);
    
    // 언어별 조회
    Page<Question> findByLanguage(String language, Pageable pageable);
    
    // 작성자별 조회
    Page<Question> findByAuthorId(Long authorId, Pageable pageable);
    
    // 복합 검색
    @Query("SELECT q FROM Question q WHERE " +
           "(:keyword IS NULL OR q.title LIKE %:keyword% OR q.content LIKE %:keyword%) AND " +
           "(:status IS NULL OR q.status = :status) AND " +
           "(:category IS NULL OR q.category = :category) AND " +
           "(:course IS NULL OR q.course = :course) AND " +
           "(:language IS NULL OR q.language = :language) AND " +
           "(:authorId IS NULL OR q.author.id = :authorId)")
    Page<Question> findBySearchCriteria(
            @Param("keyword") String keyword,
            @Param("status") QuestionStatus status,
            @Param("category") QuestionCategory category,
            @Param("course") String course,
            @Param("language") String language,
            @Param("authorId") Long authorId,
            Pageable pageable);
    
    // 인기 질문 조회 (좋아요 순)
    @Query("SELECT q FROM Question q ORDER BY q.likes DESC, q.createdAt DESC")
    Page<Question> findPopularQuestions(Pageable pageable);
    
    // 최신 질문 조회
    @Query("SELECT q FROM Question q ORDER BY q.createdAt DESC")
    Page<Question> findRecentQuestions(Pageable pageable);
    
    // 해결된 질문 조회
    @Query("SELECT q FROM Question q WHERE q.status = 'RESOLVED' ORDER BY q.updatedAt DESC")
    Page<Question> findResolvedQuestions(Pageable pageable);
    
    // 미해결 질문 조회
    @Query("SELECT q FROM Question q WHERE q.status = 'UNRESOLVED' ORDER BY q.createdAt DESC")
    Page<Question> findUnresolvedQuestions(Pageable pageable);
    
    // 질문과 답변을 함께 조회 (N+1 문제 해결)
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.answers WHERE q.id = :id")
    Optional<Question> findByIdWithAnswers(@Param("id") Long id);
    
    // 통계 조회
    @Query("SELECT COUNT(q) FROM Question q WHERE q.status = :status")
    Long countByStatus(@Param("status") QuestionStatus status);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.category = :category")
    Long countByCategory(@Param("category") QuestionCategory category);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.author.id = :authorId")
    Long countByAuthorId(@Param("authorId") Long authorId);
    
    // 강의별 질문 수 조회
    @Query("SELECT q.course, COUNT(q) FROM Question q WHERE q.course IS NOT NULL GROUP BY q.course ORDER BY COUNT(q) DESC")
    List<Object[]> countByCourse();
    
    // 언어별 질문 수 조회
    @Query("SELECT q.language, COUNT(q) FROM Question q WHERE q.language IS NOT NULL GROUP BY q.language ORDER BY COUNT(q) DESC")
    List<Object[]> countByLanguage();
}
