package com.krutika.interviewprepai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OaContent(
        String disclaimer,
        String researchSummary,
        List<PracticeProblem> problems
) {
}
