-- 샘플 커리큘럼 데이터 삽입
-- V101__sample_curriculums.sql

-- ============================================
-- 샘플 강사 사용자 추가 (커리큘럼 작성자)
-- ============================================
INSERT INTO users (username, login_id, password) VALUES
('테스트사용자1', 'testuser1', 'password123'),
('테스트사용자2', 'testuser2', 'password123'),
('개발자', 'developer', 'dev123'),
('김유희', 'kim.yuhee', 'password123'),
('이서준', 'lee.seojun', 'password123'),
('박가은', 'park.gaeun', 'password123'),
('최민수', 'choi.minsu', 'password123'),
('정은지', 'jung.eunji', 'password123'),
('오지후', 'oh.jihu', 'password123')
ON CONFLICT (login_id) DO NOTHING;

-- ============================================
-- 샘플 커리큘럼 데이터
-- ============================================

-- 커리큘럼 1: Introduction To Algorithms
INSERT INTO curriculums (title, description, is_public, difficulty, summary, thumbnail_image_url, duration_minutes, author_id, created_at, updated_at) VALUES (
    'Introduction To Algorithms',
    '알고리즘 기초 개념을 예제로 익히고 실습 문제로 다집니다. 정렬, 그래프, 동적계획법 등 핵심 알고리즘을 학습합니다.',
    true,
    '기초',
    '알고리즘의 기본 개념과 핵심 정렬 알고리즘을 학습합니다.',
    NULL,
    120,
    (SELECT id FROM users WHERE login_id = 'kim.yuhee'),
    '2025-09-01 00:00:00',
    '2025-09-01 00:00:00'
);

-- 커리큘럼 1 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), '알고리즘'),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), '기초'),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), '정렬');

-- 커리큘럼 2: 웹 기초 HTML/CSS
INSERT INTO curriculums (title, description, is_public, difficulty, summary, thumbnail_image_url, duration_minutes, author_id, created_at, updated_at) VALUES (
    '웹 기초: HTML/CSS',
    'HTML과 CSS로 반응형 레이아웃을 구현합니다. 레이아웃 설계와 반응형 디자인 기법을 학습합니다.',
    true,
    '기초',
    'HTML/CSS 기초부터 반응형 디자인까지 실습으로 배웁니다.',
    NULL,
    105,
    (SELECT id FROM users WHERE login_id = 'lee.seojun'),
    '2025-08-22 00:00:00',
    '2025-08-22 00:00:00'
);

-- 커리큘럼 2 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), '웹개발'),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), 'HTML'),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), 'CSS'),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), '반응형');

-- 커리큘럼 3: Python 자료구조
INSERT INTO curriculums (title, description, is_public, difficulty, summary, thumbnail_image_url, duration_minutes, author_id, created_at, updated_at) VALUES (
    'Python 자료구조',
    '파이썬 핵심 자료구조와 알고리즘 기본. 리스트, 딕셔너리, 세트 등의 자료구조를 실습을 통해 학습합니다.',
    true,
    '기초',
    '파이썬 핵심 자료구조를 실습으로 배웁니다.',
    NULL,
    90,
    (SELECT id FROM users WHERE login_id = 'park.gaeun'),
    '2025-07-05 00:00:00',
    '2025-07-05 00:00:00'
);

-- 커리큘럼 3 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), 'Python'),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), '자료구조'),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), '기초');

-- 커리큘럼 4: 게임 개발 입문 with Unity
INSERT INTO curriculums (title, description, is_public, difficulty, summary, thumbnail_image_url, duration_minutes, author_id, created_at, updated_at) VALUES (
    '게임 개발 입문 with Unity',
    'Unity로 간단한 게임을 만들며 핵심 개념을 배웁니다. 씬, 프리팹, 게임 오브젝트 등을 학습합니다.',
    true,
    '중급',
    'Unity 기초부터 간단한 게임 제작까지 단계별로 학습합니다.',
    NULL,
    150,
    (SELECT id FROM users WHERE login_id = 'choi.minsu'),
    '2025-05-18 00:00:00',
    '2025-05-18 00:00:00'
);

-- 커리큘럼 4 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = '게임 개발 입문 with Unity'), 'Unity'),
((SELECT id FROM curriculums WHERE title = '게임 개발 입문 with Unity'), '게임개발'),
((SELECT id FROM curriculums WHERE title = '게임 개발 입문 with Unity'), '중급');

-- 커리큘럼 5: SQL로 하는 데이터 질의
INSERT INTO curriculums (title, description, is_public, difficulty, summary, thumbnail_image_url, duration_minutes, author_id, created_at, updated_at) VALUES (
    'SQL로 하는 데이터 질의',
    '실무 예제로 익히는 SQL 쿼리 작성. JOIN, 인덱스 등 데이터베이스 핵심 개념을 학습합니다.',
    true,
    '중급',
    '실무 SQL 쿼리 작성 능력을 향상시킵니다.',
    NULL,
    100,
    (SELECT id FROM users WHERE login_id = 'jung.eunji'),
    '2025-04-10 00:00:00',
    '2025-04-10 00:00:00'
);

-- 커리큘럼 5 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = 'SQL로 하는 데이터 질의'), 'SQL'),
((SELECT id FROM curriculums WHERE title = 'SQL로 하는 데이터 질의'), '데이터베이스'),
((SELECT id FROM curriculums WHERE title = 'SQL로 하는 데이터 질의'), '중급');

-- 커리큘럼 6: 인공지능 개요
INSERT INTO curriculums (title, description, is_public, difficulty, summary, thumbnail_image_url, duration_minutes, author_id, created_at, updated_at) VALUES (
    '인공지능 개요',
    'AI 기본 개념과 사례 소개. 머신러닝과 딥러닝의 기초를 이해하고 실제 사례를 살펴봅니다.',
    true,
    '기초',
    'AI, 머신러닝, 딥러닝의 기본 개념을 이해합니다.',
    NULL,
    80,
    (SELECT id FROM users WHERE login_id = 'oh.jihu'),
    '2025-03-03 00:00:00',
    '2025-03-03 00:00:00'
);

-- 커리큘럼 6 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = '인공지능 개요'), 'AI'),
((SELECT id FROM curriculums WHERE title = '인공지능 개요'), '머신러닝'),
((SELECT id FROM curriculums WHERE title = '인공지능 개요'), '기초');
