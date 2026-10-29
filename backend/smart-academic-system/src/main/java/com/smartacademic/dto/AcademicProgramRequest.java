
package com.smartacademic.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AcademicProgramRequest(

        @NotNull(message = "Department ID is required")
        @Min(value = 1, message = "Department ID must be positive")
        Long departmentId,

        @NotBlank(message = "Program name is required")
        @Size(max = 150, message = "Program name cannot exceed 150 characters")
        String name,

        @NotBlank(message = "Program code is required")
        @Size(max = 20, message = "Program code cannot exceed 20 characters")
        String code,

        @Min(value = 1, message = "Duration must be at least 1 year")
        @Max(value = 10, message = "Duration cannot exceed 10 years")
        int durationYears,

        @Min(value = 1, message = "There must be at least 1 semester per year")
        @Max(value = 4, message = "Semesters per year cannot exceed 4")
        int semestersPerYear

) {
}
