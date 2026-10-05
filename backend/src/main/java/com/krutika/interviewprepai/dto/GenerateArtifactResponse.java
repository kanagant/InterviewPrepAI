package com.krutika.interviewprepai.dto;

import com.krutika.interviewprepai.model.ArtifactType;

import java.time.Instant;

public record GenerateArtifactResponse(
        Long id,
        Long sessionId,
        ArtifactType type,
        Object content,
        Instant createdAt
) {
}
