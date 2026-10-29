
package com.smartacademic.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TeacherAssignmentRequest(

    @NotNull(message = "Teacher ID is required")
    @Positive(message = "Teacher ID must be positive")
    Long teacherId,

    @NotNull(message = "Offering ID is required")
    @Positive(message = "Offering ID must be positive")
    Long offeringId

) {}
