package com.college.academix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapFacultySubjectRequest {

    @NotNull(message = "Faculty ID is required")
    private Long facultyId;

    @NotNull(message = "Subject IDs are required")
    private List<Long> subjectIds;

    private List<Long> sectionIds; // Optional: map faculty to specific sections
}
