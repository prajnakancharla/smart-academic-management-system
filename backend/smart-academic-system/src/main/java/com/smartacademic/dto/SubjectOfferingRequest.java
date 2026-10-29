
package com.smartacademic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SubjectOfferingRequest(

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Semester ID is required")
        Long semesterId,

        @NotNull(message = "Credits are required")
        @DecimalMin(value = "0.01", message = "Credits must be greater than zero")
        BigDecimal credits
) {
}
