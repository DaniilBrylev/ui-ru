package com.example.learningplatform.dto;

import java.time.LocalDate;

public record AssignmentSummaryResponse(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        Integer maxScore,
        Long lessonId,
        String lessonTitle
) {
}
