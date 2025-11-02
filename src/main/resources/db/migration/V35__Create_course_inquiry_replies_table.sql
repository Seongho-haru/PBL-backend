-- 리뷰/문의 답글 테이블 (통합)
CREATE TABLE course_review_replies (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    CONSTRAINT fk_review_replies_review FOREIGN KEY (review_id) REFERENCES course_reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_replies_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_review_replies_review_id ON course_review_replies(review_id);
CREATE INDEX idx_review_replies_author_id ON course_review_replies(author_id);
CREATE INDEX idx_review_replies_created_at ON course_review_replies(created_at ASC);

COMMENT ON TABLE course_review_replies IS '커리큘럼 리뷰 및 문의 답글 테이블';
COMMENT ON COLUMN course_review_replies.review_id IS '리뷰 또는 문의 ID (course_reviews.id)';
COMMENT ON COLUMN course_review_replies.author_id IS '답글 작성자 ID';

