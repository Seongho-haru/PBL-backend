package com.PBL.ai.tools;

import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.service.LanguageService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI가 사용할 프로그래밍 언어 조회 도구
 *
 * Judge0에서 지원하는 프로그래밍 언어 정보를 제공합니다.
 * 커리큘럼 및 강의 생성 시 지원 가능한 언어를 확인하는데 사용됩니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LanguageTools {

    private final LanguageService languageService;

    @Tool("현재 시스템에서 지원하는 모든 활성화된 프로그래밍 언어 목록을 조회합니다. " +
          "각 언어의 이름, ID, 컴파일 지원 여부, 실행 방식 등의 정보를 제공합니다. " +
          "커리큘럼이나 강의를 생성하기 전에 반드시 이 도구를 사용하여 지원 가능한 언어를 확인하세요.")
    public Map<String, Object> getAllSupportedLanguages() {
        log.debug("🔧 [도구 호출] getAllSupportedLanguages");

        Map<String, Object> result = new HashMap<>();

        try {
            List<Language> languages = languageService.findAllEnabled();

            List<Map<String, Object>> languageList = languages.stream()
                    .map(this::languageToMap)
                    .collect(Collectors.toList());

            result.put("languages", languageList);
            result.put("total_count", languages.size());
            result.put("compiled_count", languages.stream().filter(Language::supportsCompilation).count());
            result.put("interpreted_count", languages.stream().filter(l -> !l.supportsCompilation()).count());

            log.debug("✅ [도구 결과] getAllSupportedLanguages - 총 {}개 언어", languages.size());

        } catch (Exception e) {
            log.error("❌ [도구 오류] getAllSupportedLanguages - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("languages", List.of());
        }

        return result;
    }

    @Tool("특정 프로그래밍 언어를 이름으로 검색합니다. " +
          "부분 일치 검색을 지원하므로 'Python', 'Java', 'C++' 등의 키워드로 검색할 수 있습니다. " +
          "예: 'Python'으로 검색하면 'Python (3.8.1)', 'Python (3.11.2)' 등을 모두 찾습니다.")
    public Map<String, Object> searchLanguageByName(
            @P("검색할 언어 이름 (부분 일치, 예: Python, Java, C++)") String name) {

        log.debug("🔧 [도구 호출] searchLanguageByName - 이름: {}", name);

        Map<String, Object> result = new HashMap<>();

        try {
            List<Language> languages = languageService.searchByName(name);

            List<Map<String, Object>> languageList = languages.stream()
                    .filter(Language::isEnabled)
                    .map(this::languageToMap)
                    .collect(Collectors.toList());

            result.put("query", name);
            result.put("languages", languageList);
            result.put("found_count", languageList.size());

            log.debug("✅ [도구 결과] searchLanguageByName - 이름: {}, 결과: {}개", name, languageList.size());

        } catch (Exception e) {
            log.error("❌ [도구 오류] searchLanguageByName - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("languages", List.of());
        }

        return result;
    }

    @Tool("특정 ID의 프로그래밍 언어 정보를 조회합니다. " +
          "언어 ID를 알고 있을 때 정확한 언어 정보를 가져올 수 있습니다.")
    public Map<String, Object> getLanguageById(
            @P("언어 ID (정수)") Integer languageId) {

        log.debug("🔧 [도구 호출] getLanguageById - ID: {}", languageId);

        Map<String, Object> result = new HashMap<>();

        try {
            Language language = languageService.findById(languageId);

            if (language == null) {
                result.put("found", false);
                result.put("message", "언어 ID " + languageId + "를 찾을 수 없습니다.");
            } else if (!language.isEnabled()) {
                result.put("found", true);
                result.put("enabled", false);
                result.put("message", "언어 ID " + languageId + "는 현재 비활성화 상태입니다.");
                result.put("language", languageToMap(language));
            } else {
                result.put("found", true);
                result.put("enabled", true);
                result.put("language", languageToMap(language));
            }

            log.debug("✅ [도구 결과] getLanguageById - ID: {}, 찾음: {}", languageId, result.get("found"));

        } catch (Exception e) {
            log.error("❌ [도구 오류] getLanguageById - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Tool("컴파일이 필요한 프로그래밍 언어들만 조회합니다. " +
          "C, C++, Java, C#, Go, Rust 등 컴파일 단계가 있는 언어들을 반환합니다. " +
          "컴파일 언어 관련 커리큘럼이나 강의를 만들 때 사용하세요.")
    public Map<String, Object> getCompiledLanguages() {
        log.debug("🔧 [도구 호출] getCompiledLanguages");

        Map<String, Object> result = new HashMap<>();

        try {
            List<Language> languages = languageService.getCompiledLanguages();

            List<Map<String, Object>> languageList = languages.stream()
                    .filter(Language::isEnabled)
                    .map(this::languageToMap)
                    .collect(Collectors.toList());

            result.put("languages", languageList);
            result.put("count", languageList.size());
            result.put("type", "compiled");
            result.put("description", "컴파일 단계가 필요한 언어들");

            log.debug("✅ [도구 결과] getCompiledLanguages - 총 {}개", languageList.size());

        } catch (Exception e) {
            log.error("❌ [도구 오류] getCompiledLanguages - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("languages", List.of());
        }

        return result;
    }

    @Tool("인터프리터 방식의 프로그래밍 언어들만 조회합니다. " +
          "Python, JavaScript, Ruby, PHP 등 직접 실행 가능한 언어들을 반환합니다. " +
          "스크립트 언어 관련 커리큘럼이나 강의를 만들 때 사용하세요.")
    public Map<String, Object> getInterpretedLanguages() {
        log.debug("🔧 [도구 호출] getInterpretedLanguages");

        Map<String, Object> result = new HashMap<>();

        try {
            List<Language> languages = languageService.getInterpretedLanguages();

            List<Map<String, Object>> languageList = languages.stream()
                    .filter(Language::isEnabled)
                    .map(this::languageToMap)
                    .collect(Collectors.toList());

            result.put("languages", languageList);
            result.put("count", languageList.size());
            result.put("type", "interpreted");
            result.put("description", "컴파일 없이 직접 실행 가능한 언어들");

            log.debug("✅ [도구 결과] getInterpretedLanguages - 총 {}개", languageList.size());

        } catch (Exception e) {
            log.error("❌ [도구 오류] getInterpretedLanguages - 에러: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("languages", List.of());
        }

        return result;
    }

    @Tool("시스템의 언어 지원 통계 정보를 조회합니다. " +
          "전체 언어 수, 활성화된 언어 수, 컴파일/인터프리터 언어 분포 등을 제공합니다.")
    public Map<String, Object> getLanguageStatistics() {
        log.debug("🔧 [도구 호출] getLanguageStatistics");

        try {
            Map<String, Object> stats = languageService.getLanguageStatistics();

            log.debug("✅ [도구 결과] getLanguageStatistics - 활성화: {}, 전체: {}",
                     stats.get("enabled"), stats.get("total"));

            return stats;

        } catch (Exception e) {
            log.error("❌ [도구 오류] getLanguageStatistics - 에러: {}", e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    /**
     * Language 엔티티를 AI가 이해하기 쉬운 Map으로 변환
     */
    private Map<String, Object> languageToMap(Language language) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", language.getId());
        map.put("name", language.getName());
        map.put("is_compiled", language.supportsCompilation());
        map.put("is_enabled", language.isEnabled());
        map.put("source_file", language.getSourceFile());

        // 실행 정보
        if (language.supportsCompilation()) {
            map.put("compile_cmd", language.getCompileCmd());
        }
        map.put("run_cmd", language.getRunCmd());

        // 자원 제한
        map.put("time_limit_seconds", language.getTimeLimit());
        map.put("memory_limit_kb", language.getMemoryLimit());

        // 특수 언어 표시
        if (language.isProject()) {
            map.put("is_project_language", true);
            map.put("supports_multi_file", true);
        }

        return map;
    }
}
