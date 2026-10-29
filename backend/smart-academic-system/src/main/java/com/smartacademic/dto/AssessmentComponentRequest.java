
package com.smartacademic.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AssessmentComponentRequest(

        @NotNull(message = "Scheme ID is required")
        @Positive(message = "Scheme ID must be positive")
        Long schemeId,

        @NotBlank(message = "Component code is required")
        @Size(max = 30, message = "Component code cannot exceed 30 characters")
        String componentCode,

        @NotBlank(message = "Component name is required")
        @Size(max = 100, message = "Component name cannot exceed 100 characters")
        String componentName,

        @NotNull(message = "Maximum marks are required")
        @DecimalMin(value = "0.01", message = "Maximum marks must be greater than zero")
        @DecimalMax(value = "9999.99", message = "Maximum marks cannot exceed 9999.99")
        BigDecimal maximumMarks,

        @NotNull(message = "Weight percentage is required")
        @DecimalMin(value = "0.00", message = "Weight percentage cannot be negative")
        @DecimalMax(value = "100.00", message = "Weight percentage cannot exceed 100")
        BigDecimal weightPercentage
) {
}
