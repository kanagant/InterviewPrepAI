package com.krutika.interviewprepai.service.claude;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnthropicMessageResponse(
        List<ContentBlock> content,
        @JsonProperty("stop_reason") String stopReason
) {
}
