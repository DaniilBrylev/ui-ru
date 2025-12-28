package com.example.learningplatform.dto;

import java.time.LocalDate;

public record AssignmentResponse(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        Integer maxScore
) {
}
