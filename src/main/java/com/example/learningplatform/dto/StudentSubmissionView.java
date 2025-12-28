package com.example.learningplatform.dto;

import java.time.LocalDateTime;

public record StudentSubmissionView(
        Long submissionId,
        Long assignmentId,
        String assignmentTitle,
        Integer score,
        LocalDateTime submittedAt,
        String feedback
) {
}
