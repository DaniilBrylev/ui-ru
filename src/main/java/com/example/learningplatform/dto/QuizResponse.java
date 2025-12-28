package com.example.learningplatform.dto;

public record QuizResponse(
        Long id,
        Long moduleId,
        String title,
        Integer timeLimit
) {
}
