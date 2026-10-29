
package com.smartacademic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AcademicYearRequest(
        @NotBlank(message = "Academic year label is required")
        @Size(max = 30, message = "Label cannot exceed 30 characters")
        String label,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate
) {
}
