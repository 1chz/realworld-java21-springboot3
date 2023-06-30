package io.shirohoo.realworld.application.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.shirohoo.realworld.IntegrationTest;
import io.shirohoo.realworld.application.user.service.ProfileService;
import io.shirohoo.realworld.domain.user.ProfileVO;
import io.shirohoo.realworld.domain.user.User;
import io.shirohoo.realworld.domain.user.UserRepository;

@IntegrationTest
@DisplayName("The Profile Services")
class ProfileServiceTest {
    @Autowired
    private ProfileService sut;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        User james = User.builder()
                .email("james@example.com")
                .username("james")
                .password("password")
                .build();
        userRepository.save(james);

        User simpson = User.builder()
                .email("simpson@example.com")
                .username("simpson")
                .password("password")
                .build();
        userRepository.save(simpson);
    }

    @Test
    @DisplayName("provides function to lookup profile.")
    void getProfile() throws Exception {
        // given
        User james = userRepository.findByEmail("james@example.com").orElseThrow();

        // when
        ProfileVO simpsonProfile = sut.getProfile(james, "simpson");

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isEmpty();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isFalse();
    }

    @Test
    @DisplayName("provides the function to follow other users.")
    void follow() throws Exception {
        // given
        User james = userRepository.findByEmail("james@example.com").orElseThrow();

        // when
        ProfileVO simpsonProfile = sut.follow(james, "simpson");

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isEmpty();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isTrue();
    }

    @Test
    @DisplayName("provides the function to unfollow other users.")
    void unfollow() throws Exception {
        // given
        // - fetch users
        User james = userRepository.findByEmail("james@example.com").orElseThrow();

        // - james follow simpson
        sut.follow(james, "simpson");

        // when
        ProfileVO simpsonProfile = sut.unfollow(james, "simpson");

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isEmpty();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isFalse();
    }
}
