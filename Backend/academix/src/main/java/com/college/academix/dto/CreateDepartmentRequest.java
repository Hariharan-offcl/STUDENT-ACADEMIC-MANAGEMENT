package com.college.academix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentRequest {

    @NotBlank(message = "Department code is required")
    private String code; // AM, AD, CS, IT

    @NotBlank(message = "Department name is required")
    private String name;
}
