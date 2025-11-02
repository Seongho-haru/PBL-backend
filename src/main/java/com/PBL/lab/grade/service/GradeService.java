package com.PBL.lab.grade.service;

import com.PBL.lab.core.config.SystemConfig;
import com.PBL.lab.core.config.FeatureFlagsConfig;
import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.entity.Constraints;
import com.PBL.lab.core.exception.AccessDeniedException;
import com.PBL.lab.grade.dto.GradeRequest;
import com.PBL.lab.grade.entity.Grade;
import com.PBL.lab.core.enums.Status;
import com.PBL.lab.grade.repository.GradeRepository;
import com.PBL.lab.core.repository.ConstraintsRepository;
import com.PBL.lab.core.service.LanguageService;
import com.PBL.lab.core.dto.ExecutionResult;
import com.PBL.lecture.LectureService;
import com.PBL.lecture.entity.TestCase;
import com.PBL.lecture.repository.TestCaseRepository;
import com.PBL.user.User;
import com.PBL.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradeService {
    private final GradeRepository gradeRepository;
    private final TestCaseRepository testCaseRepository;
    private final LanguageService languageService;
    private final ConstraintsRepository constraintsRepository;
    private final FeatureFlagsConfig featureFlagsConfig;
    private final SystemConfig systemConfig;
    private final LectureService lectureService;
    private final UserRepository userRepository;


    public Page<Grade> findAll(Pageable pageable) {
        return gradeRepository.findAllWithFetch(pageable);
    }

    public Page<Grade> findByProblemId(Long problemId, Pageable pageable) {
        return gradeRepository.findByProblemId(problemId, pageable);
    }

    public Grade findByToken(String token) {
        return gradeRepository.findByToken(token);
    }

    public void deleteGrade(String token) {
        Grade grade = findByToken(token);
        if (grade != null) {
            gradeRepository.delete(grade);
        }
    }

    /**
     * 대기열에 있는 채점 개수 조회
     * - 현재 큐에 대기 중인 채점들의 개수를 반환
     * - 시스템 부하 관리 및 큐 크기 제한에 사용
     */
    @Transactional(readOnly = true)
    public long countGradeInQueue() {
        return gradeRepository.countGradesInQueue();
    }

    /**
     * 처리 중인 채점 개수 조회
     * - 현재 실행 중인 채점들의 개수를 반환
     * - 시스템 리소스 모니터링에 사용
     */
    @Transactional(readOnly = true)
    public long countGradeInProcess() {
        return gradeRepository.countGradesInProcess();
    }

    @Transactional
    public Grade createGrade(GradeRequest request, Long userId) {

        // 1) 언어 검증: 존재 여부 + 아카이브(사용 불가) 여부
        Language language = languageService.findById(request.getLanguageId());
        if (language == null) {
            throw new IllegalArgumentException("Language with id " + request.getLanguageId() + " doesn't exist");
        }
        if (Boolean.TRUE.equals(language.getIsArchived())) {
            throw new IllegalArgumentException("Language with id " + request.getLanguageId() + " is archived and cannot be used anymore");
        }

        // 2) User 설정 (userId가 제공된 경우)
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " doesn't exist"));
        }

        Grade grade = Grade.builder()
                .sourceCode(request.getSourceCode())
                .language(language)
                .languageId(request.getLanguageId())
                .problemId(request.getProblemId())
                .user(user)
                .build();

        // 3) 소스/추가파일 처리: 프로젝트형 vs 비프로젝트형 상호배타성 보장
        // 단일 소스코드 제출: sourceCode 필수
        if (request.getSourceCode() == null || request.getSourceCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Source code is required for non-project submissions");
        }
        grade.setSourceCode(request.getSourceCode());


        // 4) 표준입력/기대출력 설정 (ExecutionInputOutput 엔티티 사용)
        // Grade는 채점용이므로 초기에는 빈 입출력 객체 생성

        // 5) 제약조건 처리: constraintsId 우선, 없으면 개별 값으로 새로 생성
        Constraints constraints = resolveConstraints(request);
        grade.setConstraints(constraints);

        // 8) 설정/보안 제약 일괄 검증
        validateGrade(grade);

        // 9) 초기 상태 지정: 큐 진입 + 큐잉 시간 기록
        grade.setStatus(Status.QUEUE);

        // 10) 영속화
        grade = gradeRepository.save(grade);
        log.info("Created grade with token: {}", grade.getToken());


        return grade;
    }

    public List<TestCase> findTestCasesByLectureId(Long lectureId) {
        return testCaseRepository.findByLectureId(lectureId);
    }

    private void validateGrade(Grade grade) {
        if (grade.getConstraints() == null) {
            throw new IllegalArgumentException("Grade constraints must be set");
        }

        // 4) 추가파일 허용 여부
        if (grade.hasAdditionalFiles()) {
            if (!featureFlagsConfig.isEnableAdditionalFiles()) {
                throw new IllegalArgumentException("Setting additional files is not allowed");
            }
        }
    }

    private Constraints resolveConstraints(GradeRequest request) {
        return lectureService.getLecture(request.getProblemId()).orElseThrow(
                () -> new IllegalArgumentException("강의를 찾을 수 없습니다.")
        ).getConstraints();
    }

    public void updateStatus(String token, Status status) {
        Grade grade = findByToken(token);
        grade.setStatus(status);
        if (status.isTerminal()) {
            // 종결 상태로 전환 시 완료 시각 기록
            grade.setFinishedAt(LocalDateTime.now());
        }
        gradeRepository.save(grade);
        log.debug("Updated grade {} status to {}", token, status.getName());
    }

    /**
     * 채점 결과를 업데이트하는 메서드
     *
     * @param token 채점 토큰
     * @param result 실행 결과
     */
    public void updateResult(String token, ExecutionResult result) {
        Grade grade = findByToken(token);

        if (result.getErrorId() != null) {
            // TODO: Core의 ExecutionInputOutputRepository 사용하도록 리팩토링 필요
            // grade.setInputOutput(executionInputOutputRepository.findById(result.getErrorId()).orElse(null));
        }
        grade.setMessage(result.getMessage());

        // 실행 결과 정보 설정
        grade.setTime(result.getTime());
        grade.setWallTime(result.getWallTime());
        grade.setMemory(result.getMemory());
        grade.setExitCode(result.getExitCode());
        grade.setExitSignal(result.getExitSignal());
        grade.setFinishedAt(LocalDateTime.now());

        // 상태 설정
        grade.setStatus(result.getStatus());

        // 종결 상태인 경우 완료 시각 기록
        if (result.getStatus().isTerminal()) {
            grade.setFinishedAt(LocalDateTime.now());
        }

        gradeRepository.save(grade);
        log.debug("Updated grade {} result with status {}", token, result.getStatus().getName());
    }

    /**
     * 채점에 대한 사용자 접근 권한을 검증합니다.
     *
     * 접근 제어 규칙:
     * 1. grade.user == null (익명 제출) → 누구나 접근 가능
     * 2. grade.user != null && requestUserId == null → 접근 거부
     * 3. grade.user != null && requestUserId != grade.user.id → 접근 거부
     * 4. grade.user != null && requestUserId == grade.user.id → 접근 허용
     *
     * @param grade 접근 대상 채점
     * @param requestUserId 요청한 사용자 ID (헤더에서 전달, nullable)
     * @throws AccessDeniedException 접근 권한이 없을 때
     */
    public void validateAccess(Grade grade, Long requestUserId) {
        // 익명 제출은 누구나 접근 가능
        if (grade.getUser() == null) {
            return;
        }

        // 회원 제출인데 요청자가 인증되지 않음
        if (requestUserId == null) {
            throw new AccessDeniedException("This grade requires authentication");
        }

        // 회원 제출인데 다른 사용자가 접근 시도
        if (!grade.getUser().getId().equals(requestUserId)) {
            throw new AccessDeniedException("You don't have permission to access this grade");
        }
    }
}
