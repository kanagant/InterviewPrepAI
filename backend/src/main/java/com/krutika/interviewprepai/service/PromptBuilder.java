package com.krutika.interviewprepai.service;

import com.krutika.interviewprepai.model.PrepSession;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    private static final String BEHAVIORAL_SYSTEM_PROMPT = """
            You are an expert technical interview coach. Given a job description and a \
            candidate's resume, generate 5 to 8 behavioral interview questions tailored to \
            the overlap and gaps between them. For each question, also write a full sample \
            answer in the STAR format (Situation, Task, Action, Result), written in first \
            person as the candidate would say it, drawing on specific, real details from \
            the candidate's resume (actual project names, numbers, technologies) rather \
            than generic placeholders - invent plausible specifics only where the resume \
            doesn't give enough detail, and keep it natural, not overly long. Respond with \
            ONLY valid JSON matching exactly this schema, with no markdown code fences and \
            no prose before or after: \
            {"questions":[{"question":"string","rationale":"string","answer":"string"}]}""";

    private static final String TECHNICAL_SYSTEM_PROMPT = """
            You are an expert technical interview coach. Given a job description and a \
            candidate's resume, generate 5 to 8 technical interview questions (CS \
            fundamentals, system design, or role-specific technical concepts) tailored to \
            the overlap and gaps between them. For each question, also write a full worked \
            answer explaining the concept or approach clearly, at the depth expected in a \
            real interview, referencing the candidate's actual resume background where \
            relevant (e.g. connecting the explanation to a technology or project they've \
            listed). Respond with ONLY valid JSON matching exactly this schema, with no \
            markdown code fences and no prose before or after: \
            {"questions":[{"question":"string","rationale":"string","answer":"string"}]}""";

    private static final String OA_SYSTEM_PROMPT = """
            You are an expert technical interview coach helping a candidate prepare for an \
            Online Assessment (OA). Use the web_search tool to find real, specific reports \
            of this company's OA process for this role or a closely related one - check \
            sources like Reddit, Glassdoor, LeetCode Discuss, and Blind. Base your \
            recommendations on what you actually find, not on the job description's \
            keywords and not on generic guesses.

            Recommend a set of REAL, well-known, already-existing practice problems (for \
            example actual named LeetCode problems) chosen because they exercise the same \
            patterns or topics that the real OA is reported to use. Do not invent problems. \
            For each problem, explain specifically how solving it builds the skill or \
            pattern needed for the real OA - not just a bare name. Also give a brief \
            approach hint for each problem (the key insight or technique to use, 1-3 \
            sentences) - a nudge in the right direction, NOT a full step-by-step solution \
            or working code.

            You MUST include a clear disclaimer that these are practice problems only, not \
            the actual OA questions, and that the real OA will differ.

            Respond with ONLY valid JSON matching exactly this schema, with no markdown \
            code fences and no prose before or after: \
            {"disclaimer":"string","researchSummary":"string or null","problems":[{"name":"string","platform":"string","difficulty":"string","topics":["string"],"relevance":"string","approachHint":"string"}]}""";

    public ClaudePrompt buildBehavioralPrompt(PrepSession session) {
        return buildQuestionPrompt(session, BEHAVIORAL_SYSTEM_PROMPT, "behavioral");
    }

    public ClaudePrompt buildTechnicalPrompt(PrepSession session) {
        return buildQuestionPrompt(session, TECHNICAL_SYSTEM_PROMPT, "technical");
    }

    private ClaudePrompt buildQuestionPrompt(PrepSession session, String systemPrompt, String questionKind) {
        StringBuilder user = new StringBuilder();
        user.append("Job Description:\n").append(session.getJobDescription())
                .append("\n\nCandidate Resume:\n").append(session.getResumeText());

        appendTargetRole(session, user);

        user.append("\n\nGenerate the ").append(questionKind).append(" interview questions as specified.");

        return new ClaudePrompt(systemPrompt, user.toString());
    }

    public ClaudePrompt buildOaPrompt(PrepSession session) {
        // Company presence is validated by ArtifactGenerationService before this is called.
        StringBuilder user = new StringBuilder();
        user.append("Company: ").append(session.getCompany());
        if (session.getRole() != null && !session.getRole().isBlank()) {
            user.append("\nRole: ").append(session.getRole());
        }
        user.append("\n\nJob Description:\n").append(session.getJobDescription())
                .append("\n\nCandidate Resume:\n").append(session.getResumeText())
                .append("\n\nResearch this company's OA process for this role and recommend practice problems as specified.");

        return new ClaudePrompt(OA_SYSTEM_PROMPT, user.toString());
    }

    private void appendTargetRole(PrepSession session, StringBuilder user) {
        if (session.getRole() != null || session.getCompany() != null) {
            user.append("\n\nTarget role: ")
                    .append(session.getRole() != null ? session.getRole() : "unspecified role")
                    .append(session.getCompany() != null ? " at " + session.getCompany() : "")
                    .append(".");
        }
    }

    public record ClaudePrompt(String system, String user) {
    }
}
