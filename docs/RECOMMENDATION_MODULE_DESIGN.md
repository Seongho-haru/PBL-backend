# 추천 모듈 설계 문서 (Recommendation Module Design)

## 📋 목차

1. [개요](#개요)
2. [요구사항 분석](#요구사항-분석)
3. [데이터베이스 설계](#데이터베이스-설계)
4. [추천 알고리즘](#추천-알고리즘)
5. [API 설계](#api-설계)
6. [구현 계획](#구현-계획)

---

## 🎯 개요

### 목적

사용자에게 개인화된 커리큘럼 및 강의를 추천하여 학습 효율을 극대화

### 핵심 기능

1. **개인화 추천**: 사용자의 관심사, 학습 이력 기반 커리큘럼 추천
2. **유사 문제 추천**: 현재 풀고 있는 문제와 유사한 강의 추천

---

## 🔍 요구사항 분석

### 1. 개인화 추천 (유튜브 홈 스타일)

**기준:**

- 사용자의 수강 이력
- 관심 카테고리/태그
- 난이도 선호도
- 평점 높은 인기 강의
- 최근 인기 트렌드

**데이터 소스:**

- `enrollments`: 수강 이력
- `curriculums.tags`: 카테고리/태그
- `curriculums.difficulty`: 난이도
- `curriculums.average_rating`: 평균 평점
- `curriculums.student_count`: 수강생 수

### 2. 유사 문제 추천 (우선순위: 높음)

**기준:**

- 문제 유형 (PROBLEM 타입)
- 카테고리 매칭
- 태그 유사도
- 난이도 유사도

**데이터 소스:**

- `lectures.type`: 강의 타입
- `lectures.category`: 카테고리
- `lectures.tags`: 태그
- `lecture_progress.status`: 학습 상태

---

## 🗄️ 데이터베이스 설계

### 1. 사용자 선호도 테이블 (user_preferences)

**목적:** 사용자별 선호 카테고리, 태그, 난이도 저장

```sql
CREATE TABLE user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,

    -- 선호 카테고리 (JSON 배열)
    preferred_categories TEXT[],

    -- 선호 태그 (JSON 배열)
    preferred_tags TEXT[],

    -- 선호 난이도
    preferred_difficulty VARCHAR(20),

    -- 학습 선호도 (시간대 등)
    learning_style JSONB,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(user_id)
);

CREATE INDEX idx_user_preferences_user_id ON user_preferences(user_id);
CREATE INDEX idx_user_preferences_categories ON user_preferences USING gin(preferred_categories);
CREATE INDEX idx_user_preferences_tags ON user_preferences USING gin(preferred_tags);
```

### 2. 추천 로그 테이블 (recommendation_logs)

**목적:** 추천 내역 및 클릭율 추적

```sql
CREATE TABLE recommendation_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    curriculum_id BIGINT,
    lecture_id BIGINT,

    -- 추천 타입
    recommendation_type VARCHAR(50) NOT NULL, -- 'PERSONALIZED', 'SIMILAR_PROBLEM', 'TRENDING', etc.

    -- 추천 점수
    recommendation_score DECIMAL(5,2),

    -- 클릭 여부
    is_clicked BOOLEAN DEFAULT FALSE,
    clicked_at TIMESTAMP,

    -- 표시된 순서
    display_order INTEGER,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (curriculum_id) REFERENCES curriculums(id) ON DELETE SET NULL,
    FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE SET NULL
);

CREATE INDEX idx_recommendation_logs_user_id ON recommendation_logs(user_id);
CREATE INDEX idx_recommendation_logs_created_at ON recommendation_logs(created_at);
CREATE INDEX idx_recommendation_logs_type ON recommendation_logs(recommendation_type);
```

### 3. 강의 메타데이터 강화

**목적:** 추천을 위한 추가 데이터 저장

**기존 `lectures` 테이블에 추가:**

- `difficulty`: 난이도 (BEGINNER, INTERMEDIATE, ADVANCED)
- `estimated_time_minutes`: 예상 소요 시간
- `tags`: 태그 배열

**기존 `curriculums` 테이블 활용:**

- `tags`, `category`, `difficulty` 이미 존재 ✓
- `average_rating`, `student_count` 이미 존재 ✓

---

## 🧮 추천 알고리즘

### 1. 개인화 추천 점수 계산

```
Total Score = (수강 이력 기반 점수 × 0.4) +
              (태그/카테고리 매칭 점수 × 0.3) +
              (평점 점수 × 0.2) +
              (인기도 점수 × 0.1)

각 점수 = 0 ~ 100
```

#### 1.1 수강 이력 기반 점수

- 사용자가 수강한 커리큘럼의 카테고리와 일치: +30
- 태그가 3개 이상 일치: +40
- 난이도가 유사: +30

#### 1.2 태그/카테고리 매칭 점수

- 카테고리 매칭: +50
- 태그 일치율 × 50

#### 1.3 평점 점수

- `average_rating / 5.0 × 100`

#### 1.4 인기도 점수

- `log(student_count + 1) / log(max(student_count) + 1) × 100`

### 2. 유사 문제 추천 알고리즘

```
Similarity Score = (카테고리 매칭 × 0.5) +
                   (태그 유사도 × 0.3) +
                   (난이도 유사도 × 0.2)
```

**조건:**

- `lecture.type = 'PROBLEM'`
- 현재 사용자가 풀고 있거나 완료한 문제
- 같은 카테고리 우선
- 비슷한 난이도 (±1 level)

---

## 📡 API 설계

### 1. 개인화 커리큘럼 추천

```
GET /api/recommendations/curriculums?limit=10
Headers: X-User-Id: {userId}
Response: List<CurriculumResponse>
```

**로직:**

1. 사용자 수강 이력 분석
2. 카테고리/태그 선호도 추출
3. 점수 계산
4. 상위 N개 반환

**쿼리 파라미터:**

- `limit`: 추천 개수 (기본값: 10)
- `difficulty`: 난이도 필터 (선택)
- `category`: 카테고리 필터 (선택)

### 2. 유사 문제 강의 추천

```
GET /api/recommendations/similar-lectures?lectureId={lectureId}&limit=5
Headers: X-User-Id: {userId}
Response: List<LectureResponse>
```

**로직:**

1. 현재 강의 정보 조회
2. 카테고리/태그/난이도 기준 유사 강의 검색
3. 사용자의 완료 여부 필터링
4. 유사도 점수 계산 및 정렬
5. 상위 N개 반환

**쿼리 파라미터:**

- `lectureId`: 기준 강의 ID (필수)
- `limit`: 추천 개수 (기본값: 5)
- `excludeCompleted`: 완료한 강의 제외 여부 (기본값: true)

### 3. 인기 추천

```
GET /api/recommendations/trending?limit=10
Response: List<CurriculumResponse>
```

**로직:**

- 최근 7일 수강 증가율 상위
- 평점 높은 커리큘럼
- 최신 커리큘럼

---

## 🚀 구현 계획

### Phase 1: 기본 추천 시스템

- [x] Database 설계 및 마이그레이션
- [ ] UserPreferences 엔티티 및 Repository
- [ ] RecommendationLogs 엔티티 및 Repository
- [ ] RecommendationService (기본 구조)

### Phase 2: 개인화 추천

- [ ] 수강 이력 분석
- [ ] 선호도 추출
- [ ] 점수 계산 로직
- [ ] Curriculum 추천 API

### Phase 3: 유사 문제 추천

- [ ] 강의 유사도 분석
- [ ] 카테고리/태그 매칭
- [ ] SimilarLecture 추천 API

### Phase 4: 통계 및 로깅

- [ ] 추천 로그 저장
- [ ] 클릭률 추적
- [ ] A/B 테스트 준비

---

## 📊 추가 고려사항

### 1. 성능 최적화

- 캐싱: 인기 추천 결과 캐싱 (Redis)
- 비동기 처리: 추천 로그 저장을 비동기로
- 페이징: 대량 데이터 처리

### 2. 확장 가능성

- 머신러닝 통합 가능 (추후)
- Collaborative Filtering
- Content-Based Filtering

### 3. 비즈니스 로직

- 추천 이유 제공 (가능하면)
- 사용자 피드백 수집
- 추천 정확도 개선

---

## 🎯 우선순위

1. **Priority 1**: 유사 문제 추천 (2번)
2. **Priority 2**: 개인화 추천 (1번)
3. **Priority 3**: 통계 및 로깅
