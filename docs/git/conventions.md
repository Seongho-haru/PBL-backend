# Git 컨벤션 (한국어)

본 문서는 이 저장소에서 일관된 협업을 위한 Git 사용 규칙을 정의합니다. 변경이 필요하면 PR로 제안하세요.

## 1) 브랜치 전략 (Git Flow)

- 기본 브랜치
  - `main`: 프로덕션 배포 이력. 항상 배포 가능한 상태 유지
  - `develop`: 다음 릴리스를 위한 통합 브랜치
- 보조 브랜치
  - 기능: `feature/<이슈번호-선택>/<짧은-설명>`
  - 버그: `fix/<이슈번호-선택>/<짧은-설명>`
  - 긴급수정: `hotfix/<버전-또는-이슈번호>/<짧은-설명>`
  - 릴리스: `release/<버전>`

권장 네이밍 예

```
feature/123/add-submission-retry
fix/456/null-pointer-on-language-load
hotfix/1.2.1/fix-docker-timeout
release/1.3.0
```

규칙

- 기능/버그 작업은 `develop`에서 분기 → 작업 → PR로 `develop`에 병합
- `release`는 `develop`에서 분기하여 안정화 후 `main`과 `develop`에 병합, 태깅
- `hotfix`는 `main`에서 분기하여 수정 후 `main`과 `develop`에 병합, 태깅

## 2) 커밋 메시지 규칙 (Conventional Commits, 한국어)

포맷

```
<type>(<scope>)<!>: <한 줄 요약>

<본문 - 선택>

<푸터 - 선택>
```

type 목록

- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 스타일(포매팅, 세미콜론 등; 코드 변경 없음)
- `refactor`: 리팩터링(기능 변경 없음)
- `perf`: 성능 개선
- `test`: 테스트 추가/수정
- `build`: 빌드 시스템/의존성 변경(gradle, docker 등)
- `ci`: CI 설정/스크립트 변경
- `chore`: 기타 자잘한 변경(코드 변경 거의 없음)
- `revert`: 이전 커밋 되돌림

scope 예시(이 프로젝트 권장)
`api`, `controller`, `service`, `repository`, `entity`, `config`, `docker`, `infra`, `db`, `migration`, `security`, `logging`, `docs`, `build`, `deps`, `test`

규칙

- 한 줄 요약은 평서형, 마침표 없이, 한국어 사용 권장(영문도 허용)
- 본문에는 변경 이유(Why), 의도, 고려사항, 사이드이펙트 등을 서술
- 이슈 연결은 푸터에서 `Resolves: #123` 또는 `Refs: #123`
- 깨지는 변경은 `!` 또는 `BREAKING CHANGE:` 푸터로 명시

예시

```
feat(api): 제출 재시도 파라미터 추가

요청 실패에 대비해 재시도 횟수를 서버에서 제한적으로 허용합니다.
기본값은 0이며, 상한은 3으로 설정했습니다.

Resolves: #123
```

```
fix(repository): Submission 조회 시 N+1 발생 제거

fetch join으로 전환하여 불필요한 쿼리를 제거했습니다.
성능 측정 기준 케이스에서 응답 120ms → 75ms.

Refs: #456
```

```
feat(api)!: 채점 결과 상태코드 구조 변경

상태코드를 세분화하여 클라이언트 처리 분기를 단순화합니다.

BREAKING CHANGE: 기존 `status` 단일 필드를 `status.code`/`status.detail`로 분리했습니다.
```

## 3) PR 규칙

- 제목: 첫 줄은 커밋 규칙과 동일 포맷 사용 권장 (예: `feat(service): ...`)
- 본문 체크리스트(권장)
  - 변경 목적과 배경
  - 주요 변경점(전/후 비교 포함 가능)
  - 테스트 방법(재현/검증 절차)
  - 호환성/마이그레이션 주의사항
  - 관련 이슈/티켓 링크
- 크기: 200라인 내외 권장, 기능 단위로 작게 나누기
- 리뷰: 최소 1명 이상의 승인
- 병합 전략
  - `feature/fix` → `develop`: Squash merge 권장(히스토리 간결)
  - `release/hotfix`: Merge commit 유지(변경 이력 보존)
- 금지: 의미 없는 WIP PR 남발. 필요 시 Draft로 올리고 TODO 명시

## 4) 태그/릴리스

- 태그 규칙: `v<MAJOR>.<MINOR>.<PATCH>` (예: `v1.3.0`)
- 기준: `main`에 릴리스/핫픽스 병합 시 태깅
- 릴리스 노트: 하이라이트(주요 기능/수정), 마이그레이션, 감사(옵션)

## 5) 머지/리베이스 정책

- 로컬 작업 시 `git rebase`로 깔끔한 커밋 정리 허용
- 원격 공개된 브랜치의 히스토리 재작성 금지(force-push 금지), 단 `feature` 개인 브랜치는 협의 하에 허용
- 충돌 해결 시 커밋 메시지에 원인과 해결 방식을 간단히 기록

## 6) 커밋 단위 권장

- 한 커밋에는 하나의 논리적 변경만 포함
- 포맷팅 전용 커밋은 코드 변경과 분리
- 대규모 리팩터링은 선행 PR로 구조 변경 → 후속 PR로 기능 변경

## 7) 이슈 관리

- 이슈 제목: 간결한 한글 요약(필요 시 영문 병기)
- 라벨을 통해 유형/우선순위 명확화
- PR에서 `Resolves #번호`로 자동 닫힘 활용

---

본 문서는 팀 합의에 따라 업데이트됩니다. 제안은 `docs/git/conventions.md`에 대한 PR로 제출하세요.

