package com.PBL.lab.core.monitoring;

import java.util.Map;

/**
 * 큐 통계를 제공하는 서비스의 공통 인터페이스
 *
 * === 주요 목적 ===
 * - Submission과 Grading 서비스의 큐 통계를 통합 관리
 * - Health Check 및 Monitoring API에서 활용
 * - 각 모듈의 큐 상태를 일관된 인터페이스로 제공
 *
 * === 구현 클래스 ===
 * - judge0.service.SubmissionService
 * - grade.service.GradingService
 *
 * === 사용 예시 ===
 * <pre>
 * // Health Controller에서 사용
 * List&lt;QueueStatisticsProvider&gt; providers;
 *
 * for (QueueStatisticsProvider provider : providers) {
 *     String name = provider.getProviderName();
 *     long queueSize = provider.countInQueue();
 *     Map&lt;String, Object&gt; stats = provider.getStatistics();
 * }
 * </pre>
 */
public interface QueueStatisticsProvider {

    /**
     * 큐에 대기 중인 작업 수 반환
     *
     * - Submission: status_id = 1 (In Queue)인 개수
     * - Grading: status_id = 1 (In Queue)인 개수
     *
     * @return 대기 중인 작업 수
     */
    long countInQueue();

    /**
     * 처리 중인 작업 수 반환 (선택적 구현)
     *
     * - Submission: status_id = 2 (Processing)인 개수
     * - Grading: status_id = 2 (Processing)인 개수
     * - 기본 구현은 0 반환 (구현하지 않아도 됨)
     *
     * @return 처리 중인 작업 수
     */
    default long countInProcess() {
        return 0;
    }

    /**
     * 상세 통계 정보 반환
     *
     * 각 모듈별로 제공할 통계 정보를 Map 형태로 반환합니다.
     *
     * 예시:
     * <pre>
     * {
     *   "queue_size": 10,
     *   "in_process": 3,
     *   "completed_today": 150,
     *   "total": 500
     * }
     * </pre>
     *
     * @return 통계 정보 Map
     */
    Map<String, Object> getStatistics();

    /**
     * Provider의 이름 반환
     *
     * - "submission": Judge0 Submission 큐
     * - "grading": Grade 채점 큐
     *
     * @return Provider 이름
     */
    String getProviderName();
}
