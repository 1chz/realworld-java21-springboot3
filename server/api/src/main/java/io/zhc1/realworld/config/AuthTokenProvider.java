package io.zhc1.realworld.config;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.model.User;

@Component
@RequiredArgsConstructor
public final class AuthTokenProvider {
    private final JwtEncoder jwtEncoder;

    public String createAuthToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("user is null or unknown user.");
        }

        var now = Instant.now();
        return jwtEncoder
                .encode(JwtEncoderParameters.from(JwtClaimsSet.builder()
                        .issuer("https://realworld.io")
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(300))
                        .subject(user.getId().toString())
                        .build()))
                .getTokenValue();
    }
}
