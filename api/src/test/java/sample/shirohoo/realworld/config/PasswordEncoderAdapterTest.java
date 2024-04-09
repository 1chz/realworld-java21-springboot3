package sample.shirohoo.realworld.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sample.shirohoo.realworld.core.model.PasswordEncoder;

class PasswordEncoderAdapterTest {
    PasswordEncoder sut;

    @BeforeEach
    void setUp() {
        sut = new PasswordEncoderAdapter();
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
        String rawPassword = "TestPassword";
        String wrongRawPassword = "WrongPassword";
        String encodedPassword = sut.encode(rawPassword);

        // when
        boolean result = sut.matches(wrongRawPassword, encodedPassword);

        // then
        assertFalse(result);
    }
}
