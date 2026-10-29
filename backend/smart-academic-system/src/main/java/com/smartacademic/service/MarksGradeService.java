
package com.smartacademic.service;

import com.smartacademic.dto.AssessmentRequest;
import com.smartacademic.dto.AssessmentResponse;
import com.smartacademic.dto.MarksEntryRequest;
import com.smartacademic.dto.StudentMarksResponse;
import com.smartacademic.entity.*;
import com.smartacademic.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MarksGradeService {

    private final AssessmentRepository assessmentRepository;
    private final StudentMarksRepository marksRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherAssignmentRepository assignmentRepository;
    private final SubjectOfferingRepository offeringRepository;
    private final StudentRepository studentRepository;

    public MarksGradeService(
            AssessmentRepository assessmentRepository,
            StudentMarksRepository marksRepository,
            TeacherRepository teacherRepository,
            TeacherAssignmentRepository assignmentRepository,
            SubjectOfferingRepository offeringRepository,
            StudentRepository studentRepository) {
        this.assessmentRepository = assessmentRepository;
        this.marksRepository = marksRepository;
        this.teacherRepository = teacherRepository;
        this.assignmentRepository = assignmentRepository;
        this.offeringRepository = offeringRepository;
        this.studentRepository = studentRepository;
    }

    private TeacherEntity getActiveTeacher(String username) {
        return teacherRepository.findByUser_Username(username)
                .filter(TeacherEntity::isActive)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Active teacher not found"));
    }

    @Transactional
    public AssessmentResponse createAssessment(
            String username,
            AssessmentRequest request) {

        TeacherEntity teacher = getActiveTeacher(username);

        if (request.getOfferingId() == null) {
            throw new IllegalArgumentException(
                    "Offering ID is required");
        }

        if (request.getAssessmentDate() == null
                || request.getAssessmentDate().isAfter(
                        java.time.LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Assessment date is required and cannot be in the future");
        }

        if (request.getAssessmentType() == null) {
            throw new IllegalArgumentException(
                    "Assessment type is required");
        }

        if (request.getTitle() == null
                || request.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Assessment title is required");
        }

        if (request.getMaxMarks() == null
                || request.getMaxMarks().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Maximum marks must be greater than zero");
        }

        if (request.getPassMarks() == null
                || request.getPassMarks().compareTo(BigDecimal.ZERO) < 0
                || request.getPassMarks().compareTo(
                        request.getMaxMarks()) > 0) {
            throw new IllegalArgumentException(
                    "Pass marks must be between zero and maximum marks");
        }

        if (!assignmentRepository.existsByTeacher_IdAndOffering_Id(
                teacher.getId(), request.getOfferingId())) {
            throw new IllegalArgumentException(
                    "You are not assigned to this subject offering");
        }

        SubjectOfferingEntity offering = offeringRepository
                .findById(request.getOfferingId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Subject offering not found"));

        AssessmentEntity assessment = new AssessmentEntity();
        assessment.setOffering(offering);
        assessment.setTeacher(teacher);
        assessment.setTitle(request.getTitle().trim());
        assessment.setAssessmentType(request.getAssessmentType());
        assessment.setAssessmentDate(request.getAssessmentDate());
        assessment.setMaxMarks(request.getMaxMarks());
        assessment.setPassMarks(request.getPassMarks());

        AssessmentEntity saved =
                assessmentRepository.save(assessment);

        return toAssessmentResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AssessmentResponse> getMyAssessments(
            String username) {

        TeacherEntity teacher = getActiveTeacher(username);

        return assessmentRepository
                .findByTeacher_IdOrderByAssessmentDateDesc(
                        teacher.getId())
                .stream()
                .map(this::toAssessmentResponse)
                .toList();
    }

    @Transactional
    public StudentMarksResponse saveMarks(
            String username,
            Long assessmentId,
            MarksEntryRequest request) {

        TeacherEntity teacher = getActiveTeacher(username);

        AssessmentEntity assessment = assessmentRepository
                .findByIdAndTeacher_Id(assessmentId, teacher.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Assessment not found for this teacher"));

        if (request.getStudentId() == null) {
            throw new IllegalArgumentException(
                    "Student ID is required");
        }

        if (request.getMarksObtained() == null
                || request.getMarksObtained()
                        .compareTo(BigDecimal.ZERO) < 0
                || request.getMarksObtained()
                        .compareTo(assessment.getMaxMarks()) > 0) {
            throw new IllegalArgumentException(
                    "Marks must be between zero and maximum marks");
        }

        StudentEntity student = studentRepository
                .findById(request.getStudentId())
                .filter(StudentEntity::isActive)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Active student not found"));

        StudentMarksEntity marks = marksRepository
                .findByAssessment_IdAndStudent_Id(
                        assessmentId, student.getId())
                .orElseGet(StudentMarksEntity::new);

        marks.setAssessment(assessment);
        marks.setStudent(student);
        marks.setMarksObtained(request.getMarksObtained());
        marks.setRemarks(request.getRemarks());

        StudentMarksEntity saved = marksRepository.save(marks);

        return toMarksResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StudentMarksResponse> getAssessmentMarks(
            String username,
            Long assessmentId) {

        TeacherEntity teacher = getActiveTeacher(username);

        assessmentRepository
                .findByIdAndTeacher_Id(assessmentId, teacher.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Assessment not found for this teacher"));

        return marksRepository
                .findByAssessment_IdOrderByStudent_RollNumberAsc(
                        assessmentId)
                .stream()
                .map(this::toMarksResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentMarksResponse> getMyResults(String username) {
        StudentEntity student = studentRepository
                .findByUser_Username(username)
                .filter(StudentEntity::isActive)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Active student not found"));

        return marksRepository
                .findByStudent_IdOrderByAssessment_AssessmentDateDesc(
                        student.getId())
                .stream()
                .map(this::toMarksResponse)
                .toList();
    }

    private AssessmentResponse toAssessmentResponse(
            AssessmentEntity assessment) {
        return new AssessmentResponse(
                assessment.getId(),
                assessment.getOffering().getId(),
                assessment.getTitle(),
                assessment.getAssessmentType(),
                assessment.getAssessmentDate(),
                assessment.getMaxMarks(),
                assessment.getPassMarks()
        );
    }

    private StudentMarksResponse toMarksResponse(
            StudentMarksEntity marks) {

        AssessmentEntity assessment = marks.getAssessment();
        BigDecimal obtained = marks.getMarksObtained();
        BigDecimal maximum = assessment.getMaxMarks();

        BigDecimal percentage = obtained
                .multiply(BigDecimal.valueOf(100))
                .divide(maximum, 2, RoundingMode.HALF_UP);

        String grade = calculateGrade(percentage);
        boolean passed = obtained.compareTo(
                assessment.getPassMarks()) >= 0;

        StudentEntity student = marks.getStudent();

        StudentMarksResponse response = new StudentMarksResponse(
                marks.getId(),
                assessment.getId(),
                student.getId(),
                student.getRollNumber(),
                student.getFirstName() + " " + student.getLastName(),
                obtained,
                maximum,
                percentage,
                grade,
                passed,
                marks.getRemarks()
        );
        response.setSubjectCode(assessment.getOffering().getSubject().getCode());
        response.setSubjectTitle(assessment.getOffering().getSubject().getTitle());
        response.setAssessmentTitle(assessment.getTitle());
        return response;
    }

    private String calculateGrade(BigDecimal percentage) {
        if (percentage.compareTo(new BigDecimal("90")) >= 0) return "A+";
        if (percentage.compareTo(new BigDecimal("80")) >= 0) return "A";
        if (percentage.compareTo(new BigDecimal("70")) >= 0) return "B";
        if (percentage.compareTo(new BigDecimal("60")) >= 0) return "C";
        if (percentage.compareTo(new BigDecimal("50")) >= 0) return "D";
        if (percentage.compareTo(new BigDecimal("40")) >= 0) return "E";
        return "F";
    }
}
