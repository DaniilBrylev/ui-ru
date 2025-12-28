package com.example.learningplatform.dto;

import java.time.LocalDate;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        Long courseId,
        LocalDate enrollDate,
        String status
) {
}
