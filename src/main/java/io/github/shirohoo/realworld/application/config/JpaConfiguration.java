package io.github.shirohoo.realworld.application.config;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration
@EnableJpaAuditing
class JpaConfiguration {
    @PersistenceContext
    EntityManager entityManager;

    @Bean
    JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    AuditorAware<UUID> createAuditorAware() {
        return () -> {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            JwtAuthenticationToken authentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            return Optional.of(authentication.getName()).map(UUID::fromString);
        };
    }
}
