package com.smartacademic.service;

import com.smartacademic.dto.AssessmentSchemeRequest;
import com.smartacademic.dto.AssessmentSchemeResponse;
import com.smartacademic.entity.AssessmentSchemeEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.AssessmentSchemeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class AssessmentSchemeService {

    private final AssessmentSchemeRepository schemeRepository;

    public AssessmentSchemeService(
            AssessmentSchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    public AssessmentSchemeResponse create(AssessmentSchemeRequest request) {

        String name = request.name().trim();
        String subjectType = normalizeSubjectType(request.subjectType());

        if (schemeRepository.existsByNameIgnoreCaseAndSubjectTypeIgnoreCase(
                name, subjectType)) {
            throw new IllegalArgumentException(
                    "An assessment scheme with this name and subject type already exists");
        }

        AssessmentSchemeEntity scheme = new AssessmentSchemeEntity();
        scheme.setName(name);
        scheme.setSubjectType(subjectType);
        scheme.setActive(true);

        try {
            return toResponse(schemeRepository.saveAndFlush(scheme));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "The assessment scheme could not be saved because of a database constraint");
        }
    }

    @Transactional(readOnly = true)
    public List<AssessmentSchemeResponse> getAll() {
        return schemeRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssessmentSchemeResponse> getActive() {
        return schemeRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssessmentSchemeResponse getById(Long id) {
        return toResponse(findScheme(id));
    }

    public AssessmentSchemeResponse update(
            Long id, AssessmentSchemeRequest request) {

        AssessmentSchemeEntity scheme = findScheme(id);

        String name = request.name().trim();
        String subjectType = normalizeSubjectType(request.subjectType());

        if (schemeRepository
                .existsByNameIgnoreCaseAndSubjectTypeIgnoreCaseAndIdNot(
                        name, subjectType, id)) {
            throw new IllegalArgumentException(
                    "Another assessment scheme already uses this name and subject type");
        }

        scheme.setName(name);
        scheme.setSubjectType(subjectType);

        try {
            return toResponse(schemeRepository.saveAndFlush(scheme));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "The assessment scheme could not be updated because of a database constraint");
        }
    }

    public AssessmentSchemeResponse deactivate(Long id) {
        AssessmentSchemeEntity scheme = findScheme(id);

        scheme.setActive(false);

        return toResponse(schemeRepository.save(scheme));
    }

    public AssessmentSchemeResponse activate(Long id) {
        AssessmentSchemeEntity scheme = findScheme(id);

        scheme.setActive(true);

        return toResponse(schemeRepository.save(scheme));
    }

    private AssessmentSchemeEntity findScheme(Long id) {
        return schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment scheme not found with ID: " + id));
    }

    private String normalizeSubjectType(String subjectType) {
        String normalized = subjectType.trim().toUpperCase(Locale.ROOT);

        if (!normalized.equals("THEORY")
                && !normalized.equals("LABORATORY")) {
            throw new IllegalArgumentException(
                    "Subject type must be THEORY or LABORATORY");
        }

        return normalized;
    }

    private AssessmentSchemeResponse toResponse(
            AssessmentSchemeEntity scheme) {

        return new AssessmentSchemeResponse(
                scheme.getId(),
                scheme.getName(),
                scheme.getSubjectType(),
                scheme.isActive()
        );
    }
}