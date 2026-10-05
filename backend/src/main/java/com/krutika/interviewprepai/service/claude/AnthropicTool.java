package com.krutika.interviewprepai.service.claude;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnthropicTool(String type, String name, @JsonProperty("max_uses") Integer maxUses) {
}
