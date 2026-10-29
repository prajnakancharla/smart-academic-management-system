
package com.smartacademic.repository;

import com.smartacademic.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository
        extends JpaRepository<DepartmentEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    List<DepartmentEntity> findByActiveTrueOrderByNameAsc();
}
