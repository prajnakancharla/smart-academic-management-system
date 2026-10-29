
package com.smartacademic.dto;

import java.time.LocalDate;

public class AttendanceReportResponse {

    private Long sessionId;
    private LocalDate sessionDate;
    private Integer classHour;
    private long totalStudents;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private long excusedCount;
    private double attendancePercentage;

    public AttendanceReportResponse(
            Long sessionId,
            LocalDate sessionDate,
            Integer classHour,
            long totalStudents,
            long presentCount,
            long absentCount,
            long lateCount,
            long excusedCount,
            double attendancePercentage) {

        this.sessionId = sessionId;
        this.sessionDate = sessionDate;
        this.classHour = classHour;
        this.totalStudents = totalStudents;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.lateCount = lateCount;
        this.excusedCount = excusedCount;
        this.attendancePercentage = attendancePercentage;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public Integer getClassHour() {
        return classHour;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public long getLateCount() {
        return lateCount;
    }

    public long getExcusedCount() {
        return excusedCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }
}
