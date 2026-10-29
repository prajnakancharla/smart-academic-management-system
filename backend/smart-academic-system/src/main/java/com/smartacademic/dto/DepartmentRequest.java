
package com.smartacademic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(

        @NotBlank(message = "Department name is required")
        @Size(max = 150, message = "Department name cannot exceed 150 characters")
        String name,

        @NotBlank(message = "Department code is required")
        @Size(max = 20, message = "Department code cannot exceed 20 characters")
        String code

) {
}
