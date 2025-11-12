package com.PBL.lab.grade.service;

import com.PBL.lab.core.dto.StatusResponse;
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
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradeProgressService {

    // 토큰별 SSE Emitter 저장
    private final Map<String, SseEmitter> progressEmitters = new ConcurrentHashMap<>();

    // 토큰별 GradeResponse 캐시 (DB 읽기 제거를 위해 전체 Grade 정보 캐싱)
    private final Map<String, GradeResponse> gradeResponseCache = new ConcurrentHashMap<>();

    // 스케줄러 (주기적으로 연결 상태 확인)
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final GradeService gradeService;

    /**
     * SSE 연결 등록
     */
    public void registerProgressListener(String token, SseEmitter emitter) {
        progressEmitters.put(token, emitter);
        log.info("Progress listener registered for token: {}", token);

        // 캐시된 GradeResponse가 있으면 즉시 전송
        GradeResponse cachedResponse = gradeResponseCache.get(token);
        if (cachedResponse != null) {
            sendProgressUpdate(token);
        } else {
            // DB에서 한 번만 읽어서 캐시 초기화
            Grade grade = gradeService.findByToken(token);
            GradeResponse gradeResponse = GradeResponse.from(grade, false, null);

            // ProgressResponse 초기화
            ProgressResponse progress = ProgressResponse.builder()
                    .totalTestCase(0)
                    .doneTestCase(0)
                    .currentTestCase(0)
                    .progressPercentage(0.0)
                    .build();

            gradeResponse.setProgress(progress);
            gradeResponse.setUpdatedAt(LocalDateTime.now());

            // 캐시에 저장
            gradeResponseCache.put(token, gradeResponse);

            // 초기 이벤트 전송
            sendProgressUpdate(token);
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
     * 채점 시작 알림 - 캐시된 GradeResponse 업데이트
     */
    public void notifyGradingStarted(String token, int totalTestCases) {
        GradeResponse cached = gradeResponseCache.get(token);

        // 캐시가 없으면 생성 (SSE 연결 전에 채점이 시작된 경우)
        if (cached == null) {
            log.debug("Cache not found, initializing for token: {}", token);
            Grade grade = gradeService.findByToken(token);
            cached = GradeResponse.from(grade, false, null);

            ProgressResponse progress = ProgressResponse.builder()
                    .totalTestCase(totalTestCases)
                    .doneTestCase(0)
                    .currentTestCase(0)
                    .progressPercentage(0.0)
                    .build();

            cached.setProgress(progress);
            cached.setUpdatedAt(LocalDateTime.now());
            gradeResponseCache.put(token, cached);
        } else if (cached.getProgress() != null) {
            // 캐시된 GradeResponse의 프로그레스 업데이트
            ProgressResponse progress = cached.getProgress();
            progress.setTotalTestCase(totalTestCases);
            progress.setDoneTestCase(0);
            progress.setCurrentTestCase(0);
            progress.setProgressPercentage(0.0);
        }

        sendProgressUpdate(token);
    }

    /**
     * 테스트케이스 진행상황 업데이트
     */
    public void updateTestCaseProgress(String token, int currentTestCase, int totalTestCases, String testCaseStatus) {
        GradeResponse cached = gradeResponseCache.get(token);
        if (cached != null && cached.getProgress() != null) {
            // Progress 업데이트 (DB 읽기 제거 - 트랜잭션 충돌 방지)
            ProgressResponse progress = cached.getProgress();
            progress.setDoneTestCase(currentTestCase);
            progress.setCurrentTestCase(currentTestCase + 1);
            progress.setProgressPercentage((double) currentTestCase / totalTestCases * 100);
            sendProgressUpdate(token);
        } else {
            log.warn("Cannot update test case progress: no cached response for token: {}", token);
        }
    }

    /**
     * 채점 완료 알림
     */
    public void notifyGradingCompleted(String token) {
        GradeResponse cached = gradeResponseCache.get(token);
        if (cached != null && cached.getProgress() != null) {
            // Progress 완료 처리 (DB 읽기 제거 - 트랜잭션 충돌 방지)
            ProgressResponse progress = cached.getProgress();
            progress.setDoneTestCase(progress.getTotalTestCase());
            progress.setProgressPercentage(100.0);
            sendProgressUpdate(token);

            // 트랜잭션 커밋 후 최종 상태 업데이트 (0.5초 후)
            scheduler.schedule(() -> {
                try {
                    Grade grade = gradeService.findByToken(token);
                    cached.setStatus(StatusResponse.from(grade.getStatus()));
                    cached.setTime(grade.getTime());
                    cached.setMemory(grade.getMemory());
                    cached.setExitCode(grade.getExitCode());
                    sendProgressUpdate(token);
                    log.debug("Final status updated for token: {}", token);
                } catch (Exception e) {
                    log.warn("Failed to update final status for token: {}", token, e);
                }
            }, 500, TimeUnit.MILLISECONDS);
        } else {
            log.warn("Cannot notify grading completed: no cached response for token: {}", token);
        }

        // 완료 후 잠시 후 연결 해제 (최종 결과 전송 후)
        scheduler.schedule(() -> unregisterProgressListener(token), 2, TimeUnit.SECONDS);
    }

    /**
     * 채점 에러 알림
     */
    public void notifyGradingError(String token) {
        GradeResponse cached = gradeResponseCache.get(token);
        if (cached != null) {
            // 에러 상태 업데이트 (DB에서 최신 Grade 읽기)
            Grade grade = gradeService.findByToken(token);
            cached.setStatus(StatusResponse.from(grade.getStatus()));
            cached.setMessage(grade.getMessage());
            sendProgressUpdate(token);
        } else {
            log.warn("Cannot notify grading error: no cached response for token: {}", token);
        }
        // 에러 후 잠시 후 연결 해제
        scheduler.schedule(() -> unregisterProgressListener(token), 1, TimeUnit.SECONDS);
    }

    /**
     * 진행상황 업데이트 (캐시 업데이트 및 SSE 전송)
     * 
     * @deprecated 더 이상 사용되지 않음. 캐시는 자동으로 업데이트됨
     */
    @Deprecated
    public void updateProgress(String token, ProgressResponse progress) {
        // 캐시된 GradeResponse의 progress를 업데이트
        GradeResponse cached = gradeResponseCache.get(token);
        if (cached != null) {
            cached.setProgress(progress);
            sendProgressUpdate(token);
        }
    }

    /**
     * SSE를 통한 진행상황 전송 (DB 읽기 제거, 캐시만 사용)
     */
    private void sendProgressUpdate(String token) {
        SseEmitter emitter = progressEmitters.get(token);
        GradeResponse gradeResponse = gradeResponseCache.get(token);

        if (emitter != null && gradeResponse != null) {
            try {
                gradeResponse.setUpdatedAt(LocalDateTime.now());
                emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(gradeResponse));

                ProgressResponse progress = gradeResponse.getProgress();
                if (progress != null) {
                    log.debug("Progress update sent for token: {} - {}/{} ({}%)",
                            token, progress.getDoneTestCase(), progress.getTotalTestCase(),
                            progress.getProgressPercentage());
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
            if (emitter == null) {
                log.warn("No SSE emitter found for token: {}", token);
            }
            if (gradeResponse == null) {
                log.warn("No cached GradeResponse found for token: {}", token);
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
     * 진행상황 캐시 정리
     */
    public void clearProgressCache(String token) {
        gradeResponseCache.remove(token);
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
