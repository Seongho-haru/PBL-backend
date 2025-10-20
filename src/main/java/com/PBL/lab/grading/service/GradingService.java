package com.PBL.lab.grading.service;

import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lab.grading.dto.GradingRequest;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.grading.repository.GradingRepository;
import com.PBL.lab.core.repository.ConstraintsRepository;
import com.PBL.lab.judge0.repository.SubmissionRepository;
import com.PBL.lab.core.service.ConfigService;
import com.PBL.lab.core.service.LanguageService;
import com.PBL.lab.judge0.service.SubmissionService;
import com.PBL.lab.core.service.ExecutionResult;
import com.PBL.lecture.LectureService;
import com.PBL.lecture.dto.LectureResponse;
import com.PBL.lecture.entity.Lecture;
import com.PBL.lecture.entity.TestCase;
import com.PBL.lecture.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradingService {
    private final GradingRepository gradingRepository;
    private final TestCaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private final LanguageService languageService;
    private final ConstraintsRepository constraintsRepository;
    private final ConfigService configService;
    private final LectureService lectureService;


    public Page<Grading> findAll(Pageable pageable) {
        return gradingRepository.findAllWithFetch(pageable);
    }

    public Page<Grading> findByProblemId(Long problemId, Pageable pageable) {
        return gradingRepository.findByProblemId(problemId, pageable);
    }

    public Grading findByToken(String token) {
        return gradingRepository.findByToken(token);
    }

    public void deleteGrading(String token) {
        Grading grading = findByToken(token);
        if (grading != null) {
            gradingRepository.delete(grading);
        }
    }

    /**
     * 큐에 대기 중인 제출 개수를 반환합니다.
     *
     * @return QUEUE 상태 개수
     */
    /**
     * 대기열에 있는 채점 개수 조회
     * - 현재 큐에 대기 중인 채점들의 개수를 반환
     * - 시스템 부하 관리 및 큐 크기 제한에 사용
     */
    @Transactional(readOnly = true)
    public long countGradingInQueue() {
        return gradingRepository.countGradingsInQueue();
    }

    /**
     * 처리 중인 채점 개수 조회
     * - 현재 실행 중인 채점들의 개수를 반환
     * - 시스템 리소스 모니터링에 사용
     */
    @Transactional(readOnly = true)
    public long countGradingInProcess() {
        return gradingRepository.countGradingsInProcess();
    }

    /**
     * 전체 실행 중인 작업 개수 조회 (Grading + Submission)
     * - Grading 큐 + Submission 큐의 총합
     * - 시스템 전체 부하 파악에 사용
     */
    @Transactional(readOnly = true)
    public long countAllExecutionsInQueue() {
        long gradingQueue = gradingRepository.countGradingsInQueue();
        long submissionQueue = submissionRepository.countSubmissionsInQueue();
        return gradingQueue + submissionQueue;
    }

    @Transactional
    public Grading createGrading(GradingRequest request) {

        // 1) 언어 검증: 존재 여부 + 아카이브(사용 불가) 여부
        Language language = languageService.findById(request.getLanguageId());
        if (language == null) {
            throw new IllegalArgumentException("Language with id " + request.getLanguageId() + " doesn't exist");
        }
        if (Boolean.TRUE.equals(language.getIsArchived())) {
            throw new IllegalArgumentException("Language with id " + request.getLanguageId() + " is archived and cannot be used anymore");
        }

        Grading grading = new Grading();
        grading.setToken(generateToken());
        grading.setSourceCode(request.getSourceCode());
        grading.setLanguageId(request.getLanguageId());
        grading.setLanguage(language);
        grading.setProblemId(request.getProblemId());

        // 3) 소스/추가파일 처리: 프로젝트형 vs 비프로젝트형 상호배타성 보장
        // 단일 소스코드 제출: sourceCode 필수
        if (request.getSourceCode() == null || request.getSourceCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Source code is required for non-project submissions");
        }
        grading.setSourceCode(request.getSourceCode());
    

        // 4) 표준입력/기대출력 설정 (SubmissionInputOutput 엔티티 사용)
        // Grading은 채점용이므로 초기에는 빈 입출력 객체 생성

        // 5) 제약조건 처리: constraintsId 우선, 없으면 개별 값으로 새로 생성
        Constraints constraints = resolveConstraints(request);
        grading.setConstraints(constraints);

        // 8) 설정/보안 제약 일괄 검증
        validateSubmission(grading);

        // 9) 초기 상태 지정: 큐 진입 + 큐잉 시간 기록
        grading.setStatus(Status.QUEUE);

        // 10) 영속화
        grading = gradingRepository.save(grading);
        log.info("Created submission with token: {}", grading.getToken());


        return grading;
    }

    public List<TestCase> findByProblemId(Long id) {
        return testCaseRepository.findByLectureId(id);
    }

    private String generateToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (submissionRepository.existsByToken(token));
        return token;
    }

    private void validateSubmission(Grading grading) {
        if (grading.getConstraints() == null) {
            throw new IllegalArgumentException("Submission constraints must be set");
        }

        // 1) 컴파일러 옵션 검증
        if (grading.getConstraints().getCompilerOptions() != null && !grading.getConstraints().getCompilerOptions().trim().isEmpty()) {
            if (!configService.isCompilerOptionsEnabled()) {
                throw new IllegalArgumentException("Setting compiler options is not allowed");
            }

            // 컴파일 명령이 없는 인터프리터형 언어에는 옵션 금지
            if (grading.getLanguage().getCompileCmd() == null) {
                throw new IllegalArgumentException("Setting compiler options is only allowed for compiled languages");
            }

            // 허용 언어 프리픽스 화이트리스트가 존재하면 언어명 startsWith 로 판별
            List<String> allowedLanguages = configService.getAllowedLanguagesForCompilerOptions();
            if (!allowedLanguages.isEmpty() &&
                    allowedLanguages.stream().noneMatch(lang -> grading.getLanguage().getName().startsWith(lang))) {
                throw new IllegalArgumentException("Setting compiler options is not allowed for " + grading.getLanguage().getName());
            }
        }

        // 2) 명령행 인자 허용 여부
        if (grading.getConstraints().getCommandLineArguments() != null && !grading.getConstraints().getCommandLineArguments().trim().isEmpty()) {
            if (!configService.isCommandLineArgumentsEnabled()) {
                throw new IllegalArgumentException("Setting command line arguments is not allowed");
            }
        }

        // 3) 콜백 URL 허용 여부
        if (grading.getConstraints().getCallbackUrl() != null && !grading.getConstraints().getCallbackUrl().trim().isEmpty()) {
            if (!configService.isCallbacksEnabled()) {
                throw new IllegalArgumentException("Setting callback is not allowed");
            }
        }

        // 4) 추가파일 허용 여부
        if (grading.hasAdditionalFiles()) {
            if (!configService.isAdditionalFilesEnabled()) {
                throw new IllegalArgumentException("Setting additional files is not allowed");
            }
        }

        // 5) 네트워크 사용 허용 여부
        if (Boolean.TRUE.equals(grading.getConstraints().getEnableNetwork())) {
            if (!configService.isNetworkAllowed()) {
                throw new IllegalArgumentException("Enabling network is not allowed");
            }
        }
    }

    private Constraints resolveConstraints(GradingRequest request) {
        return lectureService.getLecture(request.getProblemId()).orElseThrow(
                () -> new IllegalArgumentException("강의를 찾을 수 없습니다.")
        ).getConstraints();
    }

    public void updateStatus(String token, Status status) {
        Grading grading = findByToken(token);
        grading.setStatus(status);
        if (status.isTerminal()) {
            // 종결 상태로 전환 시 완료 시각 기록
            grading.setFinishedAt(LocalDateTime.now());
        }
        gradingRepository.save(grading);
        log.debug("Updated submission {} status to {}", token, status.getName());
    }

    /**
     * 채점 결과를 업데이트하는 메서드
     * 
     * @param token 채점 토큰
     * @param result 실행 결과
     */
    public void updateResult(String token, ExecutionResult result) {
        Grading grading = findByToken(token);
        
        if (result.getErrorId() != null) {
            grading.setInputOutput(submissionService.findById(result.getErrorId()));
        }
        grading.setMessage(result.getMessage());

        // 실행 결과 정보 설정
        grading.setTime(result.getTime());
        grading.setWallTime(result.getWallTime());
        grading.setMemory(result.getMemory());
        grading.setExitCode(result.getExitCode());
        grading.setExitSignal(result.getExitSignal());
        grading.setFinishedAt(LocalDateTime.now());

        // 상태 설정
        grading.setStatus(result.getStatus());
        
        // 종결 상태인 경우 완료 시각 기록
        if (result.getStatus().isTerminal()) {
            grading.setFinishedAt(LocalDateTime.now());
        }
        
        gradingRepository.save(grading);
        log.debug("Updated grading {} result with status {}", token, result.getStatus().getName());
    }
}
