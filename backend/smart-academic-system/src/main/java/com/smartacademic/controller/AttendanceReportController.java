
package com.smartacademic.controller;

import com.smartacademic.dto.AttendanceReportResponse;
import com.smartacademic.service.AttendanceReportService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/reports")
public class AttendanceReportController {

    private final AttendanceReportService reportService;

    public AttendanceReportController(
            AttendanceReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/attendance")
    public List<AttendanceReportResponse> getAttendanceReport(
            Authentication authentication) {
        try {
            return reportService.getMyAttendanceReport(
                    authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        }
    }
}
