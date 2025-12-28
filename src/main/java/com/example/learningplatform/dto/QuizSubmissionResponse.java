package com.example.learningplatform.dto;

import java.time.LocalDateTime;

public record QuizSubmissionResponse(
        Long id,
        Long quizId,
        Long studentId,
        Integer score,
        LocalDateTime takenAt
) {
}
