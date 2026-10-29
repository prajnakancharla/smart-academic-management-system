
package com.smartacademic.repository;

import com.smartacademic.entity.SemesterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterRepository
        extends JpaRepository<SemesterEntity, Long> {

    boolean existsByProgram_IdAndAcademicYear_IdAndSemesterNumber(
            Long programId,
            Long academicYearId,
            int semesterNumber);

    boolean existsByProgram_IdAndAcademicYear_IdAndSemesterNumberAndIdNot(
            Long programId,
            Long academicYearId,
            int semesterNumber,
            Long id);

    List<SemesterEntity> findByProgram_IdOrderBySemesterNumberAsc(
            Long programId);

    List<SemesterEntity> findByAcademicYear_IdOrderByStartDateAsc(
            Long academicYearId);
}
