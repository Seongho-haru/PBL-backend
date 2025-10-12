-- 샘플 강의 데이터 삽입 및 커리큘럼 연결
-- V28__Insert_sample_lectures.sql

-- 커리큘럼 1 (알고리즘) 강의들
INSERT INTO lectures (title, description, type, category, difficulty, is_public, author_id, created_at, updated_at) VALUES
('알고리즘이란?', '# 알고리즘이란?

## 정의
알고리즘은 문제를 해결하기 위한 **단계별 절차**입니다. 컴퓨터가 수행할 작업을 명확하고 정확하게 정의한 일련의 명령어들의 집합입니다.

## 알고리즘의 특징
- **명확성**: 각 단계가 명확해야 함
- **유한성**: 반드시 종료되어야 함
- **입력**: 0개 이상의 입력을 받음
- **출력**: 1개 이상의 출력을 생성
- **효과성**: 각 단계가 실행 가능해야 함

## 예시: 최댓값 찾기
```python
def find_max(numbers):
    max_value = numbers[0]
    for num in numbers:
        if num > max_value:
            max_value = num
    return max_value

# 사용 예시
numbers = [3, 7, 2, 9, 1]
result = find_max(numbers)
print(f"최댓값: {result}")  # 출력: 최댓값: 9
```

## 알고리즘의 중요성
- **효율성**: 문제를 빠르고 정확하게 해결
- **재사용성**: 다양한 상황에 적용 가능
- **최적화**: 시간과 공간을 효율적으로 사용', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NOW(), NOW()),

('시간복잡도', '# 시간복잡도 (Time Complexity)

## 정의
시간복잡도는 알고리즘이 실행되는데 걸리는 **시간**을 입력 크기에 대한 함수로 표현한 것입니다.

## Big O 표기법
- **O(1)**: 상수 시간 (Constant Time)
- **O(log n)**: 로그 시간 (Logarithmic Time)
- **O(n)**: 선형 시간 (Linear Time)
- **O(n log n)**: 선형 로그 시간 (Linearithmic Time)
- **O(n²)**: 이차 시간 (Quadratic Time)
- **O(2ⁿ)**: 지수 시간 (Exponential Time)

## 시간복잡도 비교
| 복잡도 | n=10 | n=100 | n=1000 |
|--------|------|-------|--------|
| O(1)   | 1    | 1     | 1      |
| O(log n) | 3.3 | 6.6   | 10     |
| O(n)   | 10   | 100   | 1000   |
| O(n²)  | 100  | 10,000| 1,000,000 |

## 예시: 선형 검색 vs 이진 검색
```python
# 선형 검색: O(n)
def linear_search(arr, target):
    for i, num in enumerate(arr):
        if num == target:
            return i
    return -1

# 이진 검색: O(log n)
def binary_search(arr, target):
    left, right = 0, len(arr) - 1
    while left <= right:
        mid = (left + right) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1
```

## 공간복잡도
공간복잡도는 알고리즘이 사용하는 **메모리 공간**을 입력 크기에 대한 함수로 표현한 것입니다.', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NOW(), NOW()),

('Big O 표기법', '# Big O 표기법 (Big O Notation)

## 정의
Big O 표기법은 알고리즘의 **최악의 경우** 성능을 나타내는 수학적 표기법입니다.

## 주요 복잡도 클래스

### O(1) - 상수 시간
```python
def get_first_element(arr):
    return arr[0]  # 항상 첫 번째 원소만 반환
```

### O(log n) - 로그 시간
```python
def binary_search(arr, target):
    left, right = 0, len(arr) - 1
    while left <= right:
        mid = (left + right) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1
```

### O(n) - 선형 시간
```python
def find_max(arr):
    max_val = arr[0]
    for num in arr:
        if num > max_val:
            max_val = num
    return max_val
```

### O(n log n) - 선형 로그 시간
```python
def merge_sort(arr):
    if len(arr) <= 1:
        return arr
    
    mid = len(arr) // 2
    left = merge_sort(arr[:mid])
    right = merge_sort(arr[mid:])
    
    return merge(left, right)

def merge(left, right):
    result = []
    i = j = 0
    
    while i < len(left) and j < len(right):
        if left[i] <= right[j]:
            result.append(left[i])
            i += 1
        else:
            result.append(right[j])
            j += 1
    
    result.extend(left[i:])
    result.extend(right[j:])
    return result
```

### O(n²) - 이차 시간
```python
def bubble_sort(arr):
    n = len(arr)
    for i in range(n):
        for j in range(0, n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
    return arr
```

## 성능 비교 그래프
```
시간
 ↑
 │     O(2ⁿ)
 │    ╱
 │   ╱
 │  ╱ O(n²)
 │ ╱
 │╱ O(n log n)
 │╱
 │╱ O(n)
 │╱
 │╱ O(log n)
 │╱
 │╱ O(1)
 └─────────────────→ 입력 크기
```

## 실제 성능 비교
- **O(1)**: 배열 인덱싱, 해시 테이블 조회
- **O(log n)**: 이진 검색, 균형 이진 트리
- **O(n)**: 선형 검색, 배열 순회
- **O(n log n)**: 효율적인 정렬 알고리즘
- **O(n²)**: 버블 정렬, 선택 정렬
- **O(2ⁿ)**: 피보나치 재귀, 하노이의 탑', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NOW(), NOW()),

('버블 정렬', '# 버블 정렬 (Bubble Sort)

## 개념
버블 정렬은 인접한 두 원소를 비교하여 더 큰 원소를 뒤로 이동시키는 정렬 알고리즘입니다. 가장 큰 원소가 맨 뒤로 "버블"처럼 올라가는 모습에서 이름이 유래되었습니다.

## 동작 원리
1. 첫 번째와 두 번째 원소를 비교
2. 더 큰 원소를 뒤로 이동
3. 두 번째와 세 번째 원소를 비교
4. 이 과정을 배열 끝까지 반복
5. 한 번의 패스가 끝나면 가장 큰 원소가 맨 뒤로 이동
6. 남은 원소들에 대해 위 과정을 반복

## 코드 구현
```python
def bubble_sort(arr):
    n = len(arr)
    
    # 외부 루프: 전체 배열을 한 번씩 순회
    for i in range(n):
        # 내부 루프: 정렬되지 않은 부분만 순회
        for j in range(0, n - i - 1):
            # 인접한 원소들을 비교
            if arr[j] > arr[j + 1]:
                # 더 큰 원소를 뒤로 이동
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
    
    return arr

# 사용 예시
numbers = [64, 34, 25, 12, 22, 11, 90]
print("정렬 전:", numbers)

sorted_numbers = bubble_sort(numbers.copy())
print("정렬 후:", sorted_numbers)
```

## 시각적 표현
```
초기 배열: [64, 34, 25, 12, 22, 11, 90]

패스 1:
64 > 34 → 교환: [34, 64, 25, 12, 22, 11, 90]
64 > 25 → 교환: [34, 25, 64, 12, 22, 11, 90]
64 > 12 → 교환: [34, 25, 12, 64, 22, 11, 90]
64 > 22 → 교환: [34, 25, 12, 22, 64, 11, 90]
64 > 11 → 교환: [34, 25, 12, 22, 11, 64, 90]
64 < 90 → 교환 안함: [34, 25, 12, 22, 11, 64, 90]

패스 2:
34 > 25 → 교환: [25, 34, 12, 22, 11, 64, 90]
34 > 12 → 교환: [25, 12, 34, 22, 11, 64, 90]
34 > 22 → 교환: [25, 12, 22, 34, 11, 64, 90]
34 > 11 → 교환: [25, 12, 22, 11, 34, 64, 90]
34 < 64 → 교환 안함: [25, 12, 22, 11, 34, 64, 90]

... (계속)
```

## 시간 복잡도 분석
- **최선의 경우**: O(n) - 이미 정렬된 배열
- **평균의 경우**: O(n²) - 일반적인 경우
- **최악의 경우**: O(n²) - 역순으로 정렬된 배열

## 공간 복잡도
- **공간 복잡도**: O(1) - 추가 메모리 사용 없음 (in-place)

## 장단점

### 장점
- **구현이 간단**하고 이해하기 쉬움
- **추가 메모리**가 필요 없음 (in-place)
- **안정 정렬** (stable sort)

### 단점
- **비효율적** - O(n²) 시간 복잡도
- **대용량 데이터**에는 부적합
- **실제로는 거의 사용되지 않음**

## 개선된 버전 (최적화)
```python
def optimized_bubble_sort(arr):
    n = len(arr)
    
    for i in range(n):
        swapped = False  # 교환이 일어났는지 추적
        
        for j in range(0, n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        
        # 교환이 일어나지 않았다면 이미 정렬됨
        if not swapped:
            break
    
    return arr
```

## 실제 사용 사례
버블 정렬은 교육 목적으로만 사용되며, 실제 프로덕션에서는 다음 알고리즘들을 사용합니다:
- **퀵 정렬** (Quick Sort)
- **병합 정렬** (Merge Sort)
- **힙 정렬** (Heap Sort)
- **팀 정렬** (Timsort)', 'MARKDOWN', '알고리즘', '기초', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NOW(), NOW()),

('퀵 정렬', '퀵 정렬 알고리즘', 'MARKDOWN', '알고리즘', '중급', true, (SELECT id FROM users WHERE login_id = 'kim.yuhee'), NOW(), NOW());

-- 커리큘럼 2 (웹) 강의들
INSERT INTO lectures (title, description, type, category, difficulty, is_public, author_id, created_at, updated_at) VALUES
('HTML 기본 구조', 'HTML 문서의 기본 구조', 'MARKDOWN', '웹', '기초', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NOW(), NOW()),
('CSS 선택자', 'CSS 선택자 활용법', 'MARKDOWN', '웹', '기초', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NOW(), NOW()),
('Flexbox', 'Flexbox 레이아웃', 'MARKDOWN', '웹', '중급', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NOW(), NOW()),
('반응형 디자인', '미디어 쿼리 활용', 'MARKDOWN', '웹', '중급', true, (SELECT id FROM users WHERE login_id = 'lee.seojun'), NOW(), NOW());

-- 커리큘럼 3 (Python) 강의들
INSERT INTO lectures (title, description, type, category, difficulty, is_public, author_id, created_at, updated_at) VALUES
('리스트와 튜플', '파이썬 리스트와 튜플', 'MARKDOWN', 'Python', '기초', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NOW(), NOW()),
('딕셔너리', '딕셔너리 활용법', 'MARKDOWN', 'Python', '기초', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NOW(), NOW()),
('스택 구현', '스택 자료구조 구현', 'MARKDOWN', 'Python', '중급', true, (SELECT id FROM users WHERE login_id = 'park.gaeun'), NOW(), NOW());

-- 커리큘럼-강의 연결 (curriculum_lectures)
-- 커리큘럼 1 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '알고리즘이란?'), 1, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '시간복잡도'), 2, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = 'Big O 표기법'), 3, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '버블 정렬'), 4, true),
((SELECT id FROM curriculums WHERE title = 'Introduction To Algorithms'), (SELECT id FROM lectures WHERE title = '퀵 정렬'), 5, false);

-- 커리큘럼 2 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = 'HTML 기본 구조'), 1, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = 'CSS 선택자'), 2, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = 'Flexbox'), 3, true),
((SELECT id FROM curriculums WHERE title = '웹 기초: HTML/CSS'), (SELECT id FROM lectures WHERE title = '반응형 디자인'), 4, false);

-- 커리큘럼 3 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '리스트와 튜플'), 1, true),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '딕셔너리'), 2, true),
((SELECT id FROM curriculums WHERE title = 'Python 자료구조'), (SELECT id FROM lectures WHERE title = '스택 구현'), 3, true);
