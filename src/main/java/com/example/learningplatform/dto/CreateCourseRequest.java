package com.example.learningplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCourseRequest(
        @NotBlank String title,
        String description,
        @NotNull @Min(1) Integer duration,
        @NotNull LocalDate startDate,
        @NotNull Long categoryId,
        @NotNull Long teacherId
) {
}
