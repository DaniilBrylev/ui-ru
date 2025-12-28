package com.example.learningplatform.dto;

import java.time.LocalDateTime;

public record StudentQuizResultView(
        Long quizId,
        String quizTitle,
        Integer score,
        LocalDateTime takenAt
) {
}
