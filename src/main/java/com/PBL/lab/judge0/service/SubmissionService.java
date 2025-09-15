package com.PBL.lab.judge0.service;

import com.PBL.lab.judge0.dto.SubmissionRequest;
import com.PBL.lab.judge0.entity.Language;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.enums.Status;
import com.PBL.lab.judge0.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Submission Service - 제출 데이터 관리 비즈니스 로직 서비스
 *
 * 목적:
 * - 코드 제출(Submission)과 관련된 모든 비즈니스 로직을 처리
 * - CRUD, 제출 생명주기, 검증/제약, 기본값 주입 등 일관성 있는 도메인 규칙을 적용
 * - 대량 제출/큐 처리/통계 집계를 통한 운영 관점 기능 제공
 *
 * 핵심 기능:
 * - createSubmission(): 새로운 제출 생성 및 유효성 검증
 * - findByToken(): 토큰으로 제출 조회 (캐시 적용)
 * - updateStatus()/updateResult(): 실행 상태/결과 업데이트 및 타임스탬프 관리
 * - findAll(): 페이지네이션/정렬 기반 제출 목록 조회
 * - deleteSubmission(): 설정에 따른 삭제 허용 및 상태 제약 검사
 *
 * 데이터 무결성:
 * - 토큰(UUID) 중복 방지 (DB 조회 기반의 선행 검사)
 * - 언어 존재/아카이브 상태 검증
 * - 프로젝트 형태(파일 번들) vs 단일 소스코드 제출 구분 및 상호 배타적 필드 검사
 * - 상태 전이(terminal 여부)에 따른 FinishedAt 관리 규칙
 *
 * 설정/보안 제약:
 * - ConfigService를 통한 CPU/메모리/타임리밋 등 실행 한도 기본값 주입
 * - 컴파일러 옵션/명령행 인자/콜백/추가파일/네트워크 사용 허용 여부 점검
 * - 삭제 허용 플래그 및 완료된 제출만 삭제 가능
 *
 * 성능/운영:
 * - @Cacheable 로 자주 조회되는 단건 제출 캐시
 * - @Transactional 로 일관된 트랜잭션 경계 설정
 * - 큐 상태 조회/집계, 상태별 카운트 통계 제공
 *
 * 트랜잭션/캐시 주의:
 * - 읽기 전용 메서드는 readOnly=true 로 지정 (스냅샷/플러시 최소화)
 * - 캐시 키는 token 기반이며 null 결과는 캐시하지 않음(unless=#result == null)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final LanguageService languageService;
    private final Base64Service base64Service;
    private final ConfigService configService;

    /**
     * 새로운 제출을 생성합니다.
     *
     * 처리 순서:
     * 1) 언어 존재/아카이브 검증
     * 2) 프로젝트 제출 여부에 따라 입력 필드 상호배타성 검사
     * 3) 실행 파라미터 기본값 주입 (ConfigService)
     * 4) 보안/설정 제약(validateSubmission) 검사
     * 5) 초기 상태(Status.QUEUE) 및 큐잉 시간 설정
     * 6) 저장 후 엔터티 반환
     *
     * @param request 제출 생성 요청 DTO
     * @return 저장된 Submission 엔터티
     * @throws IllegalArgumentException 잘못된 언어 ID, 상호배타성 위반, 제약 위반 시
     */
    public Submission createSubmission(SubmissionRequest request) {
        log.debug("Creating submission for language ID: {}", request.getLanguageId());

        // 1) 언어 검증: 존재 여부 + 아카이브(사용 불가) 여부
        Language language = languageService.findById(request.getLanguageId());
        if (language == null) {
            throw new IllegalArgumentException("Language with id " + request.getLanguageId() + " doesn't exist");
        }
        if (Boolean.TRUE.equals(language.getIsArchived())) {
            throw new IllegalArgumentException("Language with id " + request.getLanguageId() + " is archived and cannot be used anymore");
        }

        // 2) Submission 엔터티 생성 및 토큰 부여
        Submission submission = new Submission();
        submission.setToken(generateToken()); // UUID 기반, 중복 시 재시도
        submission.setLanguageId(request.getLanguageId());
        submission.setLanguage(language);

        // 3) 소스/추가파일 처리: 프로젝트형 vs 비프로젝트형 상호배타성 보장
        if (language.isProject()) {
            // 프로젝트형은 추가파일(바이너리 번들) 필수, 소스코드 입력 금지
            if (request.getSourceCode() != null && !request.getSourceCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Source code should not be provided for project submissions");
            }
            if (request.getAdditionalFiles() == null || request.getAdditionalFiles().trim().isEmpty()) {
                throw new IllegalArgumentException("Additional files are required for project submissions");
            }
            // Base64 디코딩 후 raw 바이트 저장
            submission.setAdditionalFiles(base64Service.decodeToBytes(request.getAdditionalFiles()));
        } else {
            // 단일 소스코드 제출: sourceCode 필수, 추가파일은 선택
            if (request.getSourceCode() == null || request.getSourceCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Source code is required for non-project submissions");
            }
            submission.setSourceCode(request.getSourceCode());
            if (request.getAdditionalFiles() != null && !request.getAdditionalFiles().trim().isEmpty()) {
                submission.setAdditionalFiles(base64Service.decodeToBytes(request.getAdditionalFiles()));
            }
        }

        // 4) 표준입력/기대출력 설정
        submission.setStdin(request.getStdin());
        submission.setExpectedOutput(request.getExpectedOutput());

        // 5) 실행 리밋 파라미터: null 이면 ConfigService 기본값 주입
        submission.setNumberOfRuns(getValueOrDefault(request.getNumberOfRuns(), configService.getNumberOfRuns()));
        submission.setCpuTimeLimit(getValueOrDefault(request.getCpuTimeLimit(), configService.getCpuTimeLimit()));
        submission.setCpuExtraTime(getValueOrDefault(request.getCpuExtraTime(), configService.getCpuExtraTime()));
        submission.setWallTimeLimit(getValueOrDefault(request.getWallTimeLimit(), configService.getWallTimeLimit()));
        submission.setMemoryLimit(getValueOrDefault(request.getMemoryLimit(), configService.getMemoryLimit()));
        submission.setStackLimit(getValueOrDefault(request.getStackLimit(), configService.getStackLimit()));
        submission.setMaxProcessesAndOrThreads(getValueOrDefault(request.getMaxProcessesAndOrThreads(), configService.getMaxProcessesAndOrThreads()));
        submission.setMaxFileSize(getValueOrDefault(request.getMaxFileSize(), configService.getMaxFileSize()));

        // 6) 불리언 플래그 기본값 주입
        submission.setEnablePerProcessAndThreadTimeLimit(
                getValueOrDefault(request.getEnablePerProcessAndThreadTimeLimit(), configService.getEnablePerProcessAndThreadTimeLimit()));
        submission.setEnablePerProcessAndThreadMemoryLimit(
                getValueOrDefault(request.getEnablePerProcessAndThreadMemoryLimit(), configService.getEnablePerProcessAndThreadMemoryLimit()));
        submission.setRedirectStderrToStdout(
                getValueOrDefault(request.getRedirectStderrToStdout(), configService.getRedirectStderrToStdout()));
        submission.setEnableNetwork(
                getValueOrDefault(request.getEnableNetwork(), configService.getEnableNetwork()));

        // 7) 컴파일/실행 옵션 및 콜백 URL 설정 (제약은 validateSubmission 에서 검사)
        submission.setCompilerOptions(request.getCompilerOptions());
        submission.setCommandLineArguments(request.getCommandLineArguments());
        submission.setCallbackUrl(request.getCallbackUrl());

        // 8) 설정/보안 제약 일괄 검증
        validateSubmission(submission);

        // 9) 초기 상태 지정: 큐 진입 + 큐잉 시간 기록
        submission.setStatus(Status.QUEUE);
        submission.setQueuedAt(LocalDateTime.now());

        // 10) 영속화
        submission = submissionRepository.save(submission);
        log.info("Created submission with token: {}", submission.getToken());

        return submission;
    }

    /**
     * 토큰으로 단일 제출 조회 (캐시 사용).
     *
     * 캐시 전략:
     * - 캐시 이름 "submissions"
     * - 키는 token
     * - 결과가 null 인 경우는 캐시하지 않음
     *
     * @param token 제출 고유 토큰(UUID 문자열)
     * @return Submission 엔터티
     * @throws IllegalArgumentException 토큰 미존재 시
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "submissions", key = "#token", unless = "#result == null")
    public Submission findByToken(String token) {
        return submissionRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Submission with token " + token + " not found"));
    }

    /**
     * 여러 토큰으로 제출을 일괄 조회합니다.
     * 결과 맵의 키는 token, 값은 Submission 입니다.
     * 주의: 반환 맵의 순서는 토큰 리스트 순서와 일치하지 않을 수 있습니다.
     *
     * @param tokens 토큰 목록
     * @return token -> Submission 매핑
     */
    @Transactional(readOnly = true)
    public Map<String, Submission> findByTokens(List<String> tokens) {
        List<Submission> submissions = submissionRepository.findByTokenIn(tokens);
        Map<String, Submission> result = new HashMap<>();
        for (Submission submission : submissions) {
            result.put(submission.getToken(), submission);
        }
        return result;
    }

    /**
     * 페이지네이션/정렬 조건으로 제출 목록을 조회합니다.
     * 정렬은 Repository 쿼리(findAllOrderedByCreatedAt) 규칙을 따릅니다.
     *
     * @param pageable 페이지/정렬 정보
     * @return 페이지 결과
     */
    @Transactional(readOnly = true)
    public Page<Submission> findAll(Pageable pageable) {
        return submissionRepository.findAllOrderedByCreatedAt(pageable);
    }

    /**
     * 제출 상태를 업데이트합니다.
     * - 상태가 terminal(종결)인 경우 finishedAt 타임스탬프를 기록합니다.
     *
     * @param token 제출 토큰
     * @param status 변경할 상태
     */
    public void updateStatus(String token, Status status) {
        Submission submission = findByToken(token);
        submission.setStatus(status);
        if (status.isTerminal()) {
            // 종결 상태로 전환 시 완료 시각 기록
            submission.setFinishedAt(LocalDateTime.now());
        }
        submissionRepository.save(submission);
        log.debug("Updated submission {} status to {}", token, status.getName());
    }

    /**
     * 실행 결과를 제출에 반영합니다.
     * - 표준출력/표준에러/컴파일출력/시간/메모리/종료코드/신호/메시지/상태를 복사
     * - finishedAt 를 현재 시각으로 설정
     *
     * @param token 제출 토큰
     * @param result 실행 결과(샌드박스/도커 런타임에서 수집된 결과)
     */
    public void updateResult(String token, ExecutionResult result) {
        Submission submission = findByToken(token);

        // 실행 결과 필드 매핑
        submission.setStdout(result.getStdout());
        submission.setStderr(result.getStderr());
        submission.setCompileOutput(result.getCompileOutput());
        submission.setTime(result.getTime());
        submission.setWallTime(result.getWallTime());
        submission.setMemory(result.getMemory());
        submission.setExitCode(result.getExitCode());
        submission.setExitSignal(result.getExitSignal());
        submission.setMessage(result.getMessage());
        submission.setStatus(result.getStatus());
        submission.setFinishedAt(LocalDateTime.now());

        submissionRepository.save(submission);
        log.info("Updated submission {} with execution result: {}", token, result.getStatus().getName());
    }

    /**
     * 제출을 삭제합니다.
     * - ConfigService.isSubmissionDeleteEnabled() 가 true 인 경우에만 허용
     * - terminal(종결) 상태가 아닌 제출은 삭제 불가 (운영상 안전장치)
     *
     * @param token 제출 토큰
     * @throws IllegalStateException 삭제 비허용 또는 비종결 상태일 때
     */
    public void deleteSubmission(String token) {
        if (!configService.isSubmissionDeleteEnabled()) {
            throw new IllegalStateException("Submission deletion is not allowed");
        }

        Submission submission = findByToken(token);
        if (!submission.getStatus().isTerminal()) {
            throw new IllegalStateException(
                    "Submission cannot be deleted because its status is " + submission.getStatus().getId() +
                            " (" + submission.getStatus().getName() + ")");
        }

        submissionRepository.delete(submission);
        log.info("Deleted submission: {}", token);
    }

    /**
     * 큐에 대기 중인 제출 목록을 반환합니다.
     * Repository 단에서 Status.QUEUE 기준으로 조회하도록 구현되어 있어야 합니다.
     *
     * @return QUEUE 상태 제출 리스트
     */
    @Transactional(readOnly = true)
    public List<Submission> getSubmissionsInQueue() {
        return submissionRepository.findSubmissionsInQueue();
    }

    /**
     * 큐에 대기 중인 제출 개수를 반환합니다.
     *
     * @return QUEUE 상태 개수
     */
    @Transactional(readOnly = true)
    public long countSubmissionsInQueue() {
        return submissionRepository.countSubmissionsInQueue();
    }

    /**
     * 상태별 제출 통계를 반환합니다.
     * - total, in_queue, processing, accepted, wrong_answer, time_limit_exceeded, compilation_error 등
     * - 일부 범주는 구간 집계(countByStatusIdBetween)로 런타임/내부 오류군을 묶어서 계산
     *
     * @return 통계 맵 (키: 문자열, 값: Object/Number)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSubmissionStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 기본 카운트
        result.put("total", submissionRepository.count());
        result.put("in_queue", submissionRepository.countByStatusId(Status.QUEUE.getId()));
        result.put("processing", submissionRepository.countByStatusId(Status.PROCESS.getId()));
        result.put("accepted", submissionRepository.countByStatusId(Status.AC.getId()));
        result.put("wrong_answer", submissionRepository.countByStatusId(Status.WA.getId()));
        result.put("time_limit_exceeded", submissionRepository.countByStatusId(Status.TLE.getId()));
        result.put("compilation_error", submissionRepository.countByStatusId(Status.CE.getId()));
        result.put("runtime_errors",
                submissionRepository.countByStatusIdBetween(Status.SIGSEGV.getId(), Status.OTHER.getId()));
        result.put("internal_errors",
                submissionRepository.countByStatusIdBetween(Status.BOXERR.getId(), Status.EXEERR.getId()));

        return result;
    }

    /**
     * 고유 제출 토큰(UUID 문자열)을 생성합니다.
     * - 충돌을 피하기 위해 존재 여부를 DB에서 검사하며, 희박하지만 충돌 시 재시도합니다.
     * - 대량 생성 트래픽에서 성능/안정성을 위해 DB에 고유 인덱스(UNIQUE) 설정을 권장합니다.
     *
     * @return 고유 토큰
     */
    private String generateToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (submissionRepository.existsByToken(token));
        return token;
    }

    /**
     * 제출 엔터티의 제약을 일괄 검증합니다.
     * - 컴파일러 옵션: 전역 허용 플래그 + 컴파일형 언어 제한 + 허용 언어 화이트리스트
     * - 명령행 인자: 전역 허용 플래그
     * - 콜백 URL: 전역 허용 플래그
     * - 추가파일: 전역 허용 플래그
     * - 네트워크: 전역 허용 플래그
     *
     * 보안 관점:
     * - 임의 명령/옵션 주입을 제한하여 샌드박스 탈출/자원 남용 위험을 최소화합니다.
     *
     * @param submission 검증 대상 제출
     * @throws IllegalArgumentException 제약 위반 시
     */
    private void validateSubmission(Submission submission) {
        // 1) 컴파일러 옵션 검증
        if (submission.getCompilerOptions() != null && !submission.getCompilerOptions().trim().isEmpty()) {
            if (!configService.isCompilerOptionsEnabled()) {
                throw new IllegalArgumentException("Setting compiler options is not allowed");
            }

            // 컴파일 명령이 없는 인터프리터형 언어에는 옵션 금지
            if (submission.getLanguage().getCompileCmd() == null) {
                throw new IllegalArgumentException("Setting compiler options is only allowed for compiled languages");
            }

            // 허용 언어 프리픽스 화이트리스트가 존재하면 언어명 startsWith 로 판별
            List<String> allowedLanguages = configService.getAllowedLanguagesForCompilerOptions();
            if (!allowedLanguages.isEmpty() &&
                    allowedLanguages.stream().noneMatch(lang -> submission.getLanguage().getName().startsWith(lang))) {
                throw new IllegalArgumentException("Setting compiler options is not allowed for " + submission.getLanguage().getName());
            }
        }

        // 2) 명령행 인자 허용 여부
        if (submission.getCommandLineArguments() != null && !submission.getCommandLineArguments().trim().isEmpty()) {
            if (!configService.isCommandLineArgumentsEnabled()) {
                throw new IllegalArgumentException("Setting command line arguments is not allowed");
            }
        }

        // 3) 콜백 URL 허용 여부
        if (submission.getCallbackUrl() != null && !submission.getCallbackUrl().trim().isEmpty()) {
            if (!configService.isCallbacksEnabled()) {
                throw new IllegalArgumentException("Setting callback is not allowed");
            }
        }

        // 4) 추가파일 허용 여부
        if (submission.hasAdditionalFiles()) {
            if (!configService.isAdditionalFilesEnabled()) {
                throw new IllegalArgumentException("Setting additional files is not allowed");
            }
        }

        // 5) 네트워크 사용 허용 여부
        if (Boolean.TRUE.equals(submission.getEnableNetwork())) {
            if (!configService.isNetworkAllowed()) {
                throw new IllegalArgumentException("Enabling network is not allowed");
            }
        }
    }

    /**
     * null 이면 기본값, 아니면 원래 값을 반환하는 헬퍼.
     * 제네릭으로 다양한 타입(T)을 지원합니다.
     *
     * @param value        요청값(Null 가능)
     * @param defaultValue 기본값
     * @param <T>          타입 파라미터
     * @return value != null ? value : defaultValue
     */
    private <T> T getValueOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}