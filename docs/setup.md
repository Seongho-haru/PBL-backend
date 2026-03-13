# 개발 환경 설정 가이드

## 🚀 빠른 시작

### 1. 환경 변수 설정

```bash
# 필요한 경우 .env 파일을 직접 생성
# - POSTGRES_PASSWORD: PostgreSQL 데이터베이스 비밀번호
# - OPENAI_API_KEY 등 추가 환경 변수
```

### 2. 데이터베이스 설정

PostgreSQL 데이터베이스가 설치되어 있어야 합니다.

```sql
-- PostgreSQL에서 pbl_backend 데이터베이스 생성
CREATE DATABASE pbl_backend;
CREATE USER pbl_backend WITH PASSWORD 'your_password_here';
GRANT ALL PRIVILEGES ON DATABASE pbl_backend TO pbl_backend;
```

### 3. 애플리케이션 실행

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
./gradlew build
java -jar build/libs/pbl-backend-1.13.1.jar
```

## 📁 환경 설정 파일

- **`.env`**: 실제 환경변수 파일 (Git에서 제외)
- **`application.yml`**: 기본 설정 파일

## 🔧 주요 환경 변수

| 변수명        | 기본값                      | 설명                  |
| ------------- | --------------------------- | --------------------- |
| `DB_HOST`     | localhost                   | 데이터베이스 호스트   |
| `DB_PORT`     | 5432                        | 데이터베이스 포트     |
| `DB_NAME`     | pbl_backend                 | 데이터베이스 이름     |
| `DB_USERNAME` | pbl_backend                 | 데이터베이스 사용자명 |
| `DB_PASSWORD` | secret                      | 데이터베이스 비밀번호 |
| `SERVER_PORT` | 2358                        | 서버 포트             |
| `DOCKER_HOST` | unix:///var/run/docker.sock | Docker 호스트         |

## 🐳 Docker 설정

Docker가 실행 중인지 확인하세요:

```bash
# Docker 상태 확인
docker --version
docker ps
```

## 📝 로그 확인

애플리케이션 로그는 `logs/pbl-backend.log` 파일에 저장됩니다.

## 🔍 문제 해결

### 데이터베이스 연결 오류

- PostgreSQL이 실행 중인지 확인
- 데이터베이스 연결 정보가 올바른지 확인
- `.env` 파일의 `DB_PASSWORD`가 올바른지 확인

### Docker 연결 오류

- Docker가 실행 중인지 확인
- Docker 소켓 권한 확인 (Linux/Mac)

### 포트 충돌

- `.env` 파일에서 `SERVER_PORT` 변경
- 다른 애플리케이션이 같은 포트를 사용하고 있는지 확인
