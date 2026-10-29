
package com.smartacademic.dto;

import java.math.BigDecimal;

public record SubjectResponse(
        Long id,
        Long programId,
        String programName,
        String code,
        String title,
        String description,
        String subjectType,
        BigDecimal defaultCredits,
        boolean active
) {
}
