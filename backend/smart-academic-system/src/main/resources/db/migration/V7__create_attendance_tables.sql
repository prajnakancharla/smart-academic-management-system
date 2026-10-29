
CREATE TABLE attendance_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    offering_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    class_hour INT NOT NULL,
    topic VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_attendance_session_offering
        FOREIGN KEY (offering_id)
        REFERENCES subject_offerings(id),

    CONSTRAINT fk_attendance_session_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES teachers(id),

    CONSTRAINT uq_attendance_session
        UNIQUE (offering_id, session_date, class_hour)
);

CREATE TABLE student_attendance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    remarks VARCHAR(255) NULL,
    marked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_student_attendance_session
        FOREIGN KEY (session_id)
        REFERENCES attendance_sessions(id),

    CONSTRAINT fk_student_attendance_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT uq_student_attendance
        UNIQUE (session_id, student_id)
);
