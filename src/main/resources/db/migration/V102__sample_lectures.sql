-- 샘플 강의 데이터 삽입 및 커리큘럼 연결
-- V102__sample_lectures.sql

-- ============================================
-- 커리큘럼 1: Introduction To Algorithms
-- ============================================
INSERT INTO lectures (title, description, content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('알고리즘이란?', '문제를 해결하기 위한 명확한 절차와 방법을 배웁니다.', '# 알고리즘이란?

알고리즘은 문제를 해결하기 위한 명확한 절차와 방법입니다.

## 알고리즘의 정의
알고리즘(Algorithm)은 특정 문제를 해결하기 위한 단계적이고 체계적인 절차입니다. 컴퓨터 과학뿐만 아니라 일상생활에서도 많이 사용되는 개념입니다.

## 알고리즘의 특성
1. **명확성(Definiteness)**: 각 단계가 명확하게 정의되어야 하며, 모호함이 없어야 합니다.
2. **유한성(Finiteness)**: 유한한 단계 내에 종료되어야 하며, 무한 루프에 빠지면 안 됩니다.
3. **효율성(Effectiveness)**: 시간과 공간을 효율적으로 사용해야 합니다.
4. **입력(Input)**: 0개 이상의 입력을 가질 수 있습니다.
5. **출력(Output)**: 1개 이상의 출력을 생성해야 합니다.

## 기본 예제
일상생활의 알고리즘 예제: 라면 끓이기
1. 냄비에 물 550ml를 넣고 끓인다
2. 물이 끓으면 면과 스프를 넣는다
3. 4분 30초간 끓인다
4. 완성!', 'MARKDOWN', '알고리즘', '기초', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NOW(), NOW()),
('시간복잡도', '알고리즘의 성능을 측정하는 시간복잡도를 배웁니다.', '# 시간복잡도

알고리즘의 성능을 측정하는 중요한 지표인 시간복잡도에 대해 알아봅니다.

## 시간복잡도란?
시간복잡도는 알고리즘이 문제를 해결하는 데 걸리는 시간을 입력 크기의 함수로 나타낸 것입니다.

## Big O 표기법
- **O(1)**: 상수 시간 - 입력 크기와 무관하게 일정한 시간
- **O(log n)**: 로그 시간 - 이진 탐색 등
- **O(n)**: 선형 시간 - 배열 순회 등
- **O(n log n)**: 선형 로그 시간 - 효율적인 정렬 알고리즘
- **O(n²)**: 이차 시간 - 중첩 반복문
- **O(2ⁿ)**: 지수 시간 - 피보나치 재귀 등

## 예제
배열에서 최댓값 찾기는 O(n)입니다. 모든 원소를 한 번씩 확인해야 하기 때문입니다.', 'MARKDOWN', '알고리즘', '기초', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NOW(), NOW()),
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
- 시간복잡도 O(n)으로 해결', NULL, 'PROBLEM', '알고리즘', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), 1, NOW(), NOW()),
('Big O 표기법', '알고리즘 성능을 표현하는 Big O 표기법을 배웁니다.', '# Big O 표기법

실제 코드 예제를 통해 Big O 표기법을 이해합니다.

## Big O 표기법이란?
알고리즘의 시간 복잡도를 수학적으로 표현하는 방법으로, 입력 크기 n에 대한 연산 횟수의 상한을 나타냅니다.

## 주요 복잡도 클래스

### O(1) - 상수 시간
```python
def get_first_element(arr):
    return arr[0]  # 항상 1번의 연산
```

### O(n) - 선형 시간
```python
def find_max(arr):
    max_val = arr[0]
    for num in arr:  # n번 반복
        if num > max_val:
            max_val = num
    return max_val
```

### O(n²) - 이차 시간
```python
def bubble_sort(arr):
    for i in range(len(arr)):      # n번
        for j in range(len(arr)-1): # n번
            if arr[j] > arr[j+1]:
                arr[j], arr[j+1] = arr[j+1], arr[j]
```

## 성능 비교
입력 크기가 1000일 때:
- O(1): 1번 연산
- O(log n): 약 10번 연산
- O(n): 1,000번 연산
- O(n log n): 약 10,000번 연산
- O(n²): 1,000,000번 연산', 'MARKDOWN', '알고리즘', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NOW(), NOW()),
('버블 정렬', '가장 기본적인 정렬 알고리즘인 버블 정렬을 배웁니다.', '# 버블 정렬 (Bubble Sort)

가장 단순하지만 비효율적인 정렬 알고리즘입니다.

## 동작 원리
인접한 두 원소를 비교하여 큰 값을 뒤로 보내는 과정을 반복합니다. 마치 거품이 위로 올라가는 것처럼 큰 값이 뒤로 이동합니다.

## 알고리즘 단계
1. 배열의 첫 원소부터 인접한 원소끼리 비교
2. 앞의 원소가 뒤의 원소보다 크면 두 원소를 교환
3. 배열의 끝까지 반복
4. 1~3 과정을 배열 크기만큼 반복

## 파이썬 구현
```python
def bubble_sort(arr):
    n = len(arr)
    for i in range(n):
        for j in range(n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
    return arr
```

## 시간복잡도
- 최선: O(n) - 이미 정렬된 경우 (최적화 시)
- 평균: O(n²)
- 최악: O(n²)

## 최적화 기법
교환이 한 번도 일어나지 않으면 이미 정렬된 것이므로 조기 종료할 수 있습니다.', 'MARKDOWN', '알고리즘', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NOW(), NOW()),
('[문제] 버블 정렬 구현하기',
'버블 정렬 알고리즘을 구현하세요.

**입력 형식:**
- 첫 줄: 배열의 크기 N (1 ≤ N ≤ 1000)
- 둘째 줄: N개의 정수 (-10000 ≤ 각 원소 ≤ 10000)

**출력 형식:**
- 오름차순으로 정렬된 배열을 공백으로 구분하여 출력

**제약 조건:**
- 시간 제한: 2초
- 메모리 제한: 128MB', NULL, 'PROBLEM', '알고리즘', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), 1, NOW(), NOW()),
('퀵 정렬', '분할 정복 방식의 효율적인 정렬 알고리즘을 배웁니다.', '# 퀵 정렬 (Quick Sort)

평균적으로 가장 빠른 정렬 알고리즘 중 하나입니다.

## 동작 원리
분할 정복(Divide and Conquer) 방식을 사용합니다:
1. 피벗(pivot)을 선택
2. 피벗보다 작은 값은 왼쪽, 큰 값은 오른쪽으로 분할
3. 분할된 부분 배열에 대해 재귀적으로 퀵 정렬 수행

## 파이썬 구현
```python
def quick_sort(arr):
    if len(arr) <= 1:
        return arr

    pivot = arr[len(arr) // 2]
    left = [x for x in arr if x < pivot]
    middle = [x for x in arr if x == pivot]
    right = [x for x in arr if x > pivot]

    return quick_sort(left) + middle + quick_sort(right)
```

## 시간복잡도
- 최선: O(n log n)
- 평균: O(n log n)
- 최악: O(n²) - 이미 정렬된 배열에 첫 원소를 피벗으로 선택할 때

## 피벗 선택 전략
1. **첫 번째 원소**: 구현 간단, 정렬된 데이터에 비효율적
2. **마지막 원소**: 첫 번째와 동일한 문제
3. **중간 원소**: 균형 잡힌 선택
4. **랜덤**: 최악의 경우 확률 감소
5. **중앙값**: 가장 이상적이나 계산 비용 증가

## 장단점
**장점**:
- 평균적으로 매우 빠름
- 추가 메모리가 적게 필요 (in-place 정렬 가능)

**단점**:
- 최악의 경우 O(n²)
- 불안정 정렬 (같은 값의 순서가 바뀔 수 있음)', 'MARKDOWN', '알고리즘', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NULL, NOW(), NOW()),
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
- 시간복잡도 O(log n)으로 해결', NULL, 'PROBLEM', '알고리즘', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), 1, NOW(), NOW());

-- ============================================
-- 커리큘럼 2: 웹 기초 HTML/CSS
-- ============================================
INSERT INTO lectures (title, description, content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('HTML 기본 구조', 'HTML 문서의 기본 구조와 주요 태그를 배웁니다.', '# HTML 기본 구조

웹 페이지를 만드는 기초인 HTML 문서 구조를 배웁니다.

## HTML이란?
HTML(HyperText Markup Language)은 웹 페이지의 구조를 정의하는 마크업 언어입니다.

## 기본 문서 구조
```html
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>페이지 제목</title>
</head>
<body>
    <h1>안녕하세요!</h1>
    <p>첫 번째 HTML 문서입니다.</p>
</body>
</html>
```

## 주요 요소 설명
- **DOCTYPE**: HTML5 문서임을 선언
- **html**: HTML 문서의 루트 요소
- **head**: 메타데이터, 제목, 스타일 등이 들어가는 영역
- **body**: 실제로 화면에 표시되는 콘텐츠

## 시맨틱 HTML
의미 있는 태그를 사용하여 문서 구조를 명확하게 합니다:
- `<header>`: 머리말, 로고, 네비게이션
- `<nav>`: 내비게이션 링크
- `<main>`: 주요 콘텐츠
- `<article>`: 독립적인 콘텐츠
- `<section>`: 문서의 구획
- `<footer>`: 바닥글, 저작권 정보

## 시맨틱 HTML의 중요성
1. **접근성 향상**: 스크린 리더 등 보조 기술이 이해하기 쉬움
2. **SEO 개선**: 검색 엔진이 내용을 더 잘 파악
3. **유지보수 용이**: 코드의 의미가 명확함', 'MARKDOWN', '웹', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NOW(), NOW()),
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
- 유효한 HTML5 문법 사용', NULL, 'PROBLEM', '웹', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 1, NOW(), NOW()),
('CSS 선택자', 'CSS 선택자의 종류와 우선순위를 배웁니다.', '# CSS 선택자

HTML 요소에 스타일을 적용하기 위한 CSS 선택자를 배웁니다.

## CSS란?
CSS(Cascading Style Sheets)는 HTML 문서의 스타일을 정의하는 언어입니다.

## 기본 선택자

### 타입 선택자
```css
p {
    color: blue;
}
```

### 클래스 선택자
```css
.highlight {
    background-color: yellow;
}
```

### ID 선택자
```css
#main-title {
    font-size: 32px;
}
```

## 복합 선택자

### 자식 선택자
```css
div > p {
    margin: 10px;
}
```

### 후손 선택자
```css
div p {
    padding: 5px;
}
```

### 인접 형제 선택자
```css
h1 + p {
    font-weight: bold;
}
```

## 속성 선택자
```css
input[type="text"] {
    border: 1px solid gray;
}

a[href^="https"] {
    color: green;
}
```

## 가상 클래스 선택자
```css
a:hover {
    color: red;
}

li:first-child {
    font-weight: bold;
}

input:focus {
    border-color: blue;
}
```

## 선택자 우선순위
1. !important (최우선)
2. 인라인 스타일 (1000점)
3. ID 선택자 (100점)
4. 클래스/속성/가상클래스 (10점)
5. 타입 선택자 (1점)

같은 우선순위면 나중에 선언된 스타일이 적용됩니다.', 'MARKDOWN', '웹', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NOW(), NOW()),
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
- 메모리 제한: 64MB', NULL, 'PROBLEM', '웹', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 1, NOW(), NOW()),
('Flexbox', 'Flexbox 레이아웃 시스템으로 유연한 레이아웃을 만드는 방법을 배웁니다.', '# Flexbox

CSS Flexbox를 사용하여 유연한 레이아웃을 구현하는 방법을 배웁니다.

## Flexbox란?
Flexible Box Layout의 약자로, 1차원 레이아웃을 쉽게 만들 수 있는 CSS 모듈입니다.

## 기본 사용법
```css
.container {
    display: flex;
}
```

## 주요 속성 (컨테이너)

### flex-direction
아이템의 방향을 결정합니다.
```css
.container {
    flex-direction: row;        /* 기본값: 가로 */
    flex-direction: column;     /* 세로 */
    flex-direction: row-reverse;   /* 가로 역순 */
    flex-direction: column-reverse; /* 세로 역순 */
}
```

### justify-content
주축(main axis) 방향 정렬
```css
.container {
    justify-content: flex-start;    /* 시작점 */
    justify-content: center;        /* 중앙 */
    justify-content: flex-end;      /* 끝점 */
    justify-content: space-between; /* 양쪽 끝에 배치, 사이 균등 */
    justify-content: space-around;  /* 아이템 주위 균등 */
}
```

### align-items
교차축(cross axis) 방향 정렬
```css
.container {
    align-items: stretch;    /* 기본값: 늘림 */
    align-items: flex-start; /* 시작점 */
    align-items: center;     /* 중앙 */
    align-items: flex-end;   /* 끝점 */
}
```

### flex-wrap
줄바꿈 여부
```css
.container {
    flex-wrap: nowrap; /* 기본값: 한 줄 */
    flex-wrap: wrap;   /* 여러 줄 */
}
```

## 주요 속성 (아이템)

### flex-grow
여백을 차지하는 비율
```css
.item {
    flex-grow: 1; /* 균등 분배 */
}
```

### flex-shrink
공간이 부족할 때 줄어드는 비율
```css
.item {
    flex-shrink: 1; /* 기본값 */
}
```

### flex-basis
아이템의 기본 크기
```css
.item {
    flex-basis: 200px;
}
```

## 실용 예제
```css
.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-container {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}
```', 'MARKDOWN', '웹', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NOW(), NOW()),
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
- 메모리 제한: 64MB', NULL, 'PROBLEM', '웹', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 1, NOW(), NOW()),
('반응형 디자인', '다양한 디바이스에 대응하는 반응형 웹 디자인을 배웁니다.', '# 반응형 디자인 (Responsive Design)

다양한 화면 크기에 자동으로 적응하는 웹사이트를 만드는 방법을 배웁니다.

## 반응형 디자인이란?
하나의 웹사이트가 다양한 디바이스(PC, 태블릿, 모바일)에서 최적의 사용자 경험을 제공하도록 디자인하는 기법입니다.

## 미디어 쿼리
화면 크기에 따라 다른 CSS를 적용합니다.

### 기본 문법
```css
@media (조건) {
    /* 조건이 참일 때 적용될 스타일 */
}
```

### 실용 예제
```css
/* 모바일 우선 (Mobile First) */
.container {
    width: 100%;
    padding: 10px;
}

/* 태블릿 (768px 이상) */
@media (min-width: 768px) {
    .container {
        width: 750px;
        margin: 0 auto;
    }
}

/* 데스크톱 (1024px 이상) */
@media (min-width: 1024px) {
    .container {
        width: 1000px;
    }
}

/* 대형 데스크톱 (1200px 이상) */
@media (min-width: 1200px) {
    .container {
        width: 1140px;
    }
}
```

## 일반적인 브레이크포인트
```css
/* 모바일: 0 ~ 767px */
/* 태블릿: 768px ~ 1023px */
@media (min-width: 768px) and (max-width: 1023px) {
    /* 태블릿 전용 스타일 */
}

/* 데스크톱: 1024px 이상 */
@media (min-width: 1024px) {
    /* 데스크톱 스타일 */
}
```

## 뷰포트 메타 태그
반응형 디자인을 위해 필수적입니다.
```html
<meta name="viewport" content="width=device-width, initial-scale=1.0">
```

## 반응형 이미지
```css
img {
    max-width: 100%;
    height: auto;
}
```

## 반응형 그리드 예제
```css
.grid {
    display: grid;
    gap: 20px;
    grid-template-columns: 1fr; /* 모바일: 1열 */
}

@media (min-width: 768px) {
    .grid {
        grid-template-columns: repeat(2, 1fr); /* 태블릿: 2열 */
    }
}

@media (min-width: 1024px) {
    .grid {
        grid-template-columns: repeat(3, 1fr); /* 데스크톱: 3열 */
    }
}
```

## 모바일 우선 vs 데스크톱 우선

### 모바일 우선 (권장)
```css
/* 기본: 모바일 */
.box { width: 100%; }

/* 태블릿 이상 */
@media (min-width: 768px) {
    .box { width: 50%; }
}
```

### 데스크톱 우선
```css
/* 기본: 데스크톱 */
.box { width: 33.33%; }

/* 태블릿 이하 */
@media (max-width: 767px) {
    .box { width: 100%; }
}
```', 'MARKDOWN', '웹', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NULL, NOW(), NOW()),
('[문제] 반응형 그리드 레이아웃',
'미디어 쿼리로 반응형 그리드를 만드세요.

**요구사항:**
- 모바일 (0-767px): grid-template-columns 1개
- 태블릿 (768px-1023px): grid-template-columns 2개
- 데스크톱 (1024px 이상): grid-template-columns 3개
- grid-gap: 10px 이상 30px 이하

**제약 조건:**
- 시간 제한: 5초
- 메모리 제한: 64MB', NULL, 'PROBLEM', '웹', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'lee.seojun'), 1, NOW(), NOW());

-- ============================================
-- 커리큘럼 3: Python 자료구조
-- ============================================
INSERT INTO lectures (title, description, content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('리스트와 튜플', '파이썬의 순서형 자료구조인 리스트와 튜플을 배웁니다.', '# 리스트와 튜플

파이썬의 대표적인 순서형 자료구조를 배웁니다.

## 리스트 (List)

### 특징
- 순서가 있는 컬렉션
- 변경 가능(mutable)
- 대괄호 `[]` 사용
- 다양한 타입의 요소 저장 가능

### 생성과 접근
```python
# 리스트 생성
numbers = [1, 2, 3, 4, 5]
mixed = [1, "hello", 3.14, True]

# 인덱싱
print(numbers[0])   # 1
print(numbers[-1])  # 5 (마지막 요소)

# 슬라이싱
print(numbers[1:4])  # [2, 3, 4]
print(numbers[:3])   # [1, 2, 3]
print(numbers[2:])   # [3, 4, 5]
```

### 주요 메서드
```python
fruits = ["apple", "banana"]

# 추가
fruits.append("cherry")      # 끝에 추가
fruits.insert(1, "orange")   # 특정 위치에 추가

# 삭제
fruits.remove("banana")      # 값으로 삭제
popped = fruits.pop()        # 마지막 요소 제거 및 반환
del fruits[0]                # 인덱스로 삭제

# 기타
fruits.sort()                # 정렬
fruits.reverse()             # 역순
count = fruits.count("apple") # 개수 세기
index = fruits.index("apple") # 인덱스 찾기
```

## 튜플 (Tuple)

### 특징
- 순서가 있는 컬렉션
- 변경 불가능(immutable)
- 소괄호 `()` 사용
- 리스트보다 메모리 효율적

### 생성과 사용
```python
# 튜플 생성
point = (10, 20)
single = (1,)  # 요소가 하나일 때 쉼표 필수

# 인덱싱과 슬라이싱 (리스트와 동일)
print(point[0])   # 10
print(point[:1])  # (10,)

# 언패킹
x, y = point
print(x, y)  # 10 20
```

### 사용 시나리오
```python
# 함수에서 여러 값 반환
def get_coordinates():
    return (10, 20)

# 딕셔너리 키로 사용
locations = {
    (0, 0): "원점",
    (10, 20): "점 A"
}

# 변경되지 않아야 할 데이터
RGB_RED = (255, 0, 0)
```

## 리스트 vs 튜플

| 특징 | 리스트 | 튜플 |
|------|--------|------|
| 변경 가능 | O | X |
| 속도 | 느림 | 빠름 |
| 메모리 | 많음 | 적음 |
| 사용 케이스 | 변경 필요 | 불변 데이터 |

## 리스트 컴프리헨션
```python
# 기본 형태
squares = [x**2 for x in range(10)]

# 조건부
evens = [x for x in range(10) if x % 2 == 0]

# 중첩
matrix = [[i*j for j in range(3)] for i in range(3)]
```', 'MARKDOWN', 'Python', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NULL, NOW(), NOW()),
('[문제] 리스트 중복 제거',
'정수 리스트에서 중복된 값을 제거하고 순서를 유지한 새 리스트를 반환하는 함수를 작성하세요.

**입력 형식:**
- 첫 줄: 리스트의 크기 N (1 ≤ N ≤ 1000)
- 둘째 줄: N개의 정수 (-1000 ≤ 각 원소 ≤ 1000)

**출력 형식:**
- 중복을 제거하고 순서를 유지한 리스트를 공백으로 구분하여 출력

**제약 조건:**
- 시간 제한: 1초
- 메모리 제한: 128MB', NULL, 'PROBLEM', 'Python', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'park.gaeun'), 1, NOW(), NOW()),
('딕셔너리', '키-값 쌍으로 데이터를 저장하는 딕셔너리 자료구조를 배웁니다.', '# 딕셔너리 (Dictionary)

파이썬의 해시 테이블 기반 자료구조를 배웁니다.

## 딕셔너리란?
키(key)와 값(value) 쌍으로 데이터를 저장하는 자료구조입니다.

### 특징
- 키-값 쌍으로 구성
- 순서 없음 (Python 3.7+ 에서는 삽입 순서 유지)
- 키는 고유해야 함
- 중괄호 `{}` 사용
- O(1) 시간 복잡도로 검색

## 생성과 접근

### 딕셔너리 생성
```python
# 중괄호 사용
student = {
    "name": "홍길동",
    "age": 20,
    "major": "컴퓨터공학"
}

# dict() 함수 사용
student2 = dict(name="김철수", age=21)

# 빈 딕셔너리
empty = {}
empty2 = dict()
```

### 값 접근
```python
# 키로 접근
print(student["name"])  # 홍길동

# get() 메서드 (키가 없어도 에러 발생 안 함)
print(student.get("name"))      # 홍길동
print(student.get("grade", 0))  # 0 (기본값)
```

## 주요 연산

### 추가 및 수정
```python
# 추가/수정 (같은 방법)
student["grade"] = 4.0
student["age"] = 21  # 수정

# update() 메서드
student.update({"gpa": 3.8, "year": 2023})
```

### 삭제
```python
# del 키워드
del student["age"]

# pop() 메서드 (값 반환)
grade = student.pop("grade")

# popitem() - 마지막 항목 제거
last = student.popitem()

# clear() - 모두 삭제
student.clear()
```

## 딕셔너리 메서드

### 키, 값, 항목 가져오기
```python
student = {"name": "홍길동", "age": 20, "major": "CS"}

# 모든 키
keys = student.keys()       # dict_keys([''name'', ''age'', ''major''])

# 모든 값
values = student.values()   # dict_values([''홍길동'', 20, ''CS''])

# 모든 키-값 쌍
items = student.items()     # dict_items([(''name'', ''홍길동''), ...])
```

### 반복문
```python
# 키로 반복
for key in student:
    print(key, student[key])

# 키-값 쌍으로 반복
for key, value in student.items():
    print(f"{key}: {value}")

# 값으로만 반복
for value in student.values():
    print(value)
```

## 딕셔너리 컴프리헨션
```python
# 기본 형태
squares = {x: x**2 for x in range(5)}
# {0: 0, 1: 1, 2: 4, 3: 9, 4: 16}

# 조건부
evens = {x: x**2 for x in range(10) if x % 2 == 0}

# 두 리스트를 딕셔너리로
keys = [''a'', ''b'', ''c'']
values = [1, 2, 3]
dict_comp = {k: v for k, v in zip(keys, values)}
```

## 실용 예제

### 단어 빈도수 계산
```python
text = "hello world hello"
word_count = {}
for word in text.split():
    word_count[word] = word_count.get(word, 0) + 1
# {''hello'': 2, ''world'': 1}
```

### 중첩 딕셔너리
```python
students = {
    "001": {"name": "홍길동", "score": 95},
    "002": {"name": "김철수", "score": 88}
}
print(students["001"]["name"])  # 홍길동
```

## 효율적인 데이터 검색
딕셔너리는 해시 테이블로 구현되어 O(1) 시간에 검색이 가능합니다.
```python
# 리스트 검색: O(n)
names = ["Alice", "Bob", "Charlie"]
"Bob" in names  # 전체 순회 필요

# 딕셔너리 검색: O(1)
scores = {"Alice": 95, "Bob": 88, "Charlie": 92}
"Bob" in scores  # 즉시 찾음
```', 'MARKDOWN', 'Python', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NULL, NOW(), NOW()),
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
- 메모리 제한: 128MB', NULL, 'PROBLEM', 'Python', '기초', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'park.gaeun'), 1, NOW(), NOW()),
('스택 구현', 'LIFO 방식의 스택 자료구조를 파이썬으로 구현하는 방법을 배웁니다.', '# 스택 (Stack)

후입선출(LIFO) 방식의 스택 자료구조를 배웁니다.

## 스택이란?
나중에 들어간 것이 먼저 나오는(Last In First Out, LIFO) 자료구조입니다.

### 실생활 예시
- 접시 쌓기: 마지막에 쌓은 접시를 먼저 꺼냄
- 브라우저 뒤로 가기: 가장 최근 페이지로 이동
- 함수 호출 스택: 가장 최근 호출된 함수가 먼저 종료

## 스택의 주요 연산

### 기본 연산
1. **push**: 스택에 요소 추가 (맨 위에)
2. **pop**: 스택에서 요소 제거 (맨 위에서)
3. **peek/top**: 맨 위 요소 확인 (제거하지 않음)
4. **is_empty**: 스택이 비어있는지 확인

## 파이썬 리스트로 구현

### 기본 구현
```python
class Stack:
    def __init__(self):
        self.items = []

    def push(self, item):
        """요소 추가"""
        self.items.append(item)

    def pop(self):
        """요소 제거 및 반환"""
        if not self.is_empty():
            return self.items.pop()
        raise IndexError("Stack is empty")

    def peek(self):
        """맨 위 요소 확인"""
        if not self.is_empty():
            return self.items[-1]
        raise IndexError("Stack is empty")

    def is_empty(self):
        """스택이 비어있는지 확인"""
        return len(self.items) == 0

    def size(self):
        """스택 크기 반환"""
        return len(self.items)
```

### 사용 예제
```python
stack = Stack()
stack.push(1)
stack.push(2)
stack.push(3)

print(stack.peek())  # 3
print(stack.pop())   # 3
print(stack.pop())   # 2
print(stack.size())  # 1
```

## 간단한 방법 (리스트 직접 사용)
```python
stack = []

# push
stack.append(1)
stack.append(2)
stack.append(3)

# pop
top = stack.pop()  # 3

# peek
top = stack[-1]  # 2

# is_empty
is_empty = len(stack) == 0
```

## 실제 사용 사례

### 1. 괄호 짝 맞추기
```python
def is_balanced(expression):
    stack = []
    pairs = {'')'': ''('', ''}'': ''{'', '']'': ''[''}

    for char in expression:
        if char in ''({['':
            stack.append(char)
        elif char in '')}]'':
            if not stack or stack[-1] != pairs[char]:
                return False
            stack.pop()

    return len(stack) == 0

print(is_balanced("({[]})"))  # True
print(is_balanced("({[}])"))  # False
```

### 2. 역순 문자열
```python
def reverse_string(text):
    stack = []
    for char in text:
        stack.append(char)

    reversed_text = ''''
    while stack:
        reversed_text += stack.pop()

    return reversed_text

print(reverse_string("hello"))  # "olleh"
```

### 3. 함수 호출 스택
```python
def factorial(n):
    """
    factorial(3) 호출 시 스택:
    1. factorial(3) push
    2. factorial(2) push
    3. factorial(1) push
    4. factorial(0) push
    5. factorial(0) pop -> return 1
    6. factorial(1) pop -> return 1
    7. factorial(2) pop -> return 2
    8. factorial(3) pop -> return 6
    """
    if n == 0:
        return 1
    return n * factorial(n - 1)
```

## 시간 복잡도
- push: O(1)
- pop: O(1)
- peek: O(1)
- is_empty: O(1)

모든 연산이 상수 시간에 수행됩니다!

## 응용 문제
1. 계산기 구현 (후위 표기법)
2. 미로 탐색 (DFS)
3. 되돌리기(Undo) 기능
4. 웹 브라우저 방문 기록', 'MARKDOWN', 'Python', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NULL, NOW(), NOW()),
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
- 스택 자료구조 사용 필수', NULL, 'PROBLEM', 'Python', '중급', true, NULL, 15, (SELECT id FROM users WHERE login_id = 'park.gaeun'), 1, NOW(), NOW());

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
