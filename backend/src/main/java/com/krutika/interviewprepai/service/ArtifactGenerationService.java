package com.krutika.interviewprepai.service;

import tools.jackson.databind.ObjectMapper;
import com.krutika.interviewprepai.dto.BehavioralQuestion;
import com.krutika.interviewprepai.dto.GenerateArtifactResponse;
import com.krutika.interviewprepai.dto.OaContent;
import com.krutika.interviewprepai.exception.ClaudeApiException;
import com.krutika.interviewprepai.exception.CompanyRequiredForOaException;
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
        PrepSession session = prepSessionService.findByIdForUserOrThrow(sessionId, userId);

        if (type == ArtifactType.OA && (session.getCompany() == null || session.getCompany().isBlank())) {
            throw new CompanyRequiredForOaException();
        }

        Object content = switch (type) {
            case BEHAVIORAL -> claudeClient.generateQuestions(promptBuilder.buildBehavioralPrompt(session));
            case TECHNICAL -> claudeClient.generateQuestions(promptBuilder.buildTechnicalPrompt(session));
            case OA -> claudeClient.generateOaRecommendations(promptBuilder.buildOaPrompt(session));
        };

        PrepArtifact artifact = persist(sessionId, type, content);

        return new GenerateArtifactResponse(
                artifact.getId(),
                artifact.getSessionId(),
                artifact.getType(),
                content,
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
        Object content = switch (artifact.getType()) {
            case BEHAVIORAL, TECHNICAL -> Arrays.asList(readValue(artifact.getContent(), BehavioralQuestion[].class));
            case OA -> readValue(artifact.getContent(), OaContent.class);
        };
        return new GenerateArtifactResponse(
                artifact.getId(),
                artifact.getSessionId(),
                artifact.getType(),
                content,
                artifact.getCreatedAt()
        );
    }

    private <T> T readValue(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to parse stored artifact content", e);
        }
    }

    @Transactional
    PrepArtifact persist(Long sessionId, ArtifactType type, Object content) {
        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(content);
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
