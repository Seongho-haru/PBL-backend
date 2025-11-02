# PBL Backend API 명세서

## 📋 개요

PBL(Problem-Based Learning) 백엔드 API 명세서입니다. 사용자 인증, 강의 관리, 커리큘럼 관리, 수강 관리, 이미지 저장, Q&A 게시판 기능을 제공합니다.

## 🔗 Base URL

```
http://114.201.56.70:2358
```

## 🔐 인증 방식

모든 API 요청에 `X-User-Id` 헤더를 포함해야 합니다.

```
X-User-Id: {사용자ID}
```

---

## 👤 사용자 관리 API

### 1. 회원가입

**POST** `/api/auth/register`

**Request Body:**

```json
{
  "username": "사용자명",
  "loginId": "로그인ID",
  "password": "비밀번호"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "username": "사용자명",
  "loginId": "로그인ID",
  "createdAt": "2025-01-01T00:00:00"
}
```

**Error Response:**

- `400 Bad Request`: 중복된 로그인ID 또는 유효성 검사 실패
- `500 Internal Server Error`: 서버 오류

### 2. 로그인

**POST** `/api/auth/login`

**Request Body:**

```json
{
  "loginId": "로그인ID",
  "password": "비밀번호"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "username": "사용자명",
  "loginId": "로그인ID",
  "createdAt": "2025-01-01T00:00:00"
}
```

**Error Response:**

- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 로그인 실패 (아이디 없음 또는 비밀번호 틀림)

### 3. 제재된 사용자 목록 조회

**GET** `/api/auth/users/muted`

관리자 전용 API로, 현재 제재 중인 모든 사용자를 조회합니다.

**Headers:**

```
X-User-Id: {관리자ID}
```

**Response (200 OK):**

```json
{
  "success": true,
  "users": [
    {
      "id": 3,
      "username": "김철수",
      "loginId": "kimcs",
      "mutedUntil": "2025-11-03T17:27:53",
      "warningCount": 0,
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "count": 1
}
```

**Error Response:**

- `403 Forbidden`: 관리자 권한 필요
- `500 Internal Server Error`: 서버 오류

### 4. 사용자 제재 해제

**PUT** `/api/auth/users/{userId}/unmute`

관리자 전용 API로, 제재된 사용자의 제재를 해제합니다.

**Headers:**

```
X-User-Id: {관리자ID}
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "사용자 제재가 해제되었습니다."
}
```

**Error Response:**

- `400 Bad Request`: 제재되지 않은 사용자
- `403 Forbidden`: 관리자 권한 필요
- `404 Not Found`: 사용자를 찾을 수 없음
- `500 Internal Server Error`: 서버 오류

---

## 📚 강의 관리 API

### 1. 강의 생성

**POST** `/api/lectures`

**Headers:**

```
X-User-Id: {사용자ID}
Content-Type: application/json
```

**Request Body:**

```json
{
  "title": "강의 제목",
  "description": "강의 설명",
  "type": "MARKDOWN" | "PROBLEM",
  "category": "카테고리",
  "difficulty": "기초" | "중급" | "고급",
  "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표 (선택 사항)",
  "timeLimit": 30,  // 문제 강의인 경우만
  "memoryLimit": 128,  // 문제 강의인 경우만
  "testCases": [  // 문제 강의인 경우만
    {
      "input": "입력값",
      "expectedOutput": "예상출력값"
    }
  ]
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "title": "강의 제목",
  "description": "강의 설명",
  "type": "MARKDOWN",
  "category": "카테고리",
  "difficulty": "기초",
  "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표",
  "timeLimit": null,
  "memoryLimit": null,
  "isPublic": false,
  "testCaseCount": 0,
  "testCases": [],
  "author": {
    "id": 1,
    "username": "작성자명",
    "loginId": "작성자로그인ID"
  },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

### 2. 강의 상세 조회

**GET** `/api/lectures/{id}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "강의 제목",
  "description": "강의 설명",
  "type": "MARKDOWN",
  "category": "카테고리",
  "difficulty": "기초",
  "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표",
  "timeLimit": null,
  "memoryLimit": null,
  "isPublic": false,
  "testCaseCount": 2,
  "testCases": [
    {
      "id": 1,
      "input": "입력값1",
      "expectedOutput": "출력값1"
    }
  ],
  "author": {
    "id": 1,
    "username": "작성자명",
    "loginId": "작성자로그인ID"
  },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

**Error Response:**

- `401 Unauthorized`: 인증 필요
- `403 Forbidden`: 비공개 강의 접근 권한 없음
- `404 Not Found`: 강의 없음

### 3. 강의 수정

**PUT** `/api/lectures/{id}`

**Headers:**

```
X-User-Id: {사용자ID}
Content-Type: application/json
```

**Request Body:**

```json
{
  "title": "수정된 제목",
  "description": "수정된 설명",
  "type": "MARKDOWN",
  "category": "수정된 카테고리",
  "difficulty": "중급",
  "learningObjectives": "수정된 학습 목표 (선택 사항)",
  "timeLimit": 60,
  "memoryLimit": 256
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "수정된 제목",
  "description": "수정된 설명"
  // ... 기타 필드들
}
```

**Error Response:**

- `401 Unauthorized`: 인증 필요
- `403 Forbidden`: 수정 권한 없음
- `404 Not Found`: 강의 없음

### 4. 강의 삭제

**DELETE** `/api/lectures/{id}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
{
  "message": "강의가 성공적으로 삭제되었습니다."
}
```

**Error Response:**

- `401 Unauthorized`: 인증 필요
- `403 Forbidden`: 삭제 권한 없음
- `404 Not Found`: 강의 없음

### 5. 강의 공개/비공개 설정

