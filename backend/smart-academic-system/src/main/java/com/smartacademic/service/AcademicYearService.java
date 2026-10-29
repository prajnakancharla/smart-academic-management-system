
package com.smartacademic.service;

import com.smartacademic.dto.AcademicYearRequest;
import com.smartacademic.dto.AcademicYearResponse;
import com.smartacademic.entity.AcademicYearEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AcademicYearService {

    private final AcademicYearRepository repository;

    public AcademicYearService(AcademicYearRepository repository) {
        this.repository = repository;
    }

    public AcademicYearResponse create(AcademicYearRequest request) {
        validateDates(request);

        if (repository.existsByLabelIgnoreCase(request.label().trim())) {
            throw new IllegalArgumentException(
                    "An academic year with this label already exists");
        }

        AcademicYearEntity entity = new AcademicYearEntity();
        entity.setLabel(request.label().trim());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setActive(true);

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> getActive() {
        return repository.findByActiveTrueOrderByStartDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AcademicYearResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public AcademicYearResponse update(
            Long id, AcademicYearRequest request) {

        validateDates(request);

        AcademicYearEntity entity = findById(id);
        String label = request.label().trim();

        if (repository.existsByLabelIgnoreCaseAndIdNot(label, id)) {
            throw new IllegalArgumentException(
                    "Another academic year already uses this label");
        }

        entity.setLabel(label);
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());

        return toResponse(repository.save(entity));
    }

    public AcademicYearResponse activate(Long id) {
        AcademicYearEntity entity = findById(id);
        entity.setActive(true);
        return toResponse(repository.save(entity));
    }

    public AcademicYearResponse deactivate(Long id) {
        AcademicYearEntity entity = findById(id);
        entity.setActive(false);
        return toResponse(repository.save(entity));
    }

    private void validateDates(AcademicYearRequest request) {
        if (request.startDate() != null
                && request.endDate() != null
                && !request.endDate().isAfter(request.startDate())) {
            throw new IllegalArgumentException(
                    "End date must be after start date");
        }
    }

    private AcademicYearEntity findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic year not found with ID: " + id));
    }

    private AcademicYearResponse toResponse(
            AcademicYearEntity entity) {

        return new AcademicYearResponse(
                entity.getId(),
                entity.getLabel(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isActive()
        );
    }
}
