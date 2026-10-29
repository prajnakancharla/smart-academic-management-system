
package com.smartacademic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_attendance",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_student_attendance",
        columnNames = {"session_id", "student_id"}
    )
)
public class StudentAttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private AttendanceSessionEntity session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(length = 255)
    private String remarks;

    @Column(name = "marked_at", nullable = false, updatable = false)
    private LocalDateTime markedAt;

    @PrePersist
    protected void onCreate() {
        if (markedAt == null) {
            markedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public AttendanceSessionEntity getSession() {
        return session;
    }

    public void setSession(AttendanceSessionEntity session) {
        this.session = session;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }
}
