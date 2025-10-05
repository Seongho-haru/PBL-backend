-- 커리큘럼 모듈 테이블 생성
-- V7__Create_curriculum_tables.sql

-- 커리큘럼 테이블 생성 (폴더 역할)
CREATE TABLE IF NOT EXISTS curriculums (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 커리큘럼-강의 연결 테이블 (폴더-파일 관계)
CREATE TABLE IF NOT EXISTS curriculum_lectures (
    id BIGSERIAL PRIMARY KEY,
    curriculum_id BIGINT NOT NULL,
    lecture_id BIGINT NOT NULL,
    order_index INTEGER NOT NULL DEFAULT 1,
    is_required BOOLEAN NOT NULL DEFAULT true,
    original_author VARCHAR(100),
    source_info VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_curriculum_lectures_curriculum 
        FOREIGN KEY (curriculum_id) REFERENCES curriculums(id) ON DELETE CASCADE,
    CONSTRAINT fk_curriculum_lectures_lecture 
        FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE,
    CONSTRAINT uk_curriculum_lecture_unique 
        UNIQUE (curriculum_id, lecture_id)
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_curriculums_is_public ON curriculums(is_public);
CREATE INDEX IF NOT EXISTS idx_curriculums_created_at ON curriculums(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_curriculum_lectures_curriculum_id ON curriculum_lectures(curriculum_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_lectures_lecture_id ON curriculum_lectures(lecture_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_lectures_order ON curriculum_lectures(curriculum_id, order_index);

-- 커리큘럼 테이블에 업데이트 트리거 적용
DROP TRIGGER IF EXISTS update_curriculums_updated_at ON curriculums;
CREATE TRIGGER update_curriculums_updated_at 
    BEFORE UPDATE ON curriculums 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 테이블 코멘트
COMMENT ON TABLE curriculums IS '커리큘럼 정보를 저장하는 테이블 (강의들의 폴더 역할)';
COMMENT ON TABLE curriculum_lectures IS '커리큘럼과 강의의 연결 관계를 저장하는 테이블';
COMMENT ON COLUMN curriculums.is_public IS '공개 커리큘럼 여부 (true: 공개, false: 비공개)';
COMMENT ON COLUMN curriculum_lectures.is_required IS '필수 강의 여부 (true: 필수, false: 선택)';
COMMENT ON COLUMN curriculum_lectures.original_author IS '원본 강의 작성자 (다른 사용자 강의 링크 시)';
COMMENT ON COLUMN curriculum_lectures.source_info IS '원본 강의 출처 정보';
