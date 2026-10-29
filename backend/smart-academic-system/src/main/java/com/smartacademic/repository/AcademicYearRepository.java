
package com.smartacademic.repository;

import com.smartacademic.entity.AcademicYearEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicYearRepository
        extends JpaRepository<AcademicYearEntity, Long> {

    boolean existsByLabelIgnoreCase(String label);

    boolean existsByLabelIgnoreCaseAndIdNot(
            String label, Long id);

    List<AcademicYearEntity> findByActiveTrueOrderByStartDateDesc();
}
