package com.example.learningplatform.dto;

import com.example.learningplatform.entity.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateQuestionRequest(
        @NotBlank String text,
        @NotNull QuestionType type,
        @NotNull @Size(min = 2) List<@Valid CreateAnswerOptionRequest> options
) {
}
