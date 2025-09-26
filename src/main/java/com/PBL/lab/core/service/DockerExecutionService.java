package com.PBL.lab.core.service;

import com.PBL.lab.judge0.service.ExecutionService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.PBL.lab.core.docker.ContainerPool;
import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Docker 컨테이너 풀을 활용한 코드 실행 서비스
 * 
 * 이 서비스는 Judge0 시스템의 핵심 컴포넌트로, Docker 컨테이너를 사용하여
 * 사용자가 제출한 코드를 안전하고 격리된 환경에서 실행합니다.
 * 
 * 주요 특징:
 * - 컨테이너 풀링: 미리 생성된 컨테이너를 재사용하여 성능 최적화
 * - 보안 격리: 각 코드 실행을 독립된 컨테이너에서 수행
 * - 리소스 제한: CPU 시간, 메모리, 파일 크기 등 실행 제약사항 적용
 * - 다중 언어 지원: Python, Java, C++, JavaScript 등 다양한 언어 지원
 * 
 * 실행 과정:
 * 1. 컨테이너 풀에서 사용 가능한 컨테이너 획득
 * 2. 실행 파일 준비 (소스코드, 입력 데이터, 스크립트 등)
 * 3. 컴파일 (필요한 경우)
 * 4. 코드 실행 및 결과 수집
 * 5. 컨테이너 정리 및 풀에 반환
 * 
 * 보안 기능:
 * - nobody 사용자로 실행하여 권한 최소화
 * - 시간 제한으로 무한 루프 방지
 * - 메모리 제한으로 시스템 보호
 * - 파일 시스템 접근 제한
 * 
 * 성능 최적화:
 * - 컨테이너 재사용으로 생성 시간 단축
 * - 비동기 정리 작업으로 응답 시간 개선
 * - 풀 통계를 통한 모니터링 및 튜닝
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DockerExecutionService {

    private final DockerClient dockerClient;        // Docker API 클라이언트 (컨테이너 제어)
    private final ConfigService configService;      // 시스템 설정 관리 서비스
    private final ContainerPool containerPool;      // 컨테이너 풀 관리 서비스
    
    @Value("${judge0.docker-execution.container-timeout:30000}")
    private long containerAcquireTimeout;           // 컨테이너 획득 대기 시간 (밀리초)
    
    @Value("${judge0.docker-execution.cleanup-async:true}")
    private boolean asyncCleanup;                   // 비동기 정리 작업 사용 여부

    /**
     * 코드 실행 메인 메서드
     * 
     * 이 메서드는 컨테이너 풀을 활용하여 코드를 안전하게 실행하는 전체 과정을 관리합니다.
     * ExecutionJob에서 호출되어 실제 Docker 컨테이너에서 코드 실행을 수행합니다.
     * 
     * 실행 과정:
     * 1. 컨테이너 풀에서 사용 가능한 컨테이너 획득
     * 2. 실행에 필요한 파일들 준비 (소스코드, 스크립트, 입력 데이터)
     * 3. 컨테이너 내에서 코드 컴파일 및 실행
     * 4. 실행 결과 수집 및 반환
     * 5. 컨테이너 정리 및 풀에 반환
     * 
     * 성능 측정:
     * - 컨테이너 획득 시간 측정
     * - 전체 실행 시간 측정
     * - 결과 객체에 성능 메트릭 포함
     * 
     * 오류 처리:
     * - 컨테이너 획득 실패 시 TimeoutException 처리
     * - 실행 중 오류 발생 시 적절한 오류 메시지 반환
     * - finally 블록에서 컨테이너 반환 보장
     * 
     * @param request 실행할 코드와 설정 정보
     * @return ExecutionResult 실행 결과 (stdout, stderr, 실행 시간, 상태 등)
     */
    public ExecutionResult executeCode(ExecutionService.CodeExecutionRequest request) {
        ContainerPool.PooledContainer container = null;
        Path containerWorkDir = null;
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("컨테이너 풀을 활용한 코드 실행 시작 - 언어: {}", request.getLanguage().getName());
            
            // 컨테이너 풀에서 사용 가능한 컨테이너 획득
            // 설정된 타임아웃 시간 내에 컨테이너를 가져오지 못하면 TimeoutException 발생
            container = acquireContainer();
            containerWorkDir = Paths.get(container.getMountPath());
            
            long acquireTime = System.currentTimeMillis() - startTime;
            log.debug("컨테이너 획득 완료 - 소요 시간: {}ms", acquireTime);
            
            // 실행에 필요한 모든 파일 준비
            // 소스코드, 입력 데이터, 컴파일/실행 스크립트 생성
            prepareExecutionFiles(request, containerWorkDir);
            
            // 컨테이너 내에서 실제 코드 실행
            ExecutionResult result = executeInPooledContainer(request, container, containerWorkDir);
            
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("코드 실행 완료 - 총 소요 시간: {}ms, 최종 상태: {}", totalTime, result.getStatus());
            
            // 성능 메트릭을 결과 객체에 추가
            result.setContainerAcquireTime(acquireTime);
            result.setTotalExecutionTime(totalTime);
            
            return result;
            
        } catch (TimeoutException e) {
            log.error("컨테이너 풀에서 컨테이너 획득 실패", e);
            return ExecutionResult.error("Service temporarily unavailable - please try again");
        } catch (Exception e) {
            log.error("코드 실행 실패", e);
            return ExecutionResult.error("Internal execution error: " + e.getMessage());
        } finally {
            // 컨테이너 정리 및 풀에 반환
            // 성공/실패 여부와 관계없이 반드시 실행되어 리소스 누수 방지
            if (container != null) {
                releaseContainer(container, containerWorkDir);
            }
        }
    }
    
    /**
     * 컨테이너 풀에서 사용 가능한 컨테이너를 획득하는 메서드
     * 
     * 이 메서드는 컨테이너 풀에서 사용 가능한 컨테이너를 가져옵니다.
     * 설정된 타임아웃 시간 내에 컨테이너를 획득하지 못하면 TimeoutException을 발생시킵니다.
     * 
     * 풀 고갈 상황:
     * - 모든 컨테이너가 사용 중일 때 발생
     * - 시스템 부하가 높을 때 자주 발생
     * - 풀 통계를 로그에 기록하여 모니터링 지원
     * 
     * @return PooledContainer 사용 가능한 컨테이너 객체
     * @throws TimeoutException 타임아웃 시간 내에 컨테이너를 획득하지 못한 경우
     * @throws InterruptedException 스레드가 중단된 경우
     */
    private ContainerPool.PooledContainer acquireContainer() throws TimeoutException, InterruptedException {
        try {
            // 컨테이너 풀에서 설정된 타임아웃 시간 내에 컨테이너 획득 시도
            return containerPool.acquireContainer(containerAcquireTimeout);
        } catch (TimeoutException e) {
            // 컨테이너 풀 고갈 시 통계 정보를 로그에 기록
            // 모니터링 및 시스템 튜닝에 활용
            ContainerPool.PoolStatistics stats = containerPool.getStatistics();
            log.warn("컨테이너 풀 고갈 - 통계: {}", stats);
            throw e;
        }
    }
    
    /**
     * 사용 완료된 컨테이너를 풀에 반환하는 메서드
     * 
     * 이 메서드는 코드 실행이 완료된 후 컨테이너를 정리하고 풀에 반환합니다.
     * 비동기 정리 옵션에 따라 동기/비동기로 처리할 수 있습니다.
     * 
     * 정리 과정:
     * 1. 작업 디렉토리 정리 (임시 파일 삭제)
     * 2. 컨테이너를 풀에 반환하여 재사용 가능하도록 함
     * 
     * 비동기 정리의 장점:
     * - 클라이언트 응답 시간 단축
     * - 정리 작업이 메인 실행 흐름을 차단하지 않음
     * - 높은 처리량 환경에서 성능 향상
     * 
     * 동기 정리의 장점:
     * - 즉시 리소스 해제
     * - 디버깅 시 정리 상태 확인 용이
     * - 메모리 사용량 예측 가능
     * 
     * @param container 반환할 컨테이너 객체
     * @param workDir 정리할 작업 디렉토리 경로
     */
    private void releaseContainer(ContainerPool.PooledContainer container, Path workDir) {
        try {
            if (asyncCleanup) {
                // 비동기 정리: CompletableFuture를 사용하여 백그라운드에서 정리 작업 수행
                // 클라이언트 응답 시간을 단축하고 처리량을 향상시킴
                CompletableFuture.runAsync(() -> {
                    cleanupWorkDirectory(workDir);
                    containerPool.releaseContainer(container.getContainerId());
                });
            } else {
                // 동기 정리: 즉시 정리 작업 수행
                // 디버깅이나 메모리 제약이 있는 환경에서 사용
                cleanupWorkDirectory(workDir);
                containerPool.releaseContainer(container.getContainerId());
            }
            
            log.debug("컨테이너 풀에 반환 완료 - 컨테이너 ID: {}", container.getContainerId());
        } catch (Exception e) {
            log.error("컨테이너 반환 중 오류 발생", e);
        }
    }
    
    /**
     * 코드 실행에 필요한 파일들을 준비하는 메서드
     * 
     * 이 메서드는 컨테이너 내에서 코드를 실행하기 위해 필요한 모든 파일들을 생성합니다.
     * 소스코드, 입력 데이터, 컴파일/실행 스크립트 등을 준비합니다.
     * 
     * 준비하는 파일들:
     * - 소스코드 파일: 언어별 확장자로 저장
     * - 표준 입력 파일: stdin.txt로 저장
     * - 추가 파일들: 프로젝트 제출 시 포함된 파일들 (향후 구현)
     * - 컴파일 스크립트: 컴파일이 필요한 언어용
     * - 실행 스크립트: 실제 코드 실행용
     * 
     * 보안 고려사항:
     * - 작업 디렉토리 정리로 이전 실행의 잔여 파일 제거
     * - UTF-8 인코딩으로 파일 저장
     * - 실행 권한 설정으로 스크립트 실행 가능
     * 
     * @param request 실행 요청 정보 (소스코드, 언어, 입력 데이터 등)
     * @param workDir 작업 디렉토리 경로
     * @throws IOException 파일 생성/쓰기 중 오류 발생 시
     */
    private void prepareExecutionFiles(ExecutionService.CodeExecutionRequest request, Path workDir) throws IOException {
        Language language = request.getLanguage();
        
        // 이전 실행의 잔여 파일들을 정리하여 깨끗한 환경 준비
        cleanupWorkDirectory(workDir);
        
        // 소스코드 파일 생성
        if (request.getSourceCode() != null && !request.getSourceCode().trim().isEmpty()) {
            Path sourceFile = workDir.resolve(language.getSourceFile());
            Files.write(sourceFile, request.getSourceCode().getBytes(StandardCharsets.UTF_8));
            log.debug("소스코드 파일 생성 완료: {}", sourceFile.getFileName());
        }
        
        // 표준 입력 파일 생성
        String stdin = request.getStdin() != null ? request.getStdin() : "";
        Files.write(workDir.resolve("stdin.txt"), stdin.getBytes(StandardCharsets.UTF_8));
        
        // 추가 파일들 처리 (프로젝트 제출 시)
        if (request.getAdditionalFiles() != null && request.getAdditionalFiles().length > 0) {
            // TODO: additionalFiles는 byte[]로 저장되므로, 압축 해제 및 파일 추출 로직 구현 필요
            // 현재는 단순히 로그만 남기고 실제 파일 처리는 추후 구현
            log.debug("추가 파일들이 존재하지만 아직 처리되지 않음: {} bytes", request.getAdditionalFiles().length);
        }
        
        // 컴파일 스크립트 생성 (컴파일이 필요한 언어의 경우)
        createCompileScript(request, workDir);
        
        // 실행 스크립트 생성
        createRunScript(request, workDir);
        
        log.debug("모든 실행 파일 준비 완료 - 작업 디렉토리: {}", workDir);
    }
    
    /**
     * 컴파일 스크립트 생성 메서드
     * 
     * 이 메서드는 컴파일이 필요한 언어(Java, C++, C 등)를 위한 컴파일 스크립트를 생성합니다.
     * 인터프리터 언어(Python, JavaScript 등)는 컴파일이 필요하지 않으므로 스크립트를 생성하지 않습니다.
     * 
     * 스크립트 내용:
     * - shebang: #!/bin/bash
     * - 오류 시 중단: set -e
     * - 작업 디렉토리 이동: cd /tmp/judge
     * - 컴파일 명령어 실행
     * - 성공 메시지 출력
     * 
     * 컴파일 옵션 처리:
     * - 사용자 입력 옵션을 sanitize하여 보안 위험 제거
     * - %s 플레이스홀더가 있으면 옵션으로 교체
     * - 없으면 명령어 뒤에 옵션 추가
     * 
     * @param request 실행 요청 정보
     * @param workDir 작업 디렉토리 경로
     * @throws IOException 스크립트 파일 생성 중 오류 발생 시
     */
    private void createCompileScript(ExecutionService.CodeExecutionRequest request, Path workDir) throws IOException {
        Language language = request.getLanguage();
        
        // 컴파일이 필요하지 않은 언어는 스크립트 생성하지 않음
        if (!language.supportsCompilation()) {
            return;
        }
        
        // 컴파일러 옵션 보안 검증 및 정리
        String compilerOptions = sanitizeOptions(request.getCompilerOptions());
        String compileCommand = language.getEffectiveCompileCommand();
        
        // %s 플레이스홀더를 실제 컴파일러 옵션으로 교체
        if (compileCommand.contains("%s")) {
            compileCommand = compileCommand.replace("%s", compilerOptions);
        } else {
            compileCommand = compileCommand + " " + compilerOptions;
        }
        
        // 컴파일 스크립트 파일 생성
        Path compileScript = workDir.resolve("compile.sh");
        String scriptContent = "#!/bin/bash\n" +
                              "set -e\n" +                    // 오류 발생 시 스크립트 중단
                              "cd /tmp/judge\n" +             // 작업 디렉토리로 이동
                              compileCommand + "\n" +         // 실제 컴파일 명령어
                              "echo \"Compilation completed successfully\"";  // 성공 메시지
                              
        Files.write(compileScript, scriptContent.getBytes(StandardCharsets.UTF_8));
        compileScript.toFile().setExecutable(true);  // 실행 권한 부여
        
        log.debug("컴파일 스크립트 생성 완료: {}", compileCommand);
    }
    
    /**
     * 실행 스크립트 생성 메서드
     * 
     * 이 메서드는 실제 코드 실행을 위한 bash 스크립트를 생성합니다.
     * 모든 언어에 대해 공통적으로 사용되는 실행 스크립트입니다.
     * 
     * 스크립트 기능:
     * - 시간 제한 적용: timeout 명령어로 무한 루프 방지
     * - 표준 입력 리다이렉션: stdin.txt에서 입력 읽기
     * - 표준 출력 리다이렉션: stdout.txt로 출력 저장
     * - 표준 에러 처리: 설정에 따라 stdout으로 합치거나 별도 파일로 저장
     * - 종료 코드 저장: exit_code.txt에 프로그램 종료 코드 기록
     * 
     * 보안 기능:
     * - 명령행 인자 sanitize로 보안 위험 제거
     * - 시간 제한으로 DoS 공격 방지
     * - nobody 사용자로 실행 (Docker 실행 시 설정)
     * 
     * @param request 실행 요청 정보
     * @param workDir 작업 디렉토리 경로
     * @throws IOException 스크립트 파일 생성 중 오류 발생 시
     */
    private void createRunScript(ExecutionService.CodeExecutionRequest request, Path workDir) throws IOException {
        Language language = request.getLanguage();
        
        // 명령행 인자 보안 검증 및 정리
        String commandLineArgs = sanitizeOptions(request.getCommandLineArguments());
        String runCommand = language.getEffectiveRunCommand() + " " + commandLineArgs;
        
        // 표준 에러 출력 처리 방식 결정
        String stderrRedirect = Boolean.TRUE.equals(request.getRedirectStderrToStdout()) ? 
                               "2>&1" : "2>stderr.txt";
        
        // 실행 스크립트 파일 생성
        Path runScript = workDir.resolve("run.sh");
        String scriptContent = "#!/bin/bash\n" +
                              "cd /tmp/judge\n" +                    // 작업 디렉토리로 이동
                              "timeout " + request.getConstraints().getTimeLimit() + "s " +  // 시간 제한 적용
                              runCommand + " <stdin.txt >stdout.txt " + stderrRedirect + "\n" +  // 실행 및 입출력 리다이렉션
                              "echo $? >exit_code.txt";             // 종료 코드 저장
                              
        Files.write(runScript, scriptContent.getBytes(StandardCharsets.UTF_8));
        runScript.toFile().setExecutable(true);  // 실행 권한 부여
        
        log.debug("실행 스크립트 생성 완료: {}", runCommand);
    }
    
    private ExecutionResult executeInPooledContainer(ExecutionService.CodeExecutionRequest request, 
                                                     ContainerPool.PooledContainer container,
                                                     Path workDir) throws Exception {
        Language language = request.getLanguage();
        String containerId = container.getContainerId();
        
        String compileOutput = "";
        if (language.supportsCompilation()) {
            long compileStart = System.currentTimeMillis();
            compileOutput = runCompilation(containerId);
            long compileTime = System.currentTimeMillis() - compileStart;
            
            log.debug("Compilation took {}ms", compileTime);
            
            if (compileOutput.contains("error") || compileOutput.contains("Error")) {
                return ExecutionResult.compilationError(compileOutput);
            }
        }
        
        long executionStart = System.currentTimeMillis();
        
        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withCmd("bash", "/tmp/judge/run.sh")
                .withWorkingDir("/tmp/judge")
                .withUser("nobody:nogroup")
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();
        
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        
        BigDecimal timeLimit = request.getConstraints().getTimeLimit();
        boolean completed = dockerClient.execStartCmd(execResponse.getId())
                .exec(new ExecResultCallback(stdout, stderr))
                .awaitCompletion(timeLimit.longValue() + 2, TimeUnit.SECONDS);
        
        long executionTime = System.currentTimeMillis() - executionStart;
        BigDecimal wallTime = BigDecimal.valueOf(executionTime / 1000.0);
        
        InspectExecResponse inspectExec = dockerClient.inspectExecCmd(execResponse.getId()).exec();
        Integer exitCode = inspectExec.getExitCodeLong() != null ? 
                          inspectExec.getExitCodeLong().intValue() : null;
        
        String stdoutContent = readFileContent(workDir.resolve("stdout.txt"));
        String stderrContent = readFileContent(workDir.resolve("stderr.txt"));
        String exitCodeContent = readFileContent(workDir.resolve("exit_code.txt"));
        
        if (exitCodeContent != null && !exitCodeContent.trim().isEmpty()) {
            try {
                exitCode = Integer.parseInt(exitCodeContent.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        
        BigDecimal actualTime = calculateActualExecutionTime(workDir, wallTime);
        
        Status status = determineExecutionStatus(completed, exitCode, request.getExpectedOutput(), stdoutContent);
        
        Integer memoryUsage = getExecutionMemoryUsage(container, workDir);
        
        return ExecutionResult.builder()
                .stdout(stdoutContent)
                .stderr(stderrContent)
                .compileOutput(compileOutput.isEmpty() ? null : compileOutput)
                .time(actualTime)
                .wallTime(wallTime)
                .memory(memoryUsage)
                .exitCode(exitCode)
                .status(status)
                .build();
    }
    
    private String runCompilation(String containerId) throws Exception {
        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withCmd("bash", "/tmp/judge/compile.sh")
                .withWorkingDir("/tmp/judge")
                .withUser("nobody:nogroup")
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        boolean completed = dockerClient.execStartCmd(execResponse.getId())
                .exec(new ExecResultCallback(output, output))
                .awaitCompletion(30, TimeUnit.SECONDS);
        
        if (!completed) {
            throw new RuntimeException("Compilation timeout exceeded");
        }
        
        return output.toString(StandardCharsets.UTF_8);
    }
    
    private BigDecimal calculateActualExecutionTime(Path workDir, BigDecimal wallTime) {
        try {
            Path timingFile = workDir.resolve("timing.txt");
            if (Files.exists(timingFile)) {
                String timing = Files.readString(timingFile, StandardCharsets.UTF_8).trim();
                return new BigDecimal(timing);
            }
        } catch (Exception e) {
            log.debug("Could not read timing information", e);
        }
        
        BigDecimal overhead = new BigDecimal("0.05");
        BigDecimal actualTime = wallTime.subtract(overhead);
        return actualTime.compareTo(BigDecimal.ZERO) > 0 ? actualTime : BigDecimal.ZERO;
    }
    
    private Integer getExecutionMemoryUsage(ContainerPool.PooledContainer container, Path workDir) {
        try {
            Path memoryFile = workDir.resolve("memory.txt");
            if (Files.exists(memoryFile)) {
                String memory = Files.readString(memoryFile, StandardCharsets.UTF_8).trim();
                return Integer.parseInt(memory) / 1024;
            }
            
            return 2048;
            
        } catch (Exception e) {
            log.debug("Could not determine memory usage", e);
            return null;
        }
    }
    
    private Status determineExecutionStatus(boolean completed, Integer exitCode, 
                                           String expectedOutput, String actualOutput) {
        if (!completed) {
            return Status.TLE;
        }
        
        if (exitCode == null || exitCode != 0) {
            if (exitCode != null && exitCode == 124) {
                return Status.TLE;
            } else if (exitCode != null && exitCode == 137) {
                return Status.MLE;
            } else if (exitCode != null && exitCode > 128) {
                return Status.findRuntimeErrorByStatusCode(exitCode - 128);
            } else {
                return Status.NZEC;
            }
        }
        
        if (expectedOutput != null && !expectedOutput.trim().isEmpty()) {
            String normalizedExpected = normalizeOutput(expectedOutput);
            String normalizedActual = normalizeOutput(actualOutput);
            
            if (normalizedExpected.equals(normalizedActual)) {
                return Status.AC;
            } else {
                return Status.WA;
            }
        }
        
        return Status.AC;
    }
    
    private String normalizeOutput(String output) {
        if (output == null) {
            return "";
        }
        
        return Arrays.stream(output.split("\n"))
                .map(String::stripTrailing)
                .collect(Collectors.joining("\n"))
                .stripTrailing();
    }
    
    private String readFileContent(Path filePath) {
        try {
            if (Files.exists(filePath) && Files.size(filePath) > 0) {
                long fileSize = Files.size(filePath);
                if (fileSize > 10 * 1024 * 1024) {
                    log.warn("Output file too large: {} bytes", fileSize);
                    return "[Output truncated - file too large]";
                }
                return Files.readString(filePath, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.warn("Failed to read file: {}", filePath, e);
        }
        return null;
    }
    
    /**
     * 사용자 입력 옵션 보안 검증 및 정리 메서드
     * 
     * 이 메서드는 사용자가 입력한 컴파일러 옵션이나 명령행 인자에서
     * 잠재적인 보안 위험 요소들을 제거합니다.
     * 
     * 제거하는 위험 문자들:
     * - &: 백그라운드 실행
     * - ;: 명령어 연결
     * - <, >: 입출력 리다이렉션
     * - |: 파이프
     * - `: 명령어 치환
     * - $: 변수 치환
     * - \: 이스케이프 문자
     * 
     * 보안 목적:
     * - 명령어 주입 공격 방지
     * - 시스템 명령어 실행 차단
     * - 파일 시스템 접근 제한
     * - 권한 상승 공격 방지
     * 
     * @param options 정리할 옵션 문자열
     * @return String 보안 검증을 통과한 안전한 옵션 문자열
     */
    private String sanitizeOptions(String options) {
        if (options == null) {
            return "";
        }
        
        // 잠재적인 보안 위험 문자들을 제거하여 안전한 옵션만 남김
        return options.replaceAll("[&;<>|`$\\\\]", "").trim();
    }
    
    /**
     * 작업 디렉토리 정리 메서드
     * 
     * 이 메서드는 이전 실행에서 생성된 임시 파일들을 모두 삭제하여
     * 깨끗한 실행 환경을 준비합니다.
     * 
     * 정리 과정:
     * 1. 작업 디렉토리 존재 여부 확인
     * 2. 디렉토리 내 모든 파일과 하위 디렉토리 순회
     * 3. 역순 정렬로 하위 파일부터 삭제 (디렉토리 삭제를 위해)
     * 4. 각 파일/디렉토리 삭제 시도
     * 
     * 안전성 고려사항:
     * - 디렉토리 자체는 삭제하지 않음 (상위 디렉토리만 유지)
     * - 삭제 실패 시에도 계속 진행 (일부 파일이 사용 중일 수 있음)
     * - 예외 발생 시에도 로그만 기록하고 중단하지 않음
     * 
     * @param workDir 정리할 작업 디렉토리 경로
     */
    private void cleanupWorkDirectory(Path workDir) {
        if (workDir == null || !Files.exists(workDir)) {
            return;
        }
        
        try {
            // 작업 디렉토리 내 모든 파일과 하위 디렉토리를 순회
            Files.walk(workDir)
                    .filter(path -> !path.equals(workDir))  // 루트 디렉토리는 제외
                    .sorted(Comparator.reverseOrder())     // 역순 정렬 (하위 파일부터 삭제)
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);    // 파일/디렉토리 삭제
                        } catch (IOException e) {
                            log.debug("파일 삭제 실패: {}", path);
                        }
                    });
            log.debug("작업 디렉토리 정리 완료: {}", workDir);
        } catch (IOException e) {
            log.warn("작업 디렉토리 정리 실패: {}", workDir, e);
        }
    }
    
    /**
     * Docker 실행 결과 콜백 클래스
     * 
     * 이 클래스는 Docker 컨테이너에서 실행되는 명령어의 출력을 실시간으로 수집합니다.
     * Docker Java API의 ResultCallback을 상속받아 표준 출력과 표준 에러를 분리하여 처리합니다.
     * 
     * 주요 기능:
     * - 실시간 출력 수집: 명령어 실행 중 출력을 즉시 수집
     * - 스트림 분리: stdout과 stderr를 별도로 처리
     * - 메모리 효율성: ByteArrayOutputStream을 사용하여 메모리 내에서 처리
     * 
     * 사용 시나리오:
     * - 코드 실행 시 실시간 출력 수집
     * - 컴파일 과정에서 오류 메시지 수집
     * - 대용량 출력 처리 (파일 크기 제한 적용)
     * 
     * 스레드 안전성:
     * - Docker Java API에서 제공하는 콜백 메커니즘 사용
     * - 각 실행마다 새로운 인스턴스 생성
     */
    private static class ExecResultCallback extends ResultCallback.Adapter<Frame> {
        private final ByteArrayOutputStream stdout;  // 표준 출력 수집용
        private final ByteArrayOutputStream stderr;  // 표준 에러 수집용

        /**
         * 실행 결과 콜백 생성자
         * 
         * @param stdout 표준 출력을 저장할 ByteArrayOutputStream
         * @param stderr 표준 에러를 저장할 ByteArrayOutputStream
         */
        public ExecResultCallback(ByteArrayOutputStream stdout, ByteArrayOutputStream stderr) {
            this.stdout = stdout;
            this.stderr = stderr;
        }

        /**
         * Docker에서 전송되는 각 프레임을 처리하는 메서드
         * 
         * 이 메서드는 Docker 컨테이너에서 실행되는 명령어의 출력을
         * 스트림 타입에 따라 적절한 출력 스트림에 저장합니다.
         * 
         * @param frame Docker에서 전송되는 출력 프레임
         */
        @Override
        public void onNext(Frame frame) {
            try {
                switch (frame.getStreamType()) {
                    case STDOUT:
                        // 표준 출력을 stdout 스트림에 저장
                        stdout.write(frame.getPayload());
                        break;
                    case STDERR:
                        // 표준 에러를 stderr 스트림에 저장
                        stderr.write(frame.getPayload());
                        break;
                    default:
                        // 기타 스트림 타입은 무시
                        break;
                }
            } catch (IOException e) {
                log.warn("프레임 데이터 쓰기 오류", e);
            }
        }
    }
    
    /**
     * 컨테이너 풀 통계 정보 조회 메서드
     * 
     * 이 메서드는 현재 컨테이너 풀의 상태를 모니터링하기 위한 통계 정보를 반환합니다.
     * 시스템 관리자나 모니터링 도구에서 풀의 효율성을 파악하는 데 사용됩니다.
     * 
     * 제공하는 통계 정보:
     * - 사용 가능한 컨테이너 수
     * - 현재 사용 중인 컨테이너 수
     * - 풀의 총 크기
     * - 평균 대기 시간
     * - 성공/실패 횟수
     * 
     * 사용 목적:
     * - 풀 크기 튜닝 및 최적화
     * - 시스템 부하 모니터링
     * - 성능 지표 수집
     * - Health Check 엔드포인트에서 활용
     * 
     * @return PoolStatistics 컨테이너 풀의 통계 정보 객체
     */
    public ContainerPool.PoolStatistics getPoolStatistics() {
        return containerPool.getStatistics();
    }
}
