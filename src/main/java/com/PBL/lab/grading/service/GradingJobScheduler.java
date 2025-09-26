package com.PBL.lab.grading.service;

import com.PBL.lab.grading.job.GradingExecutionJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채점 실행 작업 스케줄러 서비스
 * 
 * 이 서비스는 Grading 시스템에서 코드 채점을 위한 백그라운드 작업들을 관리합니다.
 * JobRunr 라이브러리를 사용하여 안정적이고 신뢰할 수 있는 작업 처리를 제공합니다.
 * 
 * 주요 기능:
 * - 즉시 채점 작업 스케줄링 (executeGradingAsync 호출 시)
 * - 지연 채점 작업 스케줄링 (특정 시간 후 실행)
 * - 실행 중인 채점 작업 취소 및 상태 추적
 * - 채점 작업 큐 통계 정보 제공
 * - 완료된 채점 작업 정리 및 메모리 관리
 * 
 * JobRunr 통합:
 * - JobRunr는 분산 환경에서도 안정적인 작업 처리를 보장
 * - 작업 실패 시 자동 재시도 기능 제공
 * - 작업 상태 추적 및 모니터링 기능
 * - 데이터베이스 기반 작업 영속성 보장
 * 
 * 스레드 안전성:
 * - ConcurrentHashMap을 사용하여 멀티스레드 환경에서 안전한 작업 추적
 * - 동시에 여러 채점 작업이 스케줄링되어도 데이터 일관성 보장
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradingJobScheduler {

    private final JobScheduler jobScheduler;  // JobRunr의 작업 스케줄러 (작업 등록/관리)
    private final GradingExecutionJob gradingExecutionJob;  // 실제 코드 채점을 담당하는 작업 클래스
    
    // 스케줄된 채점 작업들을 추적하는 맵 (grading token -> JobId)
    // ConcurrentHashMap을 사용하여 멀티스레드 환경에서 안전한 접근 보장
    private final ConcurrentHashMap<String, JobId> scheduledJobs = new ConcurrentHashMap<>();

    /**
     * 제출된 코드의 즉시 채점을 스케줄링하는 메서드
     * 
     * 이 메서드는 ExecutionGradingService.executeGradingAsync()에서 호출되어
     * 코드 채점 작업을 백그라운드 큐에 등록합니다.
     * 
     * 처리 과정:
     * 1. JobRunr의 enqueue()를 사용하여 즉시 실행 작업 등록
     * 2. GradingExecutionJob.executeGrading()이 백그라운드에서 실행됨
     * 3. 작업 ID를 내부 맵에 저장하여 추적 가능하도록 함
     * 4. 작업 등록 성공/실패 로그 기록
     * 
     * @param gradingToken 채점할 제출의 고유 토큰
     * @throws RuntimeException 작업 스케줄링 실패 시 발생
     */
    public void scheduleGrading(String gradingToken) {
        try {
            log.info("코드 채점 작업 스케줄링 시작 - grading token: {}", gradingToken);
            
            // JobRunr를 통해 GradingExecutionJob.executeGrading()을 백그라운드 작업으로 등록
            // enqueue()는 작업을 즉시 실행 큐에 추가하여 가능한 빨리 처리하도록 함
            JobId jobId = jobScheduler.enqueue(() -> gradingExecutionJob.executeGrading(gradingToken));
            
            // 작업 ID를 내부 맵에 저장하여 나중에 취소하거나 상태를 확인할 수 있도록 함
            scheduledJobs.put(gradingToken, jobId);
            
            log.debug("채점 작업 스케줄링 완료 - JobId: {}, grading token: {}", jobId, gradingToken);
            
        } catch (Exception e) {
            log.error("채점 작업 스케줄링 실패 - grading token: {}", gradingToken, e);
            throw new RuntimeException("Grading job scheduling failed", e);
        }
    }

    /**
     * 제출된 코드의 지연 채점을 스케줄링하는 메서드
     * 
     * 이 메서드는 특정 시간 후에 코드 채점을 시작하도록 작업을 예약합니다.
     * 현재는 사용되지 않지만, 향후 기능 확장을 위해 구현되어 있습니다.
     * 
     * 사용 시나리오:
     * - 시스템 부하가 높을 때 채점을 지연시키고 싶은 경우
     * - 특정 시간대에만 코드 채점을 허용하고 싶은 경우
     * - 배치 처리나 스케줄링이 필요한 경우
     * 
     * 처리 과정:
     * 1. 현재 시간에 지연 시간을 더한 실행 시간 계산
     * 2. JobRunr의 schedule()을 사용하여 지연 실행 작업 등록
     * 3. 작업 ID를 내부 맵에 저장하여 추적
     * 
     * @param gradingToken 채점할 제출의 고유 토큰
     * @param delay 지연 시간 (Duration 객체)
     * @throws RuntimeException 작업 스케줄링 실패 시 발생
     */
    public void scheduleDelayedGrading(String gradingToken, Duration delay) {
        try {
            log.info("지연 채점 작업 스케줄링 시작 - grading token: {}, 지연 시간: {}", gradingToken, delay);
            
            // 현재 시간에 지연 시간을 더한 시점에 실행되도록 작업 등록
            // schedule()은 지정된 시간에 작업을 실행하도록 예약함
            JobId jobId = jobScheduler.schedule(LocalDateTime.now().plus(delay), 
                    () -> gradingExecutionJob.executeGrading(gradingToken));
            
            // 작업 ID를 내부 맵에 저장하여 추적 가능하도록 함
            scheduledJobs.put(gradingToken, jobId);
            
            log.debug("지연 채점 작업 스케줄링 완료 - JobId: {}, grading token: {}", jobId, gradingToken);
            
        } catch (Exception e) {
            log.error("지연 채점 작업 스케줄링 실패 - grading token: {}", gradingToken, e);
            throw new RuntimeException("Grading job scheduling failed", e);
        }
    }

    /**
     * 제출된 코드의 채점 작업을 취소하는 메서드
     * 
     * 이 메서드는 아직 실행되지 않은 대기 중인 채점 작업을 취소합니다.
     * 이미 실행 중인 작업은 취소할 수 없으며, 완료될 때까지 기다려야 합니다.
     * 
     * 취소 가능한 상황:
     * - 작업이 아직 큐에 대기 중인 경우
     * - 작업이 스케줄되어 있지만 아직 시작되지 않은 경우
     * 
     * 취소 불가능한 상황:
     * - 작업이 이미 실행 중인 경우 (PROCESS 상태)
     * - 작업이 이미 완료된 경우 (SUCCESS/FAILED 상태)
     * 
     * 처리 과정:
     * 1. 내부 맵에서 해당 grading token의 JobId 조회
     * 2. JobRunr의 delete()를 사용하여 작업 취소
     * 3. 내부 맵에서 해당 항목 제거
     * 4. 취소 성공/실패 결과 반환
     * 
     * @param gradingToken 취소할 제출의 고유 토큰
     * @return boolean 취소 성공 시 true, 실패 시 false
     */
    public boolean cancelGrading(String gradingToken) {
        try {
            // 내부 맵에서 해당 grading token의 JobId를 조회하고 제거
            JobId jobId = scheduledJobs.remove(gradingToken);
            
            if (jobId != null) {
                // JobRunr를 통해 실제 작업 취소
                jobScheduler.delete(jobId);
                log.info("채점 작업 취소 완료 - grading token: {}", gradingToken);
                return true;
            } else {
                // 해당 grading token에 대한 스케줄된 작업이 없는 경우
                log.warn("취소할 스케줄된 채점 작업을 찾을 수 없음 - grading token: {}", gradingToken);
                return false;
            }
        } catch (Exception e) {
            log.error("채점 작업 취소 실패 - grading token: {}", gradingToken, e);
            return false;
        }
    }

    /**
     * 제출된 코드의 채점 작업 상태를 조회하는 메서드
     * 
     * 이 메서드는 특정 grading token에 대한 채점 작업의 현재 상태를 반환합니다.
     * 현재는 단순화된 구현이지만, 실제 운영 환경에서는 JobRunr의 상세한 상태 정보를 활용할 수 있습니다.
     * 
     * 반환 가능한 상태:
     * - SCHEDULED: 작업이 스케줄되어 대기 중
     * - PROCESSING: 작업이 현재 실행 중
     * - SUCCEEDED: 작업이 성공적으로 완료됨
     * - FAILED: 작업 실행 중 오류 발생
     * - NOT_FOUND: 해당 grading token에 대한 작업이 없음
     * - UNKNOWN: 상태를 확인할 수 없음 (오류 발생)
     * 
     * 향후 개선 사항:
     * - JobRunr의 실제 작업 상태 API를 활용하여 정확한 상태 반환
     * - 작업 진행률, 예상 완료 시간 등 추가 정보 제공
     * - 실시간 상태 업데이트 및 웹소켓을 통한 상태 알림
     * 
     * @param gradingToken 상태를 조회할 제출의 고유 토큰
     * @return JobStatus 채점 작업의 현재 상태
     */
    public JobStatus getGradingJobStatus(String gradingToken) {
        // 내부 맵에서 해당 grading token의 JobId 조회
        JobId jobId = scheduledJobs.get(gradingToken);
        
        if (jobId == null) {
            // 해당 grading token에 대한 스케줄된 작업이 없는 경우
            return JobStatus.NOT_FOUND;
        }
        
        try {
            // TODO: 실제 구현에서는 JobRunr의 작업 상태 API를 사용하여 정확한 상태 반환
            // 현재는 단순화된 구현으로 SCHEDULED 상태만 반환
            // 향후 JobRunr의 JobDetails API를 활용하여 실제 상태 조회 가능
            return JobStatus.SCHEDULED;
        } catch (Exception e) {
            log.error("채점 작업 상태 조회 실패 - grading token: {}", gradingToken, e);
            return JobStatus.UNKNOWN;
        }
    }

    /**
     * 채점 작업 큐의 통계 정보를 조회하는 메서드
     * 
     * 이 메서드는 현재 채점 작업 큐의 상태를 모니터링하기 위한 통계 정보를 제공합니다.
     * 시스템 관리자나 모니터링 도구에서 큐의 부하 상태를 파악하는 데 사용됩니다.
     * 
     * 제공하는 통계 정보:
     * - enqueuedJobs: 대기 중인 채점 작업 수 (내부 맵의 크기)
     * - processingJobs: 현재 실행 중인 채점 작업 수
     * - succeededJobs: 성공적으로 완료된 채점 작업 수
     * - failedJobs: 실패한 채점 작업 수
     * - totalJobs: 전체 채점 작업 수 (위 4개 항목의 합계)
     * 
     * 사용 목적:
     * - 시스템 부하 모니터링
     * - 큐 크기 제한 관리 (maxQueueSize와 비교)
     * - 성능 지표 수집 및 분석
     * - Health Check 엔드포인트에서 시스템 상태 확인
     * 
     * 향후 개선 사항:
     * - JobRunr의 실제 큐 통계 API를 활용하여 정확한 데이터 제공
     * - 시간대별 통계, 평균 처리 시간 등 추가 메트릭 제공
     * - Prometheus/Grafana와 연동하여 시각화
     * 
     * @return QueueStatistics 채점 큐 통계 정보 객체
     */
    public QueueStatistics getGradingQueueStatistics() {
        try {
            // TODO: 실제 구현에서는 JobRunr의 큐 통계 API를 사용하여 정확한 통계 반환
            // 현재는 내부 맵의 크기만으로 대기 중인 작업 수를 추정
            // 향후 JobRunr의 Dashboard API나 Statistics API를 활용 가능
            
            return QueueStatistics.builder()
                    .enqueuedJobs(scheduledJobs.size())  // 내부 맵에 저장된 작업 수 (대기 중)
                    .processingJobs(0)                   // 현재 실행 중인 작업 수 (추후 JobRunr API로 조회)
                    .succeededJobs(0)                    // 성공한 작업 수 (추후 JobRunr API로 조회)
                    .failedJobs(0)                       // 실패한 작업 수 (추후 JobRunr API로 조회)
                    .build();
        } catch (Exception e) {
            log.error("채점 큐 통계 조회 실패", e);
            // 오류 발생 시 빈 통계 객체 반환
            return QueueStatistics.builder().build();
        }
    }

    /**
     * 완료된 채점 작업들을 정리하는 메서드
     * 
     * 이 메서드는 완료된 채점 작업들을 내부 추적 맵에서 제거하여 메모리 사용량을 최적화합니다.
     * 주기적으로 호출되어 시스템 리소스를 효율적으로 관리합니다.
     * 
     * 정리 대상:
     * - 성공적으로 완료된 채점 작업 (SUCCEEDED)
     * - 실패한 채점 작업 (FAILED)
     * - 오류가 발생한 작업 추적 항목
     * 
     * 정리하지 않는 대상:
     * - 대기 중인 작업 (SCHEDULED)
     * - 실행 중인 작업 (PROCESSING)
     * 
     * 메모리 최적화 효과:
     * - 내부 맵의 크기를 줄여 메모리 사용량 감소
     * - 가비지 컬렉션 부담 감소
     * - 작업 조회 성능 향상
     * 
     * 호출 시점:
     * - 주기적인 스케줄러에 의해 자동 호출
     * - 시스템 부하가 높을 때 수동 호출
     * - 애플리케이션 종료 전 정리 작업
     * 
     * 향후 개선 사항:
     * - JobRunr의 작업 상태 API를 활용하여 정확한 완료 여부 확인
     * - 완료된 작업의 보관 기간 설정 (예: 24시간 후 정리)
     * - 정리 작업의 성능 최적화 (배치 처리)
     */
    public void cleanupCompletedGradingJobs() {
        log.debug("완료된 채점 작업 정리 시작 - 현재 추적 중인 작업 수: {}", scheduledJobs.size());
        
        // 완료된 작업들을 내부 추적 맵에서 제거
        scheduledJobs.entrySet().removeIf(entry -> {
            try {
                // TODO: 실제 구현에서는 JobRunr의 작업 상태 API를 사용하여 완료 여부 확인
                // 현재는 단순화된 구현으로 모든 작업을 유지
                // 향후 JobRunr의 JobDetails API를 활용하여 SUCCEEDED/FAILED 상태 확인 후 제거
                return false; // 현재는 모든 작업을 유지 (단순화된 구현)
            } catch (Exception e) {
                // 오류가 발생한 작업 추적 항목은 제거하여 시스템 안정성 확보
                log.warn("채점 작업 상태 확인 중 오류 발생 - grading token: {}", entry.getKey(), e);
                return true; // 문제가 있는 항목은 제거
            }
        });
        
        log.debug("완료된 채점 작업 정리 완료 - 정리 후 추적 중인 작업 수: {}", scheduledJobs.size());
    }

    /**
     * 채점 작업 상태 열거형
     * 
     * JobRunr 채점 작업의 생명주기를 나타내는 상태값들을 정의합니다.
     * 각 상태는 채점 작업이 큐에서 실행 완료까지의 과정을 추적하는 데 사용됩니다.
     * 
     * 상태 전이 과정:
     * SCHEDULED → PROCESSING → SUCCEEDED/FAILED
     * 
     * 상태 설명:
     * - SCHEDULED: 작업이 큐에 등록되어 대기 중
     * - PROCESSING: 작업이 현재 실행 중
     * - SUCCEEDED: 작업이 성공적으로 완료됨
     * - FAILED: 작업 실행 중 오류 발생
     * - NOT_FOUND: 해당 작업이 존재하지 않음
     * - UNKNOWN: 상태를 확인할 수 없음 (시스템 오류 등)
     */
    public enum JobStatus {
        SCHEDULED,    // 스케줄됨 (대기 중)
        PROCESSING,   // 처리 중
        SUCCEEDED,    // 성공
        FAILED,       // 실패
        NOT_FOUND,    // 찾을 수 없음
        UNKNOWN       // 알 수 없음
    }

    /**
     * 채점 작업 큐 통계 정보 데이터 클래스
     * 
     * 채점 작업 큐의 현재 상태를 나타내는 통계 정보를 담는 클래스입니다.
     * 시스템 모니터링, 성능 분석, 부하 관리 등에 사용됩니다.
     * 
     * 통계 항목:
     * - enqueuedJobs: 대기 중인 채점 작업 수 (큐에 등록되어 아직 시작되지 않은 작업)
     * - processingJobs: 현재 실행 중인 채점 작업 수 (PROCESSING 상태)
     * - succeededJobs: 성공적으로 완료된 채점 작업 수 (SUCCEEDED 상태)
     * - failedJobs: 실패한 채점 작업 수 (FAILED 상태)
     * - totalJobs: 전체 채점 작업 수 (위 4개 항목의 합계)
     * 
     * 사용 목적:
     * - Health Check 엔드포인트에서 시스템 상태 확인
     * - 모니터링 대시보드에서 큐 부하 시각화
     * - 자동 스케일링 정책 수립 (큐 크기 기반)
     * - 성능 지표 수집 및 분석
     * 
     * Lombok 어노테이션:
     * - @Data: getter, setter, toString, equals, hashCode 자동 생성
     * - @Builder: 빌더 패턴을 통한 객체 생성 지원
     */
    @lombok.Data
    @lombok.Builder
    public static class QueueStatistics {
        private int enqueuedJobs;    // 대기 중인 채점 작업 수
        private int processingJobs;  // 실행 중인 채점 작업 수
        private int succeededJobs;   // 성공한 채점 작업 수
        private int failedJobs;      // 실패한 채점 작업 수
        private int totalJobs;       // 전체 채점 작업 수 (계산된 값)

        /**
         * 전체 채점 작업 수를 계산하여 반환하는 메서드
         * 
         * 대기 중, 실행 중, 성공, 실패한 모든 채점 작업의 합계를 계산합니다.
         * 이 값은 시스템의 전체 채점 처리량을 나타내는 중요한 지표입니다.
         * 
         * @return int 전체 채점 작업 수
         */
        public int getTotalJobs() {
            return enqueuedJobs + processingJobs + succeededJobs + failedJobs;
        }
    }
}
