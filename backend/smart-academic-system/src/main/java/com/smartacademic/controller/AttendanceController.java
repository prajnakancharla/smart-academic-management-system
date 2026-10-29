
package com.smartacademic.controller;

import com.smartacademic.dto.AttendanceSessionRequest;
import com.smartacademic.dto.AttendanceSessionResponse;
import com.smartacademic.entity.AttendanceSessionEntity;
import com.smartacademic.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.smartacademic.dto.AttendanceMarkRequest;
import com.smartacademic.dto.StudentAttendanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@RestController
@RequestMapping("/api/teacher/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/sessions")
    public AttendanceSessionResponse createSession(
            Authentication authentication,
            @Valid @RequestBody AttendanceSessionRequest request) {

        AttendanceSessionEntity session = attendanceService.createSession(
                authentication.getName(),
                request.getOfferingId(),
                request.getSessionDate(),
                request.getClassHour(),
                request.getTopic()
        );

        return toResponse(session);
    }

    @GetMapping("/sessions")
    public List<AttendanceSessionResponse> getMySessions(
            Authentication authentication) {

        return attendanceService.getMySessions(authentication.getName())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AttendanceSessionResponse toResponse(
            AttendanceSessionEntity session) {

        return new AttendanceSessionResponse(
                session.getId(),
                session.getOffering().getId(),
                session.getTeacher().getId(),
                session.getSessionDate(),
                session.getClassHour(),
                session.getTopic(),
                session.getCreatedAt()
        );
    }

    @PostMapping("/sessions/{sessionId}/students")
public StudentAttendanceResponse markAttendance(
        Authentication authentication,
        @PathVariable Long sessionId,
        @jakarta.validation.Valid
        @RequestBody AttendanceMarkRequest request) {

    try {
        return attendanceService.markAttendance(
                authentication.getName(),
                sessionId,
                request
        );
    } catch (IllegalArgumentException ex) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }
}

@GetMapping("/sessions/{sessionId}/students")
public List<StudentAttendanceResponse> getSessionAttendance(
        Authentication authentication,
        @PathVariable Long sessionId) {

    try {
        return attendanceService.getSessionAttendance(
                authentication.getName(),
                sessionId
        );
    } catch (IllegalArgumentException ex) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }
}
}
