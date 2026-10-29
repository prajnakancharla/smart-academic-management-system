
package com.smartacademic.repository;

import com.smartacademic.entity.AttendanceSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceSessionRepository
        extends JpaRepository<AttendanceSessionEntity, Long> {

    boolean existsByOffering_IdAndSessionDateAndClassHour(
            Long offeringId,
            LocalDate sessionDate,
            Integer classHour
    );

    List<AttendanceSessionEntity> findByTeacher_IdOrderBySessionDateDesc(
            Long teacherId
    );

    Optional<AttendanceSessionEntity> findByIdAndTeacher_Id(
            Long sessionId,
            Long teacherId
    );
}
