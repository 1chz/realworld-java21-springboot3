package sample.shirohoo.realworld.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import sample.shirohoo.realworld.IntegrationTest;
import sample.shirohoo.realworld.core.model.PasswordEncoder;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserRegistry;

@IntegrationTest
class UserServiceTest {
    @Autowired
    UserService sut;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void getUserById() {
        // given
        UUID userId = UUID.fromString("5c4471f1-3ab6-40c3-8797-599439946596");
        ThrowingCallable action = () -> sut.getUserById(userId);

        // when
        var exception = assertThatThrownBy(action);

        // then
        exception.isInstanceOf(IllegalArgumentException.class).hasMessage("user not found.");
    }

    @Test
    void getUserByUsername() {
        // given
        String username = "james";
        ThrowingCallable action = () -> sut.getUserByUsername(username);

        // when
        var exception = assertThatThrownBy(action);

        // then
        exception.isInstanceOf(IllegalArgumentException.class).hasMessage("user not found.");
    }

    @Test
    void signup() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");

        // when
        User registedUser = sut.signup(registry);

        // then
        assertThat(registedUser.getId()).isNotNull();
        assertThat(registedUser.getEmail()).isEqualTo("email");
        assertThat(registedUser.getUsername()).isEqualTo("username");
        assertThat(registedUser.getPassword()).isNotEqualTo("password");
        assertThat(registedUser.getBio()).isNull();
        assertThat(registedUser.getImageUrl()).isNull();
        assertThat(registedUser.getCreatedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_attempting_signup_with_duplicate_email() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        sut.signup(registry);

        UserRegistry secondRegistry = new UserRegistry("email", "username", "password");

        // when
        var exception = assertThatThrownBy(() -> sut.signup(secondRegistry));

        // then
        exception.isInstanceOf(IllegalArgumentException.class).hasMessage("email or username is already exists.");
    }

    @Test
    void login() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        sut.signup(registry);

        // when
        User loginedUser = sut.login("email", "password");

        // then
        assertThat(loginedUser.getId()).isNotNull();
        assertThat(loginedUser.getEmail()).isEqualTo("email");
        assertThat(loginedUser.getUsername()).isEqualTo("username");
        assertThat(loginedUser.getPassword()).isNotEqualTo("password");
        assertThat(loginedUser.getBio()).isNull();
        assertThat(loginedUser.getImageUrl()).isNull();
        assertThat(loginedUser.getCreatedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_attempting_login_with_invalid_email() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        sut.signup(registry);

        // when
        var exception = assertThatThrownBy(() -> sut.login("invalid email", "password"));

        // then
        exception.isInstanceOf(IllegalArgumentException.class).hasMessage("invalid email or password.");
    }

    @Test
    void should_throw_exception_when_attempting_login_with_invalid_password() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        sut.signup(registry);

        // when
        var exception = assertThatThrownBy(() -> sut.login("email", "invalid password"));

        // then
        exception.isInstanceOf(IllegalArgumentException.class).hasMessage("invalid email or password.");
    }

    @Test
    void updateEmail() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        User user = sut.signup(registry);

        // when
        User updatedUser = sut.updateEmail(user, "updated email");

        // then
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo("updated email");
        assertThat(updatedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getBio()).isEqualTo(user.getBio());
        assertThat(updatedUser.getImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(updatedUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    void updateUsername() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        User user = sut.signup(registry);

        // when
        User updatedUser = sut.updateUsername(user, "updated username");

        // then
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getUsername()).isEqualTo("updated username");
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getBio()).isEqualTo(user.getBio());
        assertThat(updatedUser.getImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(updatedUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    void updatePassword() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        User user = sut.signup(registry);

        // when
        User updatedUser = sut.updatePassword(user, "updated password");

        // then
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getUsername()).isEqualTo(user.getUsername());
        assertTrue(passwordEncoder.matches("updated password", updatedUser.getPassword()));
        assertThat(updatedUser.getBio()).isEqualTo(user.getBio());
        assertThat(updatedUser.getImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(updatedUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    void updateBio() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        User user = sut.signup(registry);

        // when
        User updatedUser = sut.updateBio(user, "updated bio");

        // then
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getBio()).isEqualTo("updated bio");
        assertThat(updatedUser.getImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(updatedUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    void updateImageUrl() {
        // given
        UserRegistry registry = new UserRegistry("email", "username", "password");
        User user = sut.signup(registry);

        // when
        User updatedUser = sut.updateImageUrl(user, "updated image url");

        // then
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getBio()).isEqualTo(user.getBio());
        assertThat(updatedUser.getImageUrl()).isEqualTo("updated image url");
        assertThat(updatedUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }
}
