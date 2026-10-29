
package com.smartacademic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TeacherRequest(

    @NotBlank(message = "Username is required")
    @Size(max = 80, message = "Username cannot exceed 80 characters")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    String email,

    @NotBlank(message = "Employee ID is required")
    @Size(max = 40, message = "Employee ID cannot exceed 40 characters")
    String employeeId,

    @NotBlank(message = "First name is required")
    @Size(max = 80, message = "First name cannot exceed 80 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 80, message = "Last name cannot exceed 80 characters")
    String lastName,

    @Pattern(
        regexp = "^$|^[0-9+() -]{7,25}$",
        message = "Enter a valid phone number"
    )
    @Size(max = 25, message = "Phone cannot exceed 25 characters")
    String phone,

    @NotNull(message = "Department ID is required")
    @Positive(message = "Department ID must be positive")
    Long departmentId

) {}
