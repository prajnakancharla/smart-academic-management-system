
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.SubjectRequest;
import com.smartacademic.dto.SubjectResponse;
import com.smartacademic.service.SubjectService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subjects")
@PreAuthorize("hasRole('ADMIN')")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> create(
            @Valid @RequestBody SubjectRequest request) {

        SubjectResponse response = subjectService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Subject created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subjects retrieved successfully",
                        subjectService.getAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getActive() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active subjects retrieved successfully",
                        subjectService.getActive()));
    }

    @GetMapping("/program/{programId}")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getByProgram(
            @PathVariable Long programId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subjects retrieved for program",
                        subjectService.getByProgram(programId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject retrieved successfully",
                        subjectService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject updated successfully",
                        subjectService.update(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<SubjectResponse>> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subject deactivated successfully",
                        subjectService.deactivate(id)));
    }
}
