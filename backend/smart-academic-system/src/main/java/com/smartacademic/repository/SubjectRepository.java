
package com.smartacademic.repository;

import com.smartacademic.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository
        extends JpaRepository<SubjectEntity, Long> {

    Optional<SubjectEntity> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code, Long id);

    List<SubjectEntity> findByActiveTrueOrderByCodeAsc();

    List<SubjectEntity> findByProgram_IdOrderByCodeAsc(
            Long programId);
}
