# PBL 백엔드 API 명세서 - S3 모듈

## 📋 개요

이 문서는 PBL 백엔드의 S3 모듈 API에 대한 상세 명세서입니다. S3 모듈은 이미지 업로드, 삭제, 통계 등의 기능을 제공합니다.

## 🔗 기본 정보

- **Base URL**: `http://114.201.56.70:2358`
- **API Prefix**: `/api/s3`
- **Content-Type**: `multipart/form-data` (업로드), `application/json` (기타)

## 📁 S3 모듈 API

### 1. 이미지 업로드

**POST** `/api/s3/upload`

이미지 파일을 업로드하고 메타데이터를 저장합니다.

#### 요청 헤더

```
X-User-Id: {사용자ID} (필수)
```

#### 요청 본문 (multipart/form-data)

| 필드명 | 타입   | 필수 | 설명                     |
| ------ | ------ | ---- | ------------------------ |
| file   | File   | ✅   | 업로드할 이미지 파일     |
| folder | String | ❌   | 업로드할 폴더 (선택사항) |

#### 지원 이미지 형식

- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)
- WebP (.webp)
- BMP (.bmp)

#### 파일 크기 제한

- 최대 10MB

#### 응답 (201 Created)

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

#### 에러 응답

- **400 Bad Request**: 잘못된 파일 형식 또는 크기 초과
- **401 Unauthorized**: X-User-Id 헤더 누락
- **500 Internal Server Error**: 서버 내부 오류

---

### 2. 이미지 삭제

**DELETE** `/api/s3/{imageId}`

이미지를 삭제합니다. 본인이 업로드한 이미지만 삭제 가능합니다.

#### 경로 매개변수

| 매개변수 | 타입 | 필수 | 설명             |
| -------- | ---- | ---- | ---------------- |
| imageId  | Long | ✅   | 삭제할 이미지 ID |

#### 요청 헤더

```
X-User-Id: {사용자ID} (필수)
```

#### 응답 (200 OK)

```json
{
  "message": "이미지가 성공적으로 삭제되었습니다."
}
```

#### 에러 응답

- **401 Unauthorized**: X-User-Id 헤더 누락
- **404 Not Found**: 이미지를 찾을 수 없음
- **403 Forbidden**: 본인의 이미지가 아님
- **500 Internal Server Error**: 서버 내부 오류

---

### 3. 이미지 통계 조회

**GET** `/api/s3/stats`

이미지 관련 통계 정보를 조회합니다.

#### 쿼리 매개변수

| 매개변수 | 타입 | 필수 | 설명                        |
| -------- | ---- | ---- | --------------------------- |
| userId   | Long | ❌   | 특정 사용자 통계 (선택사항) |

#### 응답 (200 OK)

```json
{
  "totalImages": 10,
  "totalFileSize": 1048576,
  "averageFileSize": 104857,
  "imagesByUser": 5
}
```

#### 에러 응답

- **500 Internal Server Error**: 서버 내부 오류

---

### 4. 버킷 정책 설정 (관리자)

**POST** `/api/s3/admin/set-public-policy`

버킷을 공개 읽기로 설정합니다.

#### 응답 (200 OK)

```json
{
  "message": "버킷 정책이 공개 읽기로 설정되었습니다."
}
```

#### 에러 응답

- **500 Internal Server Error**: 서버 내부 오류

---

## 🔄 클라이언트 사용 패턴

### 1. 이미지 업로드 후 URL 활용

```javascript
// 1. 이미지 업로드
const formData = new FormData();
formData.append("file", imageFile);

const uploadResponse = await fetch("/api/s3/upload", {
  method: "POST",
  headers: { "X-User-Id": userId },
  body: formData,
});

const uploadResult = await uploadResponse.json();

// 2. 업로드된 이미지 URL 사용
const imageUrl = uploadResult.imageUrl; // http://localhost:9000/pbl-images/...

// 3. HTML에서 이미지 표시
document.getElementById("image").src = imageUrl;

// 4. 마크다운 내용에 이미지 URL 삽입
const markdownContent = `
예시 : 개발 프로세스란?
${imageUrl}
개발 프로세스란, 이러저러한 것이다.
`;
```

### 2. 이미지 삭제

```javascript
// 이미지 ID로 삭제
const deleteResponse = await fetch(`/api/s3/${imageId}`, {
  method: "DELETE",
  headers: { "X-User-Id": userId },
});
```

### 3. 이미지 통계 조회

```javascript
// 전체 통계 조회
const statsResponse = await fetch("/api/s3/stats");
const stats = await statsResponse.json();

// 특정 사용자 통계 조회
const userStatsResponse = await fetch("/api/s3/stats?userId=1");
const userStats = await userStatsResponse.json();
```

---

## 🖼️ 이미지 URL 직접 접근

업로드된 이미지는 다음 URL 패턴으로 직접 접근할 수 있습니다:

```
http://localhost:9000/pbl-images/{storedFilename}
```

예시:

```
http://localhost:9000/pbl-images/20251012_165201_20322763.jpg
```

### 특징:

- **공개 읽기**: 모든 사용자가 접근 가능
- **직접 접근**: API 호출 없이 브라우저에서 바로 접근
- **캐싱**: 브라우저에서 자동으로 캐싱됨
- **CDN 호환**: 필요시 CDN으로 대체 가능

---

## 🛠️ 개발자 도구

### 테스트 페이지

- **파일**: `test-s3.html`
- **기능**: 모든 S3 API 테스트 가능
- **사용법**: 브라우저에서 파일 열기

### MinIO 콘솔

- **URL**: http://localhost:9001
- **계정**: minioadmin / minioadmin123
- **기능**: 업로드된 파일 확인, 버킷 정책 설정

---

## 📝 주의사항

1. **파일 크기 제한**: 최대 10MB
2. **지원 형식**: JPEG, PNG, GIF, WebP, BMP만 지원
3. **권한**: 본인이 업로드한 이미지만 삭제 가능
4. **URL 접근**: MinIO 버킷이 공개 읽기로 설정되어야 함
5. **이미지 크기**: 현재는 0으로 설정됨 (필요시 나중에 추가)

---

## 🔗 관련 모듈

- **강의 모듈**: 강의 내용에 이미지 URL 삽입
- **커리큘럼 모듈**: 커리큘럼 배너/썸네일 이미지
- **사용자 모듈**: 사용자별 이미지 관리

---

## 🚀 개선된 설계 특징

### 1. 단순화된 API

- **업로드**: `POST /api/s3/upload`
- **삭제**: `DELETE /api/s3/{imageId}`
- **통계**: `GET /api/s3/stats`
- **관리**: `POST /api/s3/admin/set-public-policy`

### 2. URL 직접 접근

- API 호출 없이 이미지에 직접 접근 가능
- 프론트엔드에서 더 편리한 사용
- 마크다운 렌더링에 최적화

### 3. 썸네일 제거

- 원본 이미지만 제공
- 코드 단순화
- 강의 내용용 이미지에 최적화

### 4. 공개 읽기

- 모든 사용자가 이미지 조회 가능
- 강의/커리큘럼 공유에 적합
