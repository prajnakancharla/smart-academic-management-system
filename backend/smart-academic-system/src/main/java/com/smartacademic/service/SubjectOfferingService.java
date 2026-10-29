
package com.smartacademic.service;

import com.smartacademic.dto.SubjectOfferingRequest;
import com.smartacademic.dto.SubjectOfferingResponse;
import com.smartacademic.entity.AcademicProgramEntity;
import com.smartacademic.entity.AcademicYearEntity;
import com.smartacademic.entity.OfferingStatus;
import com.smartacademic.entity.SemesterEntity;
import com.smartacademic.entity.SemesterStatus;
import com.smartacademic.entity.SubjectEntity;
import com.smartacademic.entity.SubjectOfferingEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.SemesterRepository;
import com.smartacademic.repository.SubjectOfferingRepository;
import com.smartacademic.repository.SubjectRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubjectOfferingService {

    private final SubjectOfferingRepository offeringRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;

    public SubjectOfferingService(
            SubjectOfferingRepository offeringRepository,
            SubjectRepository subjectRepository,
            SemesterRepository semesterRepository) {
        this.offeringRepository = offeringRepository;
        this.subjectRepository = subjectRepository;
        this.semesterRepository = semesterRepository;
    }

    public SubjectOfferingResponse create(SubjectOfferingRequest request) {
        SubjectEntity subject = findSubject(request.subjectId());
        SemesterEntity semester = findSemester(request.semesterId());

        validateOffering(subject, semester);

        if (offeringRepository.existsBySubject_IdAndSemester_Id(
                subject.getId(), semester.getId())) {
            throw new IllegalArgumentException(
                    "This subject already has an offering for this semester");
        }

        SubjectOfferingEntity offering = new SubjectOfferingEntity();
        offering.setSubject(subject);
        offering.setSemester(semester);
        offering.setCredits(request.credits());
        offering.setStatus(OfferingStatus.OPEN);

        return toResponse(offeringRepository.save(offering));
    }

    @Transactional(readOnly = true)
    public List<SubjectOfferingResponse> getAll() {
        return offeringRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectOfferingResponse getById(Long id) {
        return toResponse(findOffering(id));
    }

    @Transactional(readOnly = true)
    public List<SubjectOfferingResponse> getBySemester(Long semesterId) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new ResourceNotFoundException(
                    "Semester not found with ID: " + semesterId);
        }

        return offeringRepository
                .findBySemester_IdOrderBySubject_CodeAsc(semesterId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectOfferingResponse> getBySubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException(
                    "Subject not found with ID: " + subjectId);
        }

        return offeringRepository
                .findBySubject_IdOrderBySemester_SemesterNumberAsc(subjectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SubjectOfferingResponse update(Long id,
                                           SubjectOfferingRequest request) {
        SubjectOfferingEntity offering = findOffering(id);

        if (offering.getStatus() != OfferingStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only DRAFT offerings can be edited");
        }

        SubjectEntity subject = findSubject(request.subjectId());
        SemesterEntity semester = findSemester(request.semesterId());

        validateOffering(subject, semester);

        boolean duplicate = offeringRepository
                .existsBySubject_IdAndSemester_Id(
                        subject.getId(), semester.getId());

        boolean sameOffering = offering.getSubject().getId().equals(
                        subject.getId())
                && offering.getSemester().getId().equals(semester.getId());

        if (duplicate && !sameOffering) {
            throw new IllegalArgumentException(
                    "This subject already has an offering for this semester");
        }

        offering.setSubject(subject);
        offering.setSemester(semester);
        offering.setCredits(request.credits());

        return toResponse(offeringRepository.save(offering));
    }

    public SubjectOfferingResponse changeStatus(
            Long id, OfferingStatus newStatus) {

        SubjectOfferingEntity offering = findOffering(id);

        if (newStatus == null) {
            throw new IllegalArgumentException("Status is required");
        }

        OfferingStatus current = offering.getStatus();

        boolean validTransition = switch (current) {
            case DRAFT -> newStatus == OfferingStatus.OPEN;
            case OPEN -> newStatus == OfferingStatus.FROZEN;
            case FROZEN -> newStatus == OfferingStatus.OPEN
                    || newStatus == OfferingStatus.RESULTS_PUBLISHED;
            case RESULTS_PUBLISHED -> false;
        };

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid offering status transition from "
                            + current + " to " + newStatus);
        }

        offering.setStatus(newStatus);
        return toResponse(offeringRepository.save(offering));
    }

    private SubjectEntity findSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with ID: " + id));
    }

    private SemesterEntity findSemester(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semester not found with ID: " + id));
    }

    private SubjectOfferingEntity findOffering(Long id) {
        return offeringRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject offering not found with ID: " + id));
    }

    private void validateOffering(
            SubjectEntity subject, SemesterEntity semester) {

        if (!subject.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot offer an inactive subject");
        }

        if (!semester.getProgram().isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create an offering for an inactive program");
        }

        if (semester.getStatus() == SemesterStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Cannot create an offering for a completed semester");
        }

        AcademicProgramEntity subjectProgram = subject.getProgram();
        AcademicProgramEntity semesterProgram = semester.getProgram();

        if (!subjectProgram.getId().equals(semesterProgram.getId())) {
            throw new IllegalArgumentException(
                    "Subject and semester must belong to the same program");
        }

        AcademicYearEntity academicYear = semester.getAcademicYear();

        if (!academicYear.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create an offering for an inactive academic year");
        }
    }

    private SubjectOfferingResponse toResponse(
            SubjectOfferingEntity offering) {

        SemesterEntity semester = offering.getSemester();

        return new SubjectOfferingResponse(
                offering.getId(),
                offering.getSubject().getId(),
                offering.getSubject().getCode(),
                offering.getSubject().getTitle(),
                semester.getId(),
                semester.getSemesterNumber(),
                semester.getProgram().getId(),
                semester.getProgram().getName(),
                semester.getAcademicYear().getId(),
                semester.getAcademicYear().getLabel(),
                offering.getCredits(),
                offering.getStatus(),
                semester.getStartDate(),
                semester.getEndDate()
        );
    }
}
