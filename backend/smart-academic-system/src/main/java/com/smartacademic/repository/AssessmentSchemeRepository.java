package com.smartacademic.repository;

import com.smartacademic.entity.AssessmentSchemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentSchemeRepository
        extends JpaRepository<AssessmentSchemeEntity, Long> {

    List<AssessmentSchemeEntity> findAllByOrderByNameAsc();

    List<AssessmentSchemeEntity> findByActiveTrueOrderByNameAsc();

    Optional<AssessmentSchemeEntity> findByNameIgnoreCaseAndSubjectTypeIgnoreCase(
            String name, String subjectType);

    boolean existsByNameIgnoreCaseAndSubjectTypeIgnoreCase(
            String name, String subjectType);

    boolean existsByNameIgnoreCaseAndSubjectTypeIgnoreCaseAndIdNot(
            String name, String subjectType, Long id);

        List<AssessmentSchemeEntity>
findBySubjectTypeIgnoreCaseAndActiveTrueOrderByNameAsc(
        String subjectType);
}