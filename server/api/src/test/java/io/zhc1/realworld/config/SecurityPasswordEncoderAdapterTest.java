package io.zhc1.realworld.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.realworld.model.PasswordEncoder;

@DisplayName("Security Password Encoder Adapter - Password Encoding and Verification")
class SecurityPasswordEncoderAdapterTest {
    PasswordEncoder sut;

    @BeforeEach
    void setUp() {
        sut = new SecurityPasswordEncoderAdapter();
    }

    @Test
    @DisplayName("When matching with correct password, then should return true")
    void whenMatchingWithCorrectPassword_thenShouldReturnTrue() {
        // given
        String rawPassword = "TestPassword";
        String encodedPassword = sut.encode(rawPassword);

        // when
        boolean result = sut.matches(rawPassword, encodedPassword);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("When matching with incorrect password, then should return false")
    void whenMatchingWithIncorrectPassword_thenShouldReturnFalse() {
        // given
        String encodedPassword = sut.encode("CorrectPassword");

        // when
        boolean result = sut.matches("WrongPassword", encodedPassword);

        // then
        assertFalse(result);
    }
}
