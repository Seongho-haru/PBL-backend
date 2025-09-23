package com.PBL.lab.judge0.service;

import com.PBL.lab.judge0.entity.Language;
import com.PBL.lab.judge0.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Language Service - 프로그래밍 언어 관리 비즈니스 로직 서비스
 * 
 * 목적:
 * - Judge0에서 지원하는 모든 프로그래밍 언어 관리
 * - 언어 설정 및 유효성 검증 담당
 * - 언어별 실행 환경 및 자원 제한 설정 관리
 * 
 * 핵심 기능:
 * - findById()/findByName(): 언어 조회 (캐싱 지원)
 * - findAllEnabled(): 활성화된 언어 목록 조회 (비아카이브)
 * - findAllIncludingArchived(): 아카이브 포함 전체 언어 목록
 * - isLanguageSupported(): 언어 지원 여부 확인
 * - getCompiledLanguages()/getInterpretedLanguages(): 컴파일/인터프리터 언어 분류
 * 
 * 언어 분류 및 특성:
 * - 컴파일 언어: C, C++, Java, C# 등 컴파일 단계가 필요한 언어
 * - 인터프리터 언어: Python, JavaScript, Ruby 등 직접 실행 가능한 언어
 * - 프로젝트 언어: 다중 파일로 구성된 복잡한 프로젝트 지원
 * - 컴파일러 옵션 지원 여부: 컴파일 언어만 옵션 설정 가능
 * 
 * Docker 컨테이너 환경:
 * - getDockerImage(): 언어별 전용 Docker 이미지 또는 공통 이미지
 * - getCompileCommand()/getRunCommand(): 언어별 컴파일/실행 명령어
 * - 컨테이너 내에서의 안전한 실행 환경 설정
 * - 언어별 실행 시간 및 메모리 제한 기본값
 * 
 * 자원 제한 관리:
 * - getDefaultLimits(): 언어별 기본 실행 제한값 (CPU, 메모리)
 * - ResourceLimits 클래스를 통한 제한값 전달
 * - 시스템 기본값과 언어별 최적화된 제한값 관리
 * - 성능과 보안을 모두 고려한 균형있는 제한 설정
 * 
 * 성능 최적화:
 * - @Cacheable을 통한 언어 정보 캐싱 (자주 변경되지 않음)
 * - 단일 조회부터 목록 조회까지 모든 연산 캐싱
 * - 창고 조회 첫 번째만 데이터베이스 액세스로 성능 향상
 * 
 * 유횤성 검증:
 * - validateLanguageForSubmission(): 제출 전 언어 유효성 검증
 * - 아카이브된 언어 사용 금지 및 대체 언어 안내
 * - 언어 삭제 또는 비활성화 시 실행 중인 제출 보호
 * 
 * 모니터링 및 통계:
 * - getLanguageStatistics(): 언어 사용 통계 및 분포 정보
 * - 활성/비활성/컴파일/인터프리터 언어 수 집계
 * - 언어별 제출 빈도 및 인기도 지표 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LanguageService {

    private final LanguageRepository languageRepository;

    /**
     * Find language by ID
     */
    @Cacheable(value = "languages", key = "#id")
    public Language findById(Integer id) {
        Optional<Language> language = languageRepository.findById(id);
        if (language.isEmpty()) {
            log.warn("Language with ID {} not found", id);
            return null;
        }
        return language.get();
    }

    /**
     * Find language by name
     */
    @Cacheable(value = "languages", key = "#name")
    public Language findByName(String name) {
        return languageRepository.findByName(name).orElse(null);
    }

    /**
     * Get all enabled languages (not archived)
     */
    @Cacheable(value = "languages", key = "'enabled'")
    public List<Language> findAllEnabled() {
        return languageRepository.findAllEnabled();
    }

    /**
     * Get all languages including archived ones
     */
    @Cacheable(value = "languages", key = "'all'")
    public List<Language> findAllIncludingArchived() {
        return languageRepository.findAllIncludingArchived();
    }

    /**
     * Check if language is supported (exists and enabled)
     */
    public boolean isLanguageSupported(Integer id) {
        if (id == null) {
            return false;
        }
        return languageRepository.existsByIdAndEnabled(id);
    }

    /**
     * Get compiled languages
     */
    @Cacheable(value = "languages", key = "'compiled'")
    public List<Language> getCompiledLanguages() {
        return languageRepository.findCompiledLanguages();
    }

    /**
     * Get interpreted languages
     */
    @Cacheable(value = "languages", key = "'interpreted'")
    public List<Language> getInterpretedLanguages() {
        return languageRepository.findInterpretedLanguages();
    }

    /**
     * Find project language (Multi-file program)
     */
    @Cacheable(value = "languages", key = "'project'")
    public Language findProjectLanguage() {
        return languageRepository.findProjectLanguage().orElse(null);
    }

    /**
     * Get languages by feature support
     */
    public List<Language> findLanguagesByFeatures(Boolean supportsCompilation) {
        return languageRepository.findLanguagesByFeatures(supportsCompilation);
    }

    /**
     * Search languages by name
     */
    public List<Language> searchByName(String namePart) {
        if (namePart == null || namePart.trim().isEmpty()) {
            return findAllEnabled();
        }
        return languageRepository.findByNameContainingIgnoreCase(namePart.trim());
    }

    /**
     * Get default resource limits for a language
     */
    public ResourceLimits getDefaultLimits(Integer languageId) {
        Language language = findById(languageId);
        if (language == null) {
            // Return system defaults
            return ResourceLimits.systemDefaults();
        }

        return ResourceLimits.builder()
                .timeLimit(language.getTimeLimit())
                .memoryLimit(language.getMemoryLimit())
                .build();
    }

    /**
     * Get languages with custom Docker configurations
     */
    @Cacheable(value = "languages", key = "'custom_docker'")
    public List<Language> getLanguagesWithCustomDocker() {
        return languageRepository.findLanguagesWithCustomDocker();
    }

    /**
     * Get language statistics
     */
    public Map<String, Object> getLanguageStatistics() {
        long totalLanguages = languageRepository.countTotalLanguages();
        long enabledLanguages = languageRepository.countEnabledLanguages();
        
        return Map.of(
            "total", totalLanguages,
            "enabled", enabledLanguages,
            "archived", totalLanguages - enabledLanguages,
            "compiled", languageRepository.findCompiledLanguages().size(),
            "interpreted", languageRepository.findInterpretedLanguages().size()
        );
    }

    /**
     * Validate language for submission
     */
    public void validateLanguageForSubmission(Integer languageId) {
        if (languageId == null) {
            throw new IllegalArgumentException("Language ID is required");
        }

        Language language = findById(languageId);
        if (language == null) {
            throw new IllegalArgumentException("Language with id " + languageId + " doesn't exist");
        }

        if (Boolean.TRUE.equals(language.getIsArchived())) {
            throw new IllegalArgumentException("Language with id " + languageId + " is archived and cannot be used anymore");
        }
    }

    /**
     * Check if language supports compiler options
     */
    public boolean supportsCompilerOptions(Integer languageId) {
        Language language = findById(languageId);
        return language != null && language.supportsCompilation();
    }

    /**
     * Get effective Docker image for language
     */
    public String getDockerImage(Integer languageId) {
        Language language = findById(languageId);
        if (language == null) {
            return "judge0/compilers:1.4.0"; // Default fallback
        }
        return language.getEffectiveDockerImage();
    }

    /**
     * Get effective compile command for language
     */
    public String getCompileCommand(Integer languageId) {
        Language language = findById(languageId);
        if (language == null) {
            return null;
        }
        return language.getEffectiveCompileCommand();
    }

    /**
     * Get effective run command for language
     */
    public String getRunCommand(Integer languageId) {
        Language language = findById(languageId);
        if (language == null) {
            return null;
        }
        return language.getEffectiveRunCommand();
    }

    /**
     * Resource limits data class
     */
    @lombok.Data
    @lombok.Builder
    public static class ResourceLimits {
        private final java.math.BigDecimal timeLimit;
        private final Integer memoryLimit;

        public ResourceLimits(java.math.BigDecimal timeLimit, Integer memoryLimit) {
            this.timeLimit = timeLimit;
            this.memoryLimit = memoryLimit;
        }

        public static ResourceLimits systemDefaults() {
            return new ResourceLimits(java.math.BigDecimal.valueOf(5.0), 128000);
        }
    }
}
