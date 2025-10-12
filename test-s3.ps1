# S3 이미지 업로드 테스트 스크립트 (PowerShell)

$API_BASE = "http://localhost:2358/api/s3"
$USER_ID = 1

Write-Host "=== S3 이미지 업로드 테스트 ===" -ForegroundColor Green

# 1. 이미지 업로드 테스트
Write-Host "`n1. 이미지 업로드 테스트" -ForegroundColor Yellow

# 테스트용 이미지 파일 경로 (실제 경로로 변경하세요)
$IMAGE_PATH = "C:\Users\wnstj\Pictures\test-image.jpg"

if (Test-Path $IMAGE_PATH) {
    Write-Host "이미지 파일을 찾았습니다: $IMAGE_PATH"
    
    $form = @{
        file = Get-Item $IMAGE_PATH
        description = "PowerShell 테스트 이미지"
        isPublic = "true"
    }
    
    try {
        $response = Invoke-RestMethod -Uri "$API_BASE/upload" -Method Post -Form $form -Headers @{"X-User-Id" = $USER_ID}
        Write-Host "업로드 성공!" -ForegroundColor Green
        Write-Host "이미지 ID: $($response.id)"
        Write-Host "이미지 URL: $($response.imageUrl)"
        Write-Host "썸네일 URL: $($response.thumbnailUrl)"
        
        $UPLOADED_IMAGE_ID = $response.id
    }
    catch {
        Write-Host "업로드 실패: $($_.Exception.Message)" -ForegroundColor Red
    }
}
else {
    Write-Host "테스트 이미지 파일을 찾을 수 없습니다: $IMAGE_PATH" -ForegroundColor Red
    Write-Host "다른 이미지 파일 경로를 설정하거나 이미지를 준비해주세요." -ForegroundColor Yellow
}

# 2. 공개 이미지 목록 조회
Write-Host "`n2. 공개 이미지 목록 조회" -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "$API_BASE/public?page=0&size=5" -Method Get
    Write-Host "이미지 목록 조회 성공!" -ForegroundColor Green
    Write-Host "총 이미지 수: $($response.totalElements)"
    Write-Host "현재 페이지: $($response.currentPage + 1)/$($response.totalPages)"
    
    if ($response.images.Count -gt 0) {
        Write-Host "`n최근 이미지들:"
        foreach ($image in $response.images) {
            Write-Host "- ID: $($image.id), 파일명: $($image.originalFilename), 크기: $($image.width)x$($image.height)"
        }
    }
}
catch {
    Write-Host "이미지 목록 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 특정 이미지 정보 조회
if ($UPLOADED_IMAGE_ID) {
    Write-Host "`n3. 특정 이미지 정보 조회 (ID: $UPLOADED_IMAGE_ID)" -ForegroundColor Yellow
    
    try {
        $response = Invoke-RestMethod -Uri "$API_BASE/$UPLOADED_IMAGE_ID" -Method Get
        Write-Host "이미지 정보 조회 성공!" -ForegroundColor Green
        Write-Host "원본 파일명: $($response.originalFilename)"
        Write-Host "파일 크기: $($response.fileSize) bytes"
        Write-Host "이미지 크기: $($response.width)x$($response.height)"
        Write-Host "MIME 타입: $($response.contentType)"
        Write-Host "공개 여부: $($response.isPublic)"
        Write-Host "업로드 시간: $($response.uploadedAt)"
    }
    catch {
        Write-Host "이미지 정보 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 4. 이미지 검색 테스트
Write-Host "`n4. 이미지 검색 테스트" -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "$API_BASE/search?keyword=test&page=0&size=5" -Method Get
    Write-Host "이미지 검색 성공!" -ForegroundColor Green
    Write-Host "검색 결과 수: $($response.totalElements)"
    
    if ($response.images.Count -gt 0) {
        Write-Host "`n검색 결과:"
        foreach ($image in $response.images) {
            Write-Host "- ID: $($image.id), 파일명: $($image.originalFilename)"
        }
    }
}
catch {
    Write-Host "이미지 검색 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 통계 조회
Write-Host "`n5. 이미지 통계 조회" -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "$API_BASE/stats" -Method Get
    Write-Host "통계 조회 성공!" -ForegroundColor Green
    Write-Host "총 이미지 수: $($response.totalImages)"
    Write-Host "공개 이미지 수: $($response.publicImages)"
    Write-Host "비공개 이미지 수: $($response.privateImages)"
    Write-Host "총 파일 크기: $($response.totalFileSize) bytes"
    Write-Host "평균 파일 크기: $($response.averageFileSize) bytes"
}
catch {
    Write-Host "통계 조회 실패: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 테스트 완료 ===" -ForegroundColor Green
