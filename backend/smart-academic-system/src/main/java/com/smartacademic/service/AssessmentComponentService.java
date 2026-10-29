
package com.smartacademic.service;

import com.smartacademic.dto.AssessmentComponentRequest;
import com.smartacademic.dto.AssessmentComponentResponse;
import com.smartacademic.entity.AssessmentComponentEntity;
import com.smartacademic.entity.AssessmentSchemeEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.AssessmentComponentRepository;
import com.smartacademic.repository.AssessmentSchemeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;


@Service
@Transactional
public class AssessmentComponentService {

    private final AssessmentComponentRepository componentRepository;
    private final AssessmentSchemeRepository schemeRepository;

    public AssessmentComponentService(
            AssessmentComponentRepository componentRepository,
            AssessmentSchemeRepository schemeRepository) {
        this.componentRepository = componentRepository;
        this.schemeRepository = schemeRepository;
    }

    public AssessmentComponentResponse create(
            AssessmentComponentRequest request) {

        AssessmentSchemeEntity scheme = findActiveScheme(request.schemeId());
        String code = normalizeCode(request.componentCode());

        if (componentRepository.existsByScheme_IdAndComponentCodeIgnoreCase(
                scheme.getId(), code)) {
            throw new IllegalArgumentException(
                    "This component code already exists in the selected scheme");
        }

        AssessmentComponentEntity component = new AssessmentComponentEntity();
        component.setScheme(scheme);
        applyRequest(component, request, code);

        try {
            return toResponse(componentRepository.saveAndFlush(component));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Component could not be saved due to a database constraint");
        }
    }

    @Transactional(readOnly = true)
    public List<AssessmentComponentResponse> getAll() {
        return componentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssessmentComponentResponse getById(Long id) {
        return toResponse(findComponent(id));
    }

    @Transactional(readOnly = true)
    public List<AssessmentComponentResponse> getByScheme(Long schemeId) {
        if (!schemeRepository.existsById(schemeId)) {
            throw new ResourceNotFoundException(
                    "Assessment scheme not found with ID: " + schemeId);
        }

        return componentRepository.findByScheme_IdOrderByIdAsc(schemeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AssessmentComponentResponse update(
            Long id, AssessmentComponentRequest request) {

        AssessmentComponentEntity component = findComponent(id);
        AssessmentSchemeEntity scheme = findActiveScheme(request.schemeId());
        String code = normalizeCode(request.componentCode());

        if (componentRepository
        .existsByScheme_IdAndComponentCodeIgnoreCaseAndIdNot(
                scheme.getId(), code, component.getId())) {
    throw new IllegalArgumentException(
            "This component code already exists in the selected scheme");
}

        component.setScheme(scheme);
        applyRequest(component, request, code);

        try {
            return toResponse(componentRepository.saveAndFlush(component));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Component could not be updated due to a database constraint");
        }
    }

    public void delete(Long id) {
        AssessmentComponentEntity component = findComponent(id);
        componentRepository.delete(component);
    }

    private AssessmentSchemeEntity findActiveScheme(Long schemeId) {
        AssessmentSchemeEntity scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment scheme not found with ID: " + schemeId));

        if (!scheme.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a component to an inactive assessment scheme");
        }

        return scheme;
    }

    private AssessmentComponentEntity findComponent(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment component not found with ID: " + id));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private void applyRequest(
            AssessmentComponentEntity component,
            AssessmentComponentRequest request,
            String code) {

        component.setComponentCode(code);
        component.setComponentName(request.componentName().trim());
        component.setMaximumMarks(request.maximumMarks());
        component.setWeightPercentage(request.weightPercentage());
    }

    private AssessmentComponentResponse toResponse(
            AssessmentComponentEntity component) {

        AssessmentSchemeEntity scheme = component.getScheme();

        return new AssessmentComponentResponse(
                component.getId(),
                scheme.getId(),
                scheme.getName(),
                scheme.getSubjectType(),
                component.getComponentCode(),
                component.getComponentName(),
                component.getMaximumMarks(),
                component.getWeightPercentage()
        );
    }

    @Transactional(readOnly = true)
public void validateSchemeWeights(Long schemeId) {
    var components = componentRepository.findByScheme_IdOrderByIdAsc(schemeId);

    if (components.isEmpty()) {
        throw new IllegalArgumentException(
                "The assessment scheme must contain at least one component");
    }

    BigDecimal totalWeight = components.stream()
            .map(AssessmentComponentEntity::getWeightPercentage)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (totalWeight.compareTo(new BigDecimal("100.00")) != 0) {
        throw new IllegalArgumentException(
                "Assessment component weights must total 100%. Current total: "
                        + totalWeight);
    }
}

@Transactional(readOnly = true)
public void validateSchemeForSubjectType(String subjectType) {

    List<AssessmentSchemeEntity> schemes =
            schemeRepository
                    .findBySubjectTypeIgnoreCaseAndActiveTrueOrderByNameAsc(
                            subjectType);

    List<AssessmentSchemeEntity> validSchemes = schemes.stream()
            .filter(scheme -> {
                try {
                    validateSchemeWeights(scheme.getId());
                    return true;
                } catch (IllegalArgumentException ex) {
                    return false;
                }
            })
            .toList();

    if (validSchemes.isEmpty()) {
        throw new IllegalArgumentException(
                "No active assessment scheme with valid component weights "
                        + "was found for subject type: " + subjectType);
    }

    if (validSchemes.size() > 1) {
        throw new IllegalArgumentException(
                "Multiple valid assessment schemes found for subject type: "
                        + subjectType
                        + ". Keep only one active valid scheme for this type.");
    }
}
}
