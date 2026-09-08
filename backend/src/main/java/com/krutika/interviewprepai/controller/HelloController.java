package com.krutika.interviewprepai.controller;
import com.krutika.interviewprepai.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class HelloController {

    private GreetingService greetingService;

    public HelloController(GreetingService greetingService){
        this.greetingService = greetingService;
    }

    @GetMapping("/hello")
    public GreetingResponse hello(@RequestParam String name){

        return new GreetingResponse(
                greetingService.getMessage(name),
                "success"
        );

    }
}
