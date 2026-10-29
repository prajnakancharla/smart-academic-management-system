
CREATE TABLE assessments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    offering_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    assessment_type VARCHAR(30) NOT NULL,
    assessment_date DATE NOT NULL,
    max_marks DECIMAL(7,2) NOT NULL,
    pass_marks DECIMAL(7,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_assessments PRIMARY KEY (id),

    CONSTRAINT fk_assessments_offering
        FOREIGN KEY (offering_id)
        REFERENCES subject_offerings(id),

    CONSTRAINT fk_assessments_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES teachers(id),

    CONSTRAINT chk_assessment_max_marks
        CHECK (max_marks > 0),

    CONSTRAINT chk_assessment_pass_marks
        CHECK (pass_marks >= 0 AND pass_marks <= max_marks),

    INDEX idx_assessments_offering (offering_id),
    INDEX idx_assessments_teacher (teacher_id)
);

CREATE TABLE student_marks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    assessment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    marks_obtained DECIMAL(7,2) NOT NULL,
    remarks VARCHAR(255),
    entered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_student_marks PRIMARY KEY (id),

    CONSTRAINT fk_student_marks_assessment
        FOREIGN KEY (assessment_id)
        REFERENCES assessments(id),

    CONSTRAINT fk_student_marks_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT uq_student_marks_assessment_student
        UNIQUE (assessment_id, student_id),

    CONSTRAINT chk_student_marks_nonnegative
        CHECK (marks_obtained >= 0),

    INDEX idx_student_marks_student (student_id)
);
