#!/bin/bash

###############################################################################
# PBL-Backend Docker 배포 스크립트 (초간단 버전)
# 모든 것을 Docker Compose로 관리합니다!
#
# 사용법: 
#   chmod +x docker-deploy.sh
#   ./docker-deploy.sh
###############################################################################

set -e

# 색상
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 로그 함수
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step() { echo -e "${BLUE}[STEP]${NC} $1"; }

# 배너
print_banner() {
    echo ""
    echo "╔═══════════════════════════════════════════════════╗"
    echo "║   PBL-Backend Docker 자동 배포 스크립트          ║"
    echo "║   모든 것을 Docker로!                             ║"
    echo "╚═══════════════════════════════════════════════════╝"
    echo ""
}

# Docker 설치 확인
check_docker() {
    log_step "Docker 확인 중..."
    
    if ! command -v docker &> /dev/null; then
        log_warn "Docker가 설치되어 있지 않습니다. 설치를 시작합니다..."
        
        # Docker 설치
        curl -fsSL https://get.docker.com | sh
        
        # Docker 서비스 시작
        systemctl start docker
        systemctl enable docker
        
        log_info "✓ Docker 설치 완료"
    else
        log_info "✓ Docker가 이미 설치되어 있습니다."
        docker --version
    fi
}

# Docker Compose 설치 확인
check_docker_compose() {
    log_step "Docker Compose 확인 중..."
    
    if ! command -v docker-compose &> /dev/null; then
        log_warn "Docker Compose가 설치되어 있지 않습니다. 설치를 시작합니다..."
        apt update
        apt install -y docker-compose
        log_info "✓ Docker Compose 설치 완료"
    else
        log_info "✓ Docker Compose가 이미 설치되어 있습니다."
        docker-compose --version
    fi
}

# 프로젝트 디렉토리 확인
check_project_dir() {
    log_step "프로젝트 디렉토리 확인 중..."
    
    if [ ! -f "docker-compose.yml" ]; then
        log_error "docker-compose.yml 파일을 찾을 수 없습니다."
        log_error "프로젝트 루트 디렉토리에서 실행해주세요."
        exit 1
    fi
    
    log_info "✓ 프로젝트 디렉토리 확인 완료"
}

# .env 파일 생성
setup_env_file() {
    log_step "환경 변수 설정 중..."
    
    if [ -f ".env" ]; then
        log_info ".env 파일이 이미 존재합니다."
        read -p "기존 .env 파일을 덮어쓰시겠습니까? (y/N): " -r
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "기존 .env 파일을 유지합니다."
            return 0
        fi
    fi
    
    echo ""
    log_info "환경 변수를 설정합니다. (Enter로 기본값 사용)"
    echo ""
    
    # DB 비밀번호
    read -sp "PostgreSQL 비밀번호 (기본값: judge0pass): " DB_PASSWORD
    echo ""
    DB_PASSWORD=${DB_PASSWORD:-judge0pass}
    
    # Spring Profile
    read -p "Spring Profile (development/production, 기본값: development): " SPRING_PROFILE
    SPRING_PROFILE=${SPRING_PROFILE:-development}
    
    # OpenAI API Key
    read -p "OpenAI API Key (선택사항): " OPENAI_KEY
    
    # MinIO 비밀번호
    read -sp "MinIO 비밀번호 (기본값: minioadmin123): " MINIO_PASSWORD
    echo ""
    MINIO_PASSWORD=${MINIO_PASSWORD:-minioadmin123}
    
    # .env 파일 생성
    cat > .env << EOF
# Database Configuration
DB_PASSWORD=${DB_PASSWORD}

# Spring Profile
SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}

# OpenAI API Key
OPENAI_API_KEY=${OPENAI_KEY}

# MinIO Configuration
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=${MINIO_PASSWORD}
EOF
    
    chmod 600 .env
    log_info "✓ .env 파일 생성 완료"
}

# 기존 컨테이너 정리
cleanup_containers() {
    log_step "기존 컨테이너 확인 중..."
    
    if docker-compose ps -q | grep -q .; then
        log_warn "실행 중인 컨테이너가 있습니다."
        read -p "기존 컨테이너를 중지하고 삭제하시겠습니까? (y/N): " -r
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            log_info "컨테이너 중지 및 삭제 중..."
            docker-compose down
            log_info "✓ 기존 컨테이너 정리 완료"
        fi
    fi
}

# Docker 이미지 빌드
build_images() {
    log_step "Docker 이미지 빌드 중..."
    log_warn "이 작업은 시간이 걸릴 수 있습니다 (약 5-10분)..."
    
    docker-compose build --no-cache
    
    log_info "✓ 이미지 빌드 완료"
}

# 컨테이너 실행
start_containers() {
    log_step "컨테이너 시작 중..."
    
    docker-compose up -d
    
    log_info "✓ 컨테이너 시작 완료"
}

# 서비스 상태 확인
check_services() {
    log_step "서비스 상태 확인 중..."
    
    sleep 10  # 서비스 시작 대기
    
    echo ""
    echo "═══════════════════════════════════════════════════"
    echo "컨테이너 상태"
    echo "═══════════════════════════════════════════════════"
    docker-compose ps
    echo ""
}

