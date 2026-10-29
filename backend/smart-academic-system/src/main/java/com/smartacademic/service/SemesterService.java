
package com.smartacademic.service;

import com.smartacademic.dto.SemesterRequest;
import com.smartacademic.dto.SemesterResponse;
import com.smartacademic.entity.AcademicProgramEntity;
import com.smartacademic.entity.AcademicYearEntity;
import com.smartacademic.entity.SemesterEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.AcademicProgramRepository;
import com.smartacademic.repository.AcademicYearRepository;
import com.smartacademic.repository.SemesterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SemesterService {

    private final SemesterRepository semesterRepository;
    private final AcademicProgramRepository programRepository;
    private final AcademicYearRepository academicYearRepository;

    public SemesterService(
            SemesterRepository semesterRepository,
            AcademicProgramRepository programRepository,
            AcademicYearRepository academicYearRepository) {
        this.semesterRepository = semesterRepository;
        this.programRepository = programRepository;
        this.academicYearRepository = academicYearRepository;
    }

    public SemesterResponse create(SemesterRequest request) {
        AcademicProgramEntity program =
                findProgram(request.programId());

        AcademicYearEntity academicYear =
                findAcademicYear(request.academicYearId());

        validateRequest(request, program, academicYear);

        if (semesterRepository
                .existsByProgram_IdAndAcademicYear_IdAndSemesterNumber(
                        program.getId(),
                        academicYear.getId(),
                        request.semesterNumber())) {
            throw new IllegalArgumentException(
                    "This semester number already exists for this program and academic year");
        }

        SemesterEntity entity = new SemesterEntity();
        entity.setProgram(program);
        entity.setAcademicYear(academicYear);
        entity.setSemesterNumber(request.semesterNumber());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setStatus(request.status());

        return toResponse(semesterRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<SemesterResponse> getAll() {
        return semesterRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SemesterResponse getById(Long id) {
        return toResponse(findSemester(id));
    }

    @Transactional(readOnly = true)
    public List<SemesterResponse> getByProgram(Long programId) {
        findProgram(programId);

        return semesterRepository
                .findByProgram_IdOrderBySemesterNumberAsc(programId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SemesterResponse> getByAcademicYear(Long academicYearId) {
        findAcademicYear(academicYearId);

        return semesterRepository
                .findByAcademicYear_IdOrderByStartDateAsc(academicYearId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SemesterResponse update(Long id, SemesterRequest request) {
        SemesterEntity entity = findSemester(id);

        AcademicProgramEntity program =
                findProgram(request.programId());

        AcademicYearEntity academicYear =
                findAcademicYear(request.academicYearId());

        validateRequest(request, program, academicYear);

        boolean duplicate =
                semesterRepository
                        .existsByProgram_IdAndAcademicYear_IdAndSemesterNumberAndIdNot(
                                program.getId(),
                                academicYear.getId(),
                                request.semesterNumber(),
                                id);

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Another semester already uses this number for the selected program and academic year");
        }

        entity.setProgram(program);
        entity.setAcademicYear(academicYear);
        entity.setSemesterNumber(request.semesterNumber());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setStatus(request.status());

        return toResponse(semesterRepository.save(entity));
    }

    private void validateRequest(
            SemesterRequest request,
            AcademicProgramEntity program,
            AcademicYearEntity academicYear) {

        if (!program.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create or update a semester for an inactive program");
        }

        if (!academicYear.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create or update a semester for an inactive academic year");
        }

        int maximumSemesters =
                program.getDurationYears() * program.getSemestersPerYear();

        if (request.semesterNumber() < 1
                || request.semesterNumber() > maximumSemesters) {
            throw new IllegalArgumentException(
                    "Semester number must be between 1 and "
                            + maximumSemesters
                            + " for the selected program");
        }

        if (!request.endDate().isAfter(request.startDate())) {
            throw new IllegalArgumentException(
                    "Semester end date must be after its start date");
        }

        if (request.startDate().isBefore(academicYear.getStartDate())
                || request.endDate().isAfter(academicYear.getEndDate())) {
            throw new IllegalArgumentException(
                    "Semester dates must fall within the selected academic year");
        }
    }

    private SemesterEntity findSemester(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semester not found with ID: " + id));
    }

    private AcademicProgramEntity findProgram(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic program not found with ID: " + id));
    }

    private AcademicYearEntity findAcademicYear(Long id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic year not found with ID: " + id));
    }

    private SemesterResponse toResponse(SemesterEntity entity) {
        return new SemesterResponse(
                entity.getId(),
                entity.getProgram().getId(),
                entity.getProgram().getName(),
                entity.getAcademicYear().getId(),
                entity.getAcademicYear().getLabel(),
                entity.getSemesterNumber(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus()
        );
    }
}
