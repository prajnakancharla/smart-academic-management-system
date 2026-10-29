
package com.smartacademic.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "subjects")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private AcademicProgramEntity program;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "subject_type", nullable = false, length = 20)
    private String subjectType;

    @Column(name = "default_credits", nullable = false,
            precision = 5, scale = 2)
    private BigDecimal defaultCredits;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcademicProgramEntity getProgram() {
        return program;
    }

    public void setProgram(AcademicProgramEntity program) {
        this.program = program;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public BigDecimal getDefaultCredits() {
        return defaultCredits;
    }

    public void setDefaultCredits(BigDecimal defaultCredits) {
        this.defaultCredits = defaultCredits;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
