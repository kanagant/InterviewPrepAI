package com.krutika.interviewprepai.service.claude;

import tools.jackson.databind.ObjectMapper;
import com.krutika.interviewprepai.config.AnthropicProperties;
import com.krutika.interviewprepai.dto.BehavioralQuestion;
import com.krutika.interviewprepai.exception.ClaudeApiException;
import com.krutika.interviewprepai.exception.ClaudeResponseParseException;
import com.krutika.interviewprepai.service.PromptBuilder.ClaudePrompt;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Component
public class ClaudeClient {

    private final AnthropicProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ClaudeClient(AnthropicProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public List<BehavioralQuestion> generateBehavioralQuestions(ClaudePrompt prompt) {
        AnthropicMessageRequest requestBody = new AnthropicMessageRequest(
                properties.model(),
                properties.maxTokens(),
                prompt.system(),
                List.of(new AnthropicMessage("user", prompt.user()))
        );

        String responseText = send(requestBody);
        return parseQuestions(responseText);
    }

    private String send(AnthropicMessageRequest requestBody) {
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to serialize Claude request", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.baseUrl()))
                .header("x-api-key", properties.apiKey())
                .header("anthropic-version", properties.version())
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to reach the Claude API", e);
        }

        if (response.statusCode() != 200) {
            throw new ClaudeApiException(
                    "Claude API returned status " + response.statusCode() + ": " + response.body());
        }

        AnthropicMessageResponse parsed;
        try {
            parsed = objectMapper.readValue(response.body(), AnthropicMessageResponse.class);
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to parse Claude API response envelope: " + response.body(), e);
        }

        return parsed.content().stream()
                .filter(block -> "text".equals(block.type()))
                .map(ContentBlock::text)
                .findFirst()
                .orElseThrow(() -> new ClaudeApiException("Claude API response contained no text content"));
    }

    private List<BehavioralQuestion> parseQuestions(String rawText) {
        String cleaned = stripCodeFences(rawText);
        try {
            QuestionsWrapper wrapper = objectMapper.readValue(cleaned, QuestionsWrapper.class);
            return wrapper.questions();
        } catch (Exception e) {
            throw new ClaudeResponseParseException(
                    "Claude response did not match the expected {questions:[...]} schema", e);
        }
    }

    private String stripCodeFences(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```[a-zA-Z]*\\n?", "");
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3);
            }
        }
        return trimmed.trim();
    }

    private record QuestionsWrapper(List<BehavioralQuestion> questions) {
    }
}
