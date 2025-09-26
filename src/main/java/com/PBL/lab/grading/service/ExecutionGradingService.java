package com.PBL.lab.grading.service;

import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.grading.job.GradingExecutionJob;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.core.service.ExecutionResult;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionGradingService {
    private final JobScheduler jobScheduler;
    private final GradingExecutionJob gradingExecutionJob;

    /**
     * 비동기 코드 실행 메서드
     *
     * 이 메서드는 코드 실행을 백그라운드 작업으로 예약합니다.
     * 클라이언트는 즉시 응답을 받고, 나중에 별도 API로 결과를 조회할 수 있습니다.
     *
     * 처리 과정:
     * 1. ExecutionJobScheduler를 통해 백그라운드 작업 예약
     * 2. 즉시 QUEUE 상태의 ExecutionResult 반환
     * 3. 실제 실행은 ExecutionJob에서 비동기적으로 처리
     *
     * @param grading 실행할 코드 제출 정보
     * @return CompletableFuture<ExecutionResult> 즉시 완료되는 Future (QUEUE 상태)
     */
    public CompletableFuture<ExecutionResult> executeAsync(Grading grading) {
        log.info("비동기 실행 예약 시작 - grading token: {}", grading.getToken());

        // JobRunr를 통해 백그라운드 작업 스케줄러에 실행 작업 예약
        // GradingExecutionJob.executeGrading()이 나중에 호출됨
        jobScheduler.enqueue(() -> gradingExecutionJob.executeGrading(grading.getToken()));

        // 즉시 완료되는 Future 반환 (실제 실행은 백그라운드에서 진행)
        // 클라이언트는 이 결과를 받고 나중에 /gradings/{token}으로 결과 조회
        return CompletableFuture.completedFuture(
                ExecutionResult.builder()
                        .status(Status.QUEUE)  // 대기열 상태로 반환
                        .build()
        );
    }

    /**
     * 코드 실행 전 제출 내용 유효성 검증 메서드
     *
     * 실행하기 전에 제출된 코드와 설정이 올바른지 검사합니다.
     * 잘못된 제출은 실행하지 않고 오류를 반환하여 시스템 리소스를 보호합니다.
     *
     * 검증 항목:
     * 1. 언어 정보 존재 여부 및 아카이브 상태 확인
     * 2. 단일 소스코드 제출 시 소스코드 필수 여부 확인
     * 3. 프로젝트 제출 시 추가 파일 필수 여부 확인
     *
     * @param grading 검증할 제출 정보
     * @return ValidationResult 검증 결과 (유효/무효 및 오류 메시지)
     */
    public ValidationResult validateGrading(Grading grading) {
        try {
            // 1. 언어 정보 존재 여부 확인
            if (grading.getLanguage() == null) {
                return ValidationResult.invalid("Language not found");
            }

            // 2. 언어가 아카이브(사용 중단) 상태인지 확인
            if (grading.getLanguage().getIsArchived()) {
                return ValidationResult.invalid("Language is archived");
            }

//            // 3. 단일 소스코드 제출인 경우 소스코드 필수 확인
//            if (!grading.isProject() &&
//                    (grading.getSourceCode() == null || grading.getSourceCode().trim().isEmpty())) {
//                return com.PBL.lab.judge0.service.ExecutionService.ValidationResult.invalid("Source code is required");
//            }
//
//            // 4. 프로젝트 제출인 경우 추가 파일 필수 확인
//            if (grading.isProject() && !grading.hasAdditionalFiles()) {
//                return com.PBL.lab.judge0.service.ExecutionService.ValidationResult.invalid("Additional files are required for project gradings");
//            }

            // 모든 검증 통과
            return ValidationResult.valid();
        } catch (Exception e) {
            // 예상치 못한 오류 발생 시
            return ValidationResult.invalid("Validation error: " + e.getMessage());
        }
    }


    /**
     * 유효성 검증 결과 데이터 클래스
     *
     * 코드 실행 전 제출 내용의 유효성 검증 결과를 담는 클래스입니다.
     * 검증이 성공했는지 여부와 실패 시 오류 메시지를 포함합니다.
     *
     * 사용 목적:
     * - 잘못된 제출을 사전에 차단하여 시스템 리소스 보호
     * - 명확한 오류 메시지 제공으로 사용자 경험 개선
     * - 검증 로직의 결과를 일관된 형태로 반환
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ValidationResult {
        private final boolean valid;  // 검증 성공 여부
        private final String error;   // 검증 실패 시 오류 메시지

        /**
         * 검증 성공 결과 생성
         * @return ValidationResult 검증 성공 객체
         */
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        /**
         * 검증 실패 결과 생성
         * @param error 오류 메시지
         * @return ValidationResult 검증 실패 객체
         */
        public static ValidationResult invalid(String error) {
            return new ValidationResult(false, error);
        }
    }
}
