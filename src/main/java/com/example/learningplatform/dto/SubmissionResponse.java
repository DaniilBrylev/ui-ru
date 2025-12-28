package com.example.learningplatform.dto;

import java.time.LocalDateTime;

public record SubmissionResponse(
        Long id,
        Long assignmentId,
        Long studentId,
        LocalDateTime submittedAt,
        String content,
        Integer score,
        String feedback
) {
}
