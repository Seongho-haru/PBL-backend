# 백준 문제 데이터 임포트 가이드

백준 문제 28,377개를 데이터베이스에 입력하는 하이브리드 방식 가이드입니다.

## 📊 개요

전체 28,377개의 백준 문제를 두 그룹으로 나누어 처리합니다:

| 그룹 | 문제 수 | 비율 | 처리 방식 | 소요 시간 |
|------|---------|------|-----------|-----------|
| **수학 기호 없음** | 19,551개 | 68.9% | SQL 마이그레이션 | 수 초 |
| **수학 기호 있음** | 8,826개 | 31.1% | HTTP API | 20-40분 |

### 왜 하이브리드 방식인가?

- **SQL 방식**: 빠르지만 LaTeX 수학 기호(`$...$`)가 Flyway placeholder와 충돌
- **HTTP API 방식**: 안전하지만 28,000개 전체를 처리하기엔 시간이 너무 오래 걸림
- **하이브리드**: 대부분(69%)을 SQL로 빠르게 처리하고, 나머지(31%)만 HTTP로 안전하게 처리

---

## 🚀 실행 순서

### 1단계: SQL 스크립트 생성 (수학 기호 없는 문제)

```bash
cd /Users/yunseongho/Desktop/PBL/PBL-backend/scripts

# SQL 파일 생성 (19,551개 문제)
python3 json_to_sql_filtered.py baekjoon_problems.json V103__baekjoon_problems_no_math.sql 1
```

**출력 예시:**
```
🚀 백준 문제 JSON → SQL 변환 스크립트 (수학 기호 필터링)
============================================================
입력 파일: baekjoon_problems.json
출력 파일: V103__baekjoon_problems_no_math.sql
Author ID: 1
============================================================

📖 JSON 파일 읽기: baekjoon_problems.json
✅ 총 28377개의 문제를 읽었습니다.
🔍 수학 기호 필터링 중...
✅ 필터링 완료:
   - 수학 기호 없음 (SQL 생성): 19551개 (68.9%)
   - 수학 기호 있음 (HTTP API 필요): 8826개 (31.1%)

⏳ 100/19551 문제 변환 중...
⏳ 200/19551 문제 변환 중...
...
```

**생성된 파일:**
- `V103__baekjoon_problems_no_math.sql` (약 95MB)

---

### 2단계: SQL 파일을 Flyway 마이그레이션 디렉토리로 이동

```bash
# 기존 V103 파일 백업 (있다면)
mv ../src/main/resources/db/migration/V103__baekjoon_problems_lecture.sql \
   ../src/main/resources/db/migration/V103__baekjoon_problems_lecture.sql.backup

# 새 SQL 파일 이동
cp V103__baekjoon_problems_no_math.sql \
   ../src/main/resources/db/migration/V103__baekjoon_problems_no_math.sql
```

---

### 3단계: Spring Boot 재시작 (마이그레이션 실행)

```bash
cd /Users/yunseongho/Desktop/PBL/PBL-backend

# 애플리케이션 재시작 (마이그레이션 자동 실행)
# IntelliJ에서 재시작하거나 명령어로 실행:
./mvnw spring-boot:run
```

**로그 확인:**
```
...
INFO  o.f.c.i.s.JdbcTableSchemaHistory : Creating Schema History table "public"."flyway_schema_history" ...
INFO  o.f.core.internal.command.DbMigrate : Migrating schema "public" to version "103 - baekjoon problems no math"
INFO  o.f.core.internal.command.DbMigrate : Successfully applied 1 migration to schema "public"
...
```

**결과:**
- ✅ 19,551개의 문제가 `lectures` 테이블에 삽입됨
- ✅ 테스트 케이스가 `test_cases` 테이블에 삽입됨
- ⏱️ 소요 시간: 10-30초

---

### 4단계: HTTP API 스크립트 실행 (수학 기호 있는 문제)

**주의:** Spring Boot 애플리케이션이 실행 중이어야 합니다!

```bash
cd /Users/yunseongho/Desktop/PBL/PBL-backend/scripts

# 필요한 패키지 설치 (처음 한 번만)
pip3 install requests

# HTTP API로 8,826개 문제 전송
python3 json_to_api.py baekjoon_problems.json
```

**실행 옵션:**

```bash
# 기본 실행 (localhost:2358, user_id=5)
python3 json_to_api.py baekjoon_problems.json

# 다른 서버 지정
python3 json_to_api.py baekjoon_problems.json http://192.168.1.100:2358/api/lectures

# 다른 사용자 ID 지정
python3 json_to_api.py baekjoon_problems.json http://localhost:2358/api/lectures 6
```

