package com.smartacademic.controller;

import com.smartacademic.dto.TeacherOfferingResponse;
import com.smartacademic.dto.StudentResponse;
import com.smartacademic.service.TeacherService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherOfferingController {

    private final TeacherService teacherService;

    public TeacherOfferingController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/subject-offerings")
    public List<TeacherOfferingResponse> getMySubjectOfferings(
            Authentication authentication) {

        return teacherService.getMySubjectOfferings(
                authentication.getName()
        );
    }

    @GetMapping("/subject-offerings/{offeringId}/students")
    public List<StudentResponse> getStudentsForOffering(
            Authentication authentication,
            @org.springframework.web.bind.annotation.PathVariable Long offeringId) {
        return teacherService.getStudentsForOffering(authentication.getName(), offeringId);
    }
}