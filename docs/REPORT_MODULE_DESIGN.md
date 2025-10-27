# 신고 모듈 설계 문서

## 📋 개요

부적절한 콘텐츠 신고 및 관리자 처리 시스템입니다.

## 🎯 주요 기능

### 1. 신고 접수 (일반 사용자)

- 다양한 콘텐츠 타입 신고 가능
- 신고 사유 선택 및 상세 내용 작성
- 신고 접수 후 관리자 확인 대기

### 2. 관리자 신고 처리

- 신고 목록 조회 및 필터링
- 신고 상세 확인
- 처리 방법 선택 및 실행
- 처리 결과 기록

### 3. 처리 방법별 자동 실행

#### DELETE_CONTENT (콘텐츠 삭제)

```
- 타입별 콘텐츠 삭제:
  - CURRICULUM: Curriculum 삭제
  - LECTURE: Lecture 삭제
  - QUESTION: Question 삭제
  - ANSWER: Answer 삭제
  - COURSE_REVIEW: CourseReview 삭제
```

#### MODIFY_REQUEST (수정 요청)

```
- 대상 사용자에게 알림 전송
- 신고된 콘텐츠 URL 제공
- 수정 기한 설정 (선택)
```

#### WARNING (경고)

```
- 사용자 경고 횟수 증가
- 경고 누적 시 자동 처리 (예: 3회 경고 시 일시 정지)
```

#### MUTE_USER (사용자 일시 정지)

```
- 사용자 계정 일시 정지
- 정지 기간 설정 (1일, 3일, 7일, 30일)
- 정지 기간 동안 로그인 및 콘텐츠 작성 불가
```

#### DELETE_ACCOUNT (계정 탈퇴)

```
- 사용자 계정 삭제
- 관련 콘텐츠 처리 옵션 (보존/삭제/익명화)
```

#### NO_ACTION (조치 없음)

```
- 신고 잘못 접수됨
- 혐의 없음 판단
- 신고자에게 처리 결과 알림
```

## 📊 데이터베이스 설계

### reports 테이블

```sql
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT NOT NULL,          -- 신고자 ID
    target_type VARCHAR(50) NOT NULL,    -- 신고 대상 타입 (CURRICULUM, LECTURE, QUESTION, ANSWER, COURSE_REVIEW)
    target_id BIGINT NOT NULL,           -- 신고 대상 ID
    reason VARCHAR(100) NOT NULL,        -- 신고 사유 (SPAM, ABUSE, INAPPROPRIATE_CONTENT, COPYRIGHT_VIOLATION, ETC)
    content TEXT,                        -- 신고 상세 내용
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- 신고 상태
    processor_id BIGINT,                 -- 처리자 ID (관리자)
    process_action VARCHAR(50),         -- 처리 방법
    process_note TEXT,                   -- 처리 내용
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP
);
```

### users 테이블에 추가 필요

```sql
-- 사용자 일시 정지 정보 추가
ALTER TABLE users ADD COLUMN is_muted BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN muted_until TIMESTAMP;

-- 경고 횟수 추가
ALTER TABLE users ADD COLUMN warning_count INTEGER DEFAULT 0;
```

## 🔐 권한 시스템

### 일반 사용자 (USER)

- 신고 작성
- 본인 신고 조회
- 본인이 작성한 신고 취소 (PENDING 상태만)

### 관리자 (ADMIN)

- 모든 신고 조회 및 처리
- 신고 통계 조회
- 콘텐츠 삭제 권한
- 사용자 정지/탈퇴 권한

## 📝 API 설계

### 1. 신고 작성

**POST** `/api/reports`

```json
{
  "targetType": "QUESTION",
  "targetId": 123,
  "reason": "ABUSE",
  "content": "욕설 및 혐오 표현이 포함되어 있습니다."
}
```

### 2. 관리자 - 신고 목록 조회

**GET** `/api/reports?status=PENDING&targetType=QUESTION&page=0&size=20`

### 3. 관리자 - 신고 처리

**PUT** `/api/reports/{id}/process`

```json
{
  "status": "RESOLVED",
  "processAction": "DELETE_CONTENT",
  "processNote": "욕설로 인해 콘텐츠 삭제 처리"
}
```

### 4. 관리자 - 신고 통계

**GET** `/api/reports/stats`

```json
{
  "totalReports": 150,
  "pendingCount": 25,
  "resolvedCount": 100,
  "rejectedCount": 15,
  "byTargetType": {
    "CURRICULUM": 30,
    "LECTURE": 45,
    "QUESTION": 40,
    "ANSWER": 25,
    "COURSE_REVIEW": 10
  },
  "byStatus": {
    "PENDING": 25,
    "PROCESSING": 10,
    "RESOLVED": 100,
    "REJECTED": 15
  },
  "byReason": {
    "SPAM": 50,
    "ABUSE": 40,
    "INAPPROPRIATE_CONTENT": 35,
    "COPYRIGHT_VIOLATION": 15,
    "OTHER": 10
  }
}
```

### 5. 사용자 - 내 신고 목록

**GET** `/api/users/me/reports`

## 🎨 프론트엔드 UI 제안

### 관리자 대시보드

1. **신고 현황 카드**

   - 오늘의 신고 수
   - 처리 대기 신고 수
   - 이번 주 해결률

2. **신고 목록 테이블**

   - 신고 ID
   - 신고 타입
   - 신고 사유
   - 신고자
   - 신고 일시
   - 상태
   - 처리 버튼

3. **신고 상세 모달**

   - 신고 콘텐츠 미리보기
   - 신고 내용
   - 처리 방법 선택
   - 처리 내용 입력

4. **통계 그래프**
   - 신고 유형별 분포
   - 일별 신고 추이
   - 처리율 추이

## 🚀 구현 단계

### Phase 1: 기본 신고 시스템

1. Report 엔티티 생성
2. Repository, Service, Controller 구현
3. Flyway 마이그레이션
4. 기본 API 구현

### Phase 2: 처리 방법 자동화

1. ProcessAction Enum 정의
2. 처리 방법별 서비스 메서드 구현
3. 사용자 mute 기능 구현
4. 콘텐츠 삭제 자동화

### Phase 3: 관리자 권한 시스템

1. User 테이블에 role 필드 추가
2. 관리자 권한 체크 기능 구현
3. 관리자 전용 API 구현

### Phase 4: 대시보드 & 통계

1. 관리자 대시보드 API 구현
2. 통계 그래프 데이터 API 구현
3. 프론트엔드 연동

## 🔒 보안 고려사항

1. **중복 신고 방지**: 동일 사용자의 동일 콘텐츠 신고 제한
2. **악의적 신고 방지**: 신고 빈도 모니터링
3. **데이터 무결성**: 신고된 콘텐츠 삭제 시 참조 처리
4. **로그 기록**: 모든 처리 로그 저장
