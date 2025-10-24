# Q&A Management API 명세서

## 개요

Q&A 게시판 시스템을 위한 RESTful API입니다. 질문과 답변을 관리하며, 검색, 필터링, 좋아요, 채택 등의 기능을 제공합니다.

## 기본 정보

- **Base URL**: `http://localhost:2358`
- **Content-Type**: `application/json`
- **인증**: X-User-Id 헤더를 통한 사용자 식별

## 질문 관리 API

### 1. 질문 생성

**POST** `/api/qna/questions`

새로운 질문을 생성합니다.

#### 요청 헤더

```
X-User-Id: 1
Content-Type: application/json
```

#### 요청 본문

```json
{
  "title": "Spring Boot 설정 관련 질문",
  "content": "Spring Boot에서 JPA 설정을 어떻게 해야 하나요?",
  "category": "QUESTION",
  "course": "자바스프링",
  "language": "Java"
}
```

#### 응답 (201 Created)

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

### 2. 질문 목록 조회

**GET** `/api/qna/questions`

질문 목록을 검색 조건에 따라 조회합니다.

#### 쿼리 파라미터

- `keyword` (optional): 검색 키워드
- `status` (optional): 질문 상태 (UNRESOLVED, RESOLVED)
- `category` (optional): 질문 카테고리 (QUESTION, TIP, BUG_REPORT, FEATURE_REQUEST, GENERAL)
- `course` (optional): 강의명
- `language` (optional): 프로그래밍 언어
- `authorId` (optional): 작성자 ID
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)
- `sort` (optional): 정렬 기준 (기본값: createdAt,desc)

#### 응답 (200 OK)

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

### 3. 질문 상세 조회

**GET** `/api/qna/questions/{questionId}`

질문의 상세 정보와 답변들을 조회합니다.

#### 응답 (200 OK)

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

### 4. 질문 수정

**PUT** `/api/qna/questions/{questionId}`

질문을 수정합니다. 본인이 작성한 질문만 수정 가능합니다.

#### 요청 헤더

```
X-User-Id: 1
Content-Type: application/json
```

#### 요청 본문

```json
{
  "title": "수정된 질문 제목",
  "content": "수정된 질문 내용",
  "category": "TIP",
  "course": "자바스프링 고급",
  "language": "Java"
}
```

#### 응답 (200 OK)

```json
{
  "id": 1,
  "title": "수정된 질문 제목",
  "content": "수정된 질문 내용",
  "status": "UNRESOLVED",
  "category": "TIP",
  "course": "자바스프링 고급",
  "language": "Java",
  "authorName": "김준성",
  "likes": 5,
  "createdAt": "2025-10-16T10:30:00",
  "updatedAt": "2025-10-16T10:45:00",
  "answers": []
}
```

### 5. 질문 삭제

**DELETE** `/api/qna/questions/{questionId}`

질문을 삭제합니다. 본인이 작성한 질문만 삭제 가능합니다.

#### 요청 헤더

```
X-User-Id: 1
```

#### 응답 (200 OK)

```json
{
  "message": "질문이 성공적으로 삭제되었습니다."
}
```

### 6. 질문 좋아요

**POST** `/api/qna/questions/{questionId}/like`

질문에 좋아요를 추가합니다.

#### 응답 (200 OK)

```json
{
  "message": "좋아요가 추가되었습니다."
}
```

### 7. 질문 좋아요 취소

**DELETE** `/api/qna/questions/{questionId}/like`

질문의 좋아요를 취소합니다.

#### 응답 (200 OK)

```json
{
  "message": "좋아요가 취소되었습니다."
}
```

### 8. 질문 해결 처리

**PUT** `/api/qna/questions/{questionId}/resolve`

질문을 해결 상태로 변경합니다.

#### 요청 헤더

```
X-User-Id: 1
```

#### 응답 (200 OK)

```json
{
  "message": "질문이 해결 상태로 변경되었습니다."
}
```

### 9. 인기 질문 조회

**GET** `/api/qna/questions/popular`

좋아요가 많은 인기 질문들을 조회합니다.

#### 응답 (200 OK)

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
      "likes": 15,
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

### 10. 질문 통계 조회

**GET** `/api/qna/questions/stats`

질문 관련 통계 정보를 조회합니다.

#### 응답 (200 OK)

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

## 답변 관리 API

### 1. 답변 생성

**POST** `/api/qna/answers/questions/{questionId}`

질문에 답변을 작성합니다.

#### 요청 헤더

```
X-User-Id: 1
Content-Type: application/json
```

#### 요청 본문

```json
{
  "content": "application.yml 파일에서 다음과 같이 설정하시면 됩니다:\n\nspring:\n  datasource:\n    url: jdbc:postgresql://localhost:5432/your_database",
  "parentAnswerId": null
}
```

#### 응답 (201 Created)

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

### 2. 답글 생성

**POST** `/api/qna/answers/questions/{questionId}`

답변에 답글을 작성합니다.

#### 요청 본문

```json
{
  "content": "감사합니다! 설정을 적용해보겠습니다.",
  "parentAnswerId": 1
}
```

