
package com.smartacademic.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "academic_programs")
public class AcademicProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "duration_years", nullable = false)
    private int durationYears;

    @Column(name = "semesters_per_year", nullable = false)
    private int semestersPerYear;

    @Column(nullable = false)
    private boolean active = true;

    public AcademicProgramEntity() {
    }

    public Long getId() {
        return id;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentEntity department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(int durationYears) {
        this.durationYears = durationYears;
    }

    public int getSemestersPerYear() {
        return semestersPerYear;
    }

    public void setSemestersPerYear(int semestersPerYear) {
        this.semestersPerYear = semestersPerYear;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
