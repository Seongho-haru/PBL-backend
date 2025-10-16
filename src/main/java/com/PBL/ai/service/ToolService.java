package com.PBL.ai.service;

import com.PBL.curriculum.Curriculum;
import com.PBL.curriculum.CurriculumService;
import com.PBL.lab.grading.dto.GradingResponse;
import com.PBL.lab.grading.entity.Grading;
import com.PBL.lab.grading.service.GradingService;
import com.PBL.lab.judge0.dto.SubmissionResponse;
import com.PBL.lab.judge0.entity.Submission;
import com.PBL.lab.judge0.service.SubmissionService;
import com.PBL.lecture.Lecture;
import com.PBL.lecture.LectureService;
import com.PBL.lecture.LectureType;
import com.PBL.user.User;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI Assistant가 사용할 수 있는 도구 모음
 * LangChain4j의 @Tool 어노테이션을 통해 AI가 자동으로 호출할 수 있습니다.
 */
@Component
@AllArgsConstructor
@Slf4j
public class ToolService {
    private final SubmissionService submissionService;
    private final GradingService gradingService;
    private final LectureService lectureService;
    private final CurriculumService curriculumService;

    // ========================================
    // 1. 코드 실행/채점 관련 도구
    // ========================================

    @Tool("제출 토큰으로 코드 실행 결과를 조회합니다. 실행 상태, 출력 결과, 에러 메시지, 실행 시간, 메모리 사용량 등을 확인할 수 있습니다.")
    public Submission getSubmissionByToken(@P("조회할 제출의 토큰") String submissionToken) {
        log.debug("🔧 [도구 호출] getSubmission - 파라미터: submissionToken={}", submissionToken);
        Submission result = submissionService.findByToken(submissionToken);
        log.debug("✅ [도구 결과] getSubmission - 상태: {}", result != null ? result.getStatus() : "null");
        return result;
    }

    @Tool("코드 제출 목록을 페이징으로 조회합니다. 실행 결과와 상태를 확인할 수 있습니다.")
    public Page<Submission> getSubmission(
            @P("페이징 정보 (페이지 번호, 크기, 정렬 기준)")@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @P("코드를 Base64로 인코딩하여 반환할지 여부")boolean base64_encoded,
            @P("응답에 포함할 필드 목록 (쉼표로 구분)")String fields
            //, TODO: 향후 추가 예정 - 유저 ID로 특정 사용자의 제출만 조회
    ) {
        try{
            Page<Submission> submissionPage = null;
            /*
             TODO
                1. 유저 id로 특정 사용자의 제출을 페이징으로 조회
            if(userId != null) {
                submissionPage = submissionService.findByUserId(userId, pageable);
            } else {
                submissionPage = submissionService.findAll(pageable);
            }
             */
            submissionPage = submissionService.findAll(pageable);

            List<SubmissionResponse> submissions = submissionPage.getContent().stream()
                    .map(submission -> SubmissionResponse.from(submission, base64_encoded, parseFields(fields)))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("submissions", submissions);
            response.put("meta", createPaginationMeta(submissionPage));
            return submissionPage;
        }catch(Exception e){
            return null;
        }
    }


    @Tool("채점 토큰으로 코드 채점 결과를 조회합니다. 테스트 케이스 통과 여부, 점수, 실패한 케이스 정보, 피드백 등을 확인할 수 있습니다.")
    public Grading getGradingByToken(@P("조회할 채점의 토큰") String gradingToken) {
        log.debug("🔧 [도구 호출] getGrading - 파라미터: gradingToken={}", gradingToken);
        Grading result = gradingService.findByToken(gradingToken);
        log.debug("✅ [도구 결과] getGrading - 상태: {}", result != null ? result.getStatus() : "null");
        return result;
    }

