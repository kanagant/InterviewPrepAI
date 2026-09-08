package com.krutika.interviewprepai.service;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String getMessage(String name) {
        return "Hello " + name + "!";
    }
}
