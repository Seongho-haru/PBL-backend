#!/bin/bash

# 빠른 배포 스크립트
# OCI에서 실행할 간단한 배포 스크립트

set -e

# 환경 변수 로드 (선택사항)
if [ -f .env.prod ]; then
    export $(cat .env.prod | grep -v '^#' | xargs)
    echo "환경 변수 파일 로드됨: .env.prod"
else
    echo "환경 변수 파일 없음 - 기본값 사용"
fi

# 기본 설정
REGISTRY=${REGISTRY:-iad.ocir.io}
OCI_NAMESPACE=${OCI_NAMESPACE:-your-namespace}
IMAGE_NAME="pbl-backend"
FULL_IMAGE_NAME="${REGISTRY}/${OCI_NAMESPACE}/${IMAGE_NAME}"

echo "🚀 PBL Backend 빠른 배포 시작..."

# 1. 최신 이미지 가져오기
echo "📥 최신 이미지 가져오는 중..."
docker pull ${FULL_IMAGE_NAME}:latest

# 2. 기존 컨테이너 중지
echo "⏹️ 기존 컨테이너 중지 중..."
docker-compose down || true

# 3. 새 컨테이너 시작
echo "▶️ 새 컨테이너 시작 중..."
docker-compose up -d

# 4. 헬스 체크
echo "🔍 헬스 체크 중..."
sleep 30

for i in {1..10}; do
    if curl -f http://localhost:2358/health > /dev/null 2>&1; then
        echo "✅ 배포 완료! 애플리케이션이 정상적으로 실행 중입니다."
        echo "🌐 URL: http://localhost:2358"
        exit 0
    fi
    echo "⏳ 헬스 체크 시도 $i/10 - 10초 후 재시도..."
    sleep 10
done

echo "❌ 헬스 체크 실패. 로그를 확인하세요:"
echo "docker logs pbl-backend"
exit 1
