
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.AcademicYearRequest;
import com.smartacademic.dto.AcademicYearResponse;
import com.smartacademic.service.AcademicYearService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/academic-years")
@PreAuthorize("hasRole('ADMIN')")
public class AcademicYearController {

    private final AcademicYearService service;

    public AcademicYearController(AcademicYearService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AcademicYearResponse> create(
            @Valid @RequestBody AcademicYearRequest request) {
        return ApiResponse.success(
                "Academic year created successfully",
                service.create(request));
    }

    @GetMapping
    public ApiResponse<List<AcademicYearResponse>> getAll() {
        return ApiResponse.success(
                "Academic years retrieved successfully",
                service.getAll());
    }

    @GetMapping("/active")
    public ApiResponse<List<AcademicYearResponse>> getActive() {
        return ApiResponse.success(
                "Active academic years retrieved successfully",
                service.getActive());
    }

    @GetMapping("/{id}")
    public ApiResponse<AcademicYearResponse> getById(
            @PathVariable Long id) {
        return ApiResponse.success(
                "Academic year retrieved successfully",
                service.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<AcademicYearResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicYearRequest request) {
        return ApiResponse.success(
                "Academic year updated successfully",
                service.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<AcademicYearResponse> activate(
            @PathVariable Long id) {
        return ApiResponse.success(
                "Academic year activated successfully",
                service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<AcademicYearResponse> deactivate(
            @PathVariable Long id) {
        return ApiResponse.success(
                "Academic year deactivated successfully",
                service.deactivate(id));
    }
}
