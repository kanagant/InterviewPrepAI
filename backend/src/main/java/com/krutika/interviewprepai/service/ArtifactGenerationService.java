package com.krutika.interviewprepai.service;

import tools.jackson.databind.ObjectMapper;
import com.krutika.interviewprepai.dto.BehavioralQuestion;
import com.krutika.interviewprepai.dto.GenerateArtifactResponse;
import com.krutika.interviewprepai.exception.ClaudeApiException;
import com.krutika.interviewprepai.exception.UnsupportedArtifactTypeException;
import com.krutika.interviewprepai.model.ArtifactType;
import com.krutika.interviewprepai.model.PrepArtifact;
import com.krutika.interviewprepai.model.PrepSession;
import com.krutika.interviewprepai.repository.PrepArtifactRepository;
import com.krutika.interviewprepai.service.claude.ClaudeClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ArtifactGenerationService {

    private final PrepSessionService prepSessionService;
    private final PromptBuilder promptBuilder;
    private final ClaudeClient claudeClient;
    private final PrepArtifactRepository prepArtifactRepository;
    private final ObjectMapper objectMapper;

    public ArtifactGenerationService(
            PrepSessionService prepSessionService,
            PromptBuilder promptBuilder,
            ClaudeClient claudeClient,
            PrepArtifactRepository prepArtifactRepository,
            ObjectMapper objectMapper
    ) {
        this.prepSessionService = prepSessionService;
        this.promptBuilder = promptBuilder;
        this.claudeClient = claudeClient;
        this.prepArtifactRepository = prepArtifactRepository;
        this.objectMapper = objectMapper;
    }

    public GenerateArtifactResponse generate(Long userId, Long sessionId, ArtifactType type) {
        if (type != ArtifactType.BEHAVIORAL) {
            throw new UnsupportedArtifactTypeException(type);
        }

        PrepSession session = prepSessionService.findByIdForUserOrThrow(sessionId, userId);

        PromptBuilder.ClaudePrompt prompt = promptBuilder.buildBehavioralPrompt(session);
        List<BehavioralQuestion> questions = claudeClient.generateBehavioralQuestions(prompt);

        PrepArtifact artifact = persist(sessionId, type, questions);

        return new GenerateArtifactResponse(
                artifact.getId(),
                artifact.getSessionId(),
                artifact.getType(),
                questions,
                artifact.getCreatedAt()
        );
    }

    public List<GenerateArtifactResponse> listForSession(Long userId, Long sessionId) {
        prepSessionService.findByIdForUserOrThrow(sessionId, userId);

        return prepArtifactRepository.findAllBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(this::toResponse)
                .toList();
    }

    private GenerateArtifactResponse toResponse(PrepArtifact artifact) {
        BehavioralQuestion[] parsed;
        try {
            parsed = objectMapper.readValue(artifact.getContent(), BehavioralQuestion[].class);
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to parse stored artifact content", e);
        }
        return new GenerateArtifactResponse(
                artifact.getId(),
                artifact.getSessionId(),
                artifact.getType(),
                Arrays.asList(parsed),
                artifact.getCreatedAt()
        );
    }

    @Transactional
    PrepArtifact persist(Long sessionId, ArtifactType type, List<BehavioralQuestion> questions) {
        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(questions);
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to serialize generated content", e);
        }

        PrepArtifact artifact = PrepArtifact.builder()
                .sessionId(sessionId)
                .type(type)
                .content(contentJson)
                .build();

        return prepArtifactRepository.save(artifact);
    }
}
