
package com.smartacademic.dto;

import java.math.BigDecimal;

public class StudentMarksResponse {

    private Long id;
    private Long assessmentId;
    private Long studentId;
    private String rollNumber;
    private String studentName;
    private BigDecimal marksObtained;
    private BigDecimal maxMarks;
    private BigDecimal percentage;
    private String grade;
    private boolean passed;
    private String remarks;
    private String subjectCode;
    private String subjectTitle;
    private String assessmentTitle;

    public StudentMarksResponse(
            Long id,
            Long assessmentId,
            Long studentId,
            String rollNumber,
            String studentName,
            BigDecimal marksObtained,
            BigDecimal maxMarks,
            BigDecimal percentage,
            String grade,
            boolean passed,
            String remarks) {
        this.id = id;
        this.assessmentId = assessmentId;
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.grade = grade;
        this.passed = passed;
        this.remarks = remarks;
    }

    public Long getId() { return id; }
    public Long getAssessmentId() { return assessmentId; }
    public Long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public BigDecimal getMarksObtained() { return marksObtained; }
    public BigDecimal getMaxMarks() { return maxMarks; }
    public BigDecimal getPercentage() { return percentage; }
    public String getGrade() { return grade; }
    public boolean isPassed() { return passed; }
    public String getRemarks() { return remarks; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectTitle() { return subjectTitle; }
    public String getAssessmentTitle() { return assessmentTitle; }
    public void setSubjectCode(String value) { subjectCode = value; }
    public void setSubjectTitle(String value) { subjectTitle = value; }
    public void setAssessmentTitle(String value) { assessmentTitle = value; }
}
