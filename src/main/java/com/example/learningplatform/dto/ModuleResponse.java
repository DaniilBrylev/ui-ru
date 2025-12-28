package com.example.learningplatform.dto;

import java.util.List;

public record ModuleResponse(
        Long id,
        String title,
        String description,
        Integer orderIndex,
        List<LessonResponse> lessons
) {
}
