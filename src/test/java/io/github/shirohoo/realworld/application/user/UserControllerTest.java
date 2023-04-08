package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("유저 API")
class UserControllerTest {
    @Autowired
    private MockMvc sut;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 API는 회원가입 API를 제공한다")
    void signUp() throws Exception {
        // given
        // - sign up request
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");

        // when
        ResultActions resultActions = sut.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        // then
        resultActions
                .andExpect(status().isTemporaryRedirect())
                .andExpect(view().name("redirect:/api/users/login"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", new UserLoginRequest("james@gmail.com", "1234")));
    }

    @Test
    @DisplayName("유저 API는 로그인 API를 제공한다")
    void login() throws Exception {
        // given
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");
        userService.signUp(signUpRequest);

        // when
        ResultActions resultActions = sut.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserLoginRequest("james@gmail.com", "1234"))));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("james@gmail.com"))
                .andExpect(jsonPath("$.user.username").value("james"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.bio").isEmpty())
                .andExpect(jsonPath("$.user.image").isEmpty());
    }

    @Test
    @DisplayName("유저 API는 인증된 현재 유저의 정보를 제공한다")
    void getCurrentUser() throws Exception {
        // given
        // - sign up
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");
        userService.signUp(signUpRequest);

        // - login and get authorization token
        UserLoginRequest loginRequest = new UserLoginRequest("james@gmail.com", "1234");
        UserResponse userResponse = userService.login(loginRequest);

        // when
        ResultActions resultActions =
                sut.perform(get("/api/user").header("Authorization", "Bearer " + userResponse.token()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("james@gmail.com"))
                .andExpect(jsonPath("$.user.username").value("james"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.bio").isEmpty())
                .andExpect(jsonPath("$.user.image").isEmpty());
    }
}
