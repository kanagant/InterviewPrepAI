package com.krutika.interviewprepai.dto;

import com.krutika.interviewprepai.model.ArtifactType;

import java.time.Instant;
import java.util.List;

public record GenerateArtifactResponse(
        Long id,
        Long sessionId,
        ArtifactType type,
        List<BehavioralQuestion> content,
        Instant createdAt
) {
}