    @Tool("코드 채점 목록을 페이징으로 조회합니다. 문제 ID로 필터링할 수 있으며, 테스트 케이스 통과 여부와 점수를 확인할 수 있습니다.")
    public Page<Grading> getGrading(
            @P("페이징 정보 (페이지 번호, 크기, 정렬 기준)")@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @P("특정 문제의 채점만 조회 (선택 사항)") Long problemId,
            @P("코드를 Base64로 인코딩하여 반환할지 여부")boolean base64_encoded,
            @P("응답에 포함할 필드 목록 (쉼표로 구분)")String fields
            //, TODO: 향후 추가 예정 - 유저 ID로 특정 사용자의 채점만 조회
    ){
        try {
            Page<Grading> gradingPage = null;
            if (problemId != null){
                gradingPage = gradingService.findByProblemId(problemId,pageable);
            }
            else{
                gradingPage = gradingService.findAll(pageable);
            }
            /*
             TODO
                1. 유저 id로 특정 사용자의 채점을 페이징으로 조회
            if(userId != null) {
                if(problemId != null) {
                    gradingPage = gradingService.findByUserIdAndProblemId(userId, problemId, pageable);
                } else {
                    gradingPage = gradingService.findByUserId(userId, pageable);
                }
            } else if (problemId != null) {
                gradingPage = gradingService.findByProblemId(problemId, pageable);
            } else {
                gradingPage = gradingService.findAll(pageable);
            }
             */
            List<GradingResponse> gradings = gradingPage.getContent().stream()
                    .map(grading -> GradingResponse.from(grading, base64_encoded, parseFields(fields)))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("grading", gradings);
            response.put("meta", createPaginationMeta(gradingPage));

            return gradingPage;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }

    // ========================================
    // 2. 강의 조회 도구
    // ========================================

    @Tool("강의 ID로 강의의 기본 정보를 조회합니다. 제목, 설명, 타입, 카테고리, 난이도 등을 확인할 수 있습니다.")
    public Lecture getLecture(@P("조회할 강의 ID") Long lectureId) {
        log.debug("🔧 [도구 호출] getLecture - 파라미터: lectureId={}", lectureId);
        Lecture result = lectureService.getLecture(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));
        log.debug("✅ [도구 결과] getLecture - 강의명: {}, 타입: {}", result.getTitle(), result.getType());
        return result;
    }

