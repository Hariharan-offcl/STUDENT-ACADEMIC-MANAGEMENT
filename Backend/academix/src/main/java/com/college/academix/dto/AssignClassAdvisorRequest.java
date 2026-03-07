package com.college.academix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignClassAdvisorRequest {

    @NotNull(message = "Faculty ID is required")
    private Long facultyId;

    @NotNull(message = "Section ID is required")
    private Long sectionId;
}
