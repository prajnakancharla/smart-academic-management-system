
package com.smartacademic.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "semesters")
public class SemesterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private AcademicProgramEntity program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYearEntity academicYear;

    @Column(name = "semester_number", nullable = false)
    private int semesterNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SemesterStatus status = SemesterStatus.PLANNED;

    public SemesterEntity() {
    }

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

    public AcademicYearEntity getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYearEntity academicYear) {
        this.academicYear = academicYear;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SemesterStatus getStatus() {
        return status;
    }

    public void setStatus(SemesterStatus status) {
        this.status = status;
    }
}
