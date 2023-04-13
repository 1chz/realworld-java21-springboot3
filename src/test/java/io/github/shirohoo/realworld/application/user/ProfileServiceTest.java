package io.github.shirohoo.realworld.application.user;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.user.ProfileVO;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("The Profile Services")
class ProfileServiceTest {
    @Autowired
    private ProfileService sut;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        User james = new User().email("james@gmail.com").username("james");
        userRepository.save(james);

        User simpson = new User().email("simpson@gmail.com").username("simpson");
        userRepository.save(simpson);
    }

    @AfterEach
    void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("provides function to lookup profile.")
    void getProfile() throws Exception {
        // given
        User james = userRepository.findByEmail("james@gmail.com").orElseThrow();
        User simpson = userRepository.findByEmail("simpson@gmail.com").orElseThrow();

        // when
        ProfileVO simpsonProfile = sut.getProfile(james, simpson);

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isNull();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isFalse();
    }

    @Test
    @DisplayName("provides the function to follow other users.")
    void follow() throws Exception {
        // given
        User james = userRepository.findByEmail("james@gmail.com").orElseThrow();
        User simpson = userRepository.findByEmail("simpson@gmail.com").orElseThrow();

        // when
        ProfileVO simpsonProfile = sut.follow(james, simpson);

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isNull();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isTrue();
    }

    @Test
    @DisplayName("provides the function to unfollow other users.")
    void unfollow() throws Exception {
        // given
        // - fetch users
        User james = userRepository.findByEmail("james@gmail.com").orElseThrow();
        User simpson = userRepository.findByEmail("simpson@gmail.com").orElseThrow();

        // - james follow simpson
        sut.follow(james, simpson);

        // when
        ProfileVO simpsonProfile = sut.unfollow(james, simpson);

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isNull();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isFalse();
    }
}
