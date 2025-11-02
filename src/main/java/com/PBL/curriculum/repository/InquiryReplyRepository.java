package com.PBL.curriculum.repository;

import com.PBL.curriculum.entity.InquiryReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {
    
    /**
     * 리뷰/문의별 답글 목록 조회 (생성일 순)
     */
    List<InquiryReply> findByReviewIdOrderByCreatedAtAsc(Long reviewId);
    
    /**
     * 작성자별 답글 목록 조회
     */
    List<InquiryReply> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}

