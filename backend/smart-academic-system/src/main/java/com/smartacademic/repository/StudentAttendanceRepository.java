
package com.smartacademic.repository;

import com.smartacademic.entity.AttendanceStatus;
import com.smartacademic.entity.StudentAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentAttendanceRepository
        extends JpaRepository<StudentAttendanceEntity, Long> {

    List<StudentAttendanceEntity> findBySession_Id(Long sessionId);

    List<StudentAttendanceEntity> findByStudent_Id(Long studentId);

    boolean existsBySession_IdAndStudent_Id(
            Long sessionId,
            Long studentId
    );

    long countByStudent_IdAndStatus(
            Long studentId,
            AttendanceStatus status
    );

    long countByStudent_Id(Long studentId);

    long countBySession_Id(Long sessionId);

    long countBySession_IdAndStatus(
            Long sessionId,
            AttendanceStatus status
    );

    @Query("""
        SELECT a.status, COUNT(a)
        FROM StudentAttendanceEntity a
        WHERE a.session.id = :sessionId
        GROUP BY a.status
        """)
    List<Object[]> countStatusesBySessionId(
            @Param("sessionId") Long sessionId
    );
}
