package io.github.shirohoo.realworld.application.user;

import static org.assertj.core.api.Assertions.*;

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
@DisplayName("팔로우 서비스")
class FollowServiceTest {
    @Autowired
    private FollowService sut;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.save(User.withEmailUsername("james@gmail.com", "james"));
        userRepository.save(User.withEmailUsername("simpson@gmail.com", "simpson"));
    }

    @AfterEach
    void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("팔로우 서비스는 프로필 조회 기능을 제공한다")
    void getProfile() throws Exception {
        // given
        User james = userRepository.findByEmail("james@gmail.com").orElseThrow();
        User simpson = userRepository.findByEmail("simpson@gmail.com").orElseThrow();

        // when
        ProfileResponse simpsonProfile = sut.getProfile(james, simpson);

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isNull();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isFalse();
    }

    @Test
    @DisplayName("팔로우 서비스는 다른 유저를 팔로우하는 기능을 제공한다")
    void follow() throws Exception {
        // given
        User james = userRepository.findByEmail("james@gmail.com").orElseThrow();
        User simpson = userRepository.findByEmail("simpson@gmail.com").orElseThrow();

        // when
        ProfileResponse simpsonProfile = sut.follow(james, simpson);

        // then
        assertThat(simpsonProfile.username()).isEqualTo("simpson");
        assertThat(simpsonProfile.bio()).isNull();
        assertThat(simpsonProfile.image()).isNull();
        assertThat(simpsonProfile.following()).isTrue();
    }
}
