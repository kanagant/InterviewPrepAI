package com.krutika.interviewprepai.exception;

public class CompanyRequiredForOaException extends RuntimeException {
    public CompanyRequiredForOaException() {
        super("Company is required to generate an OA artifact");
    }
}
