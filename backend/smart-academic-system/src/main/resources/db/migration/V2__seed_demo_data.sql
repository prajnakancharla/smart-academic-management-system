
-- V2: Initial demo data
-- Smart Academic Management System

-- 1. Roles
INSERT INTO roles (name) VALUES
    ('ADMIN'),
    ('TEACHER'),
    ('STUDENT');

-- 2. Departments
INSERT INTO departments (name, code) VALUES
    ('Computer Science and Engineering', 'CSE'),
    ('Business Administration', 'BUS');

-- 3. Academic programs
INSERT INTO academic_programs
    (department_id, name, code, duration_years, semesters_per_year)
VALUES
    (1, 'Bachelor of Technology', 'BTECH', 4, 2),
    (2, 'Bachelor of Business Administration', 'BBA', 3, 2),
    (2, 'Master of Business Administration', 'MBA', 2, 2);

-- 4. Academic year
INSERT INTO academic_years
    (label, start_date, end_date, active)
VALUES
    ('2026-2027', '2026-07-01', '2027-06-30', TRUE);

-- 5. Semesters
INSERT INTO semesters
    (program_id, academic_year_id, semester_number,
     start_date, end_date, status)
VALUES
    (1, 1, 1, '2026-07-01', '2026-12-31', 'ACTIVE'),
    (1, 1, 2, '2027-01-01', '2027-06-30', 'PLANNED'),
    (2, 1, 1, '2026-07-01', '2026-12-31', 'ACTIVE'),
    (2, 1, 2, '2027-01-01', '2027-06-30', 'PLANNED'),
    (3, 1, 1, '2026-07-01', '2026-12-31', 'ACTIVE'),
    (3, 1, 2, '2027-01-01', '2027-06-30', 'PLANNED');

-- 6. Subjects
INSERT INTO subjects
    (program_id, code, title, description, subject_type, default_credits)
VALUES
    (1, 'BTECH-CS101', 'Programming Fundamentals',
     'Introduction to programming concepts', 'THEORY', 4.00),
    (1, 'BTECH-CS102', 'Programming Laboratory',
     'Practical programming exercises', 'LABORATORY', 2.00),
    (1, 'BTECH-CS103', 'Database Management Systems',
     'Relational databases and SQL', 'THEORY', 4.00);

-- 7. Assessment schemes
INSERT INTO assessment_schemes (name, subject_type) VALUES
    ('Default Theory Scheme', 'THEORY'),
    ('Default Laboratory Scheme', 'LABORATORY');

-- 8. Grade boundaries
INSERT INTO grade_boundaries
    (grade, minimum_score, maximum_score, grade_points, passing)
VALUES
    ('O', 90.00, 100.00, 10.00, TRUE),
    ('A+', 80.00, 89.99, 9.00, TRUE),
    ('A', 70.00, 79.99, 8.00, TRUE),
    ('B+', 60.00, 69.99, 7.00, TRUE),
    ('B', 50.00, 59.99, 6.00, TRUE),
    ('C', 40.00, 49.99, 5.00, TRUE),
    ('F', 0.00, 39.99, 0.00, FALSE);
