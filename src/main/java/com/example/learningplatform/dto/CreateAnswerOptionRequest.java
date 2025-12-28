package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAnswerOptionRequest(
        @NotBlank String text,
        @NotNull Boolean isCorrect
) {
}
