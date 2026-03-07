package com.college.academix.controller;

import com.college.academix.dto.*;
import com.college.academix.model.*;
import com.college.academix.repository.UserRepository;
import com.college.academix.service.AssignmentService;
import com.college.academix.service.ODService;
import com.college.academix.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@Tag(name = "Faculty", description = "Faculty operations: assignments, evaluation, quizzes, OD approval")
public class FacultyController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ODService odService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ===================== ASSIGNMENTS =====================

    @Operation(summary = "Create Assignment", description = "Post a new assignment with optional instruction file")
    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<Assignment>> createAssignment(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("deadline") String deadline,
            @RequestParam("year") int year,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("sectionId") Long sectionId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam(value = "instructionFile", required = false) MultipartFile instructionFile,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        Assignment assignment = assignmentService.createAssignment(
                title, description, java.time.LocalDateTime.parse(deadline),
                year, departmentId, sectionId, subjectId, user, instructionFile);
        return ResponseEntity.ok(ApiResponse.success("Assignment created successfully", assignment));
    }

    @Operation(summary = "Get My Assignments", description = "View all assignments created by you")
    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<List<Assignment>>> getMyAssignments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Assignments retrieved",
                assignmentService.getAssignmentsByFaculty(user)));
    }

    @Operation(summary = "View Submissions", description = "View all student submissions for an assignment")
    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<ApiResponse<List<Submission>>> getSubmissions(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(ApiResponse.success("Submissions retrieved",
                assignmentService.getSubmissionsByAssignment(assignmentId)));
    }

    @Operation(summary = "Evaluate Submission", description = "Assign marks and feedback to a student submission")
    @PatchMapping("/submissions/{submissionId}/evaluate")
    public ResponseEntity<ApiResponse<Submission>> evaluateSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody EvaluateSubmissionRequest request) {
        Submission submission = assignmentService.evaluateSubmission(
                submissionId, request.getMarks(), request.getFeedback());
        return ResponseEntity.ok(ApiResponse.success("Submission evaluated successfully", submission));
    }

    // ===================== QUIZZES =====================

    @Operation(summary = "Create Quiz", description = "Create an MCQ quiz with questions")
    @PostMapping("/quizzes")
    public ResponseEntity<ApiResponse<Quiz>> createQuiz(
            @Valid @RequestBody CreateQuizRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        Quiz quiz = quizService.createQuiz(request, user);
        return ResponseEntity.ok(ApiResponse.success("Quiz created successfully", quiz));
    }

    @Operation(summary = "Get My Quizzes")
    @GetMapping("/quizzes")
    public ResponseEntity<ApiResponse<List<Quiz>>> getMyQuizzes(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Quizzes retrieved", quizService.getQuizzesByFaculty(user)));
    }

    @Operation(summary = "View Quiz Scores", description = "View all student scores for a specific quiz")
    @GetMapping("/quizzes/{quizId}/scores")
    public ResponseEntity<ApiResponse<List<QuizResultResponse>>> getQuizScores(@PathVariable Long quizId) {
        return ResponseEntity.ok(ApiResponse.success("Scores retrieved", quizService.getQuizScores(quizId)));
    }

    // ===================== OD APPROVAL (MENTOR) =====================

    @Operation(summary = "Get Pending OD Requests", description = "View pending OD requests for your section (class advisor only)")
    @GetMapping("/od/pending")
    public ResponseEntity<ApiResponse<List<ODRequest>>> getPendingODRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Pending OD requests retrieved",
                odService.getPendingRequestsForMentor(user)));
    }

    @Operation(summary = "Approve OD (Mentor)", description = "Approve an OD request as class advisor/mentor")
    @PatchMapping("/od/{odId}/approve")
    public ResponseEntity<ApiResponse<ODRequest>> mentorApproveOD(@PathVariable Long odId) {
        ODRequest odRequest = odService.mentorApprove(odId);
        return ResponseEntity.ok(ApiResponse.success("OD request approved by mentor", odRequest));
    }

    @Operation(summary = "Reject OD", description = "Reject an OD request with a reason")
    @PatchMapping("/od/{odId}/reject")
    public ResponseEntity<ApiResponse<ODRequest>> rejectOD(
            @PathVariable Long odId,
            @RequestParam("reason") String reason) {
        ODRequest odRequest = odService.rejectOD(odId, reason);
        return ResponseEntity.ok(ApiResponse.success("OD request rejected", odRequest));
    }

    // ===================== OD APPROVAL (HOD) =====================

    @Operation(summary = "Get Mentor-Approved OD Requests (HOD)", description = "View OD requests approved by mentors, awaiting HOD approval")
    @GetMapping("/od/mentor-approved")
    public ResponseEntity<ApiResponse<List<ODRequest>>> getMentorApprovedOD() {
        return ResponseEntity.ok(ApiResponse.success("Mentor-approved OD requests retrieved",
                odService.getMentorApprovedRequests()));
    }

    @Operation(summary = "Approve OD (HOD)", description = "Final approval of an OD request by HOD")
    @PatchMapping("/od/{odId}/hod-approve")
    public ResponseEntity<ApiResponse<ODRequest>> hodApproveOD(@PathVariable Long odId) {
        ODRequest odRequest = odService.hodApprove(odId);
        return ResponseEntity.ok(ApiResponse.success("OD request approved by HOD", odRequest));
    }
}
