-- 커리큘럼 테이블에 메타데이터 필드 추가
-- V28__Add_curriculum_metadata_fields.sql

-- 난이도 필드 추가
ALTER TABLE curriculums 
ADD COLUMN difficulty VARCHAR(20);

-- 간단 소개 필드 추가
ALTER TABLE curriculums 
ADD COLUMN summary VARCHAR(500);

-- 평균 별점 필드 추가 (0.0 ~ 5.0)
ALTER TABLE curriculums 
ADD COLUMN average_rating DECIMAL(3,2) DEFAULT 0.00;

-- 수강생 수 필드 추가
ALTER TABLE curriculums
ADD COLUMN student_count INTEGER DEFAULT 0;

-- 썸네일 이미지 URL 필드 추가
ALTER TABLE curriculums
ADD COLUMN thumbnail_image_url VARCHAR(500);

-- 소요 시간 필드 추가 (분 단위)
ALTER TABLE curriculums
ADD COLUMN duration_minutes INTEGER;

-- 커리큘럼 태그 테이블 생성
CREATE TABLE IF NOT EXISTS curriculum_tags (
    curriculum_id BIGINT NOT NULL,
    tag VARCHAR(50) NOT NULL,
    CONSTRAINT fk_curriculum_tags_curriculum
        FOREIGN KEY (curriculum_id) REFERENCES curriculums(id) ON DELETE CASCADE
);

-- 인덱스 추가 (검색 성능 향상)
CREATE INDEX idx_curriculums_difficulty ON curriculums(difficulty);
CREATE INDEX idx_curriculums_average_rating ON curriculums(average_rating);
CREATE INDEX idx_curriculums_student_count ON curriculums(student_count);
CREATE INDEX idx_curriculum_tags_curriculum_id ON curriculum_tags(curriculum_id);
CREATE INDEX idx_curriculum_tags_tag ON curriculum_tags(tag);

-- 기존 데이터에 기본값 설정
UPDATE curriculums 
SET 
    difficulty = '기초',
    summary = SUBSTRING(description, 1, 100),
    average_rating = 0.00,
    student_count = 0
WHERE difficulty IS NULL;

-- 코멘트 추가
COMMENT ON COLUMN curriculums.difficulty IS '커리큘럼 난이도 (기초, 중급, 고급, 전문가)';
COMMENT ON COLUMN curriculums.summary IS '커리큘럼 간단 소개 (목록에서 보여줄 짧은 설명)';
COMMENT ON COLUMN curriculums.average_rating IS '평균 별점 (0.0 ~ 5.0)';
COMMENT ON COLUMN curriculums.student_count IS '현재 수강 중인 학생 수';
COMMENT ON COLUMN curriculums.thumbnail_image_url IS '커리큘럼 썸네일 이미지 URL';
COMMENT ON COLUMN curriculums.duration_minutes IS '커리큘럼 전체 소요 시간 (분 단위)';
COMMENT ON TABLE curriculum_tags IS '커리큘럼 태그 정보를 저장하는 테이블';
