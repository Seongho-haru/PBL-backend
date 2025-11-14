-- Python 커리큘럼 데이터 삽입
-- V101__sample_curriculums.sql

-- ============================================
-- Python 커리큘럼 데이터
-- ============================================

-- 커리큘럼 1: Python 기초
INSERT INTO curriculums (title, description, is_public, difficulty, summary, category, thumbnail_image_url, duration_minutes, author_id, average_rating, student_count, created_at, updated_at) VALUES (
    'Python 기초 완성',
    'Python 프로그래밍의 기초부터 차근차근 배웁니다. 변수, 자료형, 제어문, 함수 등 프로그래밍의 핵심 개념을 실습을 통해 학습합니다.',
    true,
    '기초',
    'Python 기초 문법부터 함수까지 단계별로 학습합니다.',
    'Python',
    'https://cdn.inflearn.com/public/files/courses/324427/77848686-1708-418b-999d-645d4f5b76f3/I_O_python_1.png?f=avif&w=960',
    360,
    (SELECT id FROM users WHERE login_id = 'admin'),
    4.8,
    1250,
    NOW(),
    NOW()
);

-- 커리큘럼 1 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), 'Python'),
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), '기초'),
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), '프로그래밍'),
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), '입문');

-- 커리큘럼 2: Python 중급
INSERT INTO curriculums (title, description, is_public, difficulty, summary, category, thumbnail_image_url, duration_minutes, author_id, average_rating, student_count, created_at, updated_at) VALUES (
    'Python 중급 마스터',
    'Python의 중급 기능을 학습합니다. 컴프리헨션, 람다, OOP, 예외 처리 등 실무에 필요한 핵심 개념을 익힙니다.',
    true,
    '중급',
    'Python 중급 문법과 객체지향 프로그래밍을 마스터합니다.',
    'Python',
    'https://cdn.inflearn.com/public/files/courses/324427/be31f0ce-ce50-418c-961b-cde3ae438f11/I_O_python_2.png?f=avif&w=960',
    375,
    (SELECT id FROM users WHERE login_id = 'admin'),
    4.9,
    980,
    NOW(),
    NOW()
);

-- 커리큘럼 2 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), 'Python'),
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), '중급'),
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), 'OOP'),
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), '함수형');

-- 커리큘럼 3: Python 고급
INSERT INTO curriculums (title, description, is_public, difficulty, summary, category, thumbnail_image_url, duration_minutes, author_id, average_rating, student_count, created_at, updated_at) VALUES (
    'Python 고급 전문가',
    'Python 표준 라이브러리와 고급 기능을 완벽하게 익힙니다. 45개 핵심 모듈과 데코레이터, 제너레이터, 동시성 프로그래밍까지 다룹니다.',
    true,
    '고급',
    'Python 표준 라이브러리 45개 모듈과 고급 기능을 마스터합니다.',
    'Python',
    'https://cdn.inflearn.com/public/files/courses/324145/1ad5c2e9-11d2-4005-a7b3-22a61ebf99f5/325780-eng-2.jpg?f=avif&w=960',
    1125,
    (SELECT id FROM users WHERE login_id = 'admin'),
    5.0,
    760,
    NOW(),
    NOW()
);

-- 커리큘럼 3 태그 추가
INSERT INTO curriculum_tags (curriculum_id, tag) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), 'Python'),
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), '고급'),
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), '표준라이브러리'),
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), '전문가');
