# LSP Docker Files

Judge0에서 지원하는 언어들에 대한 Language Server Protocol (LSP) Docker 컨테이너 모음입니다.

## 지원 언어 및 LSP

| 언어 | LSP | 포트 | Judge0 ID |
|------|-----|------|-----------|
| Python | Pyright | 30001 | 70, 71 |
| TypeScript/JavaScript | typescript-language-server | 30002 | 63, 74 |
| C/C++ | Clangd | 30003 | 48-50, 52-54, 75-76 |
| C# | OmniSharp | 30004 | 51 |
| Go | gopls | 30005 | 60 |
| Rust | rust-analyzer | 30006 | 73 |
| Ruby | Solargraph | 30007 | 72 |
| PHP | Intelephense | 30008 | 68 |
| Kotlin | kotlin-language-server | 30009 | 78 |
| Java | Eclipse JDT LS | 30010 | 62 |
| Groovy | groovy-language-server | 30011 | 88 |

## 사용 방법

### 전체 서비스 실행
```bash
docker-compose up -d
```

### 특정 언어만 실행
```bash
# Python LSP만 실행
docker-compose up -d python-lsp

# 여러 개 동시 실행
docker-compose up -d python-lsp typescript-lsp java-lsp
```

### 서비스 중지
```bash
docker-compose down
```

### 로그 확인
```bash
# 전체 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f python-lsp
```

## 개별 빌드

각 언어의 Dockerfile을 개별적으로 빌드할 수도 있습니다:

```bash
# Python LSP 빌드
docker build -t python-lsp -f com.PBL.lab.LanguageServerProtocol.LSPDockerFile/python/Dockerfile .

# 실행
docker run -p 30001:30001 python-lsp
```

## 디렉토리 구조

```
com.PBL.lab.LanguageServerProtocol.LSPDockerFile/
├── python/
│   └── Dockerfile
├── typescript/
│   └── Dockerfile
├── cpp/
│   └── Dockerfile
├── csharp/
│   └── Dockerfile
├── go/
│   └── Dockerfile
├── rust/
│   └── Dockerfile
├── ruby/
│   └── Dockerfile
├── php/
│   └── Dockerfile
├── kotlin/
│   └── Dockerfile
├── eclipse.jdt.ls/
│   └── Dockerfile
├── groovy/
│   └── Dockerfile
└── docker-compose.yml
```

## 요구사항

- Docker 20.10+
- Docker Compose 2.0+
- 최소 4GB RAM (여러 서비스 동시 실행 시 더 많이 필요)

## 주의사항

1. **포트 충돌**: 각 LSP 서버는 고유한 포트를 사용합니다. 포트가 이미 사용 중이면 docker-compose.yml에서 포트를 변경하세요.

2. **리소스 사용**: 모든 서비스를 동시에 실행하면 상당한 리소스가 필요합니다. 필요한 언어만 선택적으로 실행하는 것을 권장합니다.

3. **빌드 시간**: 첫 빌드는 각 언어의 LSP와 의존성을 다운로드하므로 시간이 걸릴 수 있습니다.

## 트러블슈팅

### 빌드 실패
```bash
# 캐시 없이 다시 빌드
docker-compose build --no-cache python-lsp
```

### 컨테이너가 시작되지 않음
```bash
# 로그 확인
docker-compose logs python-lsp

# 컨테이너 상태 확인
docker-compose ps
```

### 포트 변경
docker-compose.yml 파일에서 해당 서비스의 ports를 수정:
```yaml
ports:
  - "새로운포트:30001"  # 좌측만 변경
```

## 라이선스

각 LSP는 해당 프로젝트의 라이선스를 따릅니다.
