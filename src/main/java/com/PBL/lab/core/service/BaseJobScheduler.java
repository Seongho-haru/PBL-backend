package com.PBL.lab.core.service;

import com.PBL.lab.core.dto.ExecutionResult;
import com.PBL.lab.core.dto.QueueStatistics;
import com.PBL.lab.core.enums.JobStatus;
import com.PBL.lab.core.enums.Status;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JobRunr 기반 작업 스케줄러 추상 클래스
 *
 * Judge0와 Grade 모듈의 JobScheduler 공통 로직을 추상화한 베이스 클래스입니다.
 *
 * 주요 기능:
 * - 즉시 실행 작업 스케줄링 (enqueue)
 * - 지연 실행 작업 스케줄링 (schedule with delay)
 * - 실행 중인 작업 취소 및 상태 추적
 * - 작업 큐 통계 정보 제공
 * - 완료된 작업 정리 및 메모리 관리
 *
 * JobRunr 통합:
 * - JobRunr는 분산 환경에서도 안정적인 작업 처리를 보장
 * - 작업 실패 시 자동 재시도 기능 제공
 * - 작업 상태 추적 및 모니터링 기능
 * - 데이터베이스 기반 작업 영속성 보장
 *
 * 스레드 안전성:
 * - ConcurrentHashMap을 사용하여 멀티스레드 환경에서 안전한 작업 추적
 * - 동시에 여러 작업이 스케줄링되어도 데이터 일관성 보장
 *
 * 사용 방법:
 * 1. 이 클래스를 상속받는 구체 클래스 생성
 * 2. executeJob(String token) 메서드 구현
 * 3. schedule(), cancel() 등의 공통 메서드 활용
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseJobScheduler {

    protected final JobScheduler jobScheduler; // JobRunr의 작업 스케줄러 (작업 등록/관리)

    // 스케줄된 작업들을 추적하는 맵 (token -> JobId)
    // ConcurrentHashMap을 사용하여 멀티스레드 환경에서 안전한 접근 보장
    public final ConcurrentHashMap<String, JobId> scheduledJobs = new ConcurrentHashMap<>();

    /**
     * 실제 작업 실행을 담당하는 추상 메서드
     *
     * 하위 클래스에서 구체적인 Job 실행 로직을 구현합니다.
     *
     * 예시:
     * - Judge0: executionJob.executeSubmission(token)
     * - Grade: gradingExecutionJob.executeGrading(token)
     *
     * @param token 실행할 작업의 고유 토큰
     */
    protected abstract void executeJob(String token);

    /**
     * 작업의 즉시 실행을 스케줄링하는 메서드
     *
     * 이 메서드는 작업을 백그라운드 큐에 등록하여 가능한 빨리 실행되도록 합니다.
     *
     * 처리 과정:
     * 1. JobRunr의 enqueue()를 사용하여 즉시 실행 작업 등록
     * 2. executeJob()이 백그라운드에서 실행됨
     * 3. 작업 ID를 내부 맵에 저장하여 추적 가능하도록 함
     * 4. 작업 등록 성공/실패 로그 기록
     *
     * @param token 실행할 작업의 고유 토큰
     * @throws RuntimeException 작업 스케줄링 실패 시 발생
     */
    public CompletableFuture<ExecutionResult> schedule(String token) {
        try {
            log.info("작업 스케줄링 시작 - token: {}", token);

            // JobRunr를 통해 executeJob()을 백그라운드 작업으로 등록
            // enqueue()는 작업을 즉시 실행 큐에 추가하여 가능한 빨리 처리하도록 함
            JobId jobId = jobScheduler.enqueue(() -> executeJob(token));

            // 작업 ID를 내부 맵에 저장하여 나중에 취소하거나 상태를 확인할 수 있도록 함
            scheduledJobs.put(token, jobId);

            log.debug("작업 스케줄링 완료 - JobId: {}, token: {}", jobId, token);
            return CompletableFuture.completedFuture(
                    ExecutionResult.builder()
                            .status(Status.QUEUE) // 대기열 상태로 반환
                            .build());
        } catch (Exception e) {
            log.error("작업 스케줄링 실패 - token: {}", token, e);
            return CompletableFuture.completedFuture(
                    ExecutionResult.builder()
                            .status(Status.BOXERR)
                            .message("작업 스케줄링 실패: " + e.getMessage()) // 대기열 상태로 반환
                            .build());
        }
    }

    /**
     * 작업의 지연 실행을 스케줄링하는 메서드
     *
     * 이 메서드는 특정 시간 후에 작업을 시작하도록 예약합니다.
     *
     * 사용 시나리오:
     * - 시스템 부하가 높을 때 실행을 지연시키고 싶은 경우
     * - 특정 시간대에만 작업 실행을 허용하고 싶은 경우
     * - 배치 처리나 스케줄링이 필요한 경우
     *
     * 처리 과정:
     * 1. 현재 시간에 지연 시간을 더한 실행 시간 계산
     * 2. JobRunr의 schedule()을 사용하여 지연 실행 작업 등록
     * 3. 작업 ID를 내부 맵에 저장하여 추적
     *
     * @param token 실행할 작업의 고유 토큰
     * @param delay 지연 시간 (Duration 객체)
     * @throws RuntimeException 작업 스케줄링 실패 시 발생
     */
    public CompletableFuture<ExecutionResult> scheduleDelayed(String token, Duration delay) {
        try {
            log.info("지연 실행 작업 스케줄링 시작 - token: {}, 지연 시간: {}", token, delay);

            // 현재 시간에 지연 시간을 더한 시점에 실행되도록 작업 등록
            // schedule()은 지정된 시간에 작업을 실행하도록 예약함
            JobId jobId = jobScheduler.schedule(LocalDateTime.now().plus(delay),
                    () -> executeJob(token));

            // 작업 ID를 내부 맵에 저장하여 추적 가능하도록 함
            scheduledJobs.put(token, jobId);

            log.debug("지연 실행 작업 스케줄링 완료 - JobId: {}, token: {}", jobId, token);
            return CompletableFuture.completedFuture(
                    ExecutionResult.builder()
                            .status(Status.QUEUE) // 대기열 상태로 반환
                            .build());
        } catch (Exception e) {
            log.error("지연 실행 작업 스케줄링 실패 - token: {}", token, e);
            return CompletableFuture.completedFuture(
                    ExecutionResult.builder()
                            .status(Status.BOXERR)
                            .message("작업 스케줄링 실패: " + e.getMessage()) // 대기열 상태로 반환
                            .build());
        }
    }

    /**
     * 작업을 취소하는 메서드
     *
     * 이 메서드는 아직 실행되지 않은 대기 중인 작업을 취소합니다.
     * 이미 실행 중인 작업은 취소할 수 없으며, 완료될 때까지 기다려야 합니다.
     *
     * 취소 가능한 상황:
     * - 작업이 아직 큐에 대기 중인 경우
     * - 작업이 스케줄되어 있지만 아직 시작되지 않은 경우
     *
     * 취소 불가능한 상황:
     * - 작업이 이미 실행 중인 경우 (PROCESSING 상태)
     * - 작업이 이미 완료된 경우 (SUCCEEDED/FAILED 상태)
     *
     * 처리 과정:
     * 1. 내부 맵에서 해당 token의 JobId 조회
     * 2. JobRunr의 delete()를 사용하여 작업 취소
     * 3. 내부 맵에서 해당 항목 제거
     * 4. 취소 성공/실패 결과 반환
     *
     * @param token 취소할 작업의 고유 토큰
     * @return boolean 취소 성공 시 true, 실패 시 false
     */
    public boolean cancel(String token) {
        try {
            // 내부 맵에서 해당 token의 JobId를 조회하고 제거
            JobId jobId = scheduledJobs.remove(token);

            if (jobId != null) {
                // JobRunr를 통해 실제 작업 취소
                jobScheduler.delete(jobId);
                log.info("작업 취소 완료 - token: {}", token);
                return true;
            } else {
                // 해당 token에 대한 스케줄된 작업이 없는 경우
                log.warn("취소할 스케줄된 작업을 찾을 수 없음 - token: {}", token);
                return false;
            }
        } catch (Exception e) {
            log.error("작업 취소 실패 - token: {}", token, e);
            return false;
        }
    }

    /**
     * 작업 상태를 조회하는 메서드
     *
     * 이 메서드는 특정 token에 대한 작업의 현재 상태를 반환합니다.
     * 현재는 단순화된 구현이지만, 실제 운영 환경에서는 JobRunr의 상세한 상태 정보를 활용할 수 있습니다.
     *
     * 반환 가능한 상태:
     * - SCHEDULED: 작업이 스케줄되어 대기 중
     * - PROCESSING: 작업이 현재 실행 중
     * - SUCCEEDED: 작업이 성공적으로 완료됨
     * - FAILED: 작업 실행 중 오류 발생
     * - NOT_FOUND: 해당 token에 대한 작업이 없음
     * - UNKNOWN: 상태를 확인할 수 없음 (오류 발생)
     *
     * 향후 개선 사항:
     * - JobRunr의 실제 작업 상태 API를 활용하여 정확한 상태 반환
     * - 작업 진행률, 예상 완료 시간 등 추가 정보 제공
     * - 실시간 상태 업데이트 및 웹소켓을 통한 상태 알림
     *
     * @param token 상태를 조회할 작업의 고유 토큰
     * @return JobStatus 작업의 현재 상태
     */
    public JobStatus getStatus(String token) {
        // 내부 맵에서 해당 token의 JobId 조회
        JobId jobId = scheduledJobs.get(token);

        if (jobId == null) {
            // 해당 token에 대한 스케줄된 작업이 없는 경우
            return JobStatus.NOT_FOUND;
        }

        try {
            // TODO: 실제 구현에서는 JobRunr의 작업 상태 API를 사용하여 정확한 상태 반환
            // 현재는 단순화된 구현으로 SCHEDULED 상태만 반환
            // 향후 JobRunr의 JobDetails API를 활용하여 실제 상태 조회 가능
            return JobStatus.SCHEDULED;
        } catch (Exception e) {
            log.error("작업 상태 조회 실패 - token: {}", token, e);
            return JobStatus.UNKNOWN;
        }
    }

    /**
     * 작업 큐의 통계 정보를 조회하는 메서드
     *
     * 이 메서드는 현재 작업 큐의 상태를 모니터링하기 위한 통계 정보를 제공합니다.
     * 시스템 관리자나 모니터링 도구에서 큐의 부하 상태를 파악하는 데 사용됩니다.
     *
     * 제공하는 통계 정보:
     * - enqueuedJobs: 대기 중인 작업 수 (내부 맵의 크기)
     * - processingJobs: 현재 실행 중인 작업 수
     * - succeededJobs: 성공적으로 완료된 작업 수
     * - failedJobs: 실패한 작업 수
     * - totalJobs: 전체 작업 수 (위 4개 항목의 합계)
     *
     * 사용 목적:
     * - 시스템 부하 모니터링
     * - 큐 크기 제한 관리 (maxQueueSize와 비교)
     * - 성능 지표 수집 및 분석
     * - Health Check 엔드포인트에서 시스템 상태 확인
     *
     * 향후 개선 사항:
     * - JobRunr의 실제 큐 통계 API를 사용하여 정확한 통계 반환
     * - 시간대별 통계, 평균 처리 시간 등 추가 메트릭 제공
     * - Prometheus/Grafana와 연동하여 시각화
     *
     * @return QueueStatistics 큐 통계 정보 객체
     */
    public QueueStatistics getStatistics() {
        try {
            // TODO: 실제 구현에서는 JobRunr의 큐 통계 API를 사용하여 정확한 통계 반환
            // 현재는 내부 맵의 크기만으로 대기 중인 작업 수를 추정
            // 향후 JobRunr의 Dashboard API나 Statistics API를 활용 가능

            return QueueStatistics.builder()
                    .enqueuedJobs(scheduledJobs.size()) // 내부 맵에 저장된 작업 수 (대기 중)
                    .processingJobs(0) // 현재 실행 중인 작업 수 (추후 JobRunr API로 조회)
                    .succeededJobs(0) // 성공한 작업 수 (추후 JobRunr API로 조회)
                    .failedJobs(0) // 실패한 작업 수 (추후 JobRunr API로 조회)
                    .build();
        } catch (Exception e) {
            log.error("큐 통계 조회 실패", e);
            // 오류 발생 시 빈 통계 객체 반환
            return QueueStatistics.builder().build();
        }
    }

    /**
     * 완료된 작업들을 정리하는 메서드
     *
     * 이 메서드는 완료된 작업들을 내부 추적 맵에서 제거하여 메모리 사용량을 최적화합니다.
     * 주기적으로 호출되어 시스템 리소스를 효율적으로 관리합니다.
     *
     * 정리 대상:
     * - 성공적으로 완료된 작업 (SUCCEEDED)
     * - 실패한 작업 (FAILED)
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
    public void cleanupCompleted() {
        log.debug("완료된 작업 정리 시작 - 현재 추적 중인 작업 수: {}", scheduledJobs.size());

        // 완료된 작업들을 내부 추적 맵에서 제거
        scheduledJobs.entrySet().removeIf(entry -> {
            try {
                // TODO: 실제 구현에서는 JobRunr의 작업 상태 API를 사용하여 완료 여부 확인
                // 현재는 단순화된 구현으로 모든 작업을 유지
                // 향후 JobRunr의 JobDetails API를 활용하여 SUCCEEDED/FAILED 상태 확인 후 제거
                return false; // 현재는 모든 작업을 유지 (단순화된 구현)
            } catch (Exception e) {
                // 오류가 발생한 작업 추적 항목은 제거하여 시스템 안정성 확보
                log.warn("작업 상태 확인 중 오류 발생 - token: {}", entry.getKey(), e);
                return true; // 문제가 있는 항목은 제거
            }
        });

        log.debug("완료된 작업 정리 완료 - 정리 후 추적 중인 작업 수: {}", scheduledJobs.size());
    }
}
