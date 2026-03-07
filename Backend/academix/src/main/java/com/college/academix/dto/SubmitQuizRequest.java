package com.college.academix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizRequest {

    @NotNull(message = "Answers are required")
    private Map<Long, String> answers; // questionId -> selected option (A/B/C/D)
}
