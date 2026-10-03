package com.krutika.interviewprepai.dto;

import com.krutika.interviewprepai.model.ArtifactType;

public record GenerateArtifactRequest(ArtifactType type) {

    public ArtifactType typeOrDefault() {
        return type != null ? type : ArtifactType.BEHAVIORAL;
    }
}
