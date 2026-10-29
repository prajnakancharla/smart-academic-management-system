package com.smartacademic.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "assessment_schemes")
public class AssessmentSchemeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "subject_type", nullable = false, length = 20)
    private String subjectType;

    @Column(nullable = false)
    private boolean active = true;

    public AssessmentSchemeEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}