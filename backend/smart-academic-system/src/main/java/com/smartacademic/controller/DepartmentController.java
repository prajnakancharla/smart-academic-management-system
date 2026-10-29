
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.DepartmentRequest;
import com.smartacademic.dto.DepartmentResponse;
import com.smartacademic.service.DepartmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/departments")
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse department =
                departmentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Department created successfully", department));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Departments retrieved successfully",
                        departmentService.getAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>>
    getActive() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active departments retrieved successfully",
                        departmentService.getActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department retrieved successfully",
                        departmentService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department updated successfully",
                        departmentService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DepartmentResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department deactivated successfully",
                        departmentService.deactivate(id)));
    }
}
