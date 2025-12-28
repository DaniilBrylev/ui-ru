package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitAssignmentRequest(
        @NotNull Long studentId,
        @NotBlank String content
) {
}
