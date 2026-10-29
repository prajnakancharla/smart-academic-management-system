
package com.smartacademic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record TeacherAssignmentResponse(
    Long id,
    Long teacherId,
    String employeeId,
    String teacherName,
    Long offeringId,
    Long subjectId,
    String subjectCode,
    String subjectTitle,
    Long semesterId,
    Integer semesterNumber,
    BigDecimal credits,
    String status,
    LocalDate semesterStartDate,
    LocalDate semesterEndDate,
    LocalDateTime assignedAt
) {}
