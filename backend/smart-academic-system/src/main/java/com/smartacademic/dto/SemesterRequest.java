
package com.smartacademic.dto;

import com.smartacademic.entity.SemesterStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SemesterRequest(

        @NotNull(message = "Program ID is required")
        @Min(value = 1, message = "Program ID must be positive")
        Long programId,

        @NotNull(message = "Academic year ID is required")
        @Min(value = 1, message = "Academic year ID must be positive")
        Long academicYearId,

        @Min(value = 1, message = "Semester number must be at least 1")
        int semesterNumber,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotNull(message = "Semester status is required")
        SemesterStatus status
) {
}
