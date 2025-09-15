package com.PBL.lab.judge0.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.PBL.lab.judge0.dto.SubmissionResponse;
import com.PBL.lab.judge0.entity.Submission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Webhook Service
 * 
 * Handles callback notifications to external URLs.
 * Includes retry logic and error handling.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ConfigService configService;

    /**
     * Send callback notification for submission
     */
    public void sendCallback(Submission submission) {
        if (submission.getCallbackUrl() == null || submission.getCallbackUrl().trim().isEmpty()) {
            return;
        }

        if (!configService.isCallbacksEnabled()) {
            log.warn("Callbacks are disabled, skipping callback for submission: {}", submission.getToken());
            return;
        }

        try {
            log.info("Sending callback for submission: {} to URL: {}", 
                    submission.getToken(), submission.getCallbackUrl());

            // Send callback asynchronously
            CompletableFuture.runAsync(() -> sendCallbackWithRetry(submission));

        } catch (Exception e) {
            log.error("Failed to initiate callback for submission: {}", submission.getToken(), e);
        }
    }

    /**
     * Send callback with retry logic
     */
    public void sendCallbackWithRetry(Submission submission) {
        int maxAttempts = configService.getCallbacksMaxTries();
        int attempt = 0;
        
        while (attempt < maxAttempts) {
            try {
                // Create submission response with all fields and force base64 encoding
                SubmissionResponse response = SubmissionResponse.from(submission, true, null);
                
                // Convert to JSON
                String jsonPayload = objectMapper.writeValueAsString(response);
                
                // Create headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("User-Agent", "Judge0-Spring/1.13.1");
                headers.set("X-Judge0-Token", submission.getToken());
                
                // Create request entity
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
                
                // Send HTTP PUT request (as per Judge0 specification)
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                    submission.getCallbackUrl(),
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
                );
                
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    log.info("Successfully sent callback for submission: {} to URL: {}", 
                            submission.getToken(), submission.getCallbackUrl());
                    return; // Success, exit retry loop
                } else {
                    log.warn("Callback returned non-2xx status for submission: {} - Status: {}", 
                            submission.getToken(), responseEntity.getStatusCode());
                }
                
            } catch (Exception e) {
                attempt++;
                log.error("Callback failed for submission: {} to URL: {} (attempt {}/{}) - {}", 
                        submission.getToken(), submission.getCallbackUrl(), attempt, maxAttempts, e.getMessage());
                
                if (attempt >= maxAttempts) {
                    log.error("All callback attempts failed for submission: {}", submission.getToken());
                    return;
                }
                
                // Wait before retry (exponential backoff: 1s, 2s, 4s...)
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
     * Validate callback URL
     */
    public boolean validateCallbackUrl(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.trim().isEmpty()) {
            return false;
        }

        try {
            // Basic URL validation
            java.net.URL url = new java.net.URL(callbackUrl);
            
            // Check protocol
            String protocol = url.getProtocol().toLowerCase();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                log.warn("Invalid callback URL protocol: {}", protocol);
                return false;
            }
            
            // Check if it's a valid host
            String host = url.getHost();
            if (host == null || host.isEmpty()) {
                log.warn("Invalid callback URL host: {}", callbackUrl);
                return false;
            }
            
            // Additional security checks could be added here
            // e.g., blacklist private IP ranges, localhost, etc.
            
            return true;
            
        } catch (Exception e) {
            log.warn("Invalid callback URL: {}", callbackUrl, e);
            return false;
        }
    }

    /**
     * Retry failed callbacks
     * This method can be called periodically to retry failed callbacks
     */
    public void retryFailedCallbacks() {
        try {
            log.debug("Starting retry of failed callbacks");
            
            // In a real implementation, you would:
            // 1. Query database for submissions with failed callbacks
            // 2. Retry sending callbacks for those submissions
            // 3. Update failure count and timestamps
            
            // For now, this is a placeholder
            log.debug("Completed retry of failed callbacks");
            
        } catch (Exception e) {
            log.error("Error during callback retry process", e);
        }
    }

    /**
     * Test callback URL connectivity
     */
    public CallbackTestResult testCallback(String callbackUrl) {
        if (!validateCallbackUrl(callbackUrl)) {
            return CallbackTestResult.invalid("Invalid callback URL format");
        }

        try {
            // Send a simple HEAD request to test connectivity
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Judge0-Spring/1.13.1");
            headers.set("X-Judge0-Test", "true");
            
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                callbackUrl,
                HttpMethod.HEAD,
                requestEntity,
                Void.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return CallbackTestResult.success("Callback URL is reachable");
            } else {
                return CallbackTestResult.warning("Callback URL returned status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            return CallbackTestResult.error("Failed to connect to callback URL: " + e.getMessage());
        }
    }

    /**
     * Get callback statistics
     */
    public CallbackStatistics getCallbackStatistics() {
        // In a real implementation, this would query metrics from database or cache
        return CallbackStatistics.builder()
                .totalCallbacks(0)
                .successfulCallbacks(0)
                .failedCallbacks(0)
                .averageResponseTime(Duration.ZERO)
                .build();
    }

    /**
     * Callback test result
     */
    public static class CallbackTestResult {
        private final boolean success;
        private final String message;
        private final CallbackTestStatus status;

        private CallbackTestResult(boolean success, String message, CallbackTestStatus status) {
            this.success = success;
            this.message = message;
            this.status = status;
        }

        public static CallbackTestResult success(String message) {
            return new CallbackTestResult(true, message, CallbackTestStatus.SUCCESS);
        }

        public static CallbackTestResult warning(String message) {
            return new CallbackTestResult(true, message, CallbackTestStatus.WARNING);
        }

        public static CallbackTestResult error(String message) {
            return new CallbackTestResult(false, message, CallbackTestStatus.ERROR);
        }

        public static CallbackTestResult invalid(String message) {
            return new CallbackTestResult(false, message, CallbackTestStatus.INVALID);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public CallbackTestStatus getStatus() { return status; }
    }

    /**
     * Callback test status
     */
    public enum CallbackTestStatus {
        SUCCESS, WARNING, ERROR, INVALID
    }

    /**
     * Callback statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class CallbackStatistics {
        private long totalCallbacks;
        private long successfulCallbacks;
        private long failedCallbacks;
        private Duration averageResponseTime;
        
        public double getSuccessRate() {
            if (totalCallbacks == 0) {
                return 0.0;
            }
            return (double) successfulCallbacks / totalCallbacks * 100.0;
        }
    }
}
