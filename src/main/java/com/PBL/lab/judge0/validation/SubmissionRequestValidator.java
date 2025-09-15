package com.PBL.lab.judge0.validation;

import com.PBL.lab.judge0.dto.SubmissionRequest;
import com.PBL.lab.judge0.entity.Language;
import com.PBL.lab.judge0.service.ConfigService;
import com.PBL.lab.judge0.service.LanguageService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Submission Request Validator
 * 
 * Custom validator for submission requests that implements
 * Judge0 Ruby validation logic.
 */
@Component
@RequiredArgsConstructor
public class SubmissionRequestValidator implements ConstraintValidator<ValidSubmission, SubmissionRequest> {

    private final LanguageService languageService;
    private final ConfigService configService;

    @Override
    public void initialize(ValidSubmission constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(SubmissionRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        // Language existence and archival check
        Language language = null;
        try {
            language = languageService.findById(request.getLanguageId());
            if (language.getIsArchived()) {
                context.buildConstraintViolationWithTemplate(
                    "language with id " + request.getLanguageId() + " is archived and cannot be used anymore"
                ).addPropertyNode("languageId").addConstraintViolation();
                valid = false;
            }
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate(
                "language with id " + request.getLanguageId() + " doesn't exist"
            ).addPropertyNode("languageId").addConstraintViolation();
            valid = false;
        }

        // Project vs non-project validation
        if (language != null) {
            boolean isProject = "Multi-file program".equals(language.getName());
            
            if (isProject) {
                if (request.getSourceCode() != null && !request.getSourceCode().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                        "source code must be empty for project submissions"
                    ).addPropertyNode("sourceCode").addConstraintViolation();
                    valid = false;
                }
                
                if (request.getAdditionalFiles() == null || request.getAdditionalFiles().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                        "additional files are required for project submissions"
                    ).addPropertyNode("additionalFiles").addConstraintViolation();
                    valid = false;
                }
            } else {
                if (request.getSourceCode() == null || request.getSourceCode().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                        "source code is required for non-project submissions"
                    ).addPropertyNode("sourceCode").addConstraintViolation();
                    valid = false;
                }
            }
        }

        // Compiler options validation
        if (request.getCompilerOptions() != null && !request.getCompilerOptions().trim().isEmpty()) {
            if (!configService.isCompilerOptionsEnabled()) {
                context.buildConstraintViolationWithTemplate(
                    "setting compiler options is not allowed"
                ).addPropertyNode("compilerOptions").addConstraintViolation();
                valid = false;
            } else if (language != null && language.getCompileCmd() == null) {
                context.buildConstraintViolationWithTemplate(
                    "setting compiler options is only allowed for compiled languages"
                ).addPropertyNode("compilerOptions").addConstraintViolation();
                valid = false;
            } else if (language != null) {
                List<String> allowedLanguages = configService.getAllowedLanguagesForCompilerOptions();
                if (!allowedLanguages.isEmpty()) {
                    final Language finalLanguage = language; // Make it effectively final
                    boolean isAllowed = allowedLanguages.stream()
                            .anyMatch(allowed -> finalLanguage.getName().startsWith(allowed.trim()));
                    
                    if (!isAllowed) {
                        String allowedStr = allowedLanguages.size() > 1 
                            ? String.join(", ", allowedLanguages.subList(0, allowedLanguages.size() - 1)) + 
                              " and " + allowedLanguages.get(allowedLanguages.size() - 1)
                            : allowedLanguages.get(0);
                        
                        context.buildConstraintViolationWithTemplate(
                            "setting compiler options is only allowed for " + allowedStr
                        ).addPropertyNode("compilerOptions").addConstraintViolation();
                        valid = false;
                    }
                }
            }
        }

        // Command line arguments validation
        if (request.getCommandLineArguments() != null && !request.getCommandLineArguments().trim().isEmpty()) {
            if (!configService.isCommandLineArgumentsEnabled()) {
                context.buildConstraintViolationWithTemplate(
                    "setting command line arguments is not allowed"
                ).addPropertyNode("commandLineArguments").addConstraintViolation();
                valid = false;
            }
        }

        // Callback validation
        if (request.getCallbackUrl() != null && !request.getCallbackUrl().trim().isEmpty()) {
            if (!configService.isCallbacksEnabled()) {
                context.buildConstraintViolationWithTemplate(
                    "setting callback is not allowed"
                ).addPropertyNode("callbackUrl").addConstraintViolation();
                valid = false;
            }
        }

        // Additional files validation
        if (request.getAdditionalFiles() != null && !request.getAdditionalFiles().trim().isEmpty()) {
            if (!configService.isAdditionalFilesEnabled()) {
                context.buildConstraintViolationWithTemplate(
                    "setting additional files is not allowed"
                ).addPropertyNode("additionalFiles").addConstraintViolation();
                valid = false;
            }
        }

        // Network validation
        if (request.getEnableNetwork() != null && request.getEnableNetwork()) {
            if (!configService.isNetworkAllowed()) {
                context.buildConstraintViolationWithTemplate(
                    "enabling network is not allowed"
                ).addPropertyNode("enableNetwork").addConstraintViolation();
                valid = false;
            }
        }

        // Per-process and thread limits validation
        if (request.getEnablePerProcessAndThreadTimeLimit() != null && 
            request.getEnablePerProcessAndThreadTimeLimit()) {
            if (!configService.getAllowEnablePerProcessAndThreadTimeLimit()) {
                context.buildConstraintViolationWithTemplate(
                    "this option cannot be enabled"
                ).addPropertyNode("enablePerProcessAndThreadTimeLimit").addConstraintViolation();
                valid = false;
            }
        }

        if (request.getEnablePerProcessAndThreadMemoryLimit() != null && 
            request.getEnablePerProcessAndThreadMemoryLimit()) {
            if (!configService.getAllowEnablePerProcessAndThreadMemoryLimit()) {
                context.buildConstraintViolationWithTemplate(
                    "this option cannot be enabled"
                ).addPropertyNode("enablePerProcessAndThreadMemoryLimit").addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
