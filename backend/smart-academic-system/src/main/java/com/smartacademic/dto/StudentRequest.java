package com.smartacademic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentRequest(

        @NotBlank(message = "Username is required")
        @Size(max = 100, message = "Username cannot exceed 100 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        @NotBlank(message = "Roll number is required")
        @Size(max = 30, message = "Roll number cannot exceed 30 characters")
        String rollNumber,

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @Pattern(
                regexp = "^$|^[0-9+() -]{7,20}$",
                message = "Enter a valid phone number"
        )
        String phone,

        @NotNull(message = "Program ID is required")
        @Min(value = 1, message = "Program ID must be positive")
        Long programId,

        @NotNull(message = "Admission date is required")
        LocalDate admissionDate,

        @Min(value = 1, message = "Current year must be at least 1")
        int currentYear

) {
}