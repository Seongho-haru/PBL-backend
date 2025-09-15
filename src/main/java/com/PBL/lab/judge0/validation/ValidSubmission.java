package com.PBL.lab.judge0.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Valid Submission Annotation
 * 
 * Custom validation annotation for submission requests.
 * Implements Judge0 Ruby validation logic.
 */
@Documented
@Constraint(validatedBy = SubmissionRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSubmission {
    
    String message() default "Invalid submission";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
