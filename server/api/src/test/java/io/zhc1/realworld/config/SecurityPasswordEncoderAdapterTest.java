package io.zhc1.realworld.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.zhc1.realworld.model.PasswordEncoder;

class SecurityPasswordEncoderAdapterTest {
    PasswordEncoder sut;

    @BeforeEach
    void setUp() {
        sut = new SecurityPasswordEncoderAdapter();
    }

    @Test
    void testMatchesMethodWithCorrectCredentials() {
        // given
        String rawPassword = "TestPassword";
        String encodedPassword = sut.encode(rawPassword);

        // when
        boolean result = sut.matches(rawPassword, encodedPassword);

        // then
        assertTrue(result);
    }

    @Test
    void testMatchesMethodWithIncorrectCredentials() {
        // given
        String encodedPassword = sut.encode("CorrectPassword");

        // when
        boolean result = sut.matches("WrongPassword", encodedPassword);

        // then
        assertFalse(result);
    }
}
