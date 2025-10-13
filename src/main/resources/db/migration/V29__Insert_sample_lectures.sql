-- 샘플 강의 데이터 삽입 및 커리큘럼 연결
-- V29__Insert_sample_lectures.sql

-- ============================================
-- 커리큘럼 1: Introduction To Algorithms
-- ============================================
INSERT INTO lectures (title, description, type, category, difficulty, is_public, author_id, time_limit, memory_limit, created_at, updated_at) VALUES
('알고리즘이란?', '알고리즘의 정의와 특징, 기본 예제를 통한 이해. 명확성, 유한성, 효율성 등의 알고리즘 핵심 특성을 학습합니다.', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NULL, NOW(), NOW()),
('시간복잡도', '알고리즘의 성능을 측정하는 시간복잡도 개념. Big O 표기법과 다양한 복잡도 클래스(O(1), O(n), O(n²) 등)를 비교 분석합니다.', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NULL, NOW(), NOW()),
('[문제] 배열에서 최댓값 찾기',
'주어진 정수 배열에서 최댓값을 찾는 함수를 작성하세요.

**입력 형식:**
- 첫 줄: 배열의 크기 N (1 ≤ N ≤ 100)
- 둘째 줄: N개의 정수 (-1000 ≤ 각 원소 ≤ 1000)

**출력 형식:**
- 배열의 최댓값을 출력

**제약 조건:**
- 시간 제한: 1초
- 메모리 제한: 128MB
- 시간복잡도 O(n)으로 해결', 'PROBLEM', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), 1, 128, NOW(), NOW()),
('Big O 표기법', 'Big O 표기법의 정의와 주요 복잡도 클래스별 코드 예제. 실제 알고리즘의 성능을 Big O로 표현하고 비교하는 방법을 학습합니다.', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NULL, NOW(), NOW()),
('버블 정렬', '가장 기본적인 정렬 알고리즘인 버블 정렬의 동작 원리와 구현. 시간복잡도 O(n²)의 한계와 최적화 기법을 다룹니다.', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NULL, NOW(), NOW()),
('[문제] 버블 정렬 구현하기',
'버블 정렬 알고리즘을 구현하세요.

**입력 형식:**
- 첫 줄: 배열의 크기 N (1 ≤ N ≤ 1000)
- 둘째 줄: N개의 정수 (-10000 ≤ 각 원소 ≤ 10000)

**출력 형식:**
- 오름차순으로 정렬된 배열을 공백으로 구분하여 출력

**제약 조건:**
- 시간 제한: 2초
- 메모리 제한: 128MB', 'PROBLEM', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), 2, 128, NOW(), NOW()),
('퀵 정렬', '분할 정복 방식의 효율적인 정렬 알고리즘. 평균 O(n log n)의 성능과 pivot 선택 전략을 실습을 통해 학습합니다.', 'MARKDOWN', '알고리즘', '중급', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NULL, NOW(), NOW()),
('[문제] 이진 탐색 구현',
'정렬된 배열에서 특정 값을 찾는 이진 탐색 알고리즘을 구현하세요.

**입력 형식:**
- 첫 줄: 배열의 크기 N (1 ≤ N ≤ 100000)
- 둘째 줄: N개의 오름차순 정렬된 정수 (1 ≤ 각 원소 ≤ 1000000)
- 셋째 줄: 찾을 값 target (1 ≤ target ≤ 1000000)

**출력 형식:**
- target이 있으면 인덱스(0-based), 없으면 -1 출력

**제약 조건:**
- 시간 제한: 1초
- 메모리 제한: 256MB
- 시간복잡도 O(log n)으로 해결', 'PROBLEM', '알고리즘', '중급', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), 1, 256, NOW(), NOW());

-- ============================================
-- 커리큘럼 2: 웹 기초 HTML/CSS
-- ============================================
INSERT INTO lectures (title, description, type, category, difficulty, is_public, author_id, time_limit, memory_limit, created_at, updated_at) VALUES
('HTML 기본 구조', 'HTML 문서의 기본 구조와 주요 태그. DOCTYPE, head, body 요소와 시맨틱 HTML의 중요성을 학습합니다.', 'MARKDOWN', '웹', '기초', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NULL, NOW(), NOW()),
('[문제] 프로필 카드 만들기',
'HTML로 간단한 프로필 카드를 만드세요.

**요구사항:**
- 이미지 태그 사용 (src는 placeholder 사용 가능)
- h1 태그로 이름 표시
- p 태그로 소개글 작성 (10자 이상 100자 이하)
- a 태그로 링크 버튼 추가

**제약 조건:**
- 시간 제한: 5초
- 메모리 제한: 64MB
- 유효한 HTML5 문법 사용', 'PROBLEM', '웹', '기초', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 5, 64, NOW(), NOW()),
('CSS 선택자', 'CSS 선택자의 종류와 우선순위. 클래스, ID, 속성 선택자 등을 활용한 스타일링 기법을 다룹니다.', 'MARKDOWN', '웹', '기초', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NULL, NOW(), NOW()),
('[문제] CSS로 버튼 스타일링',
'CSS를 사용하여 hover 효과가 있는 버튼을 만드세요.

**요구사항:**
- 버튼 배경색: #3498db (기본), #2980b9 (hover)
- 테두리: none
- border-radius: 5px 이상 20px 이하
- padding: 10px 이상 30px 이하
- box-shadow 효과 적용 (0-10px 범위)

**제약 조건:**
- 시간 제한: 5초
- 메모리 제한: 64MB', 'PROBLEM', '웹', '기초', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 5, 64, NOW(), NOW()),
('Flexbox', 'Flexbox 레이아웃 시스템의 개념과 활용. flex-direction, justify-content, align-items 등의 속성으로 유연한 레이아웃을 구현합니다.', 'MARKDOWN', '웹', '중급', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NULL, NOW(), NOW()),
('[문제] Flexbox 네비게이션 바',
'Flexbox를 사용하여 가로 방향의 네비게이션 바를 만드세요.

**요구사항:**
- display: flex 사용
- 로고: 왼쪽 정렬 (justify-content)
- 메뉴: 오른쪽 정렬
- 메뉴 아이템: 3개 이상 10개 이하
- align-items: center

**제약 조건:**
- 시간 제한: 5초
- 메모리 제한: 64MB', 'PROBLEM', '웹', '중급', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 5, 64, NOW(), NOW()),
('반응형 디자인', '미디어 쿼리를 활용한 반응형 웹 디자인. 다양한 디바이스 크기에 맞는 레이아웃 구현 기법을 학습합니다.', 'MARKDOWN', '웹', '중급', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NULL, NOW(), NOW()),
('[문제] 반응형 그리드 레이아웃',
'미디어 쿼리로 반응형 그리드를 만드세요.

**요구사항:**
- 모바일 (0-767px): grid-template-columns 1개
- 태블릿 (768px-1023px): grid-template-columns 2개
- 데스크톱 (1024px 이상): grid-template-columns 3개
- grid-gap: 10px 이상 30px 이하

**제약 조건:**
- 시간 제한: 5초
- 메모리 제한: 64MB', 'PROBLEM', '웹', '중급', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 5, 64, NOW(), NOW());

-- ============================================
-- 커리큘럼 3: Python 자료구조
-- ============================================
INSERT INTO lectures (title, description, type, category, difficulty, is_public, author_id, time_limit, memory_limit, created_at, updated_at) VALUES
('리스트와 튜플', '파이썬의 순서형 자료구조인 리스트와 튜플. 각각의 특징과 사용 시나리오, 주요 메서드를 실습합니다.', 'MARKDOWN', 'Python', '기초', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NULL, NULL, NOW(), NOW()),
('[문제] 리스트 중복 제거',
'정수 리스트에서 중복된 값을 제거하고 순서를 유지한 새 리스트를 반환하는 함수를 작성하세요.

**입력 형식:**
- 첫 줄: 리스트의 크기 N (1 ≤ N ≤ 1000)
- 둘째 줄: N개의 정수 (-1000 ≤ 각 원소 ≤ 1000)

**출력 형식:**
- 중복을 제거하고 순서를 유지한 리스트를 공백으로 구분하여 출력

**제약 조건:**
- 시간 제한: 1초
- 메모리 제한: 128MB', 'PROBLEM', 'Python', '기초', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), 1, 128, NOW(), NOW()),
('딕셔너리', '키-값 쌍으로 데이터를 저장하는 딕셔너리 자료구조. 효율적인 데이터 검색과 활용 패턴을 학습합니다.', 'MARKDOWN', 'Python', '기초', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NULL, NULL, NOW(), NOW()),
('[문제] 단어 빈도수 계산',
'문자열을 입력받아 각 단어의 빈도수를 딕셔너리로 반환하는 함수를 작성하세요.

**입력 형식:**
- 한 줄의 문자열 (1 ≤ 길이 ≤ 1000)
- 단어는 공백으로 구분
- 알파벳과 공백만 포함

**출력 형식:**
- 각 단어와 빈도수를 "단어:빈도수" 형식으로 출력
- 단어는 알파벳 순으로 정렬
- 대소문자 구분 없이 소문자로 통일

**제약 조건:**
- 시간 제한: 1초
- 메모리 제한: 128MB', 'PROBLEM', 'Python', '기초', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), 1, 128, NOW(), NOW()),
('스택 구현', 'LIFO 방식의 스택 자료구조를 파이썬으로 구현. 실제 사용 사례와 응용 문제를 통해 이해를 깊게 합니다.', 'MARKDOWN', 'Python', '중급', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NULL, NULL, NOW(), NOW()),
('[문제] 괄호 짝 맞추기',
'스택을 사용하여 문자열의 괄호가 올바르게 짝지어져 있는지 검사하는 함수를 작성하세요.

**입력 형식:**
- 한 줄의 문자열 (1 ≤ 길이 ≤ 10000)
- (), {}, [] 세 종류의 괄호만 포함

**출력 형식:**
- 올바르게 짝지어져 있으면 "True", 아니면 "False" 출력

**제약 조건:**
- 시간 제한: 1초
- 메모리 제한: 128MB
- 스택 자료구조 사용 필수', 'PROBLEM', 'Python', '중급', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), 1, 128, NOW(), NOW());

-- ============================================
-- 커리큘럼-강의 연결 (curriculum_lectures)
-- ============================================

-- 커리큘럼 1: Introduction To Algorithms 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '알고리즘이란?'), 1, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '시간복잡도'), 2, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), 3, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = 'Big O 표기법'), 4, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '버블 정렬'), 5, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), 6, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '퀵 정렬'), 7, false),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), 8, false);