    @Tool("강의 ID로 테스트케이스를 포함한 상세 정보를 조회합니다. 문제 타입 강의의 입출력 예제와 테스트 케이스를 확인할 수 있습니다.")
    public Lecture getLectureWithTestCases(@P("조회할 강의 ID") Long lectureId) {
        log.debug("🔧 [도구 호출] getLectureWithTestCases - 파라미터: lectureId={}", lectureId);
        Lecture result = lectureService.getLectureWithTestCases(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));
        log.debug("✅ [도구 결과] getLectureWithTestCases - 테스트케이스 수: {}", 
            result.getTestCases() != null ? result.getTestCases().size() : 0);
        return result;
    }

    @Tool("강의 제목으로 검색합니다. 부분 일치로 검색되며, 대소문자를 구분하지 않습니다.")
    public List<Lecture> searchLecturesByTitle(@P("검색할 강의 제목 (부분 일치)") String title) {
        log.debug("🔧 [도구 호출] searchLecturesByTitle - 파라미터: title={}", title);
        List<Lecture> result = lectureService.searchLecturesByTitle(title);
        log.debug("✅ [도구 결과] searchLecturesByTitle - 검색 결과: {}개", result.size());
        return result;
    }

    @Tool("카테고리별로 강의를 조회합니다. 예: '알고리즘', '자료구조', '웹개발', 'Python 기초' 등")
    public List<Lecture> getLecturesByCategory(@P("조회할 카테고리") String category) {
        log.debug("🔧 [도구 호출] getLecturesByCategory - 파라미터: category={}", category);
        List<Lecture> result = lectureService.getLecturesByCategory(category);
        log.debug("✅ [도구 결과] getLecturesByCategory - 강의 수: {}개", result.size());
        return result;
    }

    @Tool("강의 타입별로 조회합니다. 사용 가능한 타입: THEORY(이론), PRACTICE(실습), PROBLEM(문제)")
    public List<Lecture> getLecturesByType(@P("강의 타입: THEORY, PRACTICE, PROBLEM 중 하나") String type) {
        log.debug("🔧 [도구 호출] getLecturesByType - 파라미터: type={}", type);
        try {
            LectureType lectureType = LectureType.valueOf(type.toUpperCase());
            List<Lecture> result = lectureService.getLecturesByType(lectureType);
            log.debug("✅ [도구 결과] getLecturesByType - 강의 수: {}개", result.size());
            return result;
        } catch (IllegalArgumentException e) {
            log.error("❌ [도구 오류] getLecturesByType - 잘못된 타입: {}", type);
            throw new IllegalArgumentException("올바른 강의 타입을 입력하세요: THEORY, PRACTICE, PROBLEM");
        }
    }

    @Tool("최근에 생성된 강의 10개를 조회합니다. 새로운 강의를 추천할 때 유용합니다.")
    public List<Lecture> getRecentLectures() {
        log.debug("🔧 [도구 호출] getRecentLectures");
        List<Lecture> result = lectureService.getRecentLectures();
        log.debug("✅ [도구 결과] getRecentLectures - 강의 수: {}개", result.size());
        return result;
    }

    @Tool("공개된 모든 강의를 조회합니다. 학생들이 접근 가능한 강의 목록입니다.")
    public List<Lecture> getPublicLectures() {
        log.debug("🔧 [도구 호출] getPublicLectures");
        List<Lecture> result = lectureService.getPublicLectures();
        log.debug("✅ [도구 결과] getPublicLectures - 강의 수: {}개", result.size());
        return result;
    }

    @Tool("공개 강의를 여러 조건으로 검색합니다. 제목, 카테고리, 난이도, 타입으로 필터링할 수 있습니다. 검색하지 않을 조건은 null로 전달하세요.")
    public List<Lecture> searchPublicLectures(
            @P("검색할 제목 (선택)") String title,
            @P("카테고리 (선택)") String category,
            @P("난이도: EASY, MEDIUM, HARD 등 (선택)") String difficulty,
            @P("강의 타입: THEORY, PRACTICE, PROBLEM (선택)") String type) {
        log.debug("🔧 [도구 호출] searchPublicLectures - 파라미터: title={}, category={}, difficulty={}, type={}", 
            title, category, difficulty, type);
        LectureType lectureType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                lectureType = LectureType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ [도구 경고] searchPublicLectures - 잘못된 타입 무시: {}", type);
            }
        }
        List<Lecture> result = lectureService.searchPublicLectures(title, category, difficulty, lectureType);
        log.debug("✅ [도구 결과] searchPublicLectures - 검색 결과: {}개", result.size());
        return result;
    }

    @Tool("테스트케이스가 있는 문제 강의만 조회합니다. 코딩 테스트 문제를 찾을 때 유용합니다.")
    public List<Lecture> getProblemLecturesWithTestCases() {
        log.debug("🔧 [도구 호출] getProblemLecturesWithTestCases");
        List<Lecture> result = lectureService.getProblemLecturesWithTestCases();
        log.debug("✅ [도구 결과] getProblemLecturesWithTestCases - 문제 수: {}개", result.size());
        return result;
    }

    // ========================================
    // 3. 커리큘럼 조회 도구
    // ========================================

    @Tool("커리큘럼 ID로 상세 정보를 조회합니다. 포함된 강의 목록과 순서를 확인할 수 있습니다.")
    public Curriculum getCurriculum(@P("조회할 커리큘럼 ID") Long curriculumId) {
        log.debug("🔧 [도구 호출] getCurriculum - 파라미터: curriculumId={}", curriculumId);
        Curriculum result = curriculumService.getCurriculumById(curriculumId)
                .orElseThrow(() -> new IllegalArgumentException("커리큘럼을 찾을 수 없습니다: " + curriculumId));
        log.debug("✅ [도구 결과] getCurriculum - 커리큘럼명: {}, 강의 수: {}", 
            result.getTitle(), result.getLectures() != null ? result.getLectures().size() : 0);
        return result;
    }

    @Tool("모든 커리큘럼을 조회합니다. 포함된 강의 목록도 함께 반환됩니다.")
    public List<Curriculum> getAllCurriculums() {
        log.debug("🔧 [도구 호출] getAllCurriculums");
        List<Curriculum> result = curriculumService.getAllCurriculums();
        log.debug("✅ [도구 결과] getAllCurriculums - 커리큘럼 수: {}개", result.size());
        return result;
    }

    @Tool("공개된 커리큘럼만 조회합니다. 학생들이 접근 가능한 학습 과정입니다.")
    public List<Curriculum> getPublicCurriculums() {
        log.debug("🔧 [도구 호출] getPublicCurriculums");
        List<Curriculum> result = curriculumService.getPublicCurriculums();
        log.debug("✅ [도구 결과] getPublicCurriculums - 커리큘럼 수: {}개", result.size());
        return result;
    }

    @Tool("커리큘럼 제목으로 검색합니다. 부분 일치로 검색되며, 대소문자를 구분하지 않습니다.")
    public List<Curriculum> searchCurriculums(@P("검색할 커리큘럼 제목 (부분 일치)") String title) {
        log.debug("🔧 [도구 호출] searchCurriculums - 파라미터: title={}", title);
        List<Curriculum> result = curriculumService.searchCurriculums(title);
        log.debug("✅ [도구 결과] searchCurriculums - 검색 결과: {}개", result.size());
        return result;
    }

    @Tool("공개 커리큘럼을 제목으로 검색합니다. 학생들이 수강 가능한 학습 과정을 찾을 때 사용합니다.")
    public List<Curriculum> searchPublicCurriculums(@P("검색할 커리큘럼 제목 (부분 일치)") String title) {
        log.debug("🔧 [도구 호출] searchPublicCurriculums - 파라미터: title={}", title);
        List<Curriculum> result = curriculumService.searchPublicCurriculums(title);
        log.debug("✅ [도구 결과] searchPublicCurriculums - 검색 결과: {}개", result.size());
        return result;
    }

    // ========================================
    // 4. 통계 및 분석 도구
    // ========================================

    @Tool("강의 타입별 통계를 조회합니다. 이론, 실습, 문제 강의가 각각 몇 개인지 확인할 수 있습니다.")
    public List<Object[]> getLectureStatsByType() {
        log.debug("🔧 [도구 호출] getLectureStatsByType");
        List<Object[]> result = lectureService.getLectureStatsByType();
        log.debug("✅ [도구 결과] getLectureStatsByType - 통계 항목: {}개", result.size());
        return result;
    }

    @Tool("카테고리별 강의 통계를 조회합니다. 어떤 주제의 강의가 많은지 파악할 수 있습니다.")
    public List<Object[]> getCategoryStats() {
        log.debug("🔧 [도구 호출] getCategoryStats");
        List<Object[]> result = lectureService.getCategoryStats();
        log.debug("✅ [도구 결과] getCategoryStats - 카테고리 수: {}개", result.size());
        return result;
    }

    /*
     * TODO 추후 추가 예정 기능:
     *  1. 강의 생성 도구 (AI가 강의 콘텐츠 생성)
     *  2. 커리큘럼 생성 도구 (학습 경로 자동 구성)
     *  3. 인터넷 검색 도구 (최신 정보 보강)
     *  4. 사용자 맞춤 추천 (학습 이력 기반)
     */

    /**
     * fields 파라미터 파싱
     */
    private String[] parseFields(String fields) {
        if (fields == null || fields.trim().isEmpty()) {
            return null;
        }
        return fields.split(",");
    }

    /**
     * 페이지네이션 메타데이터 생성
     */
    private <T> Map<String, Object> createPaginationMeta(Page<T> page) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("current_page", page.getNumber() + 1);
        meta.put("next_page", page.hasNext() ? page.getNumber() + 2 : null);
        meta.put("prev_page", page.hasPrevious() ? page.getNumber() : null);
        meta.put("total_pages", page.getTotalPages());
        meta.put("total_count", page.getTotalElements());
        meta.put("per_page", page.getSize());
        return meta;
    }
}
