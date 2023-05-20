package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.shirohoo.realworld.IntegrationTest;
import io.github.shirohoo.realworld.application.user.controller.LoginUserRequest;
import io.github.shirohoo.realworld.application.user.controller.SignUpUserRequest;
import io.github.shirohoo.realworld.application.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@IntegrationTest
@DisplayName("The Profile APIs")
class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        userService.signUp(new SignUpUserRequest("james@example.com", "james", "password"));
        userService.signUp(new SignUpUserRequest("simpson@example.com", "simpson", "password"));
    }

    @Test
    @DisplayName("provides an API that allows unauthenticated users to view other users' profiles.")
    void getProfileOnUnauthenticated() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get("/api/profiles/{username}", "simpson"));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(false)) // always false
                .andDo(print());
    }

    @Test
    @DisplayName(
            "provides an API that allows you to know whether you are following a user when viewing another user's profile in an authenticated state.")
    void getProfileOnAuthenticate() throws Exception {
        // given
        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@example.com", "password");
        String jamesToken = userService.login(loginRequest).token();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/profiles/{username}", "simpson").header("Authorization", "Token " + jamesToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API to follow other users.")
    void follow() throws Exception {
        // given
        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@example.com", "password");
        String jamesToken = userService.login(loginRequest).token();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/profiles/{username}/follow", "simpson").header("Authorization", "Token " + jamesToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API to unfollow other users.")
    void unfollow() throws Exception {
        // given
        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@example.com", "password");
        String jamesToken = userService.login(loginRequest).token();

        // - james follow simpson
        mockMvc.perform(
                post("/api/profiles/{username}/follow", "simpson").header("Authorization", "Token " + jamesToken));

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/profiles/{username}/follow", "simpson").header("Authorization", "Token " + jamesToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profile.username").value("simpson"))
                .andExpect(jsonPath("$.profile.bio").isEmpty())
                .andExpect(jsonPath("$.profile.image").isEmpty())
                .andExpect(jsonPath("$.profile.following").value(false))
                .andDo(print());
    }
}
