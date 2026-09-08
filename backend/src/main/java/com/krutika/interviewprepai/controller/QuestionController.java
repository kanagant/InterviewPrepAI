package com.krutika.interviewprepai.controller;
import com.krutika.interviewprepai.service.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{id}")
    public QuestionResponse getQuestion(
            @PathVariable Long id) {

        return questionService.getQuestion(id);
    }
}
