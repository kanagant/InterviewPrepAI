package com.krutika.interviewprepai.service.claude;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnthropicMessageRequest(
        String model,
        @JsonProperty("max_tokens") int maxTokens,
        String system,
        List<AnthropicMessage> messages
) {
}
