
package com.smartacademic.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_marks",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_student_marks_assessment_student",
        columnNames = {"assessment_id", "student_id"}
    )
)
public class StudentMarksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private AssessmentEntity assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Column(name = "marks_obtained", nullable = false, precision = 7, scale = 2)
    private BigDecimal marksObtained;

    @Column(length = 255)
    private String remarks;

    @Column(name = "entered_at", nullable = false, updatable = false)
    private LocalDateTime enteredAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (enteredAt == null) {
            enteredAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AssessmentEntity getAssessment() {
        return assessment;
    }

    public void setAssessment(AssessmentEntity assessment) {
        this.assessment = assessment;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getEnteredAt() {
        return enteredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
