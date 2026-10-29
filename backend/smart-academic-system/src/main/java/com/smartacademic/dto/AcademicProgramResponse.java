
package com.smartacademic.dto;

public record AcademicProgramResponse(
        Long id,
        Long departmentId,
        String departmentName,
        String name,
        String code,
        int durationYears,
        int semestersPerYear,
        int totalSemesters,
        boolean active
) {
}
