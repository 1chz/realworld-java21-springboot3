package io.github.shirohoo.realworld.application.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shirohoo.realworld.domain.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
public class UsernamePasswordAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        String token = jwtProvider.provide(user.getGuid().toString());
        UserResponse userResponse = UserResponse.of(user, token);
        String contentJson = objectMapper.writeValueAsString(userResponse);

        response.setStatus(200);
        response.setContentType("application/json");
        response.getWriter().write(contentJson);
    }

    private record UserResponse(UserDetails user) {
        public static UserResponse of(User user, String token) {
            return new UserResponse(UserDetails.of(user, token));
        }
    }

    private record UserDetails(String email, String token, String username, String bio, String image) {
        public static UserDetails of(User user, String token) {
            return new UserDetails(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImage());
        }
    }
}
