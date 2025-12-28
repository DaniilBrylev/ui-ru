package com.example.learningplatform.dto;

import java.time.LocalDate;
import java.util.List;

public record CourseResponse(
        Long id,
        String title,
        String description,
        Integer duration,
        LocalDate startDate,
        Long categoryId,
        Long teacherId,
        List<String> tags
) {
}
