
package com.smartacademic.dto;

import com.smartacademic.entity.AssessmentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AssessmentRequest {

    @NotNull
    private Long offeringId;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotNull
    private AssessmentType assessmentType;

    @NotNull
    private LocalDate assessmentDate;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal maxMarks;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal passMarks;

    public Long getOfferingId() { return offeringId; }
    public void setOfferingId(Long offeringId) { this.offeringId = offeringId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public AssessmentType getAssessmentType() { return assessmentType; }
    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public BigDecimal getMaxMarks() { return maxMarks; }
    public void setMaxMarks(BigDecimal maxMarks) { this.maxMarks = maxMarks; }

    public BigDecimal getPassMarks() { return passMarks; }
    public void setPassMarks(BigDecimal passMarks) { this.passMarks = passMarks; }
}
