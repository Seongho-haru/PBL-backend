# 백준 문제 JSON → SQL 변환 스크립트

백준 온라인 저지의 문제 데이터를 JSON 파일에서 PostgreSQL SQL INSERT 구문으로 변환하는 Python 스크립트입니다.

## 📋 개요

- **입력**: `baekjoon_problems.json` (28,377개 문제)
- **출력**: `baekjoon_problems.sql` (약 86MB, 2,152,244 라인)
- **대상 테이블**: `lecture`, `test_case`

## 🚀 사용법

### 기본 사용

```bash
python3 json_to_sql.py baekjoon_problems.json
```

출력 파일: `baekjoon_problems.sql` (기본값)

### 출력 파일명 지정

```bash
python3 json_to_sql.py baekjoon_problems.json output.sql
```

### 사용자 ID 지정

```bash
python3 json_to_sql.py baekjoon_problems.json output.sql 2
```

모든 문제가 `user_id = 2`로 삽입됩니다.

## 📊 변환 결과

### 생성된 파일 정보

- **파일명**: `baekjoon_problems.sql`
- **크기**: 86MB
- **라인 수**: 2,152,244 줄
- **문제 수**: 28,377개
- **테스트케이스**: 각 문제당 1개 이상

### SQL 구조

각 문제는 다음과 같이 변환됩니다:

```sql
-- 문제 N: [문제 제목]
INSERT INTO lecture (
  user_id, title, type, category, difficulty,
  description, input_description, output_description,
  time_limit_seconds, memory_limit_mb, is_public,
  test_case_count, tags, metadata,
  created_at, updated_at
) VALUES (...);

-- 테스트케이스 (문제 N)
DO $$
DECLARE
  lecture_id_var INTEGER;
BEGIN
  SELECT id INTO lecture_id_var FROM lecture
  WHERE title = '[문제 제목]' AND user_id = [USER_ID]
  ORDER BY created_at DESC LIMIT 1;

  INSERT INTO test_case (lecture_id, input, expected_output, order_index)
  VALUES (lecture_id_var, '[입력]', '[예상 출력]', [순서]);
END $$;
```

## 📦 변환 필드 매핑

| JSON 필드 | SQL 컬럼 | 타입 | 비고 |
|-----------|----------|------|------|
| `title` | `title` | VARCHAR | 문제 제목 |
| `description` | `description` | TEXT | 문제 설명 |
| `inputDescription` | `input_description` | TEXT | 입력 설명 |
| `outputDescription` | `output_description` | TEXT | 출력 설명 |
| `type` | `type` | VARCHAR | 강의 타입 (PROBLEM) |
| `category` | `category` | VARCHAR | 카테고리 |
| `difficulty` | `difficulty` | VARCHAR | 난이도 |
| `timeLimit` | `time_limit_seconds` | FLOAT | 시간 제한 (초) |
| `memoryLimit` | `memory_limit_mb` | INT | 메모리 제한 (MB) |
| `isPublic` | `is_public` | BOOLEAN | 공개 여부 |
| `tags` | `tags` | JSONB | 태그 배열 |
| `metadata` | `metadata` | JSONB | 메타데이터 (출처, URL 등) |
| `testCases` | → `test_case` 테이블 | - | 별도 테이블로 분리 |

## 🗄️ 데이터베이스 적용

### PostgreSQL에 적용

```bash
# 방법 1: psql 커맨드 사용
psql -U username -d database_name -f baekjoon_problems.sql

# 방법 2: psql 셸 내에서
\i /path/to/baekjoon_problems.sql
```

### 주의사항

⚠️ **대용량 데이터**: 28,377개의 문제와 테스트케이스를 삽입하므로 시간이 소요될 수 있습니다.

```sql
-- 실행 전 트랜잭션 시작 권장
BEGIN;

-- SQL 파일 실행
\i baekjoon_problems.sql

-- 확인 후 커밋
COMMIT;

-- 문제 발생 시 롤백
-- ROLLBACK;
```

