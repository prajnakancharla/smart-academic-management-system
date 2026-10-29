
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.AssessmentComponentRequest;
import com.smartacademic.dto.AssessmentComponentResponse;
import com.smartacademic.service.AssessmentComponentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/assessment-components")
@PreAuthorize("hasRole('ADMIN')")
public class AssessmentComponentController {

    private final AssessmentComponentService componentService;

    public AssessmentComponentController(
            AssessmentComponentService componentService) {
        this.componentService = componentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AssessmentComponentResponse>> create(
            @Valid @RequestBody AssessmentComponentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Assessment component created successfully",
                        componentService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssessmentComponentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                "Assessment components retrieved successfully",
                componentService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssessmentComponentResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment component retrieved successfully",
                componentService.getById(id)));
    }

    @GetMapping("/scheme/{schemeId}")
    public ResponseEntity<ApiResponse<List<AssessmentComponentResponse>>>
            getByScheme(@PathVariable Long schemeId) {

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment components retrieved successfully",
                componentService.getByScheme(schemeId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AssessmentComponentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AssessmentComponentRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment component updated successfully",
                componentService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        componentService.delete(id);

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment component deleted successfully", null));
    }

    @GetMapping("/scheme/{schemeId}/validate")
    public ResponseEntity<ApiResponse<Void>> validateSchemeWeights(
            @PathVariable Long schemeId) {

        componentService.validateSchemeWeights(schemeId);

        return ResponseEntity.ok(ApiResponse.success(
                "Assessment scheme weights total 100%",
                null));
    }
}
