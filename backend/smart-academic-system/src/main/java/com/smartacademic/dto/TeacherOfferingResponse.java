
package com.smartacademic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TeacherOfferingResponse(
        Long assignmentId,
        Long offeringId,
        String subjectCode,
        String subjectTitle,
        String subjectType,
        String programName,
        String semesterName,
        BigDecimal credits,
        String offeringStatus,
        LocalDateTime assignedAt
) {
}
