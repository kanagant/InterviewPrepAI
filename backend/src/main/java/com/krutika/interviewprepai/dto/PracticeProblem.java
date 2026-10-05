package com.krutika.interviewprepai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PracticeProblem(
        String name,
        String platform,
        String difficulty,
        List<String> topics,
        String relevance,
        String approachHint
) {
}
