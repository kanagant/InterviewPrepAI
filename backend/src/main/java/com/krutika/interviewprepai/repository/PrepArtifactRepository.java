package com.krutika.interviewprepai.repository;

import com.krutika.interviewprepai.model.PrepArtifact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrepArtifactRepository extends JpaRepository<PrepArtifact, Long> {
    List<PrepArtifact> findAllBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
