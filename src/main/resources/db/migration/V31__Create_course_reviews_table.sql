-- 리뷰/문의 통합 테이블
CREATE TABLE course_reviews (
    id BIGSERIAL PRIMARY KEY,
    curriculum_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    is_review BOOLEAN NOT NULL DEFAULT false, -- true: 리뷰, false: 문의
    rating DECIMAL(2,1), -- 리뷰만 사용, 0.0 ~ 5.0
    content TEXT,
    is_public BOOLEAN NOT NULL DEFAULT true, -- true: 공개, false: 비공개 (작성자와 관리자만)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    CONSTRAINT fk_course_reviews_curriculum FOREIGN KEY (curriculum_id) REFERENCES curriculums(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_reviews_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT check_rating_range CHECK (rating IS NULL OR (rating >= 0.0 AND rating <= 5.0))
);

-- 인덱스
CREATE INDEX idx_course_reviews_curriculum_id ON course_reviews(curriculum_id);
CREATE INDEX idx_course_reviews_author_id ON course_reviews(author_id);
CREATE INDEX idx_course_reviews_is_review ON course_reviews(is_review);
CREATE INDEX idx_course_reviews_is_public ON course_reviews(is_public);
CREATE INDEX idx_course_reviews_created_at ON course_reviews(created_at DESC);

-- 커리큘럼 평균 평점 자동 업데이트 트리거 함수
CREATE OR REPLACE FUNCTION update_curriculum_average_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE curriculums
    SET average_rating = (
        SELECT COALESCE(AVG(rating), 0)
        FROM course_reviews
        WHERE curriculum_id = NEW.curriculum_id
          AND is_review = true
          AND rating IS NOT NULL
    )
    WHERE id = NEW.curriculum_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 생성
CREATE TRIGGER trigger_update_curriculum_average_rating
AFTER INSERT OR UPDATE OR DELETE ON course_reviews
FOR EACH ROW
EXECUTE FUNCTION update_curriculum_average_rating();

COMMENT ON TABLE course_reviews IS '커리큘럼 리뷰 및 문의 통합 테이블';
COMMENT ON COLUMN course_reviews.is_review IS 'true: 리뷰 (별점 있음, 항상 공개), false: 문의 (별점 없음, 공개/비공개 선택)';
COMMENT ON COLUMN course_reviews.rating IS '별점 (0.0 ~ 5.0), 리뷰만 사용';
COMMENT ON COLUMN course_reviews.is_public IS '공개 여부, 문의만 사용 (리뷰는 항상 공개)';

