
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.TeacherAssignmentRequest;
import com.smartacademic.dto.TeacherAssignmentResponse;
import com.smartacademic.service.TeacherAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teacher-assignments")
@PreAuthorize("hasRole('ADMIN')")
public class TeacherAssignmentController {

    private final TeacherAssignmentService assignmentService;

    public TeacherAssignmentController(
            TeacherAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeacherAssignmentResponse>> create(
            @Valid @RequestBody TeacherAssignmentRequest request) {

        TeacherAssignmentResponse response =
                assignmentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Teacher assigned successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherAssignmentResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teacher assignments retrieved successfully",
                        assignmentService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherAssignmentResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teacher assignment retrieved successfully",
                        assignmentService.getById(id)));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<TeacherAssignmentResponse>>>
            getByTeacher(@PathVariable Long teacherId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teacher assignments retrieved successfully",
                        assignmentService.getByTeacher(teacherId)));
    }

    @GetMapping("/offering/{offeringId}")
    public ResponseEntity<ApiResponse<List<TeacherAssignmentResponse>>>
            getByOffering(@PathVariable Long offeringId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teacher assignments retrieved successfully",
                        assignmentService.getByOffering(offeringId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        assignmentService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Teacher assignment deleted successfully", null));
    }
}
