package com.krutika.interviewprepai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "anthropic")
public record AnthropicProperties(
        String apiKey,
        String model,
        String baseUrl,
        int maxTokens,
        String version
) {
}
