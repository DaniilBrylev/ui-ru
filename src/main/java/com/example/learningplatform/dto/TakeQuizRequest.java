package com.example.learningplatform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TakeQuizRequest(
        @NotNull Long studentId,
        @NotNull @Size(min = 1) List<@Valid QuestionAnswerRequest> answers
) {
}
