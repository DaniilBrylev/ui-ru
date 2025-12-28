package com.example.learningplatform.dto;

import java.time.LocalDate;

public record StudentCourseView(
        Long courseId,
        String courseTitle,
        String status,
        LocalDate enrollDate
) {
}
