package com.PBL.lab.grade.job;

import com.PBL.lab.core.dto.CodeExecutionRequest;
import com.PBL.lab.core.dto.CompilationContext;
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
        log.info("[GRADE] ========== 코드 채점 작업 시작 - Grade Token: {} ==========", gradeToken);

        Grade grade = null;
        try {
            log.debug("[GRADE] Grade 정보 조회 시작 - Token: {}", gradeToken);
            grade = gradeService.findByToken(gradeToken);

            if (grade == null) {
                log.error("[GRADE] 채점 정보를 찾을 수 없음 - Grade Token: {}", gradeToken);
                return;
            }

            log.debug("[GRADE] Grade 정보 조회 완료 - Problem ID: {}, 언어: {}",
                    grade.getProblemId(), grade.getLanguageId());

            grade.setStartedAt(LocalDateTime.now());
            grade.setExecutionHost(getHostname());

            log.info("[GRADE] 채점 상태 변경: PROCESS - Token: {}", gradeToken);
            gradeService.updateStatus(gradeToken, Status.PROCESS);

            log.info("[GRADE] 테스트케이스 조회 시작 - Problem ID: {}", grade.getProblemId());
            List<TestCase> testCases = gradeService.findTestCasesByLectureId(grade.getProblemId());
            log.info("[GRADE] 테스트케이스 조회 완료 - 총 {}개, Token: {}", testCases.size(), gradeToken);



            if (testCases.isEmpty()) {
                log.warn("테스트케이스가 없음 - grade token: {}", gradeToken);
                grade.setStatus(Status.BOXERR);
                grade.setMessage("No test cases found for this problem");
                gradeService.updateResult(grade);
                gradeProgressService.sendProgress(gradeToken);
                return;
            }

            int totalTestCases = testCases.size();
            int passedTestCases = 0;

            log.info("총 {}개의 테스트케이스로 채점 시작 - grade token: {}", totalTestCases, gradeToken);

            grade.setMessage("채점을 시작하겠습니다.");
            gradeService.updateResult(grade);
            gradeProgressService.updateProgress(gradeToken, 0, totalTestCases);

            // 1. 컴파일 준비 (1회만)
            CompilationContext compilationContext = null;
            try {
                log.info("[GRADE] ===== 1단계: 코드 컴파일 시작 =====");
                CodeExecutionRequest compileRequest = grade.buildCodeExecutionRequest(grade);
                log.debug("[GRADE] CodeExecutionRequest 생성 완료 - 소스코드 길이: {} bytes",
                        compileRequest.getSourceCode() != null ? compileRequest.getSourceCode().length() : 0);

                compilationContext = dockerExecutionService.prepareCompilation(compileRequest);

                log.info("[GRADE] 코드 컴파일 완료 - Token: {}, 소요 시간: {}ms, 컨테이너 ID: {}",
                        gradeToken, compilationContext.getCompileTime(), compilationContext.getContainerId());
            } catch (Exception e) {
                log.error("[GRADE] 코드 컴파일 실패 - Token: {}", gradeToken, e);
                grade.setStatus(Status.CE);
                grade.setMessage("컴파일 실패: " + e.getMessage());
                gradeService.updateResult(grade);
                gradeProgressService.sendProgress(gradeToken);
                return;
            }

            // 2. 각 테스트케이스 실행 (N회)
            try {
                log.info("[GRADE] ===== 2단계: 테스트케이스 실행 시작 (총 {}개) =====", totalTestCases);
                ExecutionResult commonResult = ExecutionResult.builder()
                        .status(Status.PROCESS)
                        .message("")
                        .build();

                for (int i = 0; i < testCases.size(); i++) {
                    TestCase testCase = testCases.get(i);
                    log.info("[GRADE] 테스트케이스 {}/{} 실행 시작 - Token: {}", i + 1, totalTestCases, gradeToken);

                    try {
                        // 컴파일된 코드로 실행 (빠름!)
                        log.debug("[GRADE] executeWithCompiledCode 호출 - 입력 크기: {} bytes",
                                testCase.getInput() != null ? testCase.getInput().length() : 0);

                        commonResult = dockerExecutionService.executeWithCompiledCode(
                                compilationContext,
                                testCase.getInput(),
                                testCase.getExpectedOutput());

                        log.debug("[GRADE] executeWithCompiledCode 완료 - 상태: {}, 종료 코드: {}",
                                commonResult.getStatus(), commonResult.getExitCode());

                        if (commonResult.getStatus().equals(Status.AC)) {
                            passedTestCases++;
                            log.info("[GRADE] ✅ 테스트케이스 {}/{} 통과 - Token: {}", i + 1, totalTestCases, gradeToken);

                            // 실행 성능 정보를 Grade에 저장 (최대값 사용)
                            updateGradePerformanceMetrics(grade, commonResult);

                            grade.setMessage("테스트케이스 " + (i + 1) + "/" + totalTestCases + " 통과");
                            if (totalTestCases != passedTestCases) {
                                grade.setStatus(Status.PROCESS);
                            }
                            gradeService.updateResult(grade);
                            gradeProgressService.updateProgress(gradeToken, i + 1, totalTestCases);
                        } else {
                            log.info("[GRADE] ❌ 테스트케이스 {}/{} 실패 - 상태: {}, Token: {}",
                                    i + 1, totalTestCases, commonResult.getStatus().getName(), gradeToken);

                            // 오답일 때 입출력 정보를 Grade에 저장
                            ExecutionInputOutput errorInputOutput = ExecutionInputOutput.builder()
                                    .stdin(testCase.getInput())
                                    .expectedOutput(testCase.getExpectedOutput())
                                    .stdout(commonResult.getStdout())
                                    .stderr(commonResult.getStderr())
                                    .compileOutput(commonResult.getCompileOutput())
                                    .message(commonResult.getMessage())
                                    .build();

                            grade.setInputOutput(errorInputOutput);

                            // 실행 성능 정보를 Grade에 저장
                            updateGradePerformanceMetrics(grade, commonResult);

                            grade.setMessage("테스트케이스 " + (i + 1) + "/" + totalTestCases + " 실패: "
                                    + commonResult.getStatus().getName());
                            gradeService.updateResult(grade);
                            log.info("[GRADE] 첫 번째 실패 테스트케이스로 채점 중단 - Token: {}", gradeToken);
                            break;
                        }

                    } catch (Exception e) {
                        log.error("[GRADE] 테스트케이스 {}/{} 실행 중 오류 발생 - Token: {}",
                                i + 1, totalTestCases, gradeToken, e);
                        grade.setMessage("테스트케이스 실행 중 오류 발생: " + e.getMessage());
                        grade.setStatus(Status.BOXERR);
                        gradeService.updateResult(grade);
                        gradeProgressService.sendProgress(gradeToken);
                        break;
                    }
                }

                if (commonResult.getStatus().equals(Status.AC)) {
                    grade.setMessage("채점이 완료되었습니다!");
                    grade.setStatus(Status.AC);
                    grade.setFinishedAt(LocalDateTime.now());
                    gradeService.updateResult(grade);
                    gradeProgressService.updateProgress(gradeToken, totalTestCases, totalTestCases);
                    log.info("[GRADE] ✅ 채점 성공 - Token: {}, 통과: {}/{}, 최종 상태: {}, Time: {}ms, Memory: {}KB",
                            gradeToken, passedTestCases, totalTestCases, grade.getStatus().getName(),
                            grade.getTime(), grade.getMemory());
                } else {
                    grade.setStatus(commonResult.getStatus());
                    grade.setFinishedAt(LocalDateTime.now());
                    gradeService.updateResult(grade);
                    log.error("{} : {}", gradeToken, grade);
                    gradeProgressService.sendProgress(gradeToken);
                    log.info("[GRADE] ❌ 채점 실패 - Token: {}, 통과: {}/{}, 최종 상태: {}, Time: {}ms, Memory: {}KB",
                            gradeToken, passedTestCases, totalTestCases, grade.getStatus().getName(),
                            grade.getTime(), grade.getMemory());
                }
            } finally {
                // 3. 컨테이너 정리 (1회만)
                log.info("[GRADE] ===== 3단계: 컨테이너 정리 시작 =====");
                if (compilationContext != null) {
                    log.debug("[GRADE] cleanupCompilation 호출 - 컨테이너 ID: {}", compilationContext.getContainerId());
                    dockerExecutionService.cleanupCompilation(compilationContext);
                    log.info("[GRADE] 컨테이너 정리 완료 - Token: {}", gradeToken);
                } else {
                    log.warn("[GRADE] 정리할 컴파일 컨텍스트 없음 - Token: {}", gradeToken);
                }
            }

        } catch (Exception e) {
            log.error("[GRADE] 코드 채점 작업 실패 - Token: {}", gradeToken, e);
            handleGradeFailure(grade, e);
        } finally {
            log.info("[GRADE] ========== 채점 종료 - Token: {} ==========", gradeToken);
        }
    }

    private void handleGradeFailure(Grade grade, Exception exception) {
        try {
            grade.setStatus(Status.BOXERR);
            grade.setMessage(exception.getMessage());
            grade.setInputOutput(
                    ExecutionInputOutput.builder()
                            .stderr(exception.getMessage())
                            .build()
            );

            gradeService.updateResult(grade);
            gradeProgressService.sendProgress(grade.getToken());

        } catch (Exception e) {
            log.error("채점 실패 처리 중 오류 발생 - grade token: {}", grade.getToken(), e);
        }
    }

    private void sendCallback(Grade grade) {
        if (grade.getConstraints().getCallbackUrl() != null
                && !grade.getConstraints().getCallbackUrl().trim().isEmpty()) {
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

    /**
     * ExecutionResult의 성능 정보를 Grade에 업데이트
     *
     * 여러 테스트케이스 중 worst-case 성능을 저장합니다:
     * - Time: 최대값 (가장 느린 실행 시간)
     * - Memory: 최대값 (가장 많이 사용한 메모리)
     * - ExitCode, ExitSignal: 마지막 값
     */
    private void updateGradePerformanceMetrics(Grade grade, ExecutionResult result) {
        // Time: 최대값 사용 (가장 느린 케이스)
        if (result.getTime() != null) {
            if (grade.getTime() == null || result.getTime().compareTo(grade.getTime()) > 0) {
                grade.setTime(result.getTime());
            }
        }

        // Wall Time: 최대값 사용
        if (result.getWallTime() != null) {
            if (grade.getWallTime() == null || result.getWallTime().compareTo(grade.getWallTime()) > 0) {
                grade.setWallTime(result.getWallTime());
            }
        }

        // Memory: 최대값 사용 (가장 많이 사용한 케이스)
        if (result.getMemory() != null) {
            if (grade.getMemory() == null || result.getMemory() > grade.getMemory()) {
                grade.setMemory(result.getMemory());
            }
        }

        // ExitCode, ExitSignal: 마지막 값 사용
        if (result.getExitCode() != null) {
            grade.setExitCode(result.getExitCode());
        }
        if (result.getExitSignal() != null) {
            grade.setExitSignal(result.getExitSignal());
        }
    }
}
