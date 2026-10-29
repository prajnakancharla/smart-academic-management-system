
package com.smartacademic.repository;

import com.smartacademic.entity.AssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository
        extends JpaRepository<AssessmentEntity, Long> {

    List<AssessmentEntity>
    findByTeacher_IdOrderByAssessmentDateDesc(Long teacherId);

    List<AssessmentEntity>
    findByTeacher_IdAndOffering_IdOrderByAssessmentDateDesc(
            Long teacherId,
            Long offeringId
    );

    Optional<AssessmentEntity> findByIdAndTeacher_Id(
            Long assessmentId,
            Long teacherId
    );
}
