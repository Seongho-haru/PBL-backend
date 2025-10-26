-- 추천 모듈 테이블 생성 마이그레이션
-- V30__Create_recommendation_tables.sql

-- 1. 사용자 선호도 테이블
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    
    -- 선호 카테고리 (JSON 배열)
    preferred_categories TEXT[], 
    
    -- 선호 태그 (JSON 배열)
    preferred_tags TEXT[],
    
    -- 선호 난이도
    preferred_difficulty VARCHAR(20),
    
    -- 학습 선호도 (시간대 등)
    learning_style JSONB,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. 추천 로그 테이블
CREATE TABLE IF NOT EXISTS recommendation_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    curriculum_id BIGINT,
    lecture_id BIGINT,
    
    -- 추천 타입
    recommendation_type VARCHAR(50) NOT NULL,
    
    -- 추천 점수
    recommendation_score DECIMAL(5,2),
    
    -- 클릭 여부
    is_clicked BOOLEAN DEFAULT FALSE,
    clicked_at TIMESTAMP,
    
    -- 표시된 순서
    display_order INTEGER,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (curriculum_id) REFERENCES curriculums(id) ON DELETE SET NULL,
    FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE SET NULL
);

-- 3. 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id);
CREATE INDEX IF NOT EXISTS idx_user_preferences_categories ON user_preferences USING gin(preferred_categories);
CREATE INDEX IF NOT EXISTS idx_user_preferences_tags ON user_preferences USING gin(preferred_tags);

CREATE INDEX IF NOT EXISTS idx_recommendation_logs_user_id ON recommendation_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_recommendation_logs_created_at ON recommendation_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_recommendation_logs_type ON recommendation_logs(recommendation_type);
CREATE INDEX IF NOT EXISTS idx_recommendation_logs_curriculum_id ON recommendation_logs(curriculum_id);
CREATE INDEX IF NOT EXISTS idx_recommendation_logs_lecture_id ON recommendation_logs(lecture_id);

-- 4. 업데이트 시간 자동 갱신을 위한 트리거 함수
CREATE OR REPLACE FUNCTION update_user_preferences_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_user_preferences_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_user_preferences_updated_at();

-- 5. 코멘트 추가
COMMENT ON TABLE user_preferences IS '사용자 선호도 정보를 저장하는 테이블';
COMMENT ON COLUMN user_preferences.preferred_categories IS '사용자가 선호하는 카테고리 배열';
COMMENT ON COLUMN user_preferences.preferred_tags IS '사용자가 선호하는 태그 배열';
COMMENT ON COLUMN user_preferences.preferred_difficulty IS '사용자가 선호하는 난이도';
COMMENT ON COLUMN user_preferences.learning_style IS '학습 스타일 정보 (JSON)';

COMMENT ON TABLE recommendation_logs IS '추천 내역 및 클릭율을 추적하는 테이블';
COMMENT ON COLUMN recommendation_logs.recommendation_type IS '추천 타입 (PERSONALIZED, SIMILAR_PROBLEM, TRENDING 등)';
COMMENT ON COLUMN recommendation_logs.recommendation_score IS '추천 점수 (0.00 ~ 100.00)';
COMMENT ON COLUMN recommendation_logs.is_clicked IS '사용자가 추천된 항목을 클릭했는지 여부';
COMMENT ON COLUMN recommendation_logs.clicked_at IS '클릭한 시간';
COMMENT ON COLUMN recommendation_logs.display_order IS '표시된 순서';

