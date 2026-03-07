package com.college.academix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Questions are required")
    private List<QuestionDTO> questions;
}
