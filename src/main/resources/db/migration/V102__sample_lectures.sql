-- Python 강의 데이터 삽입 및 커리큘럼 연결
-- V102__sample_lectures.sql

-- ============================================
-- Python 강의 데이터
-- ============================================

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('Python 첫걸음', 'Python의 기본 개념과 설치, 첫 프로그램 작성하기', '# Python 첫걸음

Python은 배우기 쉽고 강력한 프로그래밍 언어로, 데이터 분석부터 웹 개발까지 다양한 분야에서 활용됩니다. 이 강의에서는 Python의 기본 개념을 이해하고 첫 프로그램을 작성해봅니다.

## 학습 목표

- [ ] Python의 역사와 특징을 설명할 수 있다
- [ ] Python의 주요 활용 분야를 이해한다
- [ ] Python 설치 및 실행 환경을 구성할 수 있다
- [ ] 대화형 인터프리터(REPL)의 개념을 이해하고 사용할 수 있다

## Python이란?

Python은 1991년 귀도 반 로섬(Guido van Rossum)이 개발한 고급 프로그래밍 언어입니다. "읽기 쉬운 코드"를 철학으로 하며, 간결하고 명확한 문법이 특징입니다.

Python의 핵심 철학은 "The Zen of Python"에 잘 나타나 있습니다. 그 중 가장 중요한 원칙은 "Simple is better than complex(단순함이 복잡함보다 낫다)"와 "Readability counts(가독성이 중요하다)"입니다.

Python은 인터프리터 언어로, 코드를 한 줄씩 실행하며 동적 타이핑을 지원합니다. 이는 변수의 타입을 미리 선언할 필요가 없어 초보자도 쉽게 배울 수 있다는 의미입니다.

## Python의 활용 분야

- **데이터 분석 및 AI**: NumPy, Pandas, TensorFlow 등의 라이브러리로 데이터 처리 및 머신러닝
- **웹 개발**: Django, Flask를 활용한 백엔드 개발
- **자동화**: 반복 작업 자동화 스크립트 작성
- **과학 계산**: 연구 및 시뮬레이션 프로그램 개발

## Python 설치하기

### Windows
1. python.org에서 최신 버전 다운로드
2. 설치 시 "Add Python to PATH" 체크
3. 설치 완료 후 명령 프롬프트에서 확인

### macOS/Linux
```bash
# 버전 확인
python3 --version

# 설치 (macOS - Homebrew 사용)
brew install python3
```

### 설치 확인
```bash
# Python 버전 확인
python --version
# 출력 예: Python 3.12.0
```

## REPL로 Python 시작하기

REPL(Read-Eval-Print Loop)은 코드를 입력하면 즉시 실행하고 결과를 보여주는 대화형 환경입니다.

```python
# 터미널에서 Python 실행
python

# Python REPL 환경이 시작됩니다
>>> 2 + 3
5
>>> "Hello" + " World"
''Hello World''
>>> exit()  # 종료
```

## 주요 예제

### 예제 1: 첫 프로그램 - Hello, World!
```python
# REPL에서 실행
>>> print("Hello, World!")
Hello, World!
```

### 예제 2: 간단한 계산
```python
# 사칙연산
>>> 10 + 5
15
>>> 20 / 4
5.0
>>> 2 ** 3  # 거듭제곱
8
```

### 예제 3: 변수 사용하기
```python
# 변수 선언 (타입 지정 불필요)
>>> name = "Python"
>>> version = 3.12
>>> print(f"{name} {version}")
Python 3.12
```

### 예제 4: 파일로 작성하기
```python
# hello.py 파일 생성
print("안녕하세요, Python입니다!")
print("첫 프로그램을 실행했습니다.")

# 터미널에서 실행: python hello.py
# 출력:
# 안녕하세요, Python입니다!
# 첫 프로그램을 실행했습니다.
```

## 주의사항

- ⚠️ Python 2.x와 3.x는 호환되지 않습니다. 최신 버전인 Python 3.x를 사용하세요.
- ⚠️ 들여쓰기(indentation)가 문법의 일부입니다. 공백이나 탭을 일관되게 사용하세요.
- 💡 REPL은 간단한 테스트에 유용하지만, 복잡한 프로그램은 파일로 작성하세요.
- 💡 에러 메시지를 두려워하지 마세요. Python의 에러 메시지는 문제를 찾는 데 도움을 줍니다.

## 정리

Python은 간결하고 읽기 쉬운 문법으로 초보자부터 전문가까지 널리 사용되는 프로그래밍 언어입니다. REPL을 통해 즉시 코드를 실행하고 결과를 확인할 수 있어 학습하기 좋습니다.

### 배운 내용
- ✅ Python은 1991년에 만들어진 인터프리터 언어로, 읽기 쉬운 코드를 중시합니다
- ✅ 데이터 분석, 웹 개발, 자동화 등 다양한 분야에서 활용됩니다
- ✅ REPL 환경에서 코드를 즉시 실행하고 테스트할 수 있습니다
- ✅ print() 함수로 첫 프로그램을 작성하고 실행할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('변수와 기본 자료형', 'Python 변수 선언, 기본 자료형, 동적 타이핑 및 형변환 학습', '# 변수와 기본 자료형

변수는 데이터를 저장하는 공간입니다. Python은 동적 타이핑을 지원하여 변수 선언 시 타입을 지정하지 않아도 되며, 실행 시점에 자동으로 타입이 결정됩니다.

## 학습 목표

- [ ] 변수의 개념과 명명 규칙을 이해한다
- [ ] Python의 기본 자료형(int, float, str, bool)을 구분할 수 있다
- [ ] 동적 타이핑의 특징을 설명할 수 있다
- [ ] type() 함수를 사용하여 자료형을 확인할 수 있다
- [ ] 자료형 간 변환(형변환)을 수행할 수 있다

## 변수란?

변수는 데이터를 메모리에 저장하고 필요할 때 꺼내 쓸 수 있도록 이름을 붙인 저장 공간입니다. Python에서는 `=` 연산자를 사용하여 변수에 값을 할당합니다.

Python의 가장 큰 특징 중 하나는 **동적 타이핑(Dynamic Typing)**입니다. C나 Java처럼 변수를 선언할 때 타입을 명시할 필요가 없으며, 할당되는 값에 따라 자동으로 타입이 결정됩니다. 같은 변수에 다른 타입의 값을 다시 할당하는 것도 가능합니다.

## 기본 사용법

```python
# 변수 선언과 할당
name = "Python"
age = 30
height = 175.5
is_student = True

# 변수 사용
print(name)      # Python
print(age + 5)   # 35
```

변수명은 의미 있는 이름을 사용하여 코드의 가독성을 높이는 것이 좋습니다.

## 주요 예제

### 예제 1: 기본 자료형 확인하기
```python
# 정수형 (int)
age = 25
print(type(age))  # <class ''int''>

# 실수형 (float)
pi = 3.14
print(type(pi))   # <class ''float''>

# 문자열 (str)
message = "안녕하세요"
print(type(message))  # <class ''str''>

# 불린형 (bool)
is_active = True
print(type(is_active))  # <class ''bool''>
```

### 예제 2: 동적 타이핑
```python
# 같은 변수에 다른 타입 할당 가능
x = 10          # int
print(type(x))  # <class ''int''>

x = "Hello"     # str
print(type(x))  # <class ''str''>

x = 3.14        # float
print(type(x))  # <class ''float''>
```

### 예제 3: 형변환 (Type Conversion)
```python
# 문자열을 숫자로
num_str = "100"
num = int(num_str)
print(num + 50)  # 150

# 숫자를 문자열로
age = 25
age_str = str(age)
print("나이: " + age_str)  # 나이: 25

# 실수를 정수로 (소수점 이하 버림)
price = 9.99
int_price = int(price)
print(int_price)  # 9
```

### 예제 4: 변수 명명 규칙
```python
# 올바른 변수명 (snake_case 권장)
user_name = "김철수"
total_price = 10000
is_valid = True

# 여러 변수 동시 할당
x, y, z = 1, 2, 3
print(x, y, z)  # 1 2 3

# 같은 값 여러 변수에 할당
a = b = c = 0
print(a, b, c)  # 0 0 0
```

### 예제 5: None 타입과 isinstance() 함수
```python
# None: 값이 없음을 나타내는 특별한 타입
result = None
print(type(result))  # <class ''NoneType''>

# isinstance()로 타입 확인
age = 25
print(isinstance(age, int))     # True
print(isinstance(age, str))     # False
print(isinstance(3.14, float))  # True
```

## 주의사항

- ⚠️ 변수명은 숫자로 시작할 수 없습니다 (예: `1st_name` 불가능)
- ⚠️ 예약어(if, for, class 등)는 변수명으로 사용할 수 없습니다
- ⚠️ 형변환 시 변환할 수 없는 값이면 오류가 발생합니다 (예: `int("abc")`)
- 💡 Python 변수명 관례: `snake_case` 사용 (단어를 언더스코어로 연결)
- 💡 의미 있는 변수명을 사용하면 코드 이해가 쉬워집니다
- 💡 `type()` 함수는 디버깅 시 변수의 타입을 확인하는 데 유용합니다

## 정리

변수는 데이터를 저장하는 공간이며, Python은 동적 타이핑을 지원하여 타입 선언 없이 자유롭게 사용할 수 있습니다. 기본 자료형을 이해하고 적절히 변환하는 것은 Python 프로그래밍의 기초입니다.

### 배운 내용
- ✅ 변수 선언과 할당 방법 (`=` 연산자)
- ✅ 기본 자료형: int, float, str, bool, NoneType
- ✅ 동적 타이핑으로 인한 유연한 변수 사용
- ✅ `type()`과 `isinstance()`로 자료형 확인
- ✅ 형변환 함수: `int()`, `float()`, `str()`, `bool()`
- ✅ snake_case 변수 명명 규칙
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('연산자의 모든 것', 'Python의 다양한 연산자와 우선순위를 이해하고 활용', '# 연산자의 모든 것

프로그래밍에서 연산자는 값을 계산하고 비교하며 논리적 판단을 내리는 핵심 도구입니다. Python은 직관적이고 강력한 연산자 시스템을 제공합니다.

## 학습 목표

- [ ] 산술 연산자를 사용하여 수치 계산을 수행할 수 있다
- [ ] 비교 연산자와 논리 연산자의 차이를 이해한다
- [ ] 연산자 우선순위를 이해하고 적용할 수 있다
- [ ] 단축 평가(short-circuit evaluation)의 개념을 설명할 수 있다
- [ ] 비트 연산자의 기본 사용법을 이해한다

## 연산자란?

연산자는 변수나 값에 대해 특정 연산을 수행하는 기호입니다. Python은 산술, 비교, 논리, 할당, 비트 연산자 등 다양한 종류를 제공하며, 각각 고유한 용도가 있습니다.

연산자를 올바르게 사용하면 복잡한 계산과 조건 판단을 간결하게 표현할 수 있습니다. 특히 연산자 우선순위와 단축 평가를 이해하면 효율적이고 안전한 코드를 작성할 수 있습니다.

## 산술 연산자

### 기본 산술 연산

```python
# 사칙연산과 거듭제곱
a = 10
b = 3

print(a + b)   # 13 (덧셈)
print(a - b)   # 7 (뺄셈)
print(a * b)   # 30 (곱셈)
print(a / b)   # 3.333... (나눗셈, 항상 float 반환)
print(a ** b)  # 1000 (거듭제곱, 10의 3승)
```

### 나눗셈 연산자의 차이

```python
# 나눗셈 연산자 비교
print(10 / 3)   # 3.333... (일반 나눗셈)
print(10 // 3)  # 3 (몫, 정수 부분만)
print(10 % 3)   # 1 (나머지)

# 실용 예제: 시간 계산
seconds = 3665
hours = seconds // 3600
minutes = (seconds % 3600) // 60
print(f"{hours}시간 {minutes}분")  # 1시간 1분
```

## 비교 연산자

비교 연산자는 두 값을 비교하여 `True` 또는 `False`를 반환합니다.

```python
x = 10
y = 5

print(x == y)   # False (같음)
print(x != y)   # True (다름)
print(x > y)    # True (크다)
print(x < y)    # False (작다)
print(x >= 10)  # True (크거나 같다)
print(y <= 5)   # True (작거나 같다)
```

## 논리 연산자

### 기본 논리 연산

```python
# and, or, not 연산자
age = 25
has_license = True

# and: 모두 True일 때만 True
can_drive = age >= 18 and has_license
print(can_drive)  # True

# or: 하나라도 True면 True
is_special = age < 18 or age > 65
print(is_special)  # False

# not: 반대 값
print(not has_license)  # False
```

### 단축 평가 (Short-circuit Evaluation)

```python
# and는 첫 False에서 평가 중단
result = False and print("실행 안 됨")  # print 실행 안 됨

# or는 첫 True에서 평가 중단
result = True or print("실행 안 됨")  # print 실행 안 됨

# 실용 예제: 안전한 나눗셈
x = 0
result = x != 0 and 10 / x  # x가 0이므로 나눗셈 실행 안 됨
print(result)  # False
```

## 할당 연산자

### 복합 할당 연산자

```python
# 기본 할당
count = 10

# 복합 할당 연산자
count += 5   # count = count + 5 → 15
count -= 3   # count = count - 3 → 12
count *= 2   # count = count * 2 → 24
count //= 4  # count = count // 4 → 6
print(count)  # 6
```

## 비트 연산자

비트 연산자는 정수를 2진수로 변환하여 비트 단위로 연산합니다.

```python
# 비트 AND, OR, XOR
a = 5   # 0b0101
b = 3   # 0b0011

print(a & b)   # 1 (0b0001, AND)
print(a | b)   # 7 (0b0111, OR)
print(a ^ b)   # 6 (0b0110, XOR)
print(~a)      # -6 (비트 반전)

# 비트 시프트
print(a << 1)  # 10 (왼쪽으로 1비트, *2와 같음)
print(a >> 1)  # 2 (오른쪽으로 1비트, //2와 같음)
```

## 연산자 우선순위

```python
# 우선순위: ** > *, /, //, % > +, - > 비교 > not > and > or
result = 2 + 3 * 4    # 14 (곱셈이 먼저)
result = (2 + 3) * 4  # 20 (괄호가 최우선)

# 복잡한 표현식
result = 10 > 5 and 3 + 2 == 5  # True
# 1. 3 + 2 = 5
# 2. 10 > 5 = True, 5 == 5 = True
# 3. True and True = True
```

## 주의사항

- 나눗셈 `/`는 항상 `float`를 반환합니다. 정수 몫이 필요하면 `//`를 사용하세요
- `==`는 값 비교, `is`는 객체 동일성 비교입니다
- 단축 평가를 활용하면 오류를 방지할 수 있습니다 (예: 0으로 나누기 방지)
- 비트 연산자는 주로 저수준 프로그래밍이나 최적화에 사용됩니다
- 복잡한 표현식은 괄호 `()`로 명확하게 만드세요

## 정리

Python의 연산자는 계산, 비교, 논리 판단의 기본 도구입니다. 산술 연산자로 수치를 계산하고, 비교 연산자로 조건을 만들며, 논리 연산자로 복잡한 조건을 조합합니다. 단축 평가와 연산자 우선순위를 이해하면 더 안전하고 효율적인 코드를 작성할 수 있습니다.

### 배운 내용
- 산술 연산자 7가지: `+`, `-`, `*`, `/`, `//`, `%`, `**`
- 비교 연산자로 True/False 값 생성
- 논리 연산자의 단축 평가 동작 방식
- 복합 할당 연산자로 코드 간결화
- 비트 연산자의 기본 개념과 시프트 연산
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('입출력 다루기', 'print와 input 함수로 데이터를 주고받는 방법 학습', '# 입출력 다루기

프로그램은 사용자와 소통하기 위해 데이터를 출력하고 입력받습니다. Python의 print()와 input() 함수를 사용하면 간단하게 입출력을 처리할 수 있습니다.

## 학습 목표

- [ ] print() 함수를 사용하여 다양한 형식으로 출력할 수 있다
- [ ] input() 함수를 사용하여 사용자 입력을 받을 수 있다
- [ ] 문자열 포매팅 방법을 이해하고 활용할 수 있다
- [ ] sep, end 매개변수를 사용하여 출력 형식을 제어할 수 있다

## print() 함수란?

print() 함수는 데이터를 화면에 출력하는 가장 기본적인 함수입니다. 문자열, 숫자, 변수 등 다양한 데이터를 출력할 수 있으며, 여러 값을 동시에 출력할 수도 있습니다.

기본적으로 print()는 출력 후 자동으로 줄바꿈을 하며, 여러 값을 출력할 때는 공백으로 구분합니다.

## 기본 사용법

```python
# 문자열 출력
print("안녕하세요!")

# 숫자 출력
print(42)

# 여러 값 출력
print("이름:", "홍길동", "나이:", 25)
# 출력: 이름: 홍길동 나이: 25

# 변수 출력
name = "김철수"
age = 30
print(name, age)
# 출력: 김철수 30
```

## 주요 예제

### 예제 1: sep과 end 매개변수 사용

```python
# sep: 값 사이의 구분자 변경
print("사과", "바나나", "오렌지", sep=", ")
# 출력: 사과, 바나나, 오렌지

# end: 출력 후 줄바꿈 문자 변경
print("첫 번째 줄", end=" ")
print("같은 줄에 출력")
# 출력: 첫 번째 줄 같은 줄에 출력

# 로딩 표시 만들기
for i in range(5):
    print(".", end="")
# 출력: .....
```

### 예제 2: input() 함수로 사용자 입력 받기

```python
# 기본 입력
name = input("이름을 입력하세요: ")
print("안녕하세요,", name, "님!")

# 숫자 입력 (문자열을 숫자로 변환)
age = input("나이를 입력하세요: ")
age = int(age)  # 문자열을 정수로 변환
print("10년 후 나이는", age + 10, "살입니다.")
```

### 예제 3: 문자열 포매팅

```python
name = "이영희"
age = 28
score = 95.5

# f-string (Python 3.6+, 권장)
print(f"이름: {name}, 나이: {age}, 점수: {score}")

# format() 메서드
print("이름: {}, 나이: {}, 점수: {}".format(name, age, score))

# % 포매팅 (옛날 방식)
print("이름: %s, 나이: %d, 점수: %.1f" % (name, age, score))

# 출력: 이름: 이영희, 나이: 28, 점수: 95.5
```

### 예제 4: 실전 활용 - 간단한 계산기

```python
# 두 숫자를 입력받아 합계 출력
num1 = int(input("첫 번째 숫자: "))
num2 = int(input("두 번째 숫자: "))

result = num1 + num2
print(f"{num1} + {num2} = {result}")

# 실행 예시:
# 첫 번째 숫자: 10
# 두 번째 숫자: 20
# 10 + 20 = 30
```

### 예제 5: 여러 줄 출력

```python
# 삼중 따옴표로 여러 줄 출력
message = """
안녕하세요!
Python 입출력 강의에
오신 것을 환영합니다.
"""
print(message)

# \n으로 줄바꿈
print("첫 번째 줄\n두 번째 줄\n세 번째 줄")
```

## 주의사항

- ⚠️ **input()은 항상 문자열을 반환합니다** - 숫자 계산이 필요하면 int()나 float()로 변환하세요
- ⚠️ **타입 변환 시 에러 주의** - 숫자가 아닌 값을 int()로 변환하면 오류가 발생합니다
- 💡 **f-string 권장** - Python 3.6 이상에서는 f-string이 가장 직관적이고 빠릅니다
- 💡 **디버깅에 활용** - print()는 코드의 값을 확인하는 가장 간단한 디버깅 도구입니다

## 정리

print() 함수로 데이터를 출력하고, input() 함수로 사용자 입력을 받는 방법을 배웠습니다. sep, end 매개변수로 출력 형식을 제어할 수 있으며, f-string을 사용하면 변수를 포함한 문자열을 쉽게 만들 수 있습니다.

### 배운 내용

- ✅ print()로 다양한 데이터 출력하기
- ✅ sep, end로 출력 형식 제어하기
- ✅ input()으로 사용자 입력 받고 타입 변환하기
- ✅ f-string, format(), % 방식의 문자열 포매팅
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('문자열 기초', '문자열의 불변성, 인덱싱, 슬라이싱 등 기본 개념 학습', '# 문자열 기초

문자열(String)은 문자들의 순서 있는 집합으로, Python에서 가장 많이 사용되는 데이터 타입 중 하나입니다. 문자열을 다루는 기본 기술을 익히면 텍스트 처리, 데이터 분석, 웹 개발 등 다양한 분야에서 활용할 수 있습니다.

## 학습 목표

- [ ] 문자열의 불변성(immutability) 특징을 이해한다
- [ ] 인덱싱과 슬라이싱을 사용하여 문자열의 일부를 추출할 수 있다
- [ ] len() 함수를 사용하여 문자열 길이를 구할 수 있다
- [ ] in 연산자를 사용하여 부분 문자열 포함 여부를 확인할 수 있다

## 문자열 생성

문자열은 작은따옴표(''), 큰따옴표("), 삼중따옴표('''''', """)로 생성할 수 있습니다. 삼중따옴표는 여러 줄의 문자열을 작성할 때 사용합니다.

```python
# 다양한 방법으로 문자열 생성
text1 = ''Hello, Python!''
text2 = "안녕하세요"
text3 = ''''''여러 줄의
문자열을 작성할 때는
삼중따옴표를 사용합니다''''''

print(text1)  # Hello, Python!
print(text2)  # 안녕하세요
print(text3)  # 여러 줄 출력
```

## 문자열 인덱싱

문자열의 각 문자는 인덱스(위치)를 가지며, 0부터 시작합니다. 음수 인덱스는 뒤에서부터 접근할 때 사용합니다(-1이 마지막 문자).

```python
text = "Python"

# 양수 인덱스: 0, 1, 2, 3, 4, 5
print(text[0])   # P (첫 번째 문자)
print(text[2])   # t (세 번째 문자)

# 음수 인덱스: -6, -5, -4, -3, -2, -1
print(text[-1])  # n (마지막 문자)
print(text[-3])  # h (뒤에서 세 번째)
```

## 문자열 슬라이싱

슬라이싱은 문자열의 일부분을 추출하는 기능으로, `[start:stop:step]` 형식을 사용합니다. `start`는 포함되고 `stop`은 포함되지 않습니다.

```python
text = "Python Programming"

# [start:stop]
print(text[0:6])    # Python
print(text[7:18])   # Programming
print(text[:6])     # Python (처음부터)
print(text[7:])     # Programming (끝까지)

# [start:stop:step]
print(text[::2])    # Pto rgamn (2칸씩)
print(text[::-1])   # gnimmargorP nohtyP (역순)
```

## 문자열 길이

`len()` 함수는 문자열의 길이(문자 개수)를 반환합니다. 공백과 특수문자도 포함됩니다.

```python
text = "Hello, World!"
length = len(text)

print(length)  # 13
print(len("Python"))  # 6
print(len("안녕하세요"))  # 5
```

## 주요 예제

### 예제 1: 문자열 연산
```python
# + 연산자로 문자열 연결
greeting = "Hello" + " " + "Python"
print(greeting)  # Hello Python

# * 연산자로 문자열 반복
repeat = "Python! " * 3
print(repeat)  # Python! Python! Python!
```

### 예제 2: 부분 문자열 포함 확인
```python
text = "Python Programming"

# in 연산자로 포함 여부 확인
print("Python" in text)      # True
print("Java" in text)        # False
print("Python" not in text)  # False

# 실제 활용 예시
if "Python" in text:
    print("Python 문자열이 포함되어 있습니다")
```

### 예제 3: 이메일 도메인 추출
```python
email = "user@example.com"

# @ 위치 찾기 (index 메서드 사용)
at_index = email.index("@")

# 슬라이싱으로 도메인 추출
domain = email[at_index+1:]
print(domain)  # example.com
```

### 예제 4: 문자열의 불변성
```python
text = "Python"

# 문자열은 변경할 수 없음 (불변성)
# text[0] = "J"  # 오류 발생!

# 새로운 문자열을 생성해야 함
new_text = "J" + text[1:]
print(new_text)  # Jython
print(text)      # Python (원본은 그대로)
```

## 주의사항

- 문자열은 불변(immutable) 객체입니다. 생성 후 내용을 직접 수정할 수 없으며, 새로운 문자열을 만들어야 합니다
- 인덱스 범위를 벗어나면 `IndexError`가 발생합니다. 인덱스는 0부터 `len(문자열)-1`까지 사용 가능합니다
- 슬라이싱에서 `stop` 인덱스는 결과에 포함되지 않습니다. `text[0:3]`은 인덱스 0, 1, 2만 포함합니다

## 정리

문자열은 Python의 핵심 데이터 타입으로, 불변성을 가지며 인덱싱과 슬라이싱으로 효율적으로 다룰 수 있습니다. `len()` 함수와 `in` 연산자를 활용하면 문자열 정보를 쉽게 확인할 수 있습니다.

### 배운 내용
- 문자열은 불변(immutable) 객체로 생성 후 내용을 직접 수정할 수 없다
- 인덱싱으로 개별 문자에 접근하고, 슬라이싱으로 부분 문자열을 추출한다
- `len()` 함수로 문자열 길이를 구하고, `in` 연산자로 포함 여부를 확인한다
- `+`와 `*` 연산자로 문자열을 연결하고 반복할 수 있다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('문자열 메서드 완전 정복', '문자열 대소문자, 분리, 결합, 검색, 치환 메서드 활용법', '# 문자열 메서드 완전 정복

Python 문자열은 다양한 내장 메서드를 제공하여 텍스트 데이터를 쉽게 처리할 수 있습니다. 이번 강의에서는 실무에서 가장 자주 사용되는 문자열 메서드들을 배워보겠습니다.

## 학습 목표

- [ ] 문자열 메서드를 사용하여 대소문자를 변환할 수 있다
- [ ] split()과 join()을 사용하여 문자열을 분리하고 결합할 수 있다
- [ ] strip() 계열 메서드로 공백을 제거할 수 있다
- [ ] 문자열 검색 메서드를 활용할 수 있다
- [ ] 문자열 치환과 정렬 메서드를 사용할 수 있다

## 대소문자 변환 메서드

사용자 입력을 표준화하거나 데이터를 일관되게 처리할 때 필수적인 메서드들입니다.

```python
text = "Hello Python World"

print(text.upper())        # HELLO PYTHON WORLD
print(text.lower())        # hello python world
print(text.capitalize())   # Hello python world
print(text.title())        # Hello Python World
```

- `upper()`: 모든 문자를 대문자로 변환
- `lower()`: 모든 문자를 소문자로 변환
- `capitalize()`: 첫 문자만 대문자로, 나머지는 소문자로
- `title()`: 각 단어의 첫 문자를 대문자로 변환

## 문자열 분리와 결합

### split() - 문자열 분리

문자열을 특정 구분자로 나눠 리스트로 만듭니다.

```python
# 공백으로 분리 (기본값)
text = "Python is awesome"
words = text.split()
print(words)  # [''Python'', ''is'', ''awesome'']

# 특정 구분자로 분리
email = "user@example.com"
parts = email.split("@")
print(parts)  # [''user'', ''example.com'']
```

### join() - 문자열 결합

리스트의 요소들을 하나의 문자열로 합칩니다.

```python
words = ["Python", "is", "fun"]
sentence = " ".join(words)
print(sentence)  # Python is fun

# CSV 형식으로 결합
data = ["홍길동", "30", "서울"]
csv_line = ",".join(data)
print(csv_line)  # 홍길동,30,서울
```

## 주요 예제

### 예제 1: 공백 제거하기

```python
# 사용자 입력에서 불필요한 공백 제거
user_input = "  hello world  "
print(user_input.strip())   # "hello world"
print(user_input.lstrip())  # "hello world  "
print(user_input.rstrip())  # "  hello world"
```

`strip()`은 양쪽, `lstrip()`은 왼쪽, `rstrip()`은 오른쪽 공백을 제거합니다.

### 예제 2: 문자열 검색

```python
text = "Python programming is fun"

# 문자열 찾기
print(text.find("programming"))     # 7 (시작 위치)
print(text.find("java"))            # -1 (없으면 -1)

# 포함 여부 확인
print(text.startswith("Python"))    # True
print(text.endswith("fun"))         # True

# 개수 세기
print(text.count("n"))              # 3
```

### 예제 3: 문자열 치환

```python
text = "I like Java. Java is good."

# 문자열 바꾸기
new_text = text.replace("Java", "Python")
print(new_text)  # I like Python. Python is good.

# 횟수 제한하여 바꾸기
new_text = text.replace("Java", "Python", 1)
print(new_text)  # I like Python. Java is good.
```

### 예제 4: 문자열 정렬

```python
text = "Python"

# 가운데 정렬
print(text.center(20, "-"))  # -------Python-------

# 왼쪽 정렬
print(text.ljust(10, "*"))   # Python****

# 오른쪽 정렬
print(text.rjust(10, "*"))   # ****Python

# 숫자 앞에 0 채우기
num = "42"
print(num.zfill(5))          # 00042
```

### 예제 5: 실전 활용 - 이메일 검증

```python
email = "user@example.com"

# 기본 검증
if "@" in email and email.endswith(".com"):
    username = email.split("@")[0]
    domain = email.split("@")[1]
    print(f"사용자명: {username}")  # user
    print(f"도메인: {domain}")      # example.com
```

## 주의사항

- ⚠️ **불변성**: 문자열 메서드는 원본을 수정하지 않고 새로운 문자열을 반환합니다
- ⚠️ **find() vs index()**: `find()`는 없으면 -1 반환, `index()`는 ValueError 발생
- 💡 **체이닝**: 여러 메서드를 연결하여 사용 가능 (예: `text.strip().lower()`)
- 💡 **split() 기본값**: 인자 없이 사용하면 모든 공백 문자로 분리

## 정리

문자열 메서드는 텍스트 데이터 처리의 핵심 도구입니다. 특히 split()과 join()은 CSV 파일 처리, 로그 분석 등 실무에서 매우 자주 사용됩니다.

### 배운 내용
- ✅ 대소문자 변환: upper(), lower(), capitalize(), title()
- ✅ 분리와 결합: split(), join()
- ✅ 공백 제거: strip(), lstrip(), rstrip()
- ✅ 검색: find(), startswith(), endswith(), count()
- ✅ 치환과 정렬: replace(), center(), ljust(), rjust(), zfill()
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('문자열 포매팅 심화', '% 연산자, format(), f-string을 사용한 문자열 포매팅', '# 문자열 포매팅 심화

문자열 포매팅은 변수를 문자열에 삽입하는 기술입니다. Python은 % 연산자, format() 메서드, f-string 세 가지 방법을 제공하며, 각각의 장단점이 있습니다.

## 학습 목표

- [ ] % 연산자를 사용한 포매팅을 수행할 수 있다
- [ ] str.format() 메서드의 다양한 기능을 활용할 수 있다
- [ ] f-string(formatted string literal)을 사용할 수 있다
- [ ] 포매팅 옵션을 이해하고 적용할 수 있다
- [ ] 상황에 맞는 포매팅 방법을 선택할 수 있다

## % 연산자 포매팅

C언어 스타일의 포매팅 방법으로, 타입 지정자를 사용합니다.

```python
# 기본 사용법
name = "Alice"
age = 25
print("이름: %s, 나이: %d" % (name, age))
# 출력: 이름: Alice, 나이: 25

# 소수점 제어
price = 1234.5678
print("가격: %.2f원" % price)
# 출력: 가격: 1234.57원
```

주요 타입 지정자: `%s`(문자열), `%d`(정수), `%f`(실수)

## str.format() 메서드

Python 2.6부터 도입된 방법으로, 중괄호 `{}`를 사용합니다.

```python
# 위치 인자
print("{0}는 {1}살입니다".format("Bob", 30))
# 출력: Bob는 30살입니다

# 키워드 인자
print("{name}의 점수: {score}점".format(name="Charlie", score=95))
# 출력: Charlie의 점수: 95점
```

## f-string (권장)

Python 3.6부터 추가된 가장 직관적이고 빠른 방법입니다.

```python
name = "David"
score = 88
print(f"{name}의 점수: {score}점")
# 출력: David의 점수: 88점

# 표현식 사용 가능
x = 10
y = 20
print(f"{x} + {y} = {x + y}")
# 출력: 10 + 20 = 30
```

## 주요 예제

### 예제 1: 포매팅 옵션 - 자릿수와 정렬
```python
# 소수점 자릿수
pi = 3.14159265
print(f"원주율: {pi:.2f}")  # 출력: 원주율: 3.14

# 정렬 (< 왼쪽, > 오른쪽, ^ 가운데)
text = "Python"
print(f"|{text:<10}|")  # 출력: |Python    |
print(f"|{text:>10}|")  # 출력: |    Python|
print(f"|{text:^10}|")  # 출력: |  Python  |
```

### 예제 2: 숫자 포매팅
```python
# 천 단위 구분
number = 1234567
print(f"{number:,}원")  # 출력: 1,234,567원

# 진법 표현
num = 255
print(f"10진수: {num}, 16진수: {num:x}, 2진수: {num:b}")
# 출력: 10진수: 255, 16진수: ff, 2진수: 11111111
```

### 예제 3: 실전 활용 - 표 형식 출력
```python
# 학생 성적표
students = [("김철수", 85), ("이영희", 92), ("박민수", 78)]

print(f"{''이름'':^6} | {''점수'':^6}")
print("-" * 17)
for name, score in students:
    print(f"{name:^6} | {score:^6}")

# 출력:
#  이름   |  점수
# -----------------
# 김철수  |   85
# 이영희  |   92
# 박민수  |   78
```

### 예제 4: 날짜와 시간 포매팅
```python
from datetime import datetime

now = datetime.now()
print(f"현재 시각: {now:%Y년 %m월 %d일 %H:%M:%S}")
# 출력: 현재 시각: 2025년 10월 25일 14:30:45

# 딕셔너리 활용
info = {"name": "홍길동", "age": 28, "city": "서울"}
print(f"{info[''name'']}님은 {info[''city'']}에 거주하는 {info[''age'']}세입니다")
# 출력: 홍길동님은 서울에 거주하는 28세입니다
```

### 예제 5: 각 방법 비교
```python
name = "Eve"
age = 27

# 세 가지 방법 비교
print("이름: %s, 나이: %d" % (name, age))
print("이름: {}, 나이: {}".format(name, age))
print(f"이름: {name}, 나이: {age}")
# 모두 동일한 결과: 이름: Eve, 나이: 27
```

## 주의사항

- ⚠️ **f-string은 Python 3.6 이상**: 이전 버전에서는 사용 불가
- ⚠️ **% 연산자의 타입 불일치**: `%d`에 문자열을 넣으면 오류 발생
- 💡 **f-string 권장**: 가독성이 좋고 성능이 가장 빠름
- 💡 **복잡한 표현식은 변수로**: f-string 내부를 간결하게 유지

## 정리

문자열 포매팅은 데이터를 보기 좋게 출력하는 필수 기술입니다. Python 3.6 이상에서는 f-string을 우선적으로 사용하되, 레거시 코드나 특수한 상황에서는 다른 방법도 이해하고 있어야 합니다.

### 배운 내용
- ✅ % 연산자로 타입 지정자를 사용한 포매팅
- ✅ format() 메서드로 위치/키워드 인자 활용
- ✅ f-string으로 직관적이고 빠른 포매팅
- ✅ 정렬, 자릿수, 진법 등 다양한 포매팅 옵션
- ✅ 상황별 최적의 포매팅 방법 선택
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('리스트 기초', '리스트 생성, 인덱싱, 슬라이싱, 가변성 및 기본 연산 학습', '# 리스트 기초

리스트는 Python에서 가장 많이 사용되는 컬렉션 자료형입니다. 여러 개의 값을 순서대로 저장하고 관리할 수 있으며, 필요에 따라 내용을 자유롭게 수정할 수 있습니다.

## 학습 목표

- [ ] 리스트의 특징과 생성 방법을 이해한다
- [ ] 인덱싱과 슬라이싱으로 리스트 요소에 접근할 수 있다
- [ ] 리스트의 가변성(mutability)을 이해한다
- [ ] 리스트 연산(+, *, in)을 수행할 수 있다
- [ ] 중첩 리스트의 개념을 이해한다

## 리스트란?

리스트는 여러 개의 값을 순서대로 담을 수 있는 가변(mutable) 컨테이너입니다. 대괄호 `[]`를 사용하여 생성하며, 쉼표로 요소를 구분합니다.

리스트는 다양한 타입의 데이터를 함께 저장할 수 있고, 생성 후에도 요소를 추가, 삭제, 수정할 수 있어 매우 유연합니다. 데이터 여러 개를 한 번에 관리해야 할 때 필수적으로 사용됩니다.

## 기본 사용법

```python
# 빈 리스트 생성
empty_list = []
empty_list2 = list()

# 값이 있는 리스트 생성
numbers = [1, 2, 3, 4, 5]
fruits = [''apple'', ''banana'', ''cherry'']

# 다양한 타입 혼합 가능
mixed = [1, ''hello'', 3.14, True]
print(mixed)  # [1, ''hello'', 3.14, True]
```

## 주요 예제

### 예제 1: 인덱싱과 슬라이싱

```python
# 인덱싱: 개별 요소 접근 (0부터 시작)
fruits = [''apple'', ''banana'', ''cherry'', ''date'']
print(fruits[0])   # apple
print(fruits[-1])  # date (뒤에서 첫 번째)

# 슬라이싱: 범위 지정 [시작:끝]
print(fruits[1:3])   # [''banana'', ''cherry'']
print(fruits[:2])    # [''apple'', ''banana'']
print(fruits[2:])    # [''cherry'', ''date'']
```

### 예제 2: 리스트의 가변성

```python
# 리스트는 생성 후 변경 가능 (mutable)
colors = [''red'', ''green'', ''blue'']
colors[1] = ''yellow''  # 두 번째 요소 변경
print(colors)  # [''red'', ''yellow'', ''blue'']

# 슬라이싱으로 여러 요소 한 번에 변경
numbers = [1, 2, 3, 4, 5]
numbers[1:3] = [20, 30]
print(numbers)  # [1, 20, 30, 4, 5]
```

### 예제 3: 리스트 연산

```python
# 연결(+)
list1 = [1, 2, 3]
list2 = [4, 5]
result = list1 + list2
print(result)  # [1, 2, 3, 4, 5]

# 반복(*)
repeat = [0] * 5
print(repeat)  # [0, 0, 0, 0, 0]

# 멤버십 검사(in)
fruits = [''apple'', ''banana'']
print(''apple'' in fruits)  # True
```

### 예제 4: 리스트 기본 함수

```python
# len(): 리스트 길이
numbers = [10, 20, 30, 40]
print(len(numbers))  # 4

# max(), min(), sum()
print(max(numbers))  # 40
print(min(numbers))  # 10
print(sum(numbers))  # 100
```

### 예제 5: 중첩 리스트

```python
# 리스트 안에 리스트 (2차원 리스트)
matrix = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9]
]

# 중첩 인덱싱
print(matrix[0])     # [1, 2, 3]
print(matrix[0][1])  # 2 (첫 행, 두 번째 열)
print(matrix[1][2])  # 6 (두 번째 행, 세 번째 열)
```

## 주의사항

- ⚠️ 리스트 인덱스는 0부터 시작합니다. 범위를 벗어나면 `IndexError`가 발생합니다
- ⚠️ 슬라이싱 시 끝 인덱스는 포함되지 않습니다. `[1:3]`은 인덱스 1, 2만 포함
- 💡 음수 인덱스를 사용하면 뒤에서부터 접근할 수 있습니다 (-1이 마지막 요소)
- 💡 리스트는 가변 자료형이므로 원본이 변경될 수 있습니다. 복사가 필요하면 `list.copy()` 사용

## 정리

리스트는 여러 값을 순서대로 저장하는 가변 컨테이너로, Python에서 가장 자주 사용되는 자료형입니다. 인덱싱과 슬라이싱으로 자유롭게 요소에 접근하고, 생성 후에도 내용을 수정할 수 있습니다.

### 배운 내용

- ✅ 리스트는 `[]`로 생성하며, 다양한 타입의 데이터를 저장할 수 있습니다
- ✅ 인덱싱(0부터 시작)과 슬라이싱으로 요소에 접근하고 수정할 수 있습니다
- ✅ 리스트는 가변(mutable) 자료형으로 생성 후 내용 변경이 가능합니다
- ✅ `+`, `*`, `in` 연산자와 `len()` 함수로 리스트를 다룰 수 있습니다
- ✅ 리스트 안에 리스트를 넣어 2차원 이상의 구조를 만들 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('리스트 메서드와 활용', '리스트의 주요 메서드를 활용하여 데이터를 조작하는 방법', '# 리스트 메서드와 활용

리스트는 Python에서 가장 자주 사용되는 자료형으로, 데이터를 추가, 삭제, 정렬하는 다양한 메서드를 제공합니다. 이 강의에서는 실무에서 자주 사용되는 리스트 메서드들을 익히고 활용하는 방법을 배웁니다.

## 학습 목표

- [ ] append()와 extend()의 차이를 이해하고 사용할 수 있다
- [ ] insert()와 remove()를 사용하여 특정 위치의 요소를 조작할 수 있다
- [ ] pop()을 사용하여 요소를 제거하고 반환받을 수 있다
- [ ] sort()와 sorted()의 차이를 이해한다
- [ ] 리스트 복사의 얕은 복사와 깊은 복사를 구분할 수 있다

## 요소 추가하기

리스트에 새로운 요소를 추가하는 세 가지 주요 메서드가 있습니다.

### append() - 끝에 하나 추가

```python
fruits = [''apple'', ''banana'']
fruits.append(''cherry'')
print(fruits)  # [''apple'', ''banana'', ''cherry'']
```

`append()`는 리스트의 끝에 하나의 요소를 추가합니다. 가장 많이 사용되는 메서드입니다.

### extend() - 여러 요소 추가

```python
fruits = [''apple'', ''banana'']
fruits.extend([''cherry'', ''orange''])
print(fruits)  # [''apple'', ''banana'', ''cherry'', ''orange'']

# append와의 차이
fruits2 = [''apple'', ''banana'']
fruits2.append([''cherry'', ''orange''])
print(fruits2)  # [''apple'', ''banana'', [''cherry'', ''orange'']]
```

`extend()`는 다른 리스트의 모든 요소를 추가하지만, `append()`는 리스트 자체를 하나의 요소로 추가합니다.

### insert() - 특정 위치에 추가

```python
fruits = [''apple'', ''cherry'']
fruits.insert(1, ''banana'')  # 인덱스 1에 삽입
print(fruits)  # [''apple'', ''banana'', ''cherry'']
```

## 요소 제거하기

### remove() - 값으로 제거

```python
fruits = [''apple'', ''banana'', ''cherry'', ''banana'']
fruits.remove(''banana'')  # 첫 번째 ''banana''만 제거
print(fruits)  # [''apple'', ''cherry'', ''banana'']
```

### pop() - 위치로 제거하고 반환

```python
fruits = [''apple'', ''banana'', ''cherry'']
last = fruits.pop()  # 마지막 요소 제거
print(last)  # ''cherry''
print(fruits)  # [''apple'', ''banana'']

first = fruits.pop(0)  # 인덱스 0 제거
print(first)  # ''apple''
print(fruits)  # [''banana'']
```

`pop()`은 제거한 요소를 반환하므로 값을 사용할 수 있습니다.

### clear() - 모두 제거

```python
fruits = [''apple'', ''banana'', ''cherry'']
fruits.clear()
print(fruits)  # []
```

## 검색과 정보

### index() - 위치 찾기

```python
fruits = [''apple'', ''banana'', ''cherry'']
position = fruits.index(''banana'')
print(position)  # 1
```

### count() - 개수 세기

```python
numbers = [1, 2, 3, 2, 2, 4]
count = numbers.count(2)
print(count)  # 3
```

## 정렬하기

### sort() - 원본 리스트 정렬

```python
numbers = [3, 1, 4, 1, 5]
numbers.sort()  # 오름차순
print(numbers)  # [1, 1, 3, 4, 5]

numbers.sort(reverse=True)  # 내림차순
print(numbers)  # [5, 4, 3, 1, 1]
```

### sorted() - 새 리스트 반환

```python
numbers = [3, 1, 4, 1, 5]
new_list = sorted(numbers)
print(new_list)  # [1, 1, 3, 4, 5]
print(numbers)  # [3, 1, 4, 1, 5] - 원본 유지
```

`sort()`는 원본을 변경하고, `sorted()`는 원본을 유지하며 새 리스트를 반환합니다.

### reverse() - 순서 뒤집기

```python
fruits = [''apple'', ''banana'', ''cherry'']
fruits.reverse()
print(fruits)  # [''cherry'', ''banana'', ''apple'']
```

## 리스트 복사하기

### 얕은 복사 (Shallow Copy)

```python
# 방법 1: copy()
original = [1, 2, 3]
copied = original.copy()
copied.append(4)
print(original)  # [1, 2, 3] - 영향 없음
print(copied)    # [1, 2, 3, 4]

# 방법 2: 슬라이싱
copied2 = original[:]

# 방법 3: list()
copied3 = list(original)
```

### 중첩 리스트와 깊은 복사

```python
import copy

# 얕은 복사의 한계
original = [[1, 2], [3, 4]]
shallow = original.copy()
shallow[0].append(99)
print(original)  # [[1, 2, 99], [3, 4]] - 영향 받음!

# 깊은 복사
original = [[1, 2], [3, 4]]
deep = copy.deepcopy(original)
deep[0].append(99)
print(original)  # [[1, 2], [3, 4]] - 영향 없음
print(deep)      # [[1, 2, 99], [3, 4]]
```

## 실전 예제

### 학생 성적 관리

```python
scores = [85, 92, 78, 90, 88]

# 성적 추가
scores.append(95)

# 정렬하여 상위 3개 확인
scores.sort(reverse=True)
top3 = scores[:3]
print(f"상위 3개 성적: {top3}")

# 평균 계산
average = sum(scores) / len(scores)
print(f"평균: {average:.2f}")
```

### 중복 제거하기

```python
items = [1, 2, 2, 3, 4, 3, 5]

# 중복 제거 (순서 유지)
unique = []
for item in items:
    if item not in unique:
        unique.append(item)
print(unique)  # [1, 2, 3, 4, 5]
```

## 주의사항

- append()는 하나의 요소만, extend()는 여러 요소를 추가합니다
- remove()는 존재하지 않는 값을 제거하려 하면 오류가 발생합니다
- sort()는 None을 반환하므로 `sorted_list = list.sort()`처럼 사용하면 안 됩니다
- 중첩 리스트는 copy()로 복사하면 내부 리스트가 공유되므로 deepcopy()를 사용해야 합니다
- pop()은 빈 리스트에 사용하면 오류가 발생합니다

## 정리

리스트 메서드는 데이터를 효율적으로 관리하는 핵심 도구입니다. append()와 extend()의 차이, sort()와 sorted()의 차이, 그리고 얕은 복사와 깊은 복사의 차이를 정확히 이해하는 것이 중요합니다.

### 배운 내용
- append()는 하나, extend()는 여러 요소를 추가한다
- pop()은 제거한 값을 반환하여 사용할 수 있다
- sort()는 원본 변경, sorted()는 새 리스트 반환
- 중첩 리스트는 깊은 복사가 필요하다
- index()와 count()로 요소를 검색할 수 있다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('튜플 - 불변 시퀀스', '튜플의 불변성과 패킹/언패킹 활용법을 학습합니다', '# 튜플 - 불변 시퀀스

튜플(tuple)은 한 번 생성되면 값을 변경할 수 없는 불변(immutable) 시퀀스입니다. 리스트와 비슷해 보이지만, 수정이 불가능하다는 특성 덕분에 데이터 보호와 성능 최적화에 유용하게 사용됩니다.

## 학습 목표

- [ ] 튜플의 불변성 특징을 이해하고 설명할 수 있다
- [ ] 튜플과 리스트의 차이점을 비교하여 설명할 수 있다
- [ ] 튜플 패킹과 언패킹을 실제 코드에 활용할 수 있다
- [ ] 튜플의 활용 사례와 사용 이유를 이해한다

## 튜플이란?

튜플은 여러 개의 값을 하나로 묶어 저장하는 시퀀스 자료형입니다. 리스트와 달리 한 번 생성하면 값을 추가, 수정, 삭제할 수 없습니다. 이러한 불변성 덕분에 프로그램에서 변경되면 안 되는 데이터를 안전하게 보관하거나, 딕셔너리의 키로 사용할 수 있습니다.

튜플은 괄호 `()`를 사용하거나 쉼표로 값을 나열하여 생성합니다. 리스트보다 메모리를 적게 사용하고 접근 속도가 빠르기 때문에, 읽기 전용 데이터에 적합합니다.

## 기본 사용법

```python
# 다양한 튜플 생성 방법
empty = ()                    # 빈 튜플
numbers = (1, 2, 3)          # 숫자 튜플
mixed = (1, ''hello'', 3.14)   # 혼합 타입
single = (5,)                # 단일 요소 (쉼표 필수!)
no_paren = 1, 2, 3           # 괄호 생략 가능

print(numbers[0])            # 1 (인덱싱)
print(numbers[1:])           # (2, 3) (슬라이싱)
```

단일 요소 튜플을 만들 때는 반드시 쉼표를 붙여야 합니다. `(5)`는 그냥 정수 5이지만, `(5,)`는 튜플입니다.

## 주요 예제

### 예제 1: 튜플의 불변성
```python
# 튜플은 수정 불가능
coordinate = (10, 20)
# coordinate[0] = 15  # TypeError 발생!

# 새로운 튜플 생성은 가능
coordinate = (15, 20)  # 완전히 새로운 객체

# 리스트와 비교
my_list = [10, 20]
my_list[0] = 15  # 수정 가능
print(my_list)   # [15, 20]
```

### 예제 2: 튜플 패킹과 언패킹
```python
# 패킹: 여러 값을 튜플로 묶기
person = ''홍길동'', 25, ''Seoul''  # 괄호 생략

# 언패킹: 튜플을 개별 변수로 분리
name, age, city = person
print(f"{name}님은 {age}세입니다")  # 홍길동님은 25세입니다

# 변수 값 교환
a, b = 10, 20
a, b = b, a  # 간단한 스왑
print(a, b)  # 20 10
```

### 예제 3: 함수 반환값과 튜플
```python
def get_stats(numbers):
    return min(numbers), max(numbers), sum(numbers)

# 여러 값을 한번에 반환
data = [5, 2, 8, 1, 9]
minimum, maximum, total = get_stats(data)
print(f"최소: {minimum}, 최대: {maximum}, 합계: {total}")
# 최소: 1, 최대: 9, 합계: 25
```

### 예제 4: 딕셔너리 키로 활용
```python
# 튜플은 불변이므로 딕셔너리 키로 사용 가능
locations = {
    (0, 0): ''원점'',
    (10, 20): ''지점A'',
    (30, 40): ''지점B''
}

print(locations[(10, 20)])  # 지점A

# 리스트는 키로 사용 불가능
# locations[[1, 2]] = ''에러''  # TypeError!
```

### 예제 5: 튜플 메서드
```python
numbers = (1, 2, 3, 2, 2, 4)

# count: 특정 값의 개수
print(numbers.count(2))  # 3

# index: 특정 값의 첫 번째 인덱스
print(numbers.index(3))  # 2

# 튜플 연결과 반복
t1 = (1, 2)
t2 = (3, 4)
print(t1 + t2)    # (1, 2, 3, 4)
print(t1 * 3)     # (1, 2, 1, 2, 1, 2)
```

## 주의사항

- ⚠️ **단일 요소 튜플**: `(5)`가 아닌 `(5,)`로 쉼표를 꼭 붙여야 합니다
- ⚠️ **불변성 오해**: 튜플 자체는 불변이지만, 내부에 리스트가 있으면 그 리스트는 수정 가능합니다
- 💡 **성능 최적화**: 고정된 데이터는 리스트 대신 튜플을 사용하면 메모리와 속도 면에서 유리합니다
- 💡 **딕셔너리 키**: 좌표, 날짜 등 여러 값의 조합을 키로 사용할 때 튜플이 유용합니다

## 정리

튜플은 불변 시퀀스로 한 번 생성하면 수정할 수 없지만, 바로 그 특성 덕분에 데이터 보호, 딕셔너리 키 사용, 성능 최적화에 유용합니다. 튜플 패킹과 언패킹은 여러 값을 간결하게 다루는 파이썬의 강력한 기능입니다.

### 배운 내용
- ✅ 튜플은 괄호 `()`로 생성하며, 한 번 만들면 수정 불가능합니다
- ✅ 단일 요소 튜플은 `(5,)`처럼 쉼표가 필수입니다
- ✅ 패킹/언패킹으로 변수 할당과 함수 반환을 간결하게 처리할 수 있습니다
- ✅ 불변성 덕분에 딕셔너리 키로 사용 가능하며, 리스트보다 빠르고 안전합니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('집합(Set) - 중복 없는 컬렉션', '집합의 특징과 연산, 메서드를 학습하고 활용법을 익힙니다', '# 집합(Set) - 중복 없는 컬렉션

집합(Set)은 중복을 허용하지 않고 순서가 없는 파이썬의 자료형입니다. 수학의 집합 개념을 구현한 것으로, 중복 제거와 집합 연산이 필요할 때 매우 유용합니다.

## 학습 목표

- [ ] 집합의 특징(중복 불가, 순서 없음)을 이해한다
- [ ] 집합 연산(합집합, 교집합, 차집합)을 수행할 수 있다
- [ ] 집합 메서드를 사용하여 요소를 추가/제거할 수 있다
- [ ] 집합의 활용 사례를 이해한다
- [ ] frozenset의 개념을 이해한다

## 집합이란?

집합은 중복을 자동으로 제거하고 순서를 보장하지 않는 컬렉션입니다. 리스트나 튜플과 달리 인덱스로 접근할 수 없으며, 해시 가능한(immutable) 값만 요소로 가질 수 있습니다.

집합은 멤버십 테스트(`in` 연산)가 매우 빠르고, 수학적 집합 연산을 지원하여 데이터 분석과 중복 제거에 자주 사용됩니다.

## 기본 사용법

```python
# 집합 생성
fruits = {''apple'', ''banana'', ''orange''}
print(fruits)  # {''orange'', ''banana'', ''apple''} - 순서 보장 안 됨

# 빈 집합 생성 (주의: {}는 딕셔너리)
empty_set = set()

# 리스트에서 중복 제거
numbers = [1, 2, 2, 3, 3, 3]
unique = set(numbers)
print(unique)  # {1, 2, 3}
```

## 주요 예제

### 예제 1: 집합의 특징

```python
# 중복 자동 제거
colors = {''red'', ''blue'', ''red'', ''green'', ''blue''}
print(colors)  # {''green'', ''red'', ''blue''}

# 해시 가능한 요소만 가능
valid_set = {1, ''text'', (1, 2)}  # 정수, 문자열, 튜플 OK
# invalid = {[1, 2], {3, 4}}  # 리스트, 집합은 에러
```

### 예제 2: 집합 연산

```python
a = {1, 2, 3, 4}
b = {3, 4, 5, 6}

print(a | b)  # {1, 2, 3, 4, 5, 6} - 합집합
print(a & b)  # {3, 4} - 교집합
print(a - b)  # {1, 2} - 차집합
print(a ^ b)  # {1, 2, 5, 6} - 대칭 차집합
```

### 예제 3: 요소 추가/제거

```python
fruits = {''apple'', ''banana''}

fruits.add(''orange'')  # 요소 추가
print(fruits)  # {''apple'', ''banana'', ''orange''}

fruits.remove(''banana'')  # 요소 제거 (없으면 에러)
fruits.discard(''grape'')  # 요소 제거 (없어도 에러 없음)

item = fruits.pop()  # 임의의 요소 제거 후 반환
print(item)
```

### 예제 4: 실용적인 활용

```python
# 이메일 중복 확인
emails = [''a@test.com'', ''b@test.com'', ''a@test.com'']
unique_emails = set(emails)
print(len(unique_emails))  # 2

# 공통 관심사 찾기
user1_interests = {''python'', ''java'', ''sql''}
user2_interests = {''python'', ''javascript'', ''sql''}
common = user1_interests & user2_interests
print(common)  # {''python'', ''sql''}
```

### 예제 5: frozenset (불변 집합)

```python
# 변경 불가능한 집합
immutable_set = frozenset([1, 2, 3])
print(immutable_set)  # frozenset({1, 2, 3})

# 딕셔너리 키나 집합의 요소로 사용 가능
nested = {frozenset([1, 2]), frozenset([3, 4])}
print(nested)  # {frozenset({1, 2}), frozenset({3, 4})}
```

## 주의사항

- ⚠️ 빈 집합은 `{}`가 아닌 `set()`으로 생성해야 합니다 (`{}`는 빈 딕셔너리)
- ⚠️ 집합은 순서가 없어 인덱싱이나 슬라이싱을 사용할 수 없습니다
- ⚠️ 리스트나 딕셔너리 등 변경 가능한 객체는 집합의 요소가 될 수 없습니다
- 💡 멤버십 테스트(`in`)는 리스트보다 집합이 훨씬 빠릅니다
- 💡 중복 제거가 필요하면 `list(set(my_list))`를 사용하세요
- 💡 집합 연산은 메서드로도 가능합니다: `union()`, `intersection()`, `difference()`

## 정리

집합은 중복을 허용하지 않고 순서가 없는 컬렉션으로, 중복 제거와 집합 연산에 최적화되어 있습니다. 수학적 집합 연산을 지원하여 데이터 분석과 처리에 매우 유용합니다.

### 배운 내용

- ✅ 집합은 중복을 자동으로 제거하고 순서를 보장하지 않습니다
- ✅ 합집합(`|`), 교집합(`&`), 차집합(`-`), 대칭 차집합(`^`) 연산을 지원합니다
- ✅ `add()`, `remove()`, `discard()`, `pop()` 메서드로 요소를 관리할 수 있습니다
- ✅ 멤버십 테스트와 중복 제거에 효율적입니다
- ✅ frozenset은 변경 불가능한 집합으로 딕셔너리 키나 집합의 요소로 사용 가능합니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('딕셔너리 - 키-값 저장소', '딕셔너리 생성, 키-값 관리, 메서드 활용 및 순회 방법 학습', '# 딕셔너리 - 키-값 저장소

딕셔너리는 키(key)와 값(value)을 쌍으로 저장하는 Python의 핵심 자료형입니다. 리스트가 숫자 인덱스로 값을 관리한다면, 딕셔너리는 의미 있는 이름(키)으로 값을 저장하고 찾을 수 있습니다.

## 학습 목표

- [ ] 딕셔너리의 키-값 구조를 이해한다
- [ ] 딕셔너리에서 값을 추가, 수정, 삭제할 수 있다
- [ ] 딕셔너리 메서드를 활용할 수 있다
- [ ] get() 메서드와 대괄호 접근의 차이를 이해한다
- [ ] 딕셔너리 순회 방법을 이해한다

## 딕셔너리란?

딕셔너리는 키-값 쌍으로 데이터를 저장하는 가변(mutable) 컨테이너입니다. 중괄호 `{}`를 사용하여 생성하며, `키: 값` 형태로 데이터를 구성합니다.

사전에서 단어(키)를 찾아 뜻(값)을 확인하는 것처럼, 딕셔너리는 키를 통해 빠르게 값을 찾을 수 있습니다. 키는 고유해야 하며 불변 자료형만 사용할 수 있지만, 값은 어떤 자료형이든 가능합니다. 학생 정보, 설정값, 데이터베이스 결과 등을 저장할 때 매우 유용합니다.

## 기본 사용법

```python
# 빈 딕셔너리 생성
empty_dict = {}
empty_dict2 = dict()

# 값이 있는 딕셔너리 생성
student = {''name'': ''홍길동'', ''age'': 20, ''grade'': ''A''}

# 값 접근
print(student[''name''])  # 홍길동
print(student[''age''])   # 20
```

## 주요 예제

### 예제 1: 값 추가, 수정, 삭제

```python
# 새로운 키-값 추가
student = {''name'': ''김철수'', ''age'': 21}
student[''major''] = ''컴퓨터공학''  # 새 키 추가
print(student)  # {''name'': ''김철수'', ''age'': 21, ''major'': ''컴퓨터공학''}

# 값 수정 (이미 존재하는 키)
student[''age''] = 22
print(student[''age''])  # 22

# 값 삭제
del student[''major'']
print(student)  # {''name'': ''김철수'', ''age'': 22}
```

### 예제 2: get() 메서드 - 안전한 접근

```python
student = {''name'': ''이영희'', ''age'': 20}

# 대괄호: 키가 없으면 에러 발생
# print(student[''major''])  # KeyError 발생!

# get(): 키가 없으면 None 반환 (에러 없음)
print(student.get(''major''))  # None
print(student.get(''major'', ''미정''))  # 미정 (기본값 지정)

# 안전하게 값 확인
major = student.get(''major'', ''정보 없음'')
print(f"전공: {major}")  # 전공: 정보 없음
```

### 예제 3: keys(), values(), items() 메서드

```python
scores = {''국어'': 90, ''영어'': 85, ''수학'': 95}

# keys(): 모든 키 조회
print(scores.keys())  # dict_keys([''국어'', ''영어'', ''수학''])
print(list(scores.keys()))  # [''국어'', ''영어'', ''수학'']

# values(): 모든 값 조회
print(scores.values())  # dict_values([90, 85, 95])
print(sum(scores.values()))  # 270 (총점)

# items(): 키-값 쌍 조회
print(scores.items())
# dict_items([(''국어'', 90), (''영어'', 85), (''수학'', 95)])
```

### 예제 4: 딕셔너리 순회

```python
# 키만 순회
scores = {''국어'': 90, ''영어'': 85, ''수학'': 95}
for subject in scores:
    print(subject)  # 국어, 영어, 수학

# 키-값 함께 순회 (가장 많이 사용)
for subject, score in scores.items():
    print(f"{subject}: {score}점")
# 국어: 90점
# 영어: 85점
# 수학: 95점
```

### 예제 5: update()와 setdefault()

```python
# update(): 여러 키-값 한 번에 추가/수정
user = {''name'': ''박민수'', ''age'': 25}
user.update({''age'': 26, ''city'': ''서울''})
print(user)  # {''name'': ''박민수'', ''age'': 26, ''city'': ''서울''}

# setdefault(): 키가 없을 때만 추가
cart = {''apple'': 3}
cart.setdefault(''apple'', 5)   # 이미 있음 → 변경 안됨
cart.setdefault(''banana'', 2)  # 없음 → 추가됨
print(cart)  # {''apple'': 3, ''banana'': 2}
```

### 예제 6: 딕셔너리 컴프리헨션

```python
# 제곱수 딕셔너리 생성
squares = {x: x**2 for x in range(1, 6)}
print(squares)  # {1: 1, 2: 4, 3: 9, 4: 16, 5: 25}

# 조건부 딕셔너리 생성
scores = {''국어'': 90, ''영어'': 75, ''수학'': 95}
passed = {k: v for k, v in scores.items() if v >= 80}
print(passed)  # {''국어'': 90, ''수학'': 95}
```

## 주의사항

- ⚠️ 키는 고유해야 합니다. 같은 키를 두 번 사용하면 나중 값이 이전 값을 덮어씁니다
- ⚠️ 키로 사용 가능한 자료형: 문자열, 숫자, 튜플 (리스트, 딕셔너리는 불가)
- ⚠️ 존재하지 않는 키에 대괄호로 접근하면 `KeyError` 발생 → `get()` 사용 권장
- 💡 `in` 연산자로 키 존재 여부 확인: `''name'' in student` → True/False
- 💡 Python 3.7+에서는 딕셔너리가 입력 순서를 유지합니다

## 정리

딕셔너리는 키-값 구조로 데이터를 관리하는 강력한 자료형입니다. 의미 있는 이름(키)으로 값을 빠르게 저장하고 찾을 수 있으며, 다양한 메서드를 통해 효율적으로 데이터를 다룰 수 있습니다.

### 배운 내용

- ✅ 딕셔너리는 `{키: 값}` 형태로 생성하며, 키를 통해 값에 접근합니다
- ✅ `dict[키] = 값`으로 추가/수정, `del dict[키]`로 삭제할 수 있습니다
- ✅ `get()` 메서드는 키가 없어도 에러 없이 안전하게 값을 조회합니다
- ✅ `keys()`, `values()`, `items()`로 딕셔너리 요소를 조회할 수 있습니다
- ✅ `for key, value in dict.items():`로 모든 키-값 쌍을 순회할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('조건문 - if, elif, else', '조건에 따라 코드 실행을 제어하는 if 문 마스터하기', '# 조건문 - if, elif, else

프로그램은 상황에 따라 다른 동작을 해야 합니다. 조건문은 특정 조건이 참일 때만 코드를 실행하도록 하여, 프로그램이 상황에 맞게 대응할 수 있게 합니다.

## 학습 목표

- [ ] if 문의 구조와 실행 흐름을 이해한다
- [ ] elif와 else를 사용하여 다중 조건을 처리할 수 있다
- [ ] 중첩 if 문을 작성할 수 있다
- [ ] 조건식에서 논리 연산자를 활용할 수 있다
- [ ] Truthy/Falsy 값의 개념을 이해한다

## if 문이란?

if 문은 조건식의 결과가 참(True)일 때만 특정 코드 블록을 실행하는 제어문입니다. Python에서는 조건식 뒤에 콜론(:)을 붙이고, 실행할 코드는 들여쓰기(보통 4칸)로 구분합니다.

조건문은 프로그램의 흐름을 분기시켜 다양한 상황에 대응할 수 있게 합니다. 예를 들어 사용자 입력 검증, 등급 판정, 권한 확인 등에 필수적으로 사용됩니다.

Python은 들여쓰기로 코드 블록을 구분하므로, if 문 내부의 코드는 반드시 동일한 수준으로 들여쓰기 되어야 합니다.

## 기본 사용법

```python
# 기본 if 문
age = 20
if age >= 18:
    print("성인입니다")  # 조건이 True이므로 실행됨

# if-else 문
score = 85
if score >= 90:
    print("A 학점")
else:
    print("B 이하 학점")  # 출력: B 이하 학점
```

if 문은 조건이 True일 때만 실행되며, else는 조건이 False일 때 실행됩니다.

## 주요 예제

### 예제 1: if-elif-else 다중 조건
```python
# 성적에 따른 등급 판정
score = 75

if score >= 90:
    grade = ''A''
elif score >= 80:
    grade = ''B''
elif score >= 70:
    grade = ''C''
else:
    grade = ''F''

print(f"학점: {grade}")  # 출력: 학점: C
```

elif는 이전 조건이 False일 때 다음 조건을 검사합니다. 위에서 아래로 순차적으로 검사하며, 첫 번째로 True인 블록만 실행됩니다.

### 예제 2: 논리 연산자 활용
```python
# and, or, not 연산자
age = 25
has_license = True

# and: 모든 조건이 True여야 함
if age >= 18 and has_license:
    print("운전 가능")  # 출력됨

# or: 하나라도 True면 됨
if age < 18 or not has_license:
    print("운전 불가")
else:
    print("조건 충족")  # 출력됨
```

and는 모든 조건이 참이어야 하고, or는 하나라도 참이면 되며, not은 조건을 반대로 만듭니다.

### 예제 3: 중첩 if 문
```python
# 조건 안에 또 다른 조건
score = 85
attendance = 90

if score >= 80:
    if attendance >= 80:
        print("우수 학생")  # 출력됨
    else:
        print("성적은 좋으나 출석 부족")
else:
    print("성적 향상 필요")
```

if 문 내부에 또 다른 if 문을 넣어 더 세밀한 조건 처리가 가능합니다. 들여쓰기 레벨에 주의하세요.

### 예제 4: Truthy와 Falsy 값
```python
# Python에서 False로 평가되는 값들
values = [0, None, '''', [], {}, False]

for val in values:
    if val:
        print(f"{val}은 True")
    else:
        print(f"{val}은 False")  # 모두 False로 출력됨

# Truthy 값 활용
name = input("이름을 입력하세요: ")
if name:  # 빈 문자열이 아니면 True
    print(f"안녕하세요, {name}님!")
else:
    print("이름이 입력되지 않았습니다.")
```

Python에서는 0, None, 빈 문자열(''''), 빈 리스트([]), 빈 딕셔너리({}) 등이 False로 평가됩니다. 이를 활용하면 조건식을 간결하게 작성할 수 있습니다.

### 예제 5: 비교 연산자 체이닝
```python
# Python만의 독특한 기능: 비교 연산자 체이닝
age = 25

# 일반적인 방법
if age >= 18 and age < 65:
    print("성인")

# 체이닝 사용 (더 직관적)
if 18 <= age < 65:
    print("성인")  # 출력됨

# 범위 검사에 유용
score = 85
if 80 <= score <= 89:
    print("B 학점")  # 출력됨
```

Python에서는 수학적 표현처럼 비교 연산자를 연결할 수 있어 가독성이 높습니다.

## 주의사항

- ⚠️ **들여쓰기 주의**: Python은 들여쓰기로 블록을 구분합니다. Tab과 Space를 섞어 쓰면 오류가 발생합니다.
- ⚠️ **콜론 필수**: if, elif, else 뒤에는 반드시 콜론(:)을 붙여야 합니다.
- ⚠️ **할당과 비교**: `if x = 5:`는 오류입니다. 비교는 `==`를 사용하세요.
- 💡 **단순 조건은 간결하게**: `if x == True:` 대신 `if x:`를 사용하는 것이 pythonic합니다.
- 💡 **조건 순서 중요**: elif는 위에서 아래로 검사하므로, 더 구체적인 조건을 위에 배치하세요.

## 정리

조건문은 프로그램이 상황에 따라 다르게 동작하도록 하는 핵심 제어 구조입니다. if, elif, else를 조합하여 복잡한 논리도 명확하게 표현할 수 있으며, 논리 연산자와 Truthy/Falsy 값을 활용하면 더욱 간결한 코드를 작성할 수 있습니다.

### 배운 내용
- ✅ if 문은 조건이 True일 때만 코드를 실행하며, 들여쓰기로 블록을 구분합니다
- ✅ elif로 다중 조건을 처리하고, else로 모든 조건이 False일 때를 처리합니다
- ✅ and, or, not 연산자로 복잡한 조건을 표현하고, 중첩 if로 세밀한 제어가 가능합니다
- ✅ 0, None, 빈 컨테이너 등은 False로 평가되며, 이를 활용해 간결한 조건식을 작성할 수 있습니다
- ✅ Python의 비교 연산자 체이닝으로 범위 검사를 직관적으로 표현할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('for 반복문', 'for 문을 활용한 시퀀스 순회와 반복 제어 방법 학습', '# for 반복문

for 문은 리스트, 문자열, 튜플 등의 시퀀스를 순회하며 반복 작업을 수행하는 제어문입니다. 같은 작업을 여러 번 반복해야 할 때 효율적으로 코드를 작성할 수 있습니다.

## 학습 목표

- [ ] for 문을 사용하여 시퀀스를 순회할 수 있다
- [ ] range() 함수를 활용하여 반복을 제어할 수 있다
- [ ] enumerate()를 사용하여 인덱스와 값을 동시에 얻을 수 있다
- [ ] 중첩 반복문을 작성할 수 있다
- [ ] break와 continue의 역할을 이해한다

## for 문이란?

for 문은 반복 가능한(iterable) 객체의 요소를 하나씩 꺼내어 처리하는 반복문입니다. 리스트, 문자열, 튜플, 딕셔너리 등 순서가 있는 자료구조를 순회할 때 사용합니다.

기본 구조는 `for 변수 in 시퀀스:` 형태이며, 콜론(:) 뒤의 들여쓰기된 코드 블록이 각 요소마다 반복 실행됩니다. while 문과 달리 반복 횟수가 명확할 때 더 적합합니다.

## 기본 사용법

```python
# 리스트 순회
fruits = [''사과'', ''바나나'', ''오렌지'']
for fruit in fruits:
    print(fruit)
# 출력:
# 사과
# 바나나
# 오렌지
```

## 주요 예제

### 예제 1: range() 함수로 숫자 반복
```python
# range(stop): 0부터 stop-1까지
for i in range(5):
    print(i, end='' '')  # 0 1 2 3 4

# range(start, stop): start부터 stop-1까지
for i in range(2, 6):
    print(i, end='' '')  # 2 3 4 5

# range(start, stop, step): step 간격으로
for i in range(0, 10, 2):
    print(i, end='' '')  # 0 2 4 6 8
```

### 예제 2: enumerate()로 인덱스와 값 얻기
```python
colors = [''빨강'', ''파랑'', ''초록'']
for index, color in enumerate(colors):
    print(f"{index}: {color}")
# 출력:
# 0: 빨강
# 1: 파랑
# 2: 초록
```

### 예제 3: 중첩 for 문
```python
# 구구단 2단
for i in range(2, 4):
    for j in range(1, 4):
        print(f"{i} x {j} = {i*j}")
# 출력:
# 2 x 1 = 2
# 2 x 2 = 4
# 2 x 3 = 6
# 3 x 1 = 3
# 3 x 2 = 6
# 3 x 3 = 9
```

### 예제 4: break와 continue
```python
# break: 반복문 즉시 종료
for i in range(10):
    if i == 5:
        break
    print(i, end='' '')  # 0 1 2 3 4

print()

# continue: 현재 반복 건너뛰고 다음으로
for i in range(5):
    if i == 2:
        continue
    print(i, end='' '')  # 0 1 3 4
```

### 예제 5: zip()으로 여러 시퀀스 동시 순회
```python
names = [''철수'', ''영희'', ''민수'']
scores = [85, 92, 78]

for name, score in zip(names, scores):
    print(f"{name}: {score}점")
# 출력:
# 철수: 85점
# 영희: 92점
# 민수: 78점
```

## 주의사항

- ⚠️ range()는 stop 값을 포함하지 않습니다 (range(5)는 0~4까지)
- ⚠️ 반복 중 리스트를 수정하면 예상치 못한 결과가 발생할 수 있습니다
- 💡 enumerate()의 시작 인덱스는 start 매개변수로 변경 가능합니다 (enumerate(list, start=1))
- 💡 역순 반복은 range(10, 0, -1) 또는 reversed()를 사용하세요
- 💡 break와 continue는 가장 안쪽 반복문에만 영향을 줍니다

## 정리

for 문은 시퀀스의 요소를 순회하며 반복 작업을 수행하는 강력한 도구입니다. range(), enumerate(), zip() 등의 내장 함수와 함께 사용하면 더욱 효율적인 코드를 작성할 수 있습니다.

### 배운 내용
- ✅ for 문으로 리스트, 문자열 등 시퀀스를 순회할 수 있습니다
- ✅ range()로 숫자 범위를 생성하여 반복 횟수를 제어합니다
- ✅ enumerate()로 인덱스와 값을 동시에 얻을 수 있습니다
- ✅ 중첩 for 문으로 다차원 반복이 가능합니다
- ✅ break는 반복 종료, continue는 다음 반복으로 건너뜁니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('while 반복문', '조건에 따라 반복을 수행하는 while 문 사용법', '# while 반복문

while 문은 조건이 참인 동안 코드를 반복 실행하는 제어 구조입니다. 반복 횟수를 미리 알 수 없을 때 유용하게 사용됩니다.

## 학습 목표

- [ ] while 문의 구조와 실행 흐름을 이해한다
- [ ] 조건을 만족하는 동안 반복을 수행할 수 있다
- [ ] 무한 루프를 작성하고 제어할 수 있다
- [ ] for와 while의 사용 시나리오를 구분할 수 있다
- [ ] else 절과 함께 사용하는 방법을 이해한다

## while 문이란?

while 문은 조건식이 참(True)인 동안 코드 블록을 반복 실행합니다. for 문이 정해진 횟수나 시퀀스를 순회하는 것과 달리, while 문은 조건이 거짓이 될 때까지 계속 반복합니다.

사용자 입력을 받거나, 특정 조건이 만족될 때까지 작업을 수행하거나, 무한 루프를 만들어야 할 때 주로 사용합니다. while 문은 반복 전에 조건을 먼저 검사하므로, 처음부터 조건이 거짓이면 한 번도 실행되지 않습니다.

## 기본 사용법

```python
# 기본 구조
count = 1
while count <= 5:
    print(f"{count}번째 반복")
    count += 1  # 조건 변경 필수!

# 출력:
# 1번째 반복
# 2번째 반복
# 3번째 반복
# 4번째 반복
# 5번째 반복
```

조건을 변경하는 코드(count += 1)가 없으면 무한 루프에 빠지게 됩니다.

## 주요 예제

### 예제 1: 사용자 입력 받기
```python
# 올바른 입력을 받을 때까지 반복
password = ""
while password != "1234":
    password = input("비밀번호를 입력하세요: ")
    if password != "1234":
        print("틀렸습니다. 다시 시도하세요.")

print("비밀번호가 일치합니다!")
```

### 예제 2: 무한 루프와 break
```python
# 무한 루프 (조건이 항상 True)
while True:
    user_input = input("명령어 입력 (종료: q): ")

    if user_input == "q":
        print("프로그램을 종료합니다.")
        break  # 루프 탈출

    print(f"입력한 명령어: {user_input}")
```

### 예제 3: continue로 건너뛰기
```python
# 짝수만 출력
num = 0
while num < 10:
    num += 1
    if num % 2 != 0:
        continue  # 홀수는 건너뜀
    print(num)

# 출력: 2, 4, 6, 8, 10
```

### 예제 4: while-else 구문
```python
# 정상 종료 시 else 블록 실행
count = 0
while count < 3:
    print(f"카운트: {count}")
    count += 1
else:
    print("반복 정상 종료")  # break 없이 종료되면 실행

# break로 종료하면 else는 실행되지 않음
```

### 예제 5: for vs while 선택
```python
# for 사용이 적합한 경우 (반복 횟수가 명확)
for i in range(5):
    print(i)

# while 사용이 적합한 경우 (조건 기반 반복)
total = 0
while total < 100:
    num = int(input("숫자 입력: "))
    total += num
print(f"합계가 100을 넘었습니다: {total}")
```

## 주의사항

- 조건을 변경하는 코드를 반드시 포함해야 무한 루프를 방지할 수 있습니다
- 무한 루프에 빠졌을 때는 Ctrl+C로 강제 종료할 수 있습니다
- while True는 의도적인 무한 루프를 만들 때 사용하며, 반드시 break 조건을 포함해야 합니다
- 반복 횟수를 알 수 있다면 for 문을, 조건 기반 반복이라면 while 문을 사용하세요
- else 절은 break로 종료되지 않고 정상적으로 반복이 끝났을 때만 실행됩니다

## 정리

while 문은 조건이 참인 동안 코드를 반복하는 강력한 제어 구조입니다. 무한 루프와 break를 활용하면 유연한 프로그램 흐름을 만들 수 있습니다.

### 배운 내용
- while 문은 조건식이 True인 동안 반복을 수행합니다
- 무한 루프는 while True와 break를 조합하여 구현합니다
- continue는 현재 반복을 건너뛰고 다음 반복으로 이동합니다
- for는 횟수 기반, while은 조건 기반 반복에 적합합니다
- while-else는 break 없이 정상 종료될 때 else 블록을 실행합니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('함수 정의와 호출', 'def 키워드로 함수를 정의하고 호출하는 방법을 학습합니다', '# 함수 정의와 호출

함수는 특정 작업을 수행하는 코드 블록입니다. 반복되는 코드를 하나로 묶어 재사용할 수 있어 프로그램을 효율적으로 작성할 수 있습니다.

## 학습 목표

- [ ] 함수의 개념과 필요성을 이해한다
- [ ] def 키워드를 사용하여 함수를 정의할 수 있다
- [ ] 매개변수와 인자의 차이를 이해한다
- [ ] return 문을 사용하여 값을 반환할 수 있다
- [ ] 함수의 스코프(scope) 개념을 이해한다

## 함수란?

함수는 특정 기능을 수행하는 재사용 가능한 코드 블록입니다. 같은 코드를 여러 번 작성하는 대신, 함수로 정의하여 필요할 때마다 호출할 수 있습니다.

함수를 사용하면 코드의 중복을 줄이고, 가독성을 높이며, 유지보수가 쉬워집니다. 예를 들어, 인사말을 출력하는 코드를 여러 곳에서 사용한다면, 함수로 만들어 재사용하는 것이 효율적입니다.

## 기본 사용법

```python
# 가장 간단한 함수 정의
def greet():
    print("안녕하세요!")

# 함수 호출
greet()  # 출력: 안녕하세요!
```

`def` 키워드로 함수를 정의하고, 함수 이름 뒤에 괄호와 콜론을 붙입니다. 함수 내부 코드는 들여쓰기로 구분합니다.

## 주요 예제

### 예제 1: 매개변수가 있는 함수
```python
# 매개변수(parameter)를 받는 함수
def greet(name):
    print(f"{name}님, 안녕하세요!")

# 인자(argument)를 전달하여 호출
greet("철수")  # 출력: 철수님, 안녕하세요!
greet("영희")  # 출력: 영희님, 안녕하세요!
```

매개변수는 함수 정의 시 받는 변수이고, 인자는 함수 호출 시 전달하는 실제 값입니다.

### 예제 2: 값을 반환하는 함수
```python
# return으로 값을 반환
def add(a, b):
    result = a + b
    return result

# 반환값을 변수에 저장
total = add(5, 3)
print(total)  # 출력: 8
print(add(10, 20))  # 출력: 30
```

`return` 문은 함수의 실행을 종료하고 값을 반환합니다. 반환값이 없으면 `None`이 반환됩니다.

### 예제 3: 여러 값을 반환하는 함수
```python
# 튜플로 여러 값 반환
def calculate(a, b):
    add_result = a + b
    sub_result = a - b
    return add_result, sub_result

# 여러 값을 받기
sum_val, diff_val = calculate(10, 3)
print(f"합: {sum_val}, 차: {diff_val}")  # 출력: 합: 13, 차: 7
```

### 예제 4: 지역 변수와 전역 변수
```python
# 전역 변수
count = 0

def increment():
    # 지역 변수
    count = 1
    print(f"함수 내부: {count}")  # 출력: 함수 내부: 1

increment()
print(f"함수 외부: {count}")  # 출력: 함수 외부: 0
```

함수 내부에서 선언된 변수는 지역 변수로, 함수 외부에서는 접근할 수 없습니다.

### 예제 5: 기본값이 있는 매개변수
```python
# 기본값 설정
def greet(name, greeting="안녕하세요"):
    print(f"{name}님, {greeting}!")

greet("철수")  # 출력: 철수님, 안녕하세요!
greet("영희", "반갑습니다")  # 출력: 영희님, 반갑습니다!
```

## 주의사항

- 함수 이름은 소문자와 언더스코어를 사용하는 것이 권장됩니다 (예: `calculate_total`)
- `return` 문이 없으면 함수는 자동으로 `None`을 반환합니다
- 매개변수와 인자의 개수는 일치해야 합니다 (기본값이 있는 경우 제외)
- 함수 내부에서 전역 변수를 수정하려면 `global` 키워드가 필요합니다
- 함수는 정의된 후에 호출해야 합니다

## 정리

함수는 코드를 재사용 가능한 블록으로 만들어 프로그램을 효율적으로 작성하게 해줍니다. `def` 키워드로 정의하고, 매개변수로 값을 받아 처리한 후, `return`으로 결과를 반환할 수 있습니다.

### 배운 내용
- 함수는 `def` 키워드로 정의하고 이름()으로 호출합니다
- 매개변수는 함수 정의 시, 인자는 호출 시 사용하는 용어입니다
- `return` 문으로 값을 반환하며, 없으면 `None`이 반환됩니다
- 지역 변수는 함수 내부에서만 사용 가능하고, 전역 변수는 프로그램 전체에서 접근 가능합니다
- 기본값을 설정하여 선택적 매개변수를 만들 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('함수 매개변수 심화', '기본값, 키워드 인자, *args, **kwargs 활용법', '# 함수 매개변수 심화

함수의 매개변수를 유연하게 다루면 더 편리하고 재사용 가능한 코드를 작성할 수 있습니다. 기본값 설정, 키워드 인자, 가변 인자 등 다양한 방법을 배워봅시다.

## 학습 목표

- [ ] 기본 매개변수(default parameter)를 설정할 수 있다
- [ ] 키워드 인자(keyword argument)를 사용할 수 있다
- [ ] 가변 인자(*args, **kwargs)를 활용할 수 있다
- [ ] 매개변수 순서 규칙을 이해한다
- [ ] 함수 호출 시 인자 언패킹을 수행할 수 있다

## 기본 매개변수란?

기본 매개변수는 함수 호출 시 인자를 생략할 수 있도록 미리 기본값을 설정하는 기능입니다. 필수 매개변수와 선택적 매개변수를 구분하여 함수를 더 유연하게 만들 수 있습니다.

기본값은 함수 정의 시 `=` 기호를 사용하여 설정하며, 인자를 전달하지 않으면 기본값이 사용됩니다. 이를 통해 자주 사용하는 값을 기본값으로 설정하고, 필요할 때만 다른 값을 전달할 수 있습니다.

## 기본 사용법

```python
# 기본 매개변수 설정
def greet(name, greeting="안녕하세요"):
    print(f"{greeting}, {name}님!")

greet("철수")  # 안녕하세요, 철수님!
greet("영희", "반갑습니다")  # 반갑습니다, 영희님!
```

greeting 매개변수에 기본값을 설정했기 때문에 생략 가능합니다.

## 주요 예제

### 예제 1: 위치 인자와 키워드 인자

```python
def order_coffee(size, coffee_type, ice=True):
    temp = "아이스" if ice else "핫"
    print(f"{size} {temp} {coffee_type}")

# 위치 인자만 사용
order_coffee("라지", "아메리카노")  # 라지 아이스 아메리카노

# 키워드 인자 사용
order_coffee(coffee_type="라떼", size="톨", ice=False)  # 톨 핫 라떼
```

키워드 인자를 사용하면 순서에 관계없이 인자를 전달할 수 있습니다.

### 예제 2: 가변 위치 인자 (*args)

```python
def sum_all(*numbers):
    total = 0
    for num in numbers:
        total += num
    return total

print(sum_all(1, 2, 3))  # 6
print(sum_all(10, 20, 30, 40, 50))  # 150
```

`*args`는 여러 개의 위치 인자를 튜플로 받습니다.

### 예제 3: 가변 키워드 인자 (**kwargs)

```python
def print_info(**info):
    for key, value in info.items():
        print(f"{key}: {value}")

print_info(이름="김철수", 나이=25, 직업="개발자")
# 이름: 김철수
# 나이: 25
# 직업: 개발자
```

`**kwargs`는 여러 개의 키워드 인자를 딕셔너리로 받습니다.

### 예제 4: 매개변수 순서 규칙

```python
def complex_func(a, b, c=10, *args, d, **kwargs):
    print(f"일반: a={a}, b={b}")
    print(f"기본값: c={c}")
    print(f"가변 위치: args={args}")
    print(f"키워드 전용: d={d}")
    print(f"가변 키워드: kwargs={kwargs}")

complex_func(1, 2, 3, 4, 5, d=6, e=7, f=8)
# 일반: a=1, b=2
# 기본값: c=3
# 가변 위치: args=(4, 5)
# 키워드 전용: d=6
# 가변 키워드: kwargs={''e'': 7, ''f'': 8}
```

매개변수는 정해진 순서대로 작성해야 합니다.

### 예제 5: 인자 언패킹

```python
def calculate(x, y, z):
    return x + y + z

# 리스트 언패킹
numbers = [1, 2, 3]
print(calculate(*numbers))  # 6

# 딕셔너리 언패킹
params = {''x'': 10, ''y'': 20, ''z'': 30}
print(calculate(**params))  # 60
```

`*`와 `**`를 사용하여 시퀀스나 딕셔너리를 언패킹할 수 있습니다.

## 주의사항

- 기본 매개변수는 일반 매개변수 뒤에 위치해야 합니다
- 기본값으로 가변 객체(리스트, 딕셔너리)를 사용하면 예상치 못한 동작이 발생할 수 있습니다
- 매개변수 순서: 일반 → 기본값 → *args → 키워드 전용 → **kwargs
- 키워드 인자는 위치 인자보다 뒤에 작성해야 합니다
- `*args` 다음에 오는 매개변수는 반드시 키워드 인자로 전달해야 합니다

## 정리

함수 매개변수의 다양한 활용법을 익히면 더 유연하고 강력한 함수를 작성할 수 있습니다. 상황에 맞게 기본값, 가변 인자, 키워드 인자를 조합하여 사용하세요.

### 배운 내용

- 기본 매개변수로 선택적 인자를 만들 수 있습니다
- 키워드 인자를 사용하면 순서에 관계없이 인자를 전달할 수 있습니다
- *args로 가변 개수의 위치 인자를, **kwargs로 가변 개수의 키워드 인자를 받을 수 있습니다
- 매개변수는 정해진 순서 규칙을 따라야 합니다
- *, **를 사용하여 시퀀스와 딕셔너리를 언패킹할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('스코프와 네임스페이스', 'LEGB 규칙과 global/nonlocal 키워드로 변수 스코프 제어하기', '# 스코프와 네임스페이스

Python에서 변수를 사용할 때 "어디서 정의된 변수인가?"가 매우 중요합니다. 스코프는 변수가 접근 가능한 범위를 의미하며, 이를 이해하면 예상치 못한 오류를 방지할 수 있습니다.

## 학습 목표

- [ ] LEGB 규칙을 이해하고 변수 검색 순서를 설명할 수 있다
- [ ] global 키워드로 전역 변수를 수정할 수 있다
- [ ] nonlocal 키워드로 외부 함수 변수를 수정할 수 있다
- [ ] 클로저의 기본 개념을 이해하고 간단한 예제를 작성할 수 있다
- [ ] 스코프 관련 오류를 식별하고 해결할 수 있다

## 스코프란?

스코프(Scope)는 변수나 함수의 이름이 유효한 범위를 의미합니다. Python은 변수를 찾을 때 특정 순서로 검색하며, 이를 LEGB 규칙이라고 합니다.

네임스페이스(Namespace)는 이름과 객체를 매핑하는 공간입니다. 함수 내부, 모듈 전체, 내장 함수 등 각각의 네임스페이스가 있으며, Python은 필요한 이름을 이 네임스페이스들에서 순차적으로 찾습니다.

## LEGB 규칙

Python은 변수를 찾을 때 다음 순서로 검색합니다:
- **L**ocal: 함수 내부
- **E**nclosing: 중첩 함수의 외부 함수
- **G**lobal: 모듈 전역
- **B**uilt-in: Python 내장

```python
# LEGB 규칙 예제
x = "global"  # Global

def outer():
    x = "enclosing"  # Enclosing

    def inner():
        x = "local"  # Local
        print(f"Local: {x}")

    inner()
    print(f"Enclosing: {x}")

outer()
print(f"Global: {x}")
# 출력:
# Local: local
# Enclosing: enclosing
# Global: global
```

각 함수는 자신의 스코프에서 가장 가까운 `x`를 사용합니다.

## global 키워드

함수 내부에서 전역 변수를 수정하려면 `global` 키워드를 사용해야 합니다.

```python
count = 0  # 전역 변수

def increment():
    global count  # 전역 변수 사용 선언
    count += 1
    print(f"Count: {count}")

increment()  # Count: 1
increment()  # Count: 2
print(count)  # 2
```

`global` 없이 수정하면 오류가 발생합니다:

```python
score = 0

def add_score():
    score += 10  # UnboundLocalError!

# add_score()  # 오류 발생
```

## nonlocal 키워드

중첩 함수에서 외부 함수의 변수를 수정할 때 `nonlocal`을 사용합니다.

```python
def outer():
    count = 0  # 외부 함수 변수

    def inner():
        nonlocal count  # 외부 함수 변수 사용
        count += 1
        return count

    print(inner())  # 1
    print(inner())  # 2
    print(count)    # 2

outer()
```

`nonlocal`은 가장 가까운 외부 스코프의 변수를 찾습니다:

```python
def outer():
    x = "outer"

    def middle():
        x = "middle"

        def inner():
            nonlocal x  # middle의 x를 참조
            x = "inner"

        inner()
        print(x)  # inner

    middle()
    print(x)  # outer

outer()
```

## 주요 예제

### 예제 1: 카운터 함수 만들기
```python
def make_counter():
    count = 0  # Enclosing scope

    def counter():
        nonlocal count
        count += 1
        return count

    return counter

# 독립적인 카운터 생성
counter1 = make_counter()
counter2 = make_counter()

print(counter1())  # 1
print(counter1())  # 2
print(counter2())  # 1 (독립적)
```

이것이 클로저(Closure)입니다. 함수가 자신이 생성된 환경을 기억합니다.

### 예제 2: 설정 관리자
```python
# 전역 설정
config = {"debug": False, "timeout": 30}

def update_config(key, value):
    global config
    config[key] = value
    print(f"Updated: {key} = {value}")

def get_config(key):
    return config.get(key)  # 읽기만 하므로 global 불필요

update_config("debug", True)
print(get_config("debug"))  # True
```

### 예제 3: 스코프 확인하기
```python
x = "global"

def show_scope():
    # x를 읽기만 하면 global 불필요
    print(f"Reading x: {x}")

    # 새 로컬 변수 생성
    y = "local"
    print(f"Local y: {y}")

show_scope()
# print(y)  # NameError: y는 함수 외부에 없음
```

### 예제 4: 클로저로 곱셈 함수 만들기
```python
def make_multiplier(n):
    def multiply(x):
        return x * n  # n을 기억함
    return multiply

times_2 = make_multiplier(2)
times_5 = make_multiplier(5)

print(times_2(10))  # 20
print(times_5(10))  # 50
```

## 주의사항

- ⚠️ **global 남용 금지**: 전역 변수는 프로그램을 복잡하게 만듭니다. 가능하면 매개변수와 반환값을 사용하세요
- ⚠️ **수정 전 선언**: `global`이나 `nonlocal`은 변수를 사용하기 전에 선언해야 합니다
- ⚠️ **가변 객체 주의**: 리스트나 딕셔너리는 `global` 없이도 내용을 수정할 수 있지만, 새로운 객체를 할당하려면 `global`이 필요합니다
- 💡 **읽기 전용**: 변수를 읽기만 한다면 `global`이나 `nonlocal` 없이도 접근 가능합니다
- 💡 **클로저 활용**: 함수를 반환할 때 클로저를 활용하면 상태를 유지하는 함수를 만들 수 있습니다

## 정리

스코프는 변수가 접근 가능한 범위이며, Python은 LEGB 순서로 변수를 검색합니다. `global`과 `nonlocal` 키워드를 사용하면 외부 스코프의 변수를 수정할 수 있지만, 가능하면 매개변수와 반환값을 사용하는 것이 더 좋은 설계입니다.

### 배운 내용
- ✅ LEGB 규칙: Local → Enclosing → Global → Built-in 순서로 변수 검색
- ✅ `global` 키워드로 전역 변수를 함수 내에서 수정 가능
- ✅ `nonlocal` 키워드로 외부 함수의 변수를 중첩 함수에서 수정 가능
- ✅ 클로저는 함수가 자신이 생성된 환경(외부 변수)을 기억하는 것
- ✅ 변수를 읽기만 할 때는 키워드 없이 접근 가능
', NULL, NULL, 'MARKDOWN', 'Python', '기초', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('리스트 컴프리헨션', '리스트를 간결하고 효율적으로 생성하는 컴프리헨션 문법', '# 리스트 컴프리헨션

리스트 컴프리헨션은 기존 리스트나 반복 가능한 객체로부터 새로운 리스트를 간결하게 생성하는 Python의 강력한 문법입니다. 반복문과 조건문을 한 줄로 표현하여 코드의 가독성과 성능을 동시에 향상시킬 수 있습니다.

## 학습 목표

- [ ] 리스트 컴프리헨션의 구조를 이해하고 기본 형태를 작성할 수 있다
- [ ] 조건문을 포함한 컴프리헨션으로 데이터를 필터링할 수 있다
- [ ] 중첩 컴프리헨션을 활용하여 다차원 데이터를 처리할 수 있다
- [ ] 일반 반복문과 컴프리헨션의 성능 차이를 이해할 수 있다
- [ ] 가독성과 성능을 고려하여 적절한 방식을 선택할 수 있다

## 리스트 컴프리헨션이란?

리스트 컴프리헨션은 반복문과 조건문을 결합하여 새로운 리스트를 생성하는 간결한 방법입니다. 기존의 for 반복문과 append()를 사용하는 방식보다 코드가 짧고 읽기 쉬우며, 내부적으로 최적화되어 있어 성능도 우수합니다.

기본 구조는 `[표현식 for 항목 in 반복가능객체]` 형태로, 대괄호 안에 반복문과 생성할 값을 함께 작성합니다. 이는 함수형 프로그래밍의 map과 filter 개념을 Python스럽게 구현한 것으로, 데이터 변환과 필터링 작업에 매우 유용합니다.

## 기본 사용법

```python
# 일반 반복문 방식
numbers = []
for i in range(5):
    numbers.append(i * 2)
print(numbers)  # [0, 2, 4, 6, 8]

# 리스트 컴프리헨션 방식
numbers = [i * 2 for i in range(5)]
print(numbers)  # [0, 2, 4, 6, 8]
```

동일한 결과를 생성하지만, 컴프리헨션은 한 줄로 표현되어 더 간결합니다.

## 주요 예제

### 예제 1: 조건 필터링
```python
# 짝수만 제곱하기
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
even_squares = [n**2 for n in numbers if n % 2 == 0]
print(even_squares)  # [4, 16, 36, 64, 100]

# 문자열 리스트 필터링
words = [''hello'', ''world'', ''python'', ''code'', ''list'']
long_words = [word.upper() for word in words if len(word) > 4]
print(long_words)  # [''HELLO'', ''WORLD'', ''PYTHON'']
```

### 예제 2: if-else 조건문 포함
```python
# 짝수는 제곱, 홀수는 그대로
numbers = [1, 2, 3, 4, 5]
result = [n**2 if n % 2 == 0 else n for n in numbers]
print(result)  # [1, 4, 3, 16, 5]

# 점수 등급 변환
scores = [85, 92, 78, 95, 88]
grades = [''A'' if s >= 90 else ''B'' if s >= 80 else ''C'' for s in scores]
print(grades)  # [''B'', ''A'', ''C'', ''A'', ''B'']
```

### 예제 3: 중첩 컴프리헨션
```python
# 구구단 생성
multiplication_table = [[i * j for j in range(1, 4)] for i in range(2, 5)]
print(multiplication_table)
# [[2, 4, 6], [3, 6, 9], [4, 8, 12]]

# 2차원 리스트 평탄화
matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
flat_list = [num for row in matrix for num in row]
print(flat_list)  # [1, 2, 3, 4, 5, 6, 7, 8, 9]
```

### 예제 4: 실용적인 활용 사례
```python
# CSV 데이터 처리
data = "apple,banana,cherry,date"
fruits = [fruit.strip().title() for fruit in data.split('','')]
print(fruits)  # [''Apple'', ''Banana'', ''Cherry'', ''Date'']

# 좌표 생성
coordinates = [(x, y) for x in range(3) for y in range(3)]
print(coordinates)
# [(0,0), (0,1), (0,2), (1,0), (1,1), (1,2), (2,0), (2,1), (2,2)]
```

### 예제 5: 성능 비교
```python
import time

# 일반 반복문
start = time.time()
result1 = []
for i in range(100000):
    result1.append(i * 2)
time1 = time.time() - start

# 리스트 컴프리헨션
start = time.time()
result2 = [i * 2 for i in range(100000)]
time2 = time.time() - start

print(f"반복문: {time1:.4f}초")
print(f"컴프리헨션: {time2:.4f}초")
# 컴프리헨션이 약 30-40% 빠름
```

## 주의사항

- **가독성 우선**: 중첩이 2단계를 넘어가면 일반 반복문을 사용하는 것이 더 읽기 쉽습니다
- **메모리 사용**: 큰 데이터셋의 경우 제너레이터 표현식 `()` 사용을 고려하세요
- **조건문 위치**: 필터링은 `if`만 뒤에, 값 변환은 `if-else`를 앞에 작성합니다
- **디버깅**: 복잡한 로직은 일반 반복문으로 작성 후 컴프리헨션으로 변환하세요

## 정리

리스트 컴프리헨션은 Python에서 리스트를 생성하는 가장 효율적이고 Pythonic한 방법입니다. 기본 문법을 익히고 조건문과 중첩 활용법을 이해하면, 간결하면서도 성능이 우수한 코드를 작성할 수 있습니다.

### 배운 내용
- 리스트 컴프리헨션의 기본 구조: `[표현식 for 항목 in 반복객체]`
- 조건 필터링: `if`를 뒤에 추가하여 원하는 데이터만 선택
- 중첩 컴프리헨션: 다차원 데이터 처리와 평탄화에 활용
- 성능 장점: 일반 반복문보다 30-40% 빠른 실행 속도
- 가독성 고려: 복잡한 로직은 일반 반복문 사용 권장
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('딕셔너리와 집합 컴프리헨션', '딕셔너리와 집합을 간결하게 생성하는 컴프리헨션 문법', '# 딕셔너리와 집합 컴프리헨션

리스트 컴프리헨션처럼 딕셔너리와 집합도 컴프리헨션으로 간결하게 생성할 수 있습니다. 데이터 변환과 필터링이 필요한 실무에서 자주 사용됩니다.

## 학습 목표

- [ ] 딕셔너리 컴프리헨션을 작성할 수 있다
- [ ] 집합 컴프리헨션을 작성할 수 있다
- [ ] 키-값 쌍을 변환하는 컴프리헨션을 작성할 수 있다
- [ ] 실무 활용 사례를 이해한다

## 딕셔너리 컴프리헨션이란?

딕셔너리 컴프리헨션은 `{key: value for ...}` 형태로 딕셔너리를 생성하는 문법입니다. 기존 데이터로부터 새로운 딕셔너리를 만들 때 for 루프보다 간결하고 읽기 쉽습니다.

일반적인 for 루프로 딕셔너리를 만들면 여러 줄이 필요하지만, 컴프리헨션을 사용하면 한 줄로 표현할 수 있습니다. 특히 데이터 변환, 필터링, 키-값 쌍 생성 작업에서 효율적입니다.

## 기본 사용법

```python
# 숫자와 제곱 딕셔너리 생성
squares = {x: x**2 for x in range(1, 6)}
print(squares)
# 출력: {1: 1, 2: 4, 3: 9, 4: 16, 5: 25}

# 조건이 있는 딕셔너리 컴프리헨션
even_squares = {x: x**2 for x in range(1, 11) if x % 2 == 0}
print(even_squares)
# 출력: {2: 4, 4: 16, 6: 36, 8: 64, 10: 100}
```

중괄호 `{}` 안에 `key: value` 형태로 작성하고, for 문과 선택적으로 if 조건을 추가할 수 있습니다.

## 주요 예제

### 예제 1: zip()과 함께 사용하기
```python
# 두 리스트를 딕셔너리로 변환
names = [''Alice'', ''Bob'', ''Charlie'']
scores = [85, 92, 78]

score_dict = {name: score for name, score in zip(names, scores)}
print(score_dict)
# 출력: {''Alice'': 85, ''Bob'': 92, ''Charlie'': 78}
```

### 예제 2: 딕셔너리 키-값 변환
```python
# 기존 딕셔너리의 키와 값을 바꾸기
original = {''a'': 1, ''b'': 2, ''c'': 3}
reversed_dict = {value: key for key, value in original.items()}
print(reversed_dict)
# 출력: {1: ''a'', 2: ''b'', 3: ''c''}
```

### 예제 3: 집합 컴프리헨션
```python
# 중복 제거하며 집합 생성
numbers = [1, 2, 2, 3, 3, 3, 4, 5, 5]
unique_squares = {x**2 for x in numbers}
print(unique_squares)
# 출력: {1, 4, 9, 16, 25}

# 조건이 있는 집합 컴프리헨션
text = "Hello World"
vowels = {char.lower() for char in text if char.lower() in ''aeiou''}
print(vowels)
# 출력: {''e'', ''o''}
```

### 예제 4: 실전 데이터 변환
```python
# 상품 데이터에서 가격 인상
products = {''apple'': 1000, ''banana'': 500, ''orange'': 800}
increased = {name: price * 1.1 for name, price in products.items()}
print(increased)
# 출력: {''apple'': 1100.0, ''banana'': 550.0, ''orange'': 880.0}

# 특정 조건 필터링
cheap_products = {name: price for name, price in products.items() if price < 900}
print(cheap_products)
# 출력: {''banana'': 500, ''orange'': 800}
```

### 예제 5: 문자열 처리
```python
# 문자열 리스트를 길이 딕셔너리로
words = [''python'', ''java'', ''go'', ''rust'']
word_lengths = {word: len(word) for word in words}
print(word_lengths)
# 출력: {''python'': 6, ''java'': 4, ''go'': 2, ''rust'': 4}
```

## 주의사항

- 딕셔너리는 중괄호 `{key: value}`, 집합은 중괄호 `{value}` 형태입니다
- 딕셔너리 컴프리헨션에서 중복된 키가 있으면 마지막 값만 남습니다
- 집합은 자동으로 중복을 제거하고 순서를 보장하지 않습니다
- 너무 복잡한 로직은 일반 for 루프를 사용하는 것이 가독성에 좋습니다
- `dict.items()`로 키-값 쌍을 순회할 수 있습니다

## 정리

딕셔너리 컴프리헨션과 집합 컴프리헨션은 데이터를 변환하고 필터링하는 강력한 도구입니다. zip()과 함께 사용하면 여러 리스트를 딕셔너리로 쉽게 결합할 수 있고, items()를 사용하면 기존 딕셔너리를 변환할 수 있습니다.

### 배운 내용
- 딕셔너리 컴프리헨션: `{key: value for item in iterable}`
- 집합 컴프리헨션: `{expr for item in iterable}`
- zip()과 items()를 활용한 데이터 변환
- 조건문을 추가한 필터링 기법
- 실무에서 자주 사용하는 패턴들
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('map과 filter 함수', 'map과 filter로 데이터를 효율적으로 변환하고 필터링하기', '# map과 filter 함수

함수형 프로그래밍의 핵심 도구인 map과 filter는 데이터를 간결하게 변환하고 필터링할 수 있게 해줍니다. 반복문 없이도 컬렉션 전체를 처리할 수 있습니다.

## 학습 목표

- [ ] map() 함수를 사용하여 iterable의 모든 요소를 변환할 수 있다
- [ ] filter() 함수를 사용하여 조건에 맞는 요소를 필터링할 수 있다
- [ ] lambda 함수와 결합하여 사용할 수 있다
- [ ] 컴프리헨션과 map/filter의 차이를 이해한다
- [ ] 여러 iterable을 처리할 수 있다

## map() 함수란?

map() 함수는 iterable의 모든 요소에 함수를 적용하여 새로운 iterator를 반환합니다. 반복문 없이 데이터를 일괄 변환할 수 있어 코드가 간결해집니다.

`map(function, iterable)`의 형태로 사용하며, 결과는 map 객체로 반환되므로 list()나 tuple()로 변환하여 사용합니다. 원본 데이터는 변경되지 않습니다.

## map() 기본 사용법

```python
# 모든 숫자를 제곱하기
numbers = [1, 2, 3, 4, 5]
squared = list(map(lambda x: x**2, numbers))
print(squared)  # [1, 4, 9, 16, 25]

# 문자열을 정수로 변환
str_nums = [''1'', ''2'', ''3'', ''4'', ''5'']
int_nums = list(map(int, str_nums))
print(int_nums)  # [1, 2, 3, 4, 5]
```

첫 번째 예제는 lambda 함수로 각 숫자를 제곱하고, 두 번째는 내장 함수 int를 사용하여 문자열을 정수로 변환합니다.

## filter() 함수란?

filter() 함수는 조건 함수를 통과한 요소만 선택하여 새로운 iterator를 반환합니다. 조건에 맞는 데이터만 추출할 때 유용합니다.

`filter(function, iterable)` 형태로 사용하며, function이 True를 반환하는 요소만 포함됩니다. map과 마찬가지로 결과는 filter 객체이므로 list()로 변환하여 사용합니다.

## filter() 기본 사용법

```python
# 짝수만 필터링
numbers = [1, 2, 3, 4, 5, 6, 7, 8]
evens = list(filter(lambda x: x % 2 == 0, numbers))
print(evens)  # [2, 4, 6, 8]

# 빈 문자열 제거
words = [''hello'', '''', ''world'', '''', ''python'']
filtered = list(filter(None, words))
print(filtered)  # [''hello'', ''world'', ''python'']
```

첫 번째는 lambda로 조건을 정의하고, 두 번째는 None을 전달하여 falsy 값을 자동으로 제거합니다.

## 주요 예제

### 예제 1: 여러 iterable 동시 처리
```python
# 두 리스트의 요소를 더하기
a = [1, 2, 3, 4]
b = [10, 20, 30, 40]
result = list(map(lambda x, y: x + y, a, b))
print(result)  # [11, 22, 33, 44]
```

map()은 여러 iterable을 받을 수 있으며, 함수의 매개변수 개수와 일치해야 합니다.

### 예제 2: map과 filter 조합
```python
# 짝수만 선택하여 제곱하기
numbers = [1, 2, 3, 4, 5, 6]
result = list(map(lambda x: x**2, filter(lambda x: x % 2 == 0, numbers)))
print(result)  # [4, 16, 36]
```

filter로 짝수만 선택한 후, map으로 제곱합니다. 함수 체이닝이 가능합니다.

### 예제 3: 사용자 정의 함수 활용
```python
# 문자열 길이가 5 이상인 것만 대문자로 변환
def to_upper(s):
    return s.upper()

words = [''hi'', ''hello'', ''world'', ''python'', ''map'']
long_words = filter(lambda x: len(x) >= 5, words)
result = list(map(to_upper, long_words))
print(result)  # [''HELLO'', ''WORLD'', ''PYTHON'']
```

lambda 대신 일반 함수도 사용 가능하며, 복잡한 로직은 함수로 분리하는 것이 좋습니다.

### 예제 4: 컴프리헨션 vs map/filter
```python
numbers = [1, 2, 3, 4, 5, 6]

# map/filter 방식
result1 = list(map(lambda x: x**2, filter(lambda x: x % 2 == 0, numbers)))

# 리스트 컴프리헨션 방식
result2 = [x**2 for x in numbers if x % 2 == 0]

print(result1)  # [4, 16, 36]
print(result2)  # [4, 16, 36]
```

같은 결과를 얻을 수 있지만, 리스트 컴프리헨션이 더 직관적이고 Python스러운 코드입니다.

### 예제 5: 실용적인 데이터 처리
```python
# 학생 점수 데이터 처리
students = [
    {''name'': ''Alice'', ''score'': 85},
    {''name'': ''Bob'', ''score'': 92},
    {''name'': ''Charlie'', ''score'': 78},
    {''name'': ''David'', ''score'': 95}
]

# 90점 이상인 학생의 이름만 추출
high_scorers = map(lambda s: s[''name''],
                   filter(lambda s: s[''score''] >= 90, students))
print(list(high_scorers))  # [''Bob'', ''David'']
```

실제 데이터 처리에서 조건에 맞는 항목을 선택하고 필요한 값만 추출할 때 유용합니다.

## 주의사항

- ⚠️ map과 filter는 iterator를 반환하므로 list()로 변환하지 않으면 재사용할 수 없습니다
- ⚠️ 여러 iterable을 사용할 때는 가장 짧은 길이에 맞춰집니다
- 💡 간단한 경우 리스트 컴프리헨션이 더 읽기 쉽고 빠를 수 있습니다
- 💡 복잡한 lambda는 피하고 일반 함수로 정의하는 것이 좋습니다
- 💡 filter(None, iterable)은 falsy 값(0, '''', False, None 등)을 모두 제거합니다

## 정리

map과 filter는 함수형 프로그래밍의 기본 도구로, 데이터 변환과 필터링을 선언적으로 표현할 수 있게 해줍니다. lambda와 결합하면 간결한 코드 작성이 가능하지만, 복잡한 경우 리스트 컴프리헨션이나 일반 함수를 사용하는 것이 더 읽기 좋습니다.

### 배운 내용
- ✅ map()으로 모든 요소에 함수를 적용하여 변환
- ✅ filter()로 조건에 맞는 요소만 선택
- ✅ lambda와 결합하여 간결한 코드 작성
- ✅ 여러 iterable을 동시에 처리
- ✅ 리스트 컴프리헨션과의 차이점 이해
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('lambda와 reduce', '익명 함수 lambda와 누적 연산 reduce 활용법', '# lambda와 reduce

lambda는 이름 없는 익명 함수로, 간단한 함수를 한 줄로 표현할 때 사용합니다. reduce는 시퀀스의 요소들을 누적하여 하나의 값으로 만드는 함수형 프로그래밍 도구입니다.

## 학습 목표

- [ ] lambda 함수의 문법과 제약사항을 이해한다
- [ ] reduce() 함수를 사용하여 누적 연산을 수행할 수 있다
- [ ] lambda를 활용한 정렬을 수행할 수 있다
- [ ] 함수형 프로그래밍의 기본 개념을 이해한다
- [ ] lambda vs 일반 함수의 선택 기준을 이해한다

## lambda 함수란?

lambda는 이름이 없는 익명 함수(anonymous function)입니다. 간단한 함수를 한 줄로 정의할 수 있어, 일회성으로 사용하거나 다른 함수의 인자로 전달할 때 유용합니다.

lambda의 기본 문법은 `lambda 매개변수: 표현식` 형태입니다. 표현식의 결과가 자동으로 반환되며, return 키워드는 사용할 수 없습니다. 여러 매개변수를 받을 수 있지만, 표현식은 단 하나만 가능합니다.

lambda는 함수형 프로그래밍 스타일을 지원하며, map(), filter(), sorted() 등과 함께 사용될 때 코드를 간결하게 만들어줍니다. 다만 복잡한 로직에는 적합하지 않으므로, 간단한 연산에만 사용하는 것이 좋습니다.

## 기본 사용법

```python
# 일반 함수와 lambda 함수 비교
def add(x, y):
    return x + y

add_lambda = lambda x, y: x + y

print(add(3, 5))         # 8
print(add_lambda(3, 5))  # 8
```

lambda 함수는 변수에 할당할 수 있지만, 주로 다른 함수의 인자로 직접 전달하는 방식으로 사용됩니다.

## 주요 예제

### 예제 1: sorted()와 lambda
```python
# 튜플 리스트를 두 번째 요소로 정렬
students = [(''Alice'', 85), (''Bob'', 92), (''Charlie'', 78)]
sorted_students = sorted(students, key=lambda x: x[1])
print(sorted_students)
# [(''Charlie'', 78), (''Alice'', 85), (''Bob'', 92)]

# 딕셔너리 리스트 정렬
products = [
    {''name'': ''노트북'', ''price'': 1200000},
    {''name'': ''마우스'', ''price'': 30000},
    {''name'': ''키보드'', ''price'': 80000}
]
by_price = sorted(products, key=lambda x: x[''price''])
print(by_price[0][''name''])  # 마우스
```

### 예제 2: max(), min()과 lambda
```python
# 가장 긴 문자열 찾기
words = [''python'', ''java'', ''javascript'', ''go'']
longest = max(words, key=lambda x: len(x))
print(longest)  # javascript

# 절댓값이 가장 큰 숫자 찾기
numbers = [-10, 5, -20, 15]
max_abs = max(numbers, key=lambda x: abs(x))
print(max_abs)  # -20
```

### 예제 3: reduce()로 누적 연산
```python
from functools import reduce

# 모든 숫자의 곱 계산
numbers = [1, 2, 3, 4, 5]
product = reduce(lambda x, y: x * y, numbers)
print(product)  # 120

# 문자열 합치기
words = [''Python'', ''is'', ''awesome'']
sentence = reduce(lambda x, y: x + '' '' + y, words)
print(sentence)  # Python is awesome

# 초기값 사용
numbers = [1, 2, 3]
result = reduce(lambda x, y: x + y, numbers, 10)
print(result)  # 16 (10 + 1 + 2 + 3)
```

### 예제 4: filter()와 lambda
```python
# 짝수만 필터링
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
evens = list(filter(lambda x: x % 2 == 0, numbers))
print(evens)  # [2, 4, 6, 8, 10]

# 길이가 5 이상인 단어만 선택
words = [''cat'', ''elephant'', ''dog'', ''python'']
long_words = list(filter(lambda x: len(x) >= 5, words))
print(long_words)  # [''elephant'', ''python'']
```

### 예제 5: map()과 lambda
```python
# 모든 숫자를 제곱
numbers = [1, 2, 3, 4, 5]
squared = list(map(lambda x: x**2, numbers))
print(squared)  # [1, 4, 9, 16, 25]

# 여러 리스트 동시 처리
a = [1, 2, 3]
b = [10, 20, 30]
result = list(map(lambda x, y: x + y, a, b))
print(result)  # [11, 22, 33]
```

## 주의사항

- lambda는 단일 표현식만 가능합니다. 여러 줄의 코드나 복잡한 로직에는 일반 함수를 사용하세요
- lambda 내부에서는 할당문(=)이나 제어문(if, for, while)을 사용할 수 없습니다
- 조건 표현식은 가능합니다: `lambda x: ''even'' if x % 2 == 0 else ''odd''`
- 가독성이 떨어진다면 일반 함수로 작성하는 것이 좋습니다
- reduce()는 functools 모듈에서 import해야 합니다
- reduce()보다는 sum(), max() 등 내장 함수가 더 명확할 수 있습니다

## 정리

lambda는 간단한 함수를 한 줄로 표현하는 익명 함수이며, reduce()는 누적 연산을 수행하는 함수형 프로그래밍 도구입니다. sorted(), max(), filter() 등과 함께 사용하면 코드를 간결하게 작성할 수 있지만, 복잡한 로직에는 일반 함수를 사용하는 것이 가독성에 유리합니다.

### 배운 내용
- lambda 함수는 `lambda 매개변수: 표현식` 형태로 작성합니다
- sorted()의 key 매개변수에 lambda를 사용하여 정렬 기준을 지정할 수 있습니다
- reduce()는 시퀀스의 요소를 누적하여 하나의 값으로 만듭니다
- filter()와 map()에서 lambda를 사용하여 간결한 데이터 변환이 가능합니다
- 가독성이 중요한 경우 lambda보다 일반 함수를 사용하세요
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('파일 읽기와 쓰기', 'open()으로 파일 입출력하고 with문으로 안전하게 처리하기', '# 파일 읽기와 쓰기

프로그램에서 데이터를 영구적으로 저장하거나 외부 데이터를 읽어오려면 파일 입출력이 필요합니다. Python은 open() 함수와 다양한 메서드를 제공하여 파일을 쉽게 다룰 수 있습니다.

## 학습 목표

- [ ] open() 함수를 사용하여 파일을 열 수 있다
- [ ] 파일 모드(r, w, a, x)를 이해하고 적절히 선택할 수 있다
- [ ] 파일을 읽는 다양한 방법을 활용할 수 있다
- [ ] with 문을 사용하여 안전하게 파일을 처리할 수 있다
- [ ] 텍스트 파일과 바이너리 파일의 차이를 이해한다

## 파일 열기: open() 함수

open() 함수는 파일을 열어 파일 객체를 반환합니다. 첫 번째 인자는 파일 경로, 두 번째 인자는 파일 모드입니다.

```python
# 파일 열기 기본 형식
file = open(''example.txt'', ''r'')
content = file.read()
file.close()  # 반드시 닫아야 함
print(content)
```

파일을 연 후에는 반드시 close()로 닫아야 시스템 리소스가 해제됩니다.

## 파일 모드

파일을 어떻게 다룰지 결정하는 모드입니다:

| 모드 | 설명 | 파일이 없을 때 | 파일이 있을 때 |
|------|------|----------------|----------------|
| `r` | 읽기 (기본값) | 오류 발생 | 읽기 |
| `w` | 쓰기 | 새로 생성 | 내용 삭제 후 쓰기 |
| `a` | 추가 | 새로 생성 | 끝에 추가 |
| `x` | 배타적 생성 | 새로 생성 | 오류 발생 |
| `b` | 바이너리 모드 | - | - |
| `t` | 텍스트 모드 (기본값) | - | - |

모드는 조합 가능합니다: `''rb''` (바이너리 읽기), `''w+''` (읽기/쓰기)

## 파일 읽기 메서드

### read() - 전체 또는 n바이트 읽기

```python
# 전체 내용 읽기
with open(''data.txt'', ''r'', encoding=''utf-8'') as f:
    content = f.read()
    print(content)

# 처음 10자만 읽기
with open(''data.txt'', ''r'', encoding=''utf-8'') as f:
    partial = f.read(10)
    print(partial)
```

### readline() - 한 줄씩 읽기

```python
# 한 줄씩 읽기
with open(''data.txt'', ''r'', encoding=''utf-8'') as f:
    line1 = f.readline()
    line2 = f.readline()
    print(line1, line2)
```

### readlines() - 모든 줄을 리스트로 읽기

```python
# 모든 줄을 리스트로 읽기
with open(''data.txt'', ''r'', encoding=''utf-8'') as f:
    lines = f.readlines()
    for line in lines:
        print(line.strip())  # 줄바꿈 제거
```

## 파일 쓰기 메서드

### write() - 문자열 쓰기

```python
# 파일에 쓰기 (덮어쓰기)
with open(''output.txt'', ''w'', encoding=''utf-8'') as f:
    f.write(''첫 번째 줄\n'')
    f.write(''두 번째 줄\n'')
```

### writelines() - 여러 줄 쓰기

```python
# 리스트의 내용을 파일에 쓰기
lines = [''Apple\n'', ''Banana\n'', ''Cherry\n'']
with open(''fruits.txt'', ''w'', encoding=''utf-8'') as f:
    f.writelines(lines)
```

## with 문과 컨텍스트 매니저

with 문을 사용하면 파일이 자동으로 닫히므로 close()를 호출할 필요가 없습니다.

```python
# with 문 사용 (권장)
with open(''example.txt'', ''r'', encoding=''utf-8'') as f:
    content = f.read()
    print(content)
# 블록을 벗어나면 자동으로 파일이 닫힘
```

예외가 발생해도 파일이 안전하게 닫히므로 항상 with 문을 사용하는 것이 좋습니다.

## 주요 예제

### 예제 1: 로그 파일에 추가하기

```python
# 기존 내용을 유지하고 끝에 추가
with open(''log.txt'', ''a'', encoding=''utf-8'') as f:
    f.write(''2025-01-15: 시스템 시작\n'')
    f.write(''2025-01-15: 작업 완료\n'')
```

### 예제 2: 파일 한 줄씩 처리하기

```python
# 메모리 효율적으로 큰 파일 읽기
with open(''large_file.txt'', ''r'', encoding=''utf-8'') as f:
    for line in f:  # 한 줄씩 읽기
        if ''ERROR'' in line:
            print(line.strip())
```

### 예제 3: 파일 복사하기

```python
# 텍스트 파일 복사
with open(''source.txt'', ''r'', encoding=''utf-8'') as src:
    with open(''backup.txt'', ''w'', encoding=''utf-8'') as dst:
        dst.write(src.read())
```

### 예제 4: 바이너리 파일 다루기

```python
# 이미지 파일 복사
with open(''photo.jpg'', ''rb'') as src:
    with open(''photo_copy.jpg'', ''wb'') as dst:
        dst.write(src.read())
```

### 예제 5: 파일 존재 여부 확인 후 쓰기

```python
# x 모드로 안전하게 새 파일 생성
try:
    with open(''new_file.txt'', ''x'', encoding=''utf-8'') as f:
        f.write(''새로운 파일입니다.'')
    print(''파일이 생성되었습니다.'')
except FileExistsError:
    print(''파일이 이미 존재합니다.'')
```

## 파일 인코딩

텍스트 파일을 다룰 때는 인코딩을 명시하는 것이 좋습니다.

```python
# UTF-8 인코딩 명시 (권장)
with open(''korean.txt'', ''r'', encoding=''utf-8'') as f:
    content = f.read()

# 다른 인코딩
with open(''old_file.txt'', ''r'', encoding=''cp949'') as f:
    content = f.read()
```

## 주의사항

- ⚠️ 파일을 열었으면 반드시 닫아야 합니다 (with 문 사용 권장)
- ⚠️ ''w'' 모드는 기존 파일 내용을 완전히 삭제하므로 주의하세요
- ⚠️ 한글이 포함된 파일은 encoding=''utf-8''을 명시하세요
- 💡 큰 파일은 read() 대신 for 루프로 한 줄씩 읽으세요
- 💡 바이너리 파일(이미지, 영상)은 ''b'' 모드를 사용하세요
- 💡 파일이 없을 때 오류를 피하려면 try-except를 사용하세요

## 정리

Python의 파일 입출력은 open() 함수와 with 문을 중심으로 이루어집니다. 파일 모드를 정확히 이해하고 with 문을 사용하면 안전하고 효율적인 파일 처리가 가능합니다.

### 배운 내용
- ✅ open() 함수로 파일을 열고, 모드로 동작을 제어합니다
- ✅ read(), readline(), readlines()로 다양한 방식으로 읽을 수 있습니다
- ✅ write(), writelines()로 파일에 데이터를 씁니다
- ✅ with 문을 사용하면 자동으로 파일이 닫힙니다
- ✅ 텍스트 파일은 인코딩을, 바이너리 파일은 ''b'' 모드를 사용합니다
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('예외 처리 - try, except, finally', '예외를 처리하여 안정적인 프로그램 작성하기', '# 예외 처리 - try, except, finally

프로그램 실행 중 예상치 못한 오류가 발생하면 프로그램이 중단됩니다. 예외 처리를 사용하면 이러한 오류 상황에 대응하여 프로그램을 안정적으로 실행할 수 있습니다.

## 학습 목표

- [ ] 예외(exception)의 개념을 이해한다
- [ ] try-except 문을 사용하여 예외를 처리할 수 있다
- [ ] 여러 예외를 구분하여 처리할 수 있다
- [ ] finally 절의 역할을 이해한다
- [ ] raise 문을 사용하여 예외를 발생시킬 수 있다

## 예외(Exception)란?

예외는 프로그램 실행 중 발생하는 오류 상황을 나타내는 객체입니다. 문법 오류와 달리 실행 시점에 발생하며, 적절히 처리하지 않으면 프로그램이 종료됩니다.

Python은 다양한 내장 예외를 제공합니다:
- **ValueError**: 부적절한 값
- **TypeError**: 부적절한 타입
- **IndexError**: 잘못된 인덱스
- **KeyError**: 존재하지 않는 키
- **FileNotFoundError**: 파일을 찾을 수 없음
- **ZeroDivisionError**: 0으로 나누기

## 기본 try-except 구조

```python
# 예외 발생 가능한 코드
try:
    number = int(input("숫자 입력: "))
    print(f"입력한 숫자: {number}")
except ValueError:
    print("올바른 숫자를 입력하세요")
```

try 블록의 코드를 실행하다가 예외가 발생하면, except 블록이 실행됩니다.

## 주요 예제

### 예제 1: 기본 예외 처리
```python
# 나누기 연산 예외 처리
try:
    num1 = 10
    num2 = 0
    result = num1 / num2
except ZeroDivisionError:
    print("0으로 나눌 수 없습니다")
    result = None

print(f"결과: {result}")
# 출력: 0으로 나눌 수 없습니다
#      결과: None
```

### 예제 2: 여러 예외 처리
```python
# 리스트와 딕셔너리 예외 처리
data = [1, 2, 3]
info = {"name": "Alice"}

try:
    print(data[5])  # IndexError
    print(info["age"])  # KeyError
except IndexError:
    print("인덱스 범위 초과")
except KeyError as e:
    print(f"키를 찾을 수 없음: {e}")
```

### 예제 3: 예외 정보 활용
```python
# 예외 객체 정보 사용
try:
    value = int("abc")
except ValueError as e:
    print(f"변환 오류: {e}")
    print(f"오류 타입: {type(e).__name__}")
# 출력: 변환 오류: invalid literal for int()...
#      오류 타입: ValueError
```

### 예제 4: else와 finally 절
```python
# 파일 처리 예제
try:
    file = open("data.txt", "r")
    content = file.read()
except FileNotFoundError:
    print("파일을 찾을 수 없습니다")
else:
    print(f"파일 읽기 성공: {len(content)}자")
finally:
    print("작업 완료")
```

**else**: 예외가 발생하지 않았을 때 실행
**finally**: 예외 발생 여부와 관계없이 항상 실행

### 예제 5: raise로 예외 발생
```python
# 사용자 정의 예외 발생
def check_age(age):
    if age < 0:
        raise ValueError("나이는 0 이상이어야 합니다")
    if age < 19:
        raise Exception("미성년자입니다")
    return True

try:
    check_age(-5)
except ValueError as e:
    print(f"값 오류: {e}")
except Exception as e:
    print(f"일반 오류: {e}")
```

## 주의사항

- ⚠️ **너무 광범위한 예외 처리 지양**: `except Exception`보다 구체적인 예외 타입을 사용하세요
- ⚠️ **빈 except 블록 지양**: 예외를 무시하면 디버깅이 어려워집니다
- 💡 **as 키워드로 예외 정보 활용**: 예외 객체를 변수에 저장하여 세부 정보를 확인할 수 있습니다
- 💡 **finally는 리소스 정리에 활용**: 파일, 네트워크 연결 등을 안전하게 종료할 때 사용합니다
- 💡 **적절한 예외 선택**: raise 시 상황에 맞는 예외 타입을 선택하세요

## 정리

예외 처리는 예상치 못한 오류로부터 프로그램을 보호하는 핵심 기법입니다. try-except로 오류를 감지하고, finally로 정리 작업을 수행하며, raise로 의도적인 예외를 발생시킬 수 있습니다.

### 배운 내용
- ✅ 예외는 실행 중 발생하는 오류를 나타내는 객체입니다
- ✅ try-except 문으로 예외를 처리하여 프로그램 안정성을 높일 수 있습니다
- ✅ 여러 except 절로 예외 타입별 대응이 가능합니다
- ✅ finally 절은 예외와 관계없이 항상 실행되어 정리 작업에 유용합니다
- ✅ raise 문으로 필요에 따라 예외를 발생시킬 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('사용자 정의 예외', '나만의 예외 클래스를 만들어 더 명확한 에러 처리하기', '# 사용자 정의 예외

내장 예외만으로는 부족할 때, 나만의 예외를 만들어 더 명확하고 구체적인 에러 처리를 할 수 있습니다.

## 학습 목표

- [ ] Exception 클래스를 상속하여 사용자 정의 예외를 생성할 수 있다
- [ ] 예외 계층 구조를 이해하고 적절한 상속 관계를 설계할 수 있다
- [ ] 예외에 추가 정보를 포함시켜 더 풍부한 에러 정보를 제공할 수 있다
- [ ] raise from을 사용하여 예외 체이닝을 구현할 수 있다
- [ ] 적절한 예외 처리 전략을 수립하고 적용할 수 있다

## 사용자 정의 예외란?

사용자 정의 예외는 프로그래머가 직접 만드는 예외 클래스입니다. `ValueError`, `TypeError` 같은 내장 예외는 일반적인 상황에 사용되지만, 특정 도메인이나 비즈니스 로직에 맞는 예외가 필요할 때 사용자 정의 예외를 만듭니다.

예를 들어, 은행 애플리케이션에서는 `InsufficientBalanceError`(잔액 부족), 쇼핑몰에서는 `OutOfStockError`(재고 부족) 같은 명확한 이름의 예외를 사용하면 코드의 의도가 분명해집니다.

사용자 정의 예외는 `Exception` 클래스를 상속받아 만들며, 추가 속성이나 메서드를 포함할 수 있어 더 풍부한 에러 정보를 제공할 수 있습니다.

## 기본 사용법

```python
# 가장 간단한 사용자 정의 예외
class InvalidAgeError(Exception):
    pass

def set_age(age):
    if age < 0:
        raise InvalidAgeError("나이는 음수일 수 없습니다")
    return age

try:
    set_age(-5)
except InvalidAgeError as e:
    print(f"에러 발생: {e}")  # 에러 발생: 나이는 음수일 수 없습니다
```

## 주요 예제

### 예제 1: 추가 정보를 포함한 예외

```python
class InsufficientBalanceError(Exception):
    def __init__(self, balance, amount):
        self.balance = balance
        self.amount = amount
        message = f"잔액 부족: {balance}원 (필요: {amount}원)"
        super().__init__(message)

try:
    raise InsufficientBalanceError(10000, 50000)
except InsufficientBalanceError as e:
    print(e)  # 잔액 부족: 10000원 (필요: 50000원)
    print(f"부족 금액: {e.amount - e.balance}원")  # 부족 금액: 40000원
```

### 예제 2: 예외 계층 구조

```python
# 기본 예외 클래스
class ValidationError(Exception):
    pass

# 구체적인 예외들
class EmailValidationError(ValidationError):
    pass

class PasswordValidationError(ValidationError):
    pass

def validate_email(email):
    if "@" not in email:
        raise EmailValidationError("이메일에 @가 없습니다")

try:
    validate_email("user.com")
except ValidationError as e:  # 상위 클래스로 처리 가능
    print(f"검증 실패: {e}")  # 검증 실패: 이메일에 @가 없습니다
```

### 예제 3: 예외 체이닝

```python
class DataProcessError(Exception):
    pass

def process_data(data):
    try:
        return int(data) * 2
    except ValueError as e:
        # 원래 예외 정보 보존
        raise DataProcessError(f"데이터 처리 실패: {data}") from e

try:
    process_data("abc")
except DataProcessError as e:
    print(f"에러: {e}")  # 에러: 데이터 처리 실패: abc
    print(f"원인: {e.__cause__}")  # 원인: invalid literal for int()...
```

### 예제 4: 실전 활용 - 상품 재고 관리

```python
class OutOfStockError(Exception):
    def __init__(self, product, requested, available):
        self.product = product
        self.requested = requested
        self.available = available
        super().__init__(
            f"{product}: {requested}개 요청, {available}개만 재고 있음"
        )

class Product:
    def __init__(self, name, stock):
        self.name = name
        self.stock = stock

    def order(self, quantity):
        if quantity > self.stock:
            raise OutOfStockError(self.name, quantity, self.stock)
        self.stock -= quantity
        return f"{self.name} {quantity}개 주문 완료"

# 사용 예
product = Product("노트북", 5)
try:
    print(product.order(10))
except OutOfStockError as e:
    print(f"주문 실패: {e}")
    print(f"재주문 가능 수량: {e.available}개")
```

### 예제 5: 복합 예외 처리 전략

```python
class ConfigError(Exception):
    """설정 관련 기본 예외"""
    pass

class MissingConfigError(ConfigError):
    """필수 설정 누락"""
    pass

class InvalidConfigError(ConfigError):
    """설정 값 오류"""
    pass

def load_config(config_dict):
    if "host" not in config_dict:
        raise MissingConfigError("host 설정이 필요합니다")

    if not isinstance(config_dict["host"], str):
        raise InvalidConfigError("host는 문자열이어야 합니다")

    return config_dict

# 계층적 예외 처리
try:
    config = load_config({"port": 8080})
except MissingConfigError as e:
    print(f"설정 누락: {e}")  # 설정 누락: host 설정이 필요합니다
except InvalidConfigError as e:
    print(f"설정 오류: {e}")
except ConfigError as e:
    print(f"기타 설정 문제: {e}")
```

## 주의사항

- ⚠️ **Exception 상속**: 항상 `Exception` 또는 그 하위 클래스를 상속하세요. `BaseException`은 시스템 종료 등에 사용됩니다
- ⚠️ **명확한 이름**: 예외 이름은 `~Error` 또는 `~Exception`으로 끝나게 하여 예외임을 명확히 하세요
- ⚠️ **과도한 세분화 금지**: 너무 많은 예외 클래스를 만들면 오히려 복잡해집니다. 필요한 만큼만 만드세요
- 💡 **docstring 작성**: 예외 클래스에 언제 발생하는지 설명하는 docstring을 추가하면 좋습니다
- 💡 **예외 체이닝 활용**: `raise ... from`을 사용하면 원래 에러 정보를 보존할 수 있습니다

## 정리

사용자 정의 예외를 사용하면 도메인에 특화된 명확한 에러 처리가 가능합니다. Exception 클래스를 상속하여 만들고, 필요한 경우 추가 정보를 속성으로 포함시킬 수 있습니다. 예외 계층 구조를 잘 설계하면 유연한 에러 처리가 가능하며, 예외 체이닝을 통해 원래 에러 정보도 보존할 수 있습니다.

### 배운 내용
- ✅ Exception 클래스를 상속하여 사용자 정의 예외 생성
- ✅ 예외에 추가 속성을 포함하여 풍부한 에러 정보 제공
- ✅ 예외 계층 구조를 설계하여 유연한 처리 구현
- ✅ raise from을 사용한 예외 체이닝으로 원인 정보 보존
- ✅ 실전에서 활용 가능한 예외 처리 전략 수립
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('모듈 시스템', 'Python 모듈의 개념과 import 문 사용법 학습', '# 모듈 시스템

Python 모듈은 재사용 가능한 코드를 구조화하는 기본 단위입니다. 모듈을 통해 코드를 논리적으로 분리하고 다른 프로그램에서 재사용할 수 있습니다.

## 학습 목표

- [ ] 모듈의 개념과 필요성을 이해한다
- [ ] import 문을 사용하여 모듈을 가져올 수 있다
- [ ] from-import 문의 다양한 형태를 활용할 수 있다
- [ ] __name__ 변수의 역할을 이해한다
- [ ] 모듈 검색 경로를 이해한다

## 모듈이란?

모듈은 Python 정의와 문장들을 담고 있는 파일입니다. 파일 이름에서 `.py` 확장자를 제외한 이름이 모듈 이름이 됩니다. 예를 들어 `math_utils.py` 파일은 `math_utils` 모듈입니다.

모듈을 사용하면 코드를 여러 파일로 분리하여 관리하고, 다른 프로젝트에서 재사용할 수 있습니다. 이는 대규모 프로그램 개발에 필수적입니다.

## 기본 사용법

```python
# calculator.py 파일 내용
def add(a, b):
    return a + b

def subtract(a, b):
    return a - b

# main.py에서 사용
import calculator

result = calculator.add(10, 5)
print(result)  # 15
```

모듈을 import하면 해당 모듈의 함수와 변수를 `모듈명.이름` 형태로 사용할 수 있습니다.

## import 문법

### 기본 import

```python
import math

print(math.pi)  # 3.141592653589793
print(math.sqrt(16))  # 4.0
```

### 별칭 사용 (as)

```python
import numpy as np  # numpy를 np로 줄여서 사용
import pandas as pd

arr = np.array([1, 2, 3])
```

### 여러 모듈 import

```python
import os, sys, json

# 권장: 각각 별도 줄에 작성
import os
import sys
import json
```

## from-import 문법

### 특정 항목만 가져오기

```python
from math import pi, sqrt

print(pi)  # 3.141592653589793
print(sqrt(25))  # 5.0
# 주의: math.pi가 아닌 pi로 직접 사용
```

### 모든 항목 가져오기

```python
from math import *

# 모든 함수를 직접 사용 가능
print(sin(0))  # 0.0
# 주의: 네임스페이스 오염 가능성
```

### 별칭과 함께 사용

```python
from datetime import datetime as dt

now = dt.now()
print(now)
```

## __name__ 변수

`__name__`은 모듈이 실행되는 방식을 구분하는 특별한 변수입니다.

```python
# utils.py
def greet(name):
    return f"Hello, {name}!"

if __name__ == ''__main__'':
    # 직접 실행될 때만 실행되는 코드
    print(greet("World"))
    print("이 파일을 직접 실행했습니다")
```

**동작 방식:**
- 파일을 직접 실행: `__name__`은 `''__main__''`
- 모듈로 import: `__name__`은 모듈 이름

```python
# main.py
import utils

# utils.py의 if 블록은 실행되지 않음
message = utils.greet("Python")
print(message)  # Hello, Python!
```

## 모듈 검색 경로

Python은 다음 순서로 모듈을 검색합니다:

```python
import sys

# 모듈 검색 경로 확인
for path in sys.path:
    print(path)
```

**검색 순서:**
1. 현재 디렉토리
2. PYTHONPATH 환경변수 경로
3. 표준 라이브러리 디렉토리
4. site-packages (설치된 패키지)

### 검색 경로 추가

```python
import sys

# 새 경로 추가
sys.path.append(''/my/custom/path'')

# 이제 해당 경로의 모듈을 import 가능
import my_module
```

## 주의사항

- ⚠️ `from module import *`는 네임스페이스를 오염시킬 수 있어 권장하지 않습니다
- 💡 모듈 이름은 소문자와 밑줄을 사용하는 것이 Python 관례입니다
- ⚠️ 순환 import(A가 B를 import하고 B가 A를 import)는 피해야 합니다
- 💡 `__name__ == ''__main__''` 패턴으로 테스트 코드를 모듈에 포함할 수 있습니다

## 정리

모듈은 Python 코드를 구조화하고 재사용하는 핵심 메커니즘입니다. import 문을 사용해 필요한 기능을 가져오고, `__name__` 변수로 실행 방식을 제어할 수 있습니다.

### 배운 내용
- ✅ 모듈은 .py 파일로 재사용 가능한 코드 단위
- ✅ import와 from-import로 모듈을 가져올 수 있음
- ✅ `__name__ == ''__main__''`으로 직접 실행 여부 확인
- ✅ sys.path를 통해 모듈 검색 경로 관리
- ✅ 별칭(as)으로 긴 모듈 이름을 줄일 수 있음
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('패키지와 __init__.py', '패키지 구조와 import 메커니즘 이해하기', '# 패키지와 __init__.py

모듈을 디렉토리로 구조화하여 대규모 프로젝트를 체계적으로 관리하는 방법을 학습합니다.

## 학습 목표

- [ ] 패키지의 개념과 디렉토리 구조를 이해한다
- [ ] __init__.py 파일의 역할을 이해한다
- [ ] 상대 임포트와 절대 임포트를 구분할 수 있다
- [ ] 서브패키지를 생성하고 사용할 수 있다
- [ ] __all__ 변수의 역할을 이해한다

## 패키지란?

패키지(Package)는 모듈을 디렉토리 구조로 조직화한 것입니다. 여러 모듈을 하나의 네임스페이스로 묶어 관리할 수 있으며, 대규모 프로젝트에서 코드를 체계적으로 구성하는 데 필수적입니다.

Python 3.3 이전에는 디렉토리를 패키지로 인식하려면 반드시 `__init__.py` 파일이 필요했지만, 이후 버전에서는 선택사항이 되었습니다. 그러나 패키지 초기화와 네임스페이스 제어를 위해 여전히 중요한 역할을 합니다.

## 패키지 디렉토리 구조

```python
# mypackage 패키지 구조
mypackage/
    __init__.py
    module1.py
    module2.py
    subpackage/
        __init__.py
        module3.py
```

이 구조를 실제로 생성하고 사용해보겠습니다:

```python
# module1.py
def hello():
    return "Hello from module1"

# module2.py
def goodbye():
    return "Goodbye from module2"
```

## __init__.py 파일의 역할

`__init__.py`는 패키지 초기화 시 자동으로 실행되며, 패키지 레벨의 변수와 함수를 정의할 수 있습니다.

```python
# mypackage/__init__.py
print("mypackage 초기화")

# 패키지 레벨 변수
version = "1.0.0"

# 간편한 import를 위한 설정
from .module1 import hello
from .module2 import goodbye
```

사용 예제:

```python
import mypackage
# 출력: mypackage 초기화

print(mypackage.version)  # 1.0.0
print(mypackage.hello())  # Hello from module1
```

## 주요 예제

### 예제 1: 절대 임포트

절대 임포트는 프로젝트 루트부터 전체 경로를 명시합니다.

```python
# main.py (프로젝트 루트)
from mypackage.module1 import hello
from mypackage.subpackage.module3 import process

print(hello())
print(process())
```

### 예제 2: 상대 임포트

상대 임포트는 현재 모듈의 위치를 기준으로 합니다.

```python
# mypackage/subpackage/module3.py
from ..module1 import hello  # 부모 패키지
from . import module4        # 같은 디렉토리

def process():
    return f"Processing: {hello()}"
```

상대 임포트 규칙:
- `.` : 현재 디렉토리
- `..` : 부모 디렉토리
- `...` : 부모의 부모 디렉토리

### 예제 3: __all__ 변수 사용

`__all__`은 `from package import *` 시 공개할 항목을 정의합니다.

```python
# mypackage/__init__.py
__all__ = [''hello'', ''version'']

from .module1 import hello
from .module2 import goodbye
version = "1.0.0"

# 사용
from mypackage import *
print(hello())    # 가능
print(version)    # 가능
print(goodbye())  # NameError: __all__에 없음
```

### 예제 4: 서브패키지 구조

계층적 패키지 구조를 만들어 기능별로 분리합니다.

```python
# myapp/
#     __init__.py
#     database/
#         __init__.py
#         connection.py
#     api/
#         __init__.py
#         routes.py

# myapp/database/connection.py
def connect():
    return "Database connected"

# myapp/__init__.py
from .database.connection import connect
from .api.routes import get_routes

# 사용
import myapp
myapp.connect()
```

### 예제 5: 동적 모듈 로딩

`__init__.py`에서 여러 모듈을 자동으로 임포트합니다.

```python
# mypackage/__init__.py
import os
import importlib

# 현재 디렉토리의 모든 .py 파일 로드
package_dir = os.path.dirname(__file__)
for filename in os.listdir(package_dir):
    if filename.endswith(''.py'') and filename != ''__init__.py'':
        module_name = filename[:-3]
        importlib.import_module(f''.{module_name}'', __name__)
```

## 주의사항

- ⚠️ 상대 임포트는 패키지 내부에서만 사용 가능합니다 (실행 스크립트에서는 불가)
- ⚠️ 순환 임포트를 피하세요 (A가 B를 import, B가 A를 import)
- ⚠️ `from package import *`는 명시적이지 않아 권장되지 않습니다
- 💡 패키지 구조는 기능별로 논리적으로 분리하세요
- 💡 `__init__.py`를 비워두면 단순 네임스페이스 패키지가 됩니다
- 💡 절대 임포트가 상대 임포트보다 명확하고 안전합니다

## 정리

패키지는 모듈을 디렉토리로 구조화하여 대규모 프로젝트를 관리합니다. `__init__.py`로 패키지를 초기화하고, 절대/상대 임포트로 모듈을 참조하며, `__all__`로 공개 인터페이스를 제어할 수 있습니다.

### 배운 내용
- ✅ 패키지는 디렉토리 기반 모듈 조직화 방식입니다
- ✅ `__init__.py`는 패키지 초기화 및 네임스페이스 제어에 사용됩니다
- ✅ 절대 임포트는 전체 경로, 상대 임포트는 `.`과 `..`을 사용합니다
- ✅ 서브패키지로 계층적 구조를 만들 수 있습니다
- ✅ `__all__`은 `import *` 시 공개 항목을 정의합니다
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('표준 라이브러리 활용', 'Python 표준 라이브러리의 주요 모듈 활용법', '# 표준 라이브러리 활용

Python은 설치와 동시에 사용할 수 있는 강력한 표준 라이브러리를 제공합니다. 외부 패키지 없이도 다양한 기능을 구현할 수 있습니다.

## 학습 목표

- [ ] Python 표준 라이브러리의 구조를 이해한다
- [ ] 자주 사용되는 표준 라이브러리 모듈을 활용할 수 있다
- [ ] 공식 문서를 참조하여 모듈을 학습할 수 있다
- [ ] 적절한 표준 라이브러리를 선택할 수 있다

## 표준 라이브러리란?

표준 라이브러리는 Python 설치 시 기본으로 제공되는 모듈의 집합입니다. 별도 설치 없이 `import`만으로 사용할 수 있으며, 파일 처리, 시스템 작업, 수학 연산, 날짜 처리 등 다양한 기능을 제공합니다.

표준 라이브러리를 활용하면 코드의 신뢰성이 높아지고, 외부 의존성을 줄일 수 있습니다. 또한 Python 공식 문서의 지원을 받을 수 있어 학습과 문제 해결이 용이합니다.

## os 모듈 - 운영체제 기능

```python
import os

# 현재 작업 디렉토리 확인
print(os.getcwd())

# 디렉토리 내 파일 목록
print(os.listdir(''.''))

# 경로 결합 (OS 독립적)
path = os.path.join(''folder'', ''file.txt'')
print(path)
```

`os` 모듈은 운영체제와 상호작용하는 기능을 제공합니다. 파일 시스템 탐색, 디렉토리 생성, 환경 변수 접근 등이 가능합니다.

## sys 모듈 - 시스템 설정

```python
import sys

# Python 버전 확인
print(sys.version)

# 명령줄 인자 접근
print(sys.argv)

# 모듈 검색 경로
print(sys.path[:3])  # 처음 3개만 출력
```

`sys` 모듈은 Python 인터프리터와 관련된 기능을 제공합니다. 프로그램 종료, 표준 입출력 제어, 시스템 정보 확인 등에 사용됩니다.

## 주요 예제

### 예제 1: random 모듈 - 난수 생성
```python
import random

# 1~10 사이 정수
print(random.randint(1, 10))

# 리스트에서 무작위 선택
colors = [''red'', ''blue'', ''green'']
print(random.choice(colors))

# 리스트 섞기
random.shuffle(colors)
print(colors)
```

### 예제 2: math 모듈 - 수학 연산
```python
import math

# 제곱근
print(math.sqrt(16))  # 4.0

# 올림, 내림
print(math.ceil(3.2))   # 4
print(math.floor(3.8))  # 3

# 원주율
print(math.pi)  # 3.141592...
```

### 예제 3: datetime 모듈 - 날짜와 시간
```python
from datetime import datetime, timedelta

# 현재 시간
now = datetime.now()
print(now.strftime(''%Y-%m-%d %H:%M''))

# 3일 후
future = now + timedelta(days=3)
print(future.date())
```

### 예제 4: collections 모듈 - 특수 컨테이너
```python
from collections import Counter, defaultdict

# 요소 개수 세기
words = [''apple'', ''banana'', ''apple'']
count = Counter(words)
print(count[''apple''])  # 2

# 기본값이 있는 딕셔너리
d = defaultdict(int)
d[''score''] += 10
print(d[''score''])  # 10
```

### 예제 5: itertools 모듈 - 반복 도구
```python
from itertools import combinations, chain

# 조합 생성
for combo in combinations([1, 2, 3], 2):
    print(combo)  # (1,2), (1,3), (2,3)

# 리스트 평탄화
nested = [[1, 2], [3, 4]]
flat = list(chain(*nested))
print(flat)  # [1, 2, 3, 4]
```

## 주의사항

- ⚠️ `os.path` 대신 Python 3.4+ 에서는 `pathlib` 모듈 사용 권장
- ⚠️ `random`은 암호학적으로 안전하지 않음 (보안용은 `secrets` 모듈 사용)
- 💡 표준 라이브러리는 외부 패키지보다 안정적이고 호환성이 좋음
- 💡 공식 문서(docs.python.org)에서 각 모듈의 전체 기능 확인 가능

## 공식 문서 활용법

Python 공식 문서는 표준 라이브러리 학습의 최고 자료입니다:

1. **docs.python.org/ko/3/library/** 에서 한글 문서 확인
2. 각 모듈 페이지에서 함수 목록과 예제 확인
3. "See also" 섹션에서 관련 모듈 탐색

예시: `collections` 모듈을 검색하면 `Counter`, `defaultdict`, `deque` 등의 클래스와 사용 예제를 볼 수 있습니다.

## 정리

표준 라이브러리는 Python의 강력한 무기입니다. 필요한 기능을 외부 패키지에서 찾기 전에 표준 라이브러리를 먼저 확인하는 습관을 들이면 더 견고한 코드를 작성할 수 있습니다.

### 배운 내용
- ✅ os, sys 모듈로 시스템 작업 수행
- ✅ random, math로 수학 연산 처리
- ✅ datetime, collections, itertools 등 유용한 도구 활용
- ✅ 공식 문서를 통한 모듈 학습 방법
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('클래스 기초', '객체지향 프로그래밍의 핵심인 클래스 정의와 사용법을 학습합니다', '# 클래스 기초

클래스는 관련된 데이터와 기능을 하나로 묶어 관리하는 객체지향 프로그래밍의 핵심 개념입니다. 이번 강의에서는 클래스의 기본 개념과 사용법을 배웁니다.

## 학습 목표

- [ ] 객체지향 프로그래밍의 기본 개념(클래스, 객체, 인스턴스)을 설명할 수 있다
- [ ] class 키워드를 사용하여 클래스를 정의하고 인스턴스를 생성할 수 있다
- [ ] __init__ 생성자 메서드를 작성하여 객체를 초기화할 수 있다
- [ ] 인스턴스 변수와 메서드를 정의하고 self 키워드를 올바르게 사용할 수 있다
- [ ] 실제 문제를 클래스로 모델링하여 코드를 작성할 수 있다

## 클래스란?

**클래스(class)**는 객체를 만들기 위한 설계도입니다. 예를 들어 ''자동차''라는 클래스가 있다면, 실제 자동차 객체들은 이 설계도를 바탕으로 만들어집니다.

**객체(object)**는 클래스로부터 만들어진 실체입니다. ''내 자동차'', ''친구 자동차''처럼 각각의 독립적인 존재를 의미합니다.

**인스턴스(instance)**는 객체와 같은 의미로, 특정 클래스로부터 만들어진 개별 객체를 강조할 때 사용합니다.

## 클래스 정의하기

클래스는 `class` 키워드를 사용하여 정의합니다.

```python
class Dog:
    def bark(self):
        print("멍멍!")

# 인스턴스 생성
my_dog = Dog()
my_dog.bark()  # 멍멍!
```

클래스 이름은 관례적으로 **첫 글자를 대문자**로 작성합니다(PascalCase).

## __init__ 생성자 메서드

`__init__`은 인스턴스가 생성될 때 자동으로 호출되는 특별한 메서드로, 객체의 초기 상태를 설정합니다.

```python
class Dog:
    def __init__(self, name, age):
        self.name = name  # 인스턴스 변수
        self.age = age

    def bark(self):
        print(f"{self.name}: 멍멍!")

# 인스턴스 생성 시 초기값 전달
dog1 = Dog("뽀삐", 3)
dog2 = Dog("초코", 5)

print(dog1.name)  # 뽀삐
dog2.bark()       # 초코: 멍멍!
```

## self의 의미

`self`는 인스턴스 자기 자신을 가리키는 매개변수입니다. 메서드의 첫 번째 매개변수로 반드시 포함되어야 합니다.

```python
class Person:
    def __init__(self, name):
        self.name = name  # self.name은 인스턴스 변수

    def introduce(self):
        print(f"안녕하세요, {self.name}입니다")

person = Person("홍길동")
person.introduce()  # 안녕하세요, 홍길동입니다
```

메서드를 호출할 때 `self`는 자동으로 전달되므로 직접 입력하지 않습니다.

## 주요 예제

### 예제 1: 학생 관리 클래스

```python
class Student:
    def __init__(self, name, student_id):
        self.name = name
        self.student_id = student_id
        self.scores = []

    def add_score(self, score):
        self.scores.append(score)

    def get_average(self):
        if not self.scores:
            return 0
        return sum(self.scores) / len(self.scores)

# 사용 예시
student = Student("김철수", "2024001")
student.add_score(85)
student.add_score(90)
student.add_score(88)
print(f"평균 점수: {student.get_average():.1f}")  # 평균 점수: 87.7
```

### 예제 2: 은행 계좌 클래스

```python
class BankAccount:
    def __init__(self, owner, balance=0):
        self.owner = owner
        self.balance = balance

    def deposit(self, amount):
        self.balance += amount
        print(f"{amount}원 입금 완료. 잔액: {self.balance}원")

    def withdraw(self, amount):
        if self.balance >= amount:
            self.balance -= amount
            print(f"{amount}원 출금 완료. 잔액: {self.balance}원")
        else:
            print("잔액이 부족합니다")

# 사용 예시
account = BankAccount("홍길동", 10000)
account.deposit(5000)   # 5000원 입금 완료. 잔액: 15000원
account.withdraw(3000)  # 3000원 출금 완료. 잔액: 12000원
```

### 예제 3: 도서 관리 클래스

```python
class Book:
    def __init__(self, title, author, year):
        self.title = title
        self.author = author
        self.year = year
        self.is_borrowed = False

    def borrow(self):
        if not self.is_borrowed:
            self.is_borrowed = True
            print(f"''{self.title}'' 대출되었습니다")
        else:
            print("이미 대출 중인 도서입니다")

    def return_book(self):
        self.is_borrowed = False
        print(f"''{self.title}'' 반납되었습니다")

# 사용 예시
book = Book("파이썬 프로그래밍", "김코딩", 2024)
book.borrow()        # ''파이썬 프로그래밍'' 대출되었습니다
book.borrow()        # 이미 대출 중인 도서입니다
book.return_book()   # ''파이썬 프로그래밍'' 반납되었습니다
```

## 주의사항

- ⚠️ **메서드의 첫 번째 매개변수는 항상 self**: self를 빼먹으면 오류가 발생합니다
- ⚠️ **인스턴스 변수는 self를 붙여 정의**: `self.변수명` 형태로 작성해야 합니다
- 💡 **클래스명은 PascalCase 사용**: `MyClass`, `BankAccount`처럼 각 단어의 첫 글자를 대문자로
- 💡 **생성자에서 초기화**: 인스턴스 변수는 `__init__`에서 초기화하는 것이 좋습니다
- 💡 **하나의 클래스는 하나의 역할**: 클래스는 명확한 하나의 목적을 가져야 합니다

## 정리

클래스는 데이터와 기능을 하나로 묶어 코드를 체계적으로 관리할 수 있게 해줍니다. `__init__`으로 초기 상태를 설정하고, `self`를 통해 인스턴스 자신을 참조합니다.

### 배운 내용

- ✅ 클래스는 객체를 만들기 위한 설계도이며, class 키워드로 정의합니다
- ✅ __init__ 메서드는 인스턴스 생성 시 자동 호출되어 초기화를 수행합니다
- ✅ self는 인스턴스 자신을 가리키며, 모든 메서드의 첫 번째 매개변수입니다
- ✅ 인스턴스 변수는 self.변수명으로 정의하고, 각 인스턴스마다 독립적입니다
- ✅ 클래스를 사용하면 관련 데이터와 기능을 체계적으로 관리할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('클래스 변수와 메서드', '클래스 변수와 다양한 메서드 유형 이해하기', '# 클래스 변수와 메서드

Python의 클래스는 다양한 유형의 변수와 메서드를 가질 수 있습니다. 인스턴스 변수와 클래스 변수의 차이를 이해하고, 상황에 맞는 메서드 유형을 선택하는 것이 효율적인 객체지향 프로그래밍의 핵심입니다.

## 학습 목표

- [ ] 인스턴스 변수와 클래스 변수의 차이를 이해한다
- [ ] 클래스 메서드와 정적 메서드를 정의할 수 있다
- [ ] @classmethod와 @staticmethod 데코레이터를 활용할 수 있다
- [ ] 적절한 메서드 유형을 선택할 수 있다

## 인스턴스 변수 vs 클래스 변수

**인스턴스 변수**는 각 객체마다 독립적으로 저장되는 변수이고, **클래스 변수**는 모든 인스턴스가 공유하는 변수입니다.

인스턴스 변수는 `self`를 통해 정의되며 객체마다 고유한 값을 가집니다. 반면 클래스 변수는 클래스 내부에 직접 선언되며, 모든 인스턴스가 동일한 값을 참조합니다.

클래스 변수는 주로 객체 개수 추적, 공통 설정값 저장, 상수 정의 등에 활용됩니다.

## 기본 사용법

```python
class Employee:
    company = "TechCorp"  # 클래스 변수
    count = 0  # 객체 개수 추적

    def __init__(self, name):
        self.name = name  # 인스턴스 변수
        Employee.count += 1

emp1 = Employee("김철수")
emp2 = Employee("이영희")
print(f"회사: {Employee.company}")  # TechCorp
print(f"직원 수: {Employee.count}")  # 2
```

클래스 변수는 클래스 이름으로 접근하며, 모든 인스턴스가 공유합니다.

## 주요 예제

### 예제 1: 인스턴스 메서드
```python
class Calculator:
    def __init__(self, value):
        self.value = value  # 인스턴스 변수

    def add(self, num):  # 인스턴스 메서드
        self.value += num
        return self.value

calc = Calculator(10)
print(calc.add(5))  # 15
```

인스턴스 메서드는 `self`를 첫 번째 매개변수로 받으며, 인스턴스 변수에 접근합니다.

### 예제 2: 클래스 메서드 (@classmethod)
```python
class Date:
    def __init__(self, year, month, day):
        self.year = year
        self.month = month
        self.day = day

    @classmethod
    def from_string(cls, date_str):  # 대체 생성자
        year, month, day = map(int, date_str.split(''-''))
        return cls(year, month, day)

date = Date.from_string("2025-10-25")
print(f"{date.year}년 {date.month}월 {date.day}일")
# 2025년 10월 25일
```

`@classmethod`는 클래스 자체를 첫 번째 매개변수(`cls`)로 받으며, 대체 생성자 패턴에 자주 사용됩니다.

### 예제 3: 정적 메서드 (@staticmethod)
```python
class MathUtils:
    @staticmethod
    def is_even(num):  # self나 cls 불필요
        return num % 2 == 0

    @staticmethod
    def is_positive(num):
        return num > 0

print(MathUtils.is_even(4))  # True
print(MathUtils.is_positive(-5))  # False
```

`@staticmethod`는 클래스나 인스턴스와 무관한 유틸리티 함수에 사용되며, `self`나 `cls` 없이 정의됩니다.

### 예제 4: 실전 활용 - 사용자 관리 시스템
```python
class User:
    total_users = 0  # 클래스 변수

    def __init__(self, username, email):
        self.username = username
        self.email = email
        User.total_users += 1

    @classmethod
    def get_total_users(cls):
        return cls.total_users

    @staticmethod
    def validate_email(email):
        return "@" in email and "." in email

user1 = User("alice", "alice@example.com")
user2 = User("bob", "bob@example.com")
print(User.get_total_users())  # 2
print(User.validate_email("test@mail.com"))  # True
```

### 예제 5: 메서드 유형 비교
```python
class Product:
    tax_rate = 0.1  # 클래스 변수

    def __init__(self, name, price):
        self.name = name
        self.price = price

    def get_total(self):  # 인스턴스 메서드
        return self.price * (1 + Product.tax_rate)

    @classmethod
    def update_tax(cls, new_rate):  # 클래스 메서드
        cls.tax_rate = new_rate

    @staticmethod
    def is_expensive(price):  # 정적 메서드
        return price > 100000

product = Product("노트북", 150000)
print(product.get_total())  # 165000.0
Product.update_tax(0.15)
print(product.get_total())  # 172500.0
print(Product.is_expensive(200000))  # True
```

## 주의사항

- 클래스 변수 수정 시 `클래스명.변수` 사용 (self 사용 시 인스턴스 변수 생성)
- 클래스 메서드는 상속 시 자동으로 올바른 클래스 참조
- 정적 메서드는 네임스페이스 목적으로만 클래스에 속함
- 인스턴스 메서드에서 클래스 변수 접근은 `self.__class__.변수` 권장

## 정리

클래스 변수는 모든 인스턴스가 공유하며, 인스턴스 메서드(`self`), 클래스 메서드(`@classmethod`, `cls`), 정적 메서드(`@staticmethod`)로 목적에 맞게 구현할 수 있습니다.

### 배운 내용
- 인스턴스 변수는 객체별 독립, 클래스 변수는 공유
- `@classmethod`는 대체 생성자와 클래스 상태 수정에 활용
- `@staticmethod`는 클래스와 무관한 유틸리티 함수에 적합
- 각 메서드 유형은 `self`/`cls` 유무로 구분
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('상속(Inheritance)', '부모 클래스를 상속받아 코드 재사용성을 높이는 방법', '# 상속(Inheritance)

상속은 기존 클래스의 속성과 메서드를 새로운 클래스가 물려받는 객체지향 프로그래밍의 핵심 개념입니다. 코드 재사용성을 높이고 계층적 관계를 표현할 수 있습니다.

## 학습 목표

- [ ] 상속의 개념과 필요성을 이해한다
- [ ] 부모 클래스를 상속하는 자식 클래스를 정의할 수 있다
- [ ] super() 함수를 사용하여 부모 클래스 메서드를 호출할 수 있다
- [ ] 메서드 오버라이딩을 수행할 수 있다
- [ ] isinstance()와 issubclass()를 활용할 수 있다

## 상속이란?

상속은 부모 클래스(Parent Class)의 속성과 메서드를 자식 클래스(Child Class)가 물려받는 것입니다. 이를 통해 공통된 코드를 재사용하고, 기존 클래스를 확장하여 새로운 기능을 추가할 수 있습니다.

예를 들어, ''동물'' 클래스가 있다면 ''개'', ''고양이'' 클래스는 동물의 공통 속성을 상속받고, 각자의 고유한 특성을 추가할 수 있습니다. 이는 코드 중복을 줄이고 유지보수를 쉽게 만듭니다.

상속을 사용하면 IS-A 관계를 표현할 수 있습니다. "개는 동물이다"와 같은 관계를 코드로 자연스럽게 나타낼 수 있습니다.

## 기본 사용법

```python
# 부모 클래스 정의
class Animal:
    def __init__(self, name):
        self.name = name

    def speak(self):
        print(f"{self.name}가 소리를 냅니다")

# 자식 클래스 정의 (Animal 상속)
class Dog(Animal):
    def bark(self):
        print(f"{self.name}가 멍멍 짖습니다")

# 사용 예시
dog = Dog("바둑이")
dog.speak()  # 부모 클래스 메서드 사용
dog.bark()   # 자식 클래스 메서드 사용
```

자식 클래스는 `class 자식클래스명(부모클래스명):` 형태로 정의합니다. Dog 클래스는 Animal의 모든 메서드와 속성을 자동으로 물려받습니다.

## 주요 예제

### 예제 1: super()로 부모 클래스 초기화

```python
class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age

class Student(Person):
    def __init__(self, name, age, student_id):
        super().__init__(name, age)  # 부모 초기화
        self.student_id = student_id

student = Student("김철수", 20, "2024001")
print(f"{student.name}, {student.age}세, {student.student_id}")
# 출력: 김철수, 20세, 2024001
```

### 예제 2: 메서드 오버라이딩

```python
class Animal:
    def speak(self):
        print("동물이 소리를 냅니다")

class Cat(Animal):
    def speak(self):  # 부모 메서드 재정의
        print("야옹")

animal = Animal()
cat = Cat()
animal.speak()  # 출력: 동물이 소리를 냅니다
cat.speak()     # 출력: 야옹
```

### 예제 3: isinstance()와 issubclass()

```python
class Vehicle:
    pass

class Car(Vehicle):
    pass

my_car = Car()

# isinstance: 객체가 특정 클래스의 인스턴스인지 확인
print(isinstance(my_car, Car))      # True
print(isinstance(my_car, Vehicle))  # True

# issubclass: 클래스가 다른 클래스의 서브클래스인지 확인
print(issubclass(Car, Vehicle))     # True
print(issubclass(Vehicle, Car))     # False
```

### 예제 4: super()로 부모 메서드 확장

```python
class Rectangle:
    def __init__(self, width, height):
        self.width = width
        self.height = height

    def area(self):
        return self.width * self.height

class ColoredRectangle(Rectangle):
    def __init__(self, width, height, color):
        super().__init__(width, height)
        self.color = color

    def describe(self):
        return f"{self.color} 사각형, 면적: {self.area()}"

rect = ColoredRectangle(10, 5, "빨강")
print(rect.describe())  # 출력: 빨강 사각형, 면적: 50
```

### 예제 5: 다중 상속

```python
class Flyable:
    def fly(self):
        print("날아갑니다")

class Swimmable:
    def swim(self):
        print("헤엄칩니다")

class Duck(Flyable, Swimmable):
    def quack(self):
        print("꽥꽥")

duck = Duck()
duck.fly()   # 출력: 날아갑니다
duck.swim()  # 출력: 헤엄칩니다
duck.quack() # 출력: 꽥꽥
```

## 주의사항

- ⚠️ 자식 클래스에서 `__init__`을 정의하면 부모의 `__init__`이 자동 호출되지 않습니다. 반드시 `super().__init__()`을 명시적으로 호출해야 합니다.
- ⚠️ 다중 상속 시 메서드 이름이 겹치면 MRO(Method Resolution Order)에 따라 왼쪽 부모 클래스의 메서드가 우선합니다.
- 💡 상속보다 컴포지션(Composition)이 더 적합한 경우도 있습니다. IS-A 관계가 명확할 때만 상속을 사용하세요.
- 💡 `super()`는 부모 클래스를 직접 참조하는 것보다 유연하고 안전합니다. 특히 다중 상속에서 필수적입니다.

## 정리

상속은 코드 재사용성을 극대화하고 계층적 관계를 표현하는 강력한 도구입니다. super() 함수와 메서드 오버라이딩을 적절히 활용하면 유지보수가 쉬운 객체지향 프로그램을 작성할 수 있습니다.

### 배운 내용

- ✅ 상속을 통해 부모 클래스의 기능을 자식 클래스가 물려받을 수 있습니다
- ✅ super() 함수로 부모 클래스의 메서드를 안전하게 호출할 수 있습니다
- ✅ 메서드 오버라이딩으로 부모 메서드를 재정의하여 새로운 동작을 구현할 수 있습니다
- ✅ isinstance()와 issubclass()로 객체와 클래스의 관계를 확인할 수 있습니다
- ✅ 다중 상속을 통해 여러 부모 클래스로부터 기능을 물려받을 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('다형성과 특수 메서드', '다형성 개념과 덕 타이핑, 특수 메서드 활용법을 학습합니다', '# 다형성과 특수 메서드

Python의 다형성은 같은 인터페이스를 통해 다양한 타입의 객체를 다룰 수 있게 합니다. 특수 메서드를 활용하면 내장 함수나 연산자와 자연스럽게 통합되는 클래스를 만들 수 있습니다.

## 학습 목표

- [ ] 다형성(polymorphism)의 개념을 이해하고 설명할 수 있다
- [ ] 덕 타이핑(duck typing)을 이해하고 활용할 수 있다
- [ ] 특수 메서드(__str__, __repr__)를 구현할 수 있다
- [ ] 연산자 오버로딩의 기본 개념을 이해하고 적용할 수 있다

## 다형성이란?

다형성(Polymorphism)은 같은 메서드나 연산이 서로 다른 타입의 객체에 대해 다르게 동작하는 것을 의미합니다. Python은 동적 타이핑 언어이므로 타입 체크 없이 객체가 필요한 메서드를 가지고 있기만 하면 됩니다.

예를 들어, `len()` 함수는 문자열, 리스트, 딕셔너리 등 다양한 타입에 대해 작동합니다. 각 타입은 내부적으로 `__len__()` 메서드를 구현하고 있기 때문입니다.

## 덕 타이핑 (Duck Typing)

"오리처럼 걷고 오리처럼 운다면, 그것은 오리다"라는 원칙에서 유래한 개념입니다. Python은 객체의 타입보다는 객체가 어떤 메서드를 가지고 있는지를 중요하게 여깁니다.

```python
# 덕 타이핑 예제
class Dog:
    def speak(self):
        return "멍멍!"

class Cat:
    def speak(self):
        return "야옹!"

def make_sound(animal):
    # animal의 타입 체크 없이 speak() 메서드만 있으면 작동
    print(animal.speak())

make_sound(Dog())  # 멍멍!
make_sound(Cat())  # 야옹!
```

## 특수 메서드: __str__과 __repr__

특수 메서드(Magic Methods 또는 Dunder Methods)는 이중 밑줄로 시작하고 끝나는 메서드로, Python의 내장 동작과 통합됩니다.

### __str__과 __repr__의 차이

```python
class Book:
    def __init__(self, title, price):
        self.title = title
        self.price = price

    def __str__(self):
        # 사용자 친화적인 문자열 (print에서 사용)
        return f"{self.title} - {self.price}원"

    def __repr__(self):
        # 개발자용 문자열 (디버깅용)
        return f"Book(title=''{self.title}'', price={self.price})"

book = Book("Python 기초", 25000)
print(book)         # Python 기초 - 25000원
print(repr(book))   # Book(title=''Python 기초'', price=25000)
```

## 주요 예제

### 예제 1: 컨테이너 타입 만들기

```python
class ShoppingCart:
    def __init__(self):
        self.items = []

    def add(self, item):
        self.items.append(item)

    def __len__(self):
        return len(self.items)

    def __getitem__(self, index):
        return self.items[index]

cart = ShoppingCart()
cart.add("사과")
cart.add("바나나")
print(len(cart))     # 2
print(cart[0])       # 사과
```

### 예제 2: 연산자 오버로딩 - 비교 연산

```python
class Product:
    def __init__(self, name, price):
        self.name = name
        self.price = price

    def __eq__(self, other):
        return self.price == other.price

    def __lt__(self, other):
        return self.price < other.price

    def __str__(self):
        return f"{self.name}: {self.price}원"

p1 = Product("노트북", 1000000)
p2 = Product("마우스", 50000)
print(p1 > p2)       # True
print(p1 == p2)      # False
```

### 예제 3: 연산자 오버로딩 - 산술 연산

```python
class Vector:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __add__(self, other):
        return Vector(self.x + other.x, self.y + other.y)

    def __str__(self):
        return f"Vector({self.x}, {self.y})"

v1 = Vector(1, 2)
v2 = Vector(3, 4)
v3 = v1 + v2
print(v3)  # Vector(4, 6)
```

### 예제 4: 다형성 활용

```python
class FileWriter:
    def write(self, data):
        with open("file.txt", "w") as f:
            f.write(data)

class ConsoleWriter:
    def write(self, data):
        print(data)

def save_data(writer, data):
    # writer의 타입에 관계없이 write() 메서드만 있으면 작동
    writer.write(data)

save_data(ConsoleWriter(), "콘솔 출력")
save_data(FileWriter(), "파일 저장")
```

## 주의사항

- ⚠️ `__repr__`는 항상 구현하는 것이 좋습니다 - 디버깅에 유용합니다
- ⚠️ `__str__`이 없으면 `__repr__`가 대신 사용됩니다
- 💡 연산자 오버로딩은 직관적일 때만 사용하세요 - 혼란을 줄 수 있습니다
- 💡 덕 타이핑을 활용하면 유연한 코드를 작성할 수 있지만, 문서화가 중요합니다

## 정리

다형성은 Python의 강력한 특징 중 하나로, 타입에 구애받지 않는 유연한 코드를 작성할 수 있게 합니다. 특수 메서드를 활용하면 내장 함수와 연산자를 자연스럽게 사용하는 클래스를 만들 수 있습니다.

### 배운 내용
- ✅ 다형성은 같은 인터페이스로 다양한 타입을 다루는 것
- ✅ 덕 타이핑은 타입보다 메서드 존재를 중시
- ✅ `__str__`은 사용자용, `__repr__`은 개발자용 문자열 표현
- ✅ 특수 메서드로 연산자와 내장 함수를 커스터마이징 가능
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('캡슐화와 접근 제어', 'Python의 캡슐화 개념과 접근 제어 규칙을 학습합니다', '# 캡슐화와 접근 제어

객체의 내부 데이터를 보호하고 외부 접근을 제어하는 캡슐화는 객체지향 프로그래밍의 핵심 원칙입니다.

## 학습 목표

- [ ] 캡슐화의 개념과 필요성을 설명할 수 있다
- [ ] public, protected, private 접근 제어 규칙을 적용할 수 있다
- [ ] 네임 맹글링의 동작 원리를 이해하고 활용할 수 있다
- [ ] @property 데코레이터로 getter/setter를 구현할 수 있다

## 캡슐화란?

캡슐화(Encapsulation)는 객체의 내부 데이터와 구현을 숨기고, 외부에는 필요한 인터페이스만 제공하는 것입니다. 이를 통해 데이터의 무결성을 보호하고, 코드의 유지보수성을 높입니다.

Python은 엄격한 접근 제어를 강제하지 않지만, 명명 규칙을 통해 의도를 표현합니다. 이는 "우리는 모두 성인이다(We''re all consenting adults)"라는 Python 철학을 반영합니다.

## 접근 제어 규칙

### Public (공개)

일반적인 속성과 메서드는 public으로 어디서든 접근 가능합니다.

```python
class User:
    def __init__(self, name):
        self.name = name  # public 속성

    def greet(self):  # public 메서드
        return f"안녕하세요, {self.name}입니다"

user = User("홍길동")
print(user.name)  # 출력: 홍길동
```

### Protected (보호) - 단일 언더스코어(_)

`_`로 시작하는 속성은 "내부 구현"임을 나타냅니다. 외부에서 접근하지 말라는 약속입니다.

```python
class BankAccount:
    def __init__(self, balance):
        self._balance = balance  # protected 속성

    def _validate(self, amount):  # protected 메서드
        return amount > 0

account = BankAccount(1000)
print(account._balance)  # 접근 가능하지만 권장하지 않음
```

### Private (비공개) - 이중 언더스코어(__)

`__`로 시작하는 속성은 네임 맹글링을 통해 외부 접근을 어렵게 만듭니다.

```python
class SecureData:
    def __init__(self):
        self.__secret = "비밀정보"  # private 속성

    def get_secret(self):
        return self.__secret

data = SecureData()
# print(data.__secret)  # AttributeError 발생
print(data.get_secret())  # 출력: 비밀정보
```

## 네임 맹글링

네임 맹글링(Name Mangling)은 `__`로 시작하는 속성명을 `_클래스명__속성명` 형태로 변환하는 메커니즘입니다.

```python
class Parent:
    def __init__(self):
        self.__private = "부모 비공개"

    def show(self):
        print(self.__private)

parent = Parent()
parent.show()  # 출력: 부모 비공개
print(parent._Parent__private)  # 맹글링된 이름으로 접근 가능
```

네임 맹글링은 완벽한 보안이 아닌, 실수로 인한 충돌을 방지하는 장치입니다.

## 주요 예제

### 예제 1: 은행 계좌 클래스

```python
class BankAccount:
    def __init__(self, owner, balance):
        self.owner = owner
        self.__balance = balance  # private

    def deposit(self, amount):
        if amount > 0:
            self.__balance += amount
            return True
        return False

    def get_balance(self):
        return self.__balance

account = BankAccount("김철수", 10000)
account.deposit(5000)
print(account.get_balance())  # 출력: 15000
```

### 예제 2: @property 데코레이터

@property를 사용하면 메서드를 속성처럼 사용할 수 있습니다.

```python
class Temperature:
    def __init__(self, celsius):
        self._celsius = celsius

    @property
    def celsius(self):  # getter
        return self._celsius

    @celsius.setter
    def celsius(self, value):  # setter
        if value < -273.15:
            raise ValueError("절대영도 이하입니다")
        self._celsius = value

temp = Temperature(25)
print(temp.celsius)  # 출력: 25
temp.celsius = 30  # setter 호출
print(temp.celsius)  # 출력: 30
```

### 예제 3: 읽기 전용 속성

setter를 정의하지 않으면 읽기 전용 속성이 됩니다.

```python
class Circle:
    def __init__(self, radius):
        self._radius = radius

    @property
    def area(self):
        return 3.14159 * self._radius ** 2

circle = Circle(5)
print(circle.area)  # 출력: 78.53975
# circle.area = 100  # AttributeError 발생
```

## 주의사항

- ⚠️ `__`는 네임 맹글링으로 완벽한 보안이 아닙니다. `_클래스명__속성명`으로 여전히 접근 가능합니다
- ⚠️ `_`는 단순한 관례이며, Python은 실제로 접근을 막지 않습니다
- 💡 일반적으로 `_`로 충분하며, `__`는 하위 클래스와의 이름 충돌 방지가 필요할 때만 사용합니다
- 💡 @property는 속성 접근에 로직을 추가할 수 있어 유효성 검사에 유용합니다

## 정리

캡슐화는 데이터 보호와 인터페이스 제공을 통해 안전하고 유지보수하기 쉬운 코드를 만듭니다. Python은 엄격한 강제 대신 명명 규칙과 @property를 통해 유연한 캡슐화를 지원합니다.

### 배운 내용

- ✅ 캡슐화는 데이터를 보호하고 접근을 제어하는 OOP 원칙입니다
- ✅ `_`는 protected, `__`는 private을 나타내는 관례입니다
- ✅ 네임 맹글링은 `_클래스명__속성명` 형태로 이름을 변환합니다
- ✅ @property는 메서드를 속성처럼 사용하게 하며 getter/setter를 구현합니다
', NULL, NULL, 'MARKDOWN', 'Python', '중급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('collections 모듈 완전 정복', 'namedtuple, Counter, defaultdict, deque 등 실무 필수 자료구조', '# collections 모듈 완전 정복

Python의 collections 모듈은 내장 자료형(list, dict, tuple)을 확장한 고성능 컨테이너를 제공합니다. 실무에서 자주 사용되는 필수 자료구조들을 학습합니다.

## 학습 목표

- [ ] namedtuple을 사용하여 간단한 클래스를 대체할 수 있다
- [ ] Counter를 활용하여 요소의 개수를 세고 분석할 수 있다
- [ ] defaultdict를 사용하여 기본값을 가진 딕셔너리를 생성할 수 있다
- [ ] deque를 사용하여 양방향 큐를 구현할 수 있다
- [ ] ChainMap과 OrderedDict의 활용 사례를 이해한다

## namedtuple: 필드를 가진 튜플

namedtuple은 이름 있는 필드를 가진 튜플입니다. 클래스보다 가볍고, 일반 튜플보다 가독성이 좋습니다.

```python
from collections import namedtuple

# Point 클래스 정의
Point = namedtuple(''Point'', [''x'', ''y''])

# 객체 생성
p = Point(10, 20)
print(p.x, p.y)  # 10 20
print(p[0], p[1])  # 인덱스 접근도 가능
```

튜플의 불변성을 유지하면서 필드명으로 접근할 수 있어 코드 가독성이 크게 향상됩니다.

## Counter: 요소 개수 세기

Counter는 해시 가능한 객체의 개수를 세는 딕셔너리 서브클래스입니다. 빈도 분석에 최적화되어 있습니다.

```python
from collections import Counter

# 단어 빈도 계산
words = [''apple'', ''banana'', ''apple'', ''cherry'', ''banana'', ''apple'']
counter = Counter(words)

print(counter)  # Counter({''apple'': 3, ''banana'': 2, ''cherry'': 1})
print(counter[''apple''])  # 3
print(counter.most_common(2))  # [(''apple'', 3), (''banana'', 2)]
```

most_common() 메서드는 가장 빈번한 요소를 정렬된 리스트로 반환합니다.

## defaultdict: 기본값 제공 딕셔너리

defaultdict는 존재하지 않는 키에 접근할 때 자동으로 기본값을 생성합니다. KeyError 처리가 필요 없습니다.

```python
from collections import defaultdict

# 리스트를 기본값으로 하는 딕셔너리
groups = defaultdict(list)
groups[''fruits''].append(''apple'')
groups[''vegetables''].append(''carrot'')

print(groups)  # defaultdict(<class ''list''>, {''fruits'': [''apple''], ''vegetables'': [''carrot'']})
```

setdefault()나 if 문 없이 간결하게 그룹화 작업을 수행할 수 있습니다.

## deque: 양방향 큐

deque(double-ended queue)는 양쪽 끝에서 빠른 삽입/삭제가 가능한 자료구조입니다. 리스트보다 효율적입니다.

```python
from collections import deque

# 양방향 큐 생성
queue = deque([1, 2, 3])

queue.append(4)      # 오른쪽 추가: [1, 2, 3, 4]
queue.appendleft(0)  # 왼쪽 추가: [0, 1, 2, 3, 4]

print(queue.pop())      # 4 (오른쪽에서 제거)
print(queue.popleft())  # 0 (왼쪽에서 제거)
print(queue)            # deque([1, 2, 3])
```

스택과 큐를 모두 구현할 수 있으며, 최대 길이를 설정하여 순환 버퍼로도 사용 가능합니다.

## 주요 예제

### 예제 1: namedtuple로 학생 정보 관리
```python
from collections import namedtuple

Student = namedtuple(''Student'', [''name'', ''age'', ''grade''])

students = [
    Student(''김철수'', 20, ''A''),
    Student(''이영희'', 22, ''B''),
    Student(''박민수'', 21, ''A'')
]

# A학점 학생만 필터링
a_students = [s for s in students if s.grade == ''A'']
for student in a_students:
    print(f"{student.name}: {student.grade}")
```

### 예제 2: Counter로 텍스트 분석
```python
from collections import Counter

text = "hello world hello python world world"
words = text.split()

counter = Counter(words)

# 가장 빈번한 단어 2개
print(counter.most_common(2))  # [(''world'', 3), (''hello'', 2)]

# 단어 추가
counter.update([''python'', ''python''])
print(counter[''python''])  # 3
```

### 예제 3: defaultdict로 데이터 그룹화
```python
from collections import defaultdict

data = [
    (''apple'', 3),
    (''banana'', 2),
    (''apple'', 5),
    (''cherry'', 1),
    (''banana'', 3)
]

# 과일별 수량 합계
totals = defaultdict(int)
for fruit, quantity in data:
    totals[fruit] += quantity

print(dict(totals))  # {''apple'': 8, ''banana'': 5, ''cherry'': 1}
```

### 예제 4: deque로 최근 이력 관리
```python
from collections import deque

# 최대 5개 항목만 유지하는 순환 버퍼
history = deque(maxlen=5)

for i in range(10):
    history.append(f"작업{i}")
    print(list(history))

# 최종: [''작업5'', ''작업6'', ''작업7'', ''작업8'', ''작업9'']
```

### 예제 5: ChainMap으로 설정 우선순위 관리
```python
from collections import ChainMap

# 사용자 설정 > 기본 설정 우선순위
defaults = {''theme'': ''light'', ''lang'': ''ko'', ''font'': 12}
user_config = {''theme'': ''dark''}

config = ChainMap(user_config, defaults)
print(config[''theme''])  # ''dark'' (user_config 우선)
print(config[''lang''])   # ''ko'' (defaults에서)
```

## 주의사항

- namedtuple은 불변 객체입니다. 값을 변경하려면 _replace() 메서드를 사용하세요
- Counter의 요소는 음수 값도 가질 수 있습니다
- defaultdict는 존재하지 않는 키를 조회할 때마다 기본값을 생성하므로 주의가 필요합니다
- deque는 중간 요소 접근(인덱싱)이 리스트보다 느립니다

## 정리

collections 모듈은 표준 자료구조를 확장하여 특정 상황에 최적화된 컨테이너를 제공합니다. namedtuple은 가독성을, Counter는 빈도 분석을, defaultdict는 간결성을, deque는 양방향 처리 성능을 각각 향상시킵니다.

### 배운 내용
- namedtuple: 이름 있는 필드를 가진 불변 튜플
- Counter: 요소 개수 세기 및 most_common() 활용
- defaultdict: 자동 기본값 생성으로 KeyError 방지
- deque: 양방향 큐로 빠른 append/pop 연산
- ChainMap: 여러 딕셔너리를 하나로 체인
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('itertools 모듈 마스터하기', '메모리 효율적인 반복자와 조합론 함수 활용법', '# itertools 모듈 마스터하기

itertools는 메모리 효율적이고 강력한 반복자를 제공하는 Python 표준 라이브러리입니다. 대용량 데이터나 무한 시퀀스를 처리할 때 필수적인 도구입니다.

## 학습 목표

- [ ] 무한 반복자를 활용할 수 있다
- [ ] 조합론적 반복자를 사용할 수 있다
- [ ] 체인과 그룹핑 함수를 활용할 수 있다
- [ ] 메모리 효율적인 반복 처리를 수행할 수 있다

## itertools란?

itertools는 효율적인 루프 작성을 위한 반복자 함수들을 제공합니다. 이 모듈의 핵심 장점은 지연 평가(lazy evaluation)로, 필요한 시점에만 값을 생성하여 메모리를 절약합니다.

일반적인 리스트 생성 방식은 모든 값을 메모리에 저장하지만, itertools는 한 번에 하나씩 값을 생성합니다. 이는 특히 대용량 데이터 처리나 무한 시퀀스를 다룰 때 필수적입니다.

조합론, 필터링, 그룹핑 등 다양한 반복 패턴을 간결하고 효율적으로 구현할 수 있어 데이터 분석과 알고리즘 구현에 널리 사용됩니다.

## 기본 사용법

```python
import itertools

# count: 무한 카운터
counter = itertools.count(10, 2)
print([next(counter) for _ in range(5)])
# [10, 12, 14, 16, 18]

# cycle: 순환 반복
cycler = itertools.cycle([''A'', ''B'', ''C''])
print([next(cycler) for _ in range(7)])
# [''A'', ''B'', ''C'', ''A'', ''B'', ''C'', ''A'']
```

## 주요 예제

### 예제 1: 무한 반복자 활용

```python
import itertools

# count: 시작값과 간격으로 무한 숫자 생성
for i in itertools.count(1, 0.5):
    if i > 3:
        break
    print(i)  # 1, 1.5, 2.0, 2.5, 3.0

# repeat: 값을 n번 반복
result = list(itertools.repeat(''Python'', 3))
print(result)  # [''Python'', ''Python'', ''Python'']
```

### 예제 2: 조합론 함수

```python
import itertools

# permutations: 순열 (순서 중요)
perms = list(itertools.permutations([1, 2, 3], 2))
print(perms)
# [(1, 2), (1, 3), (2, 1), (2, 3), (3, 1), (3, 2)]

# combinations: 조합 (순서 무관)
combs = list(itertools.combinations([1, 2, 3], 2))
print(combs)
# [(1, 2), (1, 3), (2, 3)]

# product: 데카르트 곱
prod = list(itertools.product([1, 2], [''a'', ''b'']))
print(prod)
# [(1, ''a''), (1, ''b''), (2, ''a''), (2, ''b'')]
```

### 예제 3: 체이닝과 그룹핑

```python
import itertools

# chain: 여러 iterable 연결
result = list(itertools.chain([1, 2], [3, 4], [5]))
print(result)  # [1, 2, 3, 4, 5]

# groupby: 연속된 값 그룹핑 (정렬 필요)
data = [1, 1, 2, 2, 2, 3, 1]
for key, group in itertools.groupby(data):
    print(f"{key}: {list(group)}")
# 1: [1, 1]
# 2: [2, 2, 2]
# 3: [3]
# 1: [1]
```

### 예제 4: 필터링 함수

```python
import itertools

# filterfalse: 조건이 False인 것만 필터링
result = list(itertools.filterfalse(lambda x: x % 2 == 0, [1, 2, 3, 4, 5]))
print(result)  # [1, 3, 5]

# takewhile: 조건이 True인 동안만 추출
result = list(itertools.takewhile(lambda x: x < 5, [1, 3, 6, 2, 1]))
print(result)  # [1, 3]

# dropwhile: 조건이 True인 동안 스킵
result = list(itertools.dropwhile(lambda x: x < 5, [1, 3, 6, 2, 1]))
print(result)  # [6, 2, 1]
```

### 예제 5: 실전 활용 - 배치 처리

```python
import itertools

def batch(iterable, n):
    """iterable을 n개씩 묶어서 반환"""
    it = iter(iterable)
    while True:
        chunk = list(itertools.islice(it, n))
        if not chunk:
            break
        yield chunk

# 데이터를 3개씩 배치 처리
data = range(10)
for batch_data in batch(data, 3):
    print(batch_data)
# [0, 1, 2]
# [3, 4, 5]
# [6, 7, 8]
# [9]
```

## 주의사항

- ⚠️ **groupby는 정렬 필요**: groupby는 연속된 값만 그룹핑합니다. 모든 같은 값을 그룹핑하려면 먼저 정렬하세요.
- ⚠️ **무한 반복자 주의**: count(), cycle() 등은 무한히 값을 생성하므로 반드시 종료 조건이 필요합니다.
- ⚠️ **일회성 소비**: itertools가 반환하는 반복자는 한 번만 순회 가능합니다. 재사용하려면 다시 생성해야 합니다.
- 💡 **메모리 효율**: 큰 데이터셋은 list()로 변환하지 말고 반복자 그대로 사용하세요.
- 💡 **combinations_with_replacement**: 중복 조합이 필요하면 이 함수를 사용하세요.

## 정리

itertools는 메모리 효율적인 반복 처리를 위한 강력한 도구입니다. 무한 반복자로 끝없는 시퀀스를 생성하고, 조합론 함수로 복잡한 순열/조합을 간단히 구현하며, 체이닝과 그룹핑으로 데이터를 유연하게 처리할 수 있습니다.

### 배운 내용
- count(), cycle(), repeat()로 무한 반복자 생성
- permutations(), combinations(), product()로 조합론 문제 해결
- chain(), groupby()로 데이터 체이닝과 그룹핑
- filterfalse(), takewhile(), dropwhile()로 효율적인 필터링
- islice()를 활용한 배치 처리 패턴
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('functools 모듈 심화', '함수형 프로그래밍을 위한 고급 도구 활용법', '# functools 모듈 심화

functools는 고차 함수(함수를 다루는 함수)를 위한 표준 라이브러리로, 함수형 프로그래밍을 더 효율적으로 구현할 수 있게 해줍니다.

## 학습 목표

- [ ] partial을 사용하여 부분 적용 함수를 생성할 수 있다
- [ ] lru_cache를 활용하여 함수 결과를 캐싱할 수 있다
- [ ] wraps를 사용하여 데코레이터를 작성할 수 있다
- [ ] total_ordering을 활용하여 비교 메서드를 자동 생성할 수 있다

## functools란?

functools는 Higher-order functions(고차 함수)를 위한 모듈로, 함수를 인자로 받거나 반환하는 작업을 쉽게 만듭니다. 반복적인 함수 호출을 최적화하고, 데코레이터 작성을 단순화하며, 함수의 일부 인자를 미리 고정하는 등의 작업을 지원합니다.

성능 최적화, 코드 재사용성 향상, 함수형 프로그래밍 패턴 구현에 필수적인 도구입니다.

## partial - 부분 적용 함수

partial은 함수의 일부 인자를 미리 고정하여 새로운 함수를 만듭니다.

```python
from functools import partial

# 기본 함수
def power(base, exponent):
    return base ** exponent

# exponent를 2로 고정
square = partial(power, exponent=2)
cube = partial(power, exponent=3)

print(square(5))  # 25
print(cube(3))    # 27
```

### 예제 1: 로깅 함수 만들기
```python
from functools import partial

def log(level, message):
    print(f"[{level}] {message}")

# 레벨별 로깅 함수 생성
info = partial(log, "INFO")
error = partial(log, "ERROR")

info("서버 시작")   # [INFO] 서버 시작
error("오류 발생")  # [ERROR] 오류 발생
```

## lru_cache - 메모이제이션

lru_cache는 함수 결과를 캐싱하여 동일한 인자로 재호출 시 성능을 극적으로 향상시킵니다.

```python
from functools import lru_cache

@lru_cache(maxsize=128)
def fibonacci(n):
    if n < 2:
        return n
    return fibonacci(n-1) + fibonacci(n-2)

print(fibonacci(100))  # 빠르게 계산됨
print(fibonacci.cache_info())  # 캐시 통계 확인
```

### 예제 2: API 호출 캐싱
```python
from functools import lru_cache
import time

@lru_cache(maxsize=32)
def fetch_user_data(user_id):
    time.sleep(1)  # API 호출 시뮬레이션
    return f"User {user_id} data"

# 첫 호출: 1초 소요
print(fetch_user_data(1))
# 재호출: 즉시 반환
print(fetch_user_data(1))
```

## wraps - 데코레이터 메타데이터 보존

wraps는 데코레이터를 작성할 때 원본 함수의 메타데이터를 보존합니다.

```python
from functools import wraps

def my_decorator(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        print("함수 실행 전")
        result = func(*args, **kwargs)
        print("함수 실행 후")
        return result
    return wrapper

@my_decorator
def greet(name):
    """인사 함수"""
    return f"안녕, {name}"

print(greet.__name__)  # ''greet'' (wraps 없으면 ''wrapper'')
print(greet.__doc__)   # ''인사 함수''
```

### 예제 3: 실행 시간 측정 데코레이터
```python
from functools import wraps
import time

def timer(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        start = time.time()
        result = func(*args, **kwargs)
        print(f"{func.__name__}: {time.time()-start:.4f}초")
        return result
    return wrapper

@timer
def slow_function():
    time.sleep(0.1)
    return "완료"

slow_function()  # slow_function: 0.1001초
```

## total_ordering - 비교 연산자 자동 생성

total_ordering은 `__eq__`와 하나의 비교 메서드만 정의하면 나머지 비교 연산자를 자동 생성합니다.

```python
from functools import total_ordering

@total_ordering
class Student:
    def __init__(self, name, grade):
        self.name = name
        self.grade = grade

    def __eq__(self, other):
        return self.grade == other.grade

    def __lt__(self, other):
        return self.grade < other.grade

s1 = Student("철수", 85)
s2 = Student("영희", 90)

print(s1 < s2)   # True
print(s1 <= s2)  # True (자동 생성)
print(s1 > s2)   # False (자동 생성)
```

### 예제 4: reduce - 누적 연산
```python
from functools import reduce

# 리스트 합계
numbers = [1, 2, 3, 4, 5]
total = reduce(lambda x, y: x + y, numbers)
print(total)  # 15

# 문자열 연결
words = ["Python", "is", "awesome"]
sentence = reduce(lambda x, y: f"{x} {y}", words)
print(sentence)  # Python is awesome
```

## 주의사항

- ⚠️ **lru_cache**: 인자가 해시 가능해야 함 (리스트, 딕셔너리는 불가)
- ⚠️ **lru_cache**: maxsize를 적절히 설정하여 메모리 사용 관리
- 💡 **wraps**: 데코레이터 작성 시 항상 사용하여 디버깅 용이성 확보
- 💡 **partial**: 콜백 함수나 이벤트 핸들러에서 유용
- ⚠️ **total_ordering**: `__eq__`와 최소 1개의 비교 메서드 필수

## 정리

functools 모듈은 함수형 프로그래밍의 핵심 도구를 제공합니다. partial로 함수 재사용성을 높이고, lru_cache로 성능을 최적화하며, wraps로 깔끔한 데코레이터를 작성할 수 있습니다.

### 배운 내용
- ✅ partial로 인자를 미리 고정한 새로운 함수 생성
- ✅ lru_cache로 함수 결과 캐싱 및 성능 향상
- ✅ wraps로 데코레이터의 메타데이터 보존
- ✅ total_ordering으로 비교 연산자 자동 구현
- ✅ reduce로 반복 가능한 객체의 누적 연산
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('os 모듈 - 운영체제 인터페이스', '파일 시스템 조작과 환경 변수 활용 방법 학습', '# os 모듈 - 운영체제 인터페이스

os 모듈은 파일 시스템, 디렉토리, 환경 변수 등 운영체제와 상호작용하는 기능을 제공합니다. 크로스 플랫폼으로 작동하여 Windows, Linux, macOS에서 동일한 코드를 사용할 수 있습니다.

## 학습 목표

- [ ] os 모듈을 사용하여 파일 시스템을 조작할 수 있다
- [ ] 디렉토리를 생성, 삭제, 탐색할 수 있다
- [ ] 환경 변수를 읽고 설정할 수 있다
- [ ] 경로 조작 함수를 활용할 수 있다
- [ ] os.path 모듈의 주요 함수를 사용할 수 있다

## os 모듈이란?

os 모듈은 운영체제에 종속적인 기능을 포터블(이식 가능한) 방식으로 사용할 수 있게 해주는 표준 라이브러리입니다. 파일과 디렉토리 관리, 프로세스 관리, 환경 변수 접근 등의 기능을 제공합니다.

실제 프로젝트에서는 로그 파일 관리, 설정 파일 경로 설정, 임시 파일 생성 등 다양한 상황에서 필수적으로 사용됩니다.

## 작업 디렉토리 관리

```python
import os

# 현재 작업 디렉토리 확인
current = os.getcwd()
print(f"현재 위치: {current}")

# 디렉토리 변경
os.chdir(''/tmp'')
print(f"변경 후: {os.getcwd()}")
```

**실행 결과:**
```
현재 위치: /Users/user/project
변경 후: /tmp
```

## 주요 예제

### 예제 1: 디렉토리 생성 및 삭제

```python
import os

# 단일 디렉토리 생성
os.mkdir(''test_dir'')

# 중첩 디렉토리 생성
os.makedirs(''parent/child/grandchild'')

# 디렉토리 삭제
os.rmdir(''test_dir'')

# 중첩 디렉토리 삭제 (비어있어야 함)
os.removedirs(''parent/child/grandchild'')
```

### 예제 2: 파일 및 디렉토리 목록 조회

```python
import os

# 현재 디렉토리의 모든 항목
items = os.listdir(''.'')
print("모든 항목:", items[:5])

# Python 파일만 필터링
py_files = [f for f in items if f.endswith(''.py'')]
print("Python 파일:", py_files)
```

### 예제 3: 디렉토리 트리 탐색

```python
import os

# 하위 디렉토리까지 재귀적으로 탐색
for root, dirs, files in os.walk(''.''):
    print(f"디렉토리: {root}")
    print(f"  하위 폴더: {dirs[:3]}")
    print(f"  파일: {files[:3]}")
    break  # 첫 번째만 출력
```

### 예제 4: 경로 조작 (os.path)

```python
import os

# 경로 결합 (OS에 맞는 구분자 사용)
path = os.path.join(''data'', ''logs'', ''app.log'')
print(f"경로: {path}")

# 경로 존재 확인
exists = os.path.exists(path)
print(f"존재 여부: {exists}")

# 파일/디렉토리 구분
print(f"파일인가? {os.path.isfile(path)}")
print(f"디렉토리인가? {os.path.isdir(''data'')}")

# 경로 분리
dirname = os.path.dirname(path)
basename = os.path.basename(path)
print(f"디렉토리: {dirname}, 파일명: {basename}")
```

### 예제 5: 환경 변수 활용

```python
import os

# 환경 변수 읽기
home = os.getenv(''HOME'')
path = os.getenv(''PATH'')
print(f"홈 디렉토리: {home}")

# 기본값 설정
api_key = os.getenv(''API_KEY'', ''default_key'')
print(f"API Key: {api_key}")

# 환경 변수 설정 (현재 프로세스에만 적용)
os.environ[''MY_VAR''] = ''my_value''
print(os.environ[''MY_VAR''])
```

### 예제 6: 파일 이름 변경 및 삭제

```python
import os

# 파일 이름 변경
if os.path.exists(''old_name.txt''):
    os.rename(''old_name.txt'', ''new_name.txt'')

# 파일 삭제
if os.path.exists(''temp_file.txt''):
    os.remove(''temp_file.txt'')

# 확장자 분리
name, ext = os.path.splitext(''document.pdf'')
print(f"파일명: {name}, 확장자: {ext}")
```

### 예제 7: 실전 활용 - 로그 디렉토리 생성

```python
import os
from datetime import datetime

# 로그 디렉토리 구조 생성
log_base = ''logs''
today = datetime.now().strftime(''%Y-%m-%d'')
log_dir = os.path.join(log_base, today)

# 디렉토리가 없으면 생성 (exist_ok=True)
os.makedirs(log_dir, exist_ok=True)

# 로그 파일 경로
log_file = os.path.join(log_dir, ''app.log'')
print(f"로그 파일 경로: {log_file}")

# 절대 경로로 변환
abs_path = os.path.abspath(log_file)
print(f"절대 경로: {abs_path}")
```

## 주의사항

- ⚠️ **경로 구분자**: 직접 `/` 또는 `\`를 사용하지 말고 `os.path.join()` 사용
- ⚠️ **디렉토리 삭제**: `rmdir()`은 빈 디렉토리만 삭제 가능 (파일 있으면 오류)
- ⚠️ **환경 변수 변경**: `os.environ` 수정은 현재 프로세스에만 영향
- 💡 **경로 존재 확인**: 파일 작업 전 `os.path.exists()` 먼저 확인
- 💡 **makedirs vs mkdir**: 중첩 디렉토리는 `makedirs()` 사용
- 💡 **절대 경로 권장**: `os.path.abspath()`로 절대 경로 변환 권장

## 정리

os 모듈은 파일 시스템과 운영체제를 다루는 핵심 도구입니다. 크로스 플랫폼 호환성을 보장하며, 경로 조작과 디렉토리 관리에 필수적입니다.

### 배운 내용
- ✅ `getcwd()`, `chdir()`로 작업 디렉토리 관리
- ✅ `mkdir()`, `makedirs()`로 디렉토리 생성
- ✅ `listdir()`, `walk()`로 파일 시스템 탐색
- ✅ `os.path.join()`, `exists()` 등으로 경로 조작
- ✅ `os.getenv()`, `os.environ`으로 환경 변수 활용
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('sys 모듈 - 시스템 인터페이스', 'Python에서 시스템 정보 조회 및 명령줄 인자 처리 방법', '# sys 모듈 - 시스템 인터페이스

sys 모듈은 Python 인터프리터와 시스템 간의 인터페이스를 제공하는 표준 라이브러리입니다. 명령줄 인자 처리, 시스템 정보 조회, 표준 입출력 제어 등 프로그램이 실행되는 환경과 상호작용할 수 있습니다.

## 학습 목표

- [ ] sys 모듈을 사용하여 시스템 정보를 얻을 수 있다
- [ ] 명령줄 인자를 처리할 수 있다
- [ ] 표준 입출력 스트림을 제어할 수 있다
- [ ] Python 실행 환경 정보를 얻을 수 있다

## sys 모듈이란?

sys(system-specific parameters and functions) 모듈은 Python 인터프리터와 운영체제 사이의 상호작용을 담당합니다. 프로그램이 실행되는 환경의 정보를 얻거나, 명령줄에서 전달된 인자를 처리하거나, 프로그램의 실행 흐름을 제어하는 등의 작업에 필수적입니다.

특히 CLI(Command Line Interface) 도구를 개발하거나, 스크립트의 동작을 실행 환경에 따라 다르게 해야 할 때 유용합니다. 내장 모듈이므로 별도 설치 없이 바로 사용할 수 있습니다.

## 기본 사용법

```python
import sys

# 명령줄 인자 확인
print(f"프로그램 이름: {sys.argv[0]}")
print(f"전달된 인자: {sys.argv[1:]}")

# 시스템 정보
print(f"플랫폼: {sys.platform}")
print(f"Python 버전: {sys.version_info.major}.{sys.version_info.minor}")
```

sys.argv는 명령줄에서 전달된 모든 인자를 리스트로 제공하며, 첫 번째 요소는 항상 스크립트 이름입니다.

## 주요 예제

### 예제 1: 명령줄 인자 처리
```python
import sys

# script.py name age city
if len(sys.argv) < 4:
    print("사용법: python script.py <이름> <나이> <도시>")
    sys.exit(1)

name = sys.argv[1]
age = int(sys.argv[2])
city = sys.argv[3]

print(f"{name}님은 {age}살이고 {city}에 살고 있습니다.")
# 출력: 홍길동님은 25살이고 서울에 살고 있습니다.
```

### 예제 2: 표준 출력 리디렉션
```python
import sys

# 원래 stdout 저장
original_stdout = sys.stdout

# 파일로 출력 리디렉션
with open(''output.txt'', ''w'') as f:
    sys.stdout = f
    print("이 내용은 파일에 저장됩니다")

# stdout 복원
sys.stdout = original_stdout
print("이 내용은 화면에 출력됩니다")
```

### 예제 3: Python 경로 관리
```python
import sys

# 현재 Python 경로 확인
for path in sys.path[:3]:
    print(path)

# 새 경로 추가 (임시)
sys.path.insert(0, ''/custom/module/path'')
# 이제 해당 경로의 모듈을 import 가능
```

### 예제 4: 플랫폼별 분기 처리
```python
import sys

if sys.platform == ''win32'':
    separator = ''\\''
    config_path = ''C:\\Config''
elif sys.platform in [''linux'', ''darwin'']:
    separator = ''/''
    config_path = ''/etc/config''

print(f"설정 파일: {config_path}{separator}app.conf")
```

### 예제 5: 표준 에러 출력
```python
import sys

def log_error(message):
    # 에러는 stderr로 출력
    print(f"ERROR: {message}", file=sys.stderr)

def process_data(data):
    if not data:
        log_error("데이터가 비어있습니다")
        return False
    print(f"처리 완료: {len(data)}개 항목")
    return True

process_data([])  # ERROR 메시지는 stderr로 출력
```

## 주의사항

- 명령줄 인자는 항상 문자열로 전달되므로 필요시 형변환이 필요합니다
- sys.exit()는 SystemExit 예외를 발생시키므로 finally 블록이 실행됩니다
- sys.stdout 리디렉션 후에는 반드시 원래 상태로 복원해야 합니다
- sys.path 수정은 현재 실행 중인 프로그램에만 영향을 미칩니다
- sys.platform 값은 운영체제에 따라 ''win32'', ''linux'', ''darwin''(macOS) 등으로 다릅니다

## 정리

sys 모듈은 Python 프로그램이 실행 환경과 상호작용하는 핵심 인터페이스입니다. 명령줄 인자 처리, 표준 입출력 제어, 시스템 정보 조회 등을 통해 더 유연하고 강력한 프로그램을 작성할 수 있습니다.

### 배운 내용
- sys.argv로 명령줄 인자를 처리하고 입력 검증을 수행할 수 있습니다
- sys.stdin/stdout/stderr로 표준 스트림을 제어할 수 있습니다
- sys.path로 모듈 검색 경로를 관리할 수 있습니다
- sys.platform과 sys.version_info로 실행 환경을 파악할 수 있습니다
- sys.exit()로 프로그램 종료 상태를 명시적으로 제어할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('re 모듈 - 정규표현식', '정규표현식 패턴 매칭과 re 모듈 활용법 학습', '# re 모듈 - 정규표현식

정규표현식은 특정 패턴의 문자열을 검색, 추출, 치환하는 강력한 도구입니다. Python의 re 모듈을 사용하면 복잡한 텍스트 처리 작업을 간결하게 수행할 수 있습니다.

## 학습 목표

- [ ] 정규표현식의 기본 문법과 메타문자를 이해하고 사용할 수 있다
- [ ] re 모듈의 주요 함수로 패턴 매칭과 검색을 수행할 수 있다
- [ ] 그룹과 캡처를 활용하여 복잡한 패턴을 처리할 수 있다
- [ ] 플래그를 사용하여 검색 옵션을 제어할 수 있다
- [ ] 정규표현식으로 문자열 치환과 분리를 수행할 수 있다

## 정규표현식이란?

정규표현식(Regular Expression, Regex)은 문자열에서 특정 패턴을 찾기 위한 형식 언어입니다. 예를 들어 이메일 주소, 전화번호, URL 등의 형식을 검증하거나 추출할 때 필수적입니다.

re 모듈은 Perl 스타일의 정규표현식을 지원하며, 패턴 컴파일과 매칭을 효율적으로 처리합니다.

## 기본 메타문자

```python
import re

# . : 줄바꿈을 제외한 모든 문자 하나
print(re.findall(''a.c'', ''abc adc a\nc''))  # [''abc'', ''adc'']

# * : 0회 이상 반복
print(re.findall(''ab*c'', ''ac abc abbc''))  # [''ac'', ''abc'', ''abbc'']

# + : 1회 이상 반복
print(re.findall(''ab+c'', ''ac abc abbc''))  # [''abc'', ''abbc'']

# ? : 0회 또는 1회
print(re.findall(''ab?c'', ''ac abc abbc''))  # [''ac'', ''abc'']

# [] : 문자 클래스
print(re.findall(''[0-9]+'', ''a1b23c456''))  # [''1'', ''23'', ''456'']
```

## 주요 예제

### 예제 1: match vs search

```python
import re

text = "Python 3.9 released"

# match: 문자열 시작부터 매칭
result1 = re.match(r''\d+'', text)
print(result1)  # None

# search: 문자열 전체에서 첫 매칭
result2 = re.search(r''\d+'', text)
print(result2.group())  # ''3''
```

### 예제 2: findall과 finditer

```python
import re

text = "Email: user@example.com, admin@test.org"

# findall: 모든 매칭을 리스트로 반환
emails = re.findall(r''[\w.-]+@[\w.-]+'', text)
print(emails)  # [''user@example.com'', ''admin@test.org'']

# finditer: 매칭 객체를 반복자로 반환
for match in re.finditer(r''[\w.-]+@[\w.-]+'', text):
    print(f"{match.group()} at {match.span()}")
```

### 예제 3: 그룹과 캡처

```python
import re

text = "2025-01-15"

# 그룹으로 날짜 파싱
match = re.search(r''(\d{4})-(\d{2})-(\d{2})'', text)
if match:
    year, month, day = match.groups()
    print(f"Year: {year}, Month: {month}, Day: {day}")

# 명명된 그룹
pattern = r''(?P<year>\d{4})-(?P<month>\d{2})-(?P<day>\d{2})''
match = re.search(pattern, text)
print(match.group(''year''))  # ''2025''
```

### 예제 4: sub로 문자열 치환

```python
import re

text = "Contact: 010-1234-5678, 02-9876-5432"

# 전화번호 마스킹
masked = re.sub(r''(\d{2,3})-(\d{3,4})-\d{4}'',
                r''\1-\2-****'', text)
print(masked)  # ''Contact: 010-1234-****, 02-9876-****''

# 함수로 치환
def upper_replace(match):
    return match.group().upper()

result = re.sub(r''\b[a-z]+\b'', upper_replace, ''hello world'')
print(result)  # ''HELLO WORLD''
```

### 예제 5: split과 플래그

```python
import re

# split: 패턴으로 문자열 분리
text = "apple,banana;cherry orange"
items = re.split(r''[,;\s]+'', text)
print(items)  # [''apple'', ''banana'', ''cherry'', ''orange'']

# IGNORECASE 플래그: 대소문자 무시
text = "Python python PYTHON"
matches = re.findall(r''python'', text, re.IGNORECASE)
print(len(matches))  # 3

# MULTILINE 플래그: ^와 $가 각 줄에 적용
text = "start\nmiddle\nend"
matches = re.findall(r''^[a-z]+'', text, re.MULTILINE)
print(matches)  # [''start'', ''middle'', ''end'']
```

## 고급 패턴

### 앵커와 경계

```python
import re

# ^ : 문자열 시작, $ : 문자열 끝
print(re.search(r''^Python'', ''Python is great''))  # 매칭
print(re.search(r''great$'', ''Python is great''))   # 매칭

# \b : 단어 경계
text = "python in pythonic"
print(re.findall(r''\bpython\b'', text))  # [''python'']
```

### 전방탐색과 후방탐색

```python
import re

# 긍정 전방탐색: (?=...)
text = "price: $100, discount: $20"
prices = re.findall(r''\$(?=\d+)'', text)
print(len(prices))  # 2

# 부정 전방탐색: (?!...)
words = re.findall(r''\b\w+(?!ing\b)'', ''running jumping walk'')
print(words)  # [''runnin'', ''jumpin'', ''walk'']
```

## 컴파일과 재사용

```python
import re

# 패턴 컴파일 (반복 사용 시 성능 향상)
email_pattern = re.compile(r''[\w.-]+@[\w.-]+\.\w+'')

texts = [
    "Contact: user@example.com",
    "Support: help@test.org",
    "Invalid: notanemail"
]

for text in texts:
    match = email_pattern.search(text)
    if match:
        print(f"Found: {match.group()}")
```

## 주의사항

- **백슬래시 처리**: 일반 문자열에서 `\d`는 `\\d`로 작성해야 하므로 raw string(`r''\d''`) 사용을 권장합니다
- **탐욕적 매칭**: `*`, `+`는 기본적으로 탐욕적입니다. 최소 매칭은 `*?`, `+?`를 사용하세요
- **성능**: 복잡한 패턴은 성능에 영향을 줄 수 있습니다. 반복 사용 시 `compile()`로 미리 컴파일하세요
- **가독성**: 복잡한 패턴은 `re.VERBOSE` 플래그로 주석을 추가하여 가독성을 높일 수 있습니다

## 정리

정규표현식은 텍스트 처리의 스위스 아미 나이프입니다. re 모듈의 함수들을 적재적소에 활용하면 복잡한 문자열 작업을 간결하게 해결할 수 있습니다.

### 배운 내용

- ✅ 정규표현식 메타문자(`.`, `*`, `+`, `?`, `[]`, `^`, `$`)로 패턴을 정의했습니다
- ✅ `match()`, `search()`, `findall()`로 패턴 매칭과 검색을 수행했습니다
- ✅ 그룹 `()`과 `\1`, `\2`로 패턴을 캡처하고 재사용했습니다
- ✅ `sub()`와 `split()`으로 문자열을 치환하고 분리했습니다
- ✅ `IGNORECASE`, `MULTILINE` 등의 플래그로 검색 옵션을 제어했습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('datetime 모듈 - 날짜와 시간', '날짜와 시간을 다루는 datetime 모듈 활용법', '# datetime 모듈 - 날짜와 시간

날짜와 시간 데이터를 다루는 것은 프로그래밍에서 매우 흔한 작업입니다. Python의 datetime 모듈은 날짜와 시간을 생성하고, 조작하고, 형식화하는 강력한 기능을 제공합니다.

## 학습 목표

- [ ] datetime 모듈의 주요 클래스를 사용할 수 있다
- [ ] 날짜와 시간을 생성하고 조작할 수 있다
- [ ] 날짜/시간 연산을 수행할 수 있다
- [ ] 문자열과 datetime 객체 간 변환을 수행할 수 있다
- [ ] 시간대(timezone) 개념을 이해한다

## datetime 모듈의 주요 클래스

datetime 모듈은 4개의 핵심 클래스를 제공합니다:
- `date`: 날짜만 표현 (년, 월, 일)
- `time`: 시간만 표현 (시, 분, 초, 마이크로초)
- `datetime`: 날짜와 시간을 모두 표현
- `timedelta`: 두 날짜/시간 간의 차이를 표현

## 현재 날짜와 시간 가져오기

```python
from datetime import datetime, date, time

# 현재 날짜와 시간
now = datetime.now()
print(f"현재: {now}")  # 2025-10-25 14:30:45.123456

# 오늘 날짜만
today = date.today()
print(f"오늘: {today}")  # 2025-10-25
```

`now()`는 현재 날짜와 시간을 모두 반환하고, `today()`는 날짜만 반환합니다.

## 날짜와 시간 생성하기

```python
from datetime import datetime, date, time

# 특정 날짜 생성
birthday = date(1990, 5, 15)
print(birthday)  # 1990-05-15

# 특정 시간 생성
meeting = time(14, 30, 0)
print(meeting)  # 14:30:00

# 특정 날짜와 시간 생성
event = datetime(2025, 12, 25, 9, 0, 0)
print(event)  # 2025-12-25 09:00:00
```

## 속성 접근하기

```python
from datetime import datetime

now = datetime.now()
print(f"년: {now.year}")
print(f"월: {now.month}")
print(f"일: {now.day}")
print(f"시: {now.hour}")
print(f"분: {now.minute}")
print(f"초: {now.second}")
print(f"요일: {now.weekday()}")  # 0=월요일, 6=일요일
```

각 구성 요소에 개별적으로 접근할 수 있습니다.

## 주요 예제

### 예제 1: timedelta로 날짜 연산하기
```python
from datetime import datetime, timedelta

today = datetime.now()
print(f"오늘: {today.date()}")

# 7일 후
week_later = today + timedelta(days=7)
print(f"7일 후: {week_later.date()}")

# 3시간 전
three_hours_ago = today - timedelta(hours=3)
print(f"3시간 전: {three_hours_ago}")
```

### 예제 2: 두 날짜 간 차이 계산
```python
from datetime import datetime

start = datetime(2025, 1, 1)
end = datetime(2025, 12, 31)

difference = end - start
print(f"일수 차이: {difference.days}일")
print(f"총 초: {difference.total_seconds()}초")
```

### 예제 3: 문자열을 datetime으로 변환
```python
from datetime import datetime

# 문자열 → datetime (strptime)
date_str = "2025-10-25 14:30:00"
dt = datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S")
print(dt)  # 2025-10-25 14:30:00

# datetime → 문자열 (strftime)
formatted = dt.strftime("%Y년 %m월 %d일 %H시 %M분")
print(formatted)  # 2025년 10월 25일 14시 30분
```

### 예제 4: 날짜 비교하기
```python
from datetime import datetime

now = datetime.now()
future = datetime(2026, 1, 1)
past = datetime(2024, 1, 1)

print(now > past)    # True
print(now < future)  # True
print(now == now)    # True
```

### 예제 5: 실용적인 활용 - D-Day 계산기
```python
from datetime import datetime

def calculate_dday(target_date_str):
    target = datetime.strptime(target_date_str, "%Y-%m-%d")
    today = datetime.now()
    diff = target - today

    return diff.days

exam_day = "2025-11-15"
days_left = calculate_dday(exam_day)
print(f"시험까지 D-{days_left}일")
```

## 주요 포맷 코드

strftime/strptime에서 사용하는 주요 포맷 코드:
- `%Y`: 4자리 연도 (2025)
- `%m`: 2자리 월 (01-12)
- `%d`: 2자리 일 (01-31)
- `%H`: 24시간 형식 시 (00-23)
- `%M`: 분 (00-59)
- `%S`: 초 (00-59)
- `%A`: 요일 전체 이름 (Monday)
- `%B`: 월 전체 이름 (January)

## 주의사항

- ⚠️ strptime() 사용 시 포맷 문자열이 정확히 일치해야 합니다
- ⚠️ datetime 객체는 불변(immutable)이므로 연산 결과는 새 객체입니다
- 💡 시간대 처리가 필요하면 `pytz` 라이브러리나 `timezone` 클래스를 사용하세요
- 💡 날짜 범위 검증이 필요한 경우 try-except로 ValueError를 처리하세요

## 정리

datetime 모듈은 Python에서 날짜와 시간을 다루는 표준 방법입니다. date, time, datetime, timedelta 클래스를 통해 날짜 생성, 연산, 비교, 형식 변환 등 다양한 작업을 수행할 수 있습니다.

### 배운 내용
- ✅ datetime, date, time, timedelta 클래스의 역할과 사용법
- ✅ now()와 today()로 현재 날짜/시간 가져오기
- ✅ timedelta를 사용한 날짜/시간 연산
- ✅ strftime()과 strptime()으로 문자열 변환
- ✅ 날짜 비교와 실용적인 활용 방법
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('pathlib 모듈 - 객체지향 경로', 'Path 객체로 파일/디렉토리를 객체지향 방식으로 다루기', '# pathlib 모듈 - 객체지향 경로

pathlib은 파일 시스템 경로를 객체지향 방식으로 다루는 Python 3.4+ 표준 라이브러리입니다. 기존 os.path보다 직관적이고 강력한 인터페이스를 제공합니다.

## 학습 목표

- [ ] Path 객체를 생성하고 조작할 수 있다
- [ ] 경로 연산을 객체지향 방식으로 수행할 수 있다
- [ ] 파일/디렉토리 정보를 얻을 수 있다
- [ ] os.path보다 pathlib의 장점을 이해한다
- [ ] 파일 읽기/쓰기를 Path 객체로 수행할 수 있다

## pathlib이란?

pathlib은 파일 시스템 경로를 객체로 표현하여, 메서드 체이닝과 직관적인 연산자를 사용할 수 있게 해줍니다. os.path의 문자열 기반 접근 방식과 달리, Path 객체는 경로 조작, 파일 읽기/쓰기, 디렉토리 탐색 등을 하나의 객체로 수행할 수 있습니다.

Python 3.6부터 대부분의 파일 관련 함수들이 Path 객체를 직접 받을 수 있어, 실용성이 크게 향상되었습니다. 운영체제에 따라 자동으로 적절한 경로 구분자를 사용하므로, 크로스 플랫폼 코드 작성이 용이합니다.

## 기본 사용법

```python
from pathlib import Path

# Path 객체 생성
p = Path(''data/file.txt'')
print(p)  # data/file.txt

# 현재 디렉토리
current = Path.cwd()
print(current)  # /Users/user/project

# 홈 디렉토리
home = Path.home()
print(home)  # /Users/user
```

Path() 생성자로 경로 문자열을 Path 객체로 변환하며, cwd()와 home()으로 시스템 경로를 얻을 수 있습니다.

## 주요 예제

### 예제 1: 경로 연산
```python
from pathlib import Path

# / 연산자로 경로 결합
base = Path(''project'')
file_path = base / ''src'' / ''main.py''
print(file_path)  # project/src/main.py

# 경로 속성 접근
print(file_path.parent)      # project/src
print(file_path.name)        # main.py
print(file_path.suffix)      # .py
print(file_path.stem)        # main
```

### 예제 2: 파일/디렉토리 조작
```python
from pathlib import Path

# 디렉토리 생성
new_dir = Path(''output/results'')
new_dir.mkdir(parents=True, exist_ok=True)

# 파일 존재 확인 및 정보
file = Path(''data.txt'')
print(file.exists())    # True/False
print(file.is_file())   # True/False
print(file.is_dir())    # True/False

# 파일 삭제 및 이름 변경
# file.unlink()  # 파일 삭제
# file.rename(''new_data.txt'')  # 이름 변경
```

### 예제 3: 디렉토리 탐색
```python
from pathlib import Path

# 특정 패턴 파일 찾기
project = Path(''.'')

# 현재 디렉토리의 모든 .py 파일
py_files = list(project.glob(''*.py''))
print(py_files)

# 하위 디렉토리 포함 모든 .txt 파일
all_txt = list(project.rglob(''*.txt''))
print(all_txt)

# 디렉토리 내용 순회
for item in project.iterdir():
    print(f"{item.name} - {''DIR'' if item.is_dir() else ''FILE''}")
```

### 예제 4: 파일 읽기/쓰기
```python
from pathlib import Path

# 텍스트 파일 쓰기
output = Path(''result.txt'')
output.write_text(''Hello, pathlib!\nLine 2'')

# 텍스트 파일 읽기
content = output.read_text()
print(content)

# 바이너리 파일 다루기
data = b''\x00\x01\x02\x03''
binary_file = Path(''data.bin'')
binary_file.write_bytes(data)
loaded = binary_file.read_bytes()
print(loaded)  # b''\x00\x01\x02\x03''
```

### 예제 5: os.path 대비 장점
```python
from pathlib import Path
import os

# os.path 방식 (문자열 기반)
old_path = os.path.join(''data'', ''users'', ''info.txt'')
old_name = os.path.basename(old_path)
old_dir = os.path.dirname(old_path)

# pathlib 방식 (객체 지향)
new_path = Path(''data'') / ''users'' / ''info.txt''
new_name = new_path.name
new_dir = new_path.parent

# pathlib은 메서드 체이닝 가능
result = Path(''data'').joinpath(''users'', ''info.txt'').with_suffix(''.csv'')
print(result)  # data/users/info.csv
```

## 주의사항

- Path 객체는 문자열이 필요한 곳에 자동 변환되지만, 명시적으로 `str(path)`를 사용하는 것이 명확합니다
- `unlink()`와 `rmdir()`은 파일이나 디렉토리가 없으면 오류를 발생시키므로, `exists()` 확인이나 `missing_ok=True` 옵션을 사용하세요
- `glob()`과 `rglob()`은 제너레이터를 반환하므로, 여러 번 사용하려면 `list()`로 변환해야 합니다
- Windows와 POSIX 시스템 간 경로 차이를 PurePath로 테스트할 수 있습니다

## 정리

pathlib은 파일 시스템 작업을 객체지향 방식으로 수행할 수 있게 하여, 코드의 가독성과 유지보수성을 크게 향상시킵니다. / 연산자를 통한 직관적인 경로 결합, 메서드 체이닝, 그리고 파일 읽기/쓰기까지 통합된 인터페이스를 제공합니다.

### 배운 내용
- Path 객체 생성 및 경로 연산 (/, parent, name, suffix)
- 파일/디렉토리 조작 (mkdir, unlink, rename, exists)
- 디렉토리 탐색 (glob, rglob, iterdir)
- 파일 읽기/쓰기 (read_text, write_text, read_bytes, write_bytes)
- os.path 대비 pathlib의 객체지향적 장점
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('json 모듈 - JSON 직렬화', 'Python 객체와 JSON 간 변환 및 커스텀 직렬화 구현', '# json 모듈 - JSON 직렬화

JSON(JavaScript Object Notation)은 데이터 교환을 위한 경량 텍스트 형식입니다. Python의 json 모듈은 Python 객체를 JSON으로 변환(직렬화)하고, JSON을 Python 객체로 변환(역직렬화)하는 기능을 제공합니다.

## 학습 목표

- [ ] JSON 형식과 Python 자료형 간의 매핑 관계를 이해한다
- [ ] dumps/loads로 문자열 변환, dump/load로 파일 변환을 수행한다
- [ ] indent와 sort_keys로 JSON 출력을 제어한다
- [ ] 커스텀 인코더/디코더를 작성하여 복잡한 객체를 직렬화한다

## JSON이란?

JSON은 웹 API, 설정 파일, 데이터 저장 등에 널리 사용되는 표준 포맷입니다. Python의 딕셔너리와 유사한 구조를 가지며, 대부분의 프로그래밍 언어에서 지원합니다.

Python과 JSON 간 자료형 매핑은 다음과 같습니다:
- Python dict ↔ JSON object
- Python list/tuple ↔ JSON array
- Python str ↔ JSON string
- Python int/float ↔ JSON number
- Python True/False ↔ JSON true/false
- Python None ↔ JSON null

## 기본 사용법

```python
import json

# Python 객체를 JSON 문자열로 변환
data = {"name": "Alice", "age": 25, "active": True}
json_str = json.dumps(data)
print(json_str)  # {"name": "Alice", "age": 25, "active": true}

# JSON 문자열을 Python 객체로 변환
parsed = json.loads(json_str)
print(parsed["name"])  # Alice
```

## 주요 예제

### 예제 1: 파일로 저장하고 읽기
```python
import json

# 딕셔너리를 JSON 파일로 저장
user_data = {"id": 101, "name": "Bob", "scores": [85, 90, 78]}

with open("user.json", "w", encoding="utf-8") as f:
    json.dump(user_data, f, indent=2)

# JSON 파일 읽기
with open("user.json", "r", encoding="utf-8") as f:
    loaded = json.load(f)
    print(loaded)  # {''id'': 101, ''name'': ''Bob'', ''scores'': [85, 90, 78]}
```

### 예제 2: 들여쓰기와 정렬
```python
import json

data = {"name": "Charlie", "age": 30, "city": "Seoul"}

# 기본 출력 (압축됨)
print(json.dumps(data))
# {"name": "Charlie", "age": 30, "city": "Seoul"}

# 들여쓰기 + 키 정렬
formatted = json.dumps(data, indent=4, sort_keys=True)
print(formatted)
# {
#     "age": 30,
#     "city": "Seoul",
#     "name": "Charlie"
# }
```

### 예제 3: 리스트 직렬화
```python
import json

# 리스트와 중첩 구조
students = [
    {"name": "Alice", "grade": "A"},
    {"name": "Bob", "grade": "B"}
]

json_str = json.dumps(students, indent=2)
print(json_str)

# 역직렬화
parsed = json.loads(json_str)
print(parsed[0]["name"])  # Alice
```

### 예제 4: 커스텀 객체 직렬화
```python
import json
from datetime import datetime

class User:
    def __init__(self, name, created):
        self.name = name
        self.created = created

# 커스텀 인코더
class UserEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, User):
            return {"name": obj.name, "created": obj.created.isoformat()}
        if isinstance(obj, datetime):
            return obj.isoformat()
        return super().default(obj)

user = User("Diana", datetime(2024, 1, 15))
json_str = json.dumps(user, cls=UserEncoder, indent=2)
print(json_str)
# {"name": "Diana", "created": "2024-01-15T00:00:00"}
```

### 예제 5: 커스텀 디코더
```python
import json
from datetime import datetime

def user_decoder(dct):
    if "created" in dct:
        dct["created"] = datetime.fromisoformat(dct["created"])
    return dct

json_str = ''{"name": "Eve", "created": "2024-02-20T10:30:00"}''
user = json.loads(json_str, object_hook=user_decoder)
print(user["created"])  # 2024-02-20 10:30:00
print(type(user["created"]))  # <class ''datetime.datetime''>
```

## 주의사항

- ⚠️ **인코딩 지정**: 파일 읽기/쓰기 시 `encoding="utf-8"` 명시
- ⚠️ **직렬화 불가능한 타입**: set, datetime 등은 기본적으로 직렬화 불가
- ⚠️ **순환 참조**: 객체가 자기 자신을 참조하면 RecursionError 발생
- 💡 **ensure_ascii=False**: 한글을 유니코드 이스케이프 없이 저장
- 💡 **default 함수**: 간단한 커스텀 직렬화는 default 매개변수 사용
- 💡 **API 통신**: requests 라이브러리와 함께 사용하여 API 데이터 처리

## 정리

json 모듈은 Python 객체와 JSON 문자열 간 변환을 제공하는 표준 라이브러리입니다. dumps/loads는 문자열 변환에, dump/load는 파일 변환에 사용됩니다. 커스텀 인코더/디코더를 작성하면 복잡한 객체도 직렬화할 수 있어, 웹 API 개발과 데이터 저장에 필수적입니다.

### 배운 내용
- ✅ JSON과 Python 자료형의 매핑 관계(dict↔object, list↔array 등)
- ✅ dumps/loads(문자열), dump/load(파일) 함수 사용법
- ✅ indent와 sort_keys로 JSON 출력 포맷 제어
- ✅ JSONEncoder/object_hook으로 커스텀 객체 직렬화/역직렬화
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('csv 모듈 - CSV 파일 처리', 'CSV 파일 읽기/쓰기와 다양한 방언 처리 방법', '# csv 모듈 - CSV 파일 처리

CSV(Comma-Separated Values)는 데이터를 주고받을 때 가장 널리 사용되는 텍스트 파일 형식입니다. Python의 csv 모듈은 CSV 파일을 쉽게 읽고 쓸 수 있는 기능을 제공합니다.

## 학습 목표

- [ ] CSV 형식을 이해하고 설명할 수 있다
- [ ] csv.reader와 csv.writer를 사용하여 CSV 파일을 읽고 쓸 수 있다
- [ ] DictReader와 DictWriter를 활용하여 딕셔너리 형태로 데이터를 처리할 수 있다
- [ ] delimiter, quotechar 등의 옵션을 조정하여 다양한 형식을 처리할 수 있다
- [ ] CSV 방언(dialect)을 이해하고 커스텀 방언을 등록할 수 있다

## CSV 형식이란?

CSV는 각 행이 데이터 레코드를 나타내고, 쉼표(,)로 필드를 구분하는 텍스트 파일 형식입니다. 스프레드시트 프로그램과 데이터베이스 간의 데이터 교환에 자주 사용됩니다.

CSV 파일은 간단해 보이지만 따옴표 처리, 줄바꿈, 특수문자 등의 예외 상황이 있어 직접 파싱하는 것보다 csv 모듈을 사용하는 것이 안전합니다. csv 모듈은 이러한 복잡한 케이스를 자동으로 처리해줍니다.

## csv.reader와 csv.writer

### csv.reader - CSV 파일 읽기

```python
import csv

# CSV 파일 읽기
with open(''data.csv'', ''r'', encoding=''utf-8'') as f:
    reader = csv.reader(f)
    for row in reader:
        print(row)  # 각 row는 리스트
```

### csv.writer - CSV 파일 쓰기

```python
import csv

data = [
    [''이름'', ''나이'', ''도시''],
    [''홍길동'', ''30'', ''서울''],
    [''김철수'', ''25'', ''부산'']
]

with open(''output.csv'', ''w'', newline='''', encoding=''utf-8'') as f:
    writer = csv.writer(f)
    writer.writerows(data)  # 여러 행 한번에 쓰기
```

## DictReader와 DictWriter

딕셔너리 형태로 데이터를 다루면 컬럼명으로 접근할 수 있어 가독성이 좋습니다.

### DictReader - 딕셔너리로 읽기

```python
import csv

with open(''data.csv'', ''r'', encoding=''utf-8'') as f:
    reader = csv.DictReader(f)
    for row in reader:
        print(row[''이름''], row[''나이''])
        # 첫 행을 자동으로 헤더로 인식
```

### DictWriter - 딕셔너리로 쓰기

```python
import csv

data = [
    {''이름'': ''홍길동'', ''나이'': 30, ''도시'': ''서울''},
    {''이름'': ''김철수'', ''나이'': 25, ''도시'': ''부산''}
]

with open(''output.csv'', ''w'', newline='''', encoding=''utf-8'') as f:
    writer = csv.DictWriter(f, fieldnames=[''이름'', ''나이'', ''도시''])
    writer.writeheader()  # 헤더 행 쓰기
    writer.writerows(data)
```

## 주요 예제

### 예제 1: delimiter 옵션 - TSV 파일 처리

```python
import csv

# 탭으로 구분된 파일 읽기
with open(''data.tsv'', ''r'', encoding=''utf-8'') as f:
    reader = csv.reader(f, delimiter=''\t'')
    for row in reader:
        print(row)

# 세미콜론으로 구분된 파일 쓰기
with open(''output.csv'', ''w'', newline='''', encoding=''utf-8'') as f:
    writer = csv.writer(f, delimiter='';'')
    writer.writerow([''A'', ''B'', ''C''])
```

### 예제 2: quotechar와 quoting 옵션

```python
import csv

data = [[''이름'', ''설명''], [''제품A'', ''특별한 "고급" 제품'']]

# 따옴표 처리 옵션
with open(''output.csv'', ''w'', newline='''', encoding=''utf-8'') as f:
    writer = csv.writer(f, quotechar=''"'',
                       quoting=csv.QUOTE_MINIMAL)
    writer.writerows(data)
    # QUOTE_MINIMAL: 필요할 때만 따옴표 사용
    # QUOTE_ALL: 모든 필드에 따옴표
    # QUOTE_NONNUMERIC: 숫자가 아닌 필드에만
```

### 예제 3: CSV 방언(Dialect) 사용

```python
import csv

# Excel 방언으로 읽기
with open(''excel_data.csv'', ''r'', encoding=''utf-8'') as f:
    reader = csv.reader(f, dialect=''excel'')
    for row in reader:
        print(row)

# 커스텀 방언 등록
csv.register_dialect(''custom'',
                     delimiter=''|'',
                     quotechar=''"'',
                     quoting=csv.QUOTE_ALL)

with open(''output.csv'', ''w'', newline='''', encoding=''utf-8'') as f:
    writer = csv.writer(f, dialect=''custom'')
    writer.writerow([''A'', ''B'', ''C''])
```

### 예제 4: 실전 데이터 처리

```python
import csv

# CSV 데이터 필터링 및 변환
with open(''sales.csv'', ''r'', encoding=''utf-8'') as infile:
    reader = csv.DictReader(infile)

    # 필터링된 데이터를 새 파일로 저장
    with open(''filtered.csv'', ''w'', newline='''', encoding=''utf-8'') as outfile:
        fieldnames = [''제품명'', ''판매량'', ''매출'']
        writer = csv.DictWriter(outfile, fieldnames=fieldnames)
        writer.writeheader()

        for row in reader:
            if int(row[''판매량'']) > 100:
                writer.writerow({
                    ''제품명'': row[''제품명''],
                    ''판매량'': row[''판매량''],
                    ''매출'': row[''매출'']
                })
```

### 예제 5: 에러 처리

```python
import csv

try:
    with open(''data.csv'', ''r'', encoding=''utf-8'') as f:
        reader = csv.reader(f)
        for i, row in enumerate(reader, 1):
            try:
                # 데이터 검증
                if len(row) != 3:
                    print(f"경고: {i}행의 컬럼 수가 맞지 않음")
                    continue
                # 데이터 처리
                print(row)
            except Exception as e:
                print(f"오류 ({i}행): {e}")
except FileNotFoundError:
    print("파일을 찾을 수 없습니다")
except csv.Error as e:
    print(f"CSV 파싱 오류: {e}")
```

## 주의사항

- newline='''' 파라미터는 Windows에서 줄바꿈 문제를 방지합니다
- encoding=''utf-8''을 명시하여 한글 깨짐을 방지하세요
- 큰 파일은 한 줄씩 처리하여 메모리를 절약하세요
- DictReader 사용 시 헤더가 있는 CSV 파일인지 확인하세요
- quoting 옵션을 잘못 설정하면 데이터가 손상될 수 있습니다

## 정리

csv 모듈은 CSV 파일을 안전하고 효율적으로 처리할 수 있게 해줍니다. reader/writer는 간단한 리스트 형태로, DictReader/DictWriter는 딕셔너리 형태로 데이터를 다룹니다. delimiter, quotechar 등의 옵션과 방언(dialect)을 활용하면 다양한 형식의 CSV 파일을 유연하게 처리할 수 있습니다.

### 배운 내용
- CSV 형식과 csv 모듈의 필요성을 이해했습니다
- reader/writer와 DictReader/DictWriter의 사용법을 익혔습니다
- delimiter, quotechar, quoting 옵션으로 다양한 형식을 처리했습니다
- CSV 방언(dialect)을 이해하고 커스텀 방언을 등록했습니다
- 실전 데이터 처리와 에러 핸들링 방법을 배웠습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('매직 메서드 완전 정복', '특수 메서드로 파이썬 객체의 동작을 커스터마이징하기', '# 매직 메서드 완전 정복

매직 메서드는 `__`(더블 언더스코어)로 시작하고 끝나는 특수 메서드로, 파이썬 객체의 내부 동작을 커스터마이징할 수 있게 합니다.

## 학습 목표

- [ ] 매직 메서드(특수 메서드)의 개념을 이해한다
- [ ] 객체 생성과 초기화 관련 메서드를 구현할 수 있다
- [ ] 문자열 표현 메서드를 구현할 수 있다
- [ ] 컨테이너 프로토콜을 구현할 수 있다
- [ ] 컨텍스트 매니저를 구현할 수 있다

## 매직 메서드란?

매직 메서드(Magic Methods)는 파이썬에서 객체의 특별한 동작을 정의하는 메서드입니다. 이들은 ''던더(dunder, double underscore)'' 메서드라고도 불리며, `len()`, `str()` 같은 내장 함수나 연산자를 사용할 때 자동으로 호출됩니다.

매직 메서드를 구현하면 사용자 정의 클래스가 파이썬의 내장 타입처럼 동작하도록 만들 수 있습니다. 예를 들어, `+` 연산자를 지원하거나, `with` 문과 함께 사용할 수 있게 됩니다.

## 기본 사용법

```python
class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __str__(self):
        return f"Point({self.x}, {self.y})"

p = Point(3, 4)
print(p)  # Point(3, 4)
```

`__init__`은 객체 초기화에, `__str__`은 문자열 변환에 사용됩니다.

## 주요 예제

### 예제 1: 객체 생성과 초기화

```python
class Resource:
    def __new__(cls, name):
        print(f"__new__ 호출: {name}")
        instance = super().__new__(cls)
        return instance

    def __init__(self, name):
        print(f"__init__ 호출: {name}")
        self.name = name

r = Resource("file.txt")
# __new__ 호출: file.txt
# __init__ 호출: file.txt
```

`__new__`는 인스턴스 생성, `__init__`은 초기화를 담당합니다.

### 예제 2: 문자열 표현

```python
class Book:
    def __init__(self, title, price):
        self.title = title
        self.price = price

    def __str__(self):
        return self.title

    def __repr__(self):
        return f"Book(''{self.title}'', {self.price})"

b = Book("Python", 30000)
print(str(b))   # Python
print(repr(b))  # Book(''Python'', 30000)
```

`__str__`은 사용자 친화적 출력, `__repr__`은 개발자를 위한 상세 정보를 제공합니다.

### 예제 3: 컨테이너 프로토콜

```python
class Playlist:
    def __init__(self):
        self.songs = []

    def __len__(self):
        return len(self.songs)

    def __getitem__(self, index):
        return self.songs[index]

    def __setitem__(self, index, value):
        self.songs[index] = value

pl = Playlist()
pl.songs = ["Song1", "Song2", "Song3"]
print(len(pl))    # 3
print(pl[0])      # Song1
pl[0] = "NewSong"
print(pl[0])      # NewSong
```

`__len__`, `__getitem__`, `__setitem__`으로 리스트처럼 동작하는 객체를 만들 수 있습니다.

### 예제 4: 컨텍스트 매니저

```python
class FileManager:
    def __init__(self, filename):
        self.filename = filename

    def __enter__(self):
        print("파일 열기")
        self.file = open(self.filename, ''w'')
        return self.file

    def __exit__(self, exc_type, exc_val, exc_tb):
        print("파일 닫기")
        self.file.close()

with FileManager("test.txt") as f:
    f.write("Hello")
# 파일 열기
# 파일 닫기
```

`__enter__`와 `__exit__`으로 `with` 문을 지원하는 컨텍스트 매니저를 구현합니다.

### 예제 5: 연산자 오버로딩

```python
class Vector:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __add__(self, other):
        return Vector(self.x + other.x,
                     self.y + other.y)

    def __repr__(self):
        return f"Vector({self.x}, {self.y})"

v1 = Vector(1, 2)
v2 = Vector(3, 4)
v3 = v1 + v2
print(v3)  # Vector(4, 6)
```

`__add__`로 `+` 연산자를 사용자 정의할 수 있습니다.

## 주의사항

- ⚠️ `__new__`는 클래스 메서드로, `cls`를 받고 인스턴스를 반환해야 합니다
- ⚠️ `__repr__`은 가능하면 `eval(repr(obj)) == obj`가 되도록 구현하세요
- ⚠️ `__exit__`에서 `True`를 반환하면 예외를 억제합니다
- 💡 모든 매직 메서드를 구현할 필요는 없고, 필요한 것만 선택적으로 구현하세요
- 💡 `__str__`이 없으면 `__repr__`이 대신 사용됩니다

## 정리

매직 메서드는 파이썬 객체의 동작을 커스터마이징하는 강력한 도구입니다. 객체 생성, 문자열 표현, 컨테이너 동작, 컨텍스트 관리 등 다양한 프로토콜을 구현하여 자연스러운 파이썬 코드를 작성할 수 있습니다.

### 배운 내용
- ✅ 매직 메서드로 객체의 특별한 동작을 정의할 수 있습니다
- ✅ `__init__`, `__str__`, `__repr__` 등으로 기본 동작을 커스터마이징합니다
- ✅ 컨테이너 프로토콜로 시퀀스처럼 동작하는 객체를 만들 수 있습니다
- ✅ `__enter__`와 `__exit__`으로 컨텍스트 매니저를 구현합니다
- ✅ 연산자 오버로딩으로 직관적인 API를 제공할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('연산자 오버로딩', '매직 메서드를 활용한 사용자 정의 클래스의 연산자 구현', '# 연산자 오버로딩

연산자 오버로딩은 클래스에 특수 메서드(매직 메서드)를 정의하여 기본 연산자의 동작을 사용자 정의 객체에 맞게 구현하는 기법입니다.

## 학습 목표

- [ ] 연산자 오버로딩의 개념을 이해한다
- [ ] 산술 연산자를 오버로딩할 수 있다
- [ ] 비교 연산자를 오버로딩할 수 있다
- [ ] 단항 연산자를 오버로딩할 수 있다
- [ ] 복합 할당 연산자를 오버로딩할 수 있다

## 연산자 오버로딩이란?

연산자 오버로딩은 `+`, `-`, `*`, `==` 등의 연산자를 사용자 정의 클래스에서 자연스럽게 사용할 수 있도록 하는 Python의 강력한 기능입니다. 예를 들어 `Vector` 클래스를 만들 때 `v1 + v2`처럼 직관적으로 벡터 덧셈을 수행할 수 있습니다.

Python은 이를 위해 `__add__`, `__eq__` 같은 특수 메서드(던더 메서드)를 제공합니다. 이 메서드들을 클래스에 정의하면 해당 연산자가 객체에 사용될 때 자동으로 호출됩니다.

## 산술 연산자 오버로딩

```python
class Vector:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __add__(self, other):
        return Vector(self.x + other.x, self.y + other.y)

    def __repr__(self):
        return f"Vector({self.x}, {self.y})"

v1 = Vector(1, 2)
v2 = Vector(3, 4)
print(v1 + v2)  # Vector(4, 6)
```

## 주요 예제

### 예제 1: 분수 클래스 구현
```python
class Fraction:
    def __init__(self, numerator, denominator):
        self.num = numerator
        self.den = denominator

    def __add__(self, other):
        num = self.num * other.den + other.num * self.den
        den = self.den * other.den
        return Fraction(num, den)

    def __mul__(self, other):
        return Fraction(self.num * other.num, self.den * other.den)

    def __repr__(self):
        return f"{self.num}/{self.den}"

f1 = Fraction(1, 2)
f2 = Fraction(1, 3)
print(f1 + f2)  # 5/6
print(f1 * f2)  # 1/6
```

### 예제 2: 비교 연산자
```python
class Money:
    def __init__(self, amount):
        self.amount = amount

    def __eq__(self, other):
        return self.amount == other.amount

    def __lt__(self, other):
        return self.amount < other.amount

    def __le__(self, other):
        return self.amount <= other.amount

m1 = Money(100)
m2 = Money(200)
print(m1 == Money(100))  # True
print(m1 < m2)  # True
print(m1 <= Money(100))  # True
```

### 예제 3: 단항 연산자
```python
class Temperature:
    def __init__(self, celsius):
        self.celsius = celsius

    def __neg__(self):
        return Temperature(-self.celsius)

    def __abs__(self):
        return Temperature(abs(self.celsius))

    def __repr__(self):
        return f"{self.celsius}°C"

t = Temperature(-15)
print(-t)  # 15°C
print(abs(t))  # 15°C
```

### 예제 4: 복합 할당 연산자
```python
class Counter:
    def __init__(self, value=0):
        self.value = value

    def __iadd__(self, other):
        self.value += other
        return self

    def __isub__(self, other):
        self.value -= other
        return self

c = Counter(10)
c += 5
print(c.value)  # 15
c -= 3
print(c.value)  # 12
```

### 예제 5: 특수 메서드
```python
class Playlist:
    def __init__(self, songs):
        self.songs = songs

    def __len__(self):
        return len(self.songs)

    def __contains__(self, song):
        return song in self.songs

    def __bool__(self):
        return len(self.songs) > 0

pl = Playlist([''Song A'', ''Song B''])
print(len(pl))  # 2
print(''Song A'' in pl)  # True
print(bool(pl))  # True
```

## 주의사항

- ⚠️ `__eq__`만 정의하면 `!=`는 자동으로 반대 동작을 합니다
- ⚠️ 복합 할당 연산자(`__iadd__`)는 반드시 `self`를 반환해야 합니다
- ⚠️ `__lt__`, `__le__`, `__gt__`, `__ge__`를 모두 구현하려면 `@functools.total_ordering` 데코레이터를 활용하세요
- 💡 `__repr__`을 함께 구현하면 디버깅이 훨씬 편리합니다
- 💡 연산자 오버로딩은 코드를 직관적으로 만들지만 남용하면 가독성이 떨어질 수 있습니다

## 정리

연산자 오버로딩은 사용자 정의 클래스를 내장 타입처럼 자연스럽게 사용할 수 있게 해줍니다. 적절히 활용하면 코드의 가독성과 표현력을 크게 향상시킬 수 있습니다.

### 배운 내용
- ✅ 산술 연산자(`__add__`, `__mul__` 등)로 수학적 계산 구현
- ✅ 비교 연산자(`__eq__`, `__lt__` 등)로 객체 비교 가능
- ✅ 단항 연산자(`__neg__`, `__abs__`)로 단일 피연산자 연산 처리
- ✅ 복합 할당 연산자(`__iadd__`)로 in-place 연산 최적화
- ✅ 특수 메서드(`__len__`, `__contains__`)로 Python 내장 기능 활용
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('프로퍼티와 디스크립터', '속성 접근을 제어하는 고급 OOP 기법 학습', '# 프로퍼티와 디스크립터

속성 접근을 제어하고 검증 로직을 추가하는 파이썬의 강력한 메커니즘을 학습합니다. 프로퍼티와 디스크립터는 객체지향 프로그래밍에서 캡슐화와 데이터 무결성을 보장하는 핵심 도구입니다.

## 학습 목표

- [ ] @property 데코레이터를 활용할 수 있다
- [ ] getter, setter, deleter를 정의할 수 있다
- [ ] 디스크립터 프로토콜을 이해한다
- [ ] __get__, __set__, __delete__를 구현할 수 있다
- [ ] 프로퍼티와 디스크립터의 활용 사례를 이해한다

## @property 데코레이터란?

@property는 메서드를 속성처럼 접근할 수 있게 해주는 데코레이터입니다. 직접 속성에 접근하는 것처럼 보이지만, 실제로는 메서드가 호출되어 값을 검증하거나 계산할 수 있습니다.

프로퍼티를 사용하면 클래스의 내부 구조를 변경해도 외부 인터페이스는 그대로 유지할 수 있습니다. 이는 코드의 유지보수성을 크게 향상시킵니다.

## getter, setter, deleter 정의

```python
class Temperature:
    def __init__(self, celsius):
        self._celsius = celsius

    @property
    def celsius(self):
        """Getter: 섭씨 온도 반환"""
        return self._celsius

    @celsius.setter
    def celsius(self, value):
        """Setter: 온도 검증 후 설정"""
        if value < -273.15:
            raise ValueError("절대영도 이하 불가")
        self._celsius = value

    @celsius.deleter
    def celsius(self):
        """Deleter: 온도 데이터 삭제"""
        del self._celsius

# 사용 예시
temp = Temperature(25)
print(temp.celsius)  # 25 (getter 호출)
temp.celsius = 30    # setter 호출
```

## 주요 예제

### 예제 1: 읽기 전용 속성
```python
class Circle:
    def __init__(self, radius):
        self._radius = radius

    @property
    def area(self):
        """계산된 속성 (읽기 전용)"""
        return 3.14159 * self._radius ** 2

circle = Circle(5)
print(circle.area)  # 78.53975
# circle.area = 100  # AttributeError 발생
```

### 예제 2: property() 함수 사용
```python
class User:
    def __init__(self, name):
        self._name = name

    def get_name(self):
        return self._name

    def set_name(self, value):
        if not value:
            raise ValueError("이름은 필수입니다")
        self._name = value

    # 함수 방식으로 프로퍼티 정의
    name = property(get_name, set_name)

user = User("Alice")
print(user.name)  # Alice
```

### 예제 3: 디스크립터 프로토콜 구현
```python
class ValidString:
    """문자열 검증 디스크립터"""
    def __init__(self, min_length=0):
        self.min_length = min_length

    def __set_name__(self, owner, name):
        self.name = ''_'' + name

    def __get__(self, obj, type=None):
        if obj is None:
            return self
        return getattr(obj, self.name, '''')

    def __set__(self, obj, value):
        if len(value) < self.min_length:
            raise ValueError(f"최소 {self.min_length}자")
        setattr(obj, self.name, value)

class Account:
    username = ValidString(min_length=3)

    def __init__(self, username):
        self.username = username

acc = Account("John")
print(acc.username)  # John
# acc.username = "Al"  # ValueError 발생
```

### 예제 4: 데이터 디스크립터 vs 비데이터 디스크립터
```python
class DataDescriptor:
    """__get__과 __set__ 모두 구현 (우선순위 높음)"""
    def __get__(self, obj, type=None):
        return "데이터 디스크립터"

    def __set__(self, obj, value):
        pass

class NonDataDescriptor:
    """__get__만 구현 (우선순위 낮음)"""
    def __get__(self, obj, type=None):
        return "비데이터 디스크립터"

class Example:
    data = DataDescriptor()
    non_data = NonDataDescriptor()

ex = Example()
print(ex.data)      # 데이터 디스크립터
print(ex.non_data)  # 비데이터 디스크립터

# 인스턴스 속성으로 덮어쓰기 시도
ex.non_data = "덮어쓰기"
print(ex.non_data)  # 덮어쓰기 (비데이터는 가능)
```

### 예제 5: 실전 활용 - 타입 검증
```python
class TypedProperty:
    """타입 검증 디스크립터"""
    def __init__(self, expected_type):
        self.expected_type = expected_type

    def __set_name__(self, owner, name):
        self.name = ''_'' + name

    def __get__(self, obj, type=None):
        if obj is None:
            return self
        return getattr(obj, self.name)

    def __set__(self, obj, value):
        if not isinstance(value, self.expected_type):
            raise TypeError(f"{self.expected_type} 타입 필요")
        setattr(obj, self.name, value)

class Person:
    name = TypedProperty(str)
    age = TypedProperty(int)

    def __init__(self, name, age):
        self.name = name
        self.age = age

person = Person("Alice", 30)
print(person.name, person.age)  # Alice 30
```

## 주의사항

- 프로퍼티는 클래스 레벨에서 정의되며, 인스턴스 속성보다 우선순위가 높습니다
- 디스크립터에서 무한 재귀를 피하기 위해 내부 저장소는 다른 이름(_name)을 사용하세요
- __set_name__은 Python 3.6+에서 사용 가능하며, 디스크립터 이름을 자동으로 받습니다
- 데이터 디스크립터(__set__ 구현)는 인스턴스 __dict__보다 우선순위가 높습니다
- 성능이 중요한 경우, 단순 속성 접근보다 프로퍼티가 느릴 수 있습니다

## 정리

프로퍼티와 디스크립터는 파이썬에서 속성 접근을 제어하는 강력한 도구입니다. @property는 간단한 경우에, 디스크립터는 재사용 가능한 검증 로직이 필요할 때 사용합니다.

### 배운 내용
- @property 데코레이터로 메서드를 속성처럼 사용
- getter, setter, deleter로 속성 접근 제어
- 디스크립터 프로토콜(__get__, __set__, __delete__)로 재사용 가능한 속성 로직 구현
- 데이터 디스크립터가 비데이터 디스크립터보다 높은 우선순위
- 타입 검증, 값 검증 등 실전 활용 사례
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('데코레이터 기초', '함수를 꾸미는 데코레이터의 개념과 사용법을 학습합니다', '# 데코레이터 기초

데코레이터는 함수를 수정하지 않고 기능을 추가하는 강력한 도구입니다. 로깅, 권한 검사, 실행 시간 측정 등 반복적인 작업을 간결하게 처리할 수 있습니다.

## 학습 목표

- [ ] 데코레이터의 개념과 필요성을 설명할 수 있다
- [ ] 함수를 인자로 받고 반환하는 고차 함수를 작성할 수 있다
- [ ] @ 문법을 사용하여 데코레이터를 적용할 수 있다
- [ ] 간단한 데코레이터를 직접 구현할 수 있다
- [ ] functools.wraps를 사용하여 메타데이터를 보존할 수 있다

## 데코레이터란?

데코레이터는 다른 함수를 인자로 받아 기능을 추가한 새로운 함수를 반환하는 함수입니다. Python에서는 함수가 일급 객체(first-class object)이기 때문에 함수를 변수에 할당하고, 인자로 전달하고, 반환값으로 사용할 수 있습니다.

데코레이터는 원본 함수의 코드를 수정하지 않고 기능을 확장할 수 있어, 코드 재사용성과 가독성을 높입니다. 웹 프레임워크, 테스트 도구, 성능 모니터링 등 다양한 분야에서 활용됩니다.

## 일급 객체로서의 함수

```python
# 함수를 변수에 할당
def greet(name):
    return f"안녕하세요, {name}님!"

hello = greet
print(hello("철수"))  # 안녕하세요, 철수님!

# 함수를 인자로 전달
def execute(func, value):
    return func(value)

print(execute(greet, "영희"))  # 안녕하세요, 영희님!
```

## 기본 데코레이터 작성

```python
# 기본 데코레이터
def my_decorator(func):
    def wrapper():
        print("함수 실행 전")
        func()
        print("함수 실행 후")
    return wrapper

def say_hello():
    print("Hello!")

# 데코레이터 적용 (방법 1)
decorated = my_decorator(say_hello)
decorated()
# 출력:
# 함수 실행 전
# Hello!
# 함수 실행 후
```

## @ 문법 사용하기

```python
# @ 문법으로 데코레이터 적용
def my_decorator(func):
    def wrapper():
        print("=== 시작 ===")
        func()
        print("=== 종료 ===")
    return wrapper

@my_decorator
def greet():
    print("안녕하세요!")

greet()
# 출력:
# === 시작 ===
# 안녕하세요!
# === 종료 ===
```

## 주요 예제

### 예제 1: 실행 시간 측정 데코레이터
```python
import time

def timer(func):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = func(*args, **kwargs)
        end = time.time()
        print(f"{func.__name__} 실행 시간: {end-start:.4f}초")
        return result
    return wrapper

@timer
def slow_function(n):
    total = sum(range(n))
    return total

result = slow_function(1000000)
# 출력: slow_function 실행 시간: 0.0234초
```

### 예제 2: 로깅 데코레이터
```python
def logger(func):
    def wrapper(*args, **kwargs):
        print(f"[LOG] 함수 호출: {func.__name__}")
        print(f"[LOG] 인자: args={args}, kwargs={kwargs}")
        result = func(*args, **kwargs)
        print(f"[LOG] 반환값: {result}")
        return result
    return wrapper

@logger
def add(a, b):
    return a + b

add(3, 5)
# 출력:
# [LOG] 함수 호출: add
# [LOG] 인자: args=(3, 5), kwargs={}
# [LOG] 반환값: 8
```

### 예제 3: functools.wraps 사용
```python
from functools import wraps

def my_decorator(func):
    @wraps(func)  # 메타데이터 보존
    def wrapper(*args, **kwargs):
        print("데코레이터 실행")
        return func(*args, **kwargs)
    return wrapper

@my_decorator
def calculate(x, y):
    """두 수를 더합니다."""
    return x + y

print(calculate.__name__)  # calculate (wraps 없으면 wrapper)
print(calculate.__doc__)   # 두 수를 더합니다.
```

### 예제 4: 입력값 검증 데코레이터
```python
def validate_positive(func):
    def wrapper(x):
        if x <= 0:
            raise ValueError("양수만 입력 가능합니다")
        return func(x)
    return wrapper

@validate_positive
def square_root(x):
    return x ** 0.5

print(square_root(16))  # 4.0
# square_root(-5)  # ValueError 발생
```

### 예제 5: 함수 호출 횟수 카운팅
```python
def counter(func):
    def wrapper(*args, **kwargs):
        wrapper.count += 1
        print(f"호출 횟수: {wrapper.count}")
        return func(*args, **kwargs)
    wrapper.count = 0
    return wrapper

@counter
def process_data(data):
    return data.upper()

process_data("hello")  # 호출 횟수: 1
process_data("world")  # 호출 횟수: 2
process_data("python") # 호출 횟수: 3
```

## 주의사항

- ⚠️ **메타데이터 보존**: functools.wraps를 사용하지 않으면 원본 함수의 이름과 docstring이 손실됩니다
- ⚠️ **인자 처리**: *args, **kwargs를 사용하여 모든 인자를 처리할 수 있도록 합니다
- ⚠️ **반환값 유지**: wrapper 함수에서 원본 함수의 반환값을 그대로 반환해야 합니다
- 💡 **디버깅**: 데코레이터가 많아지면 디버깅이 어려워질 수 있으니 적절히 사용합니다
- 💡 **순서**: 여러 데코레이터를 사용할 때는 위에서 아래로 적용됩니다 (아래부터 실행)

## 정리

데코레이터는 함수의 기능을 확장하는 우아한 방법입니다. @ 문법을 사용하면 코드가 간결해지고, functools.wraps로 원본 함수의 정보를 보존할 수 있습니다.

### 배운 내용
- ✅ 데코레이터는 함수를 받아 기능을 추가한 함수를 반환합니다
- ✅ @ 문법으로 간결하게 데코레이터를 적용할 수 있습니다
- ✅ *args, **kwargs로 모든 종류의 인자를 처리할 수 있습니다
- ✅ functools.wraps로 함수의 메타데이터를 보존합니다
- ✅ 로깅, 타이밍, 검증 등 다양한 용도로 활용 가능합니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('데코레이터 심화', '인자를 받는 데코레이터, 클래스 기반 데코레이터, 실전 활용법', '# 데코레이터 심화

데코레이터는 함수를 수정하지 않고 기능을 추가하는 강력한 도구입니다. 이 강의에서는 인자를 받는 데코레이터, 클래스 기반 데코레이터, 그리고 실전 활용 사례를 학습합니다.

## 학습 목표

- [ ] 인자를 받는 데코레이터를 3단 중첩 구조로 작성할 수 있다
- [ ] 클래스 기반 데코레이터를 __call__ 메서드로 구현할 수 있다
- [ ] 여러 데코레이터를 체이닝하여 조합할 수 있다
- [ ] 로깅, 타이밍, 캐싱 등 실전 데코레이터를 작성할 수 있다

## 인자를 받는 데코레이터란?

일반 데코레이터는 함수만 인자로 받지만, 때로는 데코레이터에 설정값을 전달해야 할 때가 있습니다. 예를 들어 반복 횟수, 로그 레벨, 캐시 시간 등을 지정하고 싶을 수 있습니다.

인자를 받는 데코레이터는 3단 중첩 구조를 사용합니다. 가장 바깥 함수가 인자를 받고, 중간 함수가 실제 데코레이터 역할을 하며, 가장 안쪽 함수가 원본 함수를 감쌉니다.

```python
def repeat(times):
    def decorator(func):
        def wrapper(*args, **kwargs):
            for _ in range(times):
                result = func(*args, **kwargs)
            return result
        return wrapper
    return decorator

@repeat(3)
def greet(name):
    print(f"안녕하세요, {name}님!")

greet("철수")
# 안녕하세요, 철수님!
# 안녕하세요, 철수님!
# 안녕하세요, 철수님!
```

## 클래스 기반 데코레이터

클래스를 사용해 데코레이터를 만들면 상태를 유지하거나 복잡한 로직을 더 깔끔하게 관리할 수 있습니다. `__call__` 메서드를 구현하면 인스턴스를 함수처럼 호출할 수 있습니다.

```python
class CountCalls:
    def __init__(self, func):
        self.func = func
        self.count = 0

    def __call__(self, *args, **kwargs):
        self.count += 1
        print(f"호출 횟수: {self.count}")
        return self.func(*args, **kwargs)

@CountCalls
def say_hello():
    print("Hello!")

say_hello()  # 호출 횟수: 1, Hello!
say_hello()  # 호출 횟수: 2, Hello!
```

## 주요 예제

### 예제 1: 실행 시간 측정 데코레이터
```python
import time

def timer(func):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = func(*args, **kwargs)
        end = time.time()
        print(f"{func.__name__} 실행 시간: {end - start:.4f}초")
        return result
    return wrapper

@timer
def slow_function():
    time.sleep(1)
    return "완료"

slow_function()  # slow_function 실행 시간: 1.0001초
```

### 예제 2: 로깅 데코레이터 (인자 있음)
```python
def log(level="INFO"):
    def decorator(func):
        def wrapper(*args, **kwargs):
            print(f"[{level}] {func.__name__} 호출")
            return func(*args, **kwargs)
        return wrapper
    return decorator

@log("DEBUG")
def process_data(data):
    return data * 2

process_data(5)  # [DEBUG] process_data 호출
```

### 예제 3: 간단한 캐싱 데코레이터
```python
def cache(func):
    cached_results = {}

    def wrapper(*args):
        if args in cached_results:
            print("캐시에서 반환")
            return cached_results[args]
        result = func(*args)
        cached_results[args] = result
        return result
    return wrapper

@cache
def fibonacci(n):
    if n < 2:
        return n
    return fibonacci(n-1) + fibonacci(n-2)

print(fibonacci(5))  # 5
print(fibonacci(5))  # 캐시에서 반환, 5
```

### 예제 4: 데코레이터 체이닝
```python
@timer
@log("INFO")
def important_task():
    time.sleep(0.5)
    return "작업 완료"

# [INFO] important_task 호출
# important_task 실행 시간: 0.5001초
important_task()
```

### 예제 5: 권한 검사 데코레이터
```python
def require_auth(role):
    def decorator(func):
        def wrapper(user, *args, **kwargs):
            if user.get("role") != role:
                return "권한이 없습니다"
            return func(user, *args, **kwargs)
        return wrapper
    return decorator

@require_auth("admin")
def delete_user(user, user_id):
    return f"사용자 {user_id} 삭제"

admin = {"role": "admin"}
guest = {"role": "guest"}

print(delete_user(admin, 123))  # 사용자 123 삭제
print(delete_user(guest, 123))  # 권한이 없습니다
```

## 내장 데코레이터 복습

Python에는 자주 사용하는 내장 데코레이터가 있습니다:

```python
class User:
    def __init__(self, name, age):
        self._name = name
        self._age = age

    @property
    def name(self):
        """getter 역할"""
        return self._name

    @classmethod
    def from_birth_year(cls, name, birth_year):
        """클래스 메서드: 다른 생성 방식 제공"""
        age = 2025 - birth_year
        return cls(name, age)

    @staticmethod
    def is_adult(age):
        """정적 메서드: 인스턴스/클래스 접근 불필요"""
        return age >= 18

user = User("철수", 25)
print(user.name)  # 철수 (속성처럼 사용)

user2 = User.from_birth_year("영희", 2000)
print(User.is_adult(20))  # True
```

## 주의사항

- ⚠️ **functools.wraps 사용**: 원본 함수의 메타데이터를 보존하려면 `@functools.wraps(func)` 사용
- ⚠️ **3단 중첩 구조**: 인자 받는 데코레이터는 `인자 → 데코레이터 → 래퍼` 순서 기억
- 💡 **클래스 vs 함수**: 상태 유지가 필요하면 클래스, 단순 로직은 함수 사용
- 💡 **체이닝 순서**: 데코레이터는 아래에서 위로 적용됨 (`@a @b`는 `a(b(func))`)

## 정리

데코레이터 심화 기법을 통해 코드 재사용성과 가독성을 크게 향상시킬 수 있습니다. 인자를 받는 3단 중첩 구조, 클래스 기반 구현, 체이닝 기법을 실전에서 활용하세요.

### 배운 내용
- ✅ 인자를 받는 데코레이터는 3단 중첩 함수로 구현
- ✅ 클래스 기반 데코레이터는 `__call__` 메서드 사용
- ✅ 데코레이터 체이닝으로 여러 기능 조합 가능
- ✅ 로깅, 타이밍, 캐싱, 권한 검사 등 실전 활용 가능
- ✅ @property, @classmethod, @staticmethod는 자주 사용하는 내장 데코레이터
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('제너레이터 기초', 'yield를 사용한 메모리 효율적인 제너레이터 작성법', '# 제너레이터 기초

제너레이터는 이터레이터를 생성하는 간편한 방법으로, 대용량 데이터를 처리할 때 메모리를 효율적으로 사용할 수 있게 해줍니다.

## 학습 목표

- [ ] 제너레이터의 개념과 필요성을 설명할 수 있다
- [ ] yield 키워드를 사용하여 제너레이터 함수를 작성할 수 있다
- [ ] 제너레이터와 일반 함수의 실행 차이를 구분할 수 있다
- [ ] next() 함수로 제너레이터를 제어할 수 있다
- [ ] 제너레이터의 메모리 효율성을 이해하고 활용할 수 있다

## 제너레이터란?

제너레이터는 값을 한 번에 하나씩 생성(yield)하는 특별한 함수입니다. 일반 함수는 return으로 값을 반환하고 종료되지만, 제너레이터는 yield로 값을 반환한 후 실행을 일시 중지했다가 다음 호출 시 이어서 실행됩니다.

이 특성 덕분에 모든 데이터를 메모리에 올리지 않고도 순차적으로 데이터를 처리할 수 있어, 대용량 파일 처리나 무한 시퀀스 생성에 매우 유용합니다.

제너레이터는 이터레이터 프로토콜을 자동으로 구현하므로, for 루프나 next() 함수로 간편하게 사용할 수 있습니다.

## 기본 사용법

```python
# 일반 함수 vs 제너레이터 함수
def normal_function():
    return [1, 2, 3]

def generator_function():
    yield 1
    yield 2
    yield 3

# 사용 비교
print(normal_function())  # [1, 2, 3]
gen = generator_function()
print(gen)  # <generator object generator_function at 0x...>
```

제너레이터 함수는 호출 시 즉시 실행되지 않고 제너레이터 객체를 반환합니다.

## 주요 예제

### 예제 1: 기본 제너레이터

```python
def count_up_to(n):
    count = 1
    while count <= n:
        yield count
        count += 1

# for 루프로 사용
for num in count_up_to(5):
    print(num, end='' '')
# 출력: 1 2 3 4 5
```

### 예제 2: next() 함수로 제어

```python
def simple_gen():
    print("첫 번째 yield 전")
    yield 1
    print("두 번째 yield 전")
    yield 2
    print("세 번째 yield 전")
    yield 3

gen = simple_gen()
print(next(gen))  # 첫 번째 yield 전 / 1
print(next(gen))  # 두 번째 yield 전 / 2
print(next(gen))  # 세 번째 yield 전 / 3
# print(next(gen))  # StopIteration 예외 발생
```

### 예제 3: 메모리 효율성 비교

```python
import sys

# 리스트로 생성 (모든 값을 메모리에 저장)
def list_squares(n):
    return [i**2 for i in range(n)]

# 제너레이터로 생성 (필요할 때만 계산)
def gen_squares(n):
    for i in range(n):
        yield i**2

n = 1000000
list_result = list_squares(n)
gen_result = gen_squares(n)

print(f"리스트 크기: {sys.getsizeof(list_result):,} bytes")
print(f"제너레이터 크기: {sys.getsizeof(gen_result)} bytes")
# 리스트 크기: 8,000,000+ bytes
# 제너레이터 크기: 200 bytes 미만
```

### 예제 4: 무한 시퀀스 생성

```python
def infinite_sequence():
    num = 0
    while True:
        yield num
        num += 1

# 무한 제너레이터도 메모리를 적게 사용
gen = infinite_sequence()
for _ in range(5):
    print(next(gen), end='' '')
# 출력: 0 1 2 3 4
```

### 예제 5: 파일 처리 예제

```python
def read_large_file(file_path):
    """대용량 파일을 한 줄씩 읽기"""
    with open(file_path, ''r'', encoding=''utf-8'') as file:
        for line in file:
            yield line.strip()

# 사용 예시 (실제 파일이 있다면)
# for line in read_large_file(''large_file.txt''):
#     process(line)  # 한 번에 한 줄씩만 메모리에 로드
```

## 주의사항

- **한 번만 순회 가능**: 제너레이터는 한 번 순회하면 재사용할 수 없습니다. 다시 사용하려면 새로 생성해야 합니다.
- **StopIteration 예외**: next() 사용 시 더 이상 값이 없으면 StopIteration 예외가 발생합니다.
- **인덱싱 불가**: 제너레이터는 리스트처럼 인덱스로 접근할 수 없습니다.
- **len() 사용 불가**: 제너레이터의 길이를 미리 알 수 없습니다.
- **상태 유지**: 제너레이터는 마지막 yield 이후의 상태를 기억합니다.

## 정리

제너레이터는 yield 키워드를 사용하여 값을 하나씩 생성하는 메모리 효율적인 방법입니다. 대용량 데이터 처리나 무한 시퀀스가 필요할 때 특히 유용하며, for 루프나 next() 함수로 간편하게 제어할 수 있습니다.

### 배운 내용

- 제너레이터는 yield로 값을 하나씩 생성하며 실행 상태를 유지합니다
- 일반 함수와 달리 호출 시 제너레이터 객체를 반환합니다
- next() 함수로 제너레이터를 수동으로 제어할 수 있습니다
- 리스트 대비 메모리를 크게 절약할 수 있습니다
- 한 번 순회 후에는 재사용할 수 없으므로 필요 시 재생성해야 합니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('제너레이터 심화와 이터레이터', '제너레이터 표현식, send() 메서드, 이터레이터 프로토콜 활용', '# 제너레이터 심화와 이터레이터

제너레이터는 단순히 값을 생성하는 것을 넘어 양방향 통신, 커스텀 이터레이터 구현 등 강력한 기능을 제공합니다. 이 강의에서는 제너레이터의 고급 활용법과 이터레이터 프로토콜을 깊이 있게 학습합니다.

## 학습 목표

- [ ] 제너레이터 표현식을 작성하고 메모리 효율성을 이해할 수 있다
- [ ] send() 메서드로 제너레이터에 값을 전달할 수 있다
- [ ] 이터레이터 프로토콜(__iter__, __next__)을 구현할 수 있다
- [ ] 이터러블과 이터레이터의 차이를 설명할 수 있다
- [ ] yield from으로 서브 제너레이터를 위임할 수 있다

## 제너레이터 표현식이란?

제너레이터 표현식은 리스트 컴프리헨션과 유사하지만, 즉시 리스트를 생성하지 않고 필요할 때마다 값을 생성하는 제너레이터를 반환합니다. 대용량 데이터를 처리할 때 메모리를 크게 절약할 수 있습니다.

리스트 컴프리헨션은 `[]`를 사용하지만, 제너레이터 표현식은 `()`를 사용합니다. 이 작은 차이가 메모리 사용량에 큰 영향을 미칩니다.

## 기본 사용법

```python
# 리스트 컴프리헨션 (메모리에 전체 리스트 생성)
squares_list = [x**2 for x in range(1000000)]

# 제너레이터 표현식 (필요할 때만 생성)
squares_gen = (x**2 for x in range(1000000))

# 메모리 효율적으로 사용
for square in squares_gen:
    if square > 100:
        break
```

제너레이터 표현식은 한 번에 하나의 값만 메모리에 올리므로 대용량 데이터 처리에 적합합니다.

## 주요 예제

### 예제 1: send() 메서드로 값 전달

```python
def echo_generator():
    value = None
    while True:
        # send()로 받은 값을 반환
        value = yield value
        print(f"받은 값: {value}")

gen = echo_generator()
next(gen)  # 제너레이터 시작
gen.send(10)  # 받은 값: 10
gen.send(20)  # 받은 값: 20
```

### 예제 2: 이터레이터 프로토콜 구현

```python
class Countdown:
    def __init__(self, start):
        self.current = start

    def __iter__(self):
        return self

    def __next__(self):
        if self.current <= 0:
            raise StopIteration
        self.current -= 1
        return self.current + 1

# 사용 예
for num in Countdown(5):
    print(num)  # 5, 4, 3, 2, 1
```

### 예제 3: yield from 활용

```python
def sub_generator():
    yield 1
    yield 2
    yield 3

def main_generator():
    yield ''start''
    yield from sub_generator()  # 서브 제너레이터 위임
    yield ''end''

for value in main_generator():
    print(value)
# 출력: start, 1, 2, 3, end
```

### 예제 4: throw()와 close() 메서드

```python
def robust_generator():
    try:
        while True:
            value = yield
            print(f"처리: {value}")
    except GeneratorExit:
        print("제너레이터 종료")
    except ValueError:
        print("에러 처리됨")

gen = robust_generator()
next(gen)
gen.send(10)  # 처리: 10
gen.throw(ValueError)  # 에러 처리됨
gen.close()  # 제너레이터 종료
```

### 예제 5: 이터러블 vs 이터레이터

```python
# 이터러블: __iter__를 가진 객체
class MyIterable:
    def __init__(self, data):
        self.data = data

    def __iter__(self):
        return iter(self.data)  # 이터레이터 반환

# 이터레이터: __iter__와 __next__를 모두 가진 객체
class MyIterator:
    def __init__(self, data):
        self.data = data
        self.index = 0

    def __iter__(self):
        return self

    def __next__(self):
        if self.index >= len(self.data):
            raise StopIteration
        value = self.data[self.index]
        self.index += 1
        return value
```

## 주요 메서드

### send(value)
- 제너레이터에 값을 전달하고 다음 yield까지 실행
- 처음 호출 전에는 `next()` 또는 `send(None)` 필요

### throw(type, value=None, traceback=None)
- 제너레이터 내부에서 예외를 발생시킴
- 예외 처리 로직을 테스트할 때 유용

### close()
- 제너레이터를 종료시킴
- GeneratorExit 예외를 발생시킴

## 이터레이터 프로토콜

이터레이터 프로토콜은 Python의 반복 메커니즘의 핵심입니다:

1. **이터러블(Iterable)**: `__iter__()` 메서드를 구현한 객체
2. **이터레이터(Iterator)**: `__iter__()`와 `__next__()` 모두 구현한 객체

모든 이터레이터는 이터러블이지만, 모든 이터러블이 이터레이터인 것은 아닙니다.

## 주의사항

- ⚠️ **send() 사용 전 초기화**: send()를 사용하기 전에 반드시 `next()` 또는 `send(None)`으로 제너레이터를 시작해야 합니다
- ⚠️ **이터레이터 소진**: 이터레이터는 한 번 소진되면 재사용할 수 없습니다. 다시 사용하려면 새로 생성해야 합니다
- ⚠️ **StopIteration 필수**: `__next__()` 구현 시 종료 조건에서 반드시 StopIteration을 발생시켜야 합니다
- 💡 **제너레이터 표현식 활용**: 대용량 데이터 처리 시 리스트 컴프리헨션 대신 제너레이터 표현식을 사용하면 메모리를 절약할 수 있습니다
- 💡 **yield from의 장점**: 서브 제너레이터를 위임할 때 단순히 값뿐만 아니라 send(), throw() 등의 메서드 호출도 자동으로 전달됩니다

## 정리

제너레이터와 이터레이터는 Python의 강력한 반복 메커니즘을 제공합니다. 제너레이터 표현식으로 메모리를 효율적으로 사용하고, send()로 양방향 통신을 구현하며, 이터레이터 프로토콜로 커스텀 반복 객체를 만들 수 있습니다.

### 배운 내용

- ✅ 제너레이터 표현식은 `()`를 사용하며 메모리 효율적
- ✅ send()로 제너레이터에 값을 전달하고 양방향 통신 가능
- ✅ 이터레이터는 `__iter__()`와 `__next__()`를 구현
- ✅ 이터러블은 `__iter__()`만 구현하면 됨
- ✅ yield from으로 서브 제너레이터를 간단히 위임
- ✅ throw()와 close()로 제너레이터를 제어
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('컨텍스트 매니저', 'with 문과 컨텍스트 매니저로 리소스를 안전하게 관리하는 방법', '# 컨텍스트 매니저

컨텍스트 매니저는 리소스를 사용할 때 자동으로 설정(setup)과 정리(cleanup)를 수행하는 파이썬의 강력한 기능입니다. 파일, 네트워크 연결, 데이터베이스 커넥션 등을 안전하게 관리할 수 있습니다.

## 학습 목표

- [ ] with 문의 동작 원리를 이해하고 설명할 수 있다
- [ ] __enter__와 __exit__ 메서드를 구현하여 컨텍스트 매니저를 작성할 수 있다
- [ ] contextlib 모듈을 활용하여 간단한 컨텍스트 매니저를 만들 수 있다
- [ ] @contextmanager 데코레이터를 사용하여 함수 기반 컨텍스트 매니저를 작성할 수 있다
- [ ] 컨텍스트 매니저를 통해 예외 처리와 리소스 정리를 수행할 수 있다

## 컨텍스트 매니저란?

컨텍스트 매니저는 `with` 문과 함께 사용되어 리소스의 획득과 해제를 자동화하는 객체입니다. 파일을 열고 닫는 작업, 락(lock) 획득과 해제, 데이터베이스 트랜잭션 관리 등에서 필수적입니다.

`with` 문을 사용하면 예외가 발생하더라도 리소스가 항상 정리되므로, `try-finally` 블록을 대체하는 우아한 방법입니다. 코드의 가독성을 높이고 리소스 누수를 방지합니다.

컨텍스트 매니저는 `__enter__`와 `__exit__` 두 개의 특수 메서드를 구현하거나, `contextlib` 모듈의 데코레이터를 사용하여 만들 수 있습니다.

## with 문의 동작 원리

```python
# with 문 사용
with open(''example.txt'', ''w'') as f:
    f.write(''Hello, World!'')
# 자동으로 파일이 닫힘

# 위 코드는 아래와 동일
f = open(''example.txt'', ''w'')
f.__enter__()
try:
    f.write(''Hello, World!'')
finally:
    f.__exit__(None, None, None)
```

`with` 문이 실행되면 먼저 `__enter__` 메서드가 호출되고, 블록이 끝나면 `__exit__` 메서드가 자동으로 호출됩니다.

## 클래스 기반 컨텍스트 매니저

### 예제 1: 기본 컨텍스트 매니저
```python
class FileManager:
    def __init__(self, filename, mode):
        self.filename = filename
        self.mode = mode
        self.file = None

    def __enter__(self):
        self.file = open(self.filename, self.mode)
        return self.file

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.file:
            self.file.close()
        return False

# 사용
with FileManager(''test.txt'', ''w'') as f:
    f.write(''Context Manager Test'')
```

### 예제 2: 실행 시간 측정
```python
import time

class Timer:
    def __enter__(self):
        self.start = time.time()
        return self

    def __exit__(self, *args):
        self.end = time.time()
        print(f''실행 시간: {self.end - self.start:.2f}초'')
        return False

# 사용
with Timer():
    sum(range(1000000))
# 출력: 실행 시간: 0.03초
```

### 예제 3: 예외 처리가 있는 컨텍스트 매니저
```python
class DatabaseConnection:
    def __enter__(self):
        print(''데이터베이스 연결'')
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        if exc_type is not None:
            print(f''에러 발생: {exc_val}'')
            print(''트랜잭션 롤백'')
        else:
            print(''트랜잭션 커밋'')
        print(''연결 종료'')
        return False  # False: 예외를 다시 발생

# 사용
with DatabaseConnection():
    print(''데이터 작업 수행'')
```

## @contextmanager 데코레이터

### 예제 4: 함수 기반 컨텍스트 매니저
```python
from contextlib import contextmanager

@contextmanager
def file_manager(filename, mode):
    f = open(filename, mode)
    try:
        yield f  # __enter__의 반환값
    finally:
        f.close()  # __exit__의 동작

# 사용
with file_manager(''test.txt'', ''w'') as f:
    f.write(''Simple context manager'')
```

### 예제 5: 디렉토리 변경 관리
```python
import os
from contextlib import contextmanager

@contextmanager
def change_dir(path):
    old_dir = os.getcwd()
    os.chdir(path)
    try:
        yield
    finally:
        os.chdir(old_dir)

# 사용
with change_dir(''/tmp''):
    print(f''현재 디렉토리: {os.getcwd()}'')
print(f''원래 디렉토리: {os.getcwd()}'')
```

## contextlib 유틸리티

```python
from contextlib import suppress, closing

# suppress: 특정 예외 무시
with suppress(FileNotFoundError):
    os.remove(''nonexistent.txt'')
print(''예외 무시됨'')

# closing: close() 메서드 자동 호출
from urllib.request import urlopen
with closing(urlopen(''http://www.python.org'')) as page:
    content = page.read()
```

## 주의사항

- ⚠️ `__exit__`에서 `True`를 반환하면 예외가 억제되므로 신중하게 사용
- ⚠️ `__enter__`에서 발생한 예외는 `__exit__`이 호출되지 않음
- 💡 리소스 정리는 반드시 `__exit__`에서 수행
- 💡 `@contextmanager`를 사용하면 코드가 더 간결해짐
- 💡 여러 컨텍스트 매니저를 중첩하거나 쉼표로 연결 가능

## 정리

컨텍스트 매니저는 리소스를 안전하게 관리하는 파이썬의 핵심 기능입니다. `with` 문을 통해 자동으로 설정과 정리를 수행하며, 예외 발생 시에도 리소스가 올바르게 해제됩니다.

### 배운 내용
- ✅ `with` 문은 `__enter__`와 `__exit__`를 자동으로 호출
- ✅ 클래스에 `__enter__`와 `__exit__`를 구현하여 컨텍스트 매니저 작성
- ✅ `@contextmanager` 데코레이터로 함수 기반 컨텍스트 매니저 작성
- ✅ `contextlib` 모듈의 `suppress`, `closing` 등 유틸리티 활용
- ✅ 예외 처리와 리소스 정리를 컨텍스트 매니저로 자동화
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('threading 모듈 - 멀티스레딩 기초', '스레드 생성과 동기화, GIL의 이해', '# threading 모듈 - 멀티스레딩 기초

여러 작업을 동시에 실행하고 싶을 때 스레드를 사용합니다. Python의 threading 모듈은 멀티스레딩을 쉽게 구현할 수 있게 해줍니다.

## 학습 목표

- [ ] 스레드의 개념과 동작 원리를 설명할 수 있다
- [ ] Thread 클래스로 멀티스레딩을 구현할 수 있다
- [ ] Lock을 활용하여 스레드 동기화를 수행할 수 있다
- [ ] GIL의 특성을 이해하고 적절한 사용 시나리오를 판단할 수 있다
- [ ] 스레드 안전성을 고려한 코드를 작성할 수 있다

## 스레드란?

스레드(Thread)는 프로세스 내에서 실행되는 독립적인 실행 흐름입니다. 하나의 프로그램에서 여러 작업을 동시에 처리할 수 있게 해줍니다.

멀티스레딩은 I/O 작업이 많은 경우에 특히 유용합니다. 파일 읽기, 네트워크 요청 등이 진행되는 동안 다른 작업을 수행할 수 있기 때문입니다.

Python에서는 threading 모듈을 통해 스레드를 생성하고 관리할 수 있습니다. 단, GIL(Global Interpreter Lock)로 인해 CPU 집약적 작업에는 제한이 있습니다.

## 기본 사용법

```python
import threading
import time

def worker(name):
    print(f"{name} 시작")
    time.sleep(2)
    print(f"{name} 완료")

# 스레드 생성 및 실행
t = threading.Thread(target=worker, args=("작업1",))
t.start()  # 스레드 시작
t.join()   # 스레드 종료 대기
```

start() 메서드로 스레드를 시작하고, join() 메서드로 스레드가 완료될 때까지 대기합니다.

## 주요 예제

### 예제 1: 여러 스레드 동시 실행
```python
import threading
import time

def task(n):
    print(f"작업 {n} 시작")
    time.sleep(1)
    print(f"작업 {n} 완료")

threads = []
for i in range(3):
    t = threading.Thread(target=task, args=(i,))
    threads.append(t)
    t.start()

for t in threads:
    t.join()
print("모든 작업 완료")
```

세 개의 스레드가 거의 동시에 실행되어 약 1초 만에 완료됩니다.

### 예제 2: Lock을 사용한 동기화
```python
import threading

counter = 0
lock = threading.Lock()

def increment():
    global counter
    for _ in range(100000):
        with lock:  # 동기화
            counter += 1

t1 = threading.Thread(target=increment)
t2 = threading.Thread(target=increment)
t1.start()
t2.start()
t1.join()
t2.join()
print(f"Counter: {counter}")  # 200000
```

Lock 없이 실행하면 결과가 200000보다 작을 수 있습니다. Lock으로 동기화하여 정확한 결과를 보장합니다.

### 예제 3: 클래스 기반 스레드
```python
import threading
import time

class Worker(threading.Thread):
    def __init__(self, name):
        super().__init__()
        self.name = name

    def run(self):
        print(f"{self.name} 시작")
        time.sleep(1)
        print(f"{self.name} 완료")

w1 = Worker("작업자1")
w2 = Worker("작업자2")
w1.start()
w2.start()
w1.join()
w2.join()
```

Thread 클래스를 상속받아 run() 메서드를 오버라이드하는 방식입니다.

### 예제 4: RLock (재진입 가능 락)
```python
import threading

rlock = threading.RLock()

def recursive_function(n):
    with rlock:
        if n > 0:
            print(f"레벨 {n}")
            recursive_function(n - 1)

t = threading.Thread(target=recursive_function, args=(3,))
t.start()
t.join()
```

RLock은 같은 스레드에서 여러 번 획득 가능합니다. 재귀 함수에서 유용합니다.

### 예제 5: 데몬 스레드
```python
import threading
import time

def daemon_task():
    while True:
        print("백그라운드 작업 중...")
        time.sleep(1)

t = threading.Thread(target=daemon_task)
t.daemon = True  # 데몬 스레드로 설정
t.start()

time.sleep(3)
print("메인 종료")
# 메인이 종료되면 데몬 스레드도 자동 종료
```

데몬 스레드는 메인 프로그램이 종료되면 함께 종료됩니다.

## GIL (Global Interpreter Lock)

Python의 GIL은 한 번에 하나의 스레드만 Python 바이트코드를 실행하도록 제한합니다.

이로 인해:
- CPU 집약적 작업: 멀티스레딩 효과가 제한적
- I/O 집약적 작업: 멀티스레딩이 효과적 (네트워크, 파일 I/O)

CPU 집약적 작업에는 multiprocessing 모듈을 사용하는 것이 좋습니다.

## 주의사항

- ⚠️ 공유 자원 접근 시 반드시 Lock 사용
- ⚠️ 데드락 주의: 여러 Lock을 사용할 때 획득 순서 통일
- ⚠️ GIL로 인해 CPU 작업은 성능 향상이 제한적
- 💡 I/O 작업(네트워크, 파일)에 threading 사용
- 💡 CPU 작업에는 multiprocessing 고려
- 💡 join()으로 스레드 완료를 반드시 대기

## 정리

threading 모듈은 I/O 작업이 많은 프로그램에서 효율성을 높여줍니다. Lock을 사용한 동기화와 GIL의 특성을 이해하는 것이 중요합니다.

### 배운 내용
- ✅ Thread 클래스로 스레드 생성 및 실행
- ✅ start(), join() 메서드로 스레드 제어
- ✅ Lock, RLock을 사용한 동기화
- ✅ GIL의 특성과 적절한 사용 시나리오
- ✅ 데몬 스레드와 스레드 안전성
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('multiprocessing 모듈 - 멀티프로세싱', '프로세스 기반 병렬 처리로 CPU 집약적 작업 최적화하기', '# multiprocessing 모듈 - 멀티프로세싱

CPU 집약적 작업을 병렬로 처리하여 성능을 극대화하는 멀티프로세싱 기법을 학습합니다.

## 학습 목표

- [ ] 프로세스와 스레드의 차이를 이해하고 설명할 수 있다
- [ ] Process 클래스를 사용하여 독립적인 프로세스를 생성할 수 있다
- [ ] Pool을 활용하여 프로세스 풀을 효율적으로 관리할 수 있다
- [ ] Queue와 Pipe를 사용하여 프로세스 간 통신을 구현할 수 있다
- [ ] CPU 집약적 작업에 멀티프로세싱을 적용하여 성능을 향상시킬 수 있다

## 프로세스 vs 스레드

멀티프로세싱은 여러 프로세스를 동시에 실행하여 CPU 집약적 작업을 병렬로 처리합니다. Python의 GIL(Global Interpreter Lock) 제약을 우회하여 진정한 병렬 처리가 가능합니다.

**스레드**: 같은 메모리 공간을 공유하지만 GIL로 인해 하나의 스레드만 Python 코드를 실행합니다. I/O 작업에 적합합니다.

**프로세스**: 독립적인 메모리 공간을 가지며 각 프로세스는 자체 GIL을 가집니다. CPU 집약적 작업에 적합하며, 멀티코어 CPU를 완전히 활용할 수 있습니다.

## 기본 사용법

```python
import multiprocessing
import time

def worker(num):
    print(f"Worker {num} 시작")
    time.sleep(2)
    print(f"Worker {num} 종료")

if __name__ == ''__main__'':
    p = multiprocessing.Process(target=worker, args=(1,))
    p.start()
    p.join()  # 프로세스가 종료될 때까지 대기
```

Process 객체를 생성하고 start()로 실행, join()으로 완료를 대기합니다.

## 주요 예제

### 예제 1: 여러 프로세스 동시 실행
```python
import multiprocessing

def calculate_square(n):
    return n * n

if __name__ == ''__main__'':
    processes = []
    for i in range(5):
        p = multiprocessing.Process(target=calculate_square, args=(i,))
        processes.append(p)
        p.start()

    for p in processes:
        p.join()
```

### 예제 2: Pool을 사용한 병렬 처리
```python
from multiprocessing import Pool

def cube(x):
    return x ** 3

if __name__ == ''__main__'':
    with Pool(processes=4) as pool:
        results = pool.map(cube, [1, 2, 3, 4, 5])
        print(results)  # [1, 8, 27, 64, 125]
```

### 예제 3: Queue를 사용한 프로세스 간 통신
```python
from multiprocessing import Process, Queue

def producer(q):
    for i in range(5):
        q.put(i)
    q.put(None)  # 종료 신호

def consumer(q):
    while True:
        item = q.get()
        if item is None:
            break
        print(f"받은 데이터: {item}")

if __name__ == ''__main__'':
    queue = Queue()
    p1 = Process(target=producer, args=(queue,))
    p2 = Process(target=consumer, args=(queue,))
    p1.start()
    p2.start()
    p1.join()
    p2.join()
```

### 예제 4: Pipe를 사용한 양방향 통신
```python
from multiprocessing import Process, Pipe

def sender(conn):
    conn.send("Hello")
    response = conn.recv()
    print(f"응답: {response}")
    conn.close()

if __name__ == ''__main__'':
    parent_conn, child_conn = Pipe()
    p = Process(target=sender, args=(child_conn,))
    p.start()
    msg = parent_conn.recv()
    print(f"받은 메시지: {msg}")
    parent_conn.send("World")
    p.join()
```

### 예제 5: CPU 집약적 작업 성능 비교
```python
import multiprocessing
import time

def cpu_intensive(n):
    return sum(i*i for i in range(n))

if __name__ == ''__main__'':
    numbers = [10000000] * 4

    # 순차 처리
    start = time.time()
    results = [cpu_intensive(n) for n in numbers]
    print(f"순차: {time.time() - start:.2f}초")

    # 병렬 처리
    start = time.time()
    with Pool(4) as pool:
        results = pool.map(cpu_intensive, numbers)
    print(f"병렬: {time.time() - start:.2f}초")
```

## 주의사항

- ⚠️ **if __name__ == ''__main__'' 필수**: Windows에서 무한 프로세스 생성을 방지하기 위해 반드시 사용해야 합니다
- ⚠️ **메모리 오버헤드**: 각 프로세스는 독립적인 메모리 공간을 사용하므로 메모리 사용량이 증가합니다
- ⚠️ **프로세스 간 데이터 전달**: pickle 가능한 객체만 전달할 수 있습니다 (람다 함수, 로컬 함수 불가)
- 💡 **CPU 코어 수 확인**: `multiprocessing.cpu_count()`로 최적의 프로세스 수를 결정할 수 있습니다
- 💡 **적합한 작업**: 수치 계산, 이미지 처리, 데이터 변환 등 CPU 집약적 작업에 효과적입니다

## 정리

multiprocessing 모듈은 GIL 제약을 우회하여 진정한 병렬 처리를 가능하게 합니다. Process로 독립 프로세스를 생성하고, Pool로 효율적인 작업 분산을 구현하며, Queue와 Pipe로 프로세스 간 통신을 수행할 수 있습니다.

### 배운 내용
- ✅ 프로세스는 독립적인 메모리를 가지며 CPU 집약적 작업에 적합합니다
- ✅ Process 클래스로 프로세스를 생성하고 start()와 join()으로 관리합니다
- ✅ Pool.map()을 사용하면 간단하게 병렬 처리를 구현할 수 있습니다
- ✅ Queue는 FIFO 방식의 프로세스 간 통신을, Pipe는 양방향 통신을 제공합니다
- ✅ CPU 집약적 작업에서 멀티프로세싱은 순차 처리 대비 큰 성능 향상을 제공합니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('queue 모듈 - 스레드 안전 큐', '스레드 안전한 Queue, LifoQueue, PriorityQueue 활용', '# queue 모듈 - 스레드 안전 큐

멀티스레드 환경에서 안전하게 데이터를 주고받을 수 있는 queue 모듈을 학습합니다.

## 학습 목표

- [ ] 스레드 안전한 큐의 필요성을 이해한다
- [ ] Queue, LifoQueue, PriorityQueue를 활용할 수 있다
- [ ] put()과 get()을 사용하여 큐를 조작할 수 있다
- [ ] 생산자-소비자 패턴을 구현할 수 있다

## 스레드 안전 큐란?

여러 스레드가 동시에 데이터를 넣고 꺼낼 때 발생하는 경쟁 상태(race condition)를 방지하는 큐입니다. Python의 queue 모듈은 내부적으로 락(lock)을 사용하여 스레드 안전성을 보장합니다.

일반 리스트를 멀티스레드에서 사용하면 데이터 손실이나 예상치 못한 동작이 발생할 수 있습니다. queue 모듈은 이러한 문제를 해결하고, 생산자-소비자 패턴과 같은 동시성 프로그래밍에 필수적입니다.

## 기본 사용법

```python
import queue

# FIFO 큐 생성 (최대 크기 3)
q = queue.Queue(maxsize=3)

# 데이터 추가
q.put("첫 번째")
q.put("두 번째")

# 데이터 꺼내기 (FIFO)
print(q.get())  # 출력: 첫 번째
print(q.get())  # 출력: 두 번째
```

put()은 큐에 데이터를 추가하고, get()은 큐에서 데이터를 꺼냅니다. maxsize를 설정하면 큐의 최대 크기를 제한할 수 있습니다.

## 주요 예제

### 예제 1: Queue (FIFO 큐)
```python
import queue

q = queue.Queue()
q.put(1)
q.put(2)
q.put(3)

print(q.get())  # 출력: 1
print(q.get())  # 출력: 2
print(q.qsize())  # 출력: 1
```

### 예제 2: LifoQueue (LIFO 스택)
```python
import queue

lifo = queue.LifoQueue()
lifo.put("A")
lifo.put("B")
lifo.put("C")

print(lifo.get())  # 출력: C (마지막 입력)
print(lifo.get())  # 출력: B
```

### 예제 3: PriorityQueue (우선순위 큐)
```python
import queue

pq = queue.PriorityQueue()
pq.put((3, "낮은 우선순위"))
pq.put((1, "높은 우선순위"))
pq.put((2, "중간 우선순위"))

print(pq.get())  # 출력: (1, ''높은 우선순위'')
print(pq.get())  # 출력: (2, ''중간 우선순위'')
```

### 예제 4: 생산자-소비자 패턴
```python
import queue
import threading
import time

q = queue.Queue()

def producer():
    for i in range(5):
        q.put(f"작업-{i}")
        print(f"생산: 작업-{i}")
        time.sleep(0.1)

def consumer():
    while True:
        item = q.get()
        if item is None:
            break
        print(f"소비: {item}")
        q.task_done()

# 스레드 시작
threading.Thread(target=producer).start()
threading.Thread(target=consumer, daemon=True).start()

time.sleep(1)
q.put(None)  # 종료 신호
```

### 예제 5: 블로킹과 타임아웃
```python
import queue

q = queue.Queue(maxsize=2)
q.put("A")
q.put("B")

# 타임아웃 사용 (큐가 가득 찬 경우)
try:
    q.put("C", timeout=1)
except queue.Full:
    print("큐가 가득 참")

# 빈 큐에서 타임아웃
empty_q = queue.Queue()
try:
    empty_q.get(timeout=1)
except queue.Empty:
    print("큐가 비어 있음")
```

## 주의사항

- ⚠️ get()은 큐가 비어있으면 데이터가 들어올 때까지 대기합니다 (블로킹)
- ⚠️ put()은 큐가 가득 차면 공간이 생길 때까지 대기합니다
- 💡 get_nowait()과 put_nowait()을 사용하면 대기 없이 즉시 예외를 발생시킵니다
- 💡 task_done()과 join()을 사용하면 모든 작업 완료를 기다릴 수 있습니다
- 💡 PriorityQueue에서는 숫자가 작을수록 우선순위가 높습니다

## 정리

queue 모듈은 멀티스레드 환경에서 안전하게 데이터를 공유할 수 있는 강력한 도구입니다. Queue(FIFO), LifoQueue(LIFO), PriorityQueue(우선순위)를 상황에 맞게 선택하여 사용하면 효율적인 동시성 프로그래밍이 가능합니다.

### 배운 내용
- ✅ 스레드 안전 큐는 경쟁 상태를 방지하여 안전한 데이터 공유를 제공합니다
- ✅ Queue, LifoQueue, PriorityQueue는 각각 FIFO, LIFO, 우선순위 방식으로 동작합니다
- ✅ put()과 get()으로 데이터를 추가하고 꺼내며, 타임아웃 설정이 가능합니다
- ✅ 생산자-소비자 패턴은 task_done()과 join()을 활용하여 구현할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('pickle 모듈 - 객체 직렬화', 'Python 객체를 파일로 저장하고 복원하는 직렬화 방법', '# pickle 모듈 - 객체 직렬화

Python 객체를 파일로 저장하고 나중에 다시 불러오는 방법을 배웁니다. pickle은 복잡한 데이터 구조를 그대로 보존할 수 있는 강력한 직렬화 도구입니다.

## 학습 목표

- [ ] 직렬화(serialization)의 개념과 필요성을 설명할 수 있다
- [ ] pickle을 사용하여 Python 객체를 직렬화하고 역직렬화할 수 있다
- [ ] dump()와 load()를 사용하여 파일에 객체를 저장하고 로드할 수 있다
- [ ] pickle의 보안 위험성을 이해하고 안전하게 사용할 수 있다
- [ ] pickle과 json의 차이점을 비교하여 적절한 도구를 선택할 수 있다

## 직렬화란?

직렬화(serialization)는 Python 객체를 바이트 스트림으로 변환하는 과정입니다. 이를 통해 메모리에 있는 객체를 파일로 저장하거나 네트워크로 전송할 수 있습니다.

pickle은 Python 전용 직렬화 모듈로, 거의 모든 Python 객체를 직렬화할 수 있습니다. 리스트, 딕셔너리뿐만 아니라 사용자 정의 클래스 인스턴스도 저장할 수 있어 매우 유용합니다.

JSON과 달리 pickle은 Python 객체의 구조와 타입 정보를 완벽하게 보존하므로, 복잡한 데이터를 다룰 때 적합합니다.

## 기본 사용법

```python
import pickle

# 저장할 데이터
data = {''name'': ''홍길동'', ''age'': 30, ''scores'': [90, 85, 95]}

# 파일에 저장
with open(''data.pkl'', ''wb'') as f:
    pickle.dump(data, f)

# 파일에서 불러오기
with open(''data.pkl'', ''rb'') as f:
    loaded_data = pickle.load(f)
    print(loaded_data)  # {''name'': ''홍길동'', ''age'': 30, ''scores'': [90, 85, 95]}
```

파일을 바이너리 모드(''wb'', ''rb'')로 열어야 하며, dump()로 저장하고 load()로 불러옵니다.

## 주요 예제

### 예제 1: 복잡한 객체 저장
```python
import pickle

class Student:
    def __init__(self, name, grade):
        self.name = name
        self.grade = grade

students = [Student(''김철수'', ''A''), Student(''이영희'', ''B'')]

# 객체 리스트 저장
with open(''students.pkl'', ''wb'') as f:
    pickle.dump(students, f)

# 객체 리스트 불러오기
with open(''students.pkl'', ''rb'') as f:
    loaded = pickle.load(f)
    print(loaded[0].name)  # 김철수
```

### 예제 2: dumps()와 loads() 사용
```python
import pickle

# 바이트 문자열로 직렬화
data = {''numbers'': [1, 2, 3], ''text'': ''hello''}
byte_data = pickle.dumps(data)
print(type(byte_data))  # <class ''bytes''>

# 바이트 문자열에서 복원
restored = pickle.loads(byte_data)
print(restored)  # {''numbers'': [1, 2, 3], ''text'': ''hello''}
```

### 예제 3: 여러 객체 저장
```python
import pickle

# 여러 객체를 순차적으로 저장
with open(''multi.pkl'', ''wb'') as f:
    pickle.dump([1, 2, 3], f)
    pickle.dump({''key'': ''value''}, f)
    pickle.dump(''text data'', f)

# 같은 순서로 불러오기
with open(''multi.pkl'', ''rb'') as f:
    list_data = pickle.load(f)
    dict_data = pickle.load(f)
    text_data = pickle.load(f)
    print(list_data, dict_data, text_data)
```

### 예제 4: 프로토콜 버전 지정
```python
import pickle

data = {''version'': ''test''}

# 최신 프로토콜 사용 (빠르고 효율적)
with open(''data.pkl'', ''wb'') as f:
    pickle.dump(data, f, protocol=pickle.HIGHEST_PROTOCOL)

# 프로토콜 버전 확인
print(pickle.DEFAULT_PROTOCOL)  # 4 (Python 3.8+)
```

### 예제 5: pickle vs json
```python
import pickle
import json
from datetime import datetime

# pickle: Python 객체 완벽 보존
data = {''date'': datetime.now(), ''numbers'': {1, 2, 3}}
pickled = pickle.dumps(data)
print(pickle.loads(pickled))  # 원본 그대로 복원

# json: 기본 타입만 지원
try:
    json.dumps(data)  # TypeError 발생
except TypeError as e:
    print(f"JSON 에러: {e}")
```

## 주의사항

- ⚠️ **보안 경고**: pickle.load()는 임의의 코드를 실행할 수 있어 신뢰할 수 없는 데이터에는 절대 사용하지 마세요
- ⚠️ **Python 전용**: pickle 파일은 다른 언어에서 읽을 수 없습니다
- ⚠️ **버전 호환성**: Python 버전이 다르면 pickle 파일이 호환되지 않을 수 있습니다
- 💡 **바이너리 모드**: 파일을 열 때 반드시 ''wb'', ''rb'' 모드를 사용하세요
- 💡 **JSON 우선**: 간단한 데이터는 JSON을 사용하는 것이 안전하고 호환성이 좋습니다
- 💡 **프로토콜**: HIGHEST_PROTOCOL을 사용하면 성능이 향상됩니다

## 정리

pickle 모듈은 Python 객체를 파일로 저장하고 복원할 수 있는 강력한 도구입니다. 복잡한 데이터 구조도 완벽하게 보존할 수 있지만, 보안 위험이 있으므로 신뢰할 수 있는 데이터에만 사용해야 합니다.

### 배운 내용
- ✅ 직렬화는 객체를 바이트 스트림으로 변환하여 저장/전송하는 기술입니다
- ✅ dump()/load()는 파일 입출력에, dumps()/loads()는 바이트 변환에 사용합니다
- ✅ pickle은 Python 전용이며 거의 모든 객체를 직렬화할 수 있습니다
- ✅ 신뢰할 수 없는 pickle 파일은 절대 로드하지 말아야 합니다
- ✅ 간단한 데이터 교환에는 JSON이 더 안전하고 범용적입니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('copy 모듈 - 얕은 복사와 깊은 복사', '얕은 복사와 깊은 복사의 차이를 이해하고 활용하기', '# copy 모듈 - 얕은 복사와 깊은 복사

Python에서 변수 할당은 객체를 복사하지 않고 참조만 생성합니다. 중첩된 객체를 다룰 때 의도치 않은 변경을 방지하려면 올바른 복사 방법을 이해해야 합니다.

## 학습 목표

- [ ] 얕은 복사와 깊은 복사의 차이를 이해한다
- [ ] copy.copy()를 사용할 수 있다
- [ ] copy.deepcopy()를 사용할 수 있다
- [ ] 복사가 필요한 상황을 판단할 수 있다

## 참조와 복사의 차이

Python에서 변수를 할당하면 객체의 참조만 복사됩니다. 원본과 사본이 같은 객체를 가리키므로, 하나를 변경하면 다른 것도 영향을 받습니다.

```python
import copy

# 참조 복사
original = [1, 2, 3]
reference = original
reference.append(4)
print(original)  # [1, 2, 3, 4] - 원본도 변경됨!
```

## 얕은 복사 (Shallow Copy)

얕은 복사는 새로운 객체를 생성하지만, 내부 요소는 참조로 복사됩니다. 1차원 객체에는 안전하지만 중첩 객체는 주의가 필요합니다.

```python
import copy

# 얕은 복사 - copy.copy()
original = [1, 2, [3, 4]]
shallow = copy.copy(original)

shallow[0] = 99
print(original)  # [1, 2, [3, 4]] - 변경 안됨

shallow[2][0] = 99
print(original)  # [1, 2, [99, 4]] - 중첩 리스트는 변경됨!
```

## 깊은 복사 (Deep Copy)

깊은 복사는 객체와 내부의 모든 중첩 객체를 재귀적으로 복사합니다. 완전히 독립적인 복사본을 만들 때 사용합니다.

```python
import copy

# 깊은 복사 - copy.deepcopy()
original = [1, 2, [3, 4]]
deep = copy.deepcopy(original)

deep[2][0] = 99
print(original)  # [1, 2, [3, 4]] - 원본 유지
print(deep)      # [1, 2, [99, 4]] - 사본만 변경
```

## 주요 예제

### 예제 1: 리스트 복사 방법 비교
```python
import copy

original = [[1, 2], [3, 4]]

# 세 가지 복사 방법
ref = original               # 참조
shallow = copy.copy(original)
deep = copy.deepcopy(original)

original[0][0] = 99

print(ref)     # [[99, 2], [3, 4]]
print(shallow) # [[99, 2], [3, 4]]
print(deep)    # [[1, 2], [3, 4]] - 독립적
```

### 예제 2: 딕셔너리 복사
```python
import copy

user = {
    ''name'': ''Alice'',
    ''scores'': [90, 85, 88]
}

# 깊은 복사로 안전하게 복사
backup = copy.deepcopy(user)
user[''scores''].append(95)

print(user[''scores''])   # [90, 85, 88, 95]
print(backup[''scores'']) # [90, 85, 88] - 변경 안됨
```

### 예제 3: 클래스 객체 복사
```python
import copy

class Student:
    def __init__(self, name, grades):
        self.name = name
        self.grades = grades

original = Student(''Bob'', [80, 85])
shallow = copy.copy(original)
deep = copy.deepcopy(original)

original.grades.append(90)

print(shallow.grades)  # [80, 85, 90] - 영향받음
print(deep.grades)     # [80, 85] - 독립적
```

### 예제 4: 복사 필요성 판단
```python
import copy

# 불변 객체는 복사 불필요
numbers = (1, 2, 3)
copied = numbers  # 튜플은 변경 불가

# 가변 중첩 객체는 깊은 복사 필요
config = {
    ''settings'': {''debug'': True},
    ''users'': [''admin'', ''guest'']
}
safe_config = copy.deepcopy(config)
```

## 주의사항

- ⚠️ 얕은 복사는 중첩 객체의 내부 요소를 공유합니다
- ⚠️ 깊은 복사는 순환 참조 객체도 처리하지만 성능 비용이 있습니다
- 💡 단순 리스트는 `list.copy()` 또는 슬라이싱 `[:]`도 사용 가능합니다
- 💡 딕셔너리는 `dict.copy()` 메서드를 제공합니다
- 💡 불변 객체(int, str, tuple)는 복사가 불필요합니다

## 정리

copy 모듈은 객체를 안전하게 복사하는 기능을 제공합니다. 얕은 복사는 1차원 객체에, 깊은 복사는 중첩된 가변 객체에 사용하며, 상황에 따라 적절한 복사 방법을 선택해야 합니다.

### 배운 내용
- ✅ 참조 복사와 객체 복사의 차이
- ✅ copy.copy()로 얕은 복사 수행
- ✅ copy.deepcopy()로 깊은 복사 수행
- ✅ 중첩 객체의 복사 동작 이해
- ✅ 복사 방법 선택 기준
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('bisect 모듈 - 이진 탐색', '정렬된 리스트에서 효율적인 탐색과 삽입을 위한 bisect 모듈', '# bisect 모듈 - 이진 탐색

bisect 모듈은 정렬된 리스트를 유지하면서 효율적으로 탐색하고 삽입하는 기능을 제공합니다. 선형 탐색 O(n) 대신 이진 탐색 O(log n)을 사용하여 대용량 데이터를 빠르게 처리합니다.

## 학습 목표

- [ ] 이진 탐색 알고리즘의 원리와 O(log n) 시간 복잡도를 이해할 수 있다
- [ ] bisect_left()와 bisect_right()의 차이점을 구분하여 사용할 수 있다
- [ ] insort()를 활용하여 정렬 상태를 유지하며 요소를 삽입할 수 있다
- [ ] 실무 예제(성적 등급 계산)에 bisect를 적용할 수 있다

## 이진 탐색이란?

이진 탐색은 정렬된 배열에서 중간값과 비교하며 탐색 범위를 절반씩 줄여가는 알고리즘입니다. 매 단계마다 탐색 범위가 절반으로 줄어들기 때문에 시간 복잡도는 O(log n)입니다.

bisect 모듈은 이진 탐색을 활용하여 정렬된 리스트에서 삽입 위치를 찾거나, 정렬 상태를 유지하며 요소를 삽입하는 기능을 제공합니다. 대용량 데이터를 다룰 때 매번 정렬하는 것보다 훨씬 효율적입니다.

## 기본 사용법

```python
import bisect

# 정렬된 리스트
numbers = [1, 3, 5, 7, 9]

# 삽입 위치 찾기
pos = bisect.bisect(numbers, 6)
print(f"6을 삽입할 위치: {pos}")  # 3

# 정렬 상태 유지하며 삽입
bisect.insort(numbers, 6)
print(numbers)  # [1, 3, 5, 6, 7, 9]
```

`bisect()`는 삽입 위치만 반환하고, `insort()`는 직접 삽입까지 수행합니다.

## 주요 예제

### 예제 1: bisect_left vs bisect_right

```python
import bisect

numbers = [1, 3, 5, 5, 5, 7, 9]

# bisect_left: 같은 값이 있으면 왼쪽
left = bisect.bisect_left(numbers, 5)
print(f"bisect_left: {left}")  # 2

# bisect_right: 같은 값이 있으면 오른쪽
right = bisect.bisect_right(numbers, 5)
print(f"bisect_right: {right}")  # 5
```

### 예제 2: 성적 등급 계산

```python
import bisect

def get_grade(score):
    breakpoints = [60, 70, 80, 90]
    grades = [''F'', ''D'', ''C'', ''B'', ''A'']
    index = bisect.bisect(breakpoints, score)
    return grades[index]

# 테스트
scores = [45, 65, 75, 85, 95]
for score in scores:
    print(f"{score}점: {get_grade(score)}")
# 45점: F, 65점: D, 75점: C, 85점: B, 95점: A
```

### 예제 3: 정렬된 리스트 유지

```python
import bisect

sorted_list = []

# 순서대로 삽입하지 않아도 정렬 유지
for value in [5, 2, 8, 1, 9, 3]:
    bisect.insort(sorted_list, value)
    print(sorted_list)

# [5]
# [2, 5]
# [2, 5, 8]
# [1, 2, 5, 8]
# [1, 2, 5, 8, 9]
# [1, 2, 3, 5, 8, 9]
```

### 예제 4: 범위 검색 (특정 값의 개수)

```python
import bisect

def count_range(arr, value):
    """정렬된 배열에서 value의 개수를 O(log n)으로 구하기"""
    left = bisect.bisect_left(arr, value)
    right = bisect.bisect_right(arr, value)
    return right - left

numbers = [1, 2, 3, 3, 3, 4, 5]
count = count_range(numbers, 3)
print(f"3의 개수: {count}")  # 3
```

## 주의사항

- ⚠️ **반드시 정렬된 리스트**에서만 사용해야 합니다. 정렬되지 않은 리스트에서는 잘못된 결과가 나옵니다
- ⚠️ `insort()`는 O(n) 시간이 걸립니다. 삽입 위치를 찾는 것은 O(log n)이지만, 리스트에 삽입하는 것은 O(n)입니다
- 💡 대량의 삽입이 필요하면 모두 추가 후 `sort()`를 한 번 호출하는 것이 더 효율적일 수 있습니다
- 💡 `bisect`는 `bisect_right`의 별칭이며, `insort`는 `insort_right`의 별칭입니다

## 정리

bisect 모듈은 정렬된 리스트에서 이진 탐색을 활용하여 효율적인 탐색과 삽입을 제공합니다. bisect_left/right로 삽입 위치를 찾고, insort로 정렬 상태를 유지하며 삽입할 수 있습니다.

### 배운 내용
- ✅ 이진 탐색은 O(log n) 시간에 삽입 위치를 찾습니다
- ✅ bisect_left는 같은 값의 왼쪽, bisect_right는 오른쪽에 삽입합니다
- ✅ insort()로 정렬 상태를 유지하며 삽입할 수 있습니다
- ✅ 성적 등급 계산, 범위 검색 등 실무 문제에 활용할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('heapq 모듈 - 힙 자료구조', '힙 자료구조와 우선순위 큐를 효율적으로 구현하는 방법', '# heapq 모듈 - 힙 자료구조

힙은 효율적인 우선순위 큐를 구현하기 위한 자료구조입니다. Python의 heapq 모듈을 사용하면 최소 힙을 쉽게 구현하고 활용할 수 있습니다.

## 학습 목표

- [ ] 힙 자료구조의 개념과 특성을 설명할 수 있다
- [ ] heappush()와 heappop()을 사용하여 힙을 조작할 수 있다
- [ ] heapq를 활용하여 우선순위 큐를 구현할 수 있다
- [ ] nlargest()와 nsmallest()를 사용하여 최댓값/최솟값을 효율적으로 찾을 수 있다

## 힙이란?

힙은 완전 이진 트리 구조로, 부모 노드가 항상 자식 노드보다 작거나 같은 값을 갖는 자료구조입니다. Python의 heapq는 최소 힙을 구현하므로, 가장 작은 값이 항상 인덱스 0에 위치합니다.

힙은 우선순위 큐를 구현할 때 주로 사용되며, 삽입과 삭제가 O(log n)의 시간 복잡도로 매우 효율적입니다. 최댓값/최솟값을 반복적으로 추출해야 하는 상황에서 정렬(O(n log n))보다 훨씬 빠릅니다.

## 기본 사용법

```python
import heapq

# 빈 힙 생성
heap = []

# 원소 추가
heapq.heappush(heap, 5)
heapq.heappush(heap, 1)
heapq.heappush(heap, 3)

# 최솟값 추출
print(heapq.heappop(heap))  # 1
```

## 주요 예제

### 예제 1: heapify로 리스트를 힙으로 변환

```python
import heapq

# 일반 리스트를 힙으로 변환
numbers = [5, 2, 8, 1, 9, 3]
heapq.heapify(numbers)

print(numbers)  # [1, 2, 3, 5, 9, 8]
print(numbers[0])  # 1 (항상 최솟값)
```

### 예제 2: 우선순위 큐 구현

```python
import heapq

# (우선순위, 작업) 튜플로 우선순위 큐 구현
tasks = []
heapq.heappush(tasks, (3, "낮은 우선순위"))
heapq.heappush(tasks, (1, "높은 우선순위"))
heapq.heappush(tasks, (2, "중간 우선순위"))

while tasks:
    priority, task = heapq.heappop(tasks)
    print(f"{priority}: {task}")
# 출력: 1: 높은 우선순위, 2: 중간 우선순위, 3: 낮은 우선순위
```

### 예제 3: 최대 힙 구현 (음수 활용)

```python
import heapq

# 최대 힙은 값을 음수로 저장
max_heap = []
for num in [5, 2, 8, 1, 9]:
    heapq.heappush(max_heap, -num)

# 최댓값 추출
max_value = -heapq.heappop(max_heap)
print(max_value)  # 9
```

### 예제 4: nlargest와 nsmallest 활용

```python
import heapq

numbers = [10, 3, 15, 7, 22, 8, 1, 19]

# 가장 큰 3개 찾기
top3 = heapq.nlargest(3, numbers)
print(top3)  # [22, 19, 15]

# 가장 작은 3개 찾기
bottom3 = heapq.nsmallest(3, numbers)
print(bottom3)  # [1, 3, 7]
```

### 예제 5: 실시간 중앙값 찾기

```python
import heapq

class MedianFinder:
    def __init__(self):
        self.small = []  # 최대 힙 (음수 저장)
        self.large = []  # 최소 힙

    def add(self, num):
        heapq.heappush(self.small, -num)
        heapq.heappush(self.large, -heapq.heappop(self.small))

        if len(self.small) < len(self.large):
            heapq.heappush(self.small, -heapq.heappop(self.large))

mf = MedianFinder()
for num in [1, 3, 5]:
    mf.add(num)
print(-mf.small[0])  # 중앙값: 3
```

## 주의사항

- ⚠️ heapq는 최소 힙만 지원합니다. 최대 힙이 필요하면 값을 음수로 변환하세요
- ⚠️ 힙은 완전히 정렬된 상태가 아닙니다. 오직 heap[0]만 최솟값임이 보장됩니다
- 💡 nlargest/nsmallest는 n이 작을 때 sorted()보다 효율적입니다
- 💡 우선순위가 같을 때의 순서가 중요하다면 (count, priority, item) 형태로 저장하세요
- ⚠️ heappop()을 빈 힙에서 호출하면 IndexError가 발생합니다

## 정리

heapq 모듈은 우선순위 큐와 효율적인 최댓값/최솟값 탐색을 위한 강력한 도구입니다. O(log n)의 삽입/삭제 성능으로 대용량 데이터 처리에 적합합니다.

### 배운 내용
- ✅ 힙은 최솟값을 O(1)에 조회하고 O(log n)에 삽입/삭제할 수 있는 자료구조입니다
- ✅ heappush/heappop으로 힙을 조작하고, heapify로 리스트를 힙으로 변환합니다
- ✅ 튜플을 활용하여 우선순위 큐를 구현할 수 있습니다
- ✅ nlargest/nsmallest는 상위/하위 k개 원소를 효율적으로 찾습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('array 모듈 - 효율적인 배열', '메모리 효율적인 배열 처리를 위한 array 모듈 활용법', '# array 모듈 - 효율적인 배열

Python의 리스트는 편리하지만 대량의 숫자 데이터를 다룰 때는 메모리를 많이 사용합니다. array 모듈은 동일한 타입의 데이터를 메모리 효율적으로 저장할 수 있는 방법을 제공합니다.

## 학습 목표

- [ ] array 모듈의 필요성과 리스트와의 차이점을 설명할 수 있다
- [ ] 타입 코드를 사용하여 array 객체를 생성하고 조작할 수 있다
- [ ] 메모리 사용량을 비교하여 적절한 자료구조를 선택할 수 있다
- [ ] 바이너리 데이터 처리에 array를 효과적으로 활용할 수 있다

## array 모듈이란?

array 모듈은 C 언어의 배열처럼 동일한 타입의 데이터만 저장할 수 있는 배열을 제공합니다. 리스트는 어떤 타입의 객체도 저장할 수 있는 유연성이 있지만, 그만큼 메모리 오버헤드가 큽니다.

array는 타입을 지정하여 생성하므로 각 요소가 고정된 크기의 메모리만 사용합니다. 예를 들어, 정수 배열의 각 요소는 4바이트만 사용하지만, 리스트의 각 정수는 객체 오버헤드까지 포함하여 28바이트 이상을 사용합니다.

주로 대량의 숫자 데이터(센서 데이터, 이미지 픽셀 값 등)를 처리하거나 파일 입출력, 네트워크 통신에서 바이너리 데이터를 다룰 때 유용합니다.

## 기본 사용법

```python
import array

# 정수 배열 생성 (''i'' = signed int)
numbers = array.array(''i'', [1, 2, 3, 4, 5])
print(numbers)  # array(''i'', [1, 2, 3, 4, 5])

# 실수 배열 생성 (''f'' = float)
floats = array.array(''f'', [1.1, 2.2, 3.3])
print(floats)  # array(''f'', [1.1, 2.2, 3.3])
```

타입 코드는 배열에 저장할 데이터 타입을 지정합니다. ''i''는 정수, ''f''는 단정밀도 실수를 의미합니다.

## 주요 타입 코드

### 타입 코드 종류

```python
import array

# ''i'': signed int (4바이트)
ints = array.array(''i'', [-100, 0, 100])

# ''I'': unsigned int (4바이트, 양수만)
uints = array.array(''I'', [0, 100, 200])

# ''f'': float (4바이트)
floats = array.array(''f'', [1.5, 2.5, 3.5])

# ''d'': double (8바이트, 더 정밀한 실수)
doubles = array.array(''d'', [1.123456789, 2.987654321])

print(f"Int: {ints}")
print(f"Float: {floats}")
```

## 주요 예제

### 예제 1: 배열 조작

```python
import array

arr = array.array(''i'', [10, 20, 30])

# 요소 추가
arr.append(40)
arr.extend([50, 60])

# 인덱싱과 슬라이싱
print(arr[0])     # 10
print(arr[1:4])   # array(''i'', [20, 30, 40])

# 요소 수정
arr[0] = 15
print(arr)        # array(''i'', [15, 20, 30, 40, 50, 60])
```

### 예제 2: 메모리 사용량 비교

```python
import array
import sys

# 10000개의 정수를 리스트와 array로 저장
data = list(range(10000))
list_data = data
array_data = array.array(''i'', data)

# 메모리 사용량 비교
list_size = sys.getsizeof(list_data)
array_size = array_data.buffer_info()[1] * array_data.itemsize

print(f"리스트 크기: {list_size:,} 바이트")
print(f"array 크기: {array_size:,} 바이트")
print(f"절약: {list_size - array_size:,} 바이트")
# 리스트는 약 80KB, array는 40KB 사용
```

### 예제 3: 파일 입출력

```python
import array

# 배열 데이터를 파일에 저장
numbers = array.array(''i'', [1, 2, 3, 4, 5])
with open(''data.bin'', ''wb'') as f:
    numbers.tofile(f)

# 파일에서 배열 데이터 읽기
loaded = array.array(''i'')
with open(''data.bin'', ''rb'') as f:
    loaded.fromfile(f, 5)  # 5개 요소 읽기

print(loaded)  # array(''i'', [1, 2, 3, 4, 5])
```

### 예제 4: 리스트로 변환

```python
import array

arr = array.array(''d'', [1.1, 2.2, 3.3, 4.4])

# array를 리스트로 변환
lst = arr.tolist()
print(lst)           # [1.1, 2.2, 3.3, 4.4]
print(type(lst))     # <class ''list''>

# 계산에 활용
average = sum(lst) / len(lst)
print(f"평균: {average}")  # 평균: 2.75
```

### 예제 5: 바이트 데이터 처리

```python
import array

# 바이트 데이터로 변환
arr = array.array(''i'', [100, 200, 300])
byte_data = arr.tobytes()
print(byte_data)  # b''d\x00\x00\x00\xc8\x00\x00\x00...''

# 바이트에서 배열 생성
new_arr = array.array(''i'')
new_arr.frombytes(byte_data)
print(new_arr)    # array(''i'', [100, 200, 300])
```

## 주의사항

- ⚠️ **타입 제약**: array는 동일한 타입만 저장 가능합니다. 다른 타입을 추가하면 TypeError가 발생합니다.
- ⚠️ **범위 오류**: 타입의 범위를 벗어난 값을 저장하면 OverflowError가 발생합니다.
- 💡 **NumPy 고려**: 더 복잡한 수치 계산이 필요하다면 NumPy 라이브러리를 사용하는 것이 좋습니다.
- 💡 **적절한 선택**: 작은 데이터나 혼합 타입이 필요하면 리스트를, 대량의 동일 타입 숫자 데이터는 array를 사용하세요.

## 정리

array 모듈은 메모리 효율적으로 동일 타입의 데이터를 저장하는 방법을 제공합니다. 리스트보다 50% 이상 메모리를 절약할 수 있으며, 바이너리 파일 처리에도 유용합니다.

### 배운 내용

- ✅ array는 타입 코드를 사용하여 동일 타입 데이터를 효율적으로 저장한다
- ✅ ''i'', ''f'', ''d'' 등의 타입 코드로 정수, 실수 배열을 생성할 수 있다
- ✅ array는 리스트보다 메모리를 50% 이상 절약할 수 있다
- ✅ tofile(), fromfile()로 바이너리 데이터를 효율적으로 처리할 수 있다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('struct 모듈 - 바이너리 데이터 처리', 'struct 모듈로 바이너리 데이터를 패킹/언패킹하는 방법', '# struct 모듈 - 바이너리 데이터 처리

네트워크 통신이나 파일 포맷 처리 시 바이너리 데이터를 다뤄야 할 때가 있습니다. struct 모듈은 Python 객체를 바이너리로 변환하거나, 바이너리를 Python 객체로 파싱하는 강력한 도구입니다.

## 학습 목표

- [ ] 바이너리 데이터의 개념과 필요성을 이해할 수 있다
- [ ] struct.pack()을 사용하여 Python 데이터를 바이너리로 변환할 수 있다
- [ ] struct.unpack()을 사용하여 바이너리 데이터를 파싱할 수 있다
- [ ] 포맷 문자열(i, f, d, s 등)을 작성할 수 있다
- [ ] 바이트 순서(엔디안)의 개념을 이해하고 지정할 수 있다

## 바이너리 데이터란?

바이너리 데이터는 컴퓨터가 직접 이해하는 0과 1의 형태로 저장된 데이터입니다. 텍스트와 달리 사람이 직접 읽을 수 없지만, 메모리 효율이 높고 처리 속도가 빠릅니다.

struct 모듈은 C 구조체와 호환되는 방식으로 바이너리 데이터를 처리합니다. 네트워크 프로토콜, 이미지 파일, 데이터베이스 파일 등을 다룰 때 필수적입니다.

## 기본 사용법

```python
import struct

# 정수를 바이너리로 변환 (패킹)
binary_data = struct.pack(''i'', 42)
print(binary_data)  # b''*\x00\x00\x00''

# 바이너리를 정수로 변환 (언패킹)
number = struct.unpack(''i'', binary_data)
print(number)  # (42,)
```

pack()은 데이터를 바이너리로 변환하고, unpack()은 바이너리를 원래 데이터로 복원합니다. 반환값은 항상 튜플 형태입니다.

## 주요 예제

### 예제 1: 다양한 포맷 문자열
```python
import struct

# i: 정수, f: 실수, s: 문자열
data = struct.pack(''if5s'', 10, 3.14, b''hello'')
print(f"바이너리 크기: {len(data)}바이트")  # 17바이트

# 언패킹
num, pi, text = struct.unpack(''if5s'', data)
print(f"정수: {num}, 실수: {pi:.2f}, 문자열: {text}")
```

### 예제 2: 엔디안 지정
```python
import struct

# 리틀 엔디안 (<), 빅 엔디안 (>)
little = struct.pack(''<i'', 0x12345678)
big = struct.pack(''>i'', 0x12345678)

print(f"리틀 엔디안: {little.hex()}")  # 78563412
print(f"빅 엔디안: {big.hex()}")      # 12345678
```

### 예제 3: 여러 값 처리
```python
import struct

# 구조체 정의: 정수 2개, 실수 1개
fmt = ''iif''
values = (100, 200, 3.14)

# 패킹
packed = struct.pack(fmt, *values)
print(f"크기: {struct.calcsize(fmt)}바이트")  # 12바이트

# 언패킹
unpacked = struct.unpack(fmt, packed)
print(unpacked)  # (100, 200, 3.140000104904175)
```

### 예제 4: 바이너리 파일 읽기/쓰기
```python
import struct

# 바이너리 파일에 쓰기
with open(''data.bin'', ''wb'') as f:
    data = struct.pack(''iii'', 10, 20, 30)
    f.write(data)

# 바이너리 파일 읽기
with open(''data.bin'', ''rb'') as f:
    binary = f.read()
    numbers = struct.unpack(''iii'', binary)
    print(numbers)  # (10, 20, 30)
```

### 예제 5: 네트워크 패킷 만들기
```python
import struct

# 헤더: 버전(1바이트), 길이(2바이트), ID(4바이트)
version = 1
length = 256
packet_id = 12345

# 패킹 (B: unsigned char, H: unsigned short, I: unsigned int)
header = struct.pack(''!BHI'', version, length, packet_id)
print(f"헤더 크기: {len(header)}바이트")
print(f"헤더 데이터: {header.hex()}")

# 언패킹
v, l, pid = struct.unpack(''!BHI'', header)
print(f"버전: {v}, 길이: {l}, ID: {pid}")
```

## 주요 포맷 문자

| 문자 | C 타입 | Python 타입 | 크기 |
|------|--------|-------------|------|
| `c` | char | bytes (길이 1) | 1 |
| `b` | signed char | int | 1 |
| `B` | unsigned char | int | 1 |
| `h` | short | int | 2 |
| `H` | unsigned short | int | 2 |
| `i` | int | int | 4 |
| `I` | unsigned int | int | 4 |
| `f` | float | float | 4 |
| `d` | double | float | 8 |
| `s` | char[] | bytes | 가변 |

## 바이트 순서 (엔디안)

| 문자 | 의미 | 설명 |
|------|------|------|
| `@` | 네이티브 | 시스템 기본값 (기본) |
| `=` | 네이티브 | 표준 크기, 정렬 없음 |
| `<` | 리틀 엔디안 | 낮은 바이트 먼저 |
| `>` | 빅 엔디안 | 높은 바이트 먼저 (네트워크) |
| `!` | 네트워크 | 빅 엔디안과 동일 |

## 주의사항

- ⚠️ unpack()의 반환값은 항상 튜플입니다 (값이 하나여도 튜플)
- ⚠️ 바이트 크기가 정확히 일치해야 합니다 (calcsize()로 확인)
- ⚠️ 문자열은 bytes 타입이어야 하며, 길이를 명시해야 합니다
- 💡 네트워크 통신 시에는 `!` (빅 엔디안) 사용을 권장합니다
- 💡 struct.calcsize()로 패킹된 데이터 크기를 미리 계산할 수 있습니다
- 💡 성능이 중요한 경우 Struct 객체를 재사용하면 효율적입니다

## 정리

struct 모듈은 Python 데이터와 바이너리 데이터 간 변환을 제공하는 표준 라이브러리입니다. 네트워크 프로토콜 구현, 바이너리 파일 처리, 시스템 프로그래밍에서 필수적인 도구입니다.

### 배운 내용
- ✅ struct.pack()으로 Python 데이터를 바이너리로 변환
- ✅ struct.unpack()으로 바이너리를 Python 데이터로 파싱
- ✅ 포맷 문자열(i, f, d, s)로 데이터 타입 지정
- ✅ 엔디안(<, >, !)으로 바이트 순서 제어
- ✅ calcsize()로 바이너리 크기 계산
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('io 모듈 - 고급 입출력', 'io 모듈의 스트림 계층과 메모리 내 파일 객체 활용법', '# io 모듈 - 고급 입출력

Python의 io 모듈은 파일과 스트림을 다루는 핵심 도구입니다. 메모리 내에서 파일처럼 동작하는 객체를 만들거나, 텍스트와 바이너리 데이터를 효율적으로 처리할 수 있습니다.

## 학습 목표

- [ ] io 모듈의 스트림 계층 구조를 이해하고 설명할 수 있다
- [ ] StringIO와 BytesIO를 사용하여 메모리 내 파일 객체를 생성할 수 있다
- [ ] 텍스트 스트림과 바이너리 스트림의 차이점을 구분하고 활용할 수 있다
- [ ] BufferedReader와 TextIOWrapper의 역할을 이해하고 적용할 수 있다

## io 모듈이란?

io 모듈은 Python의 입출력 작업을 위한 핵심 라이브러리입니다. 파일 객체와 동일한 인터페이스를 제공하면서도 메모리 내에서 동작하는 객체를 만들 수 있어, 테스트나 데이터 변환 작업에 매우 유용합니다.

스트림은 크게 텍스트 스트림(문자열 데이터)과 바이너리 스트림(바이트 데이터)으로 나뉩니다. io 모듈은 이러한 스트림들을 계층적으로 구성하여 효율적인 버퍼링과 인코딩을 지원합니다.

실제 파일 I/O 없이 파일 작업을 시뮬레이션하거나, API 응답을 파일처럼 처리하거나, 데이터를 메모리에서 변환할 때 io 모듈이 필요합니다.

## 기본 사용법

```python
import io

# StringIO: 메모리 내 텍스트 스트림
text_stream = io.StringIO()
text_stream.write("Hello, World!\n")
text_stream.write("Python IO")
print(text_stream.getvalue())  # Hello, World!\nPython IO

# BytesIO: 메모리 내 바이너리 스트림
binary_stream = io.BytesIO()
binary_stream.write(b"Binary data")
print(binary_stream.getvalue())  # b''Binary data''
```

StringIO와 BytesIO는 각각 문자열과 바이트를 메모리에 저장하는 파일 객체처럼 동작합니다.

## 주요 예제

### 예제 1: StringIO로 CSV 데이터 처리
```python
import io
import csv

# 메모리에서 CSV 데이터 생성
csv_data = io.StringIO()
writer = csv.writer(csv_data)
writer.writerow([''Name'', ''Age'', ''City''])
writer.writerow([''Alice'', 30, ''Seoul''])

# 읽기 위해 커서를 처음으로 이동
csv_data.seek(0)
reader = csv.reader(csv_data)
for row in reader:
    print(row)
# [''Name'', ''Age'', ''City'']
# [''Alice'', ''30'', ''Seoul'']
```

### 예제 2: BytesIO로 이미지 데이터 처리
```python
import io

# 바이너리 데이터를 메모리에 저장
image_data = b''\x89PNG\r\n\x1a\n...''  # PNG 헤더 예시
buffer = io.BytesIO(image_data)

# 파일처럼 읽기
header = buffer.read(8)
print(header)  # b''\x89PNG\r\n\x1a\n''

# 커서 위치 확인 및 이동
print(buffer.tell())  # 8
buffer.seek(0)
print(buffer.tell())  # 0
```

### 예제 3: TextIOWrapper로 인코딩 처리
```python
import io

# 바이너리 스트림을 텍스트 스트림으로 래핑
binary_stream = io.BytesIO(b''Hello, \xed\x95\x9c\xea\xb8\x80'')
text_stream = io.TextIOWrapper(binary_stream, encoding=''utf-8'')

content = text_stream.read()
print(content)  # Hello, 한글

# 반드시 닫아주기
text_stream.close()
```

### 예제 4: 파일 대신 메모리 사용 (테스트)
```python
import io

def process_file(file_obj):
    """파일 객체를 받아 처리하는 함수"""
    content = file_obj.read()
    return content.upper()

# 실제 파일 대신 StringIO 사용
fake_file = io.StringIO("test data")
result = process_file(fake_file)
print(result)  # TEST DATA
```

### 예제 5: BufferedReader로 효율적인 읽기
```python
import io

# 바이너리 데이터 생성
data = b''A'' * 1000
raw_stream = io.BytesIO(data)

# BufferedReader로 래핑
buffered = io.BufferedReader(raw_stream, buffer_size=100)

# 청크 단위로 읽기
chunk = buffered.read(50)
print(len(chunk))  # 50
print(buffered.tell())  # 50
```

## 주의사항

- ⚠️ **StringIO는 문자열만, BytesIO는 바이트만 처리**: StringIO에 바이트를 쓰거나 BytesIO에 문자열을 쓰면 TypeError가 발생합니다
- ⚠️ **getvalue() 사용 타이밍**: 스트림이 닫힌 후에는 getvalue()를 호출할 수 없습니다
- 💡 **메모리 효율성**: 대용량 데이터는 실제 파일을 사용하는 것이 더 효율적입니다. io 모듈은 작은 데이터나 테스트에 적합합니다
- 💡 **seek(0) 활용**: 스트림에 쓴 후 읽으려면 반드시 커서를 처음으로 이동해야 합니다
- 💡 **컨텍스트 매니저**: with 문을 사용하면 자동으로 close()가 호출됩니다

## 정리

io 모듈은 메모리 내에서 파일처럼 동작하는 객체를 제공하여 실제 파일 I/O 없이 데이터를 처리할 수 있게 합니다. StringIO는 텍스트 데이터를, BytesIO는 바이너리 데이터를 다루며, TextIOWrapper는 바이너리 스트림에 인코딩 기능을 추가합니다.

### 배운 내용
- ✅ StringIO와 BytesIO로 메모리 내 파일 객체 생성
- ✅ 텍스트 스트림과 바이너리 스트림의 차이점
- ✅ TextIOWrapper로 인코딩 처리
- ✅ BufferedReader로 효율적인 버퍼링
- ✅ 파일 대신 메모리 스트림을 사용하는 테스트 기법
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('base64 모듈 - Base64 인코딩', 'Base64 인코딩으로 바이너리 데이터를 텍스트로 변환하기', '# base64 모듈 - Base64 인코딩

바이너리 데이터를 텍스트로 전송해야 할 때 Base64 인코딩을 사용합니다. 이메일 첨부파일, API 응답, 이미지 데이터 전송 등에서 필수적으로 활용됩니다.

## 학습 목표

- [ ] Base64 인코딩의 원리와 필요성을 설명할 수 있다
- [ ] b64encode()와 b64decode()로 바이너리 데이터를 인코딩/디코딩할 수 있다
- [ ] urlsafe_b64encode()를 사용해 URL-safe 인코딩을 수행할 수 있다
- [ ] 실무에서 Base64를 활용하는 사례를 구현할 수 있다

## Base64 인코딩이란?

Base64는 바이너리 데이터를 64개의 안전한 ASCII 문자(A-Z, a-z, 0-9, +, /)로 변환하는 인코딩 방식입니다. 텍스트만 지원하는 통신 채널(이메일, JSON, XML)에서 바이너리 데이터를 전송할 때 필요합니다.

바이너리 3바이트(24비트)를 4개의 ASCII 문자로 변환하므로, 인코딩된 데이터는 원본보다 약 33% 증가합니다. 하지만 데이터 손실 없이 안전하게 전송할 수 있다는 장점이 있습니다.

## 기본 사용법

```python
import base64

# 문자열을 Base64로 인코딩
text = "Hello, Python!"
encoded = base64.b64encode(text.encode(''utf-8''))
print(f"인코딩: {encoded}")
# 출력: b''SGVsbG8sIFB5dGhvbiE=''

# 디코딩
decoded = base64.b64decode(encoded)
print(f"디코딩: {decoded.decode(''utf-8'')}")
# 출력: Hello, Python!
```

`b64encode()`는 bytes를 입력받아 Base64 인코딩된 bytes를 반환합니다. 문자열은 먼저 `.encode()`로 bytes로 변환해야 합니다.

## 주요 예제

### 예제 1: 이미지 파일을 Base64로 인코딩
```python
import base64

# 이미지 파일을 Base64로 인코딩 (HTML/JSON에 임베딩)
with open(''image.png'', ''rb'') as f:
    image_data = f.read()
    encoded_image = base64.b64encode(image_data)

# HTML img 태그에 사용
data_uri = f"data:image/png;base64,{encoded_image.decode()}"
print(data_uri[:50])  # 앞부분만 출력
```

### 예제 2: URL-safe Base64 인코딩
```python
import base64

# URL 파라미터로 사용할 데이터 인코딩
data = b"user_id=123&token=abc/xyz+=="

# 일반 Base64 (URL에 안전하지 않음)
standard = base64.b64encode(data)
print(f"표준: {standard}")
# 출력: b''dXNlcl9pZD0xMjMmdG9rZW49YWJjL3h5eis9PQ==''

# URL-safe Base64 (+, / 대신 -, _ 사용)
urlsafe = base64.urlsafe_b64encode(data)
print(f"URL-safe: {urlsafe}")
# 출력: b''dXNlcl9pZD0xMjMmdG9rZW49YWJjL3h5eis9PQ==''
```

### 예제 3: API 인증 토큰 생성
```python
import base64
import json

# API 인증 정보를 Base64로 인코딩
credentials = {
    "username": "admin",
    "api_key": "secret123"
}

# JSON으로 변환 후 Base64 인코딩
json_str = json.dumps(credentials)
token = base64.b64encode(json_str.encode(''utf-8''))
print(f"Authorization Token: {token.decode()}")

# 토큰 검증 (디코딩)
decoded_json = base64.b64decode(token).decode(''utf-8'')
verified = json.loads(decoded_json)
print(f"검증됨: {verified}")
```

### 예제 4: 이메일 첨부파일 인코딩
```python
import base64

# 파일을 이메일 첨부 형식으로 인코딩
def encode_attachment(filename):
    with open(filename, ''rb'') as f:
        data = f.read()
        # 76자마다 줄바꿈 (MIME 표준)
        encoded = base64.encodebytes(data)
        return encoded

# PDF 파일 인코딩
attachment = encode_attachment(''document.pdf'')
print(f"첨부파일 크기: {len(attachment)} bytes")
```

### 예제 5: Base64 패딩 제거/복원
```python
import base64

data = b"Python"

# 일반 인코딩 (패딩 포함)
encoded = base64.b64encode(data)
print(f"패딩 포함: {encoded}")
# 출력: b''UHl0aG9u''

# 패딩 제거 (일부 시스템에서 사용)
no_padding = encoded.rstrip(b''='')
print(f"패딩 제거: {no_padding}")

# 디코딩 시 패딩 복원 필요
padding_needed = 4 - (len(no_padding) % 4)
if padding_needed != 4:
    no_padding += b''='' * padding_needed

decoded = base64.b64decode(no_padding)
print(f"디코딩: {decoded}")
```

## 주의사항

- ⚠️ **보안 목적 아님**: Base64는 인코딩이지 암호화가 아닙니다. 누구나 쉽게 디코딩할 수 있으므로 민감한 정보를 보호하는 용도로 사용하면 안 됩니다.
- ⚠️ **데이터 크기 증가**: 원본 데이터보다 약 33% 크기가 증가하므로, 대용량 데이터 전송 시 성능에 영향을 줄 수 있습니다.
- ⚠️ **bytes 입력 필수**: 문자열은 먼저 `.encode()`로 bytes로 변환해야 합니다.
- 💡 **URL 전송 시**: URL 파라미터나 경로에 포함할 때는 `urlsafe_b64encode()`를 사용하세요.
- 💡 **MIME 표준**: 이메일 첨부파일은 `encodebytes()`를 사용하면 76자마다 자동으로 줄바꿈됩니다.

## 정리

Base64 인코딩은 바이너리 데이터를 텍스트 기반 시스템에서 안전하게 전송하기 위한 필수 기술입니다. 이메일 첨부파일, 이미지 데이터 URI, API 토큰 등 다양한 실무 상황에서 활용됩니다.

### 배운 내용
- ✅ Base64는 바이너리 데이터를 64개의 안전한 ASCII 문자로 변환합니다
- ✅ b64encode()/b64decode()로 기본 인코딩/디코딩을 수행할 수 있습니다
- ✅ urlsafe_b64encode()는 URL에 안전한 문자(-, _)를 사용합니다
- ✅ Base64는 암호화가 아니라 인코딩 방식이므로 보안 목적으로 사용하면 안 됩니다
- ✅ 이미지 임베딩, API 인증, 이메일 첨부 등 다양한 실무 활용 사례가 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('hashlib 모듈 - 해시 함수', '해시 함수로 데이터 무결성을 검증하는 방법 학습', '# hashlib 모듈 - 해시 함수

hashlib은 MD5, SHA-1, SHA-256 같은 보안 해시 함수를 제공합니다. 데이터 무결성 검증, 비밀번호 저장, 파일 중복 검사 등에 활용됩니다.

## 학습 목표

- [ ] 해시 함수의 개념과 특성을 이해한다
- [ ] MD5, SHA-1, SHA-256 해시를 생성할 수 있다
- [ ] update()를 사용하여 점진적으로 해시를 계산할 수 있다
- [ ] hexdigest()와 digest()의 차이를 이해한다
- [ ] 해시의 활용 사례를 이해한다

## 해시 함수란?

해시 함수는 임의 크기의 데이터를 고정 크기의 값(해시값)으로 변환하는 함수입니다. 같은 입력에 대해서는 항상 같은 해시값을 생성하며, 조금만 입력이 달라져도 완전히 다른 해시값이 나옵니다.

주요 특성:
- **결정적**: 동일한 입력은 항상 동일한 출력을 생성
- **단방향**: 해시값으로부터 원본 데이터를 복원할 수 없음
- **충돌 회피**: 서로 다른 입력이 같은 해시값을 가질 확률이 매우 낮음
- **눈사태 효과**: 입력의 작은 변화가 해시값을 크게 변화시킴

hashlib은 파일 무결성 검증, 비밀번호 저장, 중복 파일 탐지 등에 사용됩니다. MD5와 SHA-1은 보안 취약점이 발견되어 SHA-256 이상의 사용이 권장됩니다.

## 기본 사용법

```python
import hashlib

# 문자열 해시 생성
text = "안녕하세요"
hash_value = hashlib.sha256(text.encode()).hexdigest()
print(f"SHA-256: {hash_value}")
# SHA-256: 2c68318e352971113645cbc72861e1ec23f48d5baa5f9b405fed9dddca893eb4
```

바이트 데이터를 입력받아 해시 객체를 생성하고, hexdigest()로 16진수 문자열을 얻습니다.

## 주요 예제

### 예제 1: 다양한 해시 알고리즘

```python
import hashlib

data = b"Python hashlib"

# 다양한 해시 알고리즘
print(f"MD5:     {hashlib.md5(data).hexdigest()}")
print(f"SHA-1:   {hashlib.sha1(data).hexdigest()}")
print(f"SHA-256: {hashlib.sha256(data).hexdigest()}")
print(f"SHA-512: {hashlib.sha512(data).hexdigest()}")

# 출력:
# MD5:     a7d80d5e9bfd1a3c0aef8a59d1c63e5f
# SHA-1:   ff6e266e8a84be8f1e4e31c3c53a5f4c7aade5b0
# SHA-256: b2a9c4e5d7f8a3c1e6f5d8c9a7b4e3f2d5c8a1e4f3b2a5d8c7e1f4a3b2c5d8e1
# SHA-512: (128자 16진수 문자열)
```

### 예제 2: update()로 점진적 해시 계산

```python
import hashlib

# 한 번에 해시 계산
hash1 = hashlib.sha256(b"HelloWorld").hexdigest()

# 여러 번에 나누어 계산
hash_obj = hashlib.sha256()
hash_obj.update(b"Hello")
hash_obj.update(b"World")
hash2 = hash_obj.hexdigest()

print(f"한번에:   {hash1}")
print(f"나누어서: {hash2}")
print(f"동일: {hash1 == hash2}")  # True
```

### 예제 3: digest()와 hexdigest() 비교

```python
import hashlib

data = b"test"
h = hashlib.sha256(data)

# digest(): 바이너리 바이트
binary = h.digest()
print(f"digest(): {binary[:8]}... (32 bytes)")

# hexdigest(): 16진수 문자열
hex_str = h.hexdigest()
print(f"hexdigest(): {hex_str} (64 chars)")
print(f"길이: digest={len(binary)}, hexdigest={len(hex_str)}")
```

### 예제 4: 파일 해시 계산

```python
import hashlib

def calculate_file_hash(filepath):
    """파일의 SHA-256 해시 계산"""
    sha256 = hashlib.sha256()

    with open(filepath, ''rb'') as f:
        # 큰 파일도 처리 가능하도록 청크 단위로 읽기
        while chunk := f.read(8192):
            sha256.update(chunk)

    return sha256.hexdigest()

# 사용 예
# hash_value = calculate_file_hash(''data.txt'')
# print(f"파일 해시: {hash_value}")
```

### 예제 5: 비밀번호 해싱 (간단한 예)

```python
import hashlib
import os

def hash_password(password):
    """비밀번호를 솔트와 함께 해싱"""
    salt = os.urandom(32)  # 랜덤 솔트 생성
    pwd_hash = hashlib.pbkdf2_hmac(''sha256'',
                                    password.encode(),
                                    salt,
                                    100000)  # 반복 횟수
    return salt + pwd_hash

def verify_password(stored, password):
    """비밀번호 검증"""
    salt = stored[:32]
    stored_hash = stored[32:]
    pwd_hash = hashlib.pbkdf2_hmac(''sha256'',
                                    password.encode(),
                                    salt,
                                    100000)
    return pwd_hash == stored_hash

# 사용 예
# hashed = hash_password("my_password")
# print(verify_password(hashed, "my_password"))  # True
```

## 주의사항

- MD5와 SHA-1은 보안 취약점이 발견되어 무결성 검증에만 사용하고, 보안이 중요한 경우 SHA-256 이상을 사용
- 비밀번호 저장 시 단순 해시 대신 pbkdf2_hmac, bcrypt, argon2 같은 전용 알고리즘 사용
- hexdigest()는 16진수 문자열(64자), digest()는 바이너리(32바이트) 반환
- 큰 파일 처리 시 update()를 사용하여 메모리 효율적으로 처리
- 해시는 암호화가 아니며 복호화할 수 없음

## 정리

hashlib 모듈은 데이터 무결성 검증과 보안 목적의 해시 함수를 제공합니다. SHA-256을 사용하여 파일 변조 검사, 데이터 중복 확인, 비밀번호 저장 등 다양한 용도로 활용할 수 있습니다.

### 배운 내용
- 해시 함수는 데이터를 고정 크기 값으로 변환하는 단방향 함수
- hashlib.sha256()으로 해시 생성, hexdigest()로 16진수 문자열 추출
- update()로 데이터를 점진적으로 추가하여 해시 계산 가능
- 파일 무결성 검증, 비밀번호 해싱 등에 활용
- 보안 용도로는 SHA-256 이상의 알고리즘 사용 권장
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('secrets 모듈 - 보안 난수 생성', '암호학적으로 안전한 난수 생성과 토큰 생성 방법', '# secrets 모듈 - 보안 난수 생성

비밀번호, 토큰, API 키 등 보안이 중요한 상황에서는 일반 난수가 아닌 암호학적으로 안전한 난수가 필요합니다. secrets 모듈은 이러한 보안 요구사항을 충족하는 난수 생성 기능을 제공합니다.

## 학습 목표

- [ ] random과 secrets의 차이를 이해하고 보안 상황에서 올바른 모듈을 선택할 수 있다
- [ ] token_bytes(), token_hex(), token_urlsafe() 함수를 사용하여 다양한 형식의 보안 토큰을 생성할 수 있다
- [ ] choice()와 randbelow()를 활용하여 암호학적으로 안전한 난수를 생성할 수 있다
- [ ] 실무에서 사용 가능한 보안 비밀번호와 API 키를 생성할 수 있다

## random vs secrets

**random 모듈**은 일반적인 시뮬레이션이나 게임에 적합하지만, 보안 목적으로는 부적합합니다. 예측 가능한 패턴을 사용하기 때문입니다.

**secrets 모듈**은 운영체제의 암호학적 난수 생성기를 사용하여 예측 불가능하고 보안에 안전한 난수를 생성합니다.

```python
import random
import secrets

# random: 보안에 부적합 (예측 가능)
print(random.randint(1, 100))  # 일반 난수

# secrets: 보안에 적합 (예측 불가능)
print(secrets.randbelow(100))  # 보안 난수 (0~99)
```

## 토큰 생성 함수

secrets 모듈은 세 가지 형식의 토큰 생성 함수를 제공합니다.

```python
import secrets

# 1. 바이트 형식 토큰
token_b = secrets.token_bytes(16)
print(f"bytes: {token_b}")  # 16바이트

# 2. 16진수 문자열 토큰
token_h = secrets.token_hex(16)
print(f"hex: {token_h}")  # 32자 (16바이트 = 32 hex)

# 3. URL 안전 Base64 토큰
token_u = secrets.token_urlsafe(16)
print(f"urlsafe: {token_u}")  # URL에 사용 가능
```

## 주요 함수

### choice() - 안전한 무작위 선택

```python
import secrets

# 리스트에서 안전하게 선택
colors = [''red'', ''blue'', ''green'']
selected = secrets.choice(colors)
print(selected)

# 문자 선택
alphabet = ''abcdefghijklmnopqrstuvwxyz''
char = secrets.choice(alphabet)
print(char)
```

### randbelow() - 범위 내 난수

```python
import secrets

# 0부터 n-1까지의 난수
dice = secrets.randbelow(6) + 1  # 1~6
print(f"주사위: {dice}")

# OTP 6자리 생성
otp = secrets.randbelow(1000000)
print(f"OTP: {otp:06d}")  # 000000~999999
```

## 주요 예제

### 예제 1: 보안 비밀번호 생성기

```python
import secrets
import string

def generate_password(length=12):
    alphabet = string.ascii_letters + string.digits + "!@#$%"
    password = ''''.join(secrets.choice(alphabet) for _ in range(length))
    return password

# 안전한 비밀번호 생성
pwd = generate_password(16)
print(f"생성된 비밀번호: {pwd}")
```

### 예제 2: API 키 생성

```python
import secrets

def generate_api_key():
    # 32바이트(256비트) 보안 키
    return secrets.token_urlsafe(32)

# API 키 생성
api_key = generate_api_key()
print(f"API Key: {api_key}")
print(f"길이: {len(api_key)}자")
```

### 예제 3: 세션 토큰 생성

```python
import secrets

def create_session_token():
    # 세션 토큰 (64자 16진수)
    return secrets.token_hex(32)

# 세션 관리
session_id = create_session_token()
print(f"Session ID: {session_id}")

# 추측 불가능성 확인
tokens = [create_session_token() for _ in range(5)]
print(f"중복 없음: {len(tokens) == len(set(tokens))}")
```

### 예제 4: 임시 URL 생성

```python
import secrets

def generate_temp_url(base_url):
    # URL 안전 토큰으로 임시 링크 생성
    token = secrets.token_urlsafe(16)
    return f"{base_url}?token={token}"

# 파일 다운로드 임시 링크
url = generate_temp_url("https://example.com/download")
print(f"임시 URL: {url}")
```

### 예제 5: 보안 PIN 번호 생성

```python
import secrets

def generate_pin(digits=4):
    # N자리 PIN (숫자만)
    return ''''.join(str(secrets.randbelow(10)) for _ in range(digits))

# 4자리 PIN
pin = generate_pin(4)
print(f"PIN: {pin}")

# 6자리 PIN
pin6 = generate_pin(6)
print(f"6자리 PIN: {pin6}")
```

## 주의사항

- ⚠️ **절대 random 모듈을 보안 목적으로 사용하지 마세요** - random은 예측 가능하여 해킹에 취약합니다
- ⚠️ **토큰 길이는 최소 32바이트 이상** - 보안을 위해 충분한 엔트로피가 필요합니다
- 💡 **token_urlsafe()는 URL, 파일명에 안전** - Base64로 인코딩되어 특수문자 문제가 없습니다
- 💡 **비밀번호는 최소 12자 이상 권장** - 대소문자, 숫자, 특수문자를 모두 포함하세요
- 💡 **생성된 토큰은 데이터베이스에 해시하여 저장** - 원본 토큰을 직접 저장하지 마세요

## 정리

secrets 모듈은 비밀번호, 토큰, API 키 등 보안이 중요한 난수 생성에 필수적인 도구입니다. random 모듈과 달리 암호학적으로 안전한 난수를 제공하여 실무 보안 요구사항을 충족합니다.

### 배운 내용
- ✅ random은 일반 용도, secrets는 보안 용도로 사용
- ✅ token_bytes(), token_hex(), token_urlsafe()로 다양한 형식의 토큰 생성
- ✅ choice()와 randbelow()로 안전한 무작위 선택
- ✅ 비밀번호, API 키, 세션 토큰 생성 실무 예제
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('timeit 모듈 - 코드 실행 시간 측정', 'timeit으로 코드 성능을 측정하고 비교하는 방법', '# timeit 모듈 - 코드 실행 시간 측정

코드의 실행 시간을 정확하게 측정하는 것은 성능 최적화의 첫 걸음입니다. timeit 모듈은 작은 코드 조각의 실행 시간을 정밀하게 측정할 수 있는 도구를 제공합니다.

## 학습 목표

- [ ] timeit을 사용하여 코드 실행 시간을 측정할 수 있다
- [ ] 여러 코드의 성능을 비교할 수 있다
- [ ] setup과 stmt의 역할을 이해한다
- [ ] 커맨드라인에서 timeit을 사용할 수 있다

## timeit이란?

timeit 모듈은 작은 코드 조각의 실행 시간을 측정하는 Python 표준 라이브러리입니다. 단순히 시간을 재는 것이 아니라, 여러 번 반복 실행하여 평균값을 제공하므로 더 정확한 성능 측정이 가능합니다.

일반적인 시간 측정(time.time())과 달리, timeit은 가비지 컬렉션을 비활성화하고 여러 번 반복 측정하여 더 신뢰할 수 있는 결과를 제공합니다. 이는 특히 두 가지 알고리즘의 성능을 비교할 때 유용합니다.

## 기본 사용법

```python
import timeit

# 간단한 코드 실행 시간 측정
result = timeit.timeit(''"-".join(str(n) for n in range(100))'',
                       number=10000)
print(f"실행 시간: {result:.4f}초")
# 출력: 실행 시간: 0.1234초
```

## 주요 예제

### 예제 1: 리스트 생성 방법 비교
```python
import timeit

# 방법 1: for 루프
time1 = timeit.timeit(''[i for i in range(1000)]'', number=10000)

# 방법 2: list() 함수
time2 = timeit.timeit(''list(range(1000))'', number=10000)

print(f"리스트 컴프리헨션: {time1:.4f}초")
print(f"list() 함수: {time2:.4f}초")
```

### 예제 2: setup 매개변수 사용
```python
import timeit

# 사전 준비 코드와 측정 코드 분리
setup_code = ''''''
data = list(range(1000))
''''''

stmt_code = ''''''
sum(data)
''''''

result = timeit.timeit(stmt=stmt_code,
                       setup=setup_code,
                       number=10000)
print(f"실행 시간: {result:.4f}초")
```

### 예제 3: repeat() 함수로 여러 번 측정
```python
import timeit

# 5번 반복하여 각각 10000번씩 실행
results = timeit.repeat(''[x**2 for x in range(100)]'',
                        number=10000,
                        repeat=5)

print(f"최소 시간: {min(results):.4f}초")
print(f"평균 시간: {sum(results)/len(results):.4f}초")
print(f"모든 결과: {results}")
```

### 예제 4: 함수 성능 측정
```python
import timeit

def method1():
    result = []
    for i in range(100):
        result.append(i**2)
    return result

def method2():
    return [i**2 for i in range(100)]

# globals()를 사용하여 함수 전달
time1 = timeit.timeit(''method1()'', globals=globals(), number=10000)
time2 = timeit.timeit(''method2()'', globals=globals(), number=10000)

print(f"방법1: {time1:.4f}초")
print(f"방법2: {time2:.4f}초")
print(f"방법2가 {time1/time2:.1f}배 빠릅니다")
```

### 예제 5: 문자열 연결 방법 비교
```python
import timeit

methods = {
    ''+ 연산자'': ''''''
result = ""
for i in range(100):
    result += str(i)
'''''',
    ''join 메서드'': ''''''
result = "".join(str(i) for i in range(100))
'''''',
    ''f-string'': ''''''
result = "".join(f"{i}" for i in range(100))
''''''
}

for name, code in methods.items():
    time = timeit.timeit(code, number=10000)
    print(f"{name}: {time:.4f}초")
```

## 커맨드라인 사용법

터미널에서 직접 timeit을 실행할 수 있습니다:

```python
# 커맨드라인 예제 (실제로는 터미널에서 실행)
# python -m timeit ''"-".join(str(n) for n in range(100))''

# 반복 횟수 지정
# python -m timeit -n 1000 ''[x**2 for x in range(100)]''

# setup 코드 포함
# python -m timeit -s ''import math'' ''math.sqrt(100)''
```

## 주요 매개변수

**timeit() 함수의 주요 매개변수:**

- `stmt`: 측정할 코드 (문자열 또는 callable)
- `setup`: 사전 준비 코드 (한 번만 실행)
- `number`: stmt를 실행할 횟수 (기본값 1,000,000)
- `globals`: 코드 실행 시 사용할 네임스페이스

**repeat() 함수 추가 매개변수:**

- `repeat`: timeit을 반복할 횟수 (기본값 5)

## 주의사항

- ⚠️ 측정 중에는 가비지 컬렉션이 비활성화됩니다
- ⚠️ 너무 짧은 코드는 number를 크게 설정해야 정확합니다
- 💡 성능 비교 시 min() 값을 사용하는 것이 권장됩니다
- 💡 setup 코드는 측정 시간에 포함되지 않습니다
- 💡 복잡한 함수는 globals 매개변수를 활용하세요

## 정리

timeit 모듈은 코드의 실행 시간을 정확하게 측정하여 성능 비교와 최적화를 가능하게 합니다. setup으로 준비 코드를 분리하고, number와 repeat으로 측정 정확도를 조절할 수 있습니다.

### 배운 내용
- ✅ timeit.timeit()으로 코드 실행 시간을 측정했습니다
- ✅ setup과 stmt를 분리하여 정확한 측정을 수행했습니다
- ✅ repeat()로 여러 번 측정하여 신뢰성을 높였습니다
- ✅ 여러 방법의 성능을 비교하여 최적의 코드를 선택했습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('cProfile과 pstats - 프로파일링', '프로파일링으로 성능 병목 지점 찾기', '# cProfile과 pstats - 프로파일링

코드가 느릴 때 어디를 최적화해야 할까요? 프로파일링은 함수별 실행 시간과 호출 횟수를 측정하여 성능 병목 지점을 찾아내는 기법입니다.

## 학습 목표

- [ ] 프로파일링의 개념과 필요성을 이해한다
- [ ] cProfile을 사용하여 코드를 프로파일링할 수 있다
- [ ] pstats를 사용하여 프로파일링 결과를 분석할 수 있다
- [ ] 성능 병목 지점을 찾을 수 있다

## 프로파일링이란?

프로파일링은 프로그램 실행 중 각 함수가 얼마나 자주 호출되고, 얼마나 오래 실행되는지 측정하는 과정입니다. 이를 통해 성능 병목이 발생하는 지점을 정확히 찾아낼 수 있습니다.

Python은 `cProfile` 모듈로 프로파일링을 수행하고, `pstats` 모듈로 결과를 분석합니다. cProfile은 C로 작성되어 오버헤드가 적고, 정확한 측정이 가능합니다.

## 기본 사용법

```python
import cProfile

def slow_function():
    total = 0
    for i in range(1000000):
        total += i
    return total

# 함수 프로파일링
cProfile.run(''slow_function()'')
```

**출력:**
```
         4 function calls in 0.045 seconds
   ncalls  tottime  percall  cumtime  percall filename:lineno(function)
        1    0.045    0.045    0.045    0.045 <stdin>:1(slow_function)
```

## 주요 예제

### 예제 1: 코드 블록 프로파일링
```python
import cProfile
import pstats

# 프로파일 결과를 파일로 저장
cProfile.run(''sum(range(1000000))'', ''profile_stats'')

# 결과 분석
stats = pstats.Stats(''profile_stats'')
stats.strip_dirs()
stats.sort_stats(''cumulative'')
stats.print_stats(10)  # 상위 10개 출력
```

**출력:**
```
   ncalls  tottime  percall  cumtime  percall filename:lineno(function)
        1    0.023    0.023    0.023    0.023 {built-in method builtins.sum}
```

### 예제 2: 함수별 성능 비교
```python
import cProfile

def method1():
    return [i**2 for i in range(10000)]

def method2():
    result = []
    for i in range(10000):
        result.append(i**2)
    return result

# 각 메서드 프로파일링
pr = cProfile.Profile()
pr.enable()
method1()
method2()
pr.disable()
pr.print_stats(sort=''cumtime'')
```

### 예제 3: 실제 성능 병목 찾기
```python
import cProfile
import pstats
from io import StringIO

def process_data():
    # 병목 구간 1: 중복 계산
    data = [i * 2 for i in range(100000)]

    # 병목 구간 2: 비효율적 검색
    result = []
    for i in range(1000):
        if i in data:  # O(n) 검색
            result.append(i)
    return result

# 프로파일링 및 결과 분석
pr = cProfile.Profile()
pr.enable()
process_data()
pr.disable()

# 문자열로 결과 받기
s = StringIO()
ps = pstats.Stats(pr, stream=s)
ps.sort_stats(''cumulative'')
ps.print_stats()
print(s.getvalue())
```

### 예제 4: 정렬 옵션 활용
```python
import cProfile
import pstats

def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)

cProfile.run(''fibonacci(20)'', ''fib_stats'')

stats = pstats.Stats(''fib_stats'')
stats.strip_dirs()

# 다양한 정렬 옵션
print("=== 누적 시간 기준 ===")
stats.sort_stats(''cumulative'').print_stats(5)

print("\n=== 호출 횟수 기준 ===")
stats.sort_stats(''ncalls'').print_stats(5)

print("\n=== 함수 내부 시간 기준 ===")
stats.sort_stats(''tottime'').print_stats(5)
```

### 예제 5: 컨텍스트 매니저로 프로파일링
```python
import cProfile
import pstats

def analyze_performance():
    with cProfile.Profile() as pr:
        # 측정할 코드
        data = [x**2 for x in range(100000)]
        filtered = [x for x in data if x % 2 == 0]
        result = sum(filtered)

    # 결과 출력
    stats = pstats.Stats(pr)
    stats.sort_stats(''cumulative'')
    stats.print_stats()

analyze_performance()
```

## 주의사항

- ⚠️ **오버헤드**: 프로파일링은 실행 속도를 느리게 만듭니다. 프로덕션에서는 사용하지 마세요
- ⚠️ **재귀 함수**: 재귀 함수는 호출 횟수가 폭발적으로 증가할 수 있습니다
- 💡 **정렬 옵션**: `cumulative`(누적 시간), `tottime`(함수 내부 시간), `ncalls`(호출 횟수) 등을 활용하세요
- 💡 **상위 N개**: `print_stats(10)`으로 상위 10개만 보면 병목 지점을 빠르게 찾을 수 있습니다
- 💡 **strip_dirs()**: 긴 경로명을 제거하여 가독성을 높입니다

## 정리

cProfile과 pstats는 코드의 성능 병목을 찾는 필수 도구입니다. 측정 없이는 최적화할 수 없다는 원칙을 기억하세요.

### 배운 내용
- ✅ cProfile.run()으로 코드를 프로파일링하고 함수별 실행 시간을 측정했습니다
- ✅ pstats.Stats로 결과를 정렬하고 분석하여 병목 지점을 찾았습니다
- ✅ cumulative, tottime, ncalls 등 다양한 정렬 기준으로 성능을 분석했습니다
- ✅ 프로파일링 결과를 해석하여 최적화가 필요한 함수를 식별했습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('tracemalloc - 메모리 사용량 추적', '메모리 프로파일링과 누수 탐지를 위한 tracemalloc 활용', '# tracemalloc - 메모리 사용량 추적

메모리 누수와 과도한 메모리 사용은 애플리케이션 성능 문제의 주요 원인입니다. tracemalloc 모듈은 Python에서 메모리 할당을 추적하고 분석할 수 있는 강력한 도구를 제공합니다.

## 학습 목표

- [ ] 메모리 프로파일링의 필요성을 이해한다
- [ ] tracemalloc을 사용하여 메모리 사용량을 추적할 수 있다
- [ ] 스냅샷을 비교하여 메모리 누수를 찾을 수 있다
- [ ] 메모리 사용량이 많은 코드를 식별할 수 있다

## tracemalloc이란?

tracemalloc은 Python 3.4부터 표준 라이브러리에 포함된 메모리 추적 모듈입니다. 이 모듈을 사용하면 어떤 코드에서 메모리를 가장 많이 사용하는지 파악할 수 있습니다.

메모리 프로파일링이 필요한 이유는 명확합니다. 애플리케이션이 점점 느려지거나 메모리가 계속 증가하는 현상은 메모리 누수를 의미할 수 있습니다. tracemalloc을 사용하면 이러한 문제를 정확히 찾아낼 수 있습니다.

## 기본 사용법

```python
import tracemalloc

# 메모리 추적 시작
tracemalloc.start()

# 메모리를 사용하는 코드
data = [i for i in range(1000000)]

# 현재 메모리 사용량 확인
current, peak = tracemalloc.get_traced_memory()
print(f"현재: {current / 1024 / 1024:.2f} MB")
print(f"최대: {peak / 1024 / 1024:.2f} MB")

# 추적 종료
tracemalloc.stop()
```

`start()`로 추적을 시작하고 `get_traced_memory()`로 현재 및 최대 메모리 사용량을 확인할 수 있습니다.

## 주요 예제

### 예제 1: 메모리 사용량 상위 항목 찾기
```python
import tracemalloc

tracemalloc.start()

# 여러 데이터 구조 생성
list_data = [i for i in range(100000)]
dict_data = {i: i**2 for i in range(50000)}
str_data = "a" * 1000000

# 스냅샷 생성
snapshot = tracemalloc.take_snapshot()
top_stats = snapshot.statistics(''lineno'')

# 상위 3개 출력
print("메모리 사용 상위 3개:")
for stat in top_stats[:3]:
    print(stat)

tracemalloc.stop()
```

### 예제 2: 메모리 누수 탐지
```python
import tracemalloc

tracemalloc.start()

# 첫 번째 스냅샷
snapshot1 = tracemalloc.take_snapshot()

# 메모리 누수 시뮬레이션
leaked_list = []
for i in range(10000):
    leaked_list.append([i] * 100)

# 두 번째 스냅샷
snapshot2 = tracemalloc.take_snapshot()

# 차이 비교
top_stats = snapshot2.compare_to(snapshot1, ''lineno'')

print("메모리 증가 상위 3개:")
for stat in top_stats[:3]:
    print(f"{stat.size_diff / 1024:.1f} KB: {stat}")

tracemalloc.stop()
```

### 예제 3: 특정 코드 블록의 메모리 사용량 측정
```python
import tracemalloc

def memory_intensive_function():
    # 큰 리스트 생성
    return [i**2 for i in range(500000)]

tracemalloc.start()
snapshot1 = tracemalloc.take_snapshot()

# 함수 실행
result = memory_intensive_function()

snapshot2 = tracemalloc.take_snapshot()

# 메모리 증가량 계산
diff = snapshot2.compare_to(snapshot1, ''lineno'')
for stat in diff[:1]:
    print(f"메모리 증가: {stat.size_diff / 1024 / 1024:.2f} MB")

tracemalloc.stop()
```

### 예제 4: 파일별 메모리 사용량 분석
```python
import tracemalloc

tracemalloc.start()

# 다양한 작업 수행
data1 = list(range(100000))
data2 = {i: str(i) for i in range(50000)}

snapshot = tracemalloc.take_snapshot()

# 파일별로 그룹화
top_stats = snapshot.statistics(''filename'')

print("파일별 메모리 사용량:")
for stat in top_stats[:5]:
    print(f"{stat.filename}: {stat.size / 1024:.1f} KB")

tracemalloc.stop()
```

### 예제 5: 메모리 추적 필터링
```python
import tracemalloc

tracemalloc.start()

# 여러 작업 수행
my_data = [i for i in range(200000)]
temp_data = "temporary" * 10000

snapshot = tracemalloc.take_snapshot()

# 특정 파일만 필터링
filters = [tracemalloc.Filter(True, "*.py")]
filtered_snapshot = snapshot.filter_traces(filters)

top_stats = filtered_snapshot.statistics(''lineno'')

print("필터링된 메모리 사용량:")
for stat in top_stats[:3]:
    print(stat)

tracemalloc.stop()
```

## 주의사항

- ⚠️ **성능 오버헤드**: tracemalloc은 메모리 할당마다 추적하므로 프로그램 속도가 느려집니다. 프로덕션 환경에서는 주의해서 사용하세요.
- ⚠️ **정확한 측정**: 작은 객체는 Python의 메모리 할당 방식으로 인해 예상보다 많은 메모리를 사용할 수 있습니다.
- 💡 **개발 단계 활용**: 메모리 문제 디버깅 시 개발 환경에서만 tracemalloc을 활성화하는 것이 좋습니다.
- 💡 **스냅샷 비교**: 두 스냅샷을 비교하면 특정 코드 실행 전후의 메모리 변화를 정확히 파악할 수 있습니다.

## 정리

tracemalloc은 Python 애플리케이션의 메모리 사용을 추적하고 분석하는 강력한 도구입니다. 메모리 누수를 찾고, 메모리 사용량이 많은 코드를 식별하며, 애플리케이션의 메모리 효율성을 개선하는 데 필수적인 모듈입니다.

### 배운 내용
- ✅ tracemalloc으로 메모리 할당을 추적하고 분석할 수 있습니다
- ✅ 스냅샷을 생성하고 비교하여 메모리 증가를 측정할 수 있습니다
- ✅ 메모리 사용량 상위 항목을 찾아 최적화 대상을 식별할 수 있습니다
- ✅ 필터를 사용하여 특정 코드의 메모리 사용만 추적할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('gc 모듈 - 가비지 컬렉션', 'Python의 메모리 관리와 가비지 컬렉션 제어 방법', '# gc 모듈 - 가비지 컬렉션

Python은 자동 메모리 관리를 제공하지만, 때로는 메모리 관리를 직접 제어해야 할 때가 있습니다. gc 모듈을 사용하면 가비지 컬렉션을 제어하고 메모리 누수를 디버깅할 수 있습니다.

## 학습 목표

- [ ] Python의 참조 카운팅과 메모리 관리 방식을 이해한다
- [ ] 순환 참조 문제와 가비지 컬렉션의 필요성을 파악한다
- [ ] 가비지 컬렉션의 세대별 관리 개념을 이해한다
- [ ] gc 모듈을 사용하여 메모리 관리를 제어할 수 있다
- [ ] gc 모듈을 활용하여 메모리 누수를 디버깅할 수 있다

## Python의 메모리 관리란?

Python은 두 가지 메모리 관리 메커니즘을 사용합니다. 첫 번째는 **참조 카운팅(Reference Counting)**으로, 각 객체가 몇 번 참조되고 있는지 추적합니다. 참조 카운트가 0이 되면 즉시 메모리에서 해제됩니다.

두 번째는 **순환 가비지 컬렉터(Cyclic Garbage Collector)**로, 참조 카운팅으로 해결할 수 없는 순환 참조 문제를 처리합니다. 예를 들어, 두 객체가 서로를 참조하면 참조 카운트가 0이 되지 않아 메모리 누수가 발생할 수 있습니다.

gc 모듈은 이러한 순환 가비지 컬렉터를 제어하고 모니터링하는 기능을 제공합니다.

## 참조 카운팅 확인

```python
import sys

# 객체의 참조 카운트 확인
x = [1, 2, 3]
print(sys.getrefcount(x))  # 2 (x + getrefcount의 임시 참조)

y = x  # 참조 추가
print(sys.getrefcount(x))  # 3
```

참조 카운트가 증가하면 객체가 여러 변수에서 사용되고 있음을 의미합니다.

## 주요 예제

### 예제 1: 순환 참조 문제
```python
import gc

class Node:
    def __init__(self, value):
        self.value = value
        self.next = None

# 순환 참조 생성
node1 = Node(1)
node2 = Node(2)
node1.next = node2
node2.next = node1  # 순환 참조

# 참조 제거해도 메모리 해제 안됨 (참조 카운팅만으로는 불가능)
del node1, node2

# 가비지 컬렉션 실행하여 순환 참조 객체 회수
collected = gc.collect()
print(f"회수된 객체 수: {collected}")
```

### 예제 2: 가비지 컬렉션 제어
```python
import gc

# 현재 가비지 컬렉션 상태 확인
print(f"GC 활성화 상태: {gc.isenabled()}")

# 가비지 컬렉션 비활성화 (성능 최적화)
gc.disable()
print(f"GC 비활성화: {gc.isenabled()}")

# 수동으로 가비지 컬렉션 실행
collected = gc.collect()
print(f"회수된 객체: {collected}개")

# 가비지 컬렉션 다시 활성화
gc.enable()
```

### 예제 3: 세대별 임계값 확인
```python
import gc

# 가비지 컬렉션의 세대별 임계값 확인
# (generation 0, generation 1, generation 2)
thresholds = gc.get_threshold()
print(f"세대별 임계값: {thresholds}")

# 각 세대별 객체 수 확인
print(f"세대별 객체 수: {gc.get_count()}")

# 임계값 조정 (선택적)
gc.set_threshold(700, 10, 10)
```

### 예제 4: 메모리 디버깅
```python
import gc

# 모든 객체 추적 시작
gc.set_debug(gc.DEBUG_LEAK)

# 특정 타입의 객체 찾기
class MyClass:
    pass

obj1 = MyClass()
obj2 = MyClass()

# MyClass 타입의 모든 객체 찾기
for obj in gc.get_objects():
    if isinstance(obj, MyClass):
        print(f"발견된 객체: {obj}")

# 디버그 모드 해제
gc.set_debug(0)
```

### 예제 5: 객체 참조자 추적
```python
import gc

class Data:
    def __init__(self, value):
        self.value = value

# 객체 생성
data = Data(100)
container = {''data'': data}

# 객체를 참조하는 모든 객체 찾기
referrers = gc.get_referrers(data)
print(f"참조자 수: {len(referrers)}")
for ref in referrers:
    print(f"참조자: {type(ref)}")
```

## 가비지 컬렉션의 세대(Generation)

Python의 가비지 컬렉터는 **세대별 가비지 컬렉션**을 사용합니다. 객체는 생존 시간에 따라 3개의 세대(0, 1, 2)로 분류됩니다.

- **Generation 0**: 새로 생성된 객체들 (가장 자주 검사)
- **Generation 1**: 한 번 이상 가비지 컬렉션에서 살아남은 객체들
- **Generation 2**: 오랫동안 살아남은 객체들 (가장 드물게 검사)

이 방식은 "대부분의 객체는 짧은 시간만 존재한다"는 가정에 기반하여 성능을 최적화합니다.

## 주의사항

- ⚠️ **gc.disable() 사용 주의**: 가비지 컬렉션을 비활성화하면 순환 참조로 인한 메모리 누수가 발생할 수 있습니다.
- ⚠️ **과도한 gc.collect() 호출**: 수동으로 자주 호출하면 오히려 성능이 저하될 수 있습니다.
- 💡 **성능 최적화 팁**: 짧은 시간 동안 많은 객체를 생성하는 작업에서는 일시적으로 GC를 비활성화하고 작업 후 수동 실행하는 것이 효과적입니다.
- 💡 **디버깅 활용**: gc.get_objects()와 gc.get_referrers()는 메모리 누수를 찾을 때 유용합니다.
- ⚠️ **DEBUG 플래그**: 프로덕션 환경에서는 gc.set_debug()를 사용하지 않는 것이 좋습니다 (성능 저하).

## 정리

gc 모듈은 Python의 메모리 관리를 제어하고 최적화하는 강력한 도구입니다. 참조 카운팅으로 해결할 수 없는 순환 참조 문제를 가비지 컬렉션이 해결하며, 세대별 관리를 통해 효율적으로 메모리를 관리합니다. 일반적인 애플리케이션에서는 자동 가비지 컬렉션으로 충분하지만, 성능이 중요한 상황이나 메모리 누수 디버깅이 필요할 때 gc 모듈을 활용할 수 있습니다.

### 배운 내용
- ✅ Python은 참조 카운팅과 순환 가비지 컬렉터를 함께 사용한다
- ✅ 순환 참조는 참조 카운팅만으로 해결할 수 없어 가비지 컬렉션이 필요하다
- ✅ 세대별 가비지 컬렉션은 객체의 생존 시간에 따라 효율적으로 메모리를 관리한다
- ✅ gc.collect(), gc.disable(), gc.enable()로 가비지 컬렉션을 제어할 수 있다
- ✅ gc.get_objects()와 gc.get_referrers()로 메모리 누수를 디버깅할 수 있다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('weakref 모듈 - 약한 참조', '약한 참조의 개념과 메모리 관리 최적화 기법', '# weakref 모듈 - 약한 참조

약한 참조는 객체를 가비지 컬렉션으로부터 보호하지 않는 참조입니다. 캐시나 순환 참조 문제를 해결할 때 유용합니다.

## 학습 목표

- [ ] 약한 참조(weak reference)의 개념을 이해한다
- [ ] ref()를 사용하여 약한 참조를 생성할 수 있다
- [ ] WeakValueDictionary를 활용할 수 있다
- [ ] 약한 참조의 활용 사례를 이해한다

## 약한 참조란?

일반적인 파이썬 참조는 **강한 참조(strong reference)**입니다. 객체를 참조하면 참조 카운트가 증가하고, 객체가 메모리에서 삭제되지 않습니다.

**약한 참조(weak reference)**는 객체를 참조하지만 참조 카운트를 증가시키지 않습니다. 다른 강한 참조가 없으면 객체는 가비지 컬렉션 대상이 됩니다.

약한 참조는 캐시 구현, 순환 참조 방지, 대용량 객체 관리에 필수적입니다.

## 기본 사용법

```python
import weakref

class Data:
    def __init__(self, value):
        self.value = value

obj = Data(100)
weak = weakref.ref(obj)  # 약한 참조 생성

print(weak())  # <__main__.Data object at ...>
print(weak().value)  # 100
```

약한 참조는 callable 객체로, 호출하면 원본 객체를 반환합니다.

## 주요 예제

### 예제 1: 객체 소멸 확인
```python
import weakref

class Resource:
    def __del__(self):
        print("Resource deleted")

obj = Resource()
weak = weakref.ref(obj)

print(weak() is not None)  # True
del obj  # Resource deleted
print(weak() is None)  # True (객체 소멸됨)
```

### 예제 2: WeakValueDictionary로 캐시 구현
```python
import weakref

class ExpensiveObject:
    def __init__(self, id):
        self.id = id

cache = weakref.WeakValueDictionary()
cache[''obj1''] = ExpensiveObject(1)

print(''obj1'' in cache)  # True
obj = cache[''obj1'']
print(obj.id)  # 1
```

### 예제 3: 콜백 함수 활용
```python
import weakref

def callback(ref):
    print(f"Object is being deleted")

class Data:
    pass

obj = Data()
weak = weakref.ref(obj, callback)

del obj  # Object is being deleted
```

### 예제 4: WeakKeyDictionary
```python
import weakref

class User:
    def __init__(self, name):
        self.name = name

metadata = weakref.WeakKeyDictionary()
user = User("Alice")
metadata[user] = {"login_count": 5}

print(metadata[user])  # {''login_count'': 5}
del user  # 키가 삭제되면 항목도 자동 제거
```

### 예제 5: proxy() 사용
```python
import weakref

class Config:
    def __init__(self):
        self.value = 42

obj = Config()
proxy = weakref.proxy(obj)

print(proxy.value)  # 42 (직접 접근 가능)
# del obj -> ReferenceError 발생
```

## 주의사항

- ⚠️ 모든 객체가 약한 참조를 지원하지 않습니다 (int, str, tuple 등은 불가)
- ⚠️ 약한 참조된 객체가 삭제되면 None을 반환하므로 항상 확인 필요
- 💡 WeakValueDictionary는 값이 자동으로 제거되는 캐시 구현에 이상적
- 💡 순환 참조 문제 해결에 유용하지만, 대부분 컨텍스트 관리자로 해결 가능
- 💡 proxy()는 ref()와 달리 호출 없이 직접 사용 가능하지만 객체 소멸 시 예외 발생

## 정리

weakref 모듈은 메모리 관리를 최적화하는 강력한 도구입니다. 특히 캐시, 순환 참조 방지, 대용량 객체 관리에서 필수적입니다.

### 배운 내용
- ✅ 약한 참조는 참조 카운트를 증가시키지 않아 메모리 효율적
- ✅ ref()로 약한 참조, WeakValueDictionary로 자동 정리 캐시 구현
- ✅ proxy()는 투명한 접근을 제공하지만 객체 소멸 시 예외 발생
- ✅ 순환 참조 방지와 캐시 구현의 핵심 기법
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('argparse 모듈 - 명령줄 인터페이스', 'CLI 프로그램 개발을 위한 argparse 모듈', '# argparse 모듈 - 명령줄 인터페이스

argparse는 사용자 친화적인 명령줄 인터페이스(CLI)를 쉽게 만들 수 있는 Python 표준 라이브러리입니다. 자동 도움말 생성, 타입 검증, 오류 처리 기능을 제공합니다.

## 학습 목표

- [ ] ArgumentParser를 사용하여 CLI를 구축할 수 있다
- [ ] 위치 인자와 옵션 인자를 정의할 수 있다
- [ ] 타입, 기본값, help 메시지를 설정할 수 있다
- [ ] 서브커맨드를 구현할 수 있다

## argparse가 필요한 이유

명령줄 프로그램은 사용자로부터 인자를 받아 처리해야 합니다. sys.argv로 직접 처리하면 복잡하고 오류가 발생하기 쉽습니다. argparse는 인자 파싱, 타입 변환, 유효성 검사, 도움말 생성을 자동화하여 전문적인 CLI를 빠르게 구축할 수 있게 합니다.

## 기본 사용법

```python
import argparse

# ArgumentParser 생성
parser = argparse.ArgumentParser(description=''파일 처리 도구'')

# 위치 인자 추가
parser.add_argument(''filename'', help=''처리할 파일 이름'')

# 인자 파싱
args = parser.parse_args()
print(f''파일: {args.filename}'')
```

**실행 예시:**
```
$ python script.py data.txt
파일: data.txt
```

## 주요 예제

### 예제 1: 위치 인자와 옵션 인자

```python
import argparse

parser = argparse.ArgumentParser()

# 위치 인자 (필수)
parser.add_argument(''input'', help=''입력 파일'')

# 옵션 인자 (선택적)
parser.add_argument(''-o'', ''--output'', help=''출력 파일'')
parser.add_argument(''-v'', ''--verbose'', action=''store_true'')

args = parser.parse_args()
print(f''입력: {args.input}, 출력: {args.output}'')
```

**실행:**
```
$ python script.py input.txt -o output.txt -v
입력: input.txt, 출력: output.txt
```

### 예제 2: 타입과 기본값 설정

```python
import argparse

parser = argparse.ArgumentParser()

parser.add_argument(''count'', type=int, help=''반복 횟수'')
parser.add_argument(''--limit'', type=int, default=100)
parser.add_argument(''--format'', choices=[''json'', ''xml'', ''csv''])

args = parser.parse_args()
print(f''횟수: {args.count}, 제한: {args.limit}'')
```

**type**으로 자동 타입 변환, **default**로 기본값 설정, **choices**로 허용 값을 제한합니다.

### 예제 3: 필수 옵션과 상호 배타적 그룹

```python
import argparse

parser = argparse.ArgumentParser()

# 필수 옵션
parser.add_argument(''--config'', required=True, help=''설정 파일'')

# 상호 배타적 그룹
group = parser.add_mutually_exclusive_group()
group.add_argument(''--start'', action=''store_true'')
group.add_argument(''--stop'', action=''store_true'')

args = parser.parse_args()
```

**required=True**로 필수 옵션을 만들고, 상호 배타적 그룹으로 동시 사용 불가능한 옵션을 관리합니다.

### 예제 4: 서브커맨드 구현

```python
import argparse

parser = argparse.ArgumentParser()
subparsers = parser.add_subparsers(dest=''command'')

# ''add'' 서브커맨드
add_parser = subparsers.add_parser(''add'', help=''항목 추가'')
add_parser.add_argument(''name'', help=''항목 이름'')

# ''remove'' 서브커맨드
remove_parser = subparsers.add_parser(''remove'')
remove_parser.add_argument(''id'', type=int)

args = parser.parse_args()
print(f''명령: {args.command}'')
```

**실행:**
```
$ python script.py add "새 항목"
명령: add
```

### 예제 5: 실전 예제 - 파일 변환 도구

```python
import argparse

def main():
    parser = argparse.ArgumentParser(
        description=''파일 형식 변환 도구''
    )

    parser.add_argument(''input'', help=''입력 파일'')
    parser.add_argument(''-o'', ''--output'', default=''output.txt'')
    parser.add_argument(''-f'', ''--format'',
                       choices=[''upper'', ''lower''],
                       default=''upper'')

    args = parser.parse_args()

    with open(args.input) as f:
        content = f.read()

    if args.format == ''upper'':
        result = content.upper()
    else:
        result = content.lower()

    with open(args.output, ''w'') as f:
        f.write(result)

    print(f''{args.input} → {args.output} 변환 완료'')

if __name__ == ''__main__'':
    main()
```

## add_argument() 주요 매개변수

| 매개변수 | 설명 | 예시 |
|---------|------|------|
| `name` | 인자 이름 | `''filename''` 또는 `''-o'', ''--output''` |
| `type` | 타입 변환 함수 | `type=int`, `type=float` |
| `default` | 기본값 | `default=10` |
| `help` | 도움말 메시지 | `help=''파일 이름''` |
| `required` | 필수 여부 | `required=True` |
| `choices` | 허용 값 목록 | `choices=[''a'', ''b'', ''c'']` |
| `action` | 특수 동작 | `action=''store_true''` |
| `nargs` | 인자 개수 | `nargs=''+''`, `nargs=2` |

## 주의사항

- ⚠️ **위치 인자는 순서가 중요**: 정의한 순서대로 입력해야 합니다
- ⚠️ **옵션 인자는 `-`로 시작**: 단일 문자는 `-`, 긴 이름은 `--`를 사용합니다
- 💡 **자동 도움말**: `-h` 또는 `--help`로 자동 생성된 도움말을 볼 수 있습니다
- 💡 **타입 검증**: type 매개변수를 사용하면 자동으로 타입을 검증하고 변환합니다
- 💡 **서브커맨드 활용**: git처럼 복잡한 CLI는 서브커맨드로 구조화하세요

## 정리

argparse는 전문적인 CLI 프로그램을 쉽게 만들 수 있게 해주는 강력한 도구입니다. ArgumentParser로 파서를 만들고, add_argument()로 인자를 정의하며, parse_args()로 파싱합니다. 위치 인자, 옵션 인자, 타입 검증, 서브커맨드 등을 활용하여 사용자 친화적인 명령줄 인터페이스를 구축할 수 있습니다.

### 배운 내용
- ✅ ArgumentParser로 CLI 파서를 생성하고 인자를 파싱했습니다
- ✅ 위치 인자와 옵션 인자의 차이를 이해하고 정의했습니다
- ✅ type, default, choices 등으로 인자의 동작을 제어했습니다
- ✅ 서브파서를 사용하여 git 스타일의 서브커맨드를 구현했습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('logging 모듈 - 로깅 시스템', 'Python 로깅 시스템으로 효과적인 디버깅과 모니터링 구현', '# logging 모듈 - 로깅 시스템

프로그램이 복잡해질수록 디버깅과 모니터링이 중요해집니다. logging 모듈은 print()보다 강력하고 유연한 로그 기록 시스템을 제공합니다.

## 학습 목표

- [ ] 로깅의 필요성과 print()와의 차이를 이해하고 설명할 수 있다
- [ ] 5가지 로그 레벨(DEBUG, INFO, WARNING, ERROR, CRITICAL)을 적절히 사용할 수 있다
- [ ] Logger, Handler, Formatter를 조합하여 로깅 시스템을 구성할 수 있다
- [ ] 파일과 콘솔에 동시에 로그를 출력하는 설정을 구현할 수 있다
- [ ] 로깅 모범 사례를 프로젝트에 적용할 수 있다

## logging이 필요한 이유

**print() vs logging의 차이점**

print()는 간단하지만 한계가 있습니다. logging은 로그 레벨 설정, 파일 저장, 형식 지정, 출력 위치 제어 등 프로덕션 환경에 필요한 모든 기능을 제공합니다.

```python
# print()의 문제점
print("사용자 로그인")  # 개발 완료 후 삭제해야 함
print("에러 발생!")     # 로그 파일에 저장 불가

# logging의 장점
import logging
logging.info("사용자 로그인")      # 레벨 설정으로 출력 제어
logging.error("에러 발생!")        # 파일 저장 가능
```

## 로그 레벨

logging은 5가지 레벨로 메시지 중요도를 구분합니다.

```python
import logging

# 기본 설정 (WARNING 이상만 출력)
logging.debug("디버그 정보")         # 출력 안됨
logging.info("일반 정보")            # 출력 안됨
logging.warning("경고 메시지")       # 출력됨
logging.error("에러 발생")           # 출력됨
logging.critical("치명적 오류")      # 출력됨
```

**레벨별 사용 시점**
- DEBUG: 상세한 진단 정보
- INFO: 일반적인 작업 확인
- WARNING: 예상치 못한 상황, 프로그램은 정상 작동
- ERROR: 심각한 문제, 기능 일부 실행 불가
- CRITICAL: 프로그램 전체 실행 불가

## 기본 설정 - basicConfig()

```python
import logging

# 로그 레벨 설정
logging.basicConfig(level=logging.DEBUG)

logging.debug("이제 출력됩니다")
logging.info("모든 레벨 출력")
```

## 주요 예제

### 예제 1: 파일에 로그 저장하기
```python
import logging

# 파일에 로그 저장 설정
logging.basicConfig(
    filename=''app.log'',
    level=logging.INFO,
    format=''%(asctime)s - %(levelname)s - %(message)s''
)

logging.info("프로그램 시작")
logging.warning("메모리 사용량 80% 초과")
logging.error("데이터베이스 연결 실패")
```

**출력 (app.log 파일)**
```
2025-01-15 10:30:45,123 - INFO - 프로그램 시작
2025-01-15 10:30:46,456 - WARNING - 메모리 사용량 80% 초과
2025-01-15 10:30:47,789 - ERROR - 데이터베이스 연접 실패
```

### 예제 2: 로거 객체 사용하기
```python
import logging

# 로거 생성
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)

# 핸들러 생성 (콘솔 출력)
handler = logging.StreamHandler()
handler.setLevel(logging.DEBUG)

# 포매터 생성
formatter = logging.Formatter(
    ''%(name)s - %(levelname)s - %(message)s''
)
handler.setFormatter(formatter)

logger.addHandler(handler)
logger.info("로거 설정 완료")
```

### 예제 3: 파일과 콘솔에 동시 출력
```python
import logging

logger = logging.getLogger(''my_app'')
logger.setLevel(logging.DEBUG)

# 파일 핸들러
file_handler = logging.FileHandler(''debug.log'')
file_handler.setLevel(logging.DEBUG)

# 콘솔 핸들러 (WARNING 이상만)
console_handler = logging.StreamHandler()
console_handler.setLevel(logging.WARNING)

# 포매터 설정
formatter = logging.Formatter(
    ''%(asctime)s - %(name)s - %(levelname)s - %(message)s''
)
file_handler.setFormatter(formatter)
console_handler.setFormatter(formatter)

logger.addHandler(file_handler)
logger.addHandler(console_handler)

logger.debug("파일에만 기록")
logger.warning("파일과 콘솔 모두 출력")
```

### 예제 4: 실전 예제 - 웹 크롤러 로깅
```python
import logging

logging.basicConfig(
    level=logging.INFO,
    format=''%(asctime)s - %(levelname)s - %(message)s'',
    handlers=[
        logging.FileHandler(''crawler.log''),
        logging.StreamHandler()
    ]
)

def crawl_website(url):
    logging.info(f"크롤링 시작: {url}")
    try:
        # 크롤링 로직
        logging.debug(f"페이지 파싱 중: {url}")
        # ...
        logging.info(f"크롤링 완료: {url}")
    except Exception as e:
        logging.error(f"크롤링 실패: {url}, 오류: {e}")

crawl_website("https://example.com")
```

### 예제 5: 로거 계층 구조
```python
import logging

# 부모 로거
parent_logger = logging.getLogger(''myapp'')
parent_logger.setLevel(logging.DEBUG)

# 자식 로거 (이름에 점 사용)
db_logger = logging.getLogger(''myapp.database'')
api_logger = logging.getLogger(''myapp.api'')

# 핸들러는 부모에만 추가
handler = logging.StreamHandler()
formatter = logging.Formatter(''%(name)s - %(message)s'')
handler.setFormatter(formatter)
parent_logger.addHandler(handler)

# 자식 로거가 부모 핸들러 사용
db_logger.info("DB 연결")     # myapp.database - DB 연결
api_logger.info("API 호출")   # myapp.api - API 호출
```

## 주요 컴포넌트

**3가지 핵심 구성 요소:**

1. **Logger**: 로그 메시지를 생성하는 객체
2. **Handler**: 로그를 출력할 위치 결정 (파일, 콘솔, 네트워크 등)
3. **Formatter**: 로그 메시지 형식 지정

**포맷 문자열 옵션:**
- `%(asctime)s`: 시간
- `%(name)s`: 로거 이름
- `%(levelname)s`: 로그 레벨
- `%(message)s`: 로그 메시지
- `%(filename)s`: 파일명
- `%(lineno)d`: 라인 번호

## 주의사항

- ⚠️ **basicConfig()는 한 번만**: 프로그램 시작 시 한 번만 호출해야 합니다
- ⚠️ **로거 재사용**: `getLogger(name)`은 같은 이름이면 같은 객체를 반환합니다
- ⚠️ **핸들러 중복 추가 방지**: 같은 핸들러를 여러 번 추가하지 마세요
- 💡 **`__name__` 사용 권장**: `getLogger(__name__)`으로 모듈별 로거 생성
- 💡 **프로덕션 레벨**: 운영 환경에서는 INFO 또는 WARNING 레벨 사용
- 💡 **로그 로테이션**: `RotatingFileHandler`로 파일 크기 관리 가능

## 정리

logging 모듈은 print()를 대체하는 강력한 도구입니다. 로그 레벨로 메시지 중요도를 구분하고, Handler로 출력 위치를 제어하며, Formatter로 형식을 지정합니다. 프로덕션 환경에서는 필수적인 도구입니다.

### 배운 내용
- ✅ 5가지 로그 레벨(DEBUG, INFO, WARNING, ERROR, CRITICAL)과 사용 시점
- ✅ basicConfig()로 간단한 로깅 설정
- ✅ Logger, Handler, Formatter를 조합한 고급 설정
- ✅ 파일과 콘솔에 동시 로그 출력 구현
- ✅ 로거 계층 구조와 모범 사례
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('unittest 모듈 - 단위 테스트', 'Python의 unittest 모듈로 단위 테스트 작성하기', '# unittest 모듈 - 단위 테스트

unittest는 Python 표준 라이브러리에 포함된 단위 테스트 프레임워크입니다. 코드의 정확성을 자동으로 검증하여 버그를 조기에 발견하고 리팩토링을 안전하게 수행할 수 있습니다.

## 학습 목표

- [ ] 단위 테스트의 개념과 필요성을 이해한다
- [ ] TestCase 클래스를 상속하여 테스트를 작성할 수 있다
- [ ] assert 메서드를 사용하여 검증을 수행할 수 있다
- [ ] setUp()과 tearDown()을 활용할 수 있다
- [ ] 테스트를 실행하고 결과를 해석할 수 있다

## 단위 테스트란?

단위 테스트(Unit Test)는 소프트웨어의 개별 구성 요소(함수, 메서드)가 올바르게 작동하는지 검증하는 자동화된 테스트입니다. 코드 변경 시 기존 기능이 정상 작동하는지 즉시 확인할 수 있어 개발 속도와 품질을 동시에 향상시킵니다.

unittest 모듈은 테스트 케이스 작성, 테스트 실행, 결과 보고 등의 기능을 제공합니다. TestCase 클래스를 상속받아 테스트 메서드를 작성하고, assert 메서드로 예상 결과를 검증합니다.

## 기본 사용법

```python
import unittest

def add(a, b):
    return a + b

class TestAddFunction(unittest.TestCase):
    def test_add_positive(self):
        self.assertEqual(add(2, 3), 5)

    def test_add_negative(self):
        self.assertEqual(add(-1, -1), -2)

if __name__ == ''__main__'':
    unittest.main()
```

테스트 메서드는 `test_`로 시작해야 하며, `assertEqual()`로 실제 값과 예상 값을 비교합니다.

## 주요 예제

### 예제 1: 계산기 클래스 테스트

```python
import unittest

class Calculator:
    def divide(self, a, b):
        if b == 0:
            raise ValueError("0으로 나눌 수 없습니다")
        return a / b

class TestCalculator(unittest.TestCase):
    def test_divide_normal(self):
        calc = Calculator()
        self.assertEqual(calc.divide(10, 2), 5.0)

    def test_divide_by_zero(self):
        calc = Calculator()
        with self.assertRaises(ValueError):
            calc.divide(10, 0)
```

`assertRaises()`는 특정 예외가 발생하는지 검증합니다.

### 예제 2: setUp과 tearDown 활용

```python
import unittest

class TestDatabase(unittest.TestCase):
    def setUp(self):
        # 각 테스트 전에 실행
        self.data = []
        print("테스트 준비")

    def tearDown(self):
        # 각 테스트 후에 실행
        self.data = None
        print("테스트 정리")

    def test_append(self):
        self.data.append(1)
        self.assertEqual(len(self.data), 1)

    def test_clear(self):
        self.data.extend([1, 2, 3])
        self.data.clear()
        self.assertEqual(len(self.data), 0)
```

`setUp()`은 테스트 전 초기화, `tearDown()`은 테스트 후 정리 작업에 사용됩니다.

### 예제 3: 다양한 assert 메서드

```python
import unittest

class TestAssertMethods(unittest.TestCase):
    def test_equality(self):
        self.assertEqual(1 + 1, 2)
        self.assertNotEqual(1, 2)

    def test_boolean(self):
        self.assertTrue(True)
        self.assertFalse(False)

    def test_membership(self):
        self.assertIn(''a'', ''abc'')
        self.assertNotIn(''d'', ''abc'')

    def test_none(self):
        self.assertIsNone(None)
        self.assertIsNotNone(5)
```

### 예제 4: 테스트 실행 방법

```python
# test_example.py 파일로 저장 후 실행
import unittest

class TestStringMethods(unittest.TestCase):
    def test_upper(self):
        self.assertEqual(''hello''.upper(), ''HELLO'')

    def test_isupper(self):
        self.assertTrue(''HELLO''.isupper())
        self.assertFalse(''Hello''.isupper())

# 터미널에서 실행:
# python -m unittest test_example
# python -m unittest test_example.TestStringMethods
# python -m unittest test_example.TestStringMethods.test_upper
```

## 주의사항

- **테스트 독립성**: 각 테스트는 독립적으로 실행되어야 하며 다른 테스트에 의존하면 안 됩니다
- **명확한 이름**: 테스트 메서드 이름은 무엇을 테스트하는지 명확하게 표현해야 합니다
- **하나의 검증**: 각 테스트는 하나의 기능만 검증하는 것이 좋습니다
- **테스트 순서**: 테스트 실행 순서에 의존하지 마세요 (순서는 보장되지 않음)

## 정리

unittest 모듈은 Python 코드의 품질을 보장하는 강력한 도구입니다. TestCase를 상속받아 테스트를 작성하고, assert 메서드로 검증하며, setUp/tearDown으로 테스트 환경을 관리할 수 있습니다.

### 배운 내용
- unittest.TestCase를 상속받아 테스트 클래스 작성
- assertEqual, assertTrue, assertRaises 등의 assert 메서드 활용
- setUp()과 tearDown()으로 테스트 환경 설정
- python -m unittest 명령으로 테스트 실행
- 독립적이고 명확한 테스트 작성의 중요성
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('doctest 모듈 - 문서화 테스트', '독스트링에 테스트를 작성하는 doctest 활용법', '# doctest 모듈 - 문서화 테스트

doctest는 독스트링에 작성된 대화형 Python 예제를 찾아 실행하고 검증하는 모듈입니다. 문서화와 테스트를 동시에 수행할 수 있는 효율적인 도구입니다.

## 학습 목표

- [ ] doctest의 개념과 장점을 이해한다
- [ ] 독스트링에 테스트를 작성할 수 있다
- [ ] testmod()를 사용하여 테스트를 실행할 수 있다
- [ ] doctest와 unittest의 차이를 이해한다

## doctest란?

doctest는 독스트링(docstring) 안에 Python 대화형 세션처럼 보이는 텍스트를 찾아 실행하고, 실제 결과가 예상 결과와 일치하는지 확인합니다.

이는 두 가지 목적을 동시에 달성합니다:
1. **문서화**: 사용자에게 함수의 사용 예제를 제공
2. **테스트**: 코드가 예제대로 동작하는지 자동 검증

doctest는 특히 간단한 함수나 API 사용법을 문서화하면서 동시에 회귀 테스트를 수행하고 싶을 때 유용합니다.

## 기본 사용법

```python
def add(a, b):
    """
    두 수를 더합니다.

    >>> add(2, 3)
    5
    >>> add(-1, 1)
    0
    """
    return a + b

if __name__ == "__main__":
    import doctest
    doctest.testmod()
```

독스트링에 `>>>` (프롬프트)로 시작하는 라인은 실행할 코드, 그 다음 라인은 예상 결과를 나타냅니다.

## 주요 예제

### 예제 1: 기본 doctest 작성
```python
def factorial(n):
    """
    정수의 팩토리얼을 계산합니다.

    >>> factorial(5)
    120
    >>> factorial(0)
    1
    >>> factorial(1)
    1
    """
    if n == 0:
        return 1
    return n * factorial(n - 1)

if __name__ == "__main__":
    import doctest
    doctest.testmod(verbose=True)
```

`verbose=True` 옵션은 모든 테스트 결과를 출력합니다. 생략하면 실패한 테스트만 표시됩니다.

### 예제 2: 예외 처리 테스트
```python
def divide(a, b):
    """
    나눗셈을 수행합니다.

    >>> divide(10, 2)
    5.0
    >>> divide(10, 0)
    Traceback (most recent call last):
        ...
    ZeroDivisionError: division by zero
    """
    return a / b
```

예외를 테스트할 때는 `Traceback`으로 시작하고, 중간 부분은 `...`으로 생략하며, 마지막 예외 메시지를 정확히 작성합니다.

### 예제 3: 리스트와 딕셔너리 테스트
```python
def get_user_info(name, age):
    """
    사용자 정보를 딕셔너리로 반환합니다.

    >>> get_user_info(''Alice'', 30)
    {''name'': ''Alice'', ''age'': 30}
    >>> sorted(get_user_info(''Bob'', 25).items())
    [(''age'', 25), (''name'', ''Bob'')]
    """
    return {''name'': name, ''age'': age}
```

딕셔너리는 순서가 보장되지 않을 수 있으므로, `sorted()`를 사용하거나 Python 3.7+ 버전을 사용합니다.

### 예제 4: 파일로 분리된 doctest
```python
# calculator.py
def multiply(a, b):
    """두 수를 곱합니다."""
    return a * b

# test_calculator.txt 파일 내용:
# >>> from calculator import multiply
# >>> multiply(3, 4)
# 12

if __name__ == "__main__":
    import doctest
    doctest.testfile("test_calculator.txt")
```

별도의 텍스트 파일로 doctest를 작성하면 긴 테스트 케이스를 독스트링에서 분리할 수 있습니다.

### 예제 5: doctest와 unittest 차이
```python
# doctest - 간단하고 직관적
def is_even(n):
    """
    >>> is_even(4)
    True
    >>> is_even(3)
    False
    """
    return n % 2 == 0

# unittest - 복잡한 테스트에 적합
import unittest

class TestIsEven(unittest.TestCase):
    def test_even_number(self):
        self.assertTrue(is_even(4))

    def test_odd_number(self):
        self.assertFalse(is_even(3))
```

## 주의사항

- ⚠️ **출력 형식**: doctest는 출력을 문자열로 정확히 비교합니다. 공백, 줄바꿈도 일치해야 합니다
- ⚠️ **딕셔너리 순서**: Python 3.7+ 이전 버전에서는 딕셔너리 순서가 보장되지 않습니다
- ⚠️ **복잡한 테스트**: 복잡한 설정이나 teardown이 필요한 경우 unittest가 더 적합합니다
- 💡 **문서화 우선**: doctest는 문서화가 주목적이고 테스트는 부가 기능입니다
- 💡 **간단한 함수**: 순수 함수나 간단한 API 테스트에 가장 효과적입니다
- 💡 **ELLIPSIS 옵션**: 가변적인 출력은 `# doctest: +ELLIPSIS`와 `...`을 사용합니다

## 정리

doctest는 독스트링에 대화형 예제를 작성하여 문서화와 테스트를 동시에 수행하는 도구입니다. 간단한 함수의 사용 예제를 제공하면서 자동으로 회귀 테스트를 수행할 수 있어 효율적입니다. 복잡한 테스트에는 unittest를, 간단하고 명확한 예제에는 doctest를 사용하세요.

### 배운 내용
- ✅ doctest는 독스트링의 대화형 예제를 실행하고 검증합니다
- ✅ `testmod()`로 모듈의 모든 doctest를 실행할 수 있습니다
- ✅ 예외는 Traceback 형식으로 테스트할 수 있습니다
- ✅ doctest는 문서화 우선, unittest는 테스트 우선 도구입니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('enum 모듈 - 열거형', 'Enum 클래스로 상수 집합을 정의하고 활용하는 방법', '# enum 모듈 - 열거형

열거형(Enumeration)은 관련된 상수들을 하나의 타입으로 그룹화하여 코드의 가독성과 안정성을 높이는 기능입니다. Python의 enum 모듈을 사용하면 타입 안전성을 보장하는 상수 집합을 정의할 수 있습니다.

## 학습 목표

- [ ] 열거형(Enum)의 개념과 필요성을 이해한다
- [ ] Enum 클래스를 정의하고 멤버에 접근할 수 있다
- [ ] 열거형 멤버를 비교하고 반복할 수 있다
- [ ] IntEnum, Flag, auto()를 실무에 활용할 수 있다

## 열거형이란?

열거형은 관련된 상수들을 그룹화하여 명명된 값으로 관리하는 데이터 타입입니다. 일반 상수와 달리 열거형은 타입 체크가 가능하고, 잘못된 값 할당을 방지할 수 있습니다.

예를 들어 요일, 상태 코드, 색상 등 고정된 값들의 집합을 표현할 때 유용합니다. 문자열이나 숫자 대신 열거형을 사용하면 오타나 잘못된 값을 사전에 방지할 수 있습니다.

## 기본 사용법

```python
from enum import Enum

class Color(Enum):
    RED = 1
    GREEN = 2
    BLUE = 3

# 멤버 접근
print(Color.RED)        # Color.RED
print(Color.RED.name)   # RED
print(Color.RED.value)  # 1
```

Enum 클래스를 상속받아 정의하며, 각 멤버는 이름과 값을 가집니다. 멤버는 클래스 속성처럼 접근할 수 있습니다.

## 주요 예제

### 예제 1: 열거형 비교와 반복
```python
from enum import Enum

class Status(Enum):
    PENDING = "대기"
    APPROVED = "승인"
    REJECTED = "거부"

# 멤버 비교 (동일성)
status = Status.PENDING
print(status is Status.PENDING)  # True

# 반복
for s in Status:
    print(f"{s.name}: {s.value}")
```

### 예제 2: auto()로 자동 값 할당
```python
from enum import Enum, auto

class Priority(Enum):
    LOW = auto()     # 1
    MEDIUM = auto()  # 2
    HIGH = auto()    # 3

print(Priority.LOW.value)     # 1
print(Priority.MEDIUM.value)  # 2
```

### 예제 3: IntEnum - 정수형 열거형
```python
from enum import IntEnum

class HttpStatus(IntEnum):
    OK = 200
    NOT_FOUND = 404
    SERVER_ERROR = 500

# 정수와 직접 비교 가능
status = HttpStatus.OK
print(status == 200)  # True
print(status > 100)   # True
```

### 예제 4: Flag - 비트 플래그 조합
```python
from enum import Flag, auto

class Permission(Flag):
    READ = auto()    # 1
    WRITE = auto()   # 2
    EXECUTE = auto() # 4

# 비트 연산으로 조합
perm = Permission.READ | Permission.WRITE
print(Permission.READ in perm)   # True
print(Permission.EXECUTE in perm) # False
```

### 예제 5: 실무 활용 - 주문 상태 관리
```python
from enum import Enum

class OrderStatus(Enum):
    CREATED = "주문생성"
    PAID = "결제완료"
    SHIPPED = "배송중"
    DELIVERED = "배송완료"
    CANCELLED = "취소"

    def can_cancel(self):
        return self in (OrderStatus.CREATED, OrderStatus.PAID)

order_status = OrderStatus.PAID
print(order_status.can_cancel())  # True
```

## 주의사항

- ⚠️ **열거형 멤버는 불변**: 한번 정의된 멤버는 값을 변경할 수 없습니다
- ⚠️ **중복 값 주의**: 기본적으로 같은 값을 가진 멤버는 별칭(alias)이 됩니다
- 💡 **타입 체크 활용**: isinstance()를 사용해 열거형 타입을 검증할 수 있습니다
- 💡 **@unique 데코레이터**: 중복 값을 허용하지 않으려면 `@unique`를 사용하세요

## 정리

enum 모듈은 관련된 상수들을 타입 안전하게 관리할 수 있는 강력한 도구입니다. 일반 상수 대신 열거형을 사용하면 코드의 명확성과 유지보수성이 크게 향상됩니다.

### 배운 내용
- ✅ Enum 클래스로 관련 상수를 그룹화하여 타입 안전성 확보
- ✅ name, value 속성으로 멤버 정보에 접근
- ✅ auto()로 자동 값 할당, IntEnum으로 정수 비교 지원
- ✅ Flag로 비트 연산 기반의 조합 가능한 플래그 정의
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 20, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('dataclasses 모듈 - 데이터 클래스', '데이터 클래스로 간결한 클래스 작성하기', '# dataclasses 모듈 - 데이터 클래스

Python 3.7부터 추가된 dataclasses 모듈은 데이터를 저장하는 클래스를 간단하게 작성할 수 있게 해줍니다.

## 학습 목표

- [ ] 데이터 클래스의 개념과 필요성을 이해한다
- [ ] @dataclass 데코레이터를 사용할 수 있다
- [ ] 자동으로 생성되는 메서드를 이해한다
- [ ] field()를 사용하여 필드를 커스터마이징할 수 있다
- [ ] 불변 데이터 클래스를 생성할 수 있다

## 데이터 클래스란?

데이터 클래스는 주로 데이터를 저장하는 목적으로 사용되는 클래스입니다. 일반적으로 클래스를 만들 때 `__init__`, `__repr__`, `__eq__` 등의 메서드를 반복적으로 작성해야 하는데, dataclasses 모듈은 이러한 메서드를 자동으로 생성해줍니다.

이를 통해 코드의 양을 줄이고, 가독성을 높이며, 실수를 방지할 수 있습니다. 특히 데이터 모델, 설정 객체, API 응답 등을 표현할 때 유용합니다.

## 기본 사용법

```python
from dataclasses import dataclass

@dataclass
class Person:
    name: str
    age: int
    city: str = "서울"

# 객체 생성
p = Person("홍길동", 30)
print(p)  # Person(name=''홍길동'', age=30, city=''서울'')
```

`@dataclass` 데코레이터만 추가하면 `__init__`, `__repr__` 메서드가 자동으로 생성됩니다.

## 주요 예제

### 예제 1: 자동 생성 메서드 활용

```python
from dataclasses import dataclass

@dataclass
class Product:
    name: str
    price: int
    stock: int

p1 = Product("노트북", 1500000, 10)
p2 = Product("노트북", 1500000, 10)

print(p1)  # Product(name=''노트북'', price=1500000, stock=10)
print(p1 == p2)  # True (__eq__ 자동 생성)
```

### 예제 2: field()로 기본값 설정

```python
from dataclasses import dataclass, field

@dataclass
class Cart:
    user_id: int
    items: list = field(default_factory=list)
    total: int = 0

cart = Cart(123)
cart.items.append("사과")
print(cart)  # Cart(user_id=123, items=[''사과''], total=0)
```

`default_factory`는 가변 기본값(리스트, 딕셔너리)을 안전하게 생성합니다.

### 예제 3: 불변 데이터 클래스

```python
from dataclasses import dataclass

@dataclass(frozen=True)
class Point:
    x: int
    y: int

p = Point(10, 20)
print(p)  # Point(x=10, y=20)

# p.x = 30  # FrozenInstanceError 발생
```

`frozen=True`로 불변 객체를 만들어 데이터 무결성을 보장합니다.

### 예제 4: 정렬 가능한 데이터 클래스

```python
from dataclasses import dataclass

@dataclass(order=True)
class Student:
    score: int
    name: str = field(compare=False)

students = [
    Student(85, "김철수"),
    Student(92, "이영희"),
    Student(78, "박민수")
]

print(sorted(students))
# [Student(score=78, name=''박민수''),
#  Student(score=85, name=''김철수''),
#  Student(score=92, name=''이영희'')]
```

### 예제 5: 초기화 후 처리

```python
from dataclasses import dataclass

@dataclass
class Rectangle:
    width: int
    height: int
    area: int = 0

    def __post_init__(self):
        self.area = self.width * self.height

rect = Rectangle(10, 20)
print(rect)  # Rectangle(width=10, height=20, area=200)
```

## 주의사항

- ⚠️ 가변 기본값(리스트, 딕셔너리)은 반드시 `field(default_factory=...)`를 사용하세요
- ⚠️ `frozen=True` 설정 시 객체 생성 후 필드 수정이 불가능합니다
- 💡 타입 힌트는 필수이며, 런타임에 검증되지 않습니다
- 💡 `order=True` 사용 시 모든 필드가 비교 가능해야 합니다
- 💡 Python 3.7 이상에서 사용 가능합니다

## 정리

dataclasses 모듈은 데이터 중심 클래스를 간결하게 작성할 수 있게 해주는 강력한 도구입니다. `@dataclass` 데코레이터 하나로 반복적인 코드를 제거하고, `field()`와 `frozen` 옵션으로 다양한 요구사항을 충족할 수 있습니다.

### 배운 내용
- ✅ `@dataclass`로 `__init__`, `__repr__`, `__eq__` 자동 생성
- ✅ `field(default_factory=...)`로 안전한 기본값 설정
- ✅ `frozen=True`로 불변 객체 생성
- ✅ `order=True`로 정렬 가능한 클래스 생성
- ✅ `__post_init__`으로 초기화 후 추가 처리
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('typing 모듈 - 타입 힌트', 'Python 타입 힌트와 typing 모듈 활용법', '# typing 모듈 - 타입 힌트

타입 힌트는 Python 3.5부터 도입된 기능으로, 코드의 가독성과 유지보수성을 높여줍니다. 실행 시점에는 영향을 주지 않지만, IDE와 타입 체커가 오류를 미리 발견할 수 있게 도와줍니다.

## 학습 목표

- [ ] 타입 힌트의 개념과 필요성을 설명할 수 있다
- [ ] 함수와 변수에 타입 힌트를 작성할 수 있다
- [ ] Optional, Union, List, Dict 등 제네릭 타입을 사용할 수 있다
- [ ] TypeVar와 Generic을 활용한 제네릭 함수를 작성할 수 있다
- [ ] 타입 체커의 역할과 사용법을 이해한다

## 타입 힌트란?

타입 힌트는 변수, 함수 매개변수, 반환값의 타입을 명시하는 문법입니다. Python은 동적 타입 언어이지만, 타입 힌트를 통해 정적 타입 검사의 이점을 누릴 수 있습니다.

타입 힌트는 런타임에 강제되지 않으며, 주로 개발 도구(IDE, mypy 등)가 코드 분석에 활용합니다. 이를 통해 타입 오류를 실행 전에 발견하고, 자동완성 기능이 향상되며, 코드 문서화 효과를 얻을 수 있습니다.

## 기본 사용법

```python
# 변수 타입 힌트
name: str = "Alice"
age: int = 30

# 함수 타입 힌트
def greet(name: str) -> str:
    return f"Hello, {name}!"

# 출력: Hello, Alice!
print(greet("Alice"))
```

함수 시그니처에서 `name: str`은 매개변수 타입을, `-> str`은 반환 타입을 나타냅니다.

## 주요 예제

### 예제 1: 컬렉션 타입 힌트

```python
from typing import List, Dict, Tuple, Set

# 리스트: 같은 타입의 요소들
numbers: List[int] = [1, 2, 3]

# 딕셔너리: 키와 값의 타입 명시
scores: Dict[str, int] = {"Alice": 90, "Bob": 85}

# 튜플: 각 위치의 타입 명시
point: Tuple[int, int] = (10, 20)

# 셋: 중복 없는 요소들
tags: Set[str] = {"python", "typing"}
```

### 예제 2: Optional과 Union

```python
from typing import Optional, Union

# Optional[str]은 Union[str, None]과 동일
def find_user(user_id: int) -> Optional[str]:
    users = {1: "Alice", 2: "Bob"}
    return users.get(user_id)  # None 가능

# Union: 여러 타입 중 하나
def process(value: Union[int, str]) -> str:
    if isinstance(value, int):
        return f"Number: {value}"
    return f"Text: {value}"
```

### 예제 3: 제네릭 타입

```python
from typing import TypeVar, List

# TypeVar로 제네릭 타입 변수 정의
T = TypeVar(''T'')

def first_item(items: List[T]) -> T:
    return items[0]

# 사용 예시
print(first_item([1, 2, 3]))      # 출력: 1
print(first_item(["a", "b"]))     # 출력: a
```

### 예제 4: Callable 타입

```python
from typing import Callable

# Callable[[매개변수 타입들], 반환 타입]
def apply(func: Callable[[int, int], int], x: int, y: int) -> int:
    return func(x, y)

def add(a: int, b: int) -> int:
    return a + b

print(apply(add, 5, 3))  # 출력: 8
```

### 예제 5: Generic 클래스

```python
from typing import Generic, TypeVar

T = TypeVar(''T'')

class Box(Generic[T]):
    def __init__(self, item: T):
        self.item = item

    def get(self) -> T:
        return self.item

# 타입별 Box 생성
int_box = Box[int](42)
str_box = Box[str]("Hello")
```

## 주의사항

- ⚠️ **런타임 검사 없음**: 타입 힌트는 실행 시 검사되지 않으므로, 잘못된 타입을 전달해도 오류가 발생하지 않습니다
- ⚠️ **Python 3.9+ 간소화**: Python 3.9부터는 `List[int]` 대신 `list[int]`, `Dict[str, int]` 대신 `dict[str, int]` 사용 가능
- 💡 **mypy 사용**: `pip install mypy`로 설치 후 `mypy script.py` 명령으로 타입 검사 수행
- 💡 **점진적 적용**: 모든 코드에 타입 힌트를 추가할 필요는 없으며, 중요한 부분부터 점진적으로 적용
- 💡 **IDE 지원**: VS Code, PyCharm 등 대부분의 IDE가 타입 힌트 기반 자동완성과 오류 검사 제공

## 정리

타입 힌트는 Python 코드의 안정성과 가독성을 크게 향상시킵니다. typing 모듈의 다양한 타입을 활용하면 복잡한 데이터 구조도 명확하게 표현할 수 있으며, 타입 체커를 통해 버그를 조기에 발견할 수 있습니다.

### 배운 내용

- ✅ 타입 힌트는 개발 도구를 위한 메타데이터이며 런타임에는 영향을 주지 않음
- ✅ List, Dict, Optional, Union 등으로 다양한 타입 표현 가능
- ✅ TypeVar와 Generic으로 제네릭 함수와 클래스 작성 가능
- ✅ mypy 같은 타입 체커로 실행 전 타입 오류 발견 가능
- ✅ Python 3.9+에서는 내장 타입으로도 제네릭 표현 가능
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('abc 모듈 - 추상 베이스 클래스', '추상 베이스 클래스로 강력한 인터페이스 설계하기', '# abc 모듈 - 추상 베이스 클래스

abc 모듈은 추상 베이스 클래스(Abstract Base Class)를 정의하여 클래스의 인터페이스를 강제할 수 있게 해줍니다. 이를 통해 코드의 안정성을 높이고 명확한 설계를 할 수 있습니다.

## 학습 목표

- [ ] 추상 베이스 클래스의 개념과 필요성을 이해한다
- [ ] ABC를 상속하여 추상 클래스를 정의할 수 있다
- [ ] @abstractmethod를 사용하여 추상 메서드를 정의할 수 있다
- [ ] 인터페이스를 설계할 수 있다

## 추상 베이스 클래스란?

추상 베이스 클래스(ABC)는 직접 인스턴스화할 수 없고, 하위 클래스가 반드시 구현해야 할 메서드를 정의하는 클래스입니다. Java나 C++의 인터페이스와 유사한 개념으로, Python에서 명확한 계약(contract)을 정의할 때 사용합니다.

일반 상속과 달리 ABC는 하위 클래스가 특정 메서드를 구현하지 않으면 인스턴스화 시점에 TypeError를 발생시켜 설계 오류를 조기에 발견할 수 있습니다. 대규모 프로젝트나 라이브러리 개발 시 일관된 인터페이스를 보장하는 데 필수적입니다.

## 기본 사용법

```python
from abc import ABC, abstractmethod

class Animal(ABC):
    @abstractmethod
    def speak(self):
        pass

class Dog(Animal):
    def speak(self):
        return "Woof!"

dog = Dog()
print(dog.speak())  # Woof!
# animal = Animal()  # TypeError 발생
```

ABC를 상속받은 클래스는 직접 인스턴스화할 수 없으며, @abstractmethod로 지정된 메서드를 구현해야 합니다.

## 주요 예제

### 예제 1: 결제 시스템 인터페이스
```python
from abc import ABC, abstractmethod

class PaymentProcessor(ABC):
    @abstractmethod
    def process_payment(self, amount):
        pass

class CreditCard(PaymentProcessor):
    def process_payment(self, amount):
        return f"카드로 {amount}원 결제"

card = CreditCard()
print(card.process_payment(10000))
```

추상 클래스를 사용하면 모든 결제 방식이 process_payment를 구현하도록 강제할 수 있습니다.

### 예제 2: 추상 프로퍼티
```python
from abc import ABC, abstractmethod

class Shape(ABC):
    @property
    @abstractmethod
    def area(self):
        pass

class Rectangle(Shape):
    def __init__(self, width, height):
        self.width = width
        self.height = height

    @property
    def area(self):
        return self.width * self.height

rect = Rectangle(5, 3)
print(rect.area)  # 15
```

@property와 @abstractmethod를 함께 사용하여 추상 프로퍼티를 정의할 수 있습니다.

### 예제 3: 여러 추상 메서드
```python
from abc import ABC, abstractmethod

class Database(ABC):
    @abstractmethod
    def connect(self):
        pass

    @abstractmethod
    def query(self, sql):
        pass

class MySQL(Database):
    def connect(self):
        return "MySQL 연결"

    def query(self, sql):
        return f"실행: {sql}"

db = MySQL()
print(db.connect())  # MySQL 연결
```

하나의 추상 클래스에 여러 추상 메서드를 정의하여 복잡한 인터페이스를 설계할 수 있습니다.

### 예제 4: 구현이 있는 추상 클래스
```python
from abc import ABC, abstractmethod

class Logger(ABC):
    @abstractmethod
    def log(self, message):
        pass

    def log_error(self, error):
        return self.log(f"ERROR: {error}")

class FileLogger(Logger):
    def log(self, message):
        return f"파일에 기록: {message}"

logger = FileLogger()
print(logger.log_error("실패"))  # 파일에 기록: ERROR: 실패
```

추상 클래스에도 일반 메서드를 포함할 수 있으며, 추상 메서드를 활용하는 공통 기능을 제공할 수 있습니다.

### 예제 5: 타입 체크
```python
from abc import ABC, abstractmethod

class Drawable(ABC):
    @abstractmethod
    def draw(self):
        pass

class Circle(Drawable):
    def draw(self):
        return "원 그리기"

class Square(Drawable):
    def draw(self):
        return "사각형 그리기"

shapes = [Circle(), Square()]
for shape in shapes:
    if isinstance(shape, Drawable):
        print(shape.draw())
```

ABC를 사용하면 isinstance()로 인터페이스 준수 여부를 확인할 수 있습니다.

## 주의사항

- ⚠️ 추상 메서드를 구현하지 않으면 인스턴스화 시 TypeError가 발생합니다
- ⚠️ @abstractmethod는 메서드 정의 시 가장 안쪽 데코레이터여야 합니다
- 💡 추상 클래스에도 일반 메서드와 속성을 포함할 수 있습니다
- 💡 과도한 추상화는 코드를 복잡하게 만들 수 있으니 필요한 경우에만 사용하세요
- 💡 Python 3.4 이후부터는 abstractproperty 대신 @property와 @abstractmethod 조합을 권장합니다

## 정리

abc 모듈은 추상 베이스 클래스를 통해 명확한 인터페이스를 정의하고 클래스 설계의 일관성을 보장합니다. 대규모 프로젝트에서 협업 시 특히 유용하며, 코드의 안정성과 유지보수성을 크게 향상시킵니다.

### 배운 내용
- ✅ ABC를 상속하여 추상 클래스를 정의하고 인스턴스화를 방지할 수 있습니다
- ✅ @abstractmethod로 하위 클래스가 반드시 구현해야 할 메서드를 지정할 수 있습니다
- ✅ 추상 프로퍼티와 일반 메서드를 결합하여 유연한 인터페이스를 설계할 수 있습니다
- ✅ isinstance()를 통해 인터페이스 준수 여부를 타입 체크할 수 있습니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 25, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());

INSERT INTO lectures (title, description, content, input_content, output_content, type, category, difficulty, is_public, thumbnail_image_url, duration_minutes, author_id, constraints_id, created_at, updated_at) VALUES
('Python 코딩 모범 사례와 스타일 가이드', 'PEP 8과 Pythonic 코드 작성법을 배우고 코드 품질을 향상시킵니다', '# Python 코딩 모범 사례와 스타일 가이드

좋은 코드는 동작하는 코드를 넘어 읽기 쉽고 유지보수하기 쉬운 코드입니다. Python 커뮤니티가 합의한 모범 사례와 스타일 가이드를 학습합니다.

## 학습 목표

- [ ] PEP 8 스타일 가이드를 이해하고 적용할 수 있다
- [ ] Pythonic한 코드를 작성할 수 있다
- [ ] 일반적인 안티패턴을 피할 수 있다
- [ ] 코드 품질 도구를 활용할 수 있다
- [ ] 성능과 가독성의 균형을 맞출 수 있다

## PEP 8이란?

PEP 8은 Python 코드의 스타일 가이드로, 일관성 있고 읽기 쉬운 코드를 작성하기 위한 권장사항입니다. "코드는 작성되는 것보다 읽히는 경우가 더 많다"는 원칙에 기반합니다.

일관된 스타일을 사용하면 코드 리뷰가 쉬워지고, 팀 협업이 원활해지며, 버그를 더 빨리 발견할 수 있습니다.

## PEP 8 핵심 원칙

```python
# 명명 규칙
class UserProfile:  # 클래스: PascalCase
    MAX_USERS = 100  # 상수: UPPER_CASE

    def get_user_name(self):  # 함수/메서드: snake_case
        user_name = "홍길동"  # 변수: snake_case
        return user_name

# 들여쓰기: 공백 4칸
def process_data(items):
    for item in items:
        if item.is_valid():
            item.process()
```

함수와 변수는 snake_case, 클래스는 PascalCase, 상수는 UPPER_CASE를 사용합니다.

## Pythonic 코드 작성하기

### 예제 1: EAFP vs LBYL
```python
# LBYL (Look Before You Leap) - 비권장
if key in dictionary:
    value = dictionary[key]
else:
    value = default

# EAFP (Easier to Ask Forgiveness than Permission) - 권장
try:
    value = dictionary[key]
except KeyError:
    value = default
```

Python은 EAFP 스타일을 권장합니다. 예외 처리가 조건문보다 더 Pythonic합니다.

### 예제 2: 컴프리헨션 활용
```python
# 비권장
squares = []
for i in range(10):
    squares.append(i ** 2)

# 권장: 리스트 컴프리헨션
squares = [i ** 2 for i in range(10)]
```

### 예제 3: 컨텍스트 매니저 사용
```python
# 비권장
file = open(''data.txt'', ''r'')
data = file.read()
file.close()

# 권장: with 문 사용
with open(''data.txt'', ''r'') as file:
    data = file.read()
# 자동으로 파일이 닫힘
```

### 예제 4: 언패킹 활용
```python
# 비권장
first = items[0]
second = items[1]
rest = items[2:]

# 권장: 언패킹 사용
first, second, *rest = items
```

## 일반적인 안티패턴

### 예제 5: 가변 기본 인자
```python
# 안티패턴 - 가변 객체를 기본값으로 사용
def add_item(item, items=[]):  # 위험!
    items.append(item)
    return items

# 해결책
def add_item(item, items=None):
    if items is None:
        items = []
    items.append(item)
    return items
```

가변 객체(리스트, 딕셔너리)를 기본 인자로 사용하면 예상치 못한 동작이 발생합니다.

### 예제 6: 불필요한 조건문
```python
# 안티패턴
if condition == True:
    return True
else:
    return False

# 해결책
return condition
```

### 예제 7: 문자열 연결
```python
# 비효율적 - 반복문에서 += 사용
result = ""
for item in items:
    result += str(item)

# 효율적 - join 사용
result = "".join(str(item) for item in items)
```

## The Zen of Python

```python
import this
# Beautiful is better than ugly.
# Explicit is better than implicit.
# Simple is better than complex.
# Readability counts.
```

The Zen of Python(PEP 20)은 19개 원칙으로 Python 철학을 담고 있습니다. "아름다움이 추함보다 낫다", "명시적인 것이 암시적인 것보다 낫다" 등 코드 작성의 지침이 됩니다.

## 코드 품질 도구

```python
# black: 자동 코드 포맷터
# $ pip install black
# $ black your_script.py

# flake8: 스타일 체커
# $ pip install flake8
# $ flake8 your_script.py

# pylint: 포괄적 코드 분석
# $ pip install pylint
# $ pylint your_script.py
```

이러한 도구들을 CI/CD 파이프라인에 통합하면 코드 품질을 자동으로 관리할 수 있습니다.

## 주의사항

- 스타일 가이드는 규칙이 아닌 권장사항입니다. 프로젝트의 일관성이 더 중요합니다
- 가독성을 위해서라면 PEP 8을 어기는 것도 괜찮습니다
- 성능이 중요한 부분에서는 Pythonic 코드보다 최적화가 우선일 수 있습니다
- 기존 코드베이스의 스타일을 존중하세요

## 정리

Python 모범 사례는 코드의 일관성과 가독성을 높여 유지보수를 쉽게 만듭니다. PEP 8을 따르고, Pythonic한 방식으로 코드를 작성하며, 자동화 도구를 활용하면 높은 품질의 코드를 생산할 수 있습니다.

### 배운 내용
- PEP 8 명명 규칙과 코드 스타일을 적용할 수 있습니다
- EAFP 스타일과 컴프리헨션으로 Pythonic 코드를 작성합니다
- 가변 기본 인자 등 일반적인 안티패턴을 피할 수 있습니다
- black, flake8, pylint로 코드 품질을 관리합니다
- The Zen of Python 원칙을 실천합니다
', NULL, NULL, 'MARKDOWN', 'Python', '고급', true, NULL, 30, (SELECT id FROM users WHERE login_id = 'admin'), NULL, NOW(), NOW());


-- ============================================
-- 커리큘럼-강의 연결 (curriculum_lectures)
-- ============================================

-- Python 기초 완성 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = 'Python 첫걸음'), 1, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '변수와 기본 자료형'), 2, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '연산자의 모든 것'), 3, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '입출력 다루기'), 4, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '문자열 기초'), 5, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '문자열 메서드 완전 정복'), 6, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '문자열 포매팅 심화'), 7, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '리스트 기초'), 8, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '리스트 메서드와 활용'), 9, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '튜플 - 불변 시퀀스'), 10, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '집합(Set) - 중복 없는 컬렉션'), 11, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '딕셔너리 - 키-값 저장소'), 12, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '조건문 - if, elif, else'), 13, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = 'for 반복문'), 14, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = 'while 반복문'), 15, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '함수 정의와 호출'), 16, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '함수 매개변수 심화'), 17, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 기초 완성'), (SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), 18, false);

-- Python 중급 마스터 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '리스트 컴프리헨션'), 1, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '딕셔너리와 집합 컴프리헨션'), 2, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = 'map과 filter 함수'), 3, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = 'lambda와 reduce'), 4, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '파일 읽기와 쓰기'), 5, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '예외 처리 - try, except, finally'), 6, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '사용자 정의 예외'), 7, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '모듈 시스템'), 8, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '패키지와 __init__.py'), 9, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '표준 라이브러리 활용'), 10, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '클래스 기초'), 11, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '클래스 변수와 메서드'), 12, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '상속(Inheritance)'), 13, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), 14, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 중급 마스터'), (SELECT id FROM lectures WHERE title = '캡슐화와 접근 제어'), 15, false);

-- Python 고급 전문가 강의 연결
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'collections 모듈 완전 정복'), 1, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'itertools 모듈 마스터하기'), 2, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'functools 모듈 심화'), 3, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'os 모듈 - 운영체제 인터페이스'), 4, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'sys 모듈 - 시스템 인터페이스'), 5, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 're 모듈 - 정규표현식'), 6, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'datetime 모듈 - 날짜와 시간'), 7, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'pathlib 모듈 - 객체지향 경로'), 8, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'json 모듈 - JSON 직렬화'), 9, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'csv 모듈 - CSV 파일 처리'), 10, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '매직 메서드 완전 정복'), 11, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '연산자 오버로딩'), 12, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '프로퍼티와 디스크립터'), 13, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '데코레이터 기초'), 14, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '데코레이터 심화'), 15, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '제너레이터 기초'), 16, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '제너레이터 심화와 이터레이터'), 17, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = '컨텍스트 매니저'), 18, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'threading 모듈 - 멀티스레딩 기초'), 19, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'multiprocessing 모듈 - 멀티프로세싱'), 20, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'queue 모듈 - 스레드 안전 큐'), 21, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'pickle 모듈 - 객체 직렬화'), 22, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'copy 모듈 - 얕은 복사와 깊은 복사'), 23, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'bisect 모듈 - 이진 탐색'), 24, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'heapq 모듈 - 힙 자료구조'), 25, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'array 모듈 - 효율적인 배열'), 26, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'struct 모듈 - 바이너리 데이터 처리'), 27, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), 28, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'base64 모듈 - Base64 인코딩'), 29, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'hashlib 모듈 - 해시 함수'), 30, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), 31, true);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'timeit 모듈 - 코드 실행 시간 측정'), 32, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'cProfile과 pstats - 프로파일링'), 33, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'tracemalloc - 메모리 사용량 추적'), 34, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'gc 모듈 - 가비지 컬렉션'), 35, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'weakref 모듈 - 약한 참조'), 36, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'argparse 모듈 - 명령줄 인터페이스'), 37, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'logging 모듈 - 로깅 시스템'), 38, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'unittest 모듈 - 단위 테스트'), 39, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'doctest 모듈 - 문서화 테스트'), 40, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'enum 모듈 - 열거형'), 41, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'dataclasses 모듈 - 데이터 클래스'), 42, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'typing 모듈 - 타입 힌트'), 43, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'abc 모듈 - 추상 베이스 클래스'), 44, false);
INSERT INTO curriculum_lectures (curriculum_id, lecture_id, order_index, is_required) VALUES
((SELECT id FROM curriculums WHERE title = 'Python 고급 전문가'), (SELECT id FROM lectures WHERE title = 'Python 코딩 모범 사례와 스타일 가이드'), 45, false);


-- ============================================
-- 강의 태그 데이터 (lecture_tags)
-- ============================================

INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 첫걸음'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 첫걸음'), '기초');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 첫걸음'), '설치');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 첫걸음'), 'REPL');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 첫걸음'), '시작하기');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '변수와 기본 자료형'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '변수와 기본 자료형'), '변수');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '변수와 기본 자료형'), '자료형');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '변수와 기본 자료형'), '타입');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '변수와 기본 자료형'), '기초');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자의 모든 것'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자의 모든 것'), '연산자');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자의 모든 것'), '산술');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자의 모든 것'), '비교');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자의 모든 것'), '논리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '입출력 다루기'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '입출력 다루기'), '입출력');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '입출력 다루기'), 'print');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '입출력 다루기'), 'input');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '입출력 다루기'), '포매팅');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 기초'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 기초'), '문자열');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 기초'), '인덱싱');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 기초'), '슬라이싱');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 기초'), '기초');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 메서드 완전 정복'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 메서드 완전 정복'), '문자열');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 메서드 완전 정복'), '메서드');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 메서드 완전 정복'), 'split');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 메서드 완전 정복'), 'join');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 포매팅 심화'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 포매팅 심화'), '문자열');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 포매팅 심화'), '포매팅');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 포매팅 심화'), 'f-string');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '문자열 포매팅 심화'), 'format');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 기초'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 기초'), '리스트');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 기초'), '컬렉션');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 기초'), '가변');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 기초'), '기초');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 메서드와 활용'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 메서드와 활용'), '리스트');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 메서드와 활용'), '메서드');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 메서드와 활용'), 'append');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 메서드와 활용'), 'sort');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '튜플 - 불변 시퀀스'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '튜플 - 불변 시퀀스'), '튜플');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '튜플 - 불변 시퀀스'), '불변');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '튜플 - 불변 시퀀스'), '언패킹');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '튜플 - 불변 시퀀스'), '컬렉션');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '집합(Set) - 중복 없는 컬렉션'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '집합(Set) - 중복 없는 컬렉션'), '집합');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '집합(Set) - 중복 없는 컬렉션'), 'set');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '집합(Set) - 중복 없는 컬렉션'), '중복제거');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '집합(Set) - 중복 없는 컬렉션'), '컬렉션');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리 - 키-값 저장소'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리 - 키-값 저장소'), '딕셔너리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리 - 키-값 저장소'), 'dict');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리 - 키-값 저장소'), '키값');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리 - 키-값 저장소'), '컬렉션');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '조건문 - if, elif, else'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '조건문 - if, elif, else'), '조건문');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '조건문 - if, elif, else'), 'if');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '조건문 - if, elif, else'), '제어문');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '조건문 - if, elif, else'), '기초');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'for 반복문'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'for 반복문'), '반복문');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'for 반복문'), 'for');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'for 반복문'), 'range');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'for 반복문'), '제어문');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'while 반복문'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'while 반복문'), '반복문');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'while 반복문'), 'while');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'while 반복문'), '무한루프');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'while 반복문'), '제어문');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 정의와 호출'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 정의와 호출'), '함수');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 정의와 호출'), 'def');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 정의와 호출'), 'return');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 정의와 호출'), '기초');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 매개변수 심화'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 매개변수 심화'), '함수');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 매개변수 심화'), '매개변수');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 매개변수 심화'), 'args');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '함수 매개변수 심화'), 'kwargs');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), '함수');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), '스코프');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), 'global');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), 'nonlocal');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), 'LEGB');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), '클로저');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '스코프와 네임스페이스'), '네임스페이스');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 컴프리헨션'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 컴프리헨션'), '컴프리헨션');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 컴프리헨션'), '리스트');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 컴프리헨션'), '함수형');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '리스트 컴프리헨션'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리와 집합 컴프리헨션'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리와 집합 컴프리헨션'), '컴프리헨션');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리와 집합 컴프리헨션'), '딕셔너리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리와 집합 컴프리헨션'), '집합');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '딕셔너리와 집합 컴프리헨션'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'map과 filter 함수'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'map과 filter 함수'), 'map');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'map과 filter 함수'), 'filter');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'map과 filter 함수'), '함수형');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'map과 filter 함수'), 'lambda');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'lambda와 reduce'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'lambda와 reduce'), 'lambda');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'lambda와 reduce'), 'reduce');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'lambda와 reduce'), '함수형');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'lambda와 reduce'), 'functools');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '파일 읽기와 쓰기'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '파일 읽기와 쓰기'), '파일');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '파일 읽기와 쓰기'), '입출력');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '파일 읽기와 쓰기'), 'with');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '파일 읽기와 쓰기'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '예외 처리 - try, except, finally'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '예외 처리 - try, except, finally'), '예외');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '예외 처리 - try, except, finally'), 'try');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '예외 처리 - try, except, finally'), 'except');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '예외 처리 - try, except, finally'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '사용자 정의 예외'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '사용자 정의 예외'), '예외');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '사용자 정의 예외'), '사용자정의');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '사용자 정의 예외'), 'Exception');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '사용자 정의 예외'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '모듈 시스템'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '모듈 시스템'), '모듈');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '모듈 시스템'), 'import');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '모듈 시스템'), '__name__');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '모듈 시스템'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '패키지와 __init__.py'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '패키지와 __init__.py'), '패키지');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '패키지와 __init__.py'), '__init__');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '패키지와 __init__.py'), 'import');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '패키지와 __init__.py'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '표준 라이브러리 활용'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '표준 라이브러리 활용'), '표준라이브러리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '표준 라이브러리 활용'), 'os');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '표준 라이브러리 활용'), 'sys');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '표준 라이브러리 활용'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 기초'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 기초'), '클래스');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 기초'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 기초'), '__init__');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 기초'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 변수와 메서드'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 변수와 메서드'), '클래스');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 변수와 메서드'), 'classmethod');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 변수와 메서드'), 'staticmethod');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '클래스 변수와 메서드'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '상속(Inheritance)'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '상속(Inheritance)'), '상속');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '상속(Inheritance)'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '상속(Inheritance)'), 'super');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '상속(Inheritance)'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), '다형성');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), '특수메서드');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), '덕타이핑');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '다형성과 특수 메서드'), '연산자오버로딩');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '캡슐화와 접근 제어'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '캡슐화와 접근 제어'), '캡슐화');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '캡슐화와 접근 제어'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '캡슐화와 접근 제어'), 'property');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '캡슐화와 접근 제어'), '중급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'collections 모듈 완전 정복'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'collections 모듈 완전 정복'), 'collections');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'collections 모듈 완전 정복'), 'Counter');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'collections 모듈 완전 정복'), 'deque');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'collections 모듈 완전 정복'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'itertools 모듈 마스터하기'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'itertools 모듈 마스터하기'), 'itertools');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'itertools 모듈 마스터하기'), '조합');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'itertools 모듈 마스터하기'), 'permutations');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'itertools 모듈 마스터하기'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'functools 모듈 심화'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'functools 모듈 심화'), 'functools');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'functools 모듈 심화'), 'lru_cache');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'functools 모듈 심화'), 'partial');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'functools 모듈 심화'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'os 모듈 - 운영체제 인터페이스'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'os 모듈 - 운영체제 인터페이스'), 'os');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'os 모듈 - 운영체제 인터페이스'), '파일시스템');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'os 모듈 - 운영체제 인터페이스'), '경로');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'os 모듈 - 운영체제 인터페이스'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'sys 모듈 - 시스템 인터페이스'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'sys 모듈 - 시스템 인터페이스'), 'sys');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'sys 모듈 - 시스템 인터페이스'), '명령줄');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'sys 모듈 - 시스템 인터페이스'), 'argv');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'sys 모듈 - 시스템 인터페이스'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 're 모듈 - 정규표현식'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 're 모듈 - 정규표현식'), 're');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 're 모듈 - 정규표현식'), '정규표현식');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 're 모듈 - 정규표현식'), 'regex');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 're 모듈 - 정규표현식'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'datetime 모듈 - 날짜와 시간'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'datetime 모듈 - 날짜와 시간'), 'datetime');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'datetime 모듈 - 날짜와 시간'), '날짜');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'datetime 모듈 - 날짜와 시간'), '시간');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'datetime 모듈 - 날짜와 시간'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pathlib 모듈 - 객체지향 경로'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pathlib 모듈 - 객체지향 경로'), 'pathlib');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pathlib 모듈 - 객체지향 경로'), 'Path');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pathlib 모듈 - 객체지향 경로'), '경로');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pathlib 모듈 - 객체지향 경로'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'json 모듈 - JSON 직렬화'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'json 모듈 - JSON 직렬화'), 'json');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'json 모듈 - JSON 직렬화'), '직렬화');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'json 모듈 - JSON 직렬화'), 'API');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'json 모듈 - JSON 직렬화'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'csv 모듈 - CSV 파일 처리'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'csv 모듈 - CSV 파일 처리'), 'csv');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'csv 모듈 - CSV 파일 처리'), '파일처리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'csv 모듈 - CSV 파일 처리'), '데이터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'csv 모듈 - CSV 파일 처리'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '매직 메서드 완전 정복'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '매직 메서드 완전 정복'), '매직메서드');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '매직 메서드 완전 정복'), '특수메서드');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '매직 메서드 완전 정복'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '매직 메서드 완전 정복'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자 오버로딩'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자 오버로딩'), '연산자오버로딩');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자 오버로딩'), '매직메서드');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자 오버로딩'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '연산자 오버로딩'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '프로퍼티와 디스크립터'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '프로퍼티와 디스크립터'), 'property');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '프로퍼티와 디스크립터'), '디스크립터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '프로퍼티와 디스크립터'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '프로퍼티와 디스크립터'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 기초'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 기초'), '데코레이터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 기초'), '클로저');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 기초'), '함수형');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 기초'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 심화'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 심화'), '데코레이터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 심화'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 심화'), '메타프로그래밍');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '데코레이터 심화'), '클로저');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 기초'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 기초'), '제너레이터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 기초'), 'yield');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 기초'), '이터레이터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 기초'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 심화와 이터레이터'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 심화와 이터레이터'), '제너레이터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 심화와 이터레이터'), '이터레이터');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 심화와 이터레이터'), 'yield');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '제너레이터 심화와 이터레이터'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '컨텍스트 매니저'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '컨텍스트 매니저'), 'contextmanager');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '컨텍스트 매니저'), 'with');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '컨텍스트 매니저'), '리소스관리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = '컨텍스트 매니저'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'threading 모듈 - 멀티스레딩 기초'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'threading 모듈 - 멀티스레딩 기초'), 'threading');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'threading 모듈 - 멀티스레딩 기초'), '멀티스레딩');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'threading 모듈 - 멀티스레딩 기초'), '동시성');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'threading 모듈 - 멀티스레딩 기초'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'multiprocessing 모듈 - 멀티프로세싱'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'multiprocessing 모듈 - 멀티프로세싱'), 'multiprocessing');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'multiprocessing 모듈 - 멀티프로세싱'), '병렬처리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'multiprocessing 모듈 - 멀티프로세싱'), '동시성');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'multiprocessing 모듈 - 멀티프로세싱'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'queue 모듈 - 스레드 안전 큐'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'queue 모듈 - 스레드 안전 큐'), 'queue');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'queue 모듈 - 스레드 안전 큐'), '큐');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'queue 모듈 - 스레드 안전 큐'), '동시성');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'queue 모듈 - 스레드 안전 큐'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pickle 모듈 - 객체 직렬화'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pickle 모듈 - 객체 직렬화'), 'pickle');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pickle 모듈 - 객체 직렬화'), '직렬화');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pickle 모듈 - 객체 직렬화'), '저장');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'pickle 모듈 - 객체 직렬화'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'copy 모듈 - 얕은 복사와 깊은 복사'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'copy 모듈 - 얕은 복사와 깊은 복사'), 'copy');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'copy 모듈 - 얕은 복사와 깊은 복사'), '복사');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'copy 모듈 - 얕은 복사와 깊은 복사'), '깊은복사');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'copy 모듈 - 얕은 복사와 깊은 복사'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'bisect 모듈 - 이진 탐색'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'bisect 모듈 - 이진 탐색'), 'bisect');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'bisect 모듈 - 이진 탐색'), '이진탐색');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'bisect 모듈 - 이진 탐색'), '알고리즘');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'bisect 모듈 - 이진 탐색'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'heapq 모듈 - 힙 자료구조'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'heapq 모듈 - 힙 자료구조'), 'heapq');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'heapq 모듈 - 힙 자료구조'), '힙');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'heapq 모듈 - 힙 자료구조'), '우선순위큐');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'heapq 모듈 - 힙 자료구조'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'array 모듈 - 효율적인 배열'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'array 모듈 - 효율적인 배열'), 'array');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'array 모듈 - 효율적인 배열'), '배열');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'array 모듈 - 효율적인 배열'), '메모리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'array 모듈 - 효율적인 배열'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'struct 모듈 - 바이너리 데이터 처리'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'struct 모듈 - 바이너리 데이터 처리'), 'struct');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'struct 모듈 - 바이너리 데이터 처리'), '바이너리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'struct 모듈 - 바이너리 데이터 처리'), '패킹');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'struct 모듈 - 바이너리 데이터 처리'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), 'io');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), 'StringIO');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), 'BytesIO');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), '스트림');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'io 모듈 - 고급 입출력'), '입출력');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'base64 모듈 - Base64 인코딩'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'base64 모듈 - Base64 인코딩'), 'base64');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'base64 모듈 - Base64 인코딩'), '인코딩');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'base64 모듈 - Base64 인코딩'), '바이너리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'base64 모듈 - Base64 인코딩'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'hashlib 모듈 - 해시 함수'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'hashlib 모듈 - 해시 함수'), 'hashlib');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'hashlib 모듈 - 해시 함수'), '해시');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'hashlib 모듈 - 해시 함수'), 'SHA');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'hashlib 모듈 - 해시 함수'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), 'secrets');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), '보안');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), '난수');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), '토큰');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'secrets 모듈 - 보안 난수 생성'), '암호화');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'timeit 모듈 - 코드 실행 시간 측정'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'timeit 모듈 - 코드 실행 시간 측정'), 'timeit');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'timeit 모듈 - 코드 실행 시간 측정'), '성능측정');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'timeit 모듈 - 코드 실행 시간 측정'), '벤치마크');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'timeit 모듈 - 코드 실행 시간 측정'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'cProfile과 pstats - 프로파일링'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'cProfile과 pstats - 프로파일링'), 'cProfile');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'cProfile과 pstats - 프로파일링'), '프로파일링');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'cProfile과 pstats - 프로파일링'), '성능');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'cProfile과 pstats - 프로파일링'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'tracemalloc - 메모리 사용량 추적'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'tracemalloc - 메모리 사용량 추적'), 'tracemalloc');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'tracemalloc - 메모리 사용량 추적'), '메모리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'tracemalloc - 메모리 사용량 추적'), '프로파일링');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'tracemalloc - 메모리 사용량 추적'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'gc 모듈 - 가비지 컬렉션'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'gc 모듈 - 가비지 컬렉션'), 'gc');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'gc 모듈 - 가비지 컬렉션'), '가비지컬렉션');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'gc 모듈 - 가비지 컬렉션'), '메모리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'gc 모듈 - 가비지 컬렉션'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'weakref 모듈 - 약한 참조'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'weakref 모듈 - 약한 참조'), 'weakref');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'weakref 모듈 - 약한 참조'), '약한참조');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'weakref 모듈 - 약한 참조'), '메모리');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'weakref 모듈 - 약한 참조'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'argparse 모듈 - 명령줄 인터페이스'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'argparse 모듈 - 명령줄 인터페이스'), 'argparse');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'argparse 모듈 - 명령줄 인터페이스'), 'CLI');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'argparse 모듈 - 명령줄 인터페이스'), '명령줄');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'argparse 모듈 - 명령줄 인터페이스'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'logging 모듈 - 로깅 시스템'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'logging 모듈 - 로깅 시스템'), 'logging');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'logging 모듈 - 로깅 시스템'), '로그');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'logging 모듈 - 로깅 시스템'), '디버깅');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'logging 모듈 - 로깅 시스템'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'unittest 모듈 - 단위 테스트'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'unittest 모듈 - 단위 테스트'), 'unittest');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'unittest 모듈 - 단위 테스트'), '테스트');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'unittest 모듈 - 단위 테스트'), 'TDD');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'unittest 모듈 - 단위 테스트'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'doctest 모듈 - 문서화 테스트'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'doctest 모듈 - 문서화 테스트'), 'doctest');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'doctest 모듈 - 문서화 테스트'), '테스트');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'doctest 모듈 - 문서화 테스트'), '문서화');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'doctest 모듈 - 문서화 테스트'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'enum 모듈 - 열거형'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'enum 모듈 - 열거형'), 'enum');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'enum 모듈 - 열거형'), '열거형');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'enum 모듈 - 열거형'), '상수');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'enum 모듈 - 열거형'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'dataclasses 모듈 - 데이터 클래스'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'dataclasses 모듈 - 데이터 클래스'), 'dataclasses');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'dataclasses 모듈 - 데이터 클래스'), '데이터클래스');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'dataclasses 모듈 - 데이터 클래스'), 'OOP');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'dataclasses 모듈 - 데이터 클래스'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'typing 모듈 - 타입 힌트'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'typing 모듈 - 타입 힌트'), 'typing');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'typing 모듈 - 타입 힌트'), '타입힌트');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'typing 모듈 - 타입 힌트'), '정적타입');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'typing 모듈 - 타입 힌트'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'abc 모듈 - 추상 베이스 클래스'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'abc 모듈 - 추상 베이스 클래스'), 'abc');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'abc 모듈 - 추상 베이스 클래스'), '추상클래스');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'abc 모듈 - 추상 베이스 클래스'), '인터페이스');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'abc 모듈 - 추상 베이스 클래스'), '고급');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 코딩 모범 사례와 스타일 가이드'), 'Python');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 코딩 모범 사례와 스타일 가이드'), 'PEP8');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 코딩 모범 사례와 스타일 가이드'), '모범사례');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 코딩 모범 사례와 스타일 가이드'), '코딩스타일');
INSERT INTO lecture_tags (lecture_id, tag) VALUES
((SELECT id FROM lectures WHERE title = 'Python 코딩 모범 사례와 스타일 가이드'), '고급');