**PUT** `/api/lectures/{id}/publish`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "강의 제목",
  "isPublic": true
  // ... 기타 필드들
}
```

### 6. 모든 강의 조회

**GET** `/api/lectures`

시스템에 등록된 모든 강의를 최신순으로 조회합니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `isPublic` (optional): 공개 여부 필터 (`true`: 공개만, `false`: 비공개만, `null`: 모두) - 기본값: `null`
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "lectures": [
    {
      "id": 1,
      "title": "강의 제목",
      "description": "강의 설명",
      "type": "MARKDOWN",
      "category": "카테고리",
      "difficulty": "기초",
      "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표",
      "isPublic": true,
      "author": {
        "id": 1,
        "username": "작성자명",
        "loginId": "작성자로그인ID"
      },
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 100,
    "totalPages": 10,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 7. 유형별 강의 조회

**GET** `/api/lectures/type/{type}`

특정 유형의 강의를 조회합니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Path Parameters:**

- `type`: 강의 유형 (`MARKDOWN` 또는 `PROBLEM`)

**Query Parameters:**

- `isPublic` (optional): 공개 여부 필터 (`true`: 공개만, `false`: 비공개만, `null`: 모두) - 기본값: `null`
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "lectures": [
    {
      "id": 1,
      "title": "강의 제목",
      "description": "강의 설명",
      "type": "MARKDOWN",
      "category": "카테고리",
      "difficulty": "기초",
      "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표",
      "isPublic": true,
      "author": {
        "id": 1,
        "username": "작성자명",
        "loginId": "작성자로그인ID"
      },
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 50,
    "totalPages": 5,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 8. 최근 강의 조회

**GET** `/api/lectures/recent`

최근 생성된 강의를 조회합니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `isPublic` (optional): 공개 여부 필터 (`true`: 공개만, `false`: 비공개만, `null`: 모두) - 기본값: `null`
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "lectures": [
    {
      "id": 1,
      "title": "강의 제목",
      "description": "강의 설명",
      "type": "MARKDOWN",
      "category": "카테고리",
      "difficulty": "기초",
      "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표",
      "isPublic": true,
      "author": {
        "id": 1,
        "username": "작성자명",
        "loginId": "작성자로그인ID"
      },
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 30,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 9. 사용자별 강의 목록 조회

**GET** `/api/lectures/user/{userId}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "title": "강의 제목",
    "description": "강의 설명",
    "type": "MARKDOWN",
    "category": "카테고리",
    "difficulty": "기초",
    "isPublic": false,
    "author": {
      "id": 1,
      "username": "작성자명",
      "loginId": "작성자로그인ID"
    },
    "createdAt": "2025-01-01T00:00:00"
  }
]
```

### 10. 공개 강의 검색

**GET** `/api/lectures/public/search?title={제목}&category={카테고리}&difficulty={난이도}&type={유형}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `title` (optional): 제목 검색
- `category` (optional): 카테고리 필터
- `difficulty` (optional): 난이도 필터
- `type` (optional): 강의 유형 필터

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "title": "검색된 강의",
    "description": "강의 설명",
    "type": "MARKDOWN",
    "category": "Frontend",
    "difficulty": "기초",
    "isPublic": true,
    "author": {
      "id": 1,
      "username": "작성자명",
      "loginId": "작성자로그인ID"
    }
  }
]
```

---

## 📖 커리큘럼 관리 API

### 1. 커리큘럼 생성

**POST** `/api/curriculums`

**Headers:**

```
X-User-Id: {사용자ID}
Content-Type: application/json
```

**Request Body:**

```json
{
  "title": "커리큘럼 제목",
  "description": "커리큘럼 설명",
  "isPublic": true,
  "difficulty": "기초",
  "summary": "커리큘럼 간단 소개",
  "learningObjectives": "이 커리큘럼을 통해 달성할 수 있는 학습 목표 (선택 사항)"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "커리큘럼 제목",
  "description": "커리큘럼 설명",
  "isPublic": true,
  "difficulty": "기초",
  "summary": "커리큘럼 간단 소개",
  "learningObjectives": "이 커리큘럼을 통해 달성할 수 있는 학습 목표",
  "averageRating": 0.0,
  "studentCount": 0,
  "totalLectureCount": 0,
  "requiredLectureCount": 0,
  "optionalLectureCount": 0,
  "author": {
    "id": 1,
    "username": "작성자명",
    "loginId": "작성자로그인ID"
  },
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

### 2. 커리큘럼 상세 조회

**GET** `/api/curriculums/{id}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "커리큘럼 제목",
  "description": "커리큘럼 설명",
  "isPublic": true,
  "difficulty": "기초",
  "summary": "커리큘럼 간단 소개",
  "learningObjectives": "이 커리큘럼을 통해 달성할 수 있는 학습 목표",
  "averageRating": 4.5,
  "studentCount": 15,
  "totalLectureCount": 2,
  "requiredLectureCount": 1,
  "optionalLectureCount": 1,
  "author": {
    "id": 1,
    "username": "작성자명",
    "loginId": "작성자로그인ID"
  },
  "lectures": [
    {
      "id": 1,
      "lecture": {
        "id": 1,
        "title": "강의 제목",
        "description": "강의 설명",
        "type": "MARKDOWN",
        "category": "카테고리",
        "difficulty": "기초",
        "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표",
        "isPublic": true,
        "author": {
          "id": 1,
          "username": "작성자명",
          "loginId": "작성자로그인ID"
        }
      },
      "isRequired": true,
      "order": 1
    }
  ],
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

**Error Response:**

- `401 Unauthorized`: 인증 필요
- `403 Forbidden`: 비공개 커리큘럼 접근 권한 없음
- `404 Not Found`: 커리큘럼 없음

### 3. 커리큘럼 수정

**PUT** `/api/curriculums/{id}`

**Headers:**

```
X-User-Id: {사용자ID}
Content-Type: application/json
```

**Request Body:**

```json
{
  "title": "수정된 제목",
  "description": "수정된 설명",
  "isPublic": false,
  "learningObjectives": "수정된 학습 목표 (선택 사항)"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "수정된 제목",
  "description": "수정된 설명",
  "isPublic": false
  // ... 기타 필드들
}
```

**Error Response:**

- `401 Unauthorized`: 인증 필요
- `403 Forbidden`: 수정 권한 없음
- `404 Not Found`: 커리큘럼 없음

### 4. 커리큘럼 삭제

**DELETE** `/api/curriculums/{id}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
{
  "message": "커리큘럼이 성공적으로 삭제되었습니다."
}
```

**Error Response:**

- `401 Unauthorized`: 인증 필요
- `403 Forbidden`: 삭제 권한 없음
- `404 Not Found`: 커리큘럼 없음

### 5. 모든 커리큘럼 조회

**GET** `/api/curriculums`

시스템에 등록된 모든 커리큘럼을 최신순으로 조회합니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `isPublic` (optional): 공개 여부 필터 (`true`: 공개만, `false`: 비공개만, `null`: 모두) - 기본값: `null`
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "curriculums": [
    {
      "id": 1,
      "title": "커리큘럼 제목",
      "description": "커리큘럼 설명",
      "isPublic": true,
      "difficulty": "기초",
      "summary": "커리큘럼 간단 소개",
      "averageRating": 4.5,
      "studentCount": 15,
      "totalLectureCount": 2,
      "requiredLectureCount": 1,
      "optionalLectureCount": 1,
      "author": {
        "id": 1,
        "username": "작성자명",
        "loginId": "작성자로그인ID"
      },
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 50,
    "totalPages": 5,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 6. 커리큘럼 검색

**GET** `/api/curriculums/search?title={제목}`

제목으로 커리큘럼을 검색합니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `title` (required): 검색할 제목 (부분 일치)
- `isPublic` (optional): 공개 여부 필터 (`true`: 공개만, `false`: 비공개만, `null`: 모두) - 기본값: `null`
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "curriculums": [
    {
      "id": 1,
      "title": "검색된 커리큘럼",
      "description": "커리큘럼 설명",
      "isPublic": true,
      "difficulty": "기초",
      "summary": "커리큘럼 간단 소개",
      "averageRating": 4.5,
      "studentCount": 15,
      "totalLectureCount": 2,
      "author": {
        "id": 1,
        "username": "작성자명",
        "loginId": "작성자로그인ID"
      },
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 5,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### 7. 사용자별 커리큘럼 목록 조회

**GET** `/api/curriculums/user/{userId}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "title": "커리큘럼 제목",
    "description": "커리큘럼 설명",
    "isPublic": true,
    "difficulty": "기초",
    "summary": "커리큘럼 간단 소개",
    "averageRating": 4.5,
    "studentCount": 15,
    "totalLectureCount": 2,
    "requiredLectureCount": 1,
    "optionalLectureCount": 1,
    "author": {
      "id": 1,
      "username": "작성자명",
      "loginId": "작성자로그인ID"
    },
    "createdAt": "2025-01-01T00:00:00"
  }
]
```

### 8. 공개 커리큘럼 목록 조회

**GET** `/api/curriculums/public`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "title": "공개 커리큘럼",
    "description": "커리큘럼 설명",
    "isPublic": true,
    "difficulty": "중급",
    "summary": "공개 커리큘럼 간단 소개",
    "averageRating": 4.2,
    "studentCount": 25,
    "totalLectureCount": 3,
    "requiredLectureCount": 2,
    "optionalLectureCount": 1,
    "author": {
      "id": 1,
      "username": "작성자명",
      "loginId": "작성자로그인ID"
    },
    "createdAt": "2025-01-01T00:00:00"
  }
]
```

### 7. 커리큘럼에 강의 추가

**POST** `/api/curriculums/{id}/lectures`

**Headers:**

```
X-User-Id: {사용자ID}
Content-Type: application/json
```

**Request Body:**

```json
{
  "lectureId": 1,
  "isRequired": true,
  "order": 1
}
```

**Response (200 OK):**

```json
{
  "message": "강의가 커리큘럼에 추가되었습니다."
}
```

### 8. 커리큘럼에서 강의 제거

**DELETE** `/api/curriculums/{curriculumId}/lectures/{lectureId}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
{
  "message": "강의가 커리큘럼에서 제거되었습니다."
}
```

### 9. 커리큘럼 강의 순서 변경

**PUT** `/api/curriculums/{id}/lectures/reorder`

**Headers:**

```
X-User-Id: {사용자ID}
Content-Type: application/json
```

**Request Body:**

```json
{
  "lectureOrders": [
    { "lectureId": 1, "order": 1 },
    { "lectureId": 2, "order": 2 },
    { "lectureId": 3, "order": 3 }
  ]
}
```

**Response (200 OK):**

```json
{
  "message": "강의 순서가 성공적으로 변경되었습니다."
}
```

---

## 🔍 공개 강의 검색 API

### 공개 강의 검색 (커리큘럼 모듈)

**GET** `/api/curriculums/lectures/public/search?title={제목}&category={카테고리}&difficulty={난이도}&type={유형}`

**Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "title": "검색된 강의",
    "description": "강의 설명",
    "type": "MARKDOWN",
    "category": "Frontend",
    "difficulty": "기초",
    "isPublic": true,
    "author": {
      "id": 1,
      "username": "작성자명",
      "loginId": "작성자로그인ID"
    }
  }
]
```

---

## 📝 데이터 타입 정의

### 강의 유형 (LectureType)

- `MARKDOWN`: 마크다운 강의
- `PROBLEM`: 문제 강의

### 난이도 (Difficulty)

- `기초`: 초급자용
- `중급`: 중급자용
- `고급`: 고급자용

### HTTP 상태 코드

- `200 OK`: 성공
- `201 Created`: 생성 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 필요
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음
- `500 Internal Server Error`: 서버 오류

---

## 🚨 주의사항

1. **인증 필수**: 모든 API 요청에 `X-User-Id` 헤더가 필요합니다.
2. **권한 체크**:
   - 비공개 강의/커리큘럼은 작성자만 조회 가능
   - 수정/삭제는 작성자만 가능
3. **에러 응답**:
   - 존재하지 않는 리소스: 404 Not Found
   - 권한이 없는 리소스: 403 Forbidden
   - 인증이 필요한 경우: 401 Unauthorized
4. **시퀀스 문제**: 개발 환경에서 더미 데이터 사용 시 ID 충돌이 발생할 수 있습니다.

---

## 🖼️ S3 이미지 저장 API

### 1. 이미지 업로드

**POST** `/api/s3/upload`

**Request Headers:**

```
X-User-Id: {사용자ID}
```

**Request Body (multipart/form-data):**

- `file`: 이미지 파일 (필수)
- `folder`: 업로드 폴더 (선택사항)

**Response (201 Created):**

```json
{
  "id": 1,
  "originalFilename": "example.jpg",
  "storedFilename": "20251012_165201_20322763.jpg",
  "imageUrl": "http://localhost:9000/pbl-images/20251012_165201_20322763.jpg",
  "contentType": "image/jpeg",
  "fileSize": 32776,
  "width": 0,
  "height": 0,
  "uploadedAt": "2025-10-12T16:52:02.025858"
}
```

---

### 2. 이미지 삭제

**DELETE** `/api/s3/{imageId}`

**Request Headers:**

```
X-User-Id: {사용자ID}
```

**Response (200 OK):**

```json
{
  "message": "이미지가 성공적으로 삭제되었습니다."
}
```

---

### 3. 이미지 통계 조회

**GET** `/api/s3/stats`

**Query Parameters:**

- `userId`: 특정 사용자 통계 (선택사항)

**Response (200 OK):**

```json
{
  "totalImages": 10,
  "totalFileSize": 1048576,
  "averageFileSize": 104857,
  "imagesByUser": 5
}
```

---

### 4. 버킷 정책 설정 (관리자)

**POST** `/api/s3/admin/set-public-policy`

**Response (200 OK):**

```json
{
  "message": "버킷 정책이 공개 읽기로 설정되었습니다."
}
```

---

### 5. 이미지 URL 직접 접근

업로드된 이미지는 다음 URL 패턴으로 직접 접근할 수 있습니다:

```
http://localhost:9000/pbl-images/{storedFilename}
```

**예시:**

```
http://localhost:9000/pbl-images/20251012_165201_20322763.jpg
```

**특징:**

- 공개 읽기: 모든 사용자가 접근 가능
- 직접 접근: API 호출 없이 브라우저에서 바로 접근
- 캐싱: 브라우저에서 자동으로 캐싱됨

**에러 응답:**

- **400 Bad Request**: 잘못된 파일 형식 또는 크기 초과
- **401 Unauthorized**: X-User-Id 헤더 누락
- **404 Not Found**: 이미지를 찾을 수 없음 (삭제 API)
- **403 Forbidden**: 본인의 이미지가 아님 (삭제 API)
- **500 Internal Server Error**: 서버 내부 오류

---

## 💬 Q&A 게시판 API

### 1. 질문 생성

**POST** `/api/qna/questions`

**Request Headers:**

```
X-User-Id: 1
Content-Type: application/json
```

**Request Body:**

```json
{
  "title": "Spring Boot 설정 관련 질문",
  "content": "Spring Boot에서 JPA 설정을 어떻게 해야 하나요?",
  "category": "QUESTION",
  "course": "자바스프링",
  "language": "Java"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "title": "Spring Boot 설정 관련 질문",
  "content": "Spring Boot에서 JPA 설정을 어떻게 해야 하나요?",
  "status": "UNRESOLVED",
  "category": "QUESTION",
  "course": "자바스프링",
  "language": "Java",
  "authorName": "김준성",
  "likes": 0,
  "createdAt": "2025-10-16T10:30:00",
  "updatedAt": "2025-10-16T10:30:00",
  "answers": []
}
```

---

### 2. 질문 목록 조회

**GET** `/api/qna/questions`

**Query Parameters:**

- `keyword` (optional): 검색 키워드
- `status` (optional): 질문 상태 (UNRESOLVED, RESOLVED)
- `category` (optional): 질문 카테고리 (QUESTION, TIP, BUG_REPORT, FEATURE_REQUEST, GENERAL)
- `course` (optional): 강의명
- `language` (optional): 프로그래밍 언어
- `authorId` (optional): 작성자 ID
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Spring Boot 설정 관련 질문",
      "status": "UNRESOLVED",
      "category": "QUESTION",
      "course": "자바스프링",
      "language": "Java",
      "authorName": "김준성",
      "commentCount": 2,
      "likes": 5,
      "createdAt": "2025-10-16T10:30:00"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

---

### 3. 질문 상세 조회

**GET** `/api/qna/questions/{questionId}`

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "Spring Boot 설정 관련 질문",
  "content": "Spring Boot에서 JPA 설정을 어떻게 해야 하나요?",
  "status": "UNRESOLVED",
  "category": "QUESTION",
  "course": "자바스프링",
  "language": "Java",
  "authorName": "김준성",
  "likes": 5,
  "createdAt": "2025-10-16T10:30:00",
  "updatedAt": "2025-10-16T10:30:00",
  "answers": [
    {
      "id": 1,
      "content": "application.yml 파일에서 다음과 같이 설정하시면 됩니다...",
      "authorName": "김준성",
      "likes": 3,
      "isAccepted": false,
      "parentAnswerId": null,
      "createdAt": "2025-10-16T10:35:00",
      "updatedAt": "2025-10-16T10:35:00",
      "replies": []
    }
  ]
}
```

