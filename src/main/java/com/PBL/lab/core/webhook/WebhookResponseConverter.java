package com.PBL.lab.core.webhook;

/**
 * 엔티티를 Webhook 응답 객체로 변환하는 컨버터 인터페이스
 *
 * === 주요 목적 ===
 * - BaseWebhookService가 구체적인 Response 타입을 모르고도 변환 가능
 * - Judge0와 Grade가 각자의 Response 타입으로 변환
 * - 함수형 인터페이스로 간결한 구현 지원
 *
 * === 사용 예시 ===
 * <pre>
 * // Judge0에서 구현
 * WebhookResponseConverter&lt;Submission, SubmissionResponse&gt; converter =
 *     (submission, base64) -> SubmissionResponse.from(submission, base64, null);
 *
 * // Grade에서 구현
 * WebhookResponseConverter&lt;Grading, GradingResponse&gt; converter =
 *     (grading, base64) -> GradingResponse.from(grading, base64, null);
 * </pre>
 *
 * @param <T> 변환할 엔티티 타입 (WebhookCallbackEntity를 구현해야 함)
 * @param <R> 변환 결과 Response DTO 타입
 */
@FunctionalInterface
public interface WebhookResponseConverter<T extends WebhookCallbackEntity, R> {

    /**
     * 엔티티를 Webhook 응답 DTO로 변환
     *
     * @param entity 변환할 엔티티 (Submission 또는 Grading)
     * @param base64Encoded 출력 데이터를 Base64로 인코딩할지 여부
     * @return Webhook 응답 DTO (SubmissionResponse 또는 GradingResponse)
     */
    R convert(T entity, boolean base64Encoded);
}
