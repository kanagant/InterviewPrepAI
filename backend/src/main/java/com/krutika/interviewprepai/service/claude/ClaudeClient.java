package com.krutika.interviewprepai.service.claude;

import tools.jackson.databind.ObjectMapper;
import com.krutika.interviewprepai.config.AnthropicProperties;
import com.krutika.interviewprepai.dto.BehavioralQuestion;
import com.krutika.interviewprepai.dto.OaContent;
import com.krutika.interviewprepai.exception.ClaudeApiException;
import com.krutika.interviewprepai.exception.ClaudeResponseParseException;
import com.krutika.interviewprepai.service.PromptBuilder.ClaudePrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Component
public class ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(ClaudeClient.class);

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

    public List<BehavioralQuestion> generateQuestions(ClaudePrompt prompt) {
        String text = extractFinalText(send(buildRequest(prompt, properties.maxTokens(), null)));
        return parseQuestions(text);
    }

    public OaContent generateOaRecommendations(ClaudePrompt prompt) {
        AnthropicTool webSearch = new AnthropicTool("web_search_20260209", "web_search", properties.oaMaxSearches());
        String text = extractFinalText(send(buildRequest(prompt, properties.maxTokensOa(), List.of(webSearch))));
        return parseOaContent(text);
    }

    private AnthropicMessageRequest buildRequest(ClaudePrompt prompt, int maxTokens, List<AnthropicTool> tools) {
        return new AnthropicMessageRequest(
                properties.model(),
                maxTokens,
                prompt.system(),
                List.of(new AnthropicMessage("user", prompt.user())),
                tools
        );
    }

    private AnthropicMessageResponse send(AnthropicMessageRequest requestBody) {
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to serialize Claude request", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.baseUrl()))
                .timeout(Duration.ofSeconds(90))
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

        try {
            return objectMapper.readValue(response.body(), AnthropicMessageResponse.class);
        } catch (Exception e) {
            throw new ClaudeApiException("Failed to parse Claude API response envelope: " + response.body(), e);
        }
    }

    private String extractFinalText(AnthropicMessageResponse response) {
        // Take the LAST text block, not the first: when tools (e.g. web_search) are used,
        // Claude emits narration/tool-use blocks before its final structured-JSON answer.
        // For the no-tools path there's only ever one text block, so this is a no-op there.
        return response.content().stream()
                .filter(block -> "text".equals(block.type()))
                .map(ContentBlock::text)
                .reduce((first, second) -> second)
                .orElseThrow(() -> {
                    String blockTypes = response.content().stream()
                            .map(ContentBlock::type)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("(no content blocks)");
                    log.warn("No text content block found. stop_reason={}, block types=[{}]",
                            response.stopReason(), blockTypes);
                    return new ClaudeApiException("Claude API response contained no text content");
                });
    }

    private <T> T parseJson(String rawText, Class<T> type, String schemaDescription) {
        String cleaned = stripCodeFences(rawText);
        try {
            return objectMapper.readValue(cleaned, type);
        } catch (Exception e) {
            throw new ClaudeResponseParseException(
                    "Claude response did not match the expected " + schemaDescription + " schema", e);
        }
    }

    private List<BehavioralQuestion> parseQuestions(String rawText) {
        return parseJson(rawText, QuestionsWrapper.class, "{questions:[...]}").questions();
    }

    private OaContent parseOaContent(String rawText) {
        return parseJson(rawText, OaContent.class, "OA content");
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
