#!/bin/bash

# OCI 수동 배포 스크립트
# 이 스크립트는 OCI 인스턴스에서 실행되어 최신 이미지를 가져오고 컨테이너를 재시작합니다.

set -e

# 환경 변수 로드 (선택사항)
if [ -f .env.prod ]; then
    export $(cat .env.prod | grep -v '^#' | xargs)
    log "환경 변수 파일 로드됨: .env.prod"
else
    log "환경 변수 파일 없음 - 기본값 사용"
fi

# 기본값 설정
REGISTRY=${REGISTRY:-iad.ocir.io}
OCI_NAMESPACE=${OCI_NAMESPACE:-your-namespace}
IMAGE_NAME="pbl-backend"
FULL_IMAGE_NAME="${REGISTRY}/${OCI_NAMESPACE}/${IMAGE_NAME}"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO: $1${NC}"
}

# 도움말 표시
show_help() {
    echo "OCI 배포 스크립트"
    echo ""
    echo "사용법: $0 [옵션]"
    echo ""
    echo "옵션:"
    echo "  -h, --help          이 도움말을 표시합니다"
    echo "  -t, --tag TAG       특정 태그의 이미지를 배포합니다 (기본값: latest)"
    echo "  -f, --force         강제로 컨테이너를 재시작합니다"
    echo "  -d, --dry-run       실제 배포 없이 실행할 명령어만 표시합니다"
    echo "  --no-pull           이미지 가져오기를 건너뜁니다"
    echo "  --no-restart        컨테이너 재시작을 건너뜁니다"
    echo ""
    echo "예시:"
    echo "  $0                  # latest 태그로 배포"
    echo "  $0 -t develop       # develop 태그로 배포"
    echo "  $0 -f               # 강제 재시작"
    echo "  $0 -d               # 드라이런 모드"
}

# Docker 로그인 확인
check_docker_login() {
    log "Docker 로그인 상태 확인 중..."
    if ! docker info > /dev/null 2>&1; then
        error "Docker가 실행되지 않았거나 접근할 수 없습니다."
    fi
    
    # OCI 레지스트리 로그인 테스트
    if ! docker pull ${FULL_IMAGE_NAME}:latest > /dev/null 2>&1; then
        warn "OCI 레지스트리에서 이미지를 가져올 수 없습니다."
        log "다음 명령어로 로그인하세요:"
        echo "docker login ${REGISTRY}"
        echo "사용자명: ${OCI_NAMESPACE}/${OCI_USERNAME}"
        echo "비밀번호: ${OCI_AUTH_TOKEN}"
        return 1
    fi
    return 0
}

# 최신 이미지 가져오기
pull_image() {
    local tag=$1
    local image_with_tag="${FULL_IMAGE_NAME}:${tag}"
    
    log "이미지 가져오는 중: ${image_with_tag}"
    if docker pull ${image_with_tag}; then
        log "이미지 가져오기 완료: ${image_with_tag}"
        return 0
    else
        error "이미지 가져오기 실패: ${image_with_tag}"
    fi
}

# 컨테이너 상태 확인
check_container_status() {
    if docker ps --format "table {{.Names}}\t{{.Status}}" | grep -q "pbl-backend"; then
        return 0
    else
        return 1
    fi
}

# 컨테이너 재시작
restart_containers() {
    log "컨테이너 재시작 중..."
    
    # 현재 실행 중인 컨테이너 확인
    if check_container_status; then
        log "기존 컨테이너 중지 중..."
        docker-compose down
    fi
    
    # 새 컨테이너 시작
    log "새 컨테이너 시작 중..."
    if docker-compose up -d; then
        log "컨테이너 시작 완료"
    else
        error "컨테이너 시작 실패"
    fi
}

# 헬스 체크
health_check() {
    log "애플리케이션 헬스 체크 중..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:2358/health > /dev/null 2>&1; then
            log "✅ 애플리케이션이 정상적으로 시작되었습니다!"
            return 0
        fi
        
        log "헬스 체크 시도 $attempt/$max_attempts - 10초 후 재시도..."
        sleep 10
        ((attempt++))
    done
    
    error "❌ 애플리케이션 헬스 체크 실패 - 최대 시도 횟수 초과"
}

# 컨테이너 로그 확인
show_logs() {
    log "최근 컨테이너 로그:"
    docker logs --tail 20 pbl-backend
}

# 메인 실행
main() {
    local tag="latest"
    local force=false
    local dry_run=false
    local no_pull=false
    local no_restart=false
    
    # 명령행 인수 처리
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -t|--tag)
                tag="$2"
                shift 2
                ;;
            -f|--force)
                force=true
                shift
                ;;
            -d|--dry-run)
                dry_run=true
                shift
                ;;
            --no-pull)
                no_pull=true
                shift
                ;;
            --no-restart)
                no_restart=true
                shift
                ;;
            *)
                error "알 수 없는 옵션: $1"
                ;;
        esac
    done
    
    # 드라이런 모드
    if [ "$dry_run" = true ]; then
        info "드라이런 모드 - 실행할 명령어:"
        echo "1. docker pull ${FULL_IMAGE_NAME}:${tag}"
        echo "2. docker-compose down"
        echo "3. docker-compose up -d"
        echo "4. curl -f http://localhost:2358/health"
        exit 0
    fi
    
    log "🚀 OCI 배포 시작 (태그: ${tag})"
    
    # Docker 로그인 확인
    if ! check_docker_login; then
        if [ "$force" = true ]; then
            warn "강제 모드로 계속 진행합니다..."
        else
            error "Docker 로그인이 필요합니다."
        fi
    fi
    
    # 이미지 가져오기
    if [ "$no_pull" = false ]; then
        pull_image "$tag"
    fi
    
    # 컨테이너 재시작
    if [ "$no_restart" = false ]; then
        restart_containers
        health_check
    fi
    
    # 배포 완료 정보
    log "🎉 배포 완료!"
    log "애플리케이션 URL: http://localhost:2358"
    log "헬스 체크 URL: http://localhost:2358/health"
    
    # 컨테이너 상태 표시
    info "현재 컨테이너 상태:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

# 스크립트 실행
main "$@"
