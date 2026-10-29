
package com.smartacademic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SubjectRequest(

        @NotNull(message = "Program ID is required")
        Long programId,

        @NotBlank(message = "Subject code is required")
        @Size(max = 30, message = "Subject code cannot exceed 30 characters")
        String code,

        @NotBlank(message = "Subject title is required")
        @Size(max = 180, message = "Subject title cannot exceed 180 characters")
        String title,

        String description,

        @NotBlank(message = "Subject type is required")
        @Size(max = 20, message = "Subject type cannot exceed 20 characters")
        String subjectType,

        @NotNull(message = "Default credits are required")
        @DecimalMin(value = "0.01", message = "Credits must be greater than zero")
        BigDecimal defaultCredits
) {
}
