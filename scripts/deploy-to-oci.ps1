# OCI 배포 스크립트 (PowerShell)
# 윈도우에서 실행하여 OCI에 배포하는 스크립트

param(
    [string]$Tag = "latest",
    [switch]$Force,
    [switch]$DryRun,
    [switch]$NoPull,
    [switch]$NoRestart,
    [switch]$Help
)

# 색상 정의
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Blue = "Blue"

# 로그 함수
function Write-Log {
    param([string]$Message, [string]$Color = $Green)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message" -ForegroundColor $Color
}

function Write-Warning {
    param([string]$Message)
    Write-Log "WARNING: $Message" $Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Log "ERROR: $Message" $Red
    exit 1
}

function Write-Info {
    param([string]$Message)
    Write-Log "INFO: $Message" $Blue
}

# 도움말 표시
function Show-Help {
    Write-Host "OCI 배포 스크립트 (PowerShell)" -ForegroundColor $Green
    Write-Host ""
    Write-Host "사용법: .\deploy-to-oci.ps1 [매개변수]" -ForegroundColor $White
    Write-Host ""
    Write-Host "매개변수:" -ForegroundColor $White
    Write-Host "  -Tag <태그>        특정 태그의 이미지를 배포합니다 (기본값: latest)" -ForegroundColor $White
    Write-Host "  -Force             강제로 컨테이너를 재시작합니다" -ForegroundColor $White
    Write-Host "  -DryRun            실제 배포 없이 실행할 명령어만 표시합니다" -ForegroundColor $White
    Write-Host "  -NoPull            이미지 가져오기를 건너뜁니다" -ForegroundColor $White
    Write-Host "  -NoRestart         컨테이너 재시작을 건너뜁니다" -ForegroundColor $White
    Write-Host "  -Help              이 도움말을 표시합니다" -ForegroundColor $White
    Write-Host ""
    Write-Host "예시:" -ForegroundColor $White
    Write-Host "  .\deploy-to-oci.ps1                    # latest 태그로 배포" -ForegroundColor $White
    Write-Host "  .\deploy-to-oci.ps1 -Tag develop      # develop 태그로 배포" -ForegroundColor $White
    Write-Host "  .\deploy-to-oci.ps1 -Force            # 강제 재시작" -ForegroundColor $White
    Write-Host "  .\deploy-to-oci.ps1 -DryRun           # 드라이런 모드" -ForegroundColor $White
}

