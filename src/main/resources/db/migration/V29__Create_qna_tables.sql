-- Q&A 질문 테이블 생성
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNRESOLVED',
    category VARCHAR(20) NOT NULL DEFAULT 'QUESTION',
    course VARCHAR(100),
    language VARCHAR(50),
    likes INTEGER NOT NULL DEFAULT 0,
    comment_count INTEGER NOT NULL DEFAULT 0,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_questions_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Q&A 답변 테이블 생성
CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    likes INTEGER NOT NULL DEFAULT 0,
    is_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    question_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    parent_answer_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_parent FOREIGN KEY (parent_answer_id) REFERENCES answers(id) ON DELETE CASCADE
);

-- 인덱스 생성
-- 질문 테이블 인덱스
CREATE INDEX idx_questions_status ON questions(status);
CREATE INDEX idx_questions_category ON questions(category);
CREATE INDEX idx_questions_course ON questions(course);
CREATE INDEX idx_questions_language ON questions(language);
CREATE INDEX idx_questions_author_id ON questions(author_id);
CREATE INDEX idx_questions_created_at ON questions(created_at);
CREATE INDEX idx_questions_likes ON questions(likes);
CREATE INDEX idx_questions_title ON questions USING gin(to_tsvector('english', title));
CREATE INDEX idx_questions_content ON questions USING gin(to_tsvector('english', content));

-- 답변 테이블 인덱스
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_answers_author_id ON answers(author_id);
CREATE INDEX idx_answers_parent_answer_id ON answers(parent_answer_id);
CREATE INDEX idx_answers_created_at ON answers(created_at);
CREATE INDEX idx_answers_likes ON answers(likes);
CREATE INDEX idx_answers_is_accepted ON answers(is_accepted);
CREATE INDEX idx_answers_content ON answers USING gin(to_tsvector('english', content));

-- 업데이트 시간 자동 갱신을 위한 트리거 함수
CREATE OR REPLACE FUNCTION update_questions_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_answers_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 생성
CREATE TRIGGER trigger_update_questions_updated_at
    BEFORE UPDATE ON questions
    FOR EACH ROW
    EXECUTE FUNCTION update_questions_updated_at();

CREATE TRIGGER trigger_update_answers_updated_at
    BEFORE UPDATE ON answers
    FOR EACH ROW
    EXECUTE FUNCTION update_answers_updated_at();

-- 코멘트 추가
COMMENT ON TABLE questions IS 'Q&A 질문 테이블';
COMMENT ON COLUMN questions.title IS '질문 제목';
COMMENT ON COLUMN questions.content IS '질문 내용';
COMMENT ON COLUMN questions.status IS '질문 상태 (UNRESOLVED, RESOLVED)';
COMMENT ON COLUMN questions.category IS '질문 카테고리 (QUESTION, TIP, BUG_REPORT, FEATURE_REQUEST, GENERAL)';
COMMENT ON COLUMN questions.course IS '강의명';
COMMENT ON COLUMN questions.language IS '프로그래밍 언어';
COMMENT ON COLUMN questions.likes IS '좋아요 수';
COMMENT ON COLUMN questions.comment_count IS '댓글 수';
COMMENT ON COLUMN questions.author_id IS '작성자 ID';

COMMENT ON TABLE answers IS 'Q&A 답변 테이블';
COMMENT ON COLUMN answers.content IS '답변 내용';
COMMENT ON COLUMN answers.likes IS '좋아요 수';
COMMENT ON COLUMN answers.is_accepted IS '채택 여부';
COMMENT ON COLUMN answers.question_id IS '질문 ID';
COMMENT ON COLUMN answers.author_id IS '작성자 ID';
COMMENT ON COLUMN answers.parent_answer_id IS '부모 답변 ID (답글인 경우)';

-- 샘플 데이터 삽입
INSERT INTO questions (title, content, status, category, course, language, author_id) VALUES
('Spring Boot 설정 관련 질문', 'Spring Boot에서 JPA 설정을 어떻게 해야 하나요? 데이터베이스 연결이 안됩니다.', 'UNRESOLVED', 'QUESTION', '자바스프링', 'Java', 1),
('React 컴포넌트 최적화 방법', 'React에서 리렌더링을 최적화하는 방법을 알고 싶습니다.', 'RESOLVED', 'TIP', 'React 기초', 'JavaScript', 1),
('데이터베이스 연결 오류', 'PostgreSQL 연결 시 "connection refused" 오류가 발생합니다.', 'UNRESOLVED', 'BUG_REPORT', '자바스프링', 'Java', 1),
('새로운 기능 제안', '코드 실행 결과를 저장할 수 있는 기능이 있으면 좋겠습니다.', 'UNRESOLVED', 'FEATURE_REQUEST', '자바스프링', 'Java', 1),
('Vue.js 라우터 설정', 'Vue Router를 사용한 페이지 이동이 제대로 작동하지 않습니다.', 'UNRESOLVED', 'QUESTION', 'Vue.js 기초', 'JavaScript', 1);

-- 샘플 답변 데이터 삽입
INSERT INTO answers (content, question_id, author_id) VALUES
('application.yml 파일에서 다음과 같이 설정하시면 됩니다:\n\nspring:\n  datasource:\n    url: jdbc:postgresql://localhost:5432/your_database\n    username: your_username\n    password: your_password', 1, 1),
('React.memo, useMemo, useCallback을 사용하여 최적화할 수 있습니다. 특히 자식 컴포넌트에 props가 자주 변경되지 않는 경우에 유용합니다.', 2, 1),
('PostgreSQL 서비스가 실행 중인지 확인해보세요. Windows의 경우 서비스 관리자에서 확인할 수 있습니다.', 3, 1),
('좋은 제안이네요! 코드 실행 결과를 히스토리로 저장하는 기능을 검토해보겠습니다.', 4, 1),
('Vue Router의 설정을 확인해보세요. router-link와 router-view가 올바르게 설정되어 있는지 확인해야 합니다.', 5, 1);

-- 답글 샘플 데이터
INSERT INTO answers (content, question_id, author_id, parent_answer_id) VALUES
('감사합니다! 설정을 적용해보겠습니다.', 1, 1, 1),
('추가로 캐싱도 고려해보시면 좋을 것 같습니다.', 2, 1, 2),
('서비스가 실행 중인데도 같은 오류가 발생합니다. 다른 원인이 있을까요?', 3, 1, 3);

-- 좋아요 수 업데이트
UPDATE questions SET likes = 5 WHERE id = 1;
UPDATE questions SET likes = 3 WHERE id = 2;
UPDATE questions SET likes = 2 WHERE id = 3;
UPDATE questions SET likes = 1 WHERE id = 4;
UPDATE questions SET likes = 4 WHERE id = 5;

UPDATE answers SET likes = 3 WHERE id = 1;
UPDATE answers SET likes = 2 WHERE id = 2;
UPDATE answers SET likes = 1 WHERE id = 3;
UPDATE answers SET likes = 1 WHERE id = 4;
UPDATE answers SET likes = 2 WHERE id = 5;

-- 댓글 수 업데이트
UPDATE questions SET comment_count = 2 WHERE id = 1;
UPDATE questions SET comment_count = 1 WHERE id = 2;
UPDATE questions SET comment_count = 1 WHERE id = 3;
UPDATE questions SET comment_count = 1 WHERE id = 4;
UPDATE questions SET comment_count = 1 WHERE id = 5;
