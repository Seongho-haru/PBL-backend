package com.PBL.lab.grading.service;

import com.PBL.lab.grading.dto.GradingProgressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.PBL.lab.core.enums.Status;

/**
 * 채점 진행상황 SSE 서비스
 * 
 * 채점 진행상황을 실시간으로 클라이언트에 전송하기 위한 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradingProgressService {

    // 토큰별 SSE Emitter 저장
    private final Map<String, SseEmitter> progressEmitters = new ConcurrentHashMap<>();
    
    // 토큰별 진행상황 저장
    private final Map<String, GradingProgressResponse> progressCache = new ConcurrentHashMap<>();
    
    // 스케줄러 (주기적으로 연결 상태 확인)
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * SSE 연결 등록
     */
    public void registerProgressListener(String token, SseEmitter emitter) {
        progressEmitters.put(token, emitter);
        log.info("Progress listener registered for token: {}", token);
        
        // 캐시된 진행상황이 있으면 즉시 전송
        GradingProgressResponse cachedProgress = progressCache.get(token);
        if (cachedProgress != null) {
            sendProgressUpdate(token, cachedProgress);
        }
    }

    /**
     * SSE 연결 해제
     */
    public void unregisterProgressListener(String token) {
        SseEmitter emitter = progressEmitters.remove(token);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("Error completing SSE emitter for token: {}", token, e);
            }
        }
        log.info("Progress listener unregistered for token: {}", token);
    }

    /**
     * 채점 시작 알림
     */
    public void notifyGradingStarted(String token, Long problemId, int totalTestCases) {
        GradingProgressResponse progress = GradingProgressResponse.builder()
                .token(token)
                .problemId(problemId)
                .totalTestCase(totalTestCases)
                .doneTestCase(0)
                .currentTestCase(1)
                .progressPercentage(0.0)
                .status(GradingProgressResponse.StatusResponse.from(Status.PROCESS))
                .currentStatus("STARTING")
                .updatedAt(LocalDateTime.now())
                .message("채점을 시작합니다... (총 " + totalTestCases + "개 테스트케이스)")
                .build();
        
        updateProgress(token, progress);
    }

    /**
     * 테스트케이스 진행상황 업데이트
     */
    public void updateTestCaseProgress(String token, int currentTestCase, int totalTestCases, String testCaseStatus) {
        GradingProgressResponse currentProgress = progressCache.get(token);
        if (currentProgress == null) {
            currentProgress = GradingProgressResponse.builder()
                    .token(token)
                    .totalTestCase(totalTestCases)
                    .doneTestCase(currentTestCase)
                    .currentTestCase(currentTestCase + 1)
                    .status(GradingProgressResponse.StatusResponse.from(Status.PROCESS))
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            currentProgress.updateProgress(currentTestCase, totalTestCases);
            currentProgress.setCurrentStatus(testCaseStatus);
            currentProgress.setUpdatedAt(LocalDateTime.now());
        }
        
        currentProgress.setMessage("테스트케이스 " + (currentTestCase + 1) + "/" + totalTestCases + " 처리 중... (" + testCaseStatus + ")");
        
        updateProgress(token, currentProgress);
    }

    /**
     * 채점 완료 알림
     */
    public void notifyGradingCompleted(String token) {
        GradingProgressResponse currentProgress = progressCache.get(token);
        if (currentProgress != null) {
            currentProgress.markComplete(currentProgress.getTotalTestCase());
            currentProgress.setMessage("채점이 완료되었습니다!");
        } else {
            currentProgress = GradingProgressResponse.builder()
                    .token(token)
                    .status(GradingProgressResponse.StatusResponse.from(Status.AC))
                    .currentStatus("COMPLETED")
                    .progressPercentage(100.0)
                    .updatedAt(LocalDateTime.now())
                    .message("채점이 완료되었습니다!")
                    .build();
        }
        
        updateProgress(token, currentProgress);
        
        // 완료 후 잠시 후 연결 해제 (최종 결과 전송 후)
        scheduler.schedule(() -> unregisterProgressListener(token), 5, TimeUnit.SECONDS);
    }

    /**
     * 채점 에러 알림
     */
    public void notifyGradingError(String token, String errorMessage) {
        GradingProgressResponse currentProgress = progressCache.get(token);
        if (currentProgress != null) {
            currentProgress.markError(errorMessage);
        } else {
            currentProgress = GradingProgressResponse.builder()
                    .token(token)
                    .status(GradingProgressResponse.StatusResponse.from(Status.BOXERR))
                    .currentStatus("ERROR")
                    .updatedAt(LocalDateTime.now())
                    .message(errorMessage)
                    .build();
        }
        
        updateProgress(token, currentProgress);
        
        // 에러 후 잠시 후 연결 해제
        scheduler.schedule(() -> unregisterProgressListener(token), 3, TimeUnit.SECONDS);
    }

    /**
     * 진행상황 업데이트 (내부 메서드)
     */
    private void updateProgress(String token, GradingProgressResponse progress) {
        // 캐시 업데이트
        progressCache.put(token, progress);
        
        // SSE로 전송
        sendProgressUpdate(token, progress);
    }

    /**
     * SSE를 통한 진행상황 전송
     */
    private void sendProgressUpdate(String token, GradingProgressResponse progress) {
        SseEmitter emitter = progressEmitters.get(token);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(progress));
                log.debug("Progress update sent for token: {} - {}/{} ({}%)", 
                        token, progress.getDoneTestCase(), progress.getTotalTestCase(), progress.getProgressPercentage());
            } catch (IOException e) {
                log.warn("Failed to send progress update for token: {}", token, e);
                // 연결 실패 시 정리
                unregisterProgressListener(token);
            } catch (Exception e) {
                log.error("Unexpected error sending progress update for token: {}", token, e);
                unregisterProgressListener(token);
            }
        }
    }

    /**
     * 현재 등록된 연결 수 반환
     */
    public int getActiveConnectionCount() {
        return progressEmitters.size();
    }

    /**
     * 특정 토큰의 진행상황 조회
     */
    public GradingProgressResponse getProgress(String token) {
        return progressCache.get(token);
    }

    /**
     * 진행상황 캐시 정리
     */
    public void clearProgressCache(String token) {
        progressCache.remove(token);
    }
}