### 3. 답변 수정

**PUT** `/api/qna/answers/{answerId}`

답변을 수정합니다. 본인이 작성한 답변만 수정 가능합니다.

#### 요청 헤더

```
X-User-Id: 1
Content-Type: application/json
```

#### 요청 본문

```json
{
  "content": "수정된 답변 내용입니다."
}
```

#### 응답 (200 OK)

```json
{
  "id": 1,
  "content": "수정된 답변 내용입니다.",
  "authorName": "김준성",
  "likes": 3,
  "isAccepted": false,
  "parentAnswerId": null,
  "createdAt": "2025-10-16T10:35:00",
  "updatedAt": "2025-10-16T10:50:00",
  "replies": []
}
```

### 4. 답변 삭제

**DELETE** `/api/qna/answers/{answerId}`

답변을 삭제합니다. 본인이 작성한 답변만 삭제 가능합니다.

#### 요청 헤더

```
X-User-Id: 1
```

#### 응답 (200 OK)

```json
{
  "message": "답변이 성공적으로 삭제되었습니다."
}
```

### 5. 질문별 답변 조회

**GET** `/api/qna/answers/questions/{questionId}`

특정 질문의 모든 답변을 조회합니다.

#### 응답 (200 OK)

```json
[
  {
    "id": 1,
    "content": "application.yml 파일에서 다음과 같이 설정하시면 됩니다...",
    "authorName": "김준성",
    "likes": 3,
    "isAccepted": false,
    "parentAnswerId": null,
    "createdAt": "2025-10-16T10:35:00",
    "updatedAt": "2025-10-16T10:35:00",
    "replies": [
      {
        "id": 2,
        "content": "감사합니다! 설정을 적용해보겠습니다.",
        "authorName": "김준성",
        "likes": 1,
        "isAccepted": false,
        "parentAnswerId": 1,
        "createdAt": "2025-10-16T10:40:00",
        "updatedAt": "2025-10-16T10:40:00",
        "replies": []
      }
    ]
  }
]
```

### 6. 답변 좋아요

**POST** `/api/qna/answers/{answerId}/like`

답변에 좋아요를 추가합니다.

#### 응답 (200 OK)

```json
{
  "message": "좋아요가 추가되었습니다."
}
```

### 7. 답변 좋아요 취소

**DELETE** `/api/qna/answers/{answerId}/like`

답변의 좋아요를 취소합니다.

#### 응답 (200 OK)

```json
{
  "message": "좋아요가 취소되었습니다."
}
```

### 8. 답변 채택

**POST** `/api/qna/answers/{answerId}/accept`

답변을 채택합니다. 질문 작성자만 채택 가능합니다.

#### 요청 헤더

```
X-User-Id: 1
```

#### 응답 (200 OK)

```json
{
  "message": "답변이 채택되었습니다."
}
```

### 9. 답변 채택 취소

**DELETE** `/api/qna/answers/{answerId}/accept`

답변의 채택을 취소합니다. 질문 작성자만 취소 가능합니다.

#### 요청 헤더

```
X-User-Id: 1
```

#### 응답 (200 OK)

```json
{
  "message": "답변 채택이 취소되었습니다."
}
```

### 10. 답변 통계 조회

**GET** `/api/qna/answers/stats`

답변 관련 통계 정보를 조회합니다.

#### 응답 (200 OK)

```json
{
  "totalAnswers": 250
}
```

## 에러 응답

### 400 Bad Request

```json
{
  "error": "잘못된 요청입니다."
}
```

### 401 Unauthorized

```json
{
  "error": "X-User-Id 헤더가 필요합니다."
}
```

### 403 Forbidden

```json
{
  "error": "권한이 없습니다."
}
```

### 404 Not Found

```json
{
  "error": "요청한 리소스를 찾을 수 없습니다."
}
```

### 500 Internal Server Error

```json
{
  "error": "서버 내부 오류가 발생했습니다."
}
```

## 데이터 모델

### QuestionStatus

- `UNRESOLVED`: 미해결
- `RESOLVED`: 해결

### QuestionCategory

- `QUESTION`: 질문
- `TIP`: 팁
- `BUG_REPORT`: 버그신고
- `FEATURE_REQUEST`: 기능요청
- `GENERAL`: 일반

## 페이지네이션

모든 목록 조회 API는 Spring Data의 Pageable을 사용하여 페이지네이션을 지원합니다.

- `page`: 페이지 번호 (0부터 시작)
- `size`: 페이지 크기 (기본값: 20)
- `sort`: 정렬 기준 (예: `createdAt,desc`)

## 검색 기능

질문 목록 조회 API는 다음과 같은 검색 기능을 제공합니다:

- **키워드 검색**: 제목과 내용에서 키워드 검색
- **상태별 필터링**: 해결/미해결 상태별 필터링
- **카테고리별 필터링**: 질문 유형별 필터링
- **강의별 필터링**: 특정 강의의 질문만 조회
- **언어별 필터링**: 특정 프로그래밍 언어의 질문만 조회
- **작성자별 필터링**: 특정 사용자의 질문만 조회
