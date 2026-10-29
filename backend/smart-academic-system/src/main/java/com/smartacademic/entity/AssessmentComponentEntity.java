
package com.smartacademic.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "assessment_components",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_scheme_component",
        columnNames = {"scheme_id", "component_code"}
    )
)
public class AssessmentComponentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scheme_id", nullable = false)
    private AssessmentSchemeEntity scheme;

    @Column(name = "component_code", nullable = false, length = 30)
    private String componentCode;

    @Column(name = "component_name", nullable = false, length = 100)
    private String componentName;

    @Column(name = "maximum_marks", nullable = false, precision = 6, scale = 2)
    private BigDecimal maximumMarks;

    @Column(name = "weight_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPercentage;

    public AssessmentComponentEntity() {
    }

    public Long getId() {
        return id;
    }

    public AssessmentSchemeEntity getScheme() {
        return scheme;
    }

    public void setScheme(AssessmentSchemeEntity scheme) {
        this.scheme = scheme;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public BigDecimal getMaximumMarks() {
        return maximumMarks;
    }

    public void setMaximumMarks(BigDecimal maximumMarks) {
        this.maximumMarks = maximumMarks;
    }

    public BigDecimal getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(BigDecimal weightPercentage) {
        this.weightPercentage = weightPercentage;
    }
}
