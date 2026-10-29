
package com.smartacademic.controller;

import com.smartacademic.dto.StudentMarksResponse;
import com.smartacademic.service.MarksGradeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/student/results")
public class StudentResultsController {

    private final MarksGradeService marksGradeService;

    public StudentResultsController(
            MarksGradeService marksGradeService) {
        this.marksGradeService = marksGradeService;
    }

    @GetMapping
    public List<StudentMarksResponse> getMyResults(
            Authentication authentication) {
        try {
            return marksGradeService.getMyResults(
                    authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        }
    }
}
