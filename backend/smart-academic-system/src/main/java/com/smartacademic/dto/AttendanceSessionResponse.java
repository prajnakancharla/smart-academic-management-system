
package com.smartacademic.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceSessionResponse {

    private Long id;
    private Long offeringId;
    private Long teacherId;
    private LocalDate sessionDate;
    private Integer classHour;
    private String topic;
    private LocalDateTime createdAt;

    public AttendanceSessionResponse(
            Long id,
            Long offeringId,
            Long teacherId,
            LocalDate sessionDate,
            Integer classHour,
            String topic,
            LocalDateTime createdAt) {
        this.id = id;
        this.offeringId = offeringId;
        this.teacherId = teacherId;
        this.sessionDate = sessionDate;
        this.classHour = classHour;
        this.topic = topic;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getOfferingId() { return offeringId; }
    public Long getTeacherId() { return teacherId; }
    public LocalDate getSessionDate() { return sessionDate; }
    public Integer getClassHour() { return classHour; }
    public String getTopic() { return topic; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
