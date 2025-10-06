FROM python:3.8-slim

# Python LSP 서버 설치
RUN pip install --no-cache-dir 'python-lsp-server[all]'

# 작업 디렉토리
WORKDIR /workspace

# 컨테이너 유지 (LSP는 exec로 시작)
CMD ["tail", "-f", "/dev/null"]
