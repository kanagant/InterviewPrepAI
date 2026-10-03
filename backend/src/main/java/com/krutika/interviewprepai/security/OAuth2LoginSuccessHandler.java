package com.krutika.interviewprepai.security;

import com.krutika.interviewprepai.config.AppProperties;
import com.krutika.interviewprepai.model.User;
import com.krutika.interviewprepai.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final AppProperties appProperties;

    public OAuth2LoginSuccessHandler(UserService userService, JwtService jwtService, AppProperties appProperties) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.appProperties = appProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        User user = userService.findOrCreateFromGoogle(
                oAuth2User.getAttribute("sub"),
                oAuth2User.getAttribute("email"),
                oAuth2User.getAttribute("name"));

        String token = jwtService.issueToken(user);

        String redirectUrl = UriComponentsBuilder.fromUriString(appProperties.frontendUrl())
                .path("/oauth2/redirect")
                .queryParam("token", token)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
