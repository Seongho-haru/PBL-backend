-- V29__Create_curriculum_lectures_table.sql
-- 커리큘럼-강의 연결 테이블 생성

CREATE TABLE IF NOT EXISTS curriculum_lectures (
    id BIGSERIAL PRIMARY KEY,
    curriculum_id BIGINT NOT NULL,
    lecture_id BIGINT NOT NULL,
    order_index INTEGER NOT NULL DEFAULT 1,
    is_required BOOLEAN NOT NULL DEFAULT true,
    original_author VARCHAR(100),
    source_info VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    CONSTRAINT fk_curriculum_lectures_curriculum 
        FOREIGN KEY (curriculum_id) REFERENCES curriculums(id) ON DELETE CASCADE,
    CONSTRAINT fk_curriculum_lectures_lecture 
        FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_curriculum_lectures_curriculum_id 
    ON curriculum_lectures (curriculum_id);
    
CREATE INDEX IF NOT EXISTS idx_curriculum_lectures_lecture_id 
    ON curriculum_lectures (lecture_id);
    
CREATE INDEX IF NOT EXISTS idx_curriculum_lectures_order 
    ON curriculum_lectures (curriculum_id, order_index);

-- 유니크 제약조건 (같은 커리큘럼에 같은 강의 중복 방지)
CREATE UNIQUE INDEX IF NOT EXISTS idx_curriculum_lectures_unique 
    ON curriculum_lectures (curriculum_id, lecture_id);
