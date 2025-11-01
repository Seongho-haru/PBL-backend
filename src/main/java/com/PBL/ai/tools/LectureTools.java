package com.PBL.ai.tools;

import com.PBL.lecture.LectureService;
import com.PBL.lecture.LectureType;
import com.PBL.lecture.entity.Lecture;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI가 사용할 강의 조회 도구
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LectureTools {

    private final LectureService lectureService;

    @Tool("강의를 다양한 조건으로 통합 검색합니다. 제목, 카테고리, 난이도, 타입(THEORY/PRACTICE/PROBLEM)으로 필터링할 수 있습니다. " +
          "검색하지 않을 조건은 null로 전달하세요.")
    public List<Lecture> searchLectures(
            @P("검색할 제목 (부분 일치, 선택)") String title,
            @P("카테고리 (선택)") String category,
            @P("난이도: EASY, MEDIUM, HARD 등 (선택)") String difficulty,
            @P("강의 타입: THEORY, PRACTICE, PROBLEM (선택)") String type) {

        log.debug("🔧 [도구 호출] searchLectures - title:{}, category:{}, difficulty:{}, type:{}",
                title, category, difficulty, type);

        LectureType lectureType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                lectureType = LectureType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ [도구 경고] searchLectures - 잘못된 타입 무시: {}", type);
            }
        }
        List<Lecture> result = lectureService.findPublicLectureEntitiesBySearch(title, category, difficulty, lectureType);
        log.debug("✅ [도구 결과] searchLectures - 검색 결과: {}개", result.size());
        return result;
    }

    @Tool("강의 ID로 상세 정보를 조회합니다. 제목, 설명, 타입, 카테고리, 난이도 등을 확인할 수 있습니다.")
    public Lecture getLecture(@P("조회할 강의 ID") Long lectureId) {
        log.debug("🔧 [도구 호출] getLecture - lectureId:{}", lectureId);
        Lecture result = lectureService.findLectureEntity(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));
        log.debug("✅ [도구 결과] getLecture - 강의명: {}", result.getTitle());
        return result;
    }

    @Tool("강의 제목으로 검색합니다. 부분 일치로 검색되며, 대소문자를 구분하지 않습니다.")
    public List<Lecture> searchLecturesByTitle(@P("검색할 강의 제목 (부분 일치)") String title) {
        log.debug("🔧 [도구 호출] searchLecturesByTitle - title:{}", title);
        List<Lecture> result = lectureService.findLectureEntitiesByTitle(title);
        log.debug("✅ [도구 결과] searchLecturesByTitle - 검색 결과: {}개", result.size());
        return result;
    }

    @Tool("카테고리별로 강의를 조회합니다. 예: '알고리즘', '자료구조', '웹개발', 'Python 기초' 등")
    public List<Lecture> getLecturesByCategory(@P("조회할 카테고리") String category) {
        log.debug("🔧 [도구 호출] getLecturesByCategory - category:{}", category);
        List<Lecture> result = lectureService.findLectureEntitiesByCategory(category);
        log.debug("✅ [도구 결과] getLecturesByCategory - 강의 수: {}개", result.size());
        return result;
    }

    @Tool("강의 타입별로 조회합니다. 사용 가능한 타입: THEORY(이론), PRACTICE(실습), PROBLEM(문제)")
    public List<Lecture> getLecturesByType(@P("강의 타입: THEORY, PRACTICE, PROBLEM 중 하나") String type) {
        log.debug("🔧 [도구 호출] getLecturesByType - type:{}", type);
        try {
            LectureType lectureType = LectureType.valueOf(type.toUpperCase());
            List<Lecture> result = lectureService.findLectureEntitiesByType(lectureType);
            log.debug("✅ [도구 결과] getLecturesByType - 강의 수: {}개", result.size());
            return result;
        } catch (IllegalArgumentException e) {
            log.error("❌ [도구 오류] getLecturesByType - 잘못된 타입: {}", type);
            throw new IllegalArgumentException("올바른 강의 타입을 입력하세요: THEORY, PRACTICE, PROBLEM");
        }
    }

    @Tool("공개된 모든 강의를 조회합니다. 학생들이 접근 가능한 강의 목록입니다.")
    public List<Lecture> getPublicLectures() {
        log.debug("🔧 [도구 호출] getPublicLectures");
        List<Lecture> result = lectureService.findPublicLectureEntities();
        log.debug("✅ [도구 결과] getPublicLectures - 강의 수: {}개", result.size());
        return result;
    }

    @Tool("최근에 생성된 강의 10개를 조회합니다. 새로운 강의를 추천할 때 유용합니다.")
    public List<Lecture> getRecentLectures() {
        log.debug("🔧 [도구 호출] getRecentLectures");
        List<Lecture> result = lectureService.findRecentLectureEntities();
        log.debug("✅ [도구 결과] getRecentLectures - 강의 수: {}개", result.size());
        return result;
    }

    @Tool("테스트케이스가 있는 문제 강의만 조회합니다. 코딩 테스트 문제를 찾을 때 유용합니다.")
    public List<Lecture> getProblemLecturesWithTestCases() {
        log.debug("🔧 [도구 호출] getProblemLecturesWithTestCases");
        List<Lecture> result = lectureService.getProblemLecturesWithTestCases();
        log.debug("✅ [도구 결과] getProblemLecturesWithTestCases - 문제 수: {}개", result.size());
        return result;
    }

    @Tool("강의 통계를 조회합니다. 타입별 강의 수와 카테고리별 강의 수를 확인할 수 있습니다.")
    public Map<String, Object> getLectureStats() {
        log.debug("🔧 [도구 호출] getLectureStats");

        Map<String, Object> result = new HashMap<>();

        // 타입별 통계
        List<Object[]> typeStats = lectureService.getLectureStatsByType();
        Map<String, Long> typeMap = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeMap.put(stat[0].toString(), ((Number) stat[1]).longValue());
        }
        result.put("by_type", typeMap);

        // 카테고리별 통계
        List<Object[]> categoryStats = lectureService.getCategoryStats();
        Map<String, Long> categoryMap = new HashMap<>();
        for (Object[] stat : categoryStats) {
            categoryMap.put(stat[0].toString(), ((Number) stat[1]).longValue());
        }
        result.put("by_category", categoryMap);

        log.debug("✅ [도구 결과] getLectureStats - 타입: {}개, 카테고리: {}개",
                typeMap.size(), categoryMap.size());
        return result;
    }
}
