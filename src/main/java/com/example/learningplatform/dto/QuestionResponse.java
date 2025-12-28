package com.example.learningplatform.dto;

import com.example.learningplatform.entity.QuestionType;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        QuestionType type,
        List<AnswerOptionResponse> options
) {
}
