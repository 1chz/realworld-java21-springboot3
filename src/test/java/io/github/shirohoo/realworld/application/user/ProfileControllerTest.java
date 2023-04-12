package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
@DisplayName("프로필 API")
class ProfileControllerTest {
    @Autowired
    private MockMvc sut;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        userService.signUp(new SignUpUserRequest("james@gmail.com", "james", "1234"));
        userService.signUp(new SignUpUserRequest("simpson@gmail.com", "simpson", "1234"));
    }

    @Test
    @DisplayName("프로필 API는 미인증 유저도 다른 유저의 프로필을 조회할 수 있는 API를 제공한다")
    void getProfileOnUnauthenticated() throws Exception {
        // when
        ResultActions resultActions = sut.perform(get("/api/profiles/simpson"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(false)); // always false
    }

    @Test
    @DisplayName("프로필 API는 인증 상태에서 다른 유저의 프로필을 조회할 시 팔로우 여부를 알 수 있는 API를 제공한다")
    void getProfileOnAuthenticate() throws Exception {
        // given
        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@gmail.com", "1234");
        String jamesToken = userService.login(loginRequest).token();

        // when
        ResultActions resultActions =
                sut.perform(get("/api/profiles/simpson").header("Authorization", "Bearer " + jamesToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    @DisplayName("프로필 API는 다른 유저를 팔로우하는 API를 제공한다")
    void follow() throws Exception {
        // given
        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@gmail.com", "1234");
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

    @Test
    @DisplayName("프로필 API는 다른 유저를 언팔로우하는 API를 제공한다")
    void unfollow() throws Exception {
        // given
        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@gmail.com", "1234");
        String jamesToken = userService.login(loginRequest).token();

        // - james follow simpson
        sut.perform(post("/api/profiles/simpson/follow").header("Authorization", "Bearer " + jamesToken));

        // when
        ResultActions resultActions =
                sut.perform(delete("/api/profiles/simpson/follow").header("Authorization", "Bearer " + jamesToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(false));
    }
}
