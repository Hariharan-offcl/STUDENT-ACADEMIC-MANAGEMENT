package com.college.academix.controller;

import com.college.academix.dto.ApiResponse;
import com.college.academix.dto.QuizResultResponse;
import com.college.academix.dto.SubmitQuizRequest;
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
@RequestMapping("/api/student")
@Tag(name = "Student", description = "Student operations: assignments, submissions, OD requests, quizzes")
public class StudentController {

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

    @Operation(summary = "View Assignments", description = "View assignments for your department, year, and section")
    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<List<Assignment>>> getAssignments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Assignments retrieved",
                assignmentService.getAssignmentsForStudent(user)));
    }

    @Operation(summary = "Submit Assignment", description = "Upload a file to submit an assignment before the deadline")
    @PostMapping("/assignments/{assignmentId}/submit")
    public ResponseEntity<ApiResponse<Submission>> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        Submission submission = assignmentService.submitAssignment(assignmentId, user, file);
        return ResponseEntity.ok(ApiResponse.success("Assignment submitted successfully", submission));
    }

    @Operation(summary = "View Marks", description = "View marks for all submitted assignments")
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<List<Submission>>> getSubmissions(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Submissions retrieved",
                assignmentService.getStudentSubmissions(user)));
    }

    // ===================== OD REQUESTS =====================

    @Operation(summary = "Apply for OD", description = "Submit an OD (On-Duty) request")
    @PostMapping("/od/apply")
    public ResponseEntity<ApiResponse<ODRequest>> applyOD(
            @RequestParam("reason") String reason,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate,
            @RequestParam(value = "eventDocument", required = false) MultipartFile eventDocument,
            @RequestParam(value = "paymentProof", required = false) MultipartFile paymentProof,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        ODRequest odRequest = odService.createODRequest(reason, fromDate, toDate, user, eventDocument, paymentProof);
        return ResponseEntity.ok(ApiResponse.success("OD request submitted successfully", odRequest));
    }

    @Operation(summary = "View My OD Requests")
    @GetMapping("/od")
    public ResponseEntity<ApiResponse<List<ODRequest>>> getMyODRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("OD requests retrieved", odService.getStudentODRequests(user)));
    }

    // ===================== QUIZZES =====================

    @Operation(summary = "View Available Quizzes", description = "View quizzes for your department, year, and section")
    @GetMapping("/quizzes")
    public ResponseEntity<ApiResponse<List<Quiz>>> getQuizzes(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Quizzes retrieved", quizService.getQuizzesForStudent(user)));
    }

    @Operation(summary = "Attempt Quiz", description = "Submit answers for a quiz. Score is calculated immediately.")
    @PostMapping("/quizzes/{quizId}/attempt")
    public ResponseEntity<ApiResponse<QuizResultResponse>> attemptQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody SubmitQuizRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        QuizResultResponse result = quizService.attemptQuiz(quizId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Quiz submitted successfully", result));
    }

    @Operation(summary = "View My Quiz Results")
    @GetMapping("/quizzes/results")
    public ResponseEntity<ApiResponse<List<QuizResultResponse>>> getMyQuizResults(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Quiz results retrieved",
                quizService.getStudentQuizResults(user)));
    }
}
