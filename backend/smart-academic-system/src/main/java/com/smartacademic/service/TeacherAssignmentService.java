
package com.smartacademic.service;

import com.smartacademic.dto.TeacherAssignmentRequest;
import com.smartacademic.dto.TeacherAssignmentResponse;
import com.smartacademic.entity.OfferingStatus;
import com.smartacademic.entity.SubjectOfferingEntity;
import com.smartacademic.entity.TeacherAssignmentEntity;
import com.smartacademic.entity.TeacherEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.SubjectOfferingRepository;
import com.smartacademic.repository.TeacherAssignmentRepository;
import com.smartacademic.repository.TeacherRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeacherAssignmentService {

    private final TeacherAssignmentRepository assignmentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectOfferingRepository offeringRepository;

    public TeacherAssignmentService(
            TeacherAssignmentRepository assignmentRepository,
            TeacherRepository teacherRepository,
            SubjectOfferingRepository offeringRepository) {
        this.assignmentRepository = assignmentRepository;
        this.teacherRepository = teacherRepository;
        this.offeringRepository = offeringRepository;
    }

    public TeacherAssignmentResponse create(TeacherAssignmentRequest request) {

        TeacherEntity teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher not found"));

        if (!teacher.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign an inactive teacher");
        }

        SubjectOfferingEntity offering =
                offeringRepository.findById(request.offeringId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subject offering not found"));

        if (offering.getStatus() != OfferingStatus.OPEN) {
            throw new IllegalArgumentException(
                    "Teacher can only be assigned to an OPEN offering");
        }

        if (assignmentRepository.existsByTeacher_IdAndOffering_Id(
                teacher.getId(), offering.getId())) {
            throw new IllegalArgumentException(
                    "This teacher is already assigned to this offering");
        }

        TeacherAssignmentEntity assignment =
                new TeacherAssignmentEntity();
        assignment.setTeacher(teacher);
        assignment.setOffering(offering);

        try {
            return toResponse(assignmentRepository.saveAndFlush(assignment));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "This teacher assignment already exists");
        }
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignmentResponse> getAll() {
        return assignmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeacherAssignmentResponse getById(Long id) {
        return toResponse(findAssignment(id));
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignmentResponse> getByTeacher(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher not found");
        }

        return assignmentRepository
                .findByTeacher_IdOrderByAssignedAtDesc(teacherId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignmentResponse> getByOffering(Long offeringId) {
        if (!offeringRepository.existsById(offeringId)) {
            throw new ResourceNotFoundException("Subject offering not found");
        }

        return assignmentRepository
                .findByOffering_IdOrderByAssignedAtDesc(offeringId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        TeacherAssignmentEntity assignment = findAssignment(id);

        if (assignment.getOffering().getStatus()
                == OfferingStatus.RESULTS_PUBLISHED) {
            throw new IllegalArgumentException(
                    "Cannot remove an assignment after results are published");
        }

        assignmentRepository.delete(assignment);
    }

    private TeacherAssignmentEntity findAssignment(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher assignment not found"));
    }

    private TeacherAssignmentResponse toResponse(
            TeacherAssignmentEntity assignment) {

        TeacherEntity teacher = assignment.getTeacher();
        SubjectOfferingEntity offering = assignment.getOffering();

        return new TeacherAssignmentResponse(
                assignment.getId(),
                teacher.getId(),
                teacher.getEmployeeId(),
                teacher.getFirstName() + " " + teacher.getLastName(),
                offering.getId(),
                offering.getSubject().getId(),
                offering.getSubject().getCode(),
                offering.getSubject().getTitle(),
                offering.getSemester().getId(),
                offering.getSemester().getSemesterNumber(),
                offering.getCredits(),
                offering.getStatus().name(),
                offering.getSemester().getStartDate(),
                offering.getSemester().getEndDate(),
                assignment.getAssignedAt()
        );
    }
}
