
package com.smartacademic.controller;

import com.smartacademic.dto.AssessmentRequest;
import com.smartacademic.dto.AssessmentResponse;
import com.smartacademic.dto.MarksEntryRequest;
import com.smartacademic.dto.StudentMarksResponse;
import com.smartacademic.service.MarksGradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/marks")
public class MarksGradeController {

    private final MarksGradeService marksGradeService;

    public MarksGradeController(MarksGradeService marksGradeService) {
        this.marksGradeService = marksGradeService;
    }

    @PostMapping("/assessments")
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentResponse createAssessment(
            Authentication authentication,
            @Valid @RequestBody AssessmentRequest request) {
        try {
            return marksGradeService.createAssessment(
                    authentication.getName(), request);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/assessments")
    public List<AssessmentResponse> getMyAssessments(
            Authentication authentication) {
        try {
            return marksGradeService.getMyAssessments(
                    authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/assessments/{assessmentId}/students")
    public StudentMarksResponse saveMarks(
            Authentication authentication,
            @PathVariable Long assessmentId,
            @Valid @RequestBody MarksEntryRequest request) {
        try {
            return marksGradeService.saveMarks(
                    authentication.getName(), assessmentId, request);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/assessments/{assessmentId}/students")
    public List<StudentMarksResponse> getAssessmentMarks(
            Authentication authentication,
            @PathVariable Long assessmentId) {
        try {
            return marksGradeService.getAssessmentMarks(
                    authentication.getName(), assessmentId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
