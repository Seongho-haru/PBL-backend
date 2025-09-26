package com.PBL.lab.judge0.service;

import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.service.DockerExecutionService;
import com.PBL.lab.core.service.ExecutionResult;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.core.enums.Status;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
@RequiredArgsConstructor
@Slf4j
public class ExecutionService {

    private final DockerExecutionService dockerExecutionService;
    private final SubmissionService submissionService;
    private final ExecutionJobScheduler executionJobScheduler;

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
     * @param submission 실행할 코드 제출 정보
     * @return CompletableFuture<ExecutionResult> 즉시 완료되는 Future (QUEUE 상태)
     */
    public CompletableFuture<ExecutionResult> executeAsync(Submission submission) {
        log.info("비동기 실행 예약 시작 - submission token: {}", submission.getToken());

        // 백그라운드 작업 스케줄러에 실행 작업 예약
        // ExecutionJob.executeSubmission()이 나중에 호출됨
        executionJobScheduler.scheduleExecution(submission.getToken());

        // 즉시 완료되는 Future 반환 (실제 실행은 백그라운드에서 진행)
        // 클라이언트는 이 결과를 받고 나중에 /submissions/{token}으로 결과 조회
        return CompletableFuture.completedFuture(
            ExecutionResult.builder()
                .status(Status.QUEUE)  // 대기열 상태로 반환
                .build()
        );
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
            CodeExecutionRequest request = CodeExecutionRequest.from(submission);
            
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
     * @param submission 검증할 제출 정보
     * @return ValidationResult 검증 결과 (유효/무효 및 오류 메시지)
     */
    public ValidationResult validateSubmission(Submission submission) {
        try {
            // 1. 언어 정보 존재 여부 확인
            if (submission.getLanguage() == null) {
                return ValidationResult.invalid("Language not found");
            }

            // 2. 언어가 아카이브(사용 중단) 상태인지 확인
            if (submission.getLanguage().getIsArchived()) {
                return ValidationResult.invalid("Language is archived");
            }

            // 3. 단일 소스코드 제출인 경우 소스코드 필수 확인
            if (!submission.isProject() && 
                (submission.getSourceCode() == null || submission.getSourceCode().trim().isEmpty())) {
                return ValidationResult.invalid("Source code is required");
            }

            // 4. 프로젝트 제출인 경우 추가 파일 필수 확인
            if (submission.isProject() && !submission.hasAdditionalFiles()) {
                return ValidationResult.invalid("Additional files are required for project submissions");
            }

            // 모든 검증 통과
            return ValidationResult.valid();
        } catch (Exception e) {
            // 예상치 못한 오류 발생 시
            return ValidationResult.invalid("Validation error: " + e.getMessage());
        }
    }

    /**
     * 코드 실행 요청 데이터 클래스
     * 
     * Submission 엔티티를 Docker 컨테이너에서 실행 가능한 형태로 변환한 데이터 구조체입니다.
     * DockerExecutionService에서 실제 코드 실행 시 사용되는 모든 정보를 포함합니다.
     * 
     * 포함 정보:
     * - sourceCode: 실행할 소스코드 (단일 파일)
     * - language: 프로그래밍 언어 정보 (컴파일러, 실행 명령어 등)
     * - stdin: 표준 입력 데이터
     * - expectedOutput: 예상 출력 (채점용)
     * - additionalFiles: 추가 파일들 (프로젝트 제출 시)
     * - constraints: 보안 제약조건 (시간, 메모리, 프로세스 제한 등)
     * - compilerOptions: 컴파일러 옵션
     * - commandLineArguments: 실행 시 명령행 인자
     * - redirectStderrToStdout: 에러 출력을 표준 출력으로 리다이렉션 여부
     * - enableNetwork: 네트워크 접근 허용 여부
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString(exclude = {"sourceCode", "stdin", "expectedOutput", "additionalFiles"})
    public static class CodeExecutionRequest {
        private String sourceCode;
        private Language language;
        private String stdin;
        private String expectedOutput;
        private SecurityConstraints constraints;
        private byte[] additionalFiles;
        private String compilerOptions;
        private String commandLineArguments;
        private Boolean redirectStderrToStdout;
        private Boolean enableNetwork;

        /**
         * Submission 엔티티를 CodeExecutionRequest로 변환하는 팩토리 메서드
         *
         * 
         * 데이터베이스에 저장된 Submission 정보를 Docker 실행에 필요한 형태로 변환합니다.
         * 이 과정에서 보안 제약조건도 함께 설정됩니다.
         * 
         * @param submission 변환할 Submission 엔티티
         * @return CodeExecutionRequest Docker 실행용 요청 객체
         */
        public static CodeExecutionRequest from(Submission submission) {
            CodeExecutionRequest request = new CodeExecutionRequest();
            
            // 기본 실행 정보 설정
            request.setSourceCode(submission.getSourceCode());           // 실행할 소스코드
            request.setLanguage(submission.getLanguage());               // 프로그래밍 언어 정보
            request.setStdin(submission.getInputOutput() != null ? submission.getInputOutput().getStdin() : null);                     // 표준 입력
            request.setExpectedOutput(submission.getInputOutput() != null ? submission.getInputOutput().getExpectedOutput() : null);   // 예상 출력
            request.setAdditionalFiles(submission.getConstraints().getAdditionalFiles()); // 추가 파일들 (프로젝트용)
            
            // 컴파일/실행 옵션 설정
            request.setCompilerOptions(submission.getConstraints().getCompilerOptions());         // 컴파일러 옵션
            request.setCommandLineArguments(submission.getConstraints().getCommandLineArguments()); // 명령행 인자
            request.setRedirectStderrToStdout(submission.getConstraints().getRedirectStderrToStdout()); // 에러 출력 리다이렉션
            request.setEnableNetwork(submission.getConstraints().getEnableNetwork());             // 네트워크 접근 허용

            // 보안 제약조건 생성 및 설정
            // Docker 컨테이너에서 코드 실행 시 적용될 리소스 제한사항들
            SecurityConstraints constraints = SecurityConstraints.builder()
                    .timeLimit(submission.getConstraints().getCpuTimeLimit())                    // CPU 시간 제한 (초)
                    .memoryLimit(submission.getConstraints().getMemoryLimit())                   // 메모리 사용량 제한 (KB)
                    .processLimit(submission.getConstraints().getMaxProcessesAndOrThreads())     // 최대 프로세스/스레드 수
                    .networkAccess(Boolean.TRUE.equals(submission.getConstraints().getEnableNetwork())) // 네트워크 접근 허용 여부
                    .fileSystemAccess(FileSystemAccess.READ_ONLY)              // 파일 시스템 접근 권한 (읽기 전용)
                    .build();
            request.setConstraints(constraints);

            return request;
        }
    }

