package com.krutika.interviewprepai.service;

import com.krutika.interviewprepai.model.PrepSession;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    private static final String BEHAVIORAL_SYSTEM_PROMPT = """
            You are an expert technical interview coach. Given a job description and a \
            candidate's resume, generate 5 to 8 behavioral interview questions tailored to \
            the overlap and gaps between them. Respond with ONLY valid JSON matching exactly \
            this schema, with no markdown code fences and no prose before or after: \
            {"questions":[{"question":"string","rationale":"string"}]}""";

    public ClaudePrompt buildBehavioralPrompt(PrepSession session) {
        StringBuilder user = new StringBuilder();
        user.append("Job Description:\n").append(session.getJobDescription())
                .append("\n\nCandidate Resume:\n").append(session.getResumeText());

        if (session.getRole() != null || session.getCompany() != null) {
            user.append("\n\nTarget role: ")
                    .append(session.getRole() != null ? session.getRole() : "unspecified role")
                    .append(session.getCompany() != null ? " at " + session.getCompany() : "")
                    .append(".");
        }

        user.append("\n\nGenerate the behavioral interview questions as specified.");

        return new ClaudePrompt(BEHAVIORAL_SYSTEM_PROMPT, user.toString());
    }

    public record ClaudePrompt(String system, String user) {
    }
}
