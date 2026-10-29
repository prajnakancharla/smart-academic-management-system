
package com.smartacademic.repository;

import com.smartacademic.entity.AcademicProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicProgramRepository
        extends JpaRepository<AcademicProgramEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    List<AcademicProgramEntity> findByActiveTrueOrderByNameAsc();
}
