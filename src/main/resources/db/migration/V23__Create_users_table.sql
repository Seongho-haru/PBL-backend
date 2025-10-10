-- 사용자 테이블 생성
-- V23__Create_users_table.sql

-- 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    login_id VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_users_login_id ON users(login_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at DESC);

-- 업데이트 시간 자동 갱신을 위한 트리거 적용
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 테이블 코멘트
COMMENT ON TABLE users IS '사용자 계정 정보를 저장하는 테이블';
COMMENT ON COLUMN users.username IS '사용자명 (표시용)';
COMMENT ON COLUMN users.login_id IS '로그인 아이디 (중복 불가)';
COMMENT ON COLUMN users.password IS '비밀번호 (해시화되어 저장)';
COMMENT ON COLUMN users.created_at IS '계정 생성 시간';
COMMENT ON COLUMN users.updated_at IS '계정 수정 시간';

-- 샘플 사용자 데이터 추가 (개발/테스트용)
INSERT INTO users (username, login_id, password) VALUES
('관리자', 'admin', 'admin123'),
('테스트사용자1', 'testuser1', 'password123'),
('테스트사용자2', 'testuser2', 'password123'),
('개발자', 'developer', 'dev123');