**출력 예시:**
```
🚀 백준 문제 JSON → HTTP API 전송 스크립트 (수학 기호 포함)
============================================================
입력 파일: baekjoon_problems.json
API URL: http://localhost:2358/api/lectures
User ID: 5
시작 시간: 2025-10-20 21:30:00
============================================================

📖 JSON 파일 읽기: baekjoon_problems.json
✅ 총 28377개의 문제를 읽었습니다.
🔍 수학 기호 필터링 중...
✅ 필터링 완료:
   - 수학 기호 있음 (HTTP API 전송): 8826개 (31.1%)
   - 수학 기호 없음 (건너뜀): 19551개 (68.9%)

🚀 HTTP API로 8826개 문제 전송 시작...
   - API URL: http://localhost:2358/api/lectures
   - User ID: 5
   - 최대 재시도: 3회
============================================================
⏳ [1/8826] 진행 중... (성공: 0, 실패: 0, 속도: 0.0개/초, 예상 남은 시간: 0.0분)
⏳ [10/8826] 진행 중... (성공: 10, 실패: 0, 속도: 2.5개/초, 예상 남은 시간: 58.8분)
...
⏳ [100/8826] 진행 중... (성공: 100, 실패: 0, 속도: 3.2개/초, 예상 남은 시간: 45.4분)
...
```

**예상 소요 시간:**
- 속도: 약 2-5개/초
- 총 시간: 20-40분

---

### 5단계: 결과 확인

#### 데이터베이스 확인

```sql
-- 전체 문제 수 확인
SELECT COUNT(*) FROM lectures WHERE type = 'PROBLEM';
-- 예상 결과: 28377

-- 테스트 케이스 수 확인
SELECT COUNT(*) FROM test_cases;

-- 수학 기호 있는 문제 샘플 확인
SELECT id, title, description
FROM lectures
WHERE description LIKE '%$%'
LIMIT 5;
```

#### API 로그 확인

스크립트가 실행되면 통계가 출력됩니다:

```
============================================================
📊 전송 완료 통계
============================================================
총 문제 수: 8826
성공: 8820 (99.9%)
실패: 6 (0.1%)
소요 시간: 35.2분
평균 속도: 4.2개/초

⚠️  실패한 6개 문제가 failed_problems.json에 저장되었습니다.
```

#### 실패한 문제 재시도

만약 일부 문제가 실패했다면:

1. `failed_problems.json` 파일 확인
2. 실패 원인 파악 (네트워크, 타임아웃 등)
3. 수동으로 재시도하거나 스크립트 재실행

---

## 📝 스크립트 상세 설명

### `json_to_sql_filtered.py`

**기능:**
- JSON에서 수학 기호(`$`, `\\`)가 **없는** 문제만 필터링
- SQL INSERT 구문 생성 (V102 스키마 호환)
- `lectures` 테이블과 `test_cases` 테이블에 데이터 삽입

**사용법:**
```bash
python3 json_to_sql_filtered.py <json_file> [output_file] [author_id]
```

**예제:**
```bash
python3 json_to_sql_filtered.py baekjoon_problems.json
python3 json_to_sql_filtered.py baekjoon_problems.json custom_output.sql
python3 json_to_sql_filtered.py baekjoon_problems.json output.sql 2  # author_id=2 (lee.seojun)
```

---

### `json_to_api.py`

**기능:**
- JSON에서 수학 기호가 **있는** 문제만 필터링
- `POST /api/lectures` API로 문제 전송
- 실패 시 최대 3회 재시도
- 진행률 및 통계 표시
- 실패한 문제를 `failed_problems.json`에 저장

**사용법:**
```bash
python3 json_to_api.py <json_file> [api_url] [user_id]
```

**예제:**
```bash
# 기본 실행
python3 json_to_api.py baekjoon_problems.json

# 커스텀 서버
python3 json_to_api.py baekjoon_problems.json http://192.168.1.100:2358/api/lectures

# 다른 사용자
python3 json_to_api.py baekjoon_problems.json http://localhost:2358/api/lectures 6
```

**API 요청 형식:**
```json
POST /api/lectures
Headers:
  Content-Type: application/json
  X-User-Id: 5

Body:
{
  "title": "A+B",
  "description": "두 정수 A와 B를 입력받은 다음, A+B를 출력하는 프로그램을 작성하시오.",
  "content": "# A+B\n\n두 정수...",
  "input_content": "첫째 줄에 A와 B가 주어진다.",
  "output_content": "첫째 줄에 A+B를 출력한다.",
  "type": "PROBLEM",
  "category": "알고리즘",
  "difficulty": "브론즈",
  "isPublic": false,
  "testCases": [
    {"input": "1 2", "expectedOutput": "3"}
  ]
}
```

---

