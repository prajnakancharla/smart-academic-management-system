
package com.smartacademic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "teacher_assignments",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_teacher_offering",
        columnNames = {"teacher_id", "offering_id"}
    )
)
public class TeacherAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherEntity teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private SubjectOfferingEntity offering;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public TeacherEntity getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherEntity teacher) {
        this.teacher = teacher;
    }

    public SubjectOfferingEntity getOffering() {
        return offering;
    }

    public void setOffering(SubjectOfferingEntity offering) {
        this.offering = offering;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
