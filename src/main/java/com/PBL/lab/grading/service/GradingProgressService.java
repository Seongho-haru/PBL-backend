package com.PBL.lab.grading.service;

import com.PBL.lab.grading.dto.GradingResponse;
import com.PBL.lab.grading.dto.ProgressResponse;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.grading.repository.GradingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final Map<String, ProgressResponse> progressCache = new ConcurrentHashMap<>();

    // 스케줄러 (주기적으로 연결 상태 확인)
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final GradingService GradingService;

    /**
     * SSE 연결 등록
     */
    public void registerProgressListener(String token, SseEmitter emitter) {
        progressEmitters.put(token, emitter);
        log.info("Progress listener registered for token: {}", token);

        // 캐시된 진행상황이 있으면 즉시 전송
        ProgressResponse cachedProgress = progressCache.get(token);
        if (cachedProgress != null) {
            sendProgressUpdate(token, cachedProgress);
        }else{
            ProgressResponse initalProgress  = ProgressResponse.builder()
                    .totalTestCase(0)
                    .doneTestCase(0)
                    .currentTestCase(0)
                    .progressPercentage(0.0)
                    .build();
            updateProgress(token, initalProgress);
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
     * 채점 시작 알림 - 캐시된 객체를 업데이트
     */
    public void notifyGradingStarted(String token, int totalTestCases) {
        ProgressResponse currentProgress = progressCache.get(token);
        if (currentProgress != null) {
            // 캐시된 객체의 프로그레스만 업데이트
        currentProgress.setTotalTestCase(totalTestCases);
        currentProgress.setDoneTestCase(0);
        currentProgress.setCurrentTestCase(0);
        currentProgress.setProgressPercentage(0.0);
        updateProgress(token, currentProgress);
        }
    }

    /**
     * 테스트케이스 진행상황 업데이트
     */
    public void updateTestCaseProgress(String token, int currentTestCase, int totalTestCases, String testCaseStatus) {
        ProgressResponse currentProgress = progressCache.get(token);
        if (currentProgress != null) {
        currentProgress.setDoneTestCase(currentTestCase);
        currentProgress.setCurrentTestCase(currentTestCase + 1);
        currentProgress.setProgressPercentage((double) currentTestCase / totalTestCases * 100);
        updateProgress(token, currentProgress);
        }
    }

    /**
     * 채점 완료 알림
     */
    public void notifyGradingCompleted(String token) {
        ProgressResponse currentProgress = progressCache.get(token);
        if (currentProgress != null) {
        currentProgress.setDoneTestCase(currentProgress.getTotalTestCase());
        currentProgress.setProgressPercentage(100.0);
        updateProgress(token, currentProgress);
        }

        // 완료 후 잠시 후 연결 해제 (최종 결과 전송 후)
        scheduler.schedule(() -> unregisterProgressListener(token), 1, TimeUnit.SECONDS);
    }

    /**
     * 채점 에러 알림
     */
    public void notifyGradingError(String token) {
        ProgressResponse currentProgress = progressCache.get(token);
        if (currentProgress != null) {
        updateProgress(token, currentProgress);
        }
        // 에러 후 잠시 후 연결 해제
        scheduler.schedule(() -> unregisterProgressListener(token), 1, TimeUnit.SECONDS);
    }

    /**
     * 진행상황 업데이트 (캐시 저장 및 SSE 전송)
     */
    public void updateProgress(String token, ProgressResponse progress) {
        // 캐시 업데이트
        progressCache.put(token, progress);

        // SSE로 전송
        sendProgressUpdate(token, progress);
    }

    /**
     * SSE를 통한 진행상황 전송
     */
    private void sendProgressUpdate(String token, ProgressResponse progress) {
        SseEmitter emitter = progressEmitters.get(token);
        if (emitter != null) {
            try {
                Grading grading = GradingService.findByToken(token);
                GradingResponse gradingResponse = GradingResponse.from(grading,false,null);
                gradingResponse.setUpdatedAt(LocalDateTime.now());
                gradingResponse.setProgress(progress);
                emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(gradingResponse));

                if (progress != null) {
                    log.debug("Progress update sent for token: {} - {}/{} ({}%)",
                            token, progress.getDoneTestCase(), progress.getTotalTestCase(), progress.getProgressPercentage());
                }
            } catch (IOException e) {
                log.warn("Failed to send progress update for token: {}", token, e);
                // 연결 실패 시 정리
                unregisterProgressListener(token);
            } catch (Exception e) {
                log.error("Unexpected error sending progress update for token: {}", token, e);
                unregisterProgressListener(token);
            }
        } else {
            log.warn("No SSE emitter found for token: {}", token);
        }
    }

    /**
     * 현재 등록된 연결 수 반환
     */
    public int getActiveConnectionCount() {
        return progressEmitters.size();
    }

    /**
     * 진행상황 캐시 정리
     */
    public void clearProgressCache(String token) {
        progressCache.remove(token);
    }
}
