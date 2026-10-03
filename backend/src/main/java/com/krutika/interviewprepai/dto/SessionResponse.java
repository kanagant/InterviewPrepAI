package com.krutika.interviewprepai.dto;

import com.krutika.interviewprepai.model.PrepSession;

import java.time.Instant;

public record SessionResponse(
        Long id,
        String jobDescription,
        String resumeText,
        String company,
        String role,
        Instant createdAt
) {
    public static SessionResponse from(PrepSession session) {
        return new SessionResponse(
                session.getId(),
                session.getJobDescription(),
                session.getResumeText(),
                session.getCompany(),
                session.getRole(),
                session.getCreatedAt()
        );
    }
}
