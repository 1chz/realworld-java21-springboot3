package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("팔로우 API")
class FollowControllerTest {
    @Autowired
    private MockMvc sut;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        userService.signUp(new UserSignUpRequest("james@gmail.com", "james", "1234"));
        userService.signUp(new UserSignUpRequest("simpson@gmail.com", "simpson", "1234"));
    }

    @Test
    @DisplayName("팔로우 API는 다른 유저를 팔로우하는 API를 제공한다")
    void follow() throws Exception {
        // given
        // - login and get authorization token
        UserLoginRequest loginRequest = new UserLoginRequest("james@gmail.com", "1234");
        String jamesToken = userService.login(loginRequest).token();

        // when
        ResultActions resultActions =
                sut.perform(post("/api/profiles/simpson/follow").header("Authorization", "Bearer " + jamesToken));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(true));
    }
}
