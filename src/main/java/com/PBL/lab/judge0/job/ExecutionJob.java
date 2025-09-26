package com.PBL.lab.judge0.job;

import com.PBL.lab.core.service.ConfigService;
import com.PBL.lab.core.service.DockerExecutionService;
import com.PBL.lab.core.service.ExecutionResult;
import com.PBL.lab.core.service.WebhookService;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.judge0.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 코드 실행 작업 클래스
 * 
 * 이 클래스는 Judge0 시스템의 핵심 컴포넌트로, JobRunr를 통해 백그라운드에서 실행되는
 * 코드 실행 작업을 담당합니다. 원본 Judge0 Ruby의 IsolateJob 기능을 완전히 포팅한 클래스입니다.
 * 
 * 주요 역할:
 * - JobRunr 큐에서 꺼내진 코드 실행 작업의 실제 처리
 * - Docker 컨테이너 환경에서의 안전한 코드 실행
 * - 실행 결과의 데이터베이스 저장 및 상태 관리
 * - 웹훅 콜백을 통한 외부 시스템 알림
 * 
 * JobRunr 통합:
 * - @Job 어노테이션으로 JobRunr 작업으로 등록
 * - 자동 재시도 기능 (retries = 3)
 * - 트랜잭션 관리로 데이터 일관성 보장
 * - 백그라운드에서 비동기적으로 실행
 * 
 * 실행 과정:
 * 1. Submission 데이터 로드 및 검증
 * 2. 실행 상태를 PROCESS로 변경
 * 3. Docker 컨테이너에서 코드 실행
 * 4. 실행 결과를 데이터베이스에 저장
 * 5. 웹훅 콜백 전송 (설정된 경우)
 * 
 * 오류 처리:
 * - 실행 실패 시 BOXERR 상태로 변경
 * - 상세한 오류 로깅 및 추적
 * - 재시도 가능한 오류와 치명적 오류 구분
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionJob {

    private final SubmissionService submissionService;      // 제출 데이터 관리 서비스
    private final DockerExecutionService dockerExecutionService;  // Docker 컨테이너 실행 서비스
    private final WebhookService webhookService;            // 웹훅 콜백 전송 서비스
    private final ConfigService configService;              // 시스템 설정 관리 서비스

    /**
     * 코드 제출 실행 메서드
     * 
     * 이 메서드는 JobRunr에 의해 큐에서 꺼내져서 실행되는 핵심 메서드입니다.
     * 원본 Judge0 Ruby의 IsolateJob#perform 메서드와 동일한 기능을 수행합니다.
     * 
     * JobRunr 어노테이션:
     * - @Job: JobRunr 작업으로 등록되어 백그라운드에서 실행됨
     * - name: 작업 식별을 위한 이름
     * - retries: 실패 시 자동 재시도 횟수 (3회)
     * 
     * 트랜잭션 관리:
     * - @Transactional: 데이터베이스 작업의 일관성 보장
     * - 실행 중 오류 발생 시 자동 롤백
     * 
     * 실행 단계:
     * 1. Submission 데이터 로드 및 존재 여부 확인
     * 2. 실행 시작 시간 및 호스트 정보 설정
     * 3. 상태를 PROCESS로 변경 (실행 중임을 표시)
     * 4. Docker 컨테이너에서 실제 코드 실행
     * 5. 실행 결과를 데이터베이스에 저장
     * 6. 웹훅 콜백 전송 (설정된 경우)
     * 
     * @param submissionToken 실행할 제출의 고유 토큰
     */
    @Job(name = "Execute Code Submission", retries = 3)
    @Transactional
    public void executeSubmission(String submissionToken) {
        log.info("코드 실행 작업 시작 - submission token: {}", submissionToken);
        
        Submission submission = null;
        try {
            // 데이터베이스에서 제출 정보 로드
            // 토큰을 기반으로 실행할 코드와 설정 정보를 조회
            submission = submissionService.findByToken(submissionToken);
            
            if (submission == null) {
                log.error("제출 정보를 찾을 수 없음 - submission token: {}", submissionToken);
                return;
            }
            
            // 실행 시작 정보 설정
            // 실행 시작 시간과 실행 서버 호스트명을 기록하여 추적 가능하도록 함
            submission.setStartedAt(LocalDateTime.now());
            submission.setExecutionHost(getHostname());
            
            // 제출 상태를 "처리 중"으로 변경
            // 클라이언트가 결과를 조회할 때 실행 중임을 알 수 있도록 함
            submissionService.updateStatus(submissionToken, Status.PROCESS);
            
            // Docker 컨테이너에서 실제 코드 실행
            // Submission 정보를 CodeExecutionRequest로 변환하여 Docker 실행 서비스에 전달
            ExecutionService.CodeExecutionRequest request = ExecutionService.CodeExecutionRequest.from(submission);
            ExecutionResult result = dockerExecutionService.executeCode(request);
            
            // 실행 결과를 데이터베이스에 저장
            // stdout, stderr, 실행 시간, 메모리 사용량, 최종 상태 등이 저장됨
            submissionService.updateResult(submissionToken, result);
            
            log.info("코드 실행 작업 완료 - submission token: {}, 최종 상태: {}", 
                    submissionToken, result.getStatus().getName());
            
        } catch (Exception e) {
            log.error("코드 실행 작업 실패 - submission token: {}", submissionToken, e);
            // 실행 실패 시 오류 처리 및 상태 업데이트
            handleExecutionFailure(submissionToken, e);
        } finally {
            // 웹훅 콜백 전송 (설정된 경우)
            // 실행 완료 후 외부 시스템에 결과를 알림
            if (submission != null) {
                sendCallback(submission);
            }
        }
    }

    /**
     * 코드 실행 실패 처리 메서드
     * 
     * 이 메서드는 코드 실행 중 발생한 예외를 처리하고 적절한 오류 상태로 업데이트합니다.
     * JobRunr의 재시도 메커니즘과 함께 사용되어 시스템의 안정성을 보장합니다.
     * 
     * 처리 과정:
     * 1. 예외 정보를 기반으로 ExecutionResult 생성
     * 2. 상태를 BOXERR (시스템 오류)로 설정
     * 3. 오류 메시지를 데이터베이스에 저장
     * 4. 클라이언트가 오류 상황을 파악할 수 있도록 함
     * 
     * 오류 상태 설명:
     * - BOXERR: 시스템 내부 오류 (Docker 실행 실패, 리소스 부족 등)
     * - 다른 상태들과 구분하여 클라이언트가 적절한 처리를 할 수 있도록 함
     * 
     * @param submissionToken 오류가 발생한 제출의 고유 토큰
     * @param exception 발생한 예외 객체
     */
    private void handleExecutionFailure(String submissionToken, Exception exception) {
        try {
            // 실행 실패 결과 객체 생성
            // BOXERR 상태로 설정하여 시스템 오류임을 명시
            ExecutionResult errorResult = ExecutionResult.builder()
                    .status(com.PBL.lab.core.enums.Status.BOXERR)  // 시스템 오류 상태
                    .message("Internal execution error: " + exception.getMessage())  // 사용자용 오류 메시지
                    .errorMessage(exception.getMessage())  // 개발자용 상세 오류 메시지
                    .build();
            
            // 오류 결과를 데이터베이스에 저장
            // 클라이언트가 결과를 조회할 때 오류 상황을 확인할 수 있도록 함
            submissionService.updateResult(submissionToken, errorResult);
            
        } catch (Exception e) {
            // 오류 처리 중에도 예외가 발생한 경우 (매우 드문 상황)
            log.error("실행 실패 처리 중 오류 발생 - submission token: {}", submissionToken, e);
        }
    }

    /**
     * 웹훅 콜백 알림 전송 메서드
     * 
     * 이 메서드는 코드 실행이 완료된 후 외부 시스템에 결과를 알리는 웹훅 콜백을 전송합니다.
     * 클라이언트가 실시간으로 실행 결과를 받을 수 있도록 하는 중요한 기능입니다.
     * 
     * 웹훅 콜백의 장점:
     * - 폴링 방식보다 효율적인 실시간 알림
     * - 서버 리소스 절약 (클라이언트가 주기적으로 조회할 필요 없음)
     * - 외부 시스템과의 자동 연동 가능
     * 
     * 전송 조건:
     * - submission에 callbackUrl이 설정되어 있는 경우에만 전송
     * - 빈 문자열이나 null인 경우 전송하지 않음
     * 
     * 전송 과정:
     * 1. 콜백 URL 존재 여부 확인
     * 2. 최신 실행 결과를 데이터베이스에서 다시 로드
     * 3. WebhookService를 통해 HTTP POST 요청으로 결과 전송
     * 4. 전송 실패 시 로그 기록 (재시도하지 않음)
     * 
     * @param submission 콜백을 전송할 제출 정보
     */
    private void sendCallback(Submission submission) {
        // 콜백 URL이 설정되어 있는지 확인
        if (submission.getConstraints().getCallbackUrl() != null && !submission.getConstraints().getCallbackUrl().trim().isEmpty()) {
            try {
                // 최신 실행 결과를 데이터베이스에서 다시 로드
                // 실행 완료 후 업데이트된 결과 정보를 확실히 전송하기 위함
                Submission updatedSubmission = submissionService.findByToken(submission.getToken());
                
                // WebhookService를 통해 외부 시스템에 결과 전송
                // HTTP POST 요청으로 실행 결과를 JSON 형태로 전송
                webhookService.sendCallback(updatedSubmission);
                
            } catch (Exception e) {
                // 웹훅 전송 실패 시 로그만 기록 (재시도하지 않음)
                // 웹훅 실패는 클라이언트의 문제일 수 있으므로 시스템에 영향을 주지 않음
                log.error("웹훅 콜백 전송 실패 - submission token: {}", submission.getToken(), e);
            }
        }
    }

    /**
     * 실행 서버 호스트명 조회 메서드
     * 
     * 이 메서드는 현재 코드가 실행되고 있는 서버의 호스트명을 조회합니다.
     * 분산 환경에서 여러 서버가 실행 중일 때 어떤 서버에서 실행되었는지 추적하는 데 사용됩니다.
     * 
     * 사용 목적:
     * - 실행 서버 추적: 어떤 서버에서 코드가 실행되었는지 기록
     * - 디버깅 지원: 문제 발생 시 특정 서버에서의 실행 여부 확인
     * - 모니터링: 서버별 실행 통계 및 부하 분산 확인
     * - 로그 분석: 서버별 실행 패턴 분석
     * 
     * 호스트명 조회 과정:
     * 1. InetAddress.getLocalHost()로 로컬 호스트 정보 조회
     * 2. getHostName()으로 호스트명 추출
     * 3. 조회 실패 시 "unknown" 반환
     * 
     * 예외 처리:
     * - 네트워크 설정 문제로 호스트명 조회 실패 시 "unknown" 반환
     * - 시스템 오류로 인한 예외 발생 시에도 안전하게 처리
     * 
     * @return String 현재 서버의 호스트명 (조회 실패 시 "unknown")
     */
    private String getHostname() {
        try {
            // 로컬 호스트의 호스트명 조회
            // 분산 환경에서 실행 서버를 식별하는 데 사용됨
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            // 호스트명 조회 실패 시 기본값 반환
            // 네트워크 설정 문제나 시스템 오류로 인한 예외 발생 시 안전하게 처리
            return "unknown";
        }
    }
}
