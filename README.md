# PBL Backend - AI 기반 온라인 코딩 교육 플랫폼

PBL(Problem-Based Learning)은 AI 어시스턴트가 탑재된 온라인 코딩 교육 플랫폼입니다. Judge0 코드 실행 엔진을 기반으로 하여, 강의 관리, 커리큘럼 구성, 자동 채점, AI 기반 코드 해설 등의 기능을 제공합니다.

## 🚀 주요 기능

### 📚 교육 관리 시스템

- **강의 관리**: 마크다운 기반 이론 강의와 코딩 문제 강의 통합 관리
- **커리큘럼 구성**: 강의들을 체계적으로 묶어 학습 경로 제공
- **수강 신청**: 학생들의 커리큘럼 수강 및 진도 관리
- **진도 추적**: 강의별 수강 상태 및 완료율 관리

### 💻 코드 실행 및 채점

- **Judge0 엔진**: 다양한 프로그래밍 언어 지원 (Python, Java, C++, JavaScript 등)
- **자동 채점**: 테스트케이스 기반 자동 채점 시스템
- **실시간 피드백**: 코드 실행 결과 즉시 확인
- **리소스 제한**: CPU, 메모리, 시간 제한 설정 가능

### 🤖 AI 어시스턴트

- **코드 해설**: LangChain4j 기반 AI가 제출된 코드 분석 및 해설
- **스트리밍 응답**: SSE(Server-Sent Events)를 통한 실시간 AI 응답
- **벡터 검색**: Qdrant를 활용한 유사 문제 및 참고 자료 추천
- **도구 통합**: AI가 강의, 커리큘럼, 제출 이력 등을 조회하며 맥락 있는 답변 제공

### 📝 커뮤니티 기능

- **Q&A 시스템**: 강의별 질문과 답변
- **코스 리뷰**: 수강생들의 커리큘럼 평가 및 피드백
- **신고 시스템**: 부적절한 콘텐츠 관리
- **추천 시스템**: 사용자 선호도 기반 강의 추천

## 🛠️ 기술 스택

### Backend Framework

- **Java 17** - 최신 LTS 버전
- **Spring Boot 3.2.0** - 웹 애플리케이션 프레임워크
- **Spring Data JPA** - 데이터베이스 추상화 계층
- **Spring WebFlux** - 리액티브 스트리밍 지원

### Database & Storage

- **PostgreSQL 16** - 메인 데이터베이스
- **Redis 7** - 캐싱 및 작업 큐
- **Qdrant** - 벡터 데이터베이스 (AI 검색용)
- **MinIO** - S3 호환 객체 스토리지
- **Flyway** - 데이터베이스 마이그레이션 도구

### AI & ML

- **LangChain4j 1.7.1** - Java용 LLM 프레임워크
- **OpenAI GPT-4o-mini** - AI 모델
- **text-embedding-3-small** - 텍스트 임베딩 모델
- **Qdrant** - 벡터 유사도 검색

### Code Execution

- **Judge0** - 샌드박스 코드 실행 엔진
- **Docker Java API** - 컨테이너 관리
- **JobRunr** - 백그라운드 작업 스케줄링

### Monitoring & Documentation

- **Spring Actuator** - 애플리케이션 모니터링
- **Prometheus** - 메트릭 수집
- **SpringDoc OpenAPI** - API 문서 자동 생성
- **Swagger UI** - 인터랙티브 API 문서

## 📦 프로젝트 구조

```text
src/main/java/com/PBL/
├── ai/                      # AI 어시스턴트 모듈
│   ├── config/             # AI 모델 설정
│   ├── controller/         # AI API 엔드포인트
│   ├── tools/              # AI 도구 함수들
│   └── dto/                # 요청/응답 DTO
├── curriculum/             # 커리큘럼 관리
│   ├── entity/            # 커리큘럼, 리뷰 엔티티
│   ├── controller/        # 커리큘럼 API
│   └── service/           # 비즈니스 로직
├── lecture/                # 강의 관리
│   ├── entity/            # 강의, 테스트케이스 엔티티
│   ├── controller/        # 강의 API
│   └── service/           # 강의 로직
├── enrollment/             # 수강 신청 및 진도 관리
│   ├── entity/            # 수강, 진도 엔티티
│   └── service/           # 수강 관리 로직
├── user/                   # 사용자 인증/인가
│   ├── entity/            # 사용자 엔티티
│   └── service/           # 인증 서비스
├── qna/                    # Q&A 시스템
├── recommendation/         # 추천 시스템
├── report/                 # 신고 시스템
├── s3/                     # 이미지 업로드
├── search/                 # 검색 기능
└── lab/                    # Judge0 코어
    ├── core/              # 코드 실행 엔진
    ├── judge0/            # Judge0 API
    ├── grade/             # 자동 채점
    └── LanguageServerProtocol/  # LSP 지원
```

## 🚀 시작하기

### 필수 요구사항

- **Java 17 이상**
- **Docker & Docker Compose**
- **Git LFS** (Large File Storage) - 대용량 파일 관리용
- **OpenAI API Key** (AI 기능 사용 시)

### 1. Git LFS 설치

