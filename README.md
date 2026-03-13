# PBL - AI 기반 개인 맞춤형 학습 플랫폼 ✨

## 🎀 프로젝트 소개

🏷 **프로젝트명** : PBL(Problem-Based Learning) 기반 개인 맞춤형 학습 콘텐츠 지원 플랫폼  
🗓️ **프로젝트 기간** : 2025.03.02 ~ 2025.11.02 (8개월)  
👥 **구성원** : 윤성호(팀장👑), 임상진, 김준성  

---

### 🎥 서비스 소개 영상

[![데모 영상](https://img.shields.io/badge/YouTube-데모%20영상%20보기-red?style=for-the-badge&logo=youtube)](https://youtu.be/fqitJZCgcBY)

---

## ✅ 기획 배경

최근 소프트웨어 기술의 발전 속도는 매우 빠르게 진행되고 있으며, 이로 인해 **프로그래밍을 처음 접하는 초급 개발자들은 방대한 학습 범위와 빠르게 변화하는 기술 흐름 속에서 학습의 방향성을 설정하는 데 어려움**을 겪고 있습니다.

현재 많은 초급 학습자들은 도서, 온라인/오프라인 강의, 웹 검색 등을 통한 학습에 의존하고 있으나:

- 📚 최신 기술의 흐름을 신속하게 반영하기 어려움
- 💰 상업적 성격이 강해 **고비용**으로 운영
- ❓ 제공되는 정보의 **구조화 수준과 신뢰도가 일정하지 않음**

특히, 상업화된 콘텐츠가 증가함에 따라 **지식을 자유롭게 공유하고자 하는 개발 생태계의 문화가 점차 약화**되고 있으며, 이는 기술 학습의 접근성을 낮추고 개발자 간 지식 격차를 심화시킬 수 있는 요소로 작용하고 있습니다.

> 이러한 환경은 결과적으로 기술의 확산과 발전을 저해할 수 있다는 문제의식에서 본 프로젝트는 출발하였습니다.

---

## 🎯 프로젝트 목적

1. **지식 격차 해소** - 초급 개발자들이 강의를 읽으면서 동시에 문제를 풀 수 있는 환경을 제공하여 개발자 간의 지식 격차를 줄임
2. **지식 공유 문화 복원** - 지식의 상업화로 위축된 공유 문화를 해결하고, 개발자들이 지식을 자유롭게 배우고, 나누고, 순환시킬 수 있는 환경 조성
3. **개방적 생태계 구축** - 문제 중심의 학습 경험을 기반으로 누구나 쉽게 콘텐츠를 제작하고 공유할 수 있는 협력적인 개발자 생태계 복원
4. **무료 콘텐츠 축적** - 상업적 목적 없이도 가치 있는 콘텐츠가 축적되고 확산될 수 있는 기반 마련

---

## 👥 서비스 대상

| 사용자 유형 | 설명 |
|------------|------|
| **초급 개발자 (학습자)** | 문제를 풀고 토론에 참여하며 실용적인 콘텐츠를 통해 성장하는 학습자 |
| **지식 기여자 (강의자)** | 자신의 경험을 바탕으로 콘텐츠를 제작하고 지속적으로 지식을 공유하는 기여자 |
| **시스템 운영자 (관리자)** | 플랫폼의 안정성과 콘텐츠 품질을 유지하며 커뮤니티를 관리하는 운영자 |

---

## 📊 기존 시스템 비교 분석

| 비교 항목 | Dacon | Kaggle | Colab | FastCampus/Inflearn | **본 플랫폼** |
|----------|-------|--------|-------|---------------------|--------------|
| 주요 목적 | 데이터 분석 대회 | 데이터 과학 실습 | 코드 실행 환경 | 강의 중심 | **PBL 기반 실습형 학습** |
| 콘텐츠 제작자 | 운영자 중심 | 사용자 | 없음 | 강사 | **모든 사용자** |
| 실습 환경 | ✅ | ✅ | ✅ | ❌ | ✅ |
| 참여 방식 | 대회 참가 | 노트북 공유 | 코드 실행 | 수강 | **수강 및 강의 제작** |
| 개인화 기능 | ❌ | ❌ | ❌ | 일부 | **AI 기반 커리큘럼** |
| 커뮤니티 | Q&A | 중간 | ❌ | 낮음 | **Q&A** |
| 비용 구조 | 유료 | 무료 | 유료/무료 | 유료 | **무료** |

---

## 🚀 주요 기능

### 📚 교육 관리 시스템
- **강의 관리**: 마크다운 기반 이론 강의와 코딩 문제 강의 통합 관리
- **커리큘럼 구성**: 강의들을 체계적으로 묶어 학습 경로 제공
- **수강 신청**: 학생들의 커리큘럼 수강 및 진도 관리
- **진도 추적**: 강의별 수강 상태 및 완료율 관리

### 💻 코드 실행 및 채점
- **Judge0 엔진**: 다양한 프로그래밍 언어 지원 (Python, Java, C++, JavaScript 등)
- **자동 채점**: 테스트케이스 기반 자동 채점 시스템
- **실시간 피드백**: 코드 실행 결과 즉시 확인
- **리소스 제한**: CPU, 메모리, 시간 제한 설정 가능

### 🤖 AI 어시스턴트
- **코드 해설**: LangChain4j 기반 AI가 제출된 코드 분석 및 해설
- **스트리밍 응답**: SSE(Server-Sent Events)를 통한 실시간 AI 응답
- **벡터 검색**: Qdrant를 활용한 유사 문제 및 참고 자료 추천
- **도구 통합**: AI가 강의, 커리큘럼, 제출 이력 등을 조회하며 맥락 있는 답변 제공

### 📝 커뮤니티 기능
- **Q&A 시스템**: 강의별 질문과 답변
- **코스 리뷰**: 수강생들의 커리큘럼 평가 및 피드백
- **신고 시스템**: 부적절한 콘텐츠 관리
- **추천 시스템**: 사용자 선호도 기반 강의 추천

---

## 🛠️ 기술 스택

![Java](https://img.shields.io/badge/Java_17-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2.0-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_16-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis_7-DC382D?style=flat-square&logo=redis&logoColor=white)
![Qdrant](https://img.shields.io/badge/Qdrant-24292E?style=flat-square)
![MinIO](https://img.shields.io/badge/MinIO-C72E49?style=flat-square&logo=minio&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![LangChain4j](https://img.shields.io/badge/LangChain4j-1C3C3C?style=flat-square)
![OpenAI](https://img.shields.io/badge/GPT--4o--mini-412991?style=flat-square&logo=openai&logoColor=white)
![Judge0](https://img.shields.io/badge/Judge0-000000?style=flat-square)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)

---

## 📁 프로젝트 구조

```
src/main/java/com/PBL/
├── ai/                      # AI 어시스턴트 모듈
│   ├── config/             # AI 모델 설정
│   ├── controller/         # AI API 엔드포인트
│   ├── tools/              # AI 도구 함수들
│   └── dto/                # 요청/응답 DTO
├── curriculum/             # 커리큘럼 관리
├── lecture/                # 강의 관리
├── enrollment/             # 수강 신청 및 진도 관리
├── user/                   # 사용자 인증/인가
├── qna/                    # Q&A 시스템
├── recommendation/         # 추천 시스템
├── report/                 # 신고 시스템
├── s3/                     # 이미지 업로드
├── search/                 # 검색 기능
└── lab/                    # Judge0 코어
    ├── core/              # 코드 실행 엔진
    ├── judge0/            # Judge0 API
    ├── grade/             # 자동 채점
    └── LanguageServerProtocol/  # LSP 지원
```

---

## 🌐 API 문서

| API 그룹 | 엔드포인트 | 설명 |
|---------|-----------|------|
| **Authentication** | `/api/auth/**` | 사용자 인증/인가 |
| **Lectures** | `/api/lectures/**` | 강의 CRUD |
| **Curriculums** | `/api/curriculums/**` | 커리큘럼 관리 |
| **Enrollments** | `/api/enrollments/**` | 수강 신청 및 진도 |
| **Code Execution Core** | `/submissions/**` | 코드 제출 및 실행 |
| **Grading** | `/grading/**` | 자동 채점 |
| **AI Assistant** | `/chat/**` | AI 코드 해설 (SSE) |
| **Q&A** | `/api/qna/**` | 질문과 답변 |
| **Recommendations** | `/api/recommendations/**` | 강의 추천 |

> 📖 Swagger UI: `http://localhost:2358/swagger-ui.html`

---

## 📈 기대 효과

### 1. 지식 공유 문화의 활성화
- 개발자들이 문제 기반의 학습 콘텐츠를 직접 제작하고 공유하여 상호 학습과 협력 촉진
- 상업적 콘텐츠 중심에서 벗어난 자유롭고 개방적인 지식 생태계 조성

### 2. 초급 개발자의 학습 효율 향상
- PBL을 통해 이론뿐만 아니라 실습 중심의 실전 감각 습득
- 자동 채점 시스템으로 학습 피드백을 빠르게 확인

### 3. 지속 가능한 커뮤니티 성장
- 학습자, 강사, 기여자의 역할이 명확히 분리되고 서로 보완
- 플랫폼 수익을 기여도에 따라 분배하는 구조로 장기적인 활동 유도

### 4. 기술 교육의 접근성 향상
- 경제적 부담 없이 고품질의 학습 콘텐츠 이용 가능
- 개발자 양성과 학습 기회의 지역적, 경제적 격차 해소

---

## 💙 팀원 소개

| 이름 | 역할 | GitHub |
|------|------|--------|
| **윤성호** 👑 | 팀장 / Backend | [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/yunseongho) |
| **임상진** | Frontend | [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/sangjin615) |
| **김준성** | Backend | [![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/Junseong03) |

---

## 📂 관련 레포지토리

| 레포지토리 | 설명 |
|-----------|------|
| **Backend** (현재) | Spring Boot 기반 API 서버, AI 어시스턴트, 코드 실행 엔진 |
| [**Frontend**](여기에_프론트엔드_링크) | 사용자 인터페이스 |
| [**AI Data Pipeline**](여기에_AI_전처리_링크) | AI 학습 데이터 전처리 및 수집 |

---

## 🚀 시작하기

### 필수 요구사항
- Java 17 이상
- Docker & Docker Compose
- Git LFS
- OpenAI API Key (AI 기능 사용 시)

### 설치 및 실행

```bash
# 저장소 클론
git clone https://github.com/Seongho-haru/PBL-backend.git
cd PBL-backend

# 인프라 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun

# Windows에서 Docker 소켓 연결이 필요하면 환경 변수 설정 후 실행
# set DOCKER_HOST=npipe:////./pipe/docker_engine
```

### 접속 URL
| 서비스 | URL |
|--------|-----|
| API 서버 | http://localhost:2358 |
| Swagger UI | http://localhost:2358/swagger-ui.html |
| MinIO Console | http://localhost:9001 |

