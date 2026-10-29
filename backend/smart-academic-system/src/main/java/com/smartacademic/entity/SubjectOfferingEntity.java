
package com.smartacademic.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "subject_offerings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_offering_subject_semester",
            columnNames = {"subject_id", "semester_id"}
        )
    }
)
public class SubjectOfferingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private SemesterEntity semester;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal credits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private OfferingStatus status = OfferingStatus.OPEN;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(SubjectEntity subject) {
        this.subject = subject;
    }

    public SemesterEntity getSemester() {
        return semester;
    }

    public void setSemester(SemesterEntity semester) {
        this.semester = semester;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public OfferingStatus getStatus() {
        return status;
    }

    public void setStatus(OfferingStatus status) {
        this.status = status;
    }
}