### 삽입 후 확인

```sql
-- 총 강의 수 확인
SELECT COUNT(*) FROM lecture WHERE type = 'PROBLEM';

-- 총 테스트케이스 수 확인
SELECT COUNT(*) FROM test_case;

-- 난이도별 통계
SELECT difficulty, COUNT(*)
FROM lecture
WHERE type = 'PROBLEM'
GROUP BY difficulty
ORDER BY COUNT(*) DESC;

-- 카테고리별 통계
SELECT category, COUNT(*)
FROM lecture
WHERE type = 'PROBLEM'
GROUP BY category
ORDER BY COUNT(*) DESC;
```

## 🔧 스크립트 구조

### 주요 함수

#### `escape_sql_string(value)`
- SQL 인젝션 방지를 위한 문자열 이스케이프
- 작은따옴표(`'`)와 백슬래시(`\`) 처리

#### `convert_to_sql(json_file, output_file, user_id)`
- JSON 파일 읽기
- SQL INSERT 구문 생성
- 테스트케이스 처리 (DO 블록 사용)

### 에러 처리

스크립트는 다음 에러를 처리합니다:

- 파일을 찾을 수 없는 경우 (`FileNotFoundError`)
- JSON 파싱 실패 (`JSONDecodeError`)
- 기타 예외 (`Exception`)

## 📝 JSON 입력 형식

```json
[
  {
    "title": "문제 제목",
    "description": "문제 설명",
    "inputDescription": "입력 설명",
    "outputDescription": "출력 설명",
    "type": "PROBLEM",
    "category": "알고리즘",
    "difficulty": "브론즈",
    "timeLimit": 2.0,
    "memoryLimit": 131072,
    "isPublic": false,
    "testCases": [
      {
        "input": "1 2",
        "expectedOutput": "3",
        "orderIndex": 1
      }
    ],
    "tags": ["구현", "수학"],
    "metadata": {
      "source": "백준 온라인 저지",
      "problemId": 1000,
      "url": "https://www.acmicpc.net/problem/1000"
    }
  }
]
```

## ⚡ 성능 최적화 팁

### 1. 인덱스 생성 (삽입 후)

```sql
-- 검색 성능 향상
CREATE INDEX idx_lecture_category ON lecture(category);
CREATE INDEX idx_lecture_difficulty ON lecture(difficulty);
CREATE INDEX idx_lecture_type ON lecture(type);
CREATE INDEX idx_lecture_user_id ON lecture(user_id);
CREATE INDEX idx_test_case_lecture_id ON test_case(lecture_id);
```

### 2. VACUUM 및 ANALYZE

```sql
-- 삽입 후 테이블 최적화
VACUUM ANALYZE lecture;
VACUUM ANALYZE test_case;
```

## 🐛 문제 해결

### 문제: 중복 제목으로 인한 테스트케이스 매칭 실패

**원인**: 동일한 제목을 가진 문제가 여러 개 있을 경우

**해결**: `ORDER BY created_at DESC LIMIT 1`을 사용하여 최신 항목 선택

### 문제: 메모리 부족

**원인**: 대용량 SQL 파일 처리

**해결**:
```bash
# 파일을 여러 개로 분할
split -l 50000 baekjoon_problems.sql part_

# 각 파일을 순차적으로 실행
for file in part_*; do
  psql -U username -d database_name -f $file
done
```

## 📚 추가 정보

### 변환 시간
- JSON 읽기 및 파싱: ~2초
- SQL 파일 생성: ~30초 (총 약 32초)

### 시스템 요구사항
- Python 3.6 이상
- 디스크 여유 공간: 최소 100MB

## 📄 라이센스

이 스크립트는 PBL 프로젝트의 일부입니다.

## 👨‍💻 작성자

Claude Code Assistant (2025)
