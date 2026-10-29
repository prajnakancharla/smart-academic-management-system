
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.AcademicProgramRequest;
import com.smartacademic.dto.AcademicProgramResponse;
import com.smartacademic.service.AcademicProgramService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/programs")
@PreAuthorize("hasRole('ADMIN')")
public class AcademicProgramController {

    private final AcademicProgramService programService;

    public AcademicProgramController(
            AcademicProgramService programService) {
        this.programService = programService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AcademicProgramResponse>> create(
            @Valid @RequestBody AcademicProgramRequest request) {

        AcademicProgramResponse program =
                programService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Academic program created successfully", program));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AcademicProgramResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Academic programs retrieved successfully",
                        programService.getAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AcademicProgramResponse>>> getActive() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active academic programs retrieved successfully",
                        programService.getActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicProgramResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Academic program retrieved successfully",
                        programService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AcademicProgramResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicProgramRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Academic program updated successfully",
                        programService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AcademicProgramResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Academic program deactivated successfully",
                        programService.deactivate(id)));
    }
}