---

### 4. 답변 생성

**POST** `/api/qna/answers/questions/{questionId}`

**Request Headers:**

```
X-User-Id: 1
Content-Type: application/json
```

**Request Body:**

```json
{
  "content": "application.yml 파일에서 다음과 같이 설정하시면 됩니다:\n\nspring:\n  datasource:\n    url: jdbc:postgresql://localhost:5432/your_database",
  "parentAnswerId": null
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "content": "application.yml 파일에서 다음과 같이 설정하시면 됩니다:\n\nspring:\n  datasource:\n    url: jdbc:postgresql://localhost:5432/your_database",
  "authorName": "김준성",
  "likes": 0,
  "isAccepted": false,
  "parentAnswerId": null,
  "createdAt": "2025-10-16T10:35:00",
  "updatedAt": "2025-10-16T10:35:00",
  "replies": []
}
```

---

### 5. 답변 채택

**POST** `/api/qna/answers/{answerId}/accept`

**Request Headers:**

```
X-User-Id: 1
```

**Response (200 OK):**

```json
{
  "message": "답변이 채택되었습니다."
}
```

---

### 6. 질문 좋아요

**POST** `/api/qna/questions/{questionId}/like`

**Response (200 OK):**

```json
{
  "message": "좋아요가 추가되었습니다."
}
```

---

### 7. 답변 좋아요

**POST** `/api/qna/answers/{answerId}/like`

**Response (200 OK):**

```json
{
  "message": "좋아요가 추가되었습니다."
}
```

---

### 8. 질문 통계 조회

**GET** `/api/qna/questions/stats`

**Response (200 OK):**

```json
{
  "totalQuestions": 100,
  "unresolvedQuestions": 25,
  "resolvedQuestions": 75,
  "courseStats": [
    ["자바스프링", 45],
    ["React 기초", 30],
    ["Vue.js 기초", 25]
  ],
  "languageStats": [
    ["Java", 50],
    ["JavaScript", 30],
    ["Python", 20]
  ]
}
```

**에러 응답:**

- **400 Bad Request**: 잘못된 요청
- **401 Unauthorized**: X-User-Id 헤더 누락
- **403 Forbidden**: 권한 없음 (본인의 질문/답변이 아님)
- **404 Not Found**: 질문/답변을 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

---

## 📝 커리큘럼 리뷰 & 문의 API

### 1. 리뷰 작성

**POST** `/api/curriculums/{curriculumId}/reviews`

**Request Body:**

