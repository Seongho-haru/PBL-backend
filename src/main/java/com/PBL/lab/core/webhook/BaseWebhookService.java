package com.PBL.lab.core.webhook;

import com.PBL.lab.core.config.FeatureFlagsConfig;
import com.PBL.lab.core.config.SystemConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * Webhook 전송을 위한 기본 서비스 추상 클래스
 *
 * === 주요 목적 ===
 * - Judge0와 Grade 모듈에서 공통으로 사용하는 Webhook 로직 제공
 * - HTTP 요청 전송, 재시도 로직, 오류 처리 등 공통 기능 구현
 * - 각 모듈은 Response 변환 로직만 구현하면 됨
 *
 * === 사용 방법 ===
 * 1. 이 클래스를 상속받는 서비스 생성 (SubmissionWebhookService, GradingWebhookService)
 * 2. getResponseConverter() 메서드를 구현하여 Response 변환 로직 제공
 * 3. sendCallback(entity) 호출하여 webhook 전송
 *
 * === 제공 기능 ===
 * - 비동기 콜백 전송
 * - 재시도 로직 (exponential backoff)
 * - Callback URL 유효성 검증
 * - Feature flag 지원 (콜백 기능 on/off)
 *
 * @param <T> Webhook을 보낼 엔티티 타입 (Submission 또는 Grading)
 * @param <R> Webhook 응답 DTO 타입 (SubmissionResponse 또는 GradingResponse)
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseWebhookService<T extends WebhookCallbackEntity, R> {

    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;
    protected final FeatureFlagsConfig featureFlagsConfig;
    protected final SystemConfig systemConfig;

    /**
     * 응답 변환기 제공 (자식 클래스에서 구현 필수)
     *
     * 각 모듈에서 자신의 Response 타입으로 변환하는 로직을 제공합니다.
     *
     * 예시:
     * <pre>
     * @Override
     * protected WebhookResponseConverter&lt;Submission, SubmissionResponse&gt; getResponseConverter() {
     *     return (submission, base64) -> SubmissionResponse.from(submission, base64, null);
     * }
     * </pre>
     *
     * @return 엔티티를 Response DTO로 변환하는 컨버터
     */
    protected abstract WebhookResponseConverter<T, R> getResponseConverter();

    /**
     * Callback 전송
     *
     * 엔티티가 완료되었을 때 외부 URL로 결과를 전송합니다.
     * 비동기로 실행되므로 호출 즉시 반환됩니다.
     *
     * @param entity 콜백을 보낼 엔티티 (Submission 또는 Grading)
     */
    public void sendCallback(T entity) {
        if (entity.getConstraints() == null ||
            entity.getConstraints().getCallbackUrl() == null ||
            entity.getConstraints().getCallbackUrl().trim().isEmpty()) {
            return;
        }

        if (!featureFlagsConfig.isEnableCallbacks()) {
            log.warn("Callbacks are disabled, skipping callback for: {}", entity.getToken());
            return;
        }

        try {
            log.info("Sending callback for: {} to URL: {}",
                    entity.getToken(), entity.getConstraints().getCallbackUrl());

            // 비동기 실행
            CompletableFuture.runAsync(() -> sendCallbackWithRetry(entity));

        } catch (Exception e) {
            log.error("Failed to initiate callback for: {}", entity.getToken(), e);
        }
    }

    /**
     * 재시도 로직을 포함한 콜백 전송
     *
     * 실패 시 exponential backoff 전략으로 재시도합니다:
     * - 1회 실패: 1초 대기 후 재시도
     * - 2회 실패: 2초 대기 후 재시도
     * - 3회 실패: 4초 대기 후 재시도
     *
     * @param entity 콜백을 보낼 엔티티
     */
    protected void sendCallbackWithRetry(T entity) {
        int maxAttempts = systemConfig.getCallbacksMaxTries();
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                // 응답 객체 생성 (자식 클래스의 converter 사용)
                R response = getResponseConverter().convert(entity, true);

                // JSON 변환
                String jsonPayload = objectMapper.writeValueAsString(response);

                // HTTP 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("User-Agent", "Judge0-Spring/1.13.1");
                headers.set("X-Judge0-Token", entity.getToken());

                HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

                // HTTP PUT 요청 (Judge0 표준)
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                    entity.getConstraints().getCallbackUrl(),
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    log.info("Successfully sent callback for: {} to URL: {}",
                            entity.getToken(), entity.getConstraints().getCallbackUrl());
                    return; // 성공 시 종료
                } else {
                    log.warn("Callback returned non-2xx status for: {} - Status: {}",
                            entity.getToken(), responseEntity.getStatusCode());
                }

            } catch (Exception e) {
                attempt++;
                log.error("Callback failed for: {} to URL: {} (attempt {}/{}) - {}",
                        entity.getToken(),
                        entity.getConstraints().getCallbackUrl(),
                        attempt,
                        maxAttempts,
                        e.getMessage());

                if (attempt >= maxAttempts) {
                    log.error("All callback attempts failed for: {}", entity.getToken());
                    return;
                }

                // Exponential backoff: 1s, 2s, 4s...
                try {
                    long delay = (long) (1000 * Math.pow(2, attempt - 1));
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            attempt++;
        }
    }

    /**
     * Callback URL 유효성 검증
     *
     * 다음 항목을 검증합니다:
     * - URL 형식이 올바른지
     * - 프로토콜이 http 또는 https인지
     * - 호스트가 존재하는지
     *
     * @param callbackUrl 검증할 URL
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateCallbackUrl(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.trim().isEmpty()) {
            return false;
        }

        try {
            java.net.URL url = new java.net.URL(callbackUrl);
            String protocol = url.getProtocol().toLowerCase();

            // 프로토콜 검증
            if (!protocol.equals("http") && !protocol.equals("https")) {
                log.warn("Invalid callback URL protocol: {}", protocol);
                return false;
            }

            // 호스트 검증
            String host = url.getHost();
            if (host == null || host.isEmpty()) {
                log.warn("Invalid callback URL host: {}", callbackUrl);
                return false;
            }

            // 추가 보안 검증 (필요 시 구현)
            // 예: private IP 차단, localhost 차단 등

            return true;

        } catch (Exception e) {
            log.warn("Invalid callback URL: {}", callbackUrl, e);
            return false;
        }
    }
}
