package com.example.learningplatform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuestionAnswerRequest(
        @NotNull Long questionId,
        @NotNull @Size(min = 1) List<Long> selectedOptionIds
) {
}
