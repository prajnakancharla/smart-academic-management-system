
package com.smartacademic.service;

import com.smartacademic.entity.AttendanceSessionEntity;
import com.smartacademic.entity.TeacherAssignmentEntity;
import com.smartacademic.entity.TeacherEntity;
import com.smartacademic.repository.AttendanceSessionRepository;
import com.smartacademic.repository.TeacherAssignmentRepository;
import com.smartacademic.repository.TeacherRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import com.smartacademic.entity.AttendanceStatus;
import com.smartacademic.entity.StudentEntity;
import com.smartacademic.entity.StudentAttendanceEntity;
import com.smartacademic.repository.StudentAttendanceRepository;
import com.smartacademic.repository.StudentRepository;
import com.smartacademic.dto.AttendanceMarkRequest;
import com.smartacademic.dto.StudentAttendanceResponse;
@Service
public class AttendanceService {

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;
    private final TeacherRepository teacherRepository;

    private final StudentAttendanceRepository studentAttendanceRepository;
private final StudentRepository studentRepository;
    public AttendanceService(
            AttendanceSessionRepository attendanceSessionRepository,
            TeacherAssignmentRepository teacherAssignmentRepository,
            TeacherRepository teacherRepository,StudentAttendanceRepository studentAttendanceRepository,
StudentRepository studentRepository) {
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
        this.teacherRepository = teacherRepository;
        this.studentAttendanceRepository = studentAttendanceRepository;
this.studentRepository = studentRepository;
    }

    @Transactional
    public AttendanceSessionEntity createSession(
            String username,
            Long offeringId,
            LocalDate sessionDate,
            Integer classHour,
            String topic) {

        TeacherEntity teacher = teacherRepository
                .findByUser_Username(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Teacher account not found"));

        if (!teacher.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive teachers cannot create attendance sessions");
        }

        boolean assigned = teacherAssignmentRepository
                .existsByTeacher_IdAndOffering_Id(
                        teacher.getId(), offeringId);

        if (!assigned) {
            throw new IllegalArgumentException(
                    "You are not assigned to this subject offering");
        }

        if (sessionDate == null) {
            throw new IllegalArgumentException(
                    "Session date is required");
        }

        if (classHour == null || classHour < 1 || classHour > 12) {
            throw new IllegalArgumentException(
                    "Class hour must be between 1 and 12");
        }

        boolean duplicate = attendanceSessionRepository
                .existsByOffering_IdAndSessionDateAndClassHour(
                        offeringId, sessionDate, classHour);

        if (duplicate) {
            throw new IllegalArgumentException(
                    "An attendance session already exists for this offering, date, and class hour");
        }

        AttendanceSessionEntity session = new AttendanceSessionEntity();
        session.setOffering(
                teacherAssignmentRepository
                        .findByTeacher_IdOrderByAssignedAtDesc(teacher.getId())
                        .stream()
                        .map(TeacherAssignmentEntity::getOffering)
                        .filter(offering -> offering.getId().equals(offeringId))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Subject offering not found")));

        session.setTeacher(teacher);
        session.setSessionDate(sessionDate);
        session.setClassHour(classHour);
        session.setTopic(topic);

        return attendanceSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<AttendanceSessionEntity> getMySessions(String username) {

        TeacherEntity teacher = teacherRepository
                .findByUser_Username(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Teacher account not found"));

        return attendanceSessionRepository
                .findByTeacher_IdOrderBySessionDateDesc(teacher.getId());
    }

    @Transactional
public StudentAttendanceResponse markAttendance(
        String username,
        Long sessionId,
        AttendanceMarkRequest request) {

    TeacherEntity teacher = teacherRepository
            .findByUser_Username(username)
            .orElseThrow(() ->
                    new IllegalArgumentException("Teacher account not found"));

    AttendanceSessionEntity session = attendanceSessionRepository
            .findByIdAndTeacher_Id(sessionId, teacher.getId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Attendance session not found or access denied"));

    StudentEntity student = studentRepository
            .findById(request.getStudentId())
            .orElseThrow(() ->
                    new IllegalArgumentException("Student not found"));

    if (!student.isActive()) {
        throw new IllegalArgumentException(
                "Cannot mark attendance for an inactive student");
    }

    if (studentAttendanceRepository
            .existsBySession_IdAndStudent_Id(sessionId, student.getId())) {
        throw new IllegalArgumentException(
                "Attendance has already been marked for this student");
    }

    StudentAttendanceEntity attendance = new StudentAttendanceEntity();
    attendance.setSession(session);
    attendance.setStudent(student);
    attendance.setStatus(request.getStatus());
    attendance.setRemarks(request.getRemarks());

    StudentAttendanceEntity saved =
            studentAttendanceRepository.save(attendance);

    return new StudentAttendanceResponse(
            saved.getId(),
            session.getId(),
            student.getId(),
            student.getRollNumber(),
            student.getFirstName() + " " + student.getLastName(),
            saved.getStatus(),
            saved.getRemarks(),
            saved.getMarkedAt()
    );
}

@Transactional(readOnly = true)
public List<StudentAttendanceResponse> getSessionAttendance(
        String username,
        Long sessionId) {

    TeacherEntity teacher = teacherRepository
            .findByUser_Username(username)
            .orElseThrow(() ->
                    new IllegalArgumentException("Teacher account not found"));

    AttendanceSessionEntity session = attendanceSessionRepository
            .findByIdAndTeacher_Id(sessionId, teacher.getId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Attendance session not found or access denied"));

    return studentAttendanceRepository.findBySession_Id(session.getId())
            .stream()
            .map(attendance -> new StudentAttendanceResponse(
                    attendance.getId(),
                    session.getId(),
                    attendance.getStudent().getId(),
                    attendance.getStudent().getRollNumber(),
                    attendance.getStudent().getFirstName() + " "
                            + attendance.getStudent().getLastName(),
                    attendance.getStatus(),
                    attendance.getRemarks(),
                    attendance.getMarkedAt()
            ))
            .toList();
}
}
