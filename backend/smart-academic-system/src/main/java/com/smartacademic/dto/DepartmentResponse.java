
package com.smartacademic.dto;

public record DepartmentResponse(
        Long id,
        String name,
        String code,
        boolean active
) {
}
