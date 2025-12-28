package com.example.learningplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateQuizRequest(
        @NotBlank String title,
        @Min(1) Integer timeLimit
) {
}
