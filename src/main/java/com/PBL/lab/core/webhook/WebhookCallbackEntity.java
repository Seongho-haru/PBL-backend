package com.PBL.lab.core.webhook;

import com.PBL.lab.core.entity.Constraints;

/**
 * Webhook 콜백을 지원하는 엔티티의 공통 인터페이스
 *
 * Submission과 Grading 엔티티가 이 인터페이스를 구현하여 webhook 기능을 사용할 수 있습니다.
 *
 * === 주요 목적 ===
 * - Webhook 전송에 필요한 공통 메서드 정의
 * - BaseWebhookService에서 타입 안전하게 사용
 * - Judge0와 Grade 모듈 간 코드 중복 제거
 *
 * === 구현 클래스 ===
 * - judge0.entity.Submission
 * - grade.entity.Grading
 */
public interface WebhookCallbackEntity {

    /**
     * 엔티티의 고유 토큰 반환
     * - Webhook 응답의 식별자로 사용
     * - HTTP 헤더에 X-Judge0-Token으로 전송
     *
     * @return UUID 형식의 토큰
     */
    String getToken();

    /**
     * 실행 제약조건 반환
     * - callback URL이 포함되어 있음
     * - 시간/메모리 제한 등의 정보 포함
     *
     * @return Constraints 객체
     */
    Constraints getConstraints();

    /**
     * 소스 코드 반환
     * - Webhook 응답에 포함될 수 있음
     *
     * @return 실행할 소스 코드
     */
    String getSourceCode();

    /**
     * 프로그래밍 언어 ID 반환
     * - Webhook 응답에 포함
     *
     * @return 언어 ID
     */
    Integer getLanguageId();

    /**
     * 실행 상태 ID 반환
     * - 1: Queue, 2: Processing, 3: Accepted, etc.
     * - Webhook 응답에 포함
     *
     * @return 상태 ID
     */
    Integer getStatusId();
}
