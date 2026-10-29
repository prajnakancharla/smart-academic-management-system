package com.smartacademic.dto;

public record StudentAttendanceSummaryResponse(
        long totalSessions,
        long presentCount,
        long absentCount,
        long lateCount,
        long excusedCount,
        double attendancePercentage,
        java.util.List<SubjectAttendanceSummaryResponse> subjects
) {
}