## ⚠️ 주의사항

### 1. Flyway 설정 확인

`application.yml`에 다음 설정이 있어야 합니다:

```yaml
flyway:
  enabled: true
  placeholder-replacement: false  # 중요!
```

### 2. HTTP API 실행 전 확인사항

- ✅ Spring Boot 애플리케이션이 실행 중인가?
- ✅ 포트 2358이 열려 있는가?
- ✅ `requests` 패키지가 설치되어 있는가? (`pip3 install requests`)

### 3. 네트워크 타임아웃

만약 네트워크가 불안정하면:
- 스크립트를 중단하고 (`Ctrl+C`)
- 잠시 후 다시 실행하면 이미 입력된 문제는 건너뜁니다

### 4. 중복 방지

- SQL 방식: 같은 마이그레이션 파일을 두 번 실행하면 오류 발생 (Flyway가 방지)
- HTTP API 방식: 같은 제목의 문제가 여러 개 생성될 수 있음 (수동 확인 필요)

---

## 🔧 트러블슈팅

### 문제: Flyway 마이그레이션 실패

**증상:**
```
ERROR o.f.c.i.c.DbMigrate : Migration of schema "public" to version "103" failed!
```

**해결:**
1. `placeholder-replacement: false` 설정 확인
2. SQL 파일에 문법 오류가 없는지 확인
3. Flyway 히스토리 테이블 확인:
   ```sql
   SELECT * FROM flyway_schema_history ORDER BY installed_on DESC LIMIT 5;
   ```
4. 필요시 마이그레이션 롤백:
   ```sql
   DELETE FROM flyway_schema_history WHERE version = '103';
   ```

### 문제: HTTP API 연결 실패

**증상:**
```
❌ [1/8826] '문제 제목' 실패: 연결 실패 - 서버가 실행 중인지 확인하세요
```

**해결:**
1. Spring Boot가 실행 중인지 확인:
   ```bash
   curl http://localhost:2358/api/lectures
   ```
2. 포트 확인:
   ```bash
   lsof -i :2358
   ```
3. 로그 확인:
   ```bash
   tail -f logs/judge0-spring.log
   ```

### 문제: HTTP API 타임아웃

**증상:**
```
❌ [123/8826] '문제 제목' 실패: 타임아웃 (30초 초과)
```

**해결:**
1. 서버 성능 확인 (CPU, 메모리)
2. 데이터베이스 연결 확인
3. 스크립트의 타임아웃 값 증가 (30초 → 60초)
4. 배치 크기 줄이기 (한 번에 100개씩 처리)

---

## 📈 성능 최적화

### SQL 방식 최적화

- PostgreSQL `shared_buffers` 증가
- `work_mem` 증가
- 인덱스 생성 지연 (데이터 입력 후 생성)

### HTTP API 방식 최적화

- 병렬 처리 (여러 스크립트 동시 실행)
- 배치 API 사용 (한 번에 여러 문제 전송)
- 대기 시간 조정 (0.1초 → 0.05초)

---

## 📚 참고 자료

- [Flyway 공식 문서](https://flywaydb.org/documentation/)
- [PostgreSQL 성능 튜닝](https://wiki.postgresql.org/wiki/Performance_Optimization)
- [Python requests 라이브러리](https://requests.readthedocs.io/)

---

## ✅ 체크리스트

임포트 전:
- [ ] `baekjoon_problems.json` 파일 확인 (28,377개 문제)
- [ ] `application.yml`에 `placeholder-replacement: false` 설정
- [ ] PostgreSQL 데이터베이스 실행 중
- [ ] Python 3 설치 확인
- [ ] `requests` 패키지 설치 (`pip3 install requests`)

1단계 (SQL):
- [ ] `json_to_sql_filtered.py` 실행
- [ ] `V103__baekjoon_problems_no_math.sql` 생성 확인
- [ ] SQL 파일을 마이그레이션 디렉토리로 이동

2단계 (마이그레이션):
- [ ] Spring Boot 재시작
- [ ] Flyway 마이그레이션 로그 확인
- [ ] 19,551개 문제가 입력되었는지 확인

3단계 (HTTP API):
- [ ] Spring Boot 실행 중 확인
- [ ] `json_to_api.py` 실행
- [ ] 진행률 모니터링
- [ ] 8,826개 문제가 입력되었는지 확인

완료 후:
- [ ] 총 28,377개 문제 확인 (`SELECT COUNT(*) FROM lectures WHERE type = 'PROBLEM'`)
- [ ] 테스트 케이스 확인
- [ ] 실패한 문제 처리 (있다면)
- [ ] 백업 파일 정리

---

**문의:** 문제가 발생하면 로그와 함께 이슈를 제출해주세요.
