package io.zhc1.realworld.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("User - Identity, Authentication, and Profile Management Operations")
class UserTest {
    @ParameterizedTest
    @CsvSource({"email, true", "unknown email, false"})
    @DisplayName(
            "When comparing email with equalsEmail method, then should return true for matching email and false otherwise")
    void whenComparingEmail_thenShouldReturnCorrectResult(String email, boolean expected) {
        // given
        User sut = new User("email", "username", "password");

        // when
        boolean actual = sut.equalsEmail(email);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"username, true", "unknown username, false"})
    @DisplayName(
            "When comparing username with equalsUsername method, then should return true for matching username and false otherwise")
    void whenComparingUsername_thenShouldReturnCorrectResult(String username, boolean expected) {
        // given
        User sut = new User("email", "username", "password");

        // when
        boolean actual = sut.equalsUsername(username);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("When setting a new email, then email should be updated")
    void whenSettingNewEmail_thenEmailShouldBeUpdated() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setEmail("new email");

        // then
        assertThat(sut.getEmail()).isEqualTo("new email");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("When setting email with null or empty value, then email should not be modified")
    void whenSettingEmailWithNullOrEmptyValue_thenEmailShouldNotBeModified(String email) {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setEmail(email);

        // then
        assertThat(sut.getEmail()).isEqualTo("email");
    }

    @Test
    @DisplayName("When setting a new username, then username should be updated")
    void whenSettingNewUsername_thenUsernameShouldBeUpdated() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setUsername("new username");

        // then
        assertThat(sut.getUsername()).isEqualTo("new username");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("When setting username with null or empty value, then username should not be modified")
    void whenSettingUsernameWithNullOrEmptyValue_thenUsernameShouldNotBeModified(String username) {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setUsername(username);

        // then
        assertThat(sut.getUsername()).isEqualTo("username");
    }

    @Test
    @DisplayName("When encrypting password, then password should be encoded")
    void whenEncryptingPassword_thenPasswordShouldBeEncoded() {
        // given
        User sut = new User("email", "username", "password");
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return false;
            }

            @Override
            public String encode(String rawPassword) {
                return "encoded password";
            }
        };

        // when
        sut.encryptPassword(passwordEncoder, "new password");

        // then
        assertThat(sut.getPassword()).isEqualTo("encoded password");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("When encrypting password with null or empty value, then password should not be modified")
    void whenEncryptingPasswordWithNullOrEmptyValue_thenPasswordShouldNotBeModified(String rawPassword) {
        // given
        User sut = new User("email", "username", "password");
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return false;
            }

            @Override
            public String encode(String rawPassword) {
                return "encoded password";
            }
        };

        // when
        sut.encryptPassword(passwordEncoder, rawPassword);

        // then
        assertThat(sut.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("When encrypting password with null encoder, then should throw IllegalArgumentException")
    void whenEncryptingPasswordWithNullEncoder_thenShouldThrowIllegalArgumentException() {
        User sut = new User("email", "username", "password");
        assertThatThrownBy(() -> sut.encryptPassword(null, "new password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("passwordEncoder is required.");
    }

    @Test
    @DisplayName("When setting a new bio, then bio should be updated")
    void whenSettingNewBio_thenBioShouldBeUpdated() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setBio("new bio");

        // then
        assertThat(sut.getBio()).isEqualTo("new bio");
    }

    @Test
    @DisplayName("When setting a new image URL, then image URL should be updated")
    void whenSettingNewImageUrl_thenImageUrlShouldBeUpdated() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setImageUrl("new image url");

        // then
        assertThat(sut.getImageUrl()).isEqualTo("new image url");
    }

    @Test
    @DisplayName("When comparing users with same ID, then equals should return true")
    void whenComparingUsersWithSameId_thenEqualsShouldReturnTrue() {
        // given
        UUID id = UUID.randomUUID();
        User user1 = new TestUser(id);
        User user2 = new TestUser(id);

        // when
        boolean isEquals = user1.equals(user2);

        // then
        assertTrue(isEquals);
    }

    @Test
    @DisplayName("When comparing users with different IDs, then equals should return false")
    void whenComparingUsersWithDifferentIds_thenEqualsShouldReturnFalse() {
        // given
        User user1 = new TestUser();
        User user2 = new TestUser();

        // when
        boolean isEquals = user1.equals(user2);

        // then
        assertFalse(isEquals);
    }

    @Test
    @DisplayName("When comparing hashCode of users with same ID, then hashCodes should be equal")
    void whenComparingHashCodeOfUsersWithSameId_thenHashCodesShouldBeEqual() {
        // given
        UUID id = UUID.randomUUID();
        User user1 = new TestUser(id);
        User user2 = new TestUser(id);

        // when
        boolean isEquals = user1.hashCode() == user2.hashCode();

        // then
        assertTrue(isEquals);
    }
}