# 환경 변수 로드
function Load-Environment {
    $envFile = ".env.prod"
    if (Test-Path $envFile) {
        Write-Log "환경 변수 로드 중: $envFile"
        Get-Content $envFile | ForEach-Object {
            if ($_ -match "^([^#][^=]+)=(.*)$") {
                [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
            }
        }
    }
}

# Docker 명령어 실행
function Invoke-DockerCommand {
    param([string]$Command, [string]$Description)
    
    Write-Log $Description
    Write-Info "실행 명령어: docker $Command"
    
    $result = Invoke-Expression "docker $Command" 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Docker 명령어 실행 실패: $Command`n$result"
    }
    
    return $result
}

# OCI 로그인 확인
function Test-DockerLogin {
    Write-Log "Docker 로그인 상태 확인 중..."
    
    try {
        $result = docker info 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Docker가 실행되지 않았거나 접근할 수 없습니다."
        }
        
        # OCI 레지스트리 로그인 테스트
        $registry = $env:REGISTRY ?? "iad.ocir.io"
        $namespace = $env:OCI_NAMESPACE ?? "your-namespace"
        $imageName = "$registry/$namespace/pbl-backend:latest"
        
        try {
            docker pull $imageName 2>&1 | Out-Null
            Write-Log "OCI 레지스트리 로그인 확인 완료"
            return $true
        }
        catch {
            Write-Warning "OCI 레지스트리에서 이미지를 가져올 수 없습니다."
            Write-Log "다음 명령어로 로그인하세요:"
            Write-Host "docker login $registry" -ForegroundColor $Yellow
            Write-Host "사용자명: $namespace/$($env:OCI_USERNAME)" -ForegroundColor $Yellow
            Write-Host "비밀번호: $($env:OCI_AUTH_TOKEN)" -ForegroundColor $Yellow
            return $false
        }
    }
    catch {
        Write-Error "Docker 상태 확인 실패: $_"
    }
}

# 이미지 가져오기
function Pull-Image {
    param([string]$Tag)
    
    $registry = $env:REGISTRY ?? "iad.ocir.io"
    $namespace = $env:OCI_NAMESPACE ?? "your-namespace"
    $imageName = "$registry/$namespace/pbl-backend:$Tag"
    
    Write-Log "이미지 가져오는 중: $imageName"
    Invoke-DockerCommand "pull $imageName" "이미지 가져오기"
}

# 컨테이너 재시작
function Restart-Containers {
    Write-Log "컨테이너 재시작 중..."
    
    # 기존 컨테이너 중지
    Write-Log "기존 컨테이너 중지 중..."
    try {
        docker-compose down
    }
    catch {
        Write-Warning "기존 컨테이너 중지 실패 (무시하고 계속 진행)"
    }
    
    # 새 컨테이너 시작
    Write-Log "새 컨테이너 시작 중..."
    Invoke-DockerCommand "compose up -d" "컨테이너 시작"
}

# 헬스 체크
function Test-HealthCheck {
    Write-Log "애플리케이션 헬스 체크 중..."
    
    $maxAttempts = 30
    $attempt = 1
    
    while ($attempt -le $maxAttempts) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:2358/health" -TimeoutSec 5 -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                Write-Log "✅ 애플리케이션이 정상적으로 시작되었습니다!"
                return $true
            }
        }
        catch {
            # 무시하고 재시도
        }
        
        Write-Log "헬스 체크 시도 $attempt/$maxAttempts - 10초 후 재시도..."
        Start-Sleep -Seconds 10
        $attempt++
    }
    
    Write-Error "❌ 애플리케이션 헬스 체크 실패 - 최대 시도 횟수 초과"
}

# 컨테이너 상태 표시
function Show-ContainerStatus {
    Write-Info "현재 컨테이너 상태:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

# 메인 실행
function Main {
    if ($Help) {
        Show-Help
        return
    }
    
    # 드라이런 모드
    if ($DryRun) {
        Write-Info "드라이런 모드 - 실행할 명령어:"
        $registry = $env:REGISTRY ?? "iad.ocir.io"
        $namespace = $env:OCI_NAMESPACE ?? "your-namespace"
        Write-Host "1. docker pull $registry/$namespace/pbl-backend:$Tag"
        Write-Host "2. docker-compose down"
        Write-Host "3. docker-compose up -d"
        Write-Host "4. curl -f http://localhost:2358/health"
        return
    }
    
    Write-Log "🚀 OCI 배포 시작 (태그: $Tag)"
    
    # 환경 변수 로드
    Load-Environment
    
    # Docker 로그인 확인
    if (-not (Test-DockerLogin)) {
        if ($Force) {
            Write-Warning "강제 모드로 계속 진행합니다..."
        }
        else {
            Write-Error "Docker 로그인이 필요합니다."
        }
    }
    
    # 이미지 가져오기
    if (-not $NoPull) {
        Pull-Image $Tag
    }
    
    # 컨테이너 재시작
    if (-not $NoRestart) {
        Restart-Containers
        Test-HealthCheck
    }
    
    # 배포 완료 정보
    Write-Log "🎉 배포 완료!"
    Write-Log "애플리케이션 URL: http://localhost:2358"
    Write-Log "헬스 체크 URL: http://localhost:2358/health"
    
    # 컨테이너 상태 표시
    Show-ContainerStatus
}

# 스크립트 실행
Main
