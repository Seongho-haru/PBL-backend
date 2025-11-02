package com.PBL.lab.judge0.service;

import com.PBL.lab.core.dto.CodeExecutionRequest;
import com.PBL.lab.core.service.BaseJobScheduler;
import com.PBL.lab.core.service.DockerExecutionService;
import com.PBL.lab.core.dto.ExecutionResult;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.judge0.job.SubmissionJob;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Execution Service - 코드 실행 오케스트레이션 서비스
 * 
 * 이 서비스는 Judge0 시스템의 핵심 컴포넌트로, 사용자가 제출한 코드를 
 * Docker 컨테이너 환경에서 안전하게 실행하는 전체 과정을 관리합니다.
 * 
 * 주요 목적:
 * - Docker 컨테이너 환경에서의 코드 실행 전체 오케스트레이션 담당
 * - 동기/비동기 실행 모드 모두 지원 (wait 파라미터에 따라 결정)
 * - 안전한 코드 실행을 위한 보안 제약 및 자원 관리
 * - 다양한 프로그래밍 언어의 컴파일 및 실행 처리
 * 
 * 핵심 기능:
 * - executeAsync(): 비동기 코드 실행 (대기열에 작업 예약하여 백그라운드 처리)
 * - executeSync(): 동기 코드 실행 (즉시 결과 반환, wait=true일 때 사용)
 * - validateSubmission(): 제출 내용 사전 유효성 검증 (언어, 소스코드, 파일 등)
 * - CodeExecutionRequest: 실행 요청 데이터 구조체 (Docker 실행에 필요한 모든 정보)
 * - SecurityConstraints: 보안 제약 설정 (시간/메모리/프로세스/네트워크 제한)
 * 
 * 실행 절차:
 * 1. 제출 내용 검증 (validateSubmission) - 언어 존재 여부, 소스코드 유효성 등
 * 2. 실행 요청 객체 생성 (CodeExecutionRequest.from) - Submission을 실행 가능한 형태로 변환
 * 3. Docker 컨테이너에서 실행 (DockerExecutionService.executeCode) - 실제 코드 실행
 * 4. 결과 처리 및 데이터베이스 업데이트 - stdout, stderr, 실행 시간 등 저장
 * 5. 웹훅 콜백 전송 (필요시) - 완료 시 외부 시스템에 결과 전달
 * 
 * 보안 특징:
 * - 샌드박스 이스케이프 방지를 위한 다중 보안 제약 적용
 * - 파일 시스템 액세스 제한 (READ_ONLY/READ_WRITE 선택 가능)
 * - 네트워크 액세스 제어 및 모니터링 (기본적으로 차단, 필요시 허용)
 * - 컨테이너 리소스 사용량 제한 (CPU 시간, 메모리, 디스크 공간)
 * - 프로세스/스레드 수 제한으로 DoS 공격 방지
 * 
 * 오류 처리:
 * - 실행 실패 시 자동 상태 갱신 (BOXERR 상태로 변경)
 * - 상세한 오류 로깅 및 디버깅 정보 제공
 * - 비동기 작업 실패 시 CompletableFuture 예외 처리
 * - 타임아웃, 메모리 초과, 컴파일 오류 등 다양한 실패 시나리오 대응
 */
@Service
@Slf4j
public class SubmissionExecutionService extends BaseJobScheduler {

    private final DockerExecutionService dockerExecutionService;
    private final SubmissionService submissionService;
    private final JobScheduler jobScheduler;  // JobRunr의 작업 스케줄러 (작업 등록/관리)
    private final SubmissionJob executionJob;  // 실제 코드 실행을 담당하는 작업 클래스

    public SubmissionExecutionService(DockerExecutionService dockerExecutionService, SubmissionService submissionService, JobScheduler jobScheduler, SubmissionJob executionJob) {
        super(jobScheduler);
        this.dockerExecutionService = dockerExecutionService;
        this.submissionService = submissionService;
        this.jobScheduler = jobScheduler;
        this.executionJob = executionJob;
    }
    @Override
    public void executeJob(String token) {
        executionJob.executeSubmission(token);
    }
    
    /**
     * 동기 코드 실행 메서드
     *
     * 이 메서드는 코드를 즉시 실행하고 결과를 바로 반환합니다.
     * wait=true 파라미터로 요청했을 때 사용되며, 클라이언트는 실행 완료까지 기다립니다.
     *
     * 처리 과정:
     * 1. 제출 상태를 PROCESS로 변경 (실행 중임을 표시)
     * 2. Submission을 CodeExecutionRequest로 변환
     * 3. DockerExecutionService에서 실제 코드 실행
     * 4. 실행 결과를 데이터베이스에 저장
     * 5. 최종 결과 반환
     *
     * @param submission 실행할 코드 제출 정보
     * @return ExecutionResult 실행 결과 (stdout, stderr, 실행 시간, 상태 등)
     */
    public ExecutionResult executeSync(Submission submission) {
        log.info("동기 실행 시작 - submission token: {}", submission.getToken());
        
        try {
            // 제출 상태를 "처리 중"으로 변경 (실행 시작 표시)
            submissionService.updateStatus(submission.getToken(), Status.PROCESS);
            
            // Submission 엔티티를 Docker 실행에 필요한 CodeExecutionRequest로 변환
            // 이 과정에서 보안 제약조건, 언어 정보, 소스코드 등이 포함됨
            CodeExecutionRequest request = submission.buildCodeExecutionRequest(submission);

            // Docker 컨테이너에서 실제 코드 실행
            // 여기서 컴파일, 실행, 결과 수집이 모두 이루어짐
            ExecutionResult result = dockerExecutionService.executeCode(request);
            
            // 실행 결과를 데이터베이스에 저장
            // stdout, stderr, 실행 시간, 메모리 사용량, 최종 상태 등이 저장됨
            submissionService.updateResult(submission.getToken(), result);
            
            log.info("동기 실행 완료 - submission: {}, 최종 상태: {}", 
                    submission.getToken(), result.getStatus().getName());

            return result;

        } catch (Exception e) {
            log.error("동기 실행 실패 - submission: {}", submission.getToken(), e);
            
            // 실행 실패 시 오류 결과 생성 및 저장
            ExecutionResult errorResult = ExecutionResult.error("Execution failed: " + e.getMessage());
            submissionService.updateResult(submission.getToken(), errorResult);

            return errorResult;
        }
    }


}
