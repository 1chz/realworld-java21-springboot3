package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.shirohoo.realworld.IntegrationTest;
import io.github.shirohoo.realworld.application.user.controller.LoginUserRequest;
import io.github.shirohoo.realworld.application.user.controller.SignUpUserRequest;
import io.github.shirohoo.realworld.application.user.controller.UpdateUserRequest;
import io.github.shirohoo.realworld.application.user.service.UserService;
import io.github.shirohoo.realworld.domain.user.UserVO;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

@IntegrationTest
@DisplayName("The User APIs")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("provides membership registration API.")
    void signUp() throws Exception {
        // given
        // - sign up request
        SignUpUserRequest signUpRequest = new SignUpUserRequest("james@example.com", "james", "password");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("user", signUpRequest))));

        // then
        resultActions
                .andExpect(status().isTemporaryRedirect())
                .andExpect(view().name("redirect:/api/users/login"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute(
                                "user", Map.of("user", new LoginUserRequest("james@example.com", "password"))))
                .andDo(print());
    }

    @Test
    @DisplayName("provides login API.")
    void login() throws Exception {
        // given
        // - sign up
        SignUpUserRequest signUpRequest = new SignUpUserRequest("james@example.com", "james", "password");
        userService.signUp(signUpRequest);

        // - login request
        LoginUserRequest loginRequest = new LoginUserRequest("james@example.com", "password");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("user", loginRequest))));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("james@example.com"))
                .andExpect(jsonPath("$.user.username").value("james"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.bio").isEmpty())
                .andExpect(jsonPath("$.user.image").isEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("provides logged-in user information.")
    void getCurrentUser() throws Exception {
        // given
        // - sign up
        SignUpUserRequest signUpRequest = new SignUpUserRequest("james@example.com", "james", "password");
        userService.signUp(signUpRequest);

        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@example.com", "password");
        String jamesToken = userService.login(loginRequest).token();

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/user").header("Authorization", "Token " + jamesToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("james@example.com"))
                .andExpect(jsonPath("$.user.username").value("james"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.bio").isEmpty())
                .andExpect(jsonPath("$.user.image").isEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("provides user information update API.")
    void update() throws Exception {
        // given
        // - sign up
        SignUpUserRequest signUpRequest = new SignUpUserRequest("james@example.com", "james", "password");
        userService.signUp(signUpRequest);

        // - login and get authorization token
        LoginUserRequest loginRequest = new LoginUserRequest("james@example.com", "password");
        UserVO userVO = userService.login(loginRequest);

        // - update request
        String email = "james.to@example.com";
        String username = "james.to";
        String password = "5678";
        String bio = "I like to skateboard";
        String image = "https://i.stack.imgur.com/xHWG8.jpg";
        UpdateUserRequest updateRequest = new UpdateUserRequest(email, username, password, bio, image);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/user")
                .header("Authorization", "Token " + userVO.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("user", updateRequest))));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("james.to@example.com"))
                .andExpect(jsonPath("$.user.username").value("james.to"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.bio").value("I like to skateboard"))
                .andExpect(jsonPath("$.user.image").value("https://i.stack.imgur.com/xHWG8.jpg"))
                .andDo(print());
    }
}
