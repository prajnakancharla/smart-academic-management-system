
package com.smartacademic.dto;

public record TeacherResponse(
    Long id,
    Long userId,
    String username,
    String email,
    String employeeId,
    String firstName,
    String lastName,
    String phone,
    Long departmentId,
    String departmentName,
    boolean active
) {}
