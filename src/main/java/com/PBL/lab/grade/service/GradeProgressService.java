package com.PBL.lab.grade.service;

import com.PBL.lab.grade.dto.GradeResponse;
import com.PBL.lab.grade.dto.ProgressResponse;
import com.PBL.lab.grade.entity.Grade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PreDestroy;
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
 * - SSE 연결 관리
 * - Progress 정보만 캐시 (Grade는 DB에서 읽기)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradeProgressService {

    // 토큰별 SSE Emitter 저장
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 토큰별 ProgressResponse 캐시 (Grade 엔티티에 없는 진행률 정보만)
    private final Map<String, ProgressResponse> progressCache = new ConcurrentHashMap<>();

    // 스케줄러 (주기적으로 연결 상태 확인)
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final GradeService gradeService;

    /**
     * SSE 연결 등록
     */
    public SseEmitter registerProgressListener(String token) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(token, emitter);

        // Progress 정보 초기화
        ProgressResponse progress = ProgressResponse.builder()
                .totalTestCase(0)
                .doneTestCase(0)
                .currentTestCase(0)
                .progressPercentage(0.0)
                .build();
        progressCache.put(token, progress);

        emitter.onCompletion(() -> cleanup(token));
        emitter.onTimeout(() -> cleanup(token));
        emitter.onError(e -> cleanup(token));

        log.info("Progress listener registered for token: {}", token);

        // 초기 상태 전송
        sendProgress(token);

        return emitter;
    }

    /**
     * SSE 연결 해제
     */
    public void unregisterProgressListener(String token) {
        cleanup(token);
        log.info("Progress listener unregistered for token: {}", token);
    }

    /**
     * Progress 업데이트 (테스트케이스 진행)
     */
    public void updateProgress(String token, int doneTestCase, int totalTestCase) {
        ProgressResponse progress = progressCache.get(token);
        if (progress != null) {
            progress.setTotalTestCase(totalTestCase);
            progress.setDoneTestCase(doneTestCase);
            progress.setCurrentTestCase(doneTestCase < totalTestCase ? doneTestCase + 1 : totalTestCase);
            progress.setProgressPercentage((double) doneTestCase / totalTestCase * 100);

            log.debug("Progress updated for token: {} - {}/{} ({}%)",
                    token, doneTestCase, totalTestCase, progress.getProgressPercentage());

            sendProgress(token);
        } else {
            log.warn("Cannot update progress: no cache found for token: {}", token);
        }
    }

    /**
     * 진행상황 전송
     * DB에서 최신 Grade를 읽고, 캐시된 Progress와 합쳐서 SSE로 전송
     */
    public void sendProgress(String token) {
        SseEmitter emitter = emitters.get(token);
        if (emitter == null) {
            log.debug("No SSE emitter found for token: {}", token);
            return;
        }

        try {
            // 1. DB에서 최신 Grade 읽기
            Grade grade = gradeService.findByToken(token);

            // 2. GradeResponse 생성
            GradeResponse response = GradeResponse.from(grade, false, null);
            response.setUpdatedAt(LocalDateTime.now());

            // 3. 캐시에서 Progress 가져와서 설정
            ProgressResponse progress = progressCache.get(token);
            response.setProgress(progress);

            // 4. SSE 전송
            emitter.send(SseEmitter.event()
                    .name("progress")
                    .data(response));

            if (progress != null) {
                log.debug("Progress sent for token: {} - {}/{} ({}%)",
                        token, progress.getDoneTestCase(), progress.getTotalTestCase(),
                        progress.getProgressPercentage());
            }
        } catch (IOException e) {
            log.warn("Failed to send progress for token: {}", token, e);
            cleanup(token);
        } catch (Exception e) {
            log.error("Unexpected error sending progress for token: {}", token, e);
            cleanup(token);
        }
    }

    /**
     * 현재 등록된 연결 수 반환
     */
    public int getActiveConnectionCount() {
        return emitters.size();
    }

    /**
     * 리소스 정리
     */
    private void cleanup(String token) {
        SseEmitter emitter = emitters.remove(token);
        progressCache.remove(token);

        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("Error completing SSE emitter for token: {}", token, e);
            }
        }
    }

    /**
     * 서비스 종료 시 스레드 풀 정리
     *
     * 애플리케이션 종료 시 ScheduledExecutorService를 안전하게 종료하여
     * graceful shutdown을 보장합니다.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down GradeProgressService scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Scheduler did not terminate in time, forcing shutdown...");
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("Scheduler did not terminate after forced shutdown");
                }
            }
            log.info("GradeProgressService scheduler shut down successfully");
        } catch (InterruptedException e) {
            log.error("Interrupted while shutting down scheduler", e);
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
