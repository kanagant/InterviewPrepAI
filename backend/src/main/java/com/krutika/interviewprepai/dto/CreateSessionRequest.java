package com.krutika.interviewprepai.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(
        @NotBlank String jobDescription,
        @NotBlank String resumeText,
        String company,
        String role
) {
}
