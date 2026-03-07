package com.college.academix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFacultyRequest {

    @NotBlank(message = "Faculty name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    private String employeeId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private boolean isHod = false; // If true, role = HOD
}
