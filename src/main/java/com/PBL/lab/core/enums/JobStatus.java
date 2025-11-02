package com.PBL.lab.core.enums;

/**
 * 작업 상태 열거형
 *
 * JobRunr 작업의 생명주기를 나타내는 상태값들을 정의합니다.
 * 각 상태는 작업이 큐에서 실행 완료까지의 과정을 추적하는 데 사용됩니다.
 *
 * 상태 전이 과정:
 * SCHEDULED → PROCESSING → SUCCEEDED/FAILED
 *
 * 상태 설명:
 * - SCHEDULED: 작업이 큐에 등록되어 대기 중
 * - ENQUEUED: 작업이 큐에 등록됨 (SCHEDULED와 동일하지만 명시적 표현)
 * - PROCESSING: 작업이 현재 실행 중
 * - SUCCEEDED: 작업이 성공적으로 완료됨
 * - FAILED: 작업 실행 중 오류 발생
 * - DELETED: 작업이 삭제됨 (취소됨)
 * - NOT_FOUND: 해당 작업이 존재하지 않음
 * - UNKNOWN: 상태를 확인할 수 없음 (시스템 오류 등)
 */
public enum JobStatus {
    SCHEDULED,    // 스케줄됨 (대기 중)
    ENQUEUED,     // 큐에 등록됨
    PROCESSING,   // 처리 중
    SUCCEEDED,    // 성공
    FAILED,       // 실패
    DELETED,      // 삭제됨
    NOT_FOUND,    // 찾을 수 없음
    UNKNOWN       // 알 수 없음
}
