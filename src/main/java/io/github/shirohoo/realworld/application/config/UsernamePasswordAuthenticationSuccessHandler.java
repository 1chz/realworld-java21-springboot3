package io.github.shirohoo.realworld.application.config;

import io.github.shirohoo.realworld.domain.user.User;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
class UsernamePasswordAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final ApplicationTokenService tokenService;
    private final ObjectMapper objectMapper;

    UsernamePasswordAuthenticationSuccessHandler(ApplicationTokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        User user = (User) authentication.getPrincipal();

        String token = tokenService.provide(user);
        String contentJson = objectMapper.writeValueAsString(user.bind(token).toImmutable());

        response.setStatus(200);
        response.setContentType("application/json");
        response.getWriter().write(contentJson);
    }
}
