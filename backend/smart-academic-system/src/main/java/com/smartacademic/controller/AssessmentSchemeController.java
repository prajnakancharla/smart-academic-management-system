package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.AssessmentSchemeRequest;
import com.smartacademic.dto.AssessmentSchemeResponse;
import com.smartacademic.service.AssessmentSchemeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/assessment-schemes")
@PreAuthorize("hasRole('ADMIN')")
public class AssessmentSchemeController {

    private final AssessmentSchemeService schemeService;

    public AssessmentSchemeController(
            AssessmentSchemeService schemeService) {
        this.schemeService = schemeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AssessmentSchemeResponse>> create(
            @Valid @RequestBody AssessmentSchemeRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Assessment scheme created successfully",
                        schemeService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssessmentSchemeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                "Assessment schemes retrieved successfully",
                schemeService.getAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AssessmentSchemeResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(
                "Active assessment schemes retrieved successfully",
                schemeService.getActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssessmentSchemeResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment scheme retrieved successfully",
                schemeService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AssessmentSchemeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AssessmentSchemeRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment scheme updated successfully",
                schemeService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AssessmentSchemeResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment scheme deactivated successfully",
                schemeService.deactivate(id)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AssessmentSchemeResponse>> activate(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment scheme activated successfully",
                schemeService.activate(id)));
    }
}