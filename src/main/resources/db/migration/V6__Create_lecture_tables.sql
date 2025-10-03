-- 강의 모듈 테이블 생성
-- V6__Create_lecture_tables.sql

-- 강의 테이블이 이미 존재하는지 확인하고 생성
CREATE TABLE IF NOT EXISTS lectures (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('MARKDOWN', 'PROBLEM')),
    category VARCHAR(100),
    difficulty VARCHAR(50),
    time_limit INTEGER,
    memory_limit INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 테스트케이스 테이블이 이미 존재하는지 확인하고 생성
CREATE TABLE IF NOT EXISTS test_cases (
    id BIGSERIAL PRIMARY KEY,
    lecture_id BIGINT NOT NULL,
    input TEXT,
    expected_output TEXT,
    order_index INTEGER NOT NULL DEFAULT 1
);

-- 외래키 제약조건 추가 (이미 존재하지 않는 경우에만)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_test_cases_lecture' 
        AND table_name = 'test_cases'
    ) THEN
        ALTER TABLE test_cases 
        ADD CONSTRAINT fk_test_cases_lecture 
        FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE;
    END IF;
END $$;

-- 인덱스 생성 (이미 존재하지 않는 경우에만)
CREATE INDEX IF NOT EXISTS idx_lectures_type ON lectures(type);
CREATE INDEX IF NOT EXISTS idx_lectures_category ON lectures(category);
CREATE INDEX IF NOT EXISTS idx_lectures_difficulty ON lectures(difficulty);
CREATE INDEX IF NOT EXISTS idx_lectures_created_at ON lectures(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_test_cases_lecture_id ON test_cases(lecture_id);
CREATE INDEX IF NOT EXISTS idx_test_cases_order ON test_cases(lecture_id, order_index);

-- 업데이트 시간 자동 갱신을 위한 트리거 함수 (이미 존재하지 않는 경우에만)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 강의 테이블에 업데이트 트리거 적용 (이미 존재하지 않는 경우에만)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.triggers 
        WHERE trigger_name = 'update_lectures_updated_at'
    ) THEN
        CREATE TRIGGER update_lectures_updated_at 
            BEFORE UPDATE ON lectures 
            FOR EACH ROW 
            EXECUTE FUNCTION update_updated_at_column();
    END IF;
END $$;

-- 샘플 데이터 삽입 (중복 방지)
INSERT INTO lectures (title, description, type, category, difficulty, time_limit, memory_limit) 
SELECT * FROM (VALUES
    ('Hello World 문제', '가장 기본적인 출력 문제입니다.', 'PROBLEM', '기초', '쉬움', 1, 128),
    ('두 수의 합', '두 정수를 입력받아 합을 출력하는 문제입니다.', 'PROBLEM', '기초', '쉬움', 1, 128),
    ('Java 기초 강의', '# Java 프로그래밍 기초\n\n## 변수와 자료형\n\n자바의 기본 자료형에 대해 알아봅시다.', 'MARKDOWN', 'Java', '기초', NULL, NULL),
    ('알고리즘 입문', '# 알고리즘의 기초\n\n## 시간복잡도와 공간복잡도\n\n효율적인 알고리즘 작성법을 배워봅시다.', 'MARKDOWN', '알고리즘', '중급', NULL, NULL)
) AS v(title, description, type, category, difficulty, time_limit, memory_limit)
WHERE NOT EXISTS (SELECT 1 FROM lectures WHERE title = v.title);

-- Hello World 문제의 테스트케이스 (중복 방지)
INSERT INTO test_cases (lecture_id, input, expected_output, order_index)
SELECT l.id, '', 'Hello, World!', 1
FROM lectures l
WHERE l.title = 'Hello World 문제'
AND NOT EXISTS (
    SELECT 1 FROM test_cases tc 
    WHERE tc.lecture_id = l.id AND tc.expected_output = 'Hello, World!'
);

-- 두 수의 합 문제의 테스트케이스 (중복 방지)
INSERT INTO test_cases (lecture_id, input, expected_output, order_index)
SELECT l.id, v.input, v.expected_output, v.order_index
FROM lectures l,
(VALUES 
    ('1 2', '3', 1),
    ('10 20', '30', 2),
    ('-5 5', '0', 3)
) AS v(input, expected_output, order_index)
WHERE l.title = '두 수의 합'
AND NOT EXISTS (
    SELECT 1 FROM test_cases tc 
    WHERE tc.lecture_id = l.id AND tc.input = v.input
);

-- 코멘트
COMMENT ON TABLE lectures IS '강의 정보를 저장하는 테이블 (마크다운 강의 + 문제 강의)';
COMMENT ON TABLE test_cases IS '문제 강의의 테스트케이스를 저장하는 테이블';
COMMENT ON COLUMN lectures.type IS '강의 유형: MARKDOWN(마크다운 강의), PROBLEM(문제 강의)';
COMMENT ON COLUMN lectures.time_limit IS '문제 강의의 시간 제한 (초 단위)';
COMMENT ON COLUMN lectures.memory_limit IS '문제 강의의 메모리 제한 (MB 단위)';


-- Add some sample test cases for testing
INSERT INTO test_cases (lecture_id, input, expected_output) VALUES
(1, '5\n1 2 3 4 5', '15'),
(1, '3\n10 20 30', '60'),
(1, '0', '0'),
(2, '2\n5 10', '15'),
(2, '1\n100', '100'),
-- A+B 문제 테스트케이스 (problem_id: 3)
(3, '1 2', '3'),
(3, '5 7', '12'),
(3, '10 20', '30'),
(3, '100 200', '300'),
(3, '0 0', '0'),
(3, '-5 3', '-2'),
(3, '999 1', '1000'),
(3, '1 2', '3'),
(3, '5 8', '13'),
(3, '10 20', '30')
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2')
;