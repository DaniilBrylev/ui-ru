package com.example.learningplatform.dto;

import java.util.List;

public record LessonResponse(
        Long id,
        String title,
        String content,
        String videoUrl,
        List<AssignmentResponse> assignments
) {
}
