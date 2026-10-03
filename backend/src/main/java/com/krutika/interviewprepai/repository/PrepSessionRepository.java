package com.krutika.interviewprepai.repository;

import com.krutika.interviewprepai.model.PrepSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrepSessionRepository extends JpaRepository<PrepSession, Long> {
    List<PrepSession> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
