package com.example.learningplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateModuleRequest(
        @NotBlank String title,
        String description,
        @NotNull @Min(1) Integer orderIndex
) {
}
