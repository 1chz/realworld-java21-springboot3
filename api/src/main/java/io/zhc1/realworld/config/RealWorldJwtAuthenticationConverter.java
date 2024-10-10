package io.zhc1.realworld.config;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

class RealWorldJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public final AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        String principalClaimValue = jwt.getClaimAsString(JwtClaimNames.SUB);
        return new RealWorldAuthenticationToken(jwt, new JwtAuthenticationToken(jwt, authorities, principalClaimValue));
    }
}