```json
{
  "isReview": true,
  "rating": 4.5,
  "content": "강의가 아주 만족스럽습니다!"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "curriculumId": 100,
  "curriculumTitle": "Spring Boot 기초 강의",
  "authorId": 1,
  "authorUsername": "김개발",
  "isReview": true,
  "rating": 4.5,
  "content": "강의가 아주 만족스럽습니다!",
  "isPublic": true,
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

### 2. 문의 작성

**POST** `/api/curriculums/{curriculumId}/reviews/inquiries`

**Request Body:**

```json
{
  "isReview": false,
  "rating": null,
  "content": "강의 자료는 언제 제공되나요?",
  "isPublic": true
}
```

**Response (200 OK):**

```json
{
  "id": 2,
  "curriculumId": 100,
  "curriculumTitle": "Spring Boot 기초 강의",
  "authorId": 1,
  "authorUsername": "김개발",
  "isReview": false,
  "rating": null,
  "content": "강의 자료는 언제 제공되나요?",
  "isPublic": true,
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

### 3. 리뷰 수정

**PUT** `/api/curriculums/{curriculumId}/reviews/{reviewId}`

**Request Body:**

```json
{
  "rating": 5.0,
  "content": "수정된 리뷰 내용입니다."
}
```

### 4. 문의 수정

**PUT** `/api/curriculums/{curriculumId}/reviews/inquiries/{inquiryId}`

**Request Body:**

```json
{
  "content": "수정된 문의 내용입니다.",
  "isPublic": false
}
```

### 5. 리뷰/문의 삭제

**DELETE** `/api/curriculums/{curriculumId}/reviews/{reviewId}`

**Response (204 No Content)**

### 6. 커리큘럼 리뷰 목록 조회

**GET** `/api/curriculums/{curriculumId}/reviews?page=0&size=10`

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": 1,
      "curriculumId": 100,
      "curriculumTitle": "Spring Boot 기초 강의",
      "authorId": 1,
      "authorUsername": "김개발",
      "isReview": true,
      "rating": 4.5,
      "content": "강의가 아주 만족스럽습니다!",
      "isPublic": true,
      "createdAt": "2025-01-01T10:00:00",
      "updatedAt": "2025-01-01T10:00:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "number": 0,
  "size": 10
}
```

### 7. 커리큘럼 문의 목록 조회

**GET** `/api/curriculums/{curriculumId}/reviews/inquiries?page=0&size=10`

커리큘럼의 문의 목록을 조회합니다.

**Request Headers (선택사항):**

```
X-User-Id: 1  // 관리자 또는 커리큘럼 작성자인 경우 비공개 문의도 조회 가능
```

**참고**:

- 일반 사용자: 공개 문의만 조회 가능
- 관리자(userId=1) 또는 커리큘럼 작성자: 공개 및 비공개 문의 모두 조회 가능

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": 2,
      "curriculumId": 100,
      "curriculumTitle": "Spring Boot 기초 강의",
      "authorId": 1,
      "authorUsername": "김개발",
      "isReview": false,
      "rating": null,
      "content": "강의 자료는 언제 제공되나요?",
      "isPublic": true,
      "createdAt": "2025-01-01T10:00:00",
      "updatedAt": "2025-01-01T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### 8. 내 리뷰 조회

**GET** `/api/curriculums/{curriculumId}/reviews/my`

**Response (200 OK):**

```json
{
  "id": 1,
  "curriculumId": 100,
  "curriculumTitle": "Spring Boot 기초 강의",
  "authorId": 1,
  "authorUsername": "김개발",
  "isReview": true,
  "rating": 4.5,
  "content": "강의가 아주 만족스럽습니다!",
  "isPublic": true,
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-01-01T10:00:00"
}
```

### 9. 내 문의 목록 조회

**GET** `/api/curriculums/{curriculumId}/reviews/my/inquiries`

**Response (200 OK):**

```json
[
  {
    "id": 2,
    "curriculumId": 100,
    "curriculumTitle": "Spring Boot 기초 강의",
    "authorId": 1,
    "authorUsername": "김개발",
    "isReview": false,
    "rating": null,
    "content": "강의 자료는 언제 제공되나요?",
    "isPublic": true,
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-01T10:00:00"
  }
]
```

### 10. 리뷰 답글 목록 조회

**GET** `/api/curriculums/{curriculumId}/reviews/{reviewId}/replies`

특정 리뷰에 대한 답글 목록을 조회합니다.

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "inquiryId": 1,
    "authorId": 3,
    "authorUsername": "이선생",
    "content": "좋은 리뷰 감사합니다!",
    "createdAt": "2025-01-01T11:00:00",
    "updatedAt": "2025-01-01T11:00:00"
  }
]
```

**에러 응답:**

- **404 Not Found**: 리뷰를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

### 10-1. 리뷰 답글 단건 조회

**GET** `/api/curriculums/{curriculumId}/reviews/{reviewId}/replies/{replyId}`

특정 답글을 조회합니다.

**Response (200 OK):**

```json
{
  "id": 1,
  "inquiryId": 1,
  "authorId": 3,
  "authorUsername": "이선생",
  "content": "좋은 리뷰 감사합니다!",
  "createdAt": "2025-01-01T11:00:00",
  "updatedAt": "2025-01-01T11:00:00"
}
```

### 10-2. 리뷰 답글 작성

**POST** `/api/curriculums/{curriculumId}/reviews/{reviewId}/replies`

리뷰에 답글을 작성합니다.

**Request Headers:**

```
X-User-Id: 3
Content-Type: application/json
```

**Request Body:**

```json
{
  "content": "좋은 리뷰 감사합니다!"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "inquiryId": 1,
  "authorId": 3,
  "authorUsername": "이선생",
  "content": "좋은 리뷰 감사합니다!",
  "createdAt": "2025-01-01T11:00:00",
  "updatedAt": "2025-01-01T11:00:00"
}
```

**에러 응답:**

- **400 Bad Request**: 잘못된 요청
- **401 Unauthorized**: X-User-Id 헤더 누락
- **403 Forbidden**: 권한 없음 (정지된 사용자)
- **404 Not Found**: 리뷰를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

### 10-3. 리뷰 답글 수정

**PUT** `/api/curriculums/{curriculumId}/reviews/{reviewId}/replies/{replyId}`

작성한 답글을 수정합니다.

**Request Headers:**

```
X-User-Id: 3
Content-Type: application/json
```

**Request Body:**

```json
{
  "content": "수정된 답글 내용입니다."
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "inquiryId": 1,
  "authorId": 3,
  "authorUsername": "이선생",
  "content": "수정된 답글 내용입니다.",
  "createdAt": "2025-01-01T11:00:00",
  "updatedAt": "2025-01-01T13:00:00"
}
```

### 10-4. 리뷰 답글 삭제

**DELETE** `/api/curriculums/{curriculumId}/reviews/{reviewId}/replies/{replyId}`

작성한 답글을 삭제합니다.

**Request Headers:**

```
X-User-Id: 3
```

**Response (204 No Content)**

### 11. 문의 답글 목록 조회

**GET** `/api/curriculums/{curriculumId}/reviews/inquiries/{inquiryId}/replies`

특정 문의에 대한 답글 목록을 조회합니다.

**Request Headers (선택사항):**

```
X-User-Id: 3  // 비공개 문의의 답글 조회 시 필요 (관리자 또는 커리큘럼 작성자)
```