프로젝트에서 대용량 파일을 관리하기 위해 Git LFS가 필요합니다.

```bash
# Git LFS 설치 확인
git lfs install
```

### 2. 저장소 클론

```bash
git clone https://github.com/Seongho-haru/PBL-backend.git
cd PBL-backend

# Git LFS로 관리되는 파일 다운로드
git lfs pull
```

### 3. 환경 변수 설정

운영체제에 맞는 환경 변수 파일을 복사하여 `.env` 파일을 생성합니다:

```bash
# Windows
cp env.windows.example .env

# Mac/Linux
cp env.unix.example .env
```

`.env` 파일을 열어 다음 값들을 설정합니다:

```bash
# 데이터베이스
POSTGRES_PASSWORD=your_secure_password

# Redis
REDIS_PASSWORD=your_redis_password

# MinIO
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_minio_password

# OpenAI (AI 기능 사용 시 필수)
OPENAI_API_KEY=sk-your-openai-api-key
```

### 4. 인프라 실행 (Docker Compose)

```bash
# PostgreSQL, Redis, Qdrant, MinIO 실행
docker-compose up -d

# 컨테이너 상태 확인
docker-compose ps
```

### 5. 애플리케이션 실행

#### 개발 모드

```bash
# Gradle 래퍼를 사용한 실행
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

#### 프로덕션 빌드

```bash
# JAR 파일 빌드
./gradlew build

# 빌드된 JAR 실행
java -jar build/libs/PBL-Backend-1.13.1.jar
```

### 6. 접속 확인

애플리케이션이 정상적으로 실행되면 다음 URL로 접속할 수 있습니다:

- **API 서버**: <http://localhost:2358>
- **Swagger UI**: <http://localhost:2358/swagger-ui.html>
- **API Docs**: <http://localhost:2358/api-docs>
- **Health Check**: <http://localhost:2358/actuator/health>
- **MinIO Console**: <http://localhost:9001> (admin/minioadmin123)

## 📖 API 문서

### Swagger UI

개발 서버 실행 후 [http://localhost:2358/swagger-ui.html](http://localhost:2358/swagger-ui.html)에서 전체 API를 확인할 수 있습니다.

### 주요 API 그룹

| API 그룹 | 엔드포인트 | 설명 |
|---------|-----------|------|
| **Authentication** | `/api/auth/**` | 사용자 인증/인가 |
| **Lectures** | `/api/lectures/**` | 강의 CRUD |
| **Curriculums** | `/api/curriculums/**` | 커리큘럼 관리 |
| **Enrollments** | `/api/enrollments/**` | 수강 신청 및 진도 |
| **Judge0 Core** | `/submissions/**` | 코드 제출 및 실행 |
| **Grading** | `/grading/**` | 자동 채점 |
| **AI Assistant** | `/chat/**` | AI 코드 해설 (SSE) |
| **Q&A** | `/api/qna/**` | 질문과 답변 |
| **Recommendations** | `/api/recommendations/**` | 강의 추천 |
| **S3** | `/api/s3/**` | 이미지 업로드 |

### API 테스트 컬렉션

프로젝트 루트에 Postman 컬렉션 파일이 포함되어 있습니다:

- `PBL_API_Test_Collection.postman_collection.json`
- `PBL_User_Lecture_Curriculum_Integration.postman_collection.json`

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "com.PBL.lecture.*"
```

## 🐳 Docker 배포

### 단일 이미지 빌드

```bash
# Dockerfile로 이미지 빌드
docker build -t pbl-backend:latest .

# 이미지 실행
docker run -p 2358:2358 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e POSTGRES_PASSWORD=your_password \
  pbl-backend:latest
```

### Docker Compose 프로덕션 배포

```bash
# 프로덕션 설정으로 전체 스택 실행
docker-compose -f docker-compose.prod.yml up -d
```

## ⚙️ 주요 설정

### application.yml 주요 설정

```yaml
# 서버 포트
server:
  port: 2358

# Judge0 실행 제한
judge0:
  execution:
    cpu-time-limit: 5.0
    memory-limit: 128000
    wall-time-limit: 10.0

# AI 모델 설정
langchain4j:
  chat-model:
    model-name: gpt-4o-mini
  qdrant:
    host: localhost
    port: 6334

# JobRunr 백그라운드 작업
org.jobrunr:
  background-job-server:
    worker-count: 4
    poll-interval-in-seconds: 5
```

## 🔒 보안 고려사항

- **샌드박스 실행**: Judge0는 Docker 컨테이너 내에서 코드를 격리 실행
- **리소스 제한**: CPU/메모리/시간 제한으로 무한 루프 방지
- **입력 검증**: 모든 API 입력값 유효성 검사
- **Base64 인코딩**: 코드 및 입출력 데이터 안전한 전송

## 📝 데이터베이스 마이그레이션

Flyway를 사용하여 데이터베이스 스키마를 관리합니다.

```bash
# 마이그레이션 실행
./gradlew flywayMigrate

# 마이그레이션 정보 확인
./gradlew flywayInfo

# 데이터베이스 초기화 (주의!)
./gradlew flywayClean
```

마이그레이션 스크립트는 `src/main/resources/db/migration/` 디렉토리에 위치합니다.