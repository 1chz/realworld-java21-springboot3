package io.github.shirohoo.realworld.application.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class UsernamePasswordAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;

    protected UsernamePasswordAuthenticationProcessingFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher("/api/users/login", "POST"));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (isNotApplicationJson(request)) {
            throw new IllegalArgumentException("Content-Type must be application/json");
        }

        AuthRequests authRequests = objectMapper.readValue(request.getReader(), AuthRequests.class);
        User user = authRequests.user;
        Authentication unauthenticatedToken =
                UsernamePasswordAuthenticationToken.unauthenticated(user.email, user.password);

        return getAuthenticationManager().authenticate(unauthenticatedToken);
    }

    private boolean isNotApplicationJson(HttpServletRequest request) {
        return !MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType());
    }

    private record AuthRequests(User user) {}

    private record User(String email, String password) {
        private User {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("email is required");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("password is required");
            }
        }
    }
}
