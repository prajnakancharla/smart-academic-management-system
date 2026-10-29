
package com.smartacademic.repository;

import com.smartacademic.entity.AssessmentComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentComponentRepository
        extends JpaRepository<AssessmentComponentEntity, Long> {

    List<AssessmentComponentEntity>
    findByScheme_IdOrderByIdAsc(Long schemeId);

    boolean existsByScheme_IdAndComponentCodeIgnoreCase(
            Long schemeId, String componentCode);

    boolean existsByScheme_IdAndComponentCodeIgnoreCaseAndIdNot(
            Long schemeId, String componentCode, Long id);

    
}
