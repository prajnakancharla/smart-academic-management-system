
package com.smartacademic.dto;

import com.smartacademic.entity.AttendanceStatus;

import java.time.LocalDateTime;

public class StudentAttendanceResponse {

    private Long id;
    private Long sessionId;
    private Long studentId;
    private String rollNumber;
    private String studentName;
    private AttendanceStatus status;
    private String remarks;
    private LocalDateTime markedAt;

    public StudentAttendanceResponse(
            Long id,
            Long sessionId,
            Long studentId,
            String rollNumber,
            String studentName,
            AttendanceStatus status,
            String remarks,
            LocalDateTime markedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.status = status;
        this.remarks = remarks;
        this.markedAt = markedAt;
    }

    public Long getId() { return id; }
    public Long getSessionId() { return sessionId; }
    public Long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public AttendanceStatus getStatus() { return status; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getMarkedAt() { return markedAt; }
}
