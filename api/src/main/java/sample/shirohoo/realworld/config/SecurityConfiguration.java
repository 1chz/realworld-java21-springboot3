package sample.shirohoo.realworld.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

@Configuration
@EnableMethodSecurity
class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        requests -> requests.requestMatchers(HttpMethod.POST, "/api/users", "/api/users/login")
                                .permitAll()
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/articles/{slug}/comments",
                                        "/api/articles/{slug}",
                                        "/api/articles",
                                        "/api/profiles/{username}",
                                        "/api/tags")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(STATELESS))
                .exceptionHandling(exceptionHandler -> exceptionHandler
                        /* Note: If you want, you can change the prefix of the Authorization token from 'Bearer' to 'Token'. */
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        /* Note: Allowing everything like this is not the correct way, so never do this in a practical environment. */

        var configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.key.public}") RSAPublicKey rsaPublicKey) {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(
            @Value("${security.key.public}") RSAPublicKey rsaPublicKey,
            @Value("${security.key.private}") RSAPrivateKey rsaPrivateKey) {
        var jwk = new RSAKey.Builder(rsaPublicKey).privateKey(rsaPrivateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
