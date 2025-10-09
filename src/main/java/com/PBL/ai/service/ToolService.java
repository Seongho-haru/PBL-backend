package com.PBL.ai.service;

import com.PBL.curriculum.Curriculum;
import com.PBL.curriculum.CurriculumService;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.grading.service.GradingService;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.service.SubmissionService;
import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureService;
import com.PBL.lecture.LectureType;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI Assistant가 사용할 수 있는 도구 모음
 * LangChain4j의 @Tool 어노테이션을 통해 AI가 자동으로 호출할 수 있습니다.
 */
@Component
@AllArgsConstructor
public class ToolService {
    private final SubmissionService submissionService;
    private final GradingService gradingService;
    private final LectureService lectureService;
    private final CurriculumService curriculumService;

    // ========================================
    // 1. 코드 실행/채점 관련 도구
    // ========================================

    @Tool("제출 토큰으로 코드 실행 결과를 조회합니다. 실행 상태, 출력 결과, 에러 메시지, 실행 시간, 메모리 사용량 등을 확인할 수 있습니다.")
    public Submission getSubmission(@P("조회할 제출의 토큰") String submissionToken) {
        return submissionService.findByToken(submissionToken);
    }

    @Tool("채점 토큰으로 코드 채점 결과를 조회합니다. 테스트 케이스 통과 여부, 점수, 실패한 케이스 정보, 피드백 등을 확인할 수 있습니다.")
    public Grading getGrading(@P("조회할 채점의 토큰") String gradingToken) {
        return gradingService.findByToken(gradingToken);
    }

    // ========================================
    // 2. 강의 조회 도구
    // ========================================

    @Tool("강의 ID로 강의의 기본 정보를 조회합니다. 제목, 설명, 타입, 카테고리, 난이도 등을 확인할 수 있습니다.")
    public Lecture getLecture(@P("조회할 강의 ID") Long lectureId) {
        return lectureService.getLecture(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));
    }

    @Tool("강의 ID로 테스트케이스를 포함한 상세 정보를 조회합니다. 문제 타입 강의의 입출력 예제와 테스트 케이스를 확인할 수 있습니다.")
    public Lecture getLectureWithTestCases(@P("조회할 강의 ID") Long lectureId) {
        return lectureService.getLectureWithTestCases(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));
    }

    @Tool("강의 제목으로 검색합니다. 부분 일치로 검색되며, 대소문자를 구분하지 않습니다.")
    public List<Lecture> searchLecturesByTitle(@P("검색할 강의 제목 (부분 일치)") String title) {
        return lectureService.searchLecturesByTitle(title);
    }

    @Tool("카테고리별로 강의를 조회합니다. 예: '알고리즘', '자료구조', '웹개발', 'Python 기초' 등")
    public List<Lecture> getLecturesByCategory(@P("조회할 카테고리") String category) {
        return lectureService.getLecturesByCategory(category);
    }

    @Tool("강의 타입별로 조회합니다. 사용 가능한 타입: THEORY(이론), PRACTICE(실습), PROBLEM(문제)")
    public List<Lecture> getLecturesByType(@P("강의 타입: THEORY, PRACTICE, PROBLEM 중 하나") String type) {
        try {
            LectureType lectureType = LectureType.valueOf(type.toUpperCase());
            return lectureService.getLecturesByType(lectureType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바른 강의 타입을 입력하세요: THEORY, PRACTICE, PROBLEM");
        }
    }

    @Tool("최근에 생성된 강의 10개를 조회합니다. 새로운 강의를 추천할 때 유용합니다.")
    public List<Lecture> getRecentLectures() {
        return lectureService.getRecentLectures();
    }

    @Tool("공개된 모든 강의를 조회합니다. 학생들이 접근 가능한 강의 목록입니다.")
    public List<Lecture> getPublicLectures() {
        return lectureService.getPublicLectures();
    }

    @Tool("공개 강의를 여러 조건으로 검색합니다. 제목, 카테고리, 난이도, 타입으로 필터링할 수 있습니다. 검색하지 않을 조건은 null로 전달하세요.")
    public List<Lecture> searchPublicLectures(
            @P("검색할 제목 (선택)") String title,
            @P("카테고리 (선택)") String category,
            @P("난이도: EASY, MEDIUM, HARD 등 (선택)") String difficulty,
            @P("강의 타입: THEORY, PRACTICE, PROBLEM (선택)") String type) {
        LectureType lectureType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                lectureType = LectureType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 잘못된 타입이면 무시
            }
        }
        return lectureService.searchPublicLectures(title, category, difficulty, lectureType);
    }

    @Tool("테스트케이스가 있는 문제 강의만 조회합니다. 코딩 테스트 문제를 찾을 때 유용합니다.")
    public List<Lecture> getProblemLecturesWithTestCases() {
        return lectureService.getProblemLecturesWithTestCases();
    }

    // ========================================
    // 3. 커리큘럼 조회 도구
    // ========================================

    @Tool("커리큘럼 ID로 상세 정보를 조회합니다. 포함된 강의 목록과 순서를 확인할 수 있습니다.")
    public Curriculum getCurriculum(@P("조회할 커리큘럼 ID") Long curriculumId) {
        return curriculumService.getCurriculumById(curriculumId)
                .orElseThrow(() -> new IllegalArgumentException("커리큘럼을 찾을 수 없습니다: " + curriculumId));
    }

    @Tool("모든 커리큘럼을 조회합니다. 포함된 강의 목록도 함께 반환됩니다.")
    public List<Curriculum> getAllCurriculums() {
        return curriculumService.getAllCurriculums();
    }

    @Tool("공개된 커리큘럼만 조회합니다. 학생들이 접근 가능한 학습 과정입니다.")
    public List<Curriculum> getPublicCurriculums() {
        return curriculumService.getPublicCurriculums();
    }

    @Tool("커리큘럼 제목으로 검색합니다. 부분 일치로 검색되며, 대소문자를 구분하지 않습니다.")
    public List<Curriculum> searchCurriculums(@P("검색할 커리큘럼 제목 (부분 일치)") String title) {
        return curriculumService.searchCurriculums(title);
    }

    @Tool("공개 커리큘럼을 제목으로 검색합니다. 학생들이 수강 가능한 학습 과정을 찾을 때 사용합니다.")
    public List<Curriculum> searchPublicCurriculums(@P("검색할 커리큘럼 제목 (부분 일치)") String title) {
        return curriculumService.searchPublicCurriculums(title);
    }

    // ========================================
    // 4. 통계 및 분석 도구
    // ========================================

    @Tool("강의 타입별 통계를 조회합니다. 이론, 실습, 문제 강의가 각각 몇 개인지 확인할 수 있습니다.")
    public List<Object[]> getLectureStatsByType() {
        return lectureService.getLectureStatsByType();
    }

    @Tool("카테고리별 강의 통계를 조회합니다. 어떤 주제의 강의가 많은지 파악할 수 있습니다.")
    public List<Object[]> getCategoryStats() {
        return lectureService.getCategoryStats();
    }

    /*
     * TODO 추후 추가 예정 기능:
     *  1. 강의 생성 도구 (AI가 강의 콘텐츠 생성)
     *  2. 커리큘럼 생성 도구 (학습 경로 자동 구성)
     *  3. 인터넷 검색 도구 (최신 정보 보강)
     *  4. 사용자 맞춤 추천 (학습 이력 기반)
     */
}
