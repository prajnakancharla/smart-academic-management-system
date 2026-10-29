
package com.smartacademic.dto;

import java.math.BigDecimal;

public record AssessmentComponentResponse(
        Long id,
        Long schemeId,
        String schemeName,
        String subjectType,
        String componentCode,
        String componentName,
        BigDecimal maximumMarks,
        BigDecimal weightPercentage
) {
}
