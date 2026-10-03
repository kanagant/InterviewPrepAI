package com.krutika.interviewprepai.service.claude;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContentBlock(String type, String text) {
}
