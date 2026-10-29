
package com.smartacademic.service;

import com.smartacademic.dto.AttendanceReportResponse;
import com.smartacademic.dto.StudentAttendanceSummaryResponse;
import com.smartacademic.dto.SubjectAttendanceSummaryResponse;
import com.smartacademic.entity.StudentEntity;
import com.smartacademic.entity.AttendanceSessionEntity;
import com.smartacademic.entity.AttendanceStatus;
import com.smartacademic.entity.TeacherEntity;
import com.smartacademic.repository.AttendanceSessionRepository;
import com.smartacademic.repository.StudentAttendanceRepository;
import com.smartacademic.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

@Service
public class AttendanceReportService {

    private final AttendanceSessionRepository sessionRepository;
    private final StudentAttendanceRepository attendanceRepository;
    private final TeacherRepository teacherRepository;
    private final com.smartacademic.repository.StudentRepository studentRepository;

    public AttendanceReportService(
            AttendanceSessionRepository sessionRepository,
            StudentAttendanceRepository attendanceRepository,
            TeacherRepository teacherRepository,
            com.smartacademic.repository.StudentRepository studentRepository) {
        this.sessionRepository = sessionRepository;
        this.attendanceRepository = attendanceRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceReportResponse> getMyAttendanceReport(
            String username) {

        TeacherEntity teacher = teacherRepository
                .findByUser_Username(username)
                .filter(TeacherEntity::isActive)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Active teacher not found"));

        List<AttendanceReportResponse> report = new ArrayList<>();

        List<AttendanceSessionEntity> sessions =
                sessionRepository.findByTeacher_IdOrderBySessionDateDesc(
                        teacher.getId());

        for (AttendanceSessionEntity session : sessions) {

            long total = attendanceRepository
                    .countBySession_Id(session.getId());

            long present = attendanceRepository
                    .countBySession_IdAndStatus(
                            session.getId(), AttendanceStatus.PRESENT);

            long absent = attendanceRepository
                    .countBySession_IdAndStatus(
                            session.getId(), AttendanceStatus.ABSENT);

            long late = attendanceRepository
                    .countBySession_IdAndStatus(
                            session.getId(), AttendanceStatus.LATE);

            long excused = attendanceRepository
                    .countBySession_IdAndStatus(
                            session.getId(), AttendanceStatus.EXCUSED);

            double percentage = total == 0
                    ? 0.0
                    : Math.round((present * 10000.0) / total) / 100.0;

            report.add(new AttendanceReportResponse(
                    session.getId(),
                    session.getSessionDate(),
                    session.getClassHour(),
                    total,
                    present,
                    absent,
                    late,
                    excused,
                    percentage
            ));
        }

        return report;
    }

    @Transactional(readOnly = true)
    public StudentAttendanceSummaryResponse getMyStudentAttendance(String username) {
        StudentEntity student = studentRepository.findByUser_Username(username)
                .filter(StudentEntity::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Active student not found"));

        long total = attendanceRepository.countByStudent_Id(student.getId());
        long present = attendanceRepository.countByStudent_IdAndStatus(student.getId(), AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStudent_IdAndStatus(student.getId(), AttendanceStatus.ABSENT);
        long late = attendanceRepository.countByStudent_IdAndStatus(student.getId(), AttendanceStatus.LATE);
        long excused = attendanceRepository.countByStudent_IdAndStatus(student.getId(), AttendanceStatus.EXCUSED);
        double percentage = total == 0 ? 0.0 : Math.round((present * 10000.0) / total) / 100.0;

        Map<Long, List<com.smartacademic.entity.StudentAttendanceEntity>> bySubject =
                attendanceRepository.findByStudent_Id(student.getId()).stream()
                        .collect(java.util.stream.Collectors.groupingBy(
                                item -> item.getSession().getOffering().getSubject().getId(),
                                LinkedHashMap::new,
                                java.util.stream.Collectors.toList()));

        List<SubjectAttendanceSummaryResponse> subjects = new ArrayList<>();
        bySubject.forEach((subjectId, entries) -> {
            long subjectTotal = entries.size();
            long subjectPresent = entries.stream().filter(item -> item.getStatus() == AttendanceStatus.PRESENT).count();
            long subjectAbsent = entries.stream().filter(item -> item.getStatus() == AttendanceStatus.ABSENT).count();
            long subjectLate = entries.stream().filter(item -> item.getStatus() == AttendanceStatus.LATE).count();
            long subjectExcused = entries.stream().filter(item -> item.getStatus() == AttendanceStatus.EXCUSED).count();
            var subject = entries.get(0).getSession().getOffering().getSubject();
            double subjectPercentage = subjectTotal == 0 ? 0.0
                    : Math.round((subjectPresent * 10000.0) / subjectTotal) / 100.0;
            subjects.add(new SubjectAttendanceSummaryResponse(
                    subjectId, subject.getCode(), subject.getTitle(), subjectTotal,
                    subjectPresent, subjectAbsent, subjectLate, subjectExcused, subjectPercentage));
        });

        return new StudentAttendanceSummaryResponse(
                total, present, absent, late, excused, percentage, subjects);
    }
}
