#!/bin/bash

# OCI 배포 스크립트
# 이 스크립트는 OCI 인스턴스에서 실행되어 최신 이미지를 가져오고 컨테이너를 재시작합니다.

set -e

# 환경 변수 로드
if [ -f .env.prod ]; then
    export $(cat .env.prod | grep -v '^#' | xargs)
fi

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 로그 함수
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

# OCI 로그인 확인
check_oci_login() {
    log "OCI Container Registry 로그인 상태 확인 중..."
    if ! docker info > /dev/null 2>&1; then
        error "Docker가 실행되지 않았거나 접근할 수 없습니다."
    fi
    
    # OCI 레지스트리 로그인 테스트
    if ! docker pull ${REGISTRY}/${OCI_NAMESPACE}/pbl-backend:latest > /dev/null 2>&1; then
        warn "OCI 레지스트리에서 이미지를 가져올 수 없습니다. 로그인이 필요할 수 있습니다."
        log "다음 명령어로 로그인하세요:"
        echo "docker login ${REGISTRY}"
    fi
}

# 최신 이미지 가져오기
pull_latest_image() {
    log "최신 이미지 가져오는 중..."
    docker pull ${REGISTRY}/${OCI_NAMESPACE}/pbl-backend:latest || error "이미지 가져오기 실패"
    log "최신 이미지 가져오기 완료"
}

# 컨테이너 재시작
restart_containers() {
    log "컨테이너 재시작 중..."
    
    # 기존 컨테이너 중지 및 제거
    docker-compose -f docker-compose.prod.yml down || warn "기존 컨테이너 중지 실패"
    
    # 새 컨테이너 시작
    docker-compose -f docker-compose.prod.yml up -d || error "컨테이너 시작 실패"
    
    log "컨테이너 재시작 완료"
}

# 헬스 체크
health_check() {
    log "애플리케이션 헬스 체크 중..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:2358/health > /dev/null 2>&1; then
            log "애플리케이션이 정상적으로 시작되었습니다!"
            return 0
        fi
        
        log "헬스 체크 시도 $attempt/$max_attempts - 10초 후 재시도..."
        sleep 10
        ((attempt++))
    done
    
    error "애플리케이션 헬스 체크 실패 - 최대 시도 횟수 초과"
}

# 메인 실행
main() {
    log "OCI 배포 시작"
    
    check_oci_login
    pull_latest_image
    restart_containers
    health_check
    
    log "배포 완료!"
    log "애플리케이션 URL: http://localhost:2358"
    log "헬스 체크 URL: http://localhost:2358/health"
}

# 스크립트 실행
main "$@"
