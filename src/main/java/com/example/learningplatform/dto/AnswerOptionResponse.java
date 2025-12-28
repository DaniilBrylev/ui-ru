package com.example.learningplatform.dto;

public record AnswerOptionResponse(
        Long id,
        String text,
        boolean isCorrect
) {
}
