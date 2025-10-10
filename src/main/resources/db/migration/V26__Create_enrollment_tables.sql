-- 수강 관리 테이블 생성 마이그레이션
-- V26__Create_enrollment_tables.sql

-- 1. 수강 테이블 생성
CREATE TABLE IF NOT EXISTS enrollments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    curriculum_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',
    enrolled_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    progress_percentage INTEGER NOT NULL DEFAULT 0,
    
    -- 외래키 제약조건
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (curriculum_id) REFERENCES curriculums(id) ON DELETE CASCADE,
    
    -- 유니크 제약조건 (사용자-커리큘럼 중복 방지)
    UNIQUE(user_id, curriculum_id)
);

-- 2. 강의 진도 테이블 생성
CREATE TABLE IF NOT EXISTS lecture_progress (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL,
    lecture_id BIGINT NOT NULL,
    curriculum_lecture_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE,
    FOREIGN KEY (curriculum_lecture_id) REFERENCES curriculum_lectures(id) ON DELETE CASCADE,
    
    -- 유니크 제약조건 (수강-강의 중복 방지)
    UNIQUE(enrollment_id, lecture_id)
);

-- 3. 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_curriculum_id ON enrollments(curriculum_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_status ON enrollments(status);
CREATE INDEX IF NOT EXISTS idx_enrollments_enrolled_at ON enrollments(enrolled_at);

CREATE INDEX IF NOT EXISTS idx_lecture_progress_enrollment_id ON lecture_progress(enrollment_id);
CREATE INDEX IF NOT EXISTS idx_lecture_progress_lecture_id ON lecture_progress(lecture_id);
CREATE INDEX IF NOT EXISTS idx_lecture_progress_status ON lecture_progress(status);

-- 4. 코멘트 추가
COMMENT ON TABLE enrollments IS '수강 정보 테이블';
COMMENT ON COLUMN enrollments.id IS '수강 고유 ID';
COMMENT ON COLUMN enrollments.user_id IS '수강자 사용자 ID';
COMMENT ON COLUMN enrollments.curriculum_id IS '수강 커리큘럼 ID';
COMMENT ON COLUMN enrollments.status IS '수강 상태 (ENROLLED, COMPLETED)';
COMMENT ON COLUMN enrollments.enrolled_at IS '수강 신청 시간';
COMMENT ON COLUMN enrollments.completed_at IS '수강 완료 시간';
COMMENT ON COLUMN enrollments.progress_percentage IS '진도율 (0-100)';

COMMENT ON TABLE lecture_progress IS '강의별 진도 테이블';
COMMENT ON COLUMN lecture_progress.id IS '진도 고유 ID';
COMMENT ON COLUMN lecture_progress.enrollment_id IS '수강 정보 ID';
COMMENT ON COLUMN lecture_progress.lecture_id IS '강의 ID';
COMMENT ON COLUMN lecture_progress.curriculum_lecture_id IS '커리큘럼-강의 연결 ID';
COMMENT ON COLUMN lecture_progress.status IS '진도 상태 (NOT_STARTED, IN_PROGRESS, COMPLETED)';
COMMENT ON COLUMN lecture_progress.started_at IS '강의 시작 시간';
COMMENT ON COLUMN lecture_progress.completed_at IS '강의 완료 시간';