# 헬스 체크
health_check() {
    log_step "헬스 체크 수행 중..."
    
    sleep 20  # 애플리케이션 시작 대기
    
    echo ""
    log_info "Backend 헬스 체크..."
    
    if curl -s http://localhost:2358/actuator/health | grep -q "UP"; then
        log_info "✓ Backend 서비스 정상"
    else
        log_warn "Backend 서비스 응답 없음 (아직 시작 중일 수 있습니다)"
    fi
}

# 방화벽 설정
setup_firewall() {
    log_step "방화벽 설정 중..."
    
    if ! command -v ufw &> /dev/null; then
        apt update
        apt install -y ufw
    fi
    
    read -p "방화벽을 설정하시겠습니까? (Y/n): " -r
    if [[ ! $REPLY =~ ^[Nn]$ ]]; then
        ufw --force enable
        ufw allow 22/tcp
        ufw allow 2358/tcp
        
        read -p "MinIO Console 포트(9000, 9001)도 열겠습니까? (y/N): " -r
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            ufw allow 9000/tcp
            ufw allow 9001/tcp
        fi
        
        log_info "✓ 방화벽 설정 완료"
    fi
}

# 관리 스크립트 생성
create_manage_script() {
    log_step "관리 스크립트 생성 중..."
    
    cat > manage.sh << 'MANAGE_EOF'
#!/bin/bash

case "$1" in
  start)
    docker-compose up -d
    ;;
  stop)
    docker-compose down
    ;;
  restart)
    docker-compose restart ${2:-}
    ;;
  logs)
    docker-compose logs -f ${2:-}
    ;;
  status)
    docker-compose ps
    ;;
  stats)
    docker stats
    ;;
  update)
    git pull
    docker-compose up -d --build pbl-backend
    ;;
  backup-db)
    docker-compose exec postgres pg_dump -U judge0 judge0 > backup_$(date +%Y%m%d_%H%M%S).sql
    echo "Backup saved: backup_$(date +%Y%m%d_%H%M%S).sql"
    ;;
  shell)
    docker-compose exec ${2:-pbl-backend} /bin/bash
    ;;
  *)
    echo "PBL-Backend 관리 스크립트"
    echo ""
    echo "사용법: $0 {command}"
    echo ""
    echo "Commands:"
    echo "  start           - 전체 서비스 시작"
    echo "  stop            - 전체 서비스 중지"
    echo "  restart [svc]   - 서비스 재시작"
    echo "  logs [svc]      - 로그 확인"
    echo "  status          - 컨테이너 상태"
    echo "  stats           - 리소스 사용량"
    echo "  update          - 코드 업데이트 및 재배포"
    echo "  backup-db       - DB 백업"
    echo "  shell [svc]     - 컨테이너 쉘 접속"
    echo ""
    echo "예시:"
    echo "  $0 start"
    echo "  $0 logs pbl-backend"
    echo "  $0 restart postgres"
    exit 1
esac
MANAGE_EOF
    
    chmod +x manage.sh
    log_info "✓ 관리 스크립트(manage.sh) 생성 완료"
}

# 최종 정보 출력
print_final_info() {
    SERVER_IP=$(hostname -I | awk '{print $1}')
    
    echo ""
    echo "╔═══════════════════════════════════════════════════╗"
    echo "║              배포 완료! 🎉                        ║"
    echo "╚═══════════════════════════════════════════════════╝"
    echo ""
    echo "📌 접속 정보:"
    echo "  • Backend API:     http://${SERVER_IP}:2358"
    echo "  • Swagger UI:      http://${SERVER_IP}:2358/swagger-ui.html"
    echo "  • MinIO Console:   http://${SERVER_IP}:9001"
    echo "    - Username: minioadmin"
    echo "    - Password: (설정한 비밀번호)"
    echo ""
    echo "📌 관리 명령어:"
    echo "  • 로그 확인:       docker-compose logs -f"
    echo "  • 상태 확인:       docker-compose ps"
    echo "  • 재시작:          docker-compose restart"
    echo "  • 중지:            docker-compose down"
    echo ""
    echo "  또는 간편하게:     ./manage.sh {command}"
    echo ""
    echo "📌 문제 해결:"
    echo "  • 로그 확인:       ./manage.sh logs pbl-backend"
    echo "  • DB 로그:         ./manage.sh logs postgres"
    echo "  • 컨테이너 상태:   ./manage.sh status"
    echo ""
}

# 메인 함수
main() {
    print_banner
    
    # Root 권한 확인
    if [ "$EUID" -ne 0 ]; then
        log_warn "이 스크립트는 root 권한으로 실행하는 것을 권장합니다."
        read -p "계속하시겠습니까? (y/N): " -r
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 0
        fi
    fi
    
    # 각 단계 실행
    check_docker
    check_docker_compose
    check_project_dir
    setup_env_file
    cleanup_containers
    build_images
    start_containers
    check_services
    health_check
    setup_firewall
    create_manage_script
    print_final_info
    
    log_info "배포가 완료되었습니다! 🚀"
}

# 실행
main "$@"
