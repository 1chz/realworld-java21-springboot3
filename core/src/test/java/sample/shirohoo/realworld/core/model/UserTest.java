package sample.shirohoo.realworld.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class UserTest {
    @ParameterizedTest
    @CsvSource({"email, true", "unknown email, false"})
    void equalsEmail(String email, boolean expected) {
        // given
        User sut = new User("email", "username", "password");

        // when
        boolean actual = sut.equalsEmail(email);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"username, true", "unknown username, false"})
    void equalsUsername(String username, boolean expected) {
        // given
        User sut = new User("email", "username", "password");

        // when
        boolean actual = sut.equalsUsername(username);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void setEmail() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setEmail("new email");

        // then
        assertThat(sut.getEmail()).isEqualTo("new email");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void if_email_is_null_or_blank_then_not_modify_email(String email) {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setEmail(email);

        // then
        assertThat(sut.getEmail()).isEqualTo("email");
    }

    @Test
    void setUsername() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setUsername("new username");

        // then
        assertThat(sut.getUsername()).isEqualTo("new username");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void if_username_is_null_or_blank_then_not_modify_username(String username) {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setUsername(username);

        // then
        assertThat(sut.getUsername()).isEqualTo("username");
    }

    @Test
    void setPassword() {
        // given
        User sut = new User("email", "username", "password");
        PasswordEncoder stubPasswordEncoder = new PasswordEncoder() {
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
        sut.setPassword(stubPasswordEncoder, "new password");

        // then
        assertThat(sut.getPassword()).isEqualTo("encoded password");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void if_raw_password_is_null_or_blank_then_not_modify_password(String rawPassword) {
        // given
        User sut = new User("email", "username", "password");
        PasswordEncoder stubPasswordEncoder = new PasswordEncoder() {
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
        sut.setPassword(stubPasswordEncoder, rawPassword);

        // then
        assertThat(sut.getPassword()).isEqualTo("password");
    }

    @Test
    void if_passwordEncoder_is_null_then_throw_Exception() {
        User sut = new User("email", "username", "password");
        assertThatThrownBy(() -> sut.setPassword(null, "new password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("passwordEncoder is required.");
    }

    @Test
    void setBio() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setBio("new bio");

        // then
        assertThat(sut.getBio()).isEqualTo("new bio");
    }

    @Test
    void setImageUrl() {
        // given
        User sut = new User("email", "username", "password");

        // when
        sut.setImageUrl("new image url");

        // then
        assertThat(sut.getImageUrl()).isEqualTo("new image url");
    }

    @Test
    void equals_is_return_true_if_are_ids_same() {
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
    void equals_is_return_false_if_are_ids_difference() {
        // given
        User user1 = new TestUser();
        User user2 = new TestUser();

        // when
        boolean isEquals = user1.equals(user2);

        // then
        assertFalse(isEquals);
    }

    @Test
    void hashCode_is_return_true_if_are_ids_same() {
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
