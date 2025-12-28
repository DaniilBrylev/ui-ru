package com.example.learningplatform.dto;

public record UserSummaryResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
