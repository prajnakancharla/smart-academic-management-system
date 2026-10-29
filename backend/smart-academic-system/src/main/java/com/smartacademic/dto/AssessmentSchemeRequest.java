package com.smartacademic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AssessmentSchemeRequest(

        @NotBlank(message = "Scheme name is required")
        @Size(max = 100, message = "Scheme name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Subject type is required")
        @Pattern(
                regexp = "(?i)THEORY|LABORATORY",
                message = "Subject type must be THEORY or LABORATORY"
        )
        String subjectType
) {
}