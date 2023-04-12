package io.github.shirohoo.realworld.application.user;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserVO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("The User Services")
class UserServiceTest {
    @Autowired
    private UserService sut;

    @Test
    @DisplayName("provides membership registration function.")
    void signUp() throws Exception {
        // given
        // - sign up request
        SignUpUserRequest signUpRequest = new SignUpUserRequest("james@gmail.com", "james", "1234");

        // when
        User user = sut.signUp(signUpRequest);

        // then
        assertThat(user.id()).isNotNull();
        assertThat(user.email()).isEqualTo("james@gmail.com");
        assertThat(user.username()).isEqualTo("james");
        assertThat(user.password()).isNotEqualTo("1234");
    }

    @Test
    @DisplayName("provides login function.")
    void login() throws Exception {
        // given
        // - sign up
        SignUpUserRequest signUpRequest = new SignUpUserRequest("james@gmail.com", "james", "1234");
        sut.signUp(signUpRequest);

        // when
        UserVO user = sut.login(new LoginUserRequest("james@gmail.com", "1234"));

        // then
        assertThat(user.email()).isEqualTo("james@gmail.com");
        assertThat(user.username()).isEqualTo("james");
        assertThat(user.token()).isNotEmpty();
        assertThat(user.bio()).isNull();
        assertThat(user.image()).isNull();
    }

    @Test
    @DisplayName("provides member information update function.")
    void update() throws Exception {
        // given
        // - sign up
        SignUpUserRequest signUpRequest = new SignUpUserRequest("james@gmail.com", "james", "1234");
        User user = sut.signUp(signUpRequest);

        // - update request
        String email = "james.to@gmail.com";
        String username = "james.to";
        String password = "5678";
        String bio = "I like to skateboard";
        String image = "https://i.stack.imgur.com/xHWG8.jpg";
        UpdateUserRequest updateRequest = new UpdateUserRequest(email, username, password, bio, image);

        // when
        UserVO userVO = sut.update(user, updateRequest);

        // then
        assertThat(userVO.email()).isEqualTo("james.to@gmail.com");
        assertThat(userVO.username()).isEqualTo("james.to");
        assertThat(userVO.bio()).isEqualTo("I like to skateboard");
        assertThat(userVO.image()).isEqualTo("https://i.stack.imgur.com/xHWG8.jpg");
    }
}
