package io.shirohoo.realworld.application.config;

import java.util.UUID;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;

import io.shirohoo.realworld.domain.user.User;
import io.shirohoo.realworld.domain.user.UserRepository;

@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == User.class;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return User.anonymous();
        }

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String userId = jwtAuthenticationToken.getName().strip();
        String token = jwtAuthenticationToken.getToken().getTokenValue().strip();

        return userRepository
                .findById(UUID.fromString(userId))
                .map(it -> it.setToken(token))
                .orElseThrow(() -> new InvalidBearerTokenException("`%s` is invalid token".formatted(token)));
    }
}
