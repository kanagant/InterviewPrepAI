package com.krutika.interviewprepai.service;

import com.krutika.interviewprepai.exception.SessionNotFoundException;
import com.krutika.interviewprepai.model.PrepSession;
import com.krutika.interviewprepai.repository.PrepSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrepSessionService {

    private final PrepSessionRepository prepSessionRepository;

    public PrepSessionService(PrepSessionRepository prepSessionRepository) {
        this.prepSessionRepository = prepSessionRepository;
    }

    @Transactional
    public PrepSession create(Long userId, String jobDescription, String resumeText, String company, String role) {
        PrepSession session = PrepSession.builder()
                .userId(userId)
                .jobDescription(jobDescription)
                .resumeText(resumeText)
                .company(company)
                .role(role)
                .build();
        return prepSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public PrepSession findByIdForUserOrThrow(Long id, Long userId) {
        PrepSession session = prepSessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException(id));
        if (!session.getUserId().equals(userId)) {
            throw new SessionNotFoundException(id);
        }
        return session;
    }

    @Transactional(readOnly = true)
    public List<PrepSession> findAllForUser(Long userId) {
        return prepSessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
