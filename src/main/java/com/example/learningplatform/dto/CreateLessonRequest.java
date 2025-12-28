package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLessonRequest(
        @NotBlank String title,
        @NotBlank String content,
        String videoUrl
) {
}
