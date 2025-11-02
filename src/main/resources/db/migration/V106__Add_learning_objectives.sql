-- 학습 목표 필드 추가
-- 커리큘럼과 강의에 학습 목표 필드를 추가합니다 (선택 사항, null 허용)

-- 커리큘럼 테이블에 learning_objectives 컬럼 추가
ALTER TABLE curriculums 
ADD COLUMN learning_objectives TEXT;

-- 강의 테이블에 learning_objectives 컬럼 추가
ALTER TABLE lectures 
ADD COLUMN learning_objectives TEXT;

-- 커멘트 추가
COMMENT ON COLUMN curriculums.learning_objectives IS '커리큘럼 학습 목표 - 이 커리큘럼을 통해 달성할 수 있는 학습 목표';
COMMENT ON COLUMN lectures.learning_objectives IS '강의 학습 목표 - 이 강의를 통해 달성할 수 있는 학습 목표';

