package com.PBL.lab.core.service;

import com.PBL.lab.core.dto.CodeExecutionRequest;
import com.PBL.lab.core.dto.CompilationContext;
import com.PBL.lab.core.dto.ExecResult;
import com.PBL.lab.core.dto.ExecutionResult;
import com.github.dockerjava.api.DockerClient;
import com.PBL.lab.core.docker.ContainerManager;
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

    private final DockerClient dockerClient; // Docker API 클라이언트 (컨테이너 제어)
    private final ContainerManager containerManager; // 컨테이너 관리 서비스 (온디맨드 생성/삭제)

    @Value("${judge0.docker-execution.container-timeout:30000}")
    private long containerAcquireTimeout; // 컨테이너 획득 대기 시간 (밀리초)

    @Value("${judge0.docker-execution.cleanup-async:true}")
    private boolean asyncCleanup; // 비동기 정리 작업 사용 여부

    /**
     * 코드 컴파일 준비 (Grade 전용 - 1회 컴파일)
     *
     * 이 메서드는 Grade 시스템에서 여러 테스트케이스를 실행하기 위해
     * 한 번만 컴파일을 수행하고 컨텍스트를 반환합니다.
     *
     * 실행 과정:
     * 1. 작업 디렉토리 생성
     * 2. 언어별 Docker 이미지로 컨테이너 생성 및 시작
     * 3. 소스코드 파일 준비 (stdin 제외)
     * 4. 컨파일 (필요한 경우)
     * 5. CompilationContext 반환 (컨테이너는 유지)
     *
     * 주의:
     * - 컨테이너를 정리하지 않으므로 반드시 cleanupCompilation() 호출 필요
     * - Grade의 여러 테스트케이스 실행을 위해 설계됨
     *
     * @param request 실행할 코드와 설정 정보 (stdin 제외)
     * @return CompilationContext 컴파일된 컨텍스트 정보
     * @throws Exception 컴파일 오류 발생 시
     */
    public CompilationContext prepareCompilation(CodeExecutionRequest request) throws Exception {
        String containerId = null;
        Path workDir = null;
        long startTime = System.currentTimeMillis();

        try {
            Language language = request.getLanguage();
            log.debug("코드 컴파일 준비 시작 - 언어: {}, 이미지: {}", language.getName(), language.getEffectiveDockerImage());

            // 1. 작업 디렉토리 생성
            workDir = createWorkDirectory();

            // 2. 컨테이너 생성 및 시작 (언어별 이미지)
            containerId = containerManager.createExecutionContainer(language, workDir,request);
            containerManager.startContainer(containerId);

            long containerCreateTime = System.currentTimeMillis() - startTime;
            log.debug("컨테이너 생성 완료: {} ({}ms)", containerId, containerCreateTime);

            // 3. 소스코드 파일만 준비 (stdin은 나중에)
            prepareSourceCodeOnly(request, workDir);

            // 4. 컴파일 (필요시)
            String compileOutput = "";
            if (language.supportsCompilation()) {
                ExecResult compileResult =
                        containerManager.executeScript(containerId, "/tmp/judge/compile.sh", 30);

                compileOutput = compileResult.getStdout() + compileResult.getStderr();

                if (compileResult.hasError()) {
                    // 컴파일 실패 시 컨테이너 정리
                    try {
                        containerManager.stopContainer(containerId);
                        containerManager.removeContainer(containerId);
                        cleanupWorkDirectory(workDir);
                    } catch (Exception cleanupEx) {
                        log.error("컴파일 실패 후 정리 중 오류", cleanupEx);
                    }
                    throw new RuntimeException("Compilation failed: " + compileOutput);
                }
            }

            long compileTime = System.currentTimeMillis() - startTime;
            log.info("코드 컴파일 완료 - {}ms", compileTime);

            return CompilationContext.builder()
                    .containerId(containerId)
                    .workDir(workDir)
                    .compileOutput(compileOutput)
                    .language(language)
                    .compileTime(compileTime)
                    .build();

        } catch (Exception e) {
            log.error("코드 컴파일 준비 실패", e);
            // 오류 발생 시 생성된 리소스 정리
            if (containerId != null) {
                try {
                    containerManager.stopContainer(containerId);
                    containerManager.removeContainer(containerId);
                } catch (Exception cleanupEx) {
                    log.error("컨테이너 정리 실패", cleanupEx);
                }
            }
            if (workDir != null) {
                cleanupWorkDirectory(workDir);
            }
            throw e;
        }
    }

    /**
     * 컴파일된 코드를 특정 입력으로 실행 (Grade 전용 - N회 실행)
     *
     * prepareCompilation()으로 생성된 컨텍스트를 사용하여
     * 여러 테스트케이스를 빠르게 실행합니다.
     *
     * 실행 과정:
     * 1. stdin 파일 생성 (테스트케이스 입력)
     * 2. 실행 스크립트 생성
     * 3. 컨테이너에서 코드 실행
     * 4. 결과 수집 및 반환
     *
     * 주의:
     * - 컨테이너를 정리하지 않음 (계속 재사용)
     * - cleanupCompilation()으로 최종 정리 필요
     *
     * @param context 컴파일 컨텍스트
     * @param stdin 테스트케이스 입력
     * @param expectedOutput 예상 출력 (채점용)
     * @return ExecutionResult 실행 결과
     */
    public ExecutionResult executeWithCompiledCode(CompilationContext context, String stdin, String expectedOutput) {
        long startTime = System.currentTimeMillis();

        try {
            Language language = context.getLanguage();
            Path workDir = context.getWorkDir();
            String containerId = context.getContainerId();

            // 1. stdin 파일 생성
            String stdinContent = stdin != null ? stdin : "";
            Files.write(workDir.resolve("stdin.txt"), stdinContent.getBytes(StandardCharsets.UTF_8));

            // 2. 실행 스크립트 생성 (매번 생성 - stdin 리다이렉션 포함)
            createRunScriptForCompiled(language, workDir);

            // 3. 코드 실행
            ExecResult runResult =
                    containerManager.executeScript(containerId, "/tmp/judge/run.sh", 30);

            // 4. 결과 수집
            String stdoutContent = readFileContent(workDir.resolve("stdout.txt"));
            String stderrContent = readFileContent(workDir.resolve("stderr.txt"));
            String exitCodeContent = readFileContent(workDir.resolve("exit_code.txt"));

            Integer exitCode = runResult.getExitCode();
            if (exitCodeContent != null && !exitCodeContent.trim().isEmpty()) {
                try {
                    exitCode = Integer.parseInt(exitCodeContent.trim());
                } catch (NumberFormatException ignored) {}
            }

            // 5. 실행 시간 계산
            long totalTime = System.currentTimeMillis() - startTime;
            BigDecimal wallTime = BigDecimal.valueOf(totalTime / 1000.0);
            BigDecimal actualTime = calculateActualExecutionTime(workDir, wallTime);

            // 6. 상태 판정
            Status status = determineExecutionStatus(
                    runResult.isCompleted(),
                    exitCode,
                    expectedOutput,
                    stdoutContent
            );

            // 7. 메모리 사용량 (기본값)
            Integer memoryUsage = 2048;

            log.debug("코드 실행 완료 - {}ms, 상태: {}", totalTime, status);

            return ExecutionResult.builder()
                    .stdout(stdoutContent)
                    .stderr(stderrContent)
                    .compileOutput(context.getCompileOutput())
                    .time(actualTime)
                    .wallTime(wallTime)
                    .memory(memoryUsage)
                    .exitCode(exitCode)
                    .status(status)
                    .totalExecutionTime(totalTime)
                    .build();

        } catch (Exception e) {
            log.error("코드 실행 실패", e);
            return ExecutionResult.error("Execution error: " + e.getMessage());
        }
    }

    /**
     * 컴파일 컨텍스트 정리 (Grade 전용 - 1회 정리)
     *
     * prepareCompilation()으로 생성된 컨테이너와 작업 디렉토리를 정리합니다.
     *
     * @param context 정리할 컴파일 컨텍스트
     */
    public void cleanupCompilation(CompilationContext context) {
        if (context == null) {
            return;
        }

        // 컨테이너 정리
        if (context.getContainerId() != null) {
            try {
                containerManager.stopContainer(context.getContainerId());
                containerManager.removeContainer(context.getContainerId());
                log.debug("컨테이너 정리 완료: {}", context.getContainerId());
            } catch (Exception e) {
                log.error("컨테이너 정리 실패: {}", context.getContainerId(), e);
            }
        }

        // 작업 디렉토리 정리
        if (context.getWorkDir() != null) {
            cleanupWorkDirectory(context.getWorkDir());
        }
    }

    /**
     * 코드 실행 메인 메서드 (Judge0 Submission용 - 1회 실행)
     *
     * 기존 3개 함수를 순서대로 호출:
     * 1. prepareCompilation() - 컴파일
     * 2. executeWithCompiledCode() - 실행
     * 3. cleanupCompilation() - 정리
     *
     * @param request 실행할 코드와 설정 정보
     * @return ExecutionResult 실행 결과 (stdout, stderr, 실행 시간, 상태 등)
     */
    public ExecutionResult executeCode(CodeExecutionRequest request) {
        CompilationContext context = null;

        try {
            // 1. 컴파일
            context = prepareCompilation(request);

            // 2. 실행
            ExecutionResult result = executeWithCompiledCode(
                context,
                request.getStdin(),
                request.getExpectedOutput()
            );

            // 3. compileOutput과 containerAcquireTime 추가
            result.setCompileOutput(context.getCompileOutput());
            result.setContainerAcquireTime(context.getCompileTime());

            return result;

        } catch (Exception e) {
            log.error("코드 실행 실패", e);

            // 컴파일 실패는 CE 상태로 반환
            if (e.getMessage() != null && e.getMessage().contains("Compilation failed")) {
                return ExecutionResult.compilationError(e.getMessage());
            }

            return ExecutionResult.error("Execution error: " + e.getMessage());

        } finally {
            // 4. 정리
            if (context != null) {
                cleanupCompilation(context);
            }
        }
    }

    /**
     * 작업 디렉토리 생성
     *
     * 매 실행마다 고유한 임시 디렉토리를 생성하여 파일 격리를 보장합니다.
     *
     * @return Path 생성된 작업 디렉토리 경로
     * @throws IOException 디렉토리 생성 실패 시
     */
    private Path createWorkDirectory() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String dirName = "judge0-exec-" + UUID.randomUUID().toString().substring(0, 8);
        Path workDir = Paths.get(tmpDir, "judge0", dirName);
        Files.createDirectories(workDir);
        return workDir;
    }


    /**
     * 소스코드 파일만 준비하는 메서드 (Grade 전용)
     *
     * prepareCompilation()에서 사용되며, stdin은 나중에 각 테스트케이스마다 생성합니다.
     *
     * @param request 실행 요청 정보 (소스코드, 언어 등)
     * @param workDir 작업 디렉토리 경로
     * @throws IOException 파일 생성/쓰기 중 오류 발생 시
     */
    private void prepareSourceCodeOnly(CodeExecutionRequest request, Path workDir) throws IOException {
        Language language = request.getLanguage();

        // 이전 실행의 잔여 파일들을 정리하여 깨끗한 환경 준비
        cleanupWorkDirectory(workDir);

        // 소스코드 파일 생성
        if (request.getSourceCode() != null && !request.getSourceCode().trim().isEmpty()) {
            Path sourceFile = workDir.resolve(language.getSourceFile());
            Files.write(sourceFile, request.getSourceCode().getBytes(StandardCharsets.UTF_8));
            log.debug("소스코드 파일 생성 완료: {}", sourceFile.getFileName());
        }

        // 추가 파일들 처리 (프로젝트 제출 시)
        if (request.getAdditionalFiles() != null && request.getAdditionalFiles().length > 0) {
            log.debug("추가 파일들이 존재하지만 아직 처리되지 않음: {} bytes", request.getAdditionalFiles().length);
        }

        // 컴파일 스크립트 생성 (컴파일이 필요한 언어의 경우)
        createCompileScript(request, workDir);

        log.debug("소스코드 파일 준비 완료 - 작업 디렉토리: {}", workDir);
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
    private void createCompileScript(CodeExecutionRequest request, Path workDir) throws IOException {
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
                "set -e\n" + // 오류 발생 시 스크립트 중단
                "cd /tmp/judge\n" + // 작업 디렉토리로 이동
                compileCommand + "\n" + // 실제 컴파일 명령어
                "echo \"Compilation completed successfully\""; // 성공 메시지

        Files.write(compileScript, scriptContent.getBytes(StandardCharsets.UTF_8));
        compileScript.toFile().setExecutable(true); // 실행 권한 부여

        log.debug("컴파일 스크립트 생성 완료: {}", compileCommand);
    }


    /**
     * 컴파일된 코드용 실행 스크립트 생성 메서드 (Grade 전용)
     *
     * executeWithCompiledCode()에서 사용되며, 이미 컴파일된 바이너리를
     * 다른 입력으로 실행하기 위한 스크립트를 생성합니다.
     *
     * @param language 프로그래밍 언어 정보
     * @param workDir 작업 디렉토리 경로
     * @throws IOException 스크립트 파일 생성 중 오류 발생 시
     */
    private void createRunScriptForCompiled(Language language, Path workDir) throws IOException {
        String runCommand = language.getEffectiveRunCommand();

        // 실행 스크립트 파일 생성
        Path runScript = workDir.resolve("run.sh");
        String scriptContent = "#!/bin/bash\n" +
                "cd /tmp/judge\n" +
                "timeout 30s " + // 기본 30초 제한
                runCommand + " <stdin.txt >stdout.txt 2>stderr.txt\n" +
                "echo $? >exit_code.txt";

        Files.write(runScript, scriptContent.getBytes(StandardCharsets.UTF_8));
        runScript.toFile().setExecutable(true);

        log.debug("실행 스크립트 생성 완료 (컴파일된 코드용): {}", runCommand);
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

        // 화이트리스트 방식: 영문자, 숫자, 공백, 하이픈, 언더스코어, 점, 등호, 콤마만 허용
        // 명령어 주입 공격 방지 (개행, 서브쉘, 파이프, 리다이렉션 등 차단)
        if (!options.matches("^[a-zA-Z0-9\\s.\\-_=,:/]*$")) {
            log.warn("Invalid characters detected in compiler options: {}", options);
            throw new IllegalArgumentException("Compiler options contain invalid characters. Only alphanumeric, spaces, dots, hyphens, underscores, equals, commas, colons, and slashes are allowed.");
        }

        return options.trim();
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
                    .filter(path -> !path.equals(workDir)) // 루트 디렉토리는 제외
                    .sorted(Comparator.reverseOrder()) // 역순 정렬 (하위 파일부터 삭제)
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path); // 파일/디렉토리 삭제
                        } catch (IOException e) {
                            log.debug("파일 삭제 실패: {}", path);
                        }
                    });
            log.debug("작업 디렉토리 정리 완료: {}", workDir);
        } catch (IOException e) {
            log.warn("작업 디렉토리 정리 실패: {}", workDir, e);
        }
    }

}
