
package com.smartacademic.repository;

import com.smartacademic.entity.StudentMarksEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentMarksRepository
        extends JpaRepository<StudentMarksEntity, Long> {

    List<StudentMarksEntity>
    findByAssessment_IdOrderByStudent_RollNumberAsc(
            Long assessmentId
    );

    Optional<StudentMarksEntity>
    findByAssessment_IdAndStudent_Id(
            Long assessmentId,
            Long studentId
    );

    List<StudentMarksEntity>
    findByStudent_IdOrderByAssessment_AssessmentDateDesc(
            Long studentId
    );

    boolean existsByAssessment_IdAndStudent_Id(
            Long assessmentId,
            Long studentId
    );
}
