-- 강의 모듈 테이블 생성
-- V6__Create_lecture_tables.sql

-- 강의 테이블 생성
CREATE TABLE IF NOT EXISTS lectures (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    content TEXT,
    input_content TEXT,
    output_content TEXT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('MARKDOWN', 'PROBLEM')),
    category VARCHAR(100),
    difficulty VARCHAR(50),
    constraints_id BIGINT,
    is_public BOOLEAN NOT NULL DEFAULT false,
    thumbnail_image_url VARCHAR(500),
    duration_minutes INTEGER,
    language_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lectures_constraints
        FOREIGN KEY (constraints_id)
        REFERENCES submission_constraints(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_lectures_language
        FOREIGN KEY (language_id)
        REFERENCES languages(id)
        ON DELETE SET NULL
);

-- 테스트케이스 테이블 생성
CREATE TABLE IF NOT EXISTS test_cases (
    id BIGSERIAL PRIMARY KEY,
    lecture_id BIGINT NOT NULL,
    input TEXT,
    expected_output TEXT,
    order_index INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT fk_test_cases_lecture
        FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE
);

-- 강의 태그 테이블 생성
CREATE TABLE IF NOT EXISTS lecture_tags (
    lecture_id BIGINT NOT NULL,
    tag VARCHAR(50) NOT NULL,
    CONSTRAINT fk_lecture_tags_lecture
        FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_lectures_type ON lectures(type);
CREATE INDEX IF NOT EXISTS idx_lectures_category ON lectures(category);
CREATE INDEX IF NOT EXISTS idx_lectures_difficulty ON lectures(difficulty);
CREATE INDEX IF NOT EXISTS idx_lectures_is_public ON lectures(is_public);
CREATE INDEX IF NOT EXISTS idx_lectures_created_at ON lectures(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_lectures_constraints_id ON lectures(constraints_id);
CREATE INDEX IF NOT EXISTS idx_lectures_language_id ON lectures(language_id);
CREATE INDEX IF NOT EXISTS idx_test_cases_lecture_id ON test_cases(lecture_id);
CREATE INDEX IF NOT EXISTS idx_test_cases_order ON test_cases(lecture_id, order_index);
CREATE INDEX IF NOT EXISTS idx_lecture_tags_lecture_id ON lecture_tags(lecture_id);
CREATE INDEX IF NOT EXISTS idx_lecture_tags_tag ON lecture_tags(tag);

-- 업데이트 시간 자동 갱신을 위한 트리거 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 강의 테이블에 업데이트 트리거 적용
DROP TRIGGER IF EXISTS update_lectures_updated_at ON lectures;
CREATE TRIGGER update_lectures_updated_at 
    BEFORE UPDATE ON lectures 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 테이블 코멘트
COMMENT ON TABLE lectures IS '강의 정보를 저장하는 테이블 (마크다운 강의 + 문제 강의)';
COMMENT ON TABLE test_cases IS '문제 강의의 테스트케이스를 저장하는 테이블';
COMMENT ON TABLE lecture_tags IS '강의 태그를 저장하는 테이블';
COMMENT ON COLUMN lectures.type IS '강의 유형: MARKDOWN(마크다운 강의), PROBLEM(문제 강의)';
COMMENT ON COLUMN lectures.description IS '강의 간략 설명 (요약)';
COMMENT ON COLUMN lectures.content IS '강의 본문 내용 (전체) - MARKDOWN 타입에서 사용';
COMMENT ON COLUMN lectures.input_content IS '문제 입력 형식 설명 - PROBLEM 타입에서 사용';
COMMENT ON COLUMN lectures.output_content IS '문제 출력 형식 설명 - PROBLEM 타입에서 사용';
COMMENT ON COLUMN lectures.is_public IS '공개 강의 여부 (true: 공개, false: 비공개)';
COMMENT ON COLUMN lectures.thumbnail_image_url IS '강의 썸네일 이미지 URL (S3)';
COMMENT ON COLUMN lectures.duration_minutes IS '강의 예상 소요시간 (분 단위)';
COMMENT ON COLUMN lectures.language_id IS '프로그래밍 언어 ID (MARKDOWN 타입에서 사용) - languages 테이블 참조';
COMMENT ON COLUMN lectures.constraints_id IS '강의에 적용되는 실행 제약조건 (CPU 시간, 메모리 등)';

