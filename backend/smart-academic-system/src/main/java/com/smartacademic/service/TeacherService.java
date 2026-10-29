
package com.smartacademic.service;

import com.smartacademic.dto.TeacherRequest;
import com.smartacademic.dto.TeacherResponse;
import com.smartacademic.entity.DepartmentEntity;
import com.smartacademic.entity.RoleEntity;
import com.smartacademic.entity.TeacherEntity;
import com.smartacademic.entity.UserEntity;
import com.smartacademic.exception.ResourceNotFoundException;
import com.smartacademic.repository.DepartmentRepository;
import com.smartacademic.repository.RoleRepository;
import com.smartacademic.repository.TeacherRepository;
import com.smartacademic.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;


import com.smartacademic.dto.TeacherOfferingResponse;
import com.smartacademic.dto.StudentResponse;
import com.smartacademic.entity.TeacherAssignmentEntity;
import com.smartacademic.entity.SubjectOfferingEntity;
import com.smartacademic.entity.StudentEntity;
import com.smartacademic.repository.StudentRepository;
import com.smartacademic.repository.TeacherAssignmentRepository;
@Service
@Transactional
public class TeacherService {

    private static final String INITIAL_PASSWORD = "t123";

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherAssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    public TeacherService(
        TeacherRepository teacherRepository,
        UserRepository userRepository,
        RoleRepository roleRepository,
        DepartmentRepository departmentRepository,
        PasswordEncoder passwordEncoder,
        TeacherAssignmentRepository assignmentRepository,
        StudentRepository studentRepository) {

    this.teacherRepository = teacherRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.departmentRepository = departmentRepository;
    this.passwordEncoder = passwordEncoder;
    this.assignmentRepository = assignmentRepository;
    this.studentRepository = studentRepository;
}

    public TeacherResponse create(TeacherRequest request) {

        String username = normalize(request.username());
        String email = normalize(request.email());
        String employeeId = request.employeeId().trim().toUpperCase(Locale.ROOT);

        validateUnique(username, email, employeeId);

        DepartmentEntity department = departmentRepository
                .findById(request.departmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found"));

        if (!department.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a teacher to an inactive department");
        }

        RoleEntity teacherRole = roleRepository.findByName("TEACHER")
                .orElseThrow(() ->
                        new ResourceNotFoundException("TEACHER role not found"));

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(INITIAL_PASSWORD));
        user.setEnabled(true);
        user.setMustChangePassword(false);
        user.setRoles(Set.of(teacherRole));

        user = userRepository.save(user);

        TeacherEntity teacher = new TeacherEntity();
        teacher.setUser(user);
        teacher.setEmployeeId(employeeId);
        teacher.setFirstName(request.firstName().trim());
        teacher.setLastName(request.lastName().trim());
        teacher.setPhone(normalizePhone(request.phone()));
        teacher.setDepartment(department);
        teacher.setActive(true);

        teacher = teacherRepository.save(teacher);

        return toResponse(teacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> getAll() {
        return teacherRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> getActive() {
        return teacherRepository.findByActiveTrueOrderByEmployeeIdAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeacherResponse getById(Long id) {
        return toResponse(findTeacher(id));
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> getByDepartment(Long departmentId) {

        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found");
        }

        return teacherRepository
                .findByDepartment_IdOrderByEmployeeIdAsc(departmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TeacherResponse update(Long id, TeacherRequest request) {

        TeacherEntity teacher = findTeacher(id);

        String username = normalize(request.username());
        String email = normalize(request.email());
        String employeeId = request.employeeId().trim().toUpperCase(Locale.ROOT);

        validateUniqueForUpdate(username, email, employeeId, teacher);

        DepartmentEntity department = departmentRepository
                .findById(request.departmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found"));

        if (!department.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign a teacher to an inactive department");
        }

        UserEntity user = teacher.getUser();
        user.setUsername(username);
        user.setEmail(email);

        teacher.setEmployeeId(employeeId);
        teacher.setFirstName(request.firstName().trim());
        teacher.setLastName(request.lastName().trim());
        teacher.setPhone(normalizePhone(request.phone()));
        teacher.setDepartment(department);

        return toResponse(teacherRepository.save(teacher));
    }

    public TeacherResponse deactivate(Long id) {

        TeacherEntity teacher = findTeacher(id);

        teacher.setActive(false);
        teacher.getUser().setEnabled(false);

        teacherRepository.save(teacher);

        return toResponse(teacher);
    }

    private void validateUnique(
            String username, String email, String employeeId) {

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (teacherRepository.existsByEmployeeIdIgnoreCase(employeeId)) {
            throw new IllegalArgumentException("Employee ID already exists");
        }
    }

    private void validateUniqueForUpdate(
            String username,
            String email,
            String employeeId,
            TeacherEntity teacher) {

        UserEntity user = teacher.getUser();

        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(
                username, user.getId())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(
                email, user.getId())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (teacherRepository.existsByEmployeeIdIgnoreCaseAndIdNot(
                employeeId, teacher.getId())) {
            throw new IllegalArgumentException("Employee ID already exists");
        }
    }

    private TeacherEntity findTeacher(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Teacher not found"));
    }

    private TeacherResponse toResponse(TeacherEntity teacher) {

        UserEntity user = teacher.getUser();
        DepartmentEntity department = teacher.getDepartment();

        return new TeacherResponse(
                teacher.getId(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                teacher.getEmployeeId(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getPhone(),
                department.getId(),
                department.getName(),
                teacher.isActive()
        );
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.trim();
    }


    @Transactional(readOnly = true)
public List<TeacherOfferingResponse> getMySubjectOfferings(
        String username) {

    TeacherEntity teacher = teacherRepository
            .findByUser_Username(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "No teacher profile found for the logged-in user"));

    if (!teacher.isActive()) {
        throw new IllegalArgumentException(
                "Your teacher account is inactive");
    }

    List<TeacherAssignmentEntity> assignments =
            assignmentRepository
                    .findByTeacher_IdOrderByAssignedAtDesc(
                            teacher.getId());

    return assignments.stream()
            .map(assignment -> {
                var offering = assignment.getOffering();
                var subject = offering.getSubject();
                var semester = offering.getSemester();

                return new TeacherOfferingResponse(
                        assignment.getId(),
                        offering.getId(),
                        subject.getCode(),
                        subject.getTitle(),
                        subject.getSubjectType(),
                        semester.getProgram().getName(),
                        "Semester " + semester.getSemesterNumber(),
                        offering.getCredits(),
                        offering.getStatus().name(),
                        assignment.getAssignedAt()
                );
            })
            .toList();
}

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsForOffering(String username, Long offeringId) {
        TeacherEntity teacher = teacherRepository.findByUser_Username(username)
                .filter(TeacherEntity::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Active teacher not found"));

        SubjectOfferingEntity offering = assignmentRepository
                .findByTeacher_IdOrderByAssignedAtDesc(teacher.getId()).stream()
                .map(TeacherAssignmentEntity::getOffering)
                .filter(item -> item.getId().equals(offeringId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("You are not assigned to this subject offering"));

        return studentRepository.findByProgram_IdOrderByRollNumberAsc(
                        offering.getSemester().getProgram().getId())
                .stream()
                .filter(StudentEntity::isActive)
                .map(student -> new StudentResponse(
                        student.getId(),
                        student.getUser().getId(),
                        student.getUser().getUsername(),
                        student.getUser().getEmail(),
                        student.getRollNumber(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getPhone(),
                        student.getProgram().getId(),
                        student.getProgram().getName(),
                        student.getProgram().getDepartment().getName(),
                        student.getAdmissionDate(),
                        student.getCurrentYear(),
                        student.getProgram().getDurationYears(),
                        student.getAdmissionDate().getYear() + student.getProgram().getDurationYears(),
                        student.isActive()))
                .toList();
    }
}
