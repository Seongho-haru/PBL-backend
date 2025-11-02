package com.PBL.lab.core.dto;

/**
 * 작업 큐 통계 정보 데이터 클래스
 *
 * 작업 큐의 현재 상태를 나타내는 통계 정보를 담는 클래스입니다.
 * 시스템 모니터링, 성능 분석, 부하 관리 등에 사용됩니다.
 *
 * 통계 항목:
 * - enqueuedJobs: 대기 중인 작업 수 (큐에 등록되어 아직 시작되지 않은 작업)
 * - processingJobs: 현재 실행 중인 작업 수 (PROCESSING 상태)
 * - succeededJobs: 성공적으로 완료된 작업 수 (SUCCEEDED 상태)
 * - failedJobs: 실패한 작업 수 (FAILED 상태)
 * - totalJobs: 전체 작업 수 (위 4개 항목의 합계)
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
public class QueueStatistics {
    private int enqueuedJobs;    // 대기 중인 작업 수
    private int processingJobs;  // 실행 중인 작업 수
    private int succeededJobs;   // 성공한 작업 수
    private int failedJobs;      // 실패한 작업 수
    private int totalJobs;       // 전체 작업 수 (계산된 값)

    /**
     * 전체 작업 수를 계산하여 반환하는 메서드
     *
     * 대기 중, 실행 중, 성공, 실패한 모든 작업의 합계를 계산합니다.
     * 이 값은 시스템의 전체 처리량을 나타내는 중요한 지표입니다.
     *
     * @return int 전체 작업 수
     */
    public int getTotalJobs() {
        return enqueuedJobs + processingJobs + succeededJobs + failedJobs;
    }
}