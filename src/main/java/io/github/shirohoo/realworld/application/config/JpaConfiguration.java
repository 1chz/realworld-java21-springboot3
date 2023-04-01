package io.github.shirohoo.realworld.application.config;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@EnableJpaAuditing
class JpaConfiguration {
    @Bean
    AuditorAware<Integer> createAuditorAware(UserRepository userRepository) {
        return () -> {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            JwtAuthenticationToken authentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            String guid = authentication.getName();
            return userRepository.findByGuid(UUID.fromString(guid)).map(User::getId);
        };
    }
}
