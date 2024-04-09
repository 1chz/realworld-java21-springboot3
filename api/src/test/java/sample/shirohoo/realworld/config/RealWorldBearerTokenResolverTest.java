package sample.shirohoo.realworld.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

class RealWorldBearerTokenResolverTest {
    RealWorldBearerTokenResolver sut;

    @BeforeEach
    void setUp() {
        sut = new RealWorldBearerTokenResolver();
    }

    @Test
    void resolveFromAuthorizationHeaderTest() {
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
    void resolveFromRequestParametersTest() {
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
    void resolveFromRequestParametersMultipleTokensTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterValues("access_token")).thenReturn(new String[] {"token1", "token2"});

        // when & then
        assertThrows(OAuth2AuthenticationException.class, () -> sut.resolve(request));
    }
}
