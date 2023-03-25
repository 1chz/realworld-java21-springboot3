package io.github.shirohoo.realworld.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shirohoo.realworld.domain.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Component
class UsernamePasswordAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;

    protected UsernamePasswordAuthenticationProcessingFilter(
            ObjectMapper objectMapper,
            AuthenticationManager authenticationManager,
            AuthenticationSuccessHandler successHandler) {
        super(new AntPathRequestMatcher("/api/users/login", "POST"));
        super.setAuthenticationManager(authenticationManager);
        super.setAuthenticationSuccessHandler(successHandler);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (isNotApplicationJson(request)) {
            throw new IllegalArgumentException("Content-Type must be application/json");
        }

        User user = objectMapper.readValue(request.getReader(), User.class);
        Authentication unauthenticatedToken =
                UsernamePasswordAuthenticationToken.unauthenticated(user.getEmail(), user.getPassword());

        return getAuthenticationManager().authenticate(unauthenticatedToken);
    }

    private boolean isNotApplicationJson(HttpServletRequest request) {
        return !MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType());
    }
}
