package com.college.academix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;
}
