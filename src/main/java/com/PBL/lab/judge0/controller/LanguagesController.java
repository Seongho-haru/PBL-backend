package com.PBL.lab.judge0.controller;

import com.PBL.lab.judge0.entity.Language;
import com.PBL.lab.judge0.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Languages Controller - 프로그래밍 언어 관리 REST API 컴트롤러
 * 
 * 목적:
 * - Judge0에서 지원하는 모든 프로그래밍 언어 정보를 제공하는 REST API
 * - 원본 Judge0 Ruby LanguagesController의 완전한 Java 포팅
 * - 클라이언트가 제출 전에 지원 언어를 확인할 수 있도록 지원
 * 
 * 핵심 API 엔드포인트:
 * - GET /languages: 활성화된 언어 목록 (비아카이브만)
 * - GET /languages/all: 아카이브 포함 전체 언어 목록
 * - GET /languages/{id}: 특정 언어의 상세 정보 조회
 * 
 * 언어 목록 API (/languages):
 * - 제출 가능한 활성 언어만 반환
 * - 간단한 정보만 제공 (id, name)
 * - 클라이언트 드롭다운 메뉴 구성용
 * - 고속 응답을 위해 최소 정보만 전달
 * 
 * 전체 언어 API (/languages/all):
 * - 아카이브된 언어 포함 모든 언어 목록
 * - isArchived 필드로 상태 구분 가능
 * - 관리자 인터페이스나 고급 클라이언트용
 * - 언어 상태 관리 및 모니터링 목적
 * 
 * 언어 상세 API (/languages/{id}):
 * - 특정 언어의 모든 설정 정보 제공
 * - 컴파일/실행 명령어, Docker 설정, 자원 제한 등
 * - 언어별 실행 환경 구성 정보 제공
 * - 디버깅 및 최적화 목적으로 활용
 * 
 * 응답 DTO 구조:
 * - LanguageResponse: 기본 언어 정보 (id, name, isArchived)
 * - LanguageDetailResponse: 상세 언어 정보 (모든 설정 포함)
 * - 필요에 따라 다른 상세도의 정보 제공
 * - JSON 직렬화 최적화 및 클라이언트 쮐싱
 * 
 * 성능 및 캐싱:
 * - LanguageService에서 @Cacheable을 통한 캐싱 처리
 * - 언어 정보는 자주 변경되지 않아 캐싱 효과 극대화
 * - 대량 요청에도 빠른 응답 속도 보장
 * - DB 부하 최소화로 전체 시스템 성능 향상
 * 
 * 오류 처리:
 * - 존재하지 않는 언어 ID: 404 Not Found 반환
 * - 잘못된 요청 형식: 자동 400 Bad Request 처리
 * - 내부 서버 오류 시 graceful degradation
 * 
 * 클라이언트 활용 예시:
 * - 코드 에디터: 언어 드롭다운 메뉴 구성
 * - IDE 플러그인: 지원 언어 목록 동기화
 * - 대회 플랫폼: 문제별 언어 제한 설정
 * - 모니터링 대시보드: 언어 사용 현황 분석
 */
@RestController
@RequestMapping("/languages")
@RequiredArgsConstructor
public class LanguagesController {

    private final LanguageService languageService;

    /**
     * GET /languages
     * Get all enabled languages
     */
    @GetMapping
    public ResponseEntity<List<LanguageResponse>> index() {
        List<Language> languages = languageService.findAllEnabled();
        List<LanguageResponse> response = languages.stream()
                .map(lang -> LanguageResponse.builder()
                        .id(lang.getId())
                        .name(lang.getName())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /languages/all
     * Get all languages including archived
     */
    @GetMapping("/all")
    public ResponseEntity<List<LanguageResponse>> all() {
        List<Language> languages = languageService.findAllIncludingArchived();
        List<LanguageResponse> response = languages.stream()
                .map(lang -> LanguageResponse.builder()
                        .id(lang.getId())
                        .name(lang.getName())
                        .isArchived(lang.getIsArchived())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /languages/{id}
     * Get specific language by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Integer id) {
        Language language = languageService.findById(id);
        if (language == null) {
            return ResponseEntity.notFound().build();
        }

        LanguageDetailResponse response = LanguageDetailResponse.builder()
                .id(language.getId())
                .name(language.getName())
                .compileCmd(language.getCompileCmd())
                .runCmd(language.getRunCmd())
                .sourceFile(language.getSourceFile())
                .isArchived(language.getIsArchived())
                .dockerImage(language.getDockerImage())
                .timeLimit(language.getTimeLimit())
                .memoryLimit(language.getMemoryLimit())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Language Response DTO for list endpoints
     */
    @lombok.Data
    @lombok.Builder
    public static class LanguageResponse {
        private Integer id;
        private String name;
        private Boolean isArchived;
    }

    /**
     * Language Detail Response DTO for show endpoint
     */
    @lombok.Data
    @lombok.Builder
    public static class LanguageDetailResponse {
        private Integer id;
        private String name;
        private String compileCmd;
        private String runCmd;
        private String sourceFile;
        private Boolean isArchived;
        private String dockerImage;
        private java.math.BigDecimal timeLimit;
        private Integer memoryLimit;
    }
}
