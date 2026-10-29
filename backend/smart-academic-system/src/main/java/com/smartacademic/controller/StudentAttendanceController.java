package com.smartacademic.controller;

import com.smartacademic.dto.StudentAttendanceSummaryResponse;
import com.smartacademic.service.AttendanceReportService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/attendance")
public class StudentAttendanceController {

    private final AttendanceReportService attendanceReportService;

    public StudentAttendanceController(AttendanceReportService attendanceReportService) {
        this.attendanceReportService = attendanceReportService;
    }

    @GetMapping
    public StudentAttendanceSummaryResponse getMyAttendance(Authentication authentication) {
        return attendanceReportService.getMyStudentAttendance(authentication.getName());
    }
}
