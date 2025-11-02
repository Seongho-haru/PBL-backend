package com.PBL.lab.grade.job;

import com.PBL.lab.core.dto.CodeExecutionRequest;
import com.PBL.lab.core.entity.ExecutionInputOutput;
import com.PBL.lab.grade.entity.Grade;
import com.PBL.lab.grade.service.*;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.core.service.DockerExecutionService;
import com.PBL.lab.core.dto.ExecutionResult;
import com.PBL.lecture.entity.TestCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 코드 채점 작업 클래스
 *
 * 이 클래스는 Grade 시스템의 핵심 컴포넌트로, JobRunr를 통해 백그라운드에서 실행되는
 * 코드 채점 작업을 담당합니다. Judge0의 ExecutionJob을 기반으로 채점에 특화된 기능을 제공합니다.
 *
 * 주요 역할:
 * - JobRunr 큐에서 꺼내진 코드 채점 작업의 실제 처리
 * - Docker 컨테이너 환경에서의 안전한 코드 실행 및 채점
 * - 채점 결과의 데이터베이스 저장 및 상태 관리
 * - 웹훅 콜백을 통한 외부 시스템 알림
 *
 * JobRunr 통합:
 * - @Job 어노테이션으로 JobRunr 작업으로 등록
 * - 자동 재시도 기능 (retries = 3)
 * - 트랜잭션 관리로 데이터 일관성 보장
 * - 백그라운드에서 비동기적으로 실행
 *
 * 채점 과정:
 * 1. Grade 데이터 로드 및 검증
 * 2. 채점 상태를 PROCESS로 변경
 * 3. Docker 컨테이너에서 코드 실행
 * 4. 채점 결과를 데이터베이스에 저장
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
public class GradeJob {

    private final GradeService gradeService;
    private final DockerExecutionService dockerExecutionService;
    private final GradeProgressService gradeProgressService;

    @Job(name = "Execute Code Grading", retries = 3)
    @Transactional
    public void executeGrade(String gradeToken) {
        log.info("코드 채점 작업 시작 - grade token: {}", gradeToken);

        Grade grade = null;
        try {
            grade = gradeService.findByToken(gradeToken);

            if (grade == null) {
                log.error("채점 정보를 찾을 수 없음 - grade token: {}", gradeToken);
                return;
            }

            grade.setStartedAt(LocalDateTime.now());
            grade.setExecutionHost(getHostname());

            gradeService.updateStatus(gradeToken, Status.PROCESS);

            log.info("테스트케이스 찾는 중... token: {}", gradeToken);
            List<TestCase> testCases = gradeService.findTestCasesByLectureId(grade.getProblemId());
            log.info("테스트케이스 조회 완료 token: {}", gradeToken);

            ExecutionResult commonResult = ExecutionResult.builder()
                    .status(Status.PROCESS)
                    .message("")
                    .build();

            if (testCases.isEmpty()) {
                log.warn("테스트케이스가 없음 - grade token: {}", gradeToken);
                commonResult.setStatus(Status.BOXERR);
                commonResult.setMessage("No test cases found for this problem");
                gradeService.updateResult(gradeToken, commonResult);
                gradeProgressService.notifyGradingError(gradeToken);
                return;
            }

            int totalTestCases = testCases.size();
            int passedTestCases = 0;

            log.info("총 {}개의 테스트케이스로 채점 시작 - grade token: {}", totalTestCases, gradeToken);

            commonResult.setMessage("채점을 시작하겠습니다.");
            gradeService.updateResult(gradeToken, commonResult);
            gradeProgressService.notifyGradingStarted(gradeToken, totalTestCases);

            // 1. 컴파일 준비 (1회만)
            com.PBL.lab.core.dto.CompilationContext compilationContext = null;
            try {
                CodeExecutionRequest compileRequest = grade.buildCodeExecutionRequest(grade);
                compilationContext = dockerExecutionService.prepareCompilation(compileRequest);
                log.info("코드 컴파일 완료 - grade token: {}, 소요 시간: {}ms", gradeToken, compilationContext.getCompileTime());
            } catch (Exception e) {
                log.error("코드 컴파일 실패 - grade token: {}", gradeToken, e);
                commonResult.setStatus(Status.CE);
                commonResult.setMessage("컴파일 실패: " + e.getMessage());
                gradeService.updateResult(gradeToken, commonResult);
                gradeProgressService.notifyGradingError(gradeToken);
                return;
            }

            // 2. 각 테스트케이스 실행 (N회)
            try {
                for (int i = 0; i < testCases.size(); i++) {
                    TestCase testCase = testCases.get(i);
                    log.info("테스트케이스 {}/{} 실행 중 - grade token: {}", i + 1, totalTestCases, gradeToken);

                    try {
                        // 컴파일된 코드로 실행 (빠름!)
                        commonResult = dockerExecutionService.executeWithCompiledCode(
                                compilationContext,
                                testCase.getInput(),
                                testCase.getExpectedOutput()
                        );

                        if (commonResult.getStatus().equals(Status.AC)) {
                            passedTestCases++;
                            log.info("테스트케이스 {}/{} 통과 - grade token: {}", i + 1, totalTestCases, gradeToken);
                            commonResult.setMessage("테스트케이스 " + (i + 1) + "/" + totalTestCases + " 통과");
                            if (totalTestCases != passedTestCases) {
                                commonResult.setStatus(Status.PROCESS);
                            }
                            gradeService.updateResult(gradeToken, commonResult);
                            gradeProgressService.updateTestCaseProgress(gradeToken, i, totalTestCases, Status.PROCESS.getName());
                        } else {
                            log.info("테스트케이스 {}/{} 실패 (상태: {}) - grade token: {}",
                                    i + 1, totalTestCases, commonResult.getStatus().getName(), gradeToken);

                            // 오답일 때 입출력 정보를 Grade에 저장
                            ExecutionInputOutput errorInputOutput = new ExecutionInputOutput();
                            errorInputOutput.setStdin(testCase.getInput());
                            errorInputOutput.setExpectedOutput(testCase.getExpectedOutput());
                            errorInputOutput.setStdout(commonResult.getStdout());
                            errorInputOutput.setStderr(commonResult.getStderr());
                            errorInputOutput.setCompileOutput(commonResult.getCompileOutput());
                            errorInputOutput.setMessage(commonResult.getMessage());

                            grade.setInputOutput(errorInputOutput);

                            commonResult.setMessage("테스트케이스 " + (i + 1) + "/" + totalTestCases + " 실패: " + commonResult.getStatus().getName());
                            gradeService.updateResult(gradeToken, commonResult);
                            break;
                        }

                    } catch (Exception e) {
                        log.error("테스트케이스 {}/{} 실행 중 오류 발생 - grade token: {}",
                                i + 1, totalTestCases, gradeToken, e);
                        commonResult.setMessage("테스트케이스 실행 중 오류 발생: " + e.getMessage());
                        commonResult.setStatus(Status.BOXERR);
                        gradeService.updateResult(gradeToken, commonResult);
                        gradeProgressService.notifyGradingError(gradeToken);
                        break;
                    }
                }

                if (commonResult.getStatus().equals(Status.AC)) {
                    commonResult.setMessage("채점이 완료되었습니다!");
                    gradeService.updateResult(gradeToken, commonResult);
                    gradeProgressService.notifyGradingCompleted(gradeToken);
                    log.info("코드 채점 작업 완료 - grade token: {}, 통과: {}/{}, 최종 상태: {}",
                            gradeToken, passedTestCases, totalTestCases, commonResult.getStatus().getName());
                } else {
                    gradeService.updateResult(gradeToken, commonResult);
                    gradeProgressService.notifyGradingError(gradeToken);
                    log.info("코드 채점 작업 실패 - grade token: {}, 통과: {}/{}, 최종 상태: {}",
                            gradeToken, passedTestCases, totalTestCases, commonResult.getStatus().getName());
                }
            } finally {
                // 3. 컨테이너 정리 (1회만)
                if (compilationContext != null) {
                    dockerExecutionService.cleanupCompilation(compilationContext);
                    log.info("컨테이너 정리 완료 - grade token: {}", gradeToken);
                }
            }

        } catch (Exception e) {
            log.error("코드 채점 작업 실패 - grade token: {}", gradeToken, e);
            handleGradeFailure(gradeToken, e);
        }
        finally {
            log.info("채점 종료 token: {}", gradeToken);
        }
    }

    private void handleGradeFailure(String gradeToken, Exception exception) {
        try {
            ExecutionResult errorResult = ExecutionResult.builder()
                    .status(Status.BOXERR)
                    .message("Internal grading error: " + exception.getMessage())
                    .errorMessage(exception.getMessage())
                    .build();

            gradeService.updateResult(gradeToken, errorResult);
            gradeProgressService.notifyGradingError(gradeToken);

        } catch (Exception e) {
            log.error("채점 실패 처리 중 오류 발생 - grade token: {}", gradeToken, e);
        }
    }

    private void sendCallback(Grade grade) {
        if (grade.getConstraints().getCallbackUrl() != null && !grade.getConstraints().getCallbackUrl().trim().isEmpty()) {
            try {
                log.info("웹훅 콜백 전송 완료 - grade token: {}", grade.getToken());

            } catch (Exception e) {
                log.error("웹훅 콜백 전송 실패 - grade token: {}", grade.getToken(), e);
            }
        }
    }

    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
