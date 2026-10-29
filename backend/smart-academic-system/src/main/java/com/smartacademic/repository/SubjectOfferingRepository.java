
package com.smartacademic.repository;

import com.smartacademic.entity.SubjectOfferingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectOfferingRepository
        extends JpaRepository<SubjectOfferingEntity, Long> {

    boolean existsBySubject_IdAndSemester_Id(
            Long subjectId, Long semesterId);

    List<SubjectOfferingEntity>
    findBySemester_IdOrderBySubject_CodeAsc(Long semesterId);

    List<SubjectOfferingEntity>
    findBySubject_IdOrderBySemester_SemesterNumberAsc(Long subjectId);
}
