package com.smartacademic.dto;

public record SubjectAttendanceSummaryResponse(
        Long subjectId,
        String subjectCode,
        String subjectTitle,
        long totalSessions,
        long presentCount,
        long absentCount,
        long lateCount,
        long excusedCount,
        double attendancePercentage
) {
}
