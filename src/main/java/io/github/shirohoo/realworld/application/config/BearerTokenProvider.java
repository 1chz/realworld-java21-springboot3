package io.github.shirohoo.realworld.application.config;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BearerTokenProvider {
    private final JwtEncoder jwtEncoder;

    public String provide(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("https://realworld.io")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .subject(user.id().toString())
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        return jwtEncoder.encode(parameters).getTokenValue();
    }
}
