package com.krutika.interviewprepai.controller;

public class QuestionResponse {
    private Long id;
    private String title;
    private String category;

    public QuestionResponse(Long id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }
}
