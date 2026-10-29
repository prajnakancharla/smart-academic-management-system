
package com.smartacademic.service;

import com.smartacademic.dto.DepartmentRequest;
import com.smartacademic.dto.DepartmentResponse;
import com.smartacademic.entity.DepartmentEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.DepartmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {

        String name = request.name().trim();
        String code = normalizeCode(request.code());

        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "A department with this name already exists");
        }

        if (departmentRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException(
                    "A department with this code already exists");
        }

        DepartmentEntity department = new DepartmentEntity();
        department.setName(name);
        department.setCode(code);
        department.setActive(true);

        return toResponse(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {

        return departmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getActive() {

        return departmentRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {

        return toResponse(findDepartment(id));
    }

    @Transactional
    public DepartmentResponse update(
            Long id, DepartmentRequest request) {

        DepartmentEntity department = findDepartment(id);

        String name = request.name().trim();
        String code = normalizeCode(request.code());

        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(
                name, id)) {
            throw new IllegalArgumentException(
                    "Another department already uses this name");
        }

        if (departmentRepository.existsByCodeIgnoreCaseAndIdNot(
                code, id)) {
            throw new IllegalArgumentException(
                    "Another department already uses this code");
        }

        department.setName(name);
        department.setCode(code);

        return toResponse(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentResponse deactivate(Long id) {

        DepartmentEntity department = findDepartment(id);

        if (!department.isActive()) {
            throw new IllegalArgumentException(
                    "Department is already inactive");
        }

        department.setActive(false);

        return toResponse(departmentRepository.save(department));
    }

    private DepartmentEntity findDepartment(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with ID: " + id));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private DepartmentResponse toResponse(
            DepartmentEntity department) {

        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.isActive());
    }
}
