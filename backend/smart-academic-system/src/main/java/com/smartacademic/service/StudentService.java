package com.smartacademic.service;

import com.smartacademic.dto.StudentRequest;
import com.smartacademic.dto.StudentResponse;
import com.smartacademic.entity.AcademicProgramEntity;
import com.smartacademic.entity.RoleEntity;
import com.smartacademic.entity.StudentEntity;
import com.smartacademic.entity.UserEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.AcademicProgramRepository;
import com.smartacademic.repository.RoleRepository;
import com.smartacademic.repository.StudentRepository;
import com.smartacademic.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class StudentService {

    private static final String INITIAL_PASSWORD = "s123";

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AcademicProgramRepository programRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository studentRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            AcademicProgramRepository programRepository,
            PasswordEncoder passwordEncoder) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.programRepository = programRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public StudentResponse create(StudentRequest request) {

        String username = normalizeUsername(request.username());
        String email = normalizeEmail(request.email());
        String rollNumber = normalizeRollNumber(request.rollNumber());

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        if (studentRepository.existsByRollNumberIgnoreCase(rollNumber)) {
            throw new IllegalArgumentException(
                    "Roll number already exists");
        }

        AcademicProgramEntity program =
                getActiveProgram(request.programId());

        validateStudentDetails(request, program);

        RoleEntity studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new IllegalStateException(
                        "STUDENT role is missing. Check the database seed data."));

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(INITIAL_PASSWORD));
        user.setEnabled(true);

        // Password change is optional, according to the chosen project policy.
        user.setMustChangePassword(false);
        user.setRoles(Set.of(studentRole));

        UserEntity savedUser = userRepository.save(user);

        StudentEntity student = new StudentEntity();
        student.setUser(savedUser);
        student.setRollNumber(rollNumber);
        student.setFirstName(request.firstName().trim());
        student.setLastName(request.lastName().trim());
        student.setPhone(normalizePhone(request.phone()));
        student.setProgram(program);
        student.setAdmissionDate(request.admissionDate());
        student.setCurrentYear(request.currentYear());
        student.setActive(true);

        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getActive() {
        return studentRepository.findByActiveTrueOrderByRollNumberAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        return toResponse(findStudent(id));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getByProgram(Long programId) {

        if (!programRepository.existsById(programId)) {
            throw new ResourceNotFoundException(
                    "Academic program not found with ID: " + programId);
        }

        return studentRepository
                .findByProgram_IdOrderByRollNumberAsc(programId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentResponse update(Long id, StudentRequest request) {

        StudentEntity student = findStudent(id);
        UserEntity user = student.getUser();

        String username = normalizeUsername(request.username());
        String email = normalizeEmail(request.email());
        String rollNumber = normalizeRollNumber(request.rollNumber());

        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(
                username, user.getId())) {
            throw new IllegalArgumentException(
                    "Username already belongs to another account");
        }

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(
                email, user.getId())) {
            throw new IllegalArgumentException(
                    "Email already belongs to another account");
        }

        if (studentRepository.existsByRollNumberIgnoreCaseAndIdNot(
                rollNumber, id)) {
            throw new IllegalArgumentException(
                    "Roll number already belongs to another student");
        }

        AcademicProgramEntity program =
                getActiveProgram(request.programId());

        validateStudentDetails(request, program);

        user.setUsername(username);
        user.setEmail(email);

        student.setRollNumber(rollNumber);
        student.setFirstName(request.firstName().trim());
        student.setLastName(request.lastName().trim());
        student.setPhone(normalizePhone(request.phone()));
        student.setProgram(program);
        student.setAdmissionDate(request.admissionDate());
        student.setCurrentYear(request.currentYear());

        return toResponse(studentRepository.save(student));
    }

    public StudentResponse deactivate(Long id) {

        StudentEntity student = findStudent(id);

        student.setActive(false);
        student.getUser().setEnabled(false);

        return toResponse(studentRepository.save(student));
    }

    private StudentEntity findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id));
    }

    private AcademicProgramEntity getActiveProgram(Long programId) {
        AcademicProgramEntity program =
                programRepository.findById(programId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Academic program not found with ID: "
                                        + programId));

        if (!program.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a student to an inactive program");
        }

        return program;
    }

    private void validateStudentDetails(
            StudentRequest request,
            AcademicProgramEntity program) {

        if (request.admissionDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Admission date cannot be in the future");
        }

        if (request.currentYear() > program.getDurationYears()) {
            throw new IllegalArgumentException(
                    "Current year cannot exceed the program duration of "
                            + program.getDurationYears() + " years");
        }
    }

    private String normalizeUsername(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeEmail(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRollNumber(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizePhone(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private StudentResponse toResponse(StudentEntity student) {

        AcademicProgramEntity program = student.getProgram();

        int expectedCompletionYear =
                student.getAdmissionDate().getYear()
                        + program.getDurationYears();

        return new StudentResponse(
                student.getId(),
                student.getUser().getId(),
                student.getUser().getUsername(),
                student.getUser().getEmail(),
                student.getRollNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getPhone(),
                program.getId(),
                program.getName(),
                program.getDepartment().getName(),
                student.getAdmissionDate(),
                student.getCurrentYear(),
                program.getDurationYears(),
                expectedCompletionYear,
                student.isActive()
        );
    }
}