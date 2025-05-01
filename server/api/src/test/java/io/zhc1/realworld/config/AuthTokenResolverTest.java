package io.zhc1.realworld.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

@DisplayName("Auth Token Resolver - Token Extraction and Validation")
class AuthTokenResolverTest {
    BearerTokenResolver sut;

    @BeforeEach
    void setUp() {
        sut = new AuthTokenResolver();
    }

    @Test
    @DisplayName("When resolving token from authorization header, then should extract token correctly")
    void whenResolvingTokenFromAuthorizationHeader_thenShouldExtractTokenCorrectly() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Token testToken1234");

        // when
        String token = sut.resolve(request);

        // then
        assertEquals("testToken1234", token);
    }

    @Test
    @DisplayName("When resolving token from request parameters, then should extract token correctly")
    void whenResolvingTokenFromRequestParameters_thenShouldExtractTokenCorrectly() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterValues("access_token")).thenReturn(new String[] {"testToken1234"});

        // when
        String token = sut.resolve(request);

        // then
        assertEquals("testToken1234", token);
    }

    @Test
    @DisplayName("When resolving multiple tokens from request parameters, then should throw exception")
    void whenResolvingMultipleTokensFromRequestParameters_thenShouldThrowException() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterValues("access_token")).thenReturn(new String[] {"token1", "token2"});

        // when & then
        assertThrows(OAuth2AuthenticationException.class, () -> sut.resolve(request));
    }
}
