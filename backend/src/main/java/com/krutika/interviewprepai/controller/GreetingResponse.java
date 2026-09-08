package com.krutika.interviewprepai.controller;

public class GreetingResponse {
    private String message;
    private String status;

    public GreetingResponse(String message, String status){
        this.message =  message;
        this.status = status;
    }

    public String getMessage(){
        return message;
    }

    public String getStatus(){
        return status;
    }
}
