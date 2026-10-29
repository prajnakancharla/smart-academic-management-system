package com.smartacademic.repository;

import com.smartacademic.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository
        extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByRollNumberIgnoreCase(
            String rollNumber);

    boolean existsByRollNumberIgnoreCase(String rollNumber);

    boolean existsByRollNumberIgnoreCaseAndIdNot(
            String rollNumber, Long id);

    List<StudentEntity> findByActiveTrueOrderByRollNumberAsc();

    List<StudentEntity> findByProgram_IdOrderByRollNumberAsc(
            Long programId);

Optional<StudentEntity> findByUser_Username(String username);
}