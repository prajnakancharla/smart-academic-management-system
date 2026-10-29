
-- V1: Core database schema
-- Smart Academic Management System
-- MySQL 8+

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
        REFERENCES roles(id)
);

CREATE TABLE departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE academic_programs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    duration_years INT NOT NULL,
    semesters_per_year INT NOT NULL DEFAULT 2,
    CONSTRAINT fk_program_department FOREIGN KEY (department_id)
        REFERENCES departments(id),
    CHECK (duration_years > 0),
    CHECK (semesters_per_year > 0)
);

CREATE TABLE academic_years (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    label VARCHAR(20) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    CHECK (end_date >= start_date)
);

CREATE TABLE semesters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    program_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    semester_number INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    CONSTRAINT fk_semester_program FOREIGN KEY (program_id)
        REFERENCES academic_programs(id),
    CONSTRAINT fk_semester_year FOREIGN KEY (academic_year_id)
        REFERENCES academic_years(id),
    CONSTRAINT uq_semester_program_year_number
        UNIQUE (program_id, academic_year_id, semester_number),
    CHECK (semester_number > 0),
    CHECK (end_date >= start_date)
);

CREATE TABLE students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    roll_number VARCHAR(40) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    phone VARCHAR(25),
    program_id BIGINT NOT NULL,
    admission_date DATE NOT NULL,
    current_year INT NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_student_program FOREIGN KEY (program_id)
        REFERENCES academic_programs(id),
    CHECK (current_year > 0)
);

CREATE TABLE teachers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    employee_id VARCHAR(40) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    phone VARCHAR(25),
    department_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_teacher_department FOREIGN KEY (department_id)
        REFERENCES departments(id)
);

CREATE TABLE student_enrollments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id)
        REFERENCES students(id),
    CONSTRAINT fk_enrollment_semester FOREIGN KEY (semester_id)
        REFERENCES semesters(id),
    CONSTRAINT uq_student_semester UNIQUE (student_id, semester_id)
);

CREATE TABLE subjects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    program_id BIGINT NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(180) NOT NULL,
    description TEXT,
    subject_type VARCHAR(20) NOT NULL,
    default_credits DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_subject_program FOREIGN KEY (program_id)
        REFERENCES academic_programs(id),
    CHECK (default_credits > 0),
    CHECK (subject_type IN ('THEORY', 'LABORATORY'))
);

CREATE TABLE subject_offerings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    subject_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    credits DECIMAL(5,2) NOT NULL,
    status VARCHAR(25) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT fk_offering_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(id),
    CONSTRAINT fk_offering_semester FOREIGN KEY (semester_id)
        REFERENCES semesters(id),
    CONSTRAINT uq_subject_semester_offering
        UNIQUE (subject_id, semester_id),
    CHECK (credits > 0),
    CHECK (status IN ('OPEN', 'FROZEN', 'RESULTS_PUBLISHED'))
);

CREATE TABLE teacher_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    offering_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment_teacher FOREIGN KEY (teacher_id)
        REFERENCES teachers(id),
    CONSTRAINT fk_assignment_offering FOREIGN KEY (offering_id)
        REFERENCES subject_offerings(id),
    CONSTRAINT uq_teacher_offering UNIQUE (teacher_id, offering_id)
);

CREATE TABLE assessment_schemes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    subject_type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (subject_type IN ('THEORY', 'LABORATORY'))
);

CREATE TABLE assessment_components (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    scheme_id BIGINT NOT NULL,
    component_code VARCHAR(30) NOT NULL,
    component_name VARCHAR(100) NOT NULL,
    maximum_marks DECIMAL(6,2) NOT NULL,
    weight_percentage DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_component_scheme FOREIGN KEY (scheme_id)
        REFERENCES assessment_schemes(id),
    CONSTRAINT uq_scheme_component UNIQUE (scheme_id, component_code),
    CHECK (maximum_marks > 0),
    CHECK (weight_percentage >= 0 AND weight_percentage <= 100)
);

CREATE TABLE student_assessment_marks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    offering_id BIGINT NOT NULL,
    component_id BIGINT NOT NULL,
    marks_obtained DECIMAL(6,2) NOT NULL,
    entered_by BIGINT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id)
        REFERENCES students(id),
    CONSTRAINT fk_marks_offering FOREIGN KEY (offering_id)
        REFERENCES subject_offerings(id),
    CONSTRAINT fk_marks_component FOREIGN KEY (component_id)
        REFERENCES assessment_components(id),
    CONSTRAINT fk_marks_entered_by FOREIGN KEY (entered_by)
        REFERENCES users(id),
    CONSTRAINT uq_student_offering_component
        UNIQUE (student_id, offering_id, component_id),
    CHECK (marks_obtained >= 0)
);

CREATE TABLE attendance_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    offering_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    topic VARCHAR(200),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_session_offering FOREIGN KEY (offering_id)
        REFERENCES subject_offerings(id),
    CONSTRAINT fk_attendance_session_teacher FOREIGN KEY (created_by)
        REFERENCES users(id)
);

CREATE TABLE attendance_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(15) NOT NULL,
    CONSTRAINT fk_attendance_record_session FOREIGN KEY (session_id)
        REFERENCES attendance_sessions(id),
    CONSTRAINT fk_attendance_record_student FOREIGN KEY (student_id)
        REFERENCES students(id),
    CONSTRAINT uq_session_student UNIQUE (session_id, student_id),
    CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED'))
);

CREATE TABLE subject_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    offering_id BIGINT NOT NULL,
    total_score DECIMAL(5,2) NOT NULL,
    letter_grade VARCHAR(5) NOT NULL,
    grade_points DECIMAL(4,2) NOT NULL,
    passed BOOLEAN NOT NULL,
    published_at TIMESTAMP NULL,
    CONSTRAINT fk_subject_result_student FOREIGN KEY (student_id)
        REFERENCES students(id),
    CONSTRAINT fk_subject_result_offering FOREIGN KEY (offering_id)
        REFERENCES subject_offerings(id),
    CONSTRAINT uq_student_offering_result UNIQUE (student_id, offering_id),
    CHECK (total_score >= 0 AND total_score <= 100),
    CHECK (grade_points >= 0)
);

CREATE TABLE semester_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    sgpa DECIMAL(4,2),
    credits_earned DECIMAL(6,2),
    calculated_at TIMESTAMP NULL,
    published_at TIMESTAMP NULL,
    CONSTRAINT fk_semester_result_student FOREIGN KEY (student_id)
        REFERENCES students(id),
    CONSTRAINT fk_semester_result_semester FOREIGN KEY (semester_id)
        REFERENCES semesters(id),
    CONSTRAINT uq_student_semester_result UNIQUE (student_id, semester_id),
    CHECK (sgpa IS NULL OR (sgpa >= 0 AND sgpa <= 10)),
    CHECK (credits_earned IS NULL OR credits_earned >= 0)
);

CREATE TABLE learning_resources (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    offering_id BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL UNIQUE,
    content_type VARCHAR(120),
    file_size BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_resource_offering FOREIGN KEY (offering_id)
        REFERENCES subject_offerings(id),
    CONSTRAINT fk_resource_uploader FOREIGN KEY (uploaded_by)
        REFERENCES users(id),
    CHECK (file_size >= 0)
);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    actor_user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NULL,
    reason VARCHAR(500),
    details JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id)
        REFERENCES users(id)
);

CREATE TABLE grade_boundaries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    grade VARCHAR(5) NOT NULL UNIQUE,
    minimum_score DECIMAL(5,2) NOT NULL,
    maximum_score DECIMAL(5,2) NOT NULL,
    grade_points DECIMAL(4,2) NOT NULL,
    passing BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (minimum_score >= 0 AND minimum_score <= 100),
    CHECK (maximum_score >= minimum_score AND maximum_score <= 100),
    CHECK (grade_points >= 0)
);

CREATE INDEX idx_students_program ON students(program_id);
CREATE INDEX idx_enrollments_semester ON student_enrollments(semester_id);
CREATE INDEX idx_offerings_semester ON subject_offerings(semester_id);
CREATE INDEX idx_assignments_teacher ON teacher_assignments(teacher_id);
CREATE INDEX idx_marks_student_offering
    ON student_assessment_marks(student_id, offering_id);
CREATE INDEX idx_attendance_session_date
    ON attendance_sessions(session_date);
CREATE INDEX idx_attendance_student ON attendance_records(student_id);
CREATE INDEX idx_subject_results_student ON subject_results(student_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);
