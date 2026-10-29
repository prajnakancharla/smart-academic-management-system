package com.smartacademic.dto;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        Long userId,
        String username,
        String email,
        String rollNumber,
        String firstName,
        String lastName,
        String phone,
        Long programId,
        String programName,
        String departmentName,
        LocalDate admissionDate,
        int currentYear,
        int programDurationYears,
        int expectedCompletionYear,
        boolean active
) {
}