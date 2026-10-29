
package com.smartacademic.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AssessmentMarkCheckRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssessmentMarkCheckRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByComponentId(Long componentId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_assessment_marks WHERE component_id = ?",
                Integer.class,
                componentId
        );

        return count != null && count > 0;
    }
}
