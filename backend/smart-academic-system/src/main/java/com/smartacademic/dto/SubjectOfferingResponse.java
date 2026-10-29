
package com.smartacademic.dto;

import com.smartacademic.entity.OfferingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubjectOfferingResponse(
        Long id,
        Long subjectId,
        String subjectCode,
        String subjectTitle,
        Long semesterId,
        Integer semesterNumber,
        Long programId,
        String programName,
        Long academicYearId,
        String academicYearLabel,
        BigDecimal credits,
        OfferingStatus status,
        LocalDate semesterStartDate,
        LocalDate semesterEndDate
) {
}
