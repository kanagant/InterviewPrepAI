package com.krutika.interviewprepai.service;

import com.krutika.interviewprepai.model.User;
import com.krutika.interviewprepai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User findOrCreateFromGoogle(String googleId, String email, String name) {
        return userRepository.findByGoogleId(googleId)
                .map(existing -> {
                    existing.setEmail(email);
                    existing.setName(name);
                    return userRepository.save(existing);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .googleId(googleId)
                                .email(email)
                                .name(name)
                                .build()));
    }
}
