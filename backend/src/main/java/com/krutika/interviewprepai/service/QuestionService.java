package com.krutika.interviewprepai.service;
import com.krutika.interviewprepai.controller.QuestionResponse;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    public QuestionResponse getQuestion(Long id) {

        return new QuestionResponse(
                id,
                "Explain HashMap",
                "Backend Engineering"
        );
    }
}
