-- 사용자 일시 정지 관련 필드 추가
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_muted BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS muted_until TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS warning_count INTEGER DEFAULT 0;

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_users_is_muted ON users(is_muted);
CREATE INDEX IF NOT EXISTS idx_users_muted_until ON users(muted_until);

-- 코멘트
COMMENT ON COLUMN users.is_muted IS '일시 정지 여부';
COMMENT ON COLUMN users.muted_until IS '정지 해제 일시';
COMMENT ON COLUMN users.warning_count IS '경고 횟수';