-- 커리큘럼 2: 웹 기초 HTML/CSS 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = 'HTML 기본 구조'), 1, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = '[문제] 프로필 카드 만들기'), 2, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = 'CSS 선택자'), 3, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = '[문제] CSS로 버튼 스타일링'), 4, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = 'Flexbox'), 5, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = '[문제] Flexbox 네비게이션 바'), 6, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = '반응형 디자인'), 7, false),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = '[문제] 반응형 그리드 레이아웃'), 8, false);

-- 커리큘럼 3: Python 자료구조 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '리스트와 튜플'), 1, true),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), 2, true),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '딕셔너리'), 3, true),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 4, true),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '스택 구현'), 5, true),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), 6, true);

-- ============================================
-- 테스트 케이스 데이터
-- ============================================

-- [문제] 배열에서 최댓값 찾기 - 테스트 케이스
INSERT INTO test_cases (lecture_id, input, expected_output, order_index) VALUES
((SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), '1
5', '5', 1),  -- 경계값: 최소 크기 (N=1)
((SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), '5
1 2 3 4 5', '5', 2),  -- 중간값: 일반적인 경우
((SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), '10
91 23 57 84 12 45 78 36 69 100', '100', 3),  -- 중간값: 무작위 배열
((SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), '5
-1000 -500 0 500 1000', '1000', 4),  -- 경계값: 최대값이 마지막
((SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), '5
1000 -1000 0 100 -500', '1000', 5),  -- 경계값: 최대값이 처음
((SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), '5
-1000 -900 -800 -700 -600', '-600', 6),  -- 모두 음수
((SELECT id FROM lectures WHERE title = '[문제] 배열에서 최댓값 찾기'), '3
5 5 5', '5', 7);  -- 틀린값: 모든 원소가 같음

-- [문제] 버블 정렬 구현하기 - 테스트 케이스
INSERT INTO test_cases (lecture_id, input, expected_output, order_index) VALUES
((SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), '1
42', '42', 1),  -- 경계값: 최소 크기 (N=1)
((SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), '5
5 2 8 1 9', '1 2 5 8 9', 2),  -- 중간값: 일반적인 경우
((SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), '10
10 9 8 7 6 5 4 3 2 1', '1 2 3 4 5 6 7 8 9 10', 3),  -- 역순 정렬
((SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), '8
1 2 3 4 5 6 7 8', '1 2 3 4 5 6 7 8', 4),  -- 이미 정렬됨
((SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), '7
-10000 5000 -5000 0 10000 -1 1', '-10000 -5000 -1 0 1 5000 10000', 5),  -- 경계값: 음수와 양수 혼합
((SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), '6
3 3 1 1 2 2', '1 1 2 2 3 3', 6),  -- 중복값 존재
((SELECT id FROM lectures WHERE title = '[문제] 버블 정렬 구현하기'), '5
0 0 0 0 0', '0 0 0 0 0', 7);  -- 틀린값: 모든 원소가 같음

-- [문제] 이진 탐색 구현 - 테스트 케이스
INSERT INTO test_cases (lecture_id, input, expected_output, order_index) VALUES
((SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), '1
5
5', '0', 1),  -- 경계값: 최소 크기 (N=1), 찾음
((SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), '1
10
5', '-1', 2),  -- 경계값: 최소 크기 (N=1), 못찾음
((SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), '10
1 2 3 4 5 6 7 8 9 10
5', '4', 3),  -- 중간값: 중간에 있음
((SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), '10
1 2 3 4 5 6 7 8 9 10
1', '0', 4),  -- 경계값: 첫 번째
((SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), '10
1 2 3 4 5 6 7 8 9 10
10', '9', 5),  -- 경계값: 마지막
((SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), '10
1 2 3 4 5 6 7 8 9 10
11', '-1', 6),  -- 틀린값: 범위를 벗어남 (큰 값)
((SELECT id FROM lectures WHERE title = '[문제] 이진 탐색 구현'), '10
1 2 3 4 5 6 7 8 9 10
0', '-1', 7);  -- 틀린값: 범위를 벗어남 (작은 값)

-- [문제] 리스트 중복 제거 - 테스트 케이스
INSERT INTO test_cases (lecture_id, input, expected_output, order_index) VALUES
((SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), '1
42', '42', 1),  -- 경계값: 최소 크기 (N=1)
((SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), '5
1 2 3 4 5', '1 2 3 4 5', 2),  -- 중간값: 중복 없음
((SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), '8
1 2 2 3 3 3 4 5', '1 2 3 4 5', 3),  -- 중간값: 중복 있음
((SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), '5
5 5 5 5 5', '5', 4),  -- 틀린값: 모두 같은 값
((SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), '10
-1000 -1000 0 0 100 100 100 500 1000 1000', '-1000 0 100 500 1000', 5),  -- 경계값: 음수 양수 혼합
((SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), '7
1 1 2 3 2 4 3', '1 2 3 4', 6),  -- 순서 유지 확인
((SELECT id FROM lectures WHERE title = '[문제] 리스트 중복 제거'), '6
-5 -5 -3 -3 -1 -1', '-5 -3 -1', 7);  -- 모두 음수

-- [문제] 단어 빈도수 계산 - 테스트 케이스
INSERT INTO test_cases (lecture_id, input, expected_output, order_index) VALUES
((SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 'hello', 'hello:1', 1),  -- 경계값: 단어 1개
((SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 'hello world hello', 'hello:2
world:1', 2),  -- 중간값: 일반적인 경우
((SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 'Hello WORLD hello world', 'hello:2
world:2', 3),  -- 대소문자 혼합
((SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 'apple banana apple orange banana apple', 'apple:3
banana:2
orange:1', 4),  -- 중간값: 여러 단어
((SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 'a a a a a', 'a:5', 5),  -- 틀린값: 같은 단어 반복
((SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 'the quick brown fox jumps over the lazy dog', 'brown:1
dog:1
fox:1
jumps:1
lazy:1
over:1
quick:1
the:2', 6),  -- 긴 문장
((SELECT id FROM lectures WHERE title = '[문제] 단어 빈도수 계산'), 'zebra apple zebra banana', 'apple:1
banana:1
zebra:2', 7);  -- 알파벳 순 정렬 확인

-- [문제] 괄호 짝 맞추기 - 테스트 케이스
INSERT INTO test_cases (lecture_id, input, expected_output, order_index) VALUES
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '()', 'True', 1),  -- 경계값: 최소 케이스
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '()[]{}', 'True', 2),  -- 중간값: 모든 종류
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '{[()]}', 'True', 3),  -- 중간값: 중첩된 괄호
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '((()))', 'True', 4),  -- 같은 종류만
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '(]', 'False', 5),  -- 틀린값: 잘못된 짝
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '(()', 'False', 6),  -- 틀린값: 열린 괄호만
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '())', 'False', 7),  -- 틀린값: 닫힌 괄호 많음
((SELECT id FROM lectures WHERE title = '[문제] 괄호 짝 맞추기'), '{[(])}', 'False', 8);  -- 틀린값: 순서 틀림
