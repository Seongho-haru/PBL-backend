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
ADD COLUMN average_rating DECIMAL(3,2) DEFAULT 0.0;

-- 수강생 수 필드 추가
ALTER TABLE curriculums 
ADD COLUMN student_count INTEGER DEFAULT 0;

-- 인덱스 추가 (검색 성능 향상)
CREATE INDEX idx_curriculums_difficulty ON curriculums(difficulty);
CREATE INDEX idx_curriculums_average_rating ON curriculums(average_rating);
CREATE INDEX idx_curriculums_student_count ON curriculums(student_count);

-- 기존 데이터에 기본값 설정
UPDATE curriculums 
SET 
    difficulty = '기초',
    summary = SUBSTRING(description, 1, 100),
    average_rating = 0.0,
    student_count = 0
WHERE difficulty IS NULL;

-- 코멘트 추가
COMMENT ON COLUMN curriculums.difficulty IS '커리큘럼 난이도 (기초, 중급, 고급, 전문가)';
COMMENT ON COLUMN curriculums.summary IS '커리큘럼 간단 소개 (목록에서 보여줄 짧은 설명)';
COMMENT ON COLUMN curriculums.average_rating IS '평균 별점 (0.0 ~ 5.0)';
COMMENT ON COLUMN curriculums.student_count IS '현재 수강 중인 학생 수';
