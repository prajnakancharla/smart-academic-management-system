
package com.smartacademic.dto;

import com.smartacademic.entity.AssessmentType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AssessmentResponse {

    private Long id;
    private Long offeringId;
    private String title;
    private AssessmentType assessmentType;
    private LocalDate assessmentDate;
    private BigDecimal maxMarks;
    private BigDecimal passMarks;

    public AssessmentResponse(
            Long id,
            Long offeringId,
            String title,
            AssessmentType assessmentType,
            LocalDate assessmentDate,
            BigDecimal maxMarks,
            BigDecimal passMarks) {
        this.id = id;
        this.offeringId = offeringId;
        this.title = title;
        this.assessmentType = assessmentType;
        this.assessmentDate = assessmentDate;
        this.maxMarks = maxMarks;
        this.passMarks = passMarks;
    }

    public Long getId() { return id; }
    public Long getOfferingId() { return offeringId; }
    public String getTitle() { return title; }
    public AssessmentType getAssessmentType() { return assessmentType; }
    public LocalDate getAssessmentDate() { return assessmentDate; }
    public BigDecimal getMaxMarks() { return maxMarks; }
    public BigDecimal getPassMarks() { return passMarks; }
}
