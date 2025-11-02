package com.PBL.recommendation.job;

import com.PBL.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 추천 결과 워밍업 백그라운드 작업
 * 
 * 사용자 로그인 시 추천 결과를 미리 계산하여 캐시에 저장하는 백그라운드 작업입니다.
 * 이를 통해 첫 추천 요청 시 빠른 응답을 제공할 수 있습니다.
 * 
 * 주요 기능:
 * - 통합 추천 결과 워밍업
 * - 커리큘럼 추천 결과 워밍업
 * - 강의 추천 결과 워밍업
 * 
 * 리소스 관리:
 * - 우선순위가 높은 첫 페이지만 미리 계산 (page=0, size=12)
 * - 동시 실행 작업 수를 제한하여 시스템 부하 방지
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationWarmupJob {

    private final RecommendationService recommendationService;
    
    // 동시 실행 작업 추적 (리소스 제한)
    private static final ConcurrentHashMap<Long, Boolean> activeWarmups = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Long> warmupTimestamps = new ConcurrentHashMap<>();
    
    // 리소스 제한 설정
    private static final int MAX_CONCURRENT_JOBS = 3; // 동시 실행 작업 수 제한 (시스템 부하 방지)
    private static final long MIN_WARMUP_INTERVAL_MS = 5 * 60 * 1000; // 5분간 중복 워밍업 방지
    private static final AtomicInteger jobCount = new AtomicInteger(0);

    /**
     * 사용자 추천 결과 워밍업
     * 통합 추천, 커리큘럼 추천, 강의 추천을 각각 첫 페이지로 미리 계산하여 캐시에 저장
     * 
     * 리소스 제한:
     * - 동시에 최대 3개의 워밍업 작업만 실행 (시스템 부하 방지)
     * - 5분 내 중복 워밍업 방지 (불필요한 리소스 사용 방지)
     * - 우선순위가 높은 첫 페이지만 계산 (page=0, size=12)
     * - 리소스가 부족하면 강의 추천 워밍업은 건너뜀
     * 
     * @param userId 사용자 ID
     */
    @Job(name = "Recommendation Warmup for User %0", retries = 1)
    @Transactional(readOnly = true)
    public void warmupRecommendations(Long userId) {
        // 최근 워밍업 체크 (너무 자주 실행 방지)
        Long lastWarmupTime = warmupTimestamps.get(userId);
        long currentTime = System.currentTimeMillis();
        if (lastWarmupTime != null && (currentTime - lastWarmupTime) < MIN_WARMUP_INTERVAL_MS) {
            log.debug("워밍업 간격 미충족 - 사용자 ID: {}, 마지막 워밍업: {}ms 전", 
                    userId, currentTime - lastWarmupTime);
            return;
        }
        
        // 동시 실행 작업 수 제한 확인
        int currentJobs = jobCount.get();
        if (currentJobs >= MAX_CONCURRENT_JOBS) {
            log.warn("워밍업 작업 제한 초과 - 현재 실행 중: {}, 최대: {}, 사용자 ID: {} 재시도 대기", 
                    currentJobs, MAX_CONCURRENT_JOBS, userId);
            // JobRunr가 자동으로 재시도하므로 여기서는 건너뜀
            throw new RuntimeException("워밍업 작업 큐가 가득 참. 잠시 후 재시도됩니다.");
        }
        
        // 중복 실행 방지
        if (activeWarmups.putIfAbsent(userId, true) != null) {
            log.debug("워밍업 이미 진행 중 - 사용자 ID: {}", userId);
            return;
        }
        
        jobCount.incrementAndGet();
        warmupTimestamps.put(userId, currentTime);
        log.info("추천 결과 워밍업 시작 - 사용자 ID: {}, 현재 실행 중인 작업 수: {}/{}", 
                userId, jobCount.get(), MAX_CONCURRENT_JOBS);
        long startTime = System.currentTimeMillis();
        
        try {
            // 우선순위 높은 첫 페이지만 미리 계산 (리소스 절약)
            int page = 0;
            int size = 12;
            
            // 1. 통합 추천 워밍업 (가장 많이 사용되므로 우선순위 1)
            try {
                recommendationService.getUnifiedRecommendations(userId, page, size);
                log.debug("통합 추천 워밍업 완료 - 사용자 ID: {}", userId);
            } catch (Exception e) {
                log.warn("통합 추천 워밍업 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            }
            
            // 2. 커리큘럼 추천 워밍업 (우선순위 2)
            try {
                recommendationService.getPersonalizedCurriculums(userId, page, size);
                log.debug("커리큘럼 추천 워밍업 완료 - 사용자 ID: {}", userId);
            } catch (Exception e) {
                log.warn("커리큘럼 추천 워밍업 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            }
            
            // 3. 강의 추천 워밍업 (우선순위 3, 리소스 여유 있을 때만)
            if (jobCount.get() < MAX_CONCURRENT_JOBS) {
                try {
                    recommendationService.getPersonalizedLectures(userId, page, size);
                    log.debug("강의 추천 워밍업 완료 - 사용자 ID: {}", userId);
                } catch (Exception e) {
                    log.warn("강의 추천 워밍업 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
                }
            } else {
                log.debug("리소스 부족으로 강의 추천 워밍업 건너뜀 - 사용자 ID: {}", userId);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("추천 결과 워밍업 완료 - 사용자 ID: {}, 소요 시간: {}ms", userId, duration);
            
        } catch (Exception e) {
            log.error("추천 결과 워밍업 중 오류 발생 - 사용자 ID: {}", userId, e);
            warmupTimestamps.remove(userId); // 오류 시 타임스탬프 제거하여 재시도 가능하게
            throw e; // JobRunr가 재시도하도록 예외 전파
        } finally {
            // 작업 완료 후 정리
            activeWarmups.remove(userId);
            jobCount.decrementAndGet();
        }
    }
    
    /**
     * 워밍업 작업 큐 상태 확인 (모니터링용)
     */
    public int getActiveWarmupCount() {
        return jobCount.get();
    }
    
    /**
     * 워밍업 작업 강제 취소 (긴급 상황용)
     */
    public void cancelWarmup(Long userId) {
        activeWarmups.remove(userId);
        warmupTimestamps.remove(userId);
        log.info("워밍업 작업 강제 취소 - 사용자 ID: {}", userId);
    }
}

