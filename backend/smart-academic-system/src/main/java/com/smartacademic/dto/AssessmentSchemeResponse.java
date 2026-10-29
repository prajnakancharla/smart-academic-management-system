package com.smartacademic.dto;

public record AssessmentSchemeResponse(
        Long id,
        String name,
        String subjectType,
        boolean active
) {
}