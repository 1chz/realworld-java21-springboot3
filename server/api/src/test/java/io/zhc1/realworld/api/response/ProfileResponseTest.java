package io.zhc1.realworld.api.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.zhc1.realworld.model.User;

class ProfileResponseTest {

    @Test
    void shouldReturn_Username_From_UserProfile() {
        // given
        String email = "a@example.com";
        String username = "LeoLeeTech";
        String password = "password";

        User user = new User(email, username, password);

        // when
        ProfileResponse sut = ProfileResponse.from(user);

        // then
        assertThat(sut.username()).isNotEqualTo(email);
        assertThat(sut.username()).isEqualTo(username);
    }
}
