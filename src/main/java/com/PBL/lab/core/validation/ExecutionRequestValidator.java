package com.PBL.lab.core.validation;

import com.PBL.lab.core.dto.ValidationResult;
import com.PBL.lab.core.entity.Language;
import com.PBL.lab.core.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * 제출 요청 검증자
 *
 * Judge0 Ruby의 검증 로직을 구현한
 * 제출 요청용 커스텀 검증자
 */
@Component
@RequiredArgsConstructor
public class ExecutionRequestValidator {

    private final LanguageService languageService;

    /*
    공통 검중해야할 옵션
    1. 언어 검증
    2. 프로젝트 vs 단일 파일 검증 - additionalFiles
     */

    public ValidationResult validateLanguage(Integer languageId) {
        Language language = languageService.findById(languageId);

        if (language == null) {
            return ValidationResult.invalid("언어 ID " + languageId + "가 존재하지 않습니다");
        }

        if (language.getIsArchived()) {
            return ValidationResult.invalid(
                    "언어 ID " + language.getName() + "는 아카이브되어 더 이상 사용할 수 없습니다"
            );
        }

        return ValidationResult.valid();
    }

    public ValidationResult validateSourceCodeType(
            Integer languageId,
            String sourceCode,
            String additionalFiles
    ) {
        // 1. 언어 조회
        Language language = languageService.findById(languageId);
        if (language == null) {
            return ValidationResult.invalid("언어 ID " + languageId + "가 존재하지 않습니다");
        }

        // 2. 프로젝트 타입 여부 확인
        boolean isProject = "Multi-file program".equals(language.getName());

        // 3. 프로젝트 타입 검증
        if (isProject) {
            if (sourceCode != null && !sourceCode.trim().isEmpty()) {
                return ValidationResult.invalid("프로젝트 제출에서는 소스 코드가 비어있어야 합니다");
            }

            if (additionalFiles == null || additionalFiles.trim().isEmpty()) {
                return ValidationResult.invalid("프로젝트 제출에는 추가 파일이 필요합니다");
            }
        } else {
            // 4. 일반 언어 검증
            if (sourceCode == null || sourceCode.trim().isEmpty()) {
                return ValidationResult.invalid("단일 파일 제출에는 소스 코드가 필요합니다");
            }
        }

        // 5. 검증 통과
        return ValidationResult.valid();
    }
}
