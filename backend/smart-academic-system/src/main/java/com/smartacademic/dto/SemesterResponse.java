
package com.smartacademic.dto;

import com.smartacademic.entity.SemesterStatus;
import java.time.LocalDate;

public record SemesterResponse(
        Long id,
        Long programId,
        String programName,
        Long academicYearId,
        String academicYearLabel,
        int semesterNumber,
        LocalDate startDate,
        LocalDate endDate,
        SemesterStatus status
) {
}
