-- 샘플 커리큘럼 데이터 삽입
-- V101__sample_curriculums.sql

-- ============================================
-- 샘플 강사 사용자 추가 (커리큘럼 작성자)
-- ============================================
INSERT INTO users (username, login_id, password) VALUES
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
INSERT INTO curriculums (title, description, is_public, author_id, created_at, updated_at) VALUES (
    'Introduction To Algorithms',
    '알고리즘 기초 개념을 예제로 익히고 실습 문제로 다집니다. 정렬, 그래프, 동적계획법 등 핵심 알고리즘을 학습합니다.',
    true,
    (SELECT id FROM users WHERE login_id = 'kim.yuhee'),
    '2025-09-01 00:00:00',
    '2025-09-01 00:00:00'
);

-- 커리큘럼 2: 웹 기초 HTML/CSS
INSERT INTO curriculums (title, description, is_public, author_id, created_at, updated_at) VALUES (
    '웹 기초: HTML/CSS',
    'HTML과 CSS로 반응형 레이아웃을 구현합니다. 레이아웃 설계와 반응형 디자인 기법을 학습합니다.',
    true,
    (SELECT id FROM users WHERE login_id = 'lee.seojun'),
    '2025-08-22 00:00:00',
    '2025-08-22 00:00:00'
);

-- 커리큘럼 3: Python 자료구조
INSERT INTO curriculums (title, description, is_public, author_id, created_at, updated_at) VALUES (
    'Python 자료구조',
    '파이썬 핵심 자료구조와 알고리즘 기본. 리스트, 딕셔너리, 세트 등의 자료구조를 실습을 통해 학습합니다.',
    true,
    (SELECT id FROM users WHERE login_id = 'park.gaeun'),
    '2025-07-05 00:00:00',
    '2025-07-05 00:00:00'
);

-- 커리큘럼 4: 게임 개발 입문 with Unity
INSERT INTO curriculums (title, description, is_public, author_id, created_at, updated_at) VALUES (
    '게임 개발 입문 with Unity',
    'Unity로 간단한 게임을 만들며 핵심 개념을 배웁니다. 씬, 프리팹, 게임 오브젝트 등을 학습합니다.',
    true,
    (SELECT id FROM users WHERE login_id = 'choi.minsu'),
    '2025-05-18 00:00:00',
    '2025-05-18 00:00:00'
);

-- 커리큘럼 5: SQL로 하는 데이터 질의
INSERT INTO curriculums (title, description, is_public, author_id, created_at, updated_at) VALUES (
    'SQL로 하는 데이터 질의',
    '실무 예제로 익히는 SQL 쿼리 작성. JOIN, 인덱스 등 데이터베이스 핵심 개념을 학습합니다.',
    true,
    (SELECT id FROM users WHERE login_id = 'jung.eunji'),
    '2025-04-10 00:00:00',
    '2025-04-10 00:00:00'
);

-- 커리큘럼 6: 인공지능 개요
INSERT INTO curriculums (title, description, is_public, author_id, created_at, updated_at) VALUES (
    '인공지능 개요',
    'AI 기본 개념과 사례 소개. 머신러닝과 딥러닝의 기초를 이해하고 실제 사례를 살펴봅니다.',
    true,
    (SELECT id FROM users WHERE login_id = 'oh.jihu'),
    '2025-03-03 00:00:00',
    '2025-03-03 00:00:00'
);
