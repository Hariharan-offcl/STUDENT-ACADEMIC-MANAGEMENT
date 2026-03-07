package com.college.academix.service;

import com.college.academix.dto.*;
import com.college.academix.exception.BadRequestException;
import com.college.academix.exception.ResourceNotFoundException;
import com.college.academix.model.*;
import com.college.academix.repository.*;
import com.college.academix.util.RollNumberParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===================== DEPARTMENT =====================

    public Department createDepartment(CreateDepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new BadRequestException("Department with code " + request.getCode() + " already exists");
        }
        Department dept = Department.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .build();
        return departmentRepository.save(dept);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // ===================== SECTION =====================

    public Section createSection(CreateSectionRequest request) {
        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Section section = Section.builder()
                .name(request.getName().toUpperCase())
                .department(dept)
                .build();
        return sectionRepository.save(section);
    }

    public List<Section> getSectionsByDepartment(Long departmentId) {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return sectionRepository.findByDepartment(dept);
    }

    // ===================== SUBJECT =====================

    public Subject createSubject(CreateSubjectRequest request) {
        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Subject subject = Subject.builder()
                .code(request.getCode())
                .name(request.getName())
                .year(request.getYear())
                .semester(request.getSemester() != null ? request.getSemester() : 0)
                .department(dept)
                .build();
        return subjectRepository.save(subject);
    }

    public List<Subject> getSubjectsByDepartment(Long departmentId) {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return subjectRepository.findByDepartment(dept);
    }

    // ===================== STUDENT =====================

    @Transactional
    public Student createStudent(CreateStudentRequest request) {
        if (studentRepository.existsByRollNumber(request.getRollNumber().toUpperCase())) {
            throw new BadRequestException("Student with roll number " + request.getRollNumber() + " already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email " + request.getEmail() + " is already in use");
        }

        String rollNumber = request.getRollNumber().toUpperCase();

        // Parse roll number
        String deptCode = RollNumberParser.parseDepartmentCode(rollNumber);
        int joiningYear = RollNumberParser.parseJoiningYear(rollNumber);
        int studentNumber = RollNumberParser.parseStudentNumber(rollNumber);
        int currentYear = RollNumberParser.calculateCurrentYearLevel(rollNumber);

        // Find department by code
        Department dept = departmentRepository.findByCode(deptCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department with code " + deptCode + " not found. Create it first."));

        // Find section
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        // Create User account with default password
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode("Welcome@123"))
                .name(request.getName())
                .role(Role.STUDENT)
                .isFirstLogin(true)
                .build();
        user = userRepository.save(user);

        // Create Student record
        Student student = Student.builder()
                .rollNumber(rollNumber)
                .joiningYear(joiningYear)
                .currentYear(currentYear)
                .studentNumber(studentNumber)
                .user(user)
                .department(dept)
                .section(section)
                .build();
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // ===================== FACULTY =====================

    @Transactional
    public Faculty createFaculty(CreateFacultyRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email " + request.getEmail() + " is already in use");
        }

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Role role = request.isHod() ? Role.HOD : Role.FACULTY;

        // Create User account
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode("Welcome@123"))
                .name(request.getName())
                .role(role)
                .isFirstLogin(true)
                .build();
        user = userRepository.save(user);

        // Create Faculty record
        Faculty faculty = Faculty.builder()
                .employeeId(request.getEmployeeId())
                .user(user)
                .department(dept)
                .isClassAdvisor(false)
                .build();
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    // ===================== MAPPINGS =====================

    @Transactional
    public Faculty mapFacultyToSubjects(MapFacultySubjectRequest request) {
        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        // Map subjects
        Set<Subject> subjects = new HashSet<>(subjectRepository.findAllById(request.getSubjectIds()));
        faculty.getSubjects().addAll(subjects);

        // Map sections if provided
        if (request.getSectionIds() != null && !request.getSectionIds().isEmpty()) {
            Set<Section> sections = new HashSet<>(sectionRepository.findAllById(request.getSectionIds()));
            faculty.getSections().addAll(sections);
        }

        return facultyRepository.save(faculty);
    }

    @Transactional
    public Faculty assignClassAdvisor(AssignClassAdvisorRequest request) {
        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        faculty.setClassAdvisor(true);
        faculty.setAdvisorSection(section);

        return facultyRepository.save(faculty);
    }
}
