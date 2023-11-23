package sample.shirohoo.realworld.api;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.User;

@Component
@RequiredArgsConstructor
public class RealworldBearerTokenProvider {
    private final JwtEncoder jwtEncoder;

    public Jwt getToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("user is null or unknown user.");
        }

        Instant now = Instant.now();
        return jwtEncoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder()
                .issuer("https://realworld.io")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .subject(user.getId().toString())
                .build()));
    }
}
