package com.krutika.interviewprepai;

import com.krutika.interviewprepai.config.AnthropicProperties;
import com.krutika.interviewprepai.config.AppProperties;
import com.krutika.interviewprepai.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AnthropicProperties.class, AppProperties.class, JwtProperties.class})
public class InterviewprepaiApplication {

	public static void main(String[] args) {

		SpringApplication.run(InterviewprepaiApplication.class, args);
	}

}
