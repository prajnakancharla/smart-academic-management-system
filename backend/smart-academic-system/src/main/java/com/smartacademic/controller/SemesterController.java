
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.SemesterRequest;
import com.smartacademic.dto.SemesterResponse;
import com.smartacademic.service.SemesterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/semesters")
@PreAuthorize("hasRole('ADMIN')")
public class SemesterController {

    private final SemesterService service;

    public SemesterController(SemesterService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SemesterResponse> create(
            @Valid @RequestBody SemesterRequest request) {
        return ApiResponse.success(
                "Semester created successfully",
                service.create(request));
    }

    @GetMapping
    public ApiResponse<List<SemesterResponse>> getAll() {
        return ApiResponse.success(
                "Semesters retrieved successfully",
                service.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<SemesterResponse> getById(
            @PathVariable Long id) {
        return ApiResponse.success(
                "Semester retrieved successfully",
                service.getById(id));
    }

    @GetMapping("/program/{programId}")
    public ApiResponse<List<SemesterResponse>> getByProgram(
            @PathVariable Long programId) {
        return ApiResponse.success(
                "Program semesters retrieved successfully",
                service.getByProgram(programId));
    }

    @GetMapping("/academic-year/{academicYearId}")
    public ApiResponse<List<SemesterResponse>> getByAcademicYear(
            @PathVariable Long academicYearId) {
        return ApiResponse.success(
                "Academic-year semesters retrieved successfully",
                service.getByAcademicYear(academicYearId));
    }

    @PutMapping("/{id}")
    public ApiResponse<SemesterResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SemesterRequest request) {
        return ApiResponse.success(
                "Semester updated successfully",
                service.update(id, request));
    }
}
