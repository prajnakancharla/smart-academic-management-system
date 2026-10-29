
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.TeacherRequest;
import com.smartacademic.dto.TeacherResponse;
import com.smartacademic.service.TeacherService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TeacherResponse> create(
            @Valid @RequestBody TeacherRequest request) {

        return ApiResponse.success(
                "Teacher created successfully",
                teacherService.create(request));
    }

    @GetMapping
    public ApiResponse<List<TeacherResponse>> getAll() {
        return ApiResponse.success(
                "Teachers retrieved successfully",
                teacherService.getAll());
    }

    @GetMapping("/active")
    public ApiResponse<List<TeacherResponse>> getActive() {
        return ApiResponse.success(
                "Active teachers retrieved successfully",
                teacherService.getActive());
    }

    @GetMapping("/department/{departmentId}")
    public ApiResponse<List<TeacherResponse>> getByDepartment(
            @PathVariable Long departmentId) {

        return ApiResponse.success(
                "Teachers retrieved successfully",
                teacherService.getByDepartment(departmentId));
    }

    @GetMapping("/{id}")
    public ApiResponse<TeacherResponse> getById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Teacher retrieved successfully",
                teacherService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<TeacherResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequest request) {

        return ApiResponse.success(
                "Teacher updated successfully",
                teacherService.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<TeacherResponse> deactivate(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Teacher deactivated successfully",
                teacherService.deactivate(id));
    }
}
