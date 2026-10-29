
package com.smartacademic.service;

import com.smartacademic.dto.AcademicProgramRequest;
import com.smartacademic.dto.AcademicProgramResponse;
import com.smartacademic.entity.AcademicProgramEntity;
import com.smartacademic.entity.DepartmentEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.AcademicProgramRepository;
import com.smartacademic.repository.DepartmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AcademicProgramService {

    private final AcademicProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;

    public AcademicProgramService(
            AcademicProgramRepository programRepository,
            DepartmentRepository departmentRepository) {
        this.programRepository = programRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public AcademicProgramResponse create(AcademicProgramRequest request) {
        String name = request.name().trim();
        String code = normalizeCode(request.code());

        validateProgramDetails(
                name, code, request.durationYears(),
                request.semestersPerYear());

        if (programRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "A program with this name already exists");
        }

        if (programRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException(
                    "A program with this code already exists");
        }

        DepartmentEntity department = findActiveDepartment(
                request.departmentId());

        AcademicProgramEntity program = new AcademicProgramEntity();
        program.setDepartment(department);
        program.setName(name);
        program.setCode(code);
        program.setDurationYears(request.durationYears());
        program.setSemestersPerYear(request.semestersPerYear());
        program.setActive(true);

        return toResponse(programRepository.save(program));
    }

    @Transactional(readOnly = true)
    public List<AcademicProgramResponse> getAll() {
        return programRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AcademicProgramResponse> getActive() {
        return programRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AcademicProgramResponse getById(Long id) {
        return toResponse(findProgram(id));
    }

    @Transactional
    public AcademicProgramResponse update(
            Long id, AcademicProgramRequest request) {
        AcademicProgramEntity program = findProgram(id);

        String name = request.name().trim();
        String code = normalizeCode(request.code());

        validateProgramDetails(
                name, code, request.durationYears(),
                request.semestersPerYear());

        if (programRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new IllegalArgumentException(
                    "Another program already uses this name");
        }

        if (programRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new IllegalArgumentException(
                    "Another program already uses this code");
        }

        DepartmentEntity department = findActiveDepartment(
                request.departmentId());

        program.setDepartment(department);
        program.setName(name);
        program.setCode(code);
        program.setDurationYears(request.durationYears());
        program.setSemestersPerYear(request.semestersPerYear());

        return toResponse(programRepository.save(program));
    }

    @Transactional
    public AcademicProgramResponse deactivate(Long id) {
        AcademicProgramEntity program = findProgram(id);

        if (!program.isActive()) {
            throw new IllegalArgumentException(
                    "Program is already inactive");
        }

        program.setActive(false);
        return toResponse(programRepository.save(program));
    }

    private AcademicProgramEntity findProgram(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic program not found with ID: " + id));
    }

    private DepartmentEntity findActiveDepartment(Long departmentId) {
        DepartmentEntity department = departmentRepository
                .findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with ID: " + departmentId));

        if (!department.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a program to an inactive department");
        }

        return department;
    }

    private void validateProgramDetails(
            String name,
            String code,
            int durationYears,
            int semestersPerYear) {

        if (name.isBlank()) {
            throw new IllegalArgumentException(
                    "Program name is required");
        }

        if (code.isBlank()) {
            throw new IllegalArgumentException(
                    "Program code is required");
        }

        if (durationYears < 1 || durationYears > 10) {
            throw new IllegalArgumentException(
                    "Program duration must be between 1 and 10 years");
        }

        if (semestersPerYear < 1 || semestersPerYear > 4) {
            throw new IllegalArgumentException(
                    "Semesters per year must be between 1 and 4");
        }
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private AcademicProgramResponse toResponse(
            AcademicProgramEntity program) {
        return new AcademicProgramResponse(
                program.getId(),
                program.getDepartment().getId(),
                program.getDepartment().getName(),
                program.getName(),
                program.getCode(),
                program.getDurationYears(),
                program.getSemestersPerYear(),
                program.getDurationYears()
                        * program.getSemestersPerYear(),
                program.isActive());
    }
}
