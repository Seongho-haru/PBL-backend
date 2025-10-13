# Judge0 Spring Boot

Judge0의 Java Spring Boot 버전입니다. 원본 Ruby on Rails 기반 Judge0를 Java Spring Boot로 리팩토링한 프로젝트입니다.

## 🚀 개요

Judge0는 온라인 코드 실행을 위한 강력한 API입니다. 이 프로젝트는 원본 Judge0의 기능을 Java Spring Boot로 재구현하여 더 나은 성능과 확장성을 제공합니다.

## 📋 주요 변경사항

### 원본 Judge0 (Ruby on Rails) vs Judge0 Spring Boot

| 기능              | 원본 Judge0        | Judge0 Spring Boot               |
| ----------------- | ------------------ | -------------------------------- |
| **프레임워크**    | Ruby on Rails      | Java Spring Boot                 |
| **데이터베이스**  | PostgreSQL + Redis | PostgreSQL + Redis               |
| **컨테이너 관리** | Docker Compose     | Docker Compose + Kubernetes 지원 |
| **API 스타일**    | RESTful API        | RESTful API                      |

### 주요 개선사항

1. **성능 향상**: Java Spring Boot의 멀티스레딩과 JVM 최적화
2. **확장성**: Kubernetes 네이티브 지원
3. **개발 경험**: Spring Boot의 자동 구성과 개발 도구

## 🛠️ 기술 스택

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Message Queue**: Redis (Job Queue)
- **Container**: Docker
- **Build Tool**: Gradle
- **Monitoring**: Spring Boot Actuator

## 📦 설치 및 실행

### 필수 요구사항

- Docker & Docker Compose
- Java 17+ (개발 시)
- PostgreSQL 15+
- Redis 7+

### 🚀 Quick Start

#### 로컬 개발 환경

```bash
# 저장소 클론
git clone [repository-url]
cd PBL-backend

# 환경 설정 파일 생성
cp env.prod.example .env.prod
# .env.prod 파일을 열어서 데이터베이스 비밀번호 등 필요한 값들을 설정하세요

# 로컬 개발 환경 실행
docker-compose up -d
```

#### OCI 프로덕션 배포

```bash
# OCI 인스턴스에서 실행
git clone [repository-url]
cd PBL-backend

# 환경 설정 (선택사항 - 기본값으로도 실행 가능)
cp env.prod.example .env.prod
nano .env.prod  # OCI 설정 입력 (REGISTRY, OCI_NAMESPACE만 필수)

# OCI 레지스트리 로그인
docker login iad.ocir.io

# 빠른 배포
chmod +x scripts/quick-deploy.sh
./scripts/quick-deploy.sh
```

### 🔄 자동 배포 (Watchtower)

GitHub에 푸시하면 자동으로 OCI에 배포됩니다:

1. **GitHub Actions**가 자동으로 이미지를 빌드하고 OCI Container Registry에 푸시
2. **Watchtower**가 5분마다 새 이미지를 확인하고 자동으로 컨테이너 재시작

자세한 설정 방법은 [OCI 배포 가이드](docs/OCI_DEPLOYMENT_GUIDE.md)를 참조하세요.

### 환경 설정

프로젝트를 시작하기 전에 다음 단계를 따라주세요:

1. **환경 변수 설정**

   ```bash
   # .env.example 파일을 복사하여 .env 파일 생성
   cp .env.example .env

   # .env 파일을 열어서 다음 값들을 설정:
   # - DB_PASSWORD: PostgreSQL 데이터베이스 비밀번호
   # - 기타 필요한 환경 변수들
   ```

2. **데이터베이스 설정**

   - PostgreSQL 15+ 설치 및 실행
   - `judge0` 데이터베이스 생성
   - `.env` 파일에 올바른 데이터베이스 연결 정보 입력

3. **Docker 설정**
   - Docker가 실행 중인지 확인
   - Docker 소켓 권한 확인 (Linux/Mac)

## 📖 API 문서

서버 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:2358/swagger-ui.html
- **API Docs**: http://localhost:2358/api-docs

### 주요 엔드포인트

```http
POST /submissions           # 코드 제출
GET  /submissions/{token}   # 제출 결과 조회
GET  /languages            # 지원 언어 목록
GET  /statuses             # 상태 코드 목록
GET  /system_info          # 시스템 정보
```

## 🧪 테스트

### API 테스트

```bash
# 간단한 Hello World 테스트
curl -X POST http://localhost:2358/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "languageId": 62,
    "sourceCode": "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }"
  }'
```

## 📄 라이선스

이 프로젝트는 원본 Judge0와 동일한 MIT 라이선스를 사용합니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 확인하세요.

## 🙏 감사의 말

- 원본 [Judge0](https://github.com/judge0/judge0) 프로젝트와 커뮤니티
- Spring Boot 및 Java 생태계
- Docker 커뮤니티
