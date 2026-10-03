package com.krutika.interviewprepai.exception;

import com.krutika.interviewprepai.model.ArtifactType;

public class UnsupportedArtifactTypeException extends RuntimeException {
    public UnsupportedArtifactTypeException(ArtifactType type) {
        super("Artifact type " + type + " is not yet supported");
    }
}