**참고**: 비공개 문의의 답글은 관리자(userId=1) 또는 커리큘럼 작성자만 조회할 수 있습니다.

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "inquiryId": 2,
    "authorId": 3,
    "authorUsername": "이선생",
    "content": "강의 자료는 다음 주에 제공될 예정입니다.",
    "createdAt": "2025-01-01T11:00:00",
    "updatedAt": "2025-01-01T11:00:00"
  },
  {
    "id": 2,
    "inquiryId": 2,
    "authorId": 4,
    "authorUsername": "박강사",
    "content": "추가로 질문이 있으시면 언제든 말씀해주세요.",
    "createdAt": "2025-01-01T12:00:00",
    "updatedAt": "2025-01-01T12:00:00"
  }
]
```

**에러 응답:**

- **403 Forbidden**: 비공개 문의의 답글을 조회할 권한이 없음
- **404 Not Found**: 문의를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

### 11-1. 문의 답글 단건 조회

**GET** `/api/curriculums/{curriculumId}/reviews/inquiries/{inquiryId}/replies/{replyId}`

특정 답글을 조회합니다.

**Request Headers (선택사항):**

```
X-User-Id: 3  // 비공개 문의의 답글 조회 시 필요 (관리자 또는 커리큘럼 작성자)
```

**참고**: 비공개 문의의 답글은 관리자(userId=1) 또는 커리큘럼 작성자만 조회할 수 있습니다.

**Response (200 OK):**

```json
{
  "id": 1,
  "inquiryId": 2,
  "authorId": 3,
  "authorUsername": "이선생",
  "content": "강의 자료는 다음 주에 제공될 예정입니다.",
  "createdAt": "2025-01-01T11:00:00",
  "updatedAt": "2025-01-01T11:00:00"
}
```

**에러 응답:**

- **403 Forbidden**: 비공개 문의의 답글을 조회할 권한이 없음
- **404 Not Found**: 답글을 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

### 12. 문의 답글 작성

**POST** `/api/curriculums/{curriculumId}/reviews/inquiries/{inquiryId}/replies`

문의에 답글을 작성합니다.

**Request Headers:**

```
X-User-Id: 3
Content-Type: application/json
```

**Request Body:**

```json
{
  "content": "강의 자료는 다음 주에 제공될 예정입니다."
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "inquiryId": 2,
  "authorId": 3,
  "authorUsername": "이선생",
  "content": "강의 자료는 다음 주에 제공될 예정입니다.",
  "createdAt": "2025-01-01T11:00:00",
  "updatedAt": "2025-01-01T11:00:00"
}
```

**에러 응답:**

- **400 Bad Request**: 잘못된 요청
- **401 Unauthorized**: X-User-Id 헤더 누락
- **403 Forbidden**: 권한 없음 (정지된 사용자 또는 비공개 문의에 답글 작성 권한 없음)
- **404 Not Found**: 문의를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

**참고**: 비공개 문의에 답글을 작성하려면 관리자(userId=1) 또는 커리큘럼 작성자여야 합니다.

### 13. 문의 답글 수정

**PUT** `/api/curriculums/{curriculumId}/reviews/inquiries/{inquiryId}/replies/{replyId}`

작성한 답글을 수정합니다.

**Request Headers:**

```
X-User-Id: 3
Content-Type: application/json
```

**Request Body:**

```json
{
  "content": "수정된 답글 내용입니다."
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "inquiryId": 2,
  "authorId": 3,
  "authorUsername": "이선생",
  "content": "수정된 답글 내용입니다.",
  "createdAt": "2025-01-01T11:00:00",
  "updatedAt": "2025-01-01T13:00:00"
}
```

**에러 응답:**

- **401 Unauthorized**: X-User-Id 헤더 누락
- **403 Forbidden**: 권한 없음 (본인이 작성한 답글이 아님)
- **404 Not Found**: 답글을 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

### 14. 문의 답글 삭제

**DELETE** `/api/curriculums/{curriculumId}/reviews/inquiries/{inquiryId}/replies/{replyId}`

작성한 답글을 삭제합니다.

**Request Headers:**

```
X-User-Id: 3
```

**Response (204 No Content)**

**에러 응답:**

- **401 Unauthorized**: X-User-Id 헤더 누락
- **403 Forbidden**: 권한 없음 (본인이 작성한 답글이 아님)
- **404 Not Found**: 답글을 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

### 15. 커리큘럼 평균 평점 조회

**GET** `/api/curriculums/{curriculumId}/reviews/average-rating`

**Response (200 OK):**

```json
{
  "curriculumId": 100,
  "averageRating": 4.5,
  "reviewCount": 23
}
```

**에러 응답:**

- **400 Bad Request**: 잘못된 요청 (예: 리뷰에 별점이 없음)
- **401 Unauthorized**: X-User-Id 헤더 누락
- **403 Forbidden**: 권한 없음 (본인이 작성한 리뷰/문의가 아님)
- **404 Not Found**: 리뷰/문의를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

**주요 특징:**

- **리뷰**: 별점 있음, 항상 공개
- **문의**: 별점 없음, 공개/비공개 선택 가능 (작성자와 관리자만 비공개 문의 볼 수 있음)

---

## 🚨 신고 관리 API

### 1. 신고 작성

**POST** `/api/reports`

**Request Body:**

```json
{
  "targetType": "QUESTION",
  "targetId": 123,
  "reason": "ABUSE",
  "content": "욕설 및 혐오 표현이 포함되어 있습니다."
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "reporterId": 2,
  "reporterUsername": "김사용",
  "targetType": "QUESTION",
  "targetId": 123,
  "reason": "ABUSE",
  "content": "욕설 및 혐오 표현이 포함되어 있습니다.",
  "status": "PENDING",
  "createdAt": "2025-01-01T10:00:00"
}
```

### 2. 신고 목록 조회 (관리자 전용)

**GET** `/api/reports?status=PENDING&targetType=QUESTION&page=0&size=20`

**Response (200 OK):**

```json
{
  "content": [
    {
      "id": 1,
      "reporterId": 2,
      "reporterUsername": "김사용",
      "targetType": "QUESTION",
      "targetId": 123,
      "reason": "ABUSE",
      "content": "욕설 및 혐오 표현이 포함되어 있습니다.",
      "status": "PENDING",
      "createdAt": "2025-01-01T10:00:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 2,
  "number": 0,
  "size": 20
}
```

### 3. 신고 처리 (관리자 전용)

**PUT** `/api/reports/{id}/process`

**Request Body:**

```json
{
  "status": "RESOLVED",
  "processAction": "MUTE_USER",
  "processNote": "반복적인 부적절한 행동으로 인해 7일간 정지"
}
```

**처리 방법 (processAction):**

- `DELETE_CONTENT`: 콘텐츠 삭제
- `MODIFY_REQUEST`: 수정 요청
- `WARNING`: 경고 (3회 시 자동 정지)
- `MUTE_USER`: 사용자 일시 정지
- `DELETE_ACCOUNT`: 계정 탈퇴
- `NO_ACTION`: 조치 없음
- `OTHER`: 기타

**응답:**

```
403 Forbidden: 관리자 권한 필요 (user ID가 1이 아닌 경우)
```

### 4. 신고 통계 조회 (관리자 전용)

**GET** `/api/reports/stats`

**Response (200 OK):**

```json
{
  "totalReports": 150,
  "pendingCount": 25,
  "processingCount": 10,
  "resolvedCount": 100,
  "rejectedCount": 15,
  "byTargetType": {
    "CURRICULUM": 30,
    "LECTURE": 45,
    "QUESTION": 40,
    "ANSWER": 25,
    "COURSE_REVIEW": 10
  },
  "byReason": {
    "SPAM": 50,
    "ABUSE": 40,
    "INAPPROPRIATE_CONTENT": 35,
    "COPYRIGHT_VIOLATION": 15,
    "OTHER": 10
  },
  "byProcessAction": {
    "DELETE_CONTENT": 60,
    "WARNING": 30,
    "MUTE_USER": 10
  }
}
```

### 5. 내 신고 목록 조회

**GET** `/api/reports/my`

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "reporterId": 2,
    "reporterUsername": "김사용",
    "targetType": "QUESTION",
    "targetId": 123,
    "reason": "ABUSE",
    "content": "욕설 및 혐오 표현이 포함되어 있습니다.",
    "status": "PENDING",
    "createdAt": "2025-01-01T10:00:00"
  }
]
```

### 6. 신고 취소

**DELETE** `/api/reports/{id}`

**Response (204 No Content)**

**주의:** PENDING 상태의 신고만 취소 가능

**에러 응답:**

- **400 Bad Request**: 잘못된 요청
- **401 Unauthorized**: X-User-Id 헤더 누락
- **403 Forbidden**: 권한 없음 (관리자 전용 API 또는 본인 신고 아님)
- **404 Not Found**: 신고를 찾을 수 없음
- **409 Conflict**: 이미 처리 중인 신고
- **500 Internal Server Error**: 서버 내부 오류

**주의사항:**

- **정지된 사용자**: 모든 콘텐츠 생성/수정/삭제가 차단됩니다
  - 정지 기간 동안: 로그인 불가, 콘텐츠 작성 불가, 수강 불가
  - 정지 해제 자동: `mutedUntil` 시간 이후 자동 해제
  - 경고 3회: 자동 1일 정지
- **관리자 권한**: User ID가 1인 경우만 관리자로 간주
- **중복 신고 방지**: 동일 사용자의 동일 콘텐츠 중복 신고 불가

---

## 🔍 통합 검색 API

### 1. 통합 검색

**GET** `/api/search`

커리큘럼과 강의를 동시에 검색합니다. 공개된 콘텐츠만 검색됩니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `title` (optional): 검색할 제목 (부분 일치)
- `category` (optional): 카테고리 필터 (강의만 적용)
- `difficulty` (optional): 난이도 필터 (강의만 적용) - `기초`, `중급`, `고급`
- `type` (optional): 강의 유형 필터 (강의만 적용) - `MARKDOWN`, `PROBLEM`
- `isPublic` (optional): 공개 여부 필터 (`true`: 공개만, `false`: 비공개만, `null`: 모두) - 기본값: `true` (공개만 검색)
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "curriculums": {
    "curriculums": [
      {
        "id": 1,
        "title": "검색된 커리큘럼",
        "description": "커리큘럼 설명",
        "isPublic": true,
        "difficulty": "기초",
        "summary": "커리큘럼 간단 소개",
        "learningObjectives": "이 커리큘럼을 통해 달성할 수 있는 학습 목표",
        "averageRating": 4.5,
        "studentCount": 15,
        "totalLectureCount": 2,
        "author": {
          "id": 1,
          "username": "작성자명",
          "loginId": "작성자로그인ID"
        },
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "meta": {
      "currentPage": 0,
      "totalElements": 1,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  },
  "lectures": {
    "lectures": [
      {
        "id": 1,
        "title": "검색된 강의",
        "description": "강의 설명",
        "type": "MARKDOWN",
        "category": "Frontend",
        "difficulty": "기초",
        "learningObjectives": "이 강의를 통해 달성할 수 있는 학습 목표",
        "isPublic": true,
        "author": {
          "id": 1,
          "username": "작성자명",
          "loginId": "작성자로그인ID"
        },
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "meta": {
      "currentPage": 0,
      "totalElements": 1,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

**Error Response:**

- `400 Bad Request`: 잘못된 요청
- `500 Internal Server Error`: 서버 오류

**참고:**

- `title` 파라미터가 없거나 비어있으면 빈 결과를 반환합니다.
- `category`, `difficulty`, `type` 파라미터는 강의 검색에만 적용됩니다.
- `isPublic` 파라미터가 `null`이면 기본적으로 공개 콘텐츠만 검색합니다 (기존 동작 유지).
- 커리큘럼과 강의는 각각 독립적으로 페이징됩니다.

---

## 🎯 통합 추천 API

### 1. 통합 추천 (커리큘럼 + 강의 혼합)

**GET** `/api/recommendations/unified`

공개된 커리큘럼과 강의를 점수 기준으로 혼합하여 추천합니다. 사용자의 수강 이력과 선호도를 분석하여 개인화된 추천을 제공합니다. 커리큘럼과 강의가 점수 기준으로 정렬되어 혼합되어 반환됩니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "recommendations": [
    {
      "type": "CURRICULUM",
      "id": 1,
      "title": "커리큘럼 제목",
      "description": "커리큘럼 간단 소개",
      "category": "프로그래밍",
      "difficulty": "기초",
      "recommendationScore": 85.5,
      "recommendationReason": "당신이 좋아하는 카테고리, 관심 있는 주제",
      "tags": ["Python", "기초"],
      "averageRating": 4.5,
      "studentCount": 15,
      "authorName": "작성자명",
      "thumbnailImageUrl": "https://example.com/image.jpg"
    },
    {
      "type": "LECTURE",
      "id": 5,
      "title": "강의 제목",
      "description": "강의 설명",
      "category": "프로그래밍",
      "difficulty": "기초",
      "recommendationScore": 80.0,
      "recommendationReason": "당신이 좋아하는 카테고리, 적합한 난이도",
      "lectureType": "PROBLEM"
    },
    {
      "type": "CURRICULUM",
      "id": 3,
      "title": "또 다른 커리큘럼",
      "description": "커리큘럼 설명",
      "category": "알고리즘",
      "difficulty": "중급",
      "recommendationScore": 75.0,
      "recommendationReason": "관심 있는 주제",
      "tags": ["알고리즘", "자료구조"],
      "averageRating": 4.0,
      "studentCount": 10,
      "authorName": "작성자명2",
      "thumbnailImageUrl": null
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 50,
    "totalPages": 5,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Response 필드 설명:**

- `type`: 콘텐츠 타입 (`"CURRICULUM"` 또는 `"LECTURE"`)
- `id`: 커리큘럼 또는 강의 ID
- `title`: 제목
- `description`: 설명 (커리큘럼의 경우 summary, 강의의 경우 description)
- `category`: 카테고리
- `difficulty`: 난이도
- `recommendationScore`: 추천 점수 (0 이상)
- `recommendationReason`: 추천 이유

**커리큘럼 전용 필드:**

- `tags`: 태그 배열
- `averageRating`: 평균 평점
- `studentCount`: 수강생 수
- `authorName`: 작성자명
- `thumbnailImageUrl`: 썸네일 이미지 URL

**강의 전용 필드:**

- `lectureType`: 강의 유형 (`"MARKDOWN"` 또는 `"PROBLEM"`)

**Error Response:**

- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: X-User-Id 헤더 누락
- `500 Internal Server Error`: 서버 오류

**참고:**

- 공개된 커리큘럼과 강의만 추천됩니다.
- 점수가 0보다 큰 콘텐츠만 추천됩니다.
- 이미 수강 중인 커리큘럼과 학습한 강의는 제외됩니다.
- 커리큘럼과 강의가 4개씩 교차로 배치되어 반환됩니다 (커리큘럼 우선).
- 신규 사용자(수강 이력 없음)의 경우 기본 추천 점수로 계산됩니다.
  - 추천 키워드: 파이썬, C, 알고리즘, 기초, 프로그래밍 등
  - 기초 난이도 콘텐츠에 보너스 점수가 추가됩니다.
- 추천 점수는 사용자의 수강 이력, 카테고리, 태그, 난이도, 평점 등을 기반으로 계산됩니다.
- 응답에 페이지네이션 메타데이터(`meta`)가 포함되어 있습니다.
- 성능 최적화: 필요한 만큼만 상위 항목을 선택하여 처리하며, 병렬 처리와 캐싱을 통해 성능이 향상되었습니다.

### 2. 개인화된 커리큘럼 추천

**GET** `/api/recommendations/curriculums`

사용자의 수강 이력과 선호도를 기반으로 커리큘럼을 추천합니다. 신규 사용자(수강 이력이 없는 경우)는 기본 추천(파이썬, C, 알고리즘 기초 등)을 받습니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "curriculums": [
    {
      "id": 1,
      "title": "커리큘럼 제목",
      "description": "커리큘럼 설명",
      "category": "프로그래밍",
      "difficulty": "기초",
      "recommendationScore": 85.5,
      "recommendationReason": "당신이 좋아하는 카테고리, 관심 있는 주제",
      "averageRating": 4.5,
      "studentCount": 15,
      "tags": ["Python", "기초"],
      "author": {
        "id": 1,
        "username": "작성자명",
        "loginId": "작성자로그인ID"
      }
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 20,
    "totalPages": 2,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Error Response:**

- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: X-User-Id 헤더 누락
- `500 Internal Server Error`: 서버 오류

**참고:**

- 이미 수강 중인 커리큘럼은 추천에서 제외됩니다.
- 신규 사용자(수강 이력 없음)의 경우 기본 추천 점수로 계산됩니다.
  - 추천 키워드: 파이썬, C, 알고리즘, 기초, 프로그래밍 등
  - 기초 난이도 커리큘럼에 보너스 점수가 추가됩니다.
- 추천 점수는 사용자의 수강 이력, 카테고리, 태그, 난이도, 평점 등을 기반으로 계산됩니다.
- 점수가 0보다 큰 커리큘럼만 추천됩니다.
- 응답에 페이지네이션 메타데이터(`meta`)가 포함되어 있습니다.
- 성능 최적화: 필요한 만큼만 상위 항목을 선택하여 처리합니다.

### 3. 개인화된 강의 추천

**GET** `/api/recommendations/lectures`

사용자의 수강 이력과 선호도를 기반으로 강의를 추천합니다. 신규 사용자(수강 이력이 없는 경우)는 기본 추천(파이썬, C, 알고리즘 기초 등)을 받습니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)

**Response (200 OK):**

```json
{
  "lectures": [
    {
      "id": 5,
      "title": "강의 제목",
      "description": "강의 설명",
      "type": "PROBLEM",
      "category": "프로그래밍",
      "difficulty": "기초",
      "recommendationScore": 80.0,
      "recommendationReason": "당신이 좋아하는 카테고리, 적합한 난이도",
      "author": {
        "id": 2,
        "username": "작성자명2",
        "loginId": "작성자로그인ID2"
      }
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 30,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Response 필드 설명:**

- `id`: 강의 ID
- `title`: 제목
- `description`: 설명
- `type`: 강의 유형 (`"MARKDOWN"` 또는 `"PROBLEM"`)
- `category`: 카테고리
- `difficulty`: 난이도
- `recommendationScore`: 추천 점수 (0 이상)
- `recommendationReason`: 추천 이유
- `author`: 작성자 정보

**Error Response:**

- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: X-User-Id 헤더 누락
- `500 Internal Server Error`: 서버 오류

**참고:**

- 이미 학습한 강의는 추천에서 제외됩니다.
- 신규 사용자(수강 이력 없음)의 경우 기본 추천 점수로 계산됩니다.
  - 추천 키워드: 파이썬, C, 알고리즘, 기초, 프로그래밍 등
  - 기초 난이도 강의에 보너스 점수가 추가됩니다.
- 추천 점수는 사용자의 수강 이력, 카테고리, 태그, 난이도 등을 기반으로 계산됩니다.
- 점수가 0보다 큰 강의만 추천됩니다.
- 응답에 페이지네이션 메타데이터(`meta`)가 포함되어 있습니다.
- 성능 최적화: 필요한 만큼만 상위 항목을 선택하여 처리합니다.

### 4. 유사 문제 강의 추천

**GET** `/api/recommendations/similar-lectures?lectureId={강의ID}`

현재 풀고 있는 문제와 유사한 강의를 추천합니다.

**Headers:**

```
X-User-Id: {사용자ID}
```

**Query Parameters:**

- `lectureId` (required): 기준 강의 ID
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 5)

**Response (200 OK):**

```json
{
  "lectures": [
    {
      "id": 5,
      "title": "유사한 문제 강의",
      "description": "강의 설명",
      "type": "PROBLEM",
      "category": "알고리즘",
      "difficulty": "중급",
      "recommendationScore": 90.0,
      "recommendationReason": "같은 카테고리, 같은 난이도",
      "author": {
        "id": 2,
        "username": "작성자명2",
        "loginId": "작성자로그인ID2"
      }
    }
  ],
  "meta": {
    "currentPage": 0,
    "totalElements": 8,
    "totalPages": 2,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Error Response:**

- `400 Bad Request`: 기준 강의가 문제 강의가 아님
- `401 Unauthorized`: X-User-Id 헤더 누락
- `404 Not Found`: 강의를 찾을 수 없음
- `500 Internal Server Error`: 서버 오류

**참고:**

- 기준 강의와 사용자가 이미 학습한 강의는 추천에서 제외됩니다.
- 추천 점수는 카테고리, 난이도, 태그, 제목 유사도 등을 기반으로 계산됩니다.
- 점수가 0보다 큰 강의만 추천됩니다.
- 응답에 페이지네이션 메타데이터(`meta`)가 포함되어 있습니다.
- 성능 최적화: 필요한 만큼만 상위 항목을 선택하여 처리합니다.

---

## 🔗 관련 문서

- [S3 모듈 상세 API](./API_SPECIFICATION_S3.md)
- [Q&A 모듈 상세 API](./API_SPECIFICATION_QNA.md)

---

## 📞 문의사항

API 사용 중 문제가 발생하면 백엔드 개발팀에 문의해주세요.