    /**
     * 보안 제약조건 데이터 클래스
     * 
     * Docker 컨테이너에서 코드 실행 시 적용되는 보안 및 리소스 제한사항을 정의합니다.
     * 이 제약조건들은 악성 코드나 무한 루프 등으로부터 시스템을 보호하는 역할을 합니다.
     * 
     * 제약조건 종류:
     * - timeLimit: CPU 시간 제한 (초 단위)
     * - memoryLimit: 메모리 사용량 제한 (KB 단위)
     * - processLimit: 최대 프로세스/스레드 수 제한
     * - networkAccess: 네트워크 접근 허용 여부
     * - fileSystemAccess: 파일 시스템 접근 권한 (읽기 전용/읽기 쓰기)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SecurityConstraints {
        private BigDecimal timeLimit;
        private Integer memoryLimit;
        private Integer processLimit;
        private Boolean networkAccess;
        private FileSystemAccess fileSystemAccess;
    }

    /**
     * 파일 시스템 접근 권한 열거형
     * 
     * Docker 컨테이너 내에서 파일 시스템에 대한 접근 권한을 정의합니다.
     * 보안을 위해 기본적으로는 READ_ONLY를 사용하며, 필요시에만 READ_WRITE를 허용합니다.
     * 
     * 권한 종류:
     * - READ_ONLY: 읽기 전용 (기본값, 보안상 안전)
     * - READ_WRITE: 읽기/쓰기 허용 (특별한 경우에만 사용)
     */
    public enum FileSystemAccess {
        READ_ONLY,    // 읽기 전용 (기본값)
        READ_WRITE    // 읽기/쓰기 허용
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
