
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.SubjectOfferingRequest;
import com.smartacademic.dto.SubjectOfferingResponse;
import com.smartacademic.entity.OfferingStatus;
import com.smartacademic.service.SubjectOfferingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subject-offerings")
@PreAuthorize("hasRole('ADMIN')")
public class SubjectOfferingController {

    private final SubjectOfferingService offeringService;

    public SubjectOfferingController(
            SubjectOfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectOfferingResponse>> create(
            @Valid @RequestBody SubjectOfferingRequest request) {

        SubjectOfferingResponse response =
                offeringService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Subject offering created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectOfferingResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject offerings retrieved successfully",
                        offeringService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectOfferingResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject offering retrieved successfully",
                        offeringService.getById(id)));
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<SubjectOfferingResponse>>>
    getBySemester(@PathVariable Long semesterId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Offerings retrieved for semester",
                        offeringService.getBySemester(semesterId)));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<SubjectOfferingResponse>>>
    getBySubject(@PathVariable Long subjectId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Offerings retrieved for subject",
                        offeringService.getBySubject(subjectId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectOfferingResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectOfferingRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject offering updated successfully",
                        offeringService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SubjectOfferingResponse>> changeStatus(
            @PathVariable Long id,
            @RequestParam OfferingStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject offering status updated successfully",
                        offeringService.changeStatus(id, status)));
    }
}
