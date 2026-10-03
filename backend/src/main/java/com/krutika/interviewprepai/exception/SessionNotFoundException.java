package com.krutika.interviewprepai.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(Long id) {
        super("No session found with id " + id);
    }
}
