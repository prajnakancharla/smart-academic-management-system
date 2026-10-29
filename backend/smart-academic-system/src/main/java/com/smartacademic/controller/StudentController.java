package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.StudentRequest;
import com.smartacademic.dto.StudentResponse;
import com.smartacademic.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/students")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StudentResponse> create(
            @Valid @RequestBody StudentRequest request) {

        return ApiResponse.success(
                "Student created successfully",
                service.create(request));
    }

    @GetMapping
    public ApiResponse<List<StudentResponse>> getAll() {
        return ApiResponse.success(
                "Students retrieved successfully",
                service.getAll());
    }

    @GetMapping("/active")
    public ApiResponse<List<StudentResponse>> getActive() {
        return ApiResponse.success(
                "Active students retrieved successfully",
                service.getActive());
    }

    @GetMapping("/program/{programId}")
    public ApiResponse<List<StudentResponse>> getByProgram(
            @PathVariable Long programId) {

        return ApiResponse.success(
                "Students retrieved for the selected program",
                service.getByProgram(programId));
    }

    @GetMapping("/{id}")
    public ApiResponse<StudentResponse> getById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Student retrieved successfully",
                service.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<StudentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {

        return ApiResponse.success(
                "Student updated successfully",
                service.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<StudentResponse> deactivate(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Student deactivated successfully",
                service.deactivate(id));
    }
}