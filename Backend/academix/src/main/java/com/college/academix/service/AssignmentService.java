package com.college.academix.service;

import com.college.academix.exception.BadRequestException;
import com.college.academix.exception.ResourceNotFoundException;
import com.college.academix.model.*;
import com.college.academix.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // ===================== FACULTY: Create Assignment =====================

    public Assignment createAssignment(String title, String description, LocalDateTime deadline,
            int year, Long departmentId, Long sectionId, Long subjectId,
            User currentUser, MultipartFile instructionFile) {
        Faculty faculty = facultyRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));

        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Assignment assignment = Assignment.builder()
                .title(title)
                .description(description)
                .deadline(deadline)
                .year(year)
                .department(dept)
                .section(section)
                .subject(subject)
                .faculty(faculty)
                .build();

        if (instructionFile != null && !instructionFile.isEmpty()) {
            try {
                assignment.setInstructionFile(instructionFile.getBytes());
                assignment.setInstructionFileName(instructionFile.getOriginalFilename());
            } catch (IOException e) {
                throw new BadRequestException("Failed to process instruction file");
            }
        }

        return assignmentRepository.save(assignment);
    }

    // ===================== FACULTY: Get Assignments =====================

    public List<Assignment> getAssignmentsByFaculty(User currentUser) {
        Faculty faculty = facultyRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));
        return assignmentRepository.findByFaculty(faculty);
    }

    // ===================== STUDENT: View Assignments =====================

    public List<Assignment> getAssignmentsForStudent(User currentUser) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return assignmentRepository.findByDepartmentAndSectionAndYear(
                student.getDepartment(), student.getSection(), student.getCurrentYear());
    }

    // ===================== STUDENT: Submit Assignment =====================

    public Submission submitAssignment(Long assignmentId, User currentUser, MultipartFile file) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // Check deadline
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            throw new BadRequestException("Cannot submit after the deadline");
        }

        // Check if already submitted
        if (submissionRepository.existsByAssignmentAndStudent(assignment, student)) {
            throw new BadRequestException("You have already submitted this assignment");
        }

        try {
            Submission submission = Submission.builder()
                    .assignment(assignment)
                    .student(student)
                    .file(file.getBytes())
                    .fileName(file.getOriginalFilename())
                    .build();
            return submissionRepository.save(submission);
        } catch (IOException e) {
            throw new BadRequestException("Failed to process submission file");
        }
    }

    // ===================== FACULTY: View Submissions =====================

    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        return submissionRepository.findByAssignment(assignment);
    }

    // ===================== FACULTY: Evaluate Submission =====================

    public Submission evaluateSubmission(Long submissionId, int marks, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        submission.setMarks(marks);
        submission.setFeedback(feedback);
        submission.setEvaluated(true);

        return submissionRepository.save(submission);
    }

    // ===================== STUDENT: View Marks =====================

    public List<Submission> getStudentSubmissions(User currentUser) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return submissionRepository.findByStudent(student);
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
    }
}
