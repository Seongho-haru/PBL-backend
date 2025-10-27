-- 신고 테이블 생성
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    content TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    processor_id BIGINT,
    process_action VARCHAR(50),
    process_note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    
    CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reports_processor FOREIGN KEY (processor_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 인덱스 생성
CREATE INDEX idx_reports_reporter_id ON reports(reporter_id);
CREATE INDEX idx_reports_target_type ON reports(target_type);
CREATE INDEX idx_reports_target_id ON reports(target_id);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_created_at ON reports(created_at DESC);
CREATE INDEX idx_reports_processed_at ON reports(processed_at DESC);

-- 복합 인덱스 (중복 신고 체크용)
CREATE INDEX idx_reports_reporter_target ON reports(reporter_id, target_type, target_id);

COMMENT ON TABLE reports IS '신고 통합 테이블';
COMMENT ON COLUMN reports.target_type IS '신고 대상 타입 (CURRICULUM, LECTURE, QUESTION, ANSWER, COURSE_REVIEW)';
COMMENT ON COLUMN reports.reason IS '신고 사유 (SPAM, ABUSE, INAPPROPRIATE_CONTENT, COPYRIGHT_VIOLATION, ETC)';
COMMENT ON COLUMN reports.status IS '신고 상태 (PENDING, PROCESSING, RESOLVED, REJECTED)';
COMMENT ON COLUMN reports.process_action IS '처리 방법 (DELETE_CONTENT, MODIFY_REQUEST, WARNING, MUTE_USER, DELETE_ACCOUNT, NO_ACTION, OTHER)';

