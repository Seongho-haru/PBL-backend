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

### 6. 사용자별 강의 목록 조회

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

### 7. 공개 강의 검색

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
  "summary": "커리큘럼 간단 소개"
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
  "isPublic": false
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

### 5. 사용자별 커리큘럼 목록 조회

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

### 6. 공개 커리큘럼 목록 조회

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

### 7. 커리큘럼 문의 목록 조회 (공개만)

**GET** `/api/curriculums/{curriculumId}/reviews/inquiries?page=0&size=10`

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

### 10. 커리큘럼 평균 평점 조회

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

## 🔗 관련 문서

- [S3 모듈 상세 API](./API_SPECIFICATION_S3.md)
- [Q&A 모듈 상세 API](./API_SPECIFICATION_QNA.md)

---

## 📞 문의사항

API 사용 중 문제가 발생하면 백엔드 개발팀에 문의해주세요.
