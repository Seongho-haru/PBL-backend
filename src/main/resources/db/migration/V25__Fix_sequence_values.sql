-- V25__Fix_sequence_values.sql
-- 시퀀스 값을 현재 테이블의 최대 ID로 업데이트

-- lectures 테이블 시퀀스 수정
SELECT setval('lectures_id_seq', COALESCE((SELECT MAX(id) FROM lectures), 1), true);

-- curriculums 테이블 시퀀스 수정  
SELECT setval('curriculums_id_seq', COALESCE((SELECT MAX(id) FROM curriculums), 1), true);

-- curriculum_lectures 테이블 시퀀스 수정
SELECT setval('curriculum_lectures_id_seq', COALESCE((SELECT MAX(id) FROM curriculum_lectures), 1), true);

-- users 테이블 시퀀스 수정
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1), true);

-- test_cases 테이블 시퀀스 수정
SELECT setval('test_cases_id_seq', COALESCE((SELECT MAX(id) FROM test_cases), 1), true);
