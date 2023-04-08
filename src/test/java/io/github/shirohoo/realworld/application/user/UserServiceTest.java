package io.github.shirohoo.realworld.application.user;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.user.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("유저 서비스")
class UserServiceTest {
    @Autowired
    private UserService sut;

    @Test
    @DisplayName("유저 서비스는 회원가입 서비스를 제공한다")
    void signUp() throws Exception {
        // given
        // - sign up request
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");

        // when
        User user = sut.signUp(signUpRequest);

        // then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("james@gmail.com");
        assertThat(user.getUsername()).isEqualTo("james");
        assertThat(user.getPassword()).isNotEqualTo("1234");
    }

    @Test
    @DisplayName("유저 서비스는 로그인 서비스를 제공한다")
    void login() throws Exception {
        // given
        // - sign up
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");
        sut.signUp(signUpRequest);

        // when
        UserResponse user = sut.login(new UserLoginRequest("james@gmail.com", "1234"));

        // then
        assertThat(user.email()).isEqualTo("james@gmail.com");
        assertThat(user.username()).isEqualTo("james");
        assertThat(user.token()).isNotEmpty();
        assertThat(user.bio()).isNull();
        assertThat(user.image()).isNull();
    }
}
