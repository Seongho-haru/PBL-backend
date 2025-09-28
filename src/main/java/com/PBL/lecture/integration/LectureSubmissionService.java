package com.PBL.lecture.integration;

import com.PBL.lab.judge0.dto.SubmissionRequest;
import com.PBL.lab.judge0.dto.SubmissionResponse;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.service.SubmissionService;
import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 강의-제출 통합 서비스
 * 강의 모듈과 Judge0 제출 시스템을 연동하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LectureSubmissionService {

    private final LectureService lectureService;
    private final SubmissionService submissionService;

    /**
     * 강의 문제에 대한 코드 제출
     * 
     * @param lectureId 강의 ID
     * @param sourceCode 제출할 소스코드
     * @param languageId 프로그래밍 언어 ID
     * @return 제출 응답
     */
    @Transactional
    public SubmissionResponse submitCodeForLecture(Long lectureId, String sourceCode, Integer languageId) {
        // 강의 조회 및 검증
        Optional<Lecture> lectureOpt = lectureService.getLectureWithTestCases(lectureId);
        if (lectureOpt.isEmpty()) {
            throw new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId);
        }

        Lecture lecture = lectureOpt.get();
        if (!lecture.isProblemType()) {
            throw new IllegalArgumentException("문제 타입 강의에만 코드를 제출할 수 있습니다.");
        }

        if (lecture.getTestCases().isEmpty()) {
            throw new IllegalArgumentException("테스트케이스가 없는 문제입니다.");
        }

        // Judge0 제출 요청 생성
        SubmissionRequest request = new SubmissionRequest();
        request.setSourceCode(sourceCode);
        request.setLanguageId(languageId);
        
        // 강의의 제한사항 적용
        if (lecture.getTimeLimit() != null) {
            request.setCpuTimeLimit(BigDecimal.valueOf(lecture.getTimeLimit()));
        }
        if (lecture.getMemoryLimit() != null) {
            request.setMemoryLimit(lecture.getMemoryLimit() * 1024); // MB to KB 변환
        }

        // 첫 번째 테스트케이스로 제출 (단일 제출)
        var firstTestCase = lecture.getTestCases().get(0);
        request.setStdin(firstTestCase.getInput());
        request.setExpectedOutput(firstTestCase.getExpectedOutput());

        log.info("강의 {} 문제에 대한 코드 제출 - 언어: {}, 시간제한: {}초, 메모리제한: {}MB", 
                lectureId, languageId, lecture.getTimeLimit(), lecture.getMemoryLimit());

        // Submission을 SubmissionResponse로 변환
        Submission submission = submissionService.createSubmission(request);
        return SubmissionResponse.from(submission);
    }

    /**
     * 강의 문제에 대한 배치 제출 (모든 테스트케이스)
     * 
     * @param lectureId 강의 ID
     * @param sourceCode 제출할 소스코드
     * @param languageId 프로그래밍 언어 ID
     * @return 제출 응답 리스트
     */
    @Transactional
    public List<SubmissionResponse> submitCodeForAllTestCases(Long lectureId, String sourceCode, Integer languageId) {
        // 강의 조회 및 검증
        Optional<Lecture> lectureOpt = lectureService.getLectureWithTestCases(lectureId);
        if (lectureOpt.isEmpty()) {
            throw new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId);
        }

        Lecture lecture = lectureOpt.get();
        if (!lecture.isProblemType()) {
            throw new IllegalArgumentException("문제 타입 강의에만 코드를 제출할 수 있습니다.");
        }

        if (lecture.getTestCases().isEmpty()) {
            throw new IllegalArgumentException("테스트케이스가 없는 문제입니다.");
        }

        log.info("강의 {} 문제에 대한 배치 제출 시작 - 테스트케이스 {}개", lectureId, lecture.getTestCases().size());

        // 각 테스트케이스에 대해 제출
        return lecture.getTestCases().stream()
                .map(testCase -> {
                    SubmissionRequest request = new SubmissionRequest();
                    request.setSourceCode(sourceCode);
                    request.setLanguageId(languageId);
                    request.setStdin(testCase.getInput());
                    request.setExpectedOutput(testCase.getExpectedOutput());
                    
                    // 강의의 제한사항 적용
                    if (lecture.getTimeLimit() != null) {
                        request.setCpuTimeLimit(BigDecimal.valueOf(lecture.getTimeLimit()));
                    }
                    if (lecture.getMemoryLimit() != null) {
                        request.setMemoryLimit(lecture.getMemoryLimit() * 1024); // MB to KB 변환
                    }

                    Submission submission = submissionService.createSubmission(request);
                    return SubmissionResponse.from(submission);
                })
                .toList();
    }

    /**
     * 강의의 기본 제한사항 조회
     * 
     * @param lectureId 강의 ID
     * @return 제한사항 정보
     */
    public LectureLimits getLectureLimits(Long lectureId) {
        Optional<Lecture> lectureOpt = lectureService.getLecture(lectureId);
        if (lectureOpt.isEmpty()) {
            throw new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId);
        }

        Lecture lecture = lectureOpt.get();
        return new LectureLimits(
                lecture.getTimeLimit(),
                lecture.getMemoryLimit(),
                lecture.getTestCaseCount()
        );
    }


    /**
     * 강의 제한사항 정보 DTO
     */
    public record LectureLimits(
            Integer timeLimit,
            Integer memoryLimit,
            int testCaseCount
    ) {}
}
