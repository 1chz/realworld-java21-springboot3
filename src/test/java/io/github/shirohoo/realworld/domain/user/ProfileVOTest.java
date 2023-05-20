package io.github.shirohoo.realworld.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The ProfileVO")
class ProfileVOTest {
    @Test
    @DisplayName("constructor works fine.")
    void constructor() {
        // given
        User james = User.builder()
                .id(UUID.randomUUID())
                .username("james")
                .email("james@example.com")
                .password("password")
                .build();

        User simpson = User.builder()
                .id(UUID.randomUUID())
                .username("simpson")
                .email("simpson@example.com")
                .password("password")
                .bio("My name is simpson.")
                .image("https://example.com/image.jpg")
                .build();

        // when
        ProfileVO simpsonProfile = new ProfileVO(james, simpson);

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isEqualTo("My name is simpson.");
        assertThat(simpsonProfile.image()).isEqualTo("https://example.com/image.jpg");
        assertThat(simpsonProfile.following()).isFalse();
    }

    @Test
    @DisplayName("search the profile of the person you followed, following is true.")
    void following() {
        // given
        User james = User.builder()
                .id(UUID.randomUUID())
                .username("james")
                .email("james@example.com")
                .password("password")
                .build();

        User simpson = User.builder()
                .id(UUID.randomUUID())
                .username("simpson")
                .email("simpson@example.com")
                .password("password")
                .bio("My name is simpson.")
                .image("https://example.com/image.jpg")
                .build();

        // when
        ProfileVO simpsonProfile = james.follow(simpson);

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isEqualTo("My name is simpson.");
        assertThat(simpsonProfile.image()).isEqualTo("https://example.com/image.jpg");
        assertThat(simpsonProfile.following()).isTrue();
    }
}
