package com.krutika.interviewprepai.service.claude;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnthropicMessageRequest(
        String model,
        @JsonProperty("max_tokens") int maxTokens,
        String system,
        List<AnthropicMessage> messages,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<AnthropicTool> tools
) {
}
