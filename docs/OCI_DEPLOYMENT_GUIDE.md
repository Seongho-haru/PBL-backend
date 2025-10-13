# OCI 배포 가이드

이 가이드는 PBL Backend 프로젝트를 Oracle Cloud Infrastructure (OCI)에 배포하고 Watchtower를 이용한 자동 업데이트를 설정하는 방법을 설명합니다.

## 📋 목차

1. [사전 준비사항](#사전-준비사항)
2. [OCI 컨테이너 레지스트리 설정](#oci-컨테이너-레지스트리-설정)
3. [OCI 인스턴스 설정](#oci-인스턴스-설정)
4. [배포 실행](#배포-실행)
5. [Watchtower 자동 업데이트 설정](#watchtower-자동-업데이트-설정)
6. [모니터링 및 로그](#모니터링-및-로그)
7. [문제 해결](#문제-해결)

## 🚀 사전 준비사항

### 필요한 도구

- Docker 및 Docker Compose
- OCI CLI (선택사항)
- Git

### OCI 계정 설정

- Oracle Cloud 계정
- OCI Container Registry 액세스 권한
- Compute Instance 생성 권한

## 🐳 OCI 컨테이너 레지스트리 설정

### 1. OCI Container Registry 생성

1. OCI 콘솔에 로그인
2. **Developer Services** > **Container Registry** 이동
3. **Create Repository** 클릭
4. Repository 이름: `pbl-backend`
5. Access: `Public` 또는 `Private` 선택

### 2. 인증 토큰 생성

1. OCI 콘솔에서 **Profile** > **User Settings** 이동
2. **Auth Tokens** 섹션에서 **Generate Token** 클릭
3. 토큰 이름 입력 후 생성
4. **생성된 토큰을 안전한 곳에 저장** (다시 볼 수 없음)

### 3. GitHub Secrets 설정

GitHub Repository에서 다음 Secrets를 설정하세요:

```
OCI_USERNAME: your-oci-username
OCI_AUTH_TOKEN: your-auth-token
OCI_NAMESPACE: your-tenancy-namespace
```

## 🖥️ OCI 인스턴스 설정

### 1. Compute Instance 생성

**권장 사양:**

- Shape: VM.Standard.E2.1.Micro (Always Free) 또는 VM.Standard.E2.1
- OS: Oracle Linux 8 또는 Ubuntu 20.04+
- Storage: 50GB 이상

### 2. 보안 그룹 설정

다음 포트들을 열어주세요:

- `22` (SSH)
- `2358` (PBL Backend)
- `5432` (PostgreSQL)
- `6379` (Redis)
- `8000` (ChromaDB)
- `9000` (MinIO API)
- `9001` (MinIO Console)

### 3. 인스턴스 초기 설정

```bash
# 시스템 업데이트
sudo yum update -y  # Oracle Linux
# 또는
sudo apt update && sudo apt upgrade -y  # Ubuntu

# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Git 설치
sudo yum install git -y  # Oracle Linux
# 또는
sudo apt install git -y  # Ubuntu
```

## 📦 배포 실행

### 1. 프로젝트 클론

```bash
git clone https://github.com/your-username/PBL-backend.git
cd PBL-backend
```

### 2. 환경 변수 설정

```bash
# 환경 변수 파일 생성
cp env.prod.example .env.prod

# 환경 변수 편집
nano .env.prod
```

`.env.prod` 파일 내용:

```bash
# OCI Container Registry Configuration
REGISTRY=iad.ocir.io
OCI_NAMESPACE=your-namespace

# Database Configuration
POSTGRES_PASSWORD=your-secure-password

# Redis Configuration
REDIS_PASSWORD=your-redis-password

# MinIO Configuration
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your-minio-password

# ChromaDB Configuration
ANONYMIZED_TELEMETRY=TRUE

# Slack Notifications (Optional)
SLACK_WEBHOOK_URL=your-slack-webhook-url
```

### 3. OCI 레지스트리 로그인

```bash
docker login iad.ocir.io
# Username: your-namespace/your-username
# Password: your-auth-token
```

### 4. 배포 실행

#### Linux/macOS:

```bash
# 실행 권한 부여
chmod +x scripts/oci-deploy.sh

# 배포 실행
./scripts/oci-deploy.sh

# 특정 태그로 배포
./scripts/oci-deploy.sh -t develop

# 강제 재시작
./scripts/oci-deploy.sh -f
```

#### Windows (PowerShell):

```powershell
# 배포 실행
.\scripts\deploy-to-oci.ps1

# 특정 태그로 배포
.\scripts\deploy-to-oci.ps1 -Tag develop

# 강제 재시작
.\scripts\deploy-to-oci.ps1 -Force
```

## 🔄 Watchtower 자동 업데이트 설정

### 1. Watchtower 설정 확인

`docker-compose.prod.yml` 파일에서 Watchtower가 이미 설정되어 있습니다:

```yaml
watchtower:
  image: containrrr/watchtower:latest
  container_name: watchtower
  restart: unless-stopped
  environment:
    - WATCHTOWER_POLL_INTERVAL=300 # 5분마다 확인
    - WATCHTOWER_CLEANUP=true # 오래된 이미지 정리
    - WATCHTOWER_INCLUDE_STOPPED=true
    - WATCHTOWER_REVIVE_STOPPED=true
  volumes:
    - /var/run/docker.sock:/var/run/docker.sock
```

### 2. 자동 업데이트 작동 방식

1. **GitHub에 푸시** → GitHub Actions가 자동으로 실행
2. **이미지 빌드 및 푸시** → OCI Container Registry에 새 이미지 업로드
3. **Watchtower 감지** → 5분마다 새 이미지 확인
4. **자동 재시작** → 새 이미지 발견 시 컨테이너 자동 재시작

### 3. Slack 알림 설정 (선택사항)

Slack 웹훅 URL을 설정하면 업데이트 알림을 받을 수 있습니다:

```bash
# .env.prod 파일에 추가
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
```

## 📊 모니터링 및 로그

### 1. 컨테이너 상태 확인

```bash
# 실행 중인 컨테이너 확인
docker ps

# 컨테이너 로그 확인
docker logs pbl-backend
docker logs watchtower

# 실시간 로그 모니터링
docker logs -f pbl-backend
```

### 2. 헬스 체크

```bash
# 애플리케이션 헬스 체크
curl http://localhost:2358/health

# 데이터베이스 연결 확인
docker exec pbl-db pg_isready -U judge0 -d judge0

# Redis 연결 확인
docker exec pbl-redis redis-cli ping
```

### 3. 리소스 모니터링

```bash
# 시스템 리소스 사용량
docker stats

# 디스크 사용량
df -h
docker system df
```

## 🔧 문제 해결

### 일반적인 문제들

#### 1. Docker 로그인 실패

```bash
# 로그인 상태 확인
docker login iad.ocir.io

# 인증 토큰 재생성
# OCI 콘솔에서 새로운 Auth Token 생성
```

#### 2. 이미지 가져오기 실패

```bash
# 네트워크 연결 확인
ping iad.ocir.io

# DNS 설정 확인
nslookup iad.ocir.io

# 방화벽 설정 확인
sudo ufw status
```

#### 3. 컨테이너 시작 실패

```bash
# 상세 로그 확인
docker logs pbl-backend

# 환경 변수 확인
docker-compose -f docker-compose.prod.yml config

# 포트 충돌 확인
netstat -tulpn | grep :2358
```

#### 4. 데이터베이스 연결 실패

```bash
# PostgreSQL 상태 확인
docker exec pbl-db pg_isready -U judge0 -d judge0

# 데이터베이스 로그 확인
docker logs pbl-db

# 연결 테스트
docker exec pbl-backend psql -h db -U judge0 -d judge0 -c "SELECT 1;"
```

### 로그 파일 위치

- **애플리케이션 로그**: `logs/judge0-spring.log`
- **Docker 로그**: `docker logs <container_name>`
- **시스템 로그**: `/var/log/messages` (Oracle Linux)

### 성능 최적화

#### 1. 메모리 설정

```bash
# JVM 힙 메모리 조정
export JAVA_OPTS="-Xmx2g -Xms1g"
```

#### 2. 데이터베이스 최적화

```sql
-- PostgreSQL 설정 최적화
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
```

#### 3. Redis 최적화

```bash
# Redis 메모리 정책 설정
redis-cli CONFIG SET maxmemory-policy allkeys-lru
```

## 📞 지원

문제가 발생하면 다음을 확인하세요:

1. **로그 파일** 확인
2. **환경 변수** 설정 확인
3. **네트워크 연결** 상태 확인
4. **리소스 사용량** 확인

추가 도움이 필요하면 GitHub Issues에 문의하세요.

---

**참고**: 이 가이드는 Oracle Cloud Infrastructure를 기준으로 작성되었습니다. 다른 클라우드 제공업체를 사용하는 경우 해당 제공업체의 문서를 참조하세요.
