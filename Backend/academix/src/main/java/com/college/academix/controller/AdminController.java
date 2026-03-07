package com.college.academix.controller;

import com.college.academix.dto.*;
import com.college.academix.model.*;
import com.college.academix.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin operations: manage departments, sections, subjects, students, faculty")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ===================== DEPARTMENT =====================

    @Operation(summary = "Create Department")
    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<Department>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        Department dept = adminService.createDepartment(request);
        return ResponseEntity.ok(ApiResponse.success("Department created successfully", dept));
    }

    @Operation(summary = "Get All Departments")
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved", adminService.getAllDepartments()));
    }

    // ===================== SECTION =====================

    @Operation(summary = "Create Section")
    @PostMapping("/sections")
    public ResponseEntity<ApiResponse<Section>> createSection(@Valid @RequestBody CreateSectionRequest request) {
        Section section = adminService.createSection(request);
        return ResponseEntity.ok(ApiResponse.success("Section created successfully", section));
    }

    @Operation(summary = "Get Sections by Department")
    @GetMapping("/sections/{departmentId}")
    public ResponseEntity<ApiResponse<List<Section>>> getSections(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResponse.success("Sections retrieved",
                adminService.getSectionsByDepartment(departmentId)));
    }

    // ===================== SUBJECT =====================

    @Operation(summary = "Create Subject")
    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<Subject>> createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        Subject subject = adminService.createSubject(request);
        return ResponseEntity.ok(ApiResponse.success("Subject created successfully", subject));
    }

    @Operation(summary = "Get Subjects by Department")
    @GetMapping("/subjects/{departmentId}")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjects(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved",
                adminService.getSubjectsByDepartment(departmentId)));
    }

    // ===================== STUDENT =====================

    @Operation(summary = "Create Student", description = "Creates a student account using roll number. Department and year are auto-parsed from roll number.")
    @PostMapping("/students")
    public ResponseEntity<ApiResponse<Student>> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        Student student = adminService.createStudent(request);
        return ResponseEntity
                .ok(ApiResponse.success("Student created successfully with default password: Welcome@123", student));
    }

    @Operation(summary = "Get All Students")
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", adminService.getAllStudents()));
    }

    // ===================== FACULTY =====================

    @Operation(summary = "Create Faculty", description = "Creates a faculty account. Set isHod=true for HOD role.")
    @PostMapping("/faculty")
    public ResponseEntity<ApiResponse<Faculty>> createFaculty(@Valid @RequestBody CreateFacultyRequest request) {
        Faculty faculty = adminService.createFaculty(request);
        return ResponseEntity
                .ok(ApiResponse.success("Faculty created successfully with default password: Welcome@123", faculty));
    }

    @Operation(summary = "Get All Faculty")
    @GetMapping("/faculty")
    public ResponseEntity<ApiResponse<List<Faculty>>> getAllFaculty() {
        return ResponseEntity.ok(ApiResponse.success("Faculty retrieved", adminService.getAllFaculty()));
    }

    // ===================== MAPPINGS =====================

    @Operation(summary = "Map Faculty to Subjects and Sections")
    @PostMapping("/faculty/map-subjects")
    public ResponseEntity<ApiResponse<Faculty>> mapFacultyToSubjects(
            @Valid @RequestBody MapFacultySubjectRequest request) {
        Faculty faculty = adminService.mapFacultyToSubjects(request);
        return ResponseEntity.ok(ApiResponse.success("Faculty mapped to subjects successfully", faculty));
    }

    @Operation(summary = "Assign Class Advisor", description = "Assign a faculty member as class advisor for a section")
    @PostMapping("/faculty/assign-advisor")
    public ResponseEntity<ApiResponse<Faculty>> assignClassAdvisor(
            @Valid @RequestBody AssignClassAdvisorRequest request) {
        Faculty faculty = adminService.assignClassAdvisor(request);
        return ResponseEntity.ok(ApiResponse.success("Class advisor assigned successfully", faculty));
    }
}
