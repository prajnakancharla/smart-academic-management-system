
package com.smartacademic.repository;

import com.smartacademic.entity.TeacherAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherAssignmentRepository
        extends JpaRepository<TeacherAssignmentEntity, Long> {

    boolean existsByTeacher_IdAndOffering_Id(
            Long teacherId,
            Long offeringId
    );

    List<TeacherAssignmentEntity> findByTeacher_IdOrderByAssignedAtDesc(
            Long teacherId
    );

    List<TeacherAssignmentEntity> findByOffering_IdOrderByAssignedAtDesc(
            Long offeringId
    );
}
