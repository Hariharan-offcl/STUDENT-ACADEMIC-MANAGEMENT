package com.college.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResultResponse {
    private Long quizId;
    private String quizTitle;
    private String studentName;
    private String rollNumber;
    private int score;
    private int totalQuestions;
    private double percentage;
}
