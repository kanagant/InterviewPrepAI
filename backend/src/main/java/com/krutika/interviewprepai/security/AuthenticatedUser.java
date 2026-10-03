package com.krutika.interviewprepai.security;

public record AuthenticatedUser(Long userId, String email, String name) {
}
