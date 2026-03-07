package com.college.academix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSectionRequest {

    @NotBlank(message = "Section name is required")
    private String name; // A, B, C

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
