
package com.smartacademic.repository;

import com.smartacademic.entity.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository
        extends JpaRepository<TeacherEntity, Long> {

    Optional<TeacherEntity> findByEmployeeIdIgnoreCase(
            String employeeId);

    boolean existsByEmployeeIdIgnoreCase(
            String employeeId);

    boolean existsByEmployeeIdIgnoreCaseAndIdNot(
            String employeeId, Long id);

    List<TeacherEntity> findByActiveTrueOrderByEmployeeIdAsc();

    List<TeacherEntity> findByDepartment_IdOrderByEmployeeIdAsc(
            Long departmentId);

      Optional<TeacherEntity> findByUser_Username(String username);  
}
