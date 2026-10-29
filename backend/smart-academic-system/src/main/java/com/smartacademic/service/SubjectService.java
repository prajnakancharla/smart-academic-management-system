
package com.smartacademic.service;

import com.smartacademic.dto.SubjectRequest;
import com.smartacademic.dto.SubjectResponse;
import com.smartacademic.entity.AcademicProgramEntity;
import com.smartacademic.entity.SubjectEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.AcademicProgramRepository;
import com.smartacademic.repository.SubjectRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final AcademicProgramRepository programRepository;

    public SubjectService(
            SubjectRepository subjectRepository,
            AcademicProgramRepository programRepository) {
        this.subjectRepository = subjectRepository;
        this.programRepository = programRepository;
    }

    public SubjectResponse create(SubjectRequest request) {
        String code = normalizeCode(request.code());

        if (subjectRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException(
                    "A subject with this code already exists");
        }

        AcademicProgramEntity program = findActiveProgram(
                request.programId());

        SubjectEntity subject = new SubjectEntity();
        subject.setProgram(program);
        subject.setCode(code);
        subject.setTitle(request.title().trim());
        subject.setDescription(request.description());
        subject.setSubjectType(
                request.subjectType().trim().toUpperCase(Locale.ROOT));
        subject.setDefaultCredits(request.defaultCredits());
        subject.setActive(true);

        return toResponse(subjectRepository.save(subject));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAll() {
        return subjectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getActive() {
        return subjectRepository.findByActiveTrueOrderByCodeAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse getById(Long id) {
        return toResponse(findSubject(id));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getByProgram(Long programId) {
        return subjectRepository
                .findByProgram_IdOrderByCodeAsc(programId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SubjectResponse update(Long id, SubjectRequest request) {
        SubjectEntity subject = findSubject(id);
        String code = normalizeCode(request.code());

        if (subjectRepository.existsByCodeIgnoreCaseAndIdNot(
                code, id)) {
            throw new IllegalArgumentException(
                    "Another subject already uses this code");
        }

        AcademicProgramEntity program = findActiveProgram(
                request.programId());

        subject.setProgram(program);
        subject.setCode(code);
        subject.setTitle(request.title().trim());
        subject.setDescription(request.description());
        subject.setSubjectType(
                request.subjectType().trim().toUpperCase(Locale.ROOT));
        subject.setDefaultCredits(request.defaultCredits());

        return toResponse(subjectRepository.save(subject));
    }

    public SubjectResponse deactivate(Long id) {
        SubjectEntity subject = findSubject(id);
        subject.setActive(false);

        return toResponse(subjectRepository.save(subject));
    }

    private SubjectEntity findSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with ID: " + id));
    }

    private AcademicProgramEntity findActiveProgram(Long programId) {
        AcademicProgramEntity program = programRepository
                .findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic program not found with ID: " + programId));

        if (!program.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a subject to an inactive program");
        }

        return program;
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private SubjectResponse toResponse(SubjectEntity subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getProgram().getId(),
                subject.getProgram().getName(),
                subject.getCode(),
                subject.getTitle(),
                subject.getDescription(),
                subject.getSubjectType(),
                subject.getDefaultCredits(),
                subject.isActive()
        );
    }
}
