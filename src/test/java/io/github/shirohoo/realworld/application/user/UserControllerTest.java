package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        // - sign up
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");
        userService.signUp(signUpRequest);

        // - login request
        UserLoginRequest loginRequest = new UserLoginRequest("james@gmail.com", "1234");

        // when
        ResultActions resultActions = sut.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("james@gmail.com"))
                .andExpect(jsonPath("$.user.username").value("james"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.bio").isEmpty())
                .andExpect(jsonPath("$.user.image").isEmpty());
    }

    @Test
    @DisplayName("유저 API는 로그인 된 유저의 정보를 제공한다")
    void getCurrentUser() throws Exception {
        // given
        // - sign up
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");
        userService.signUp(signUpRequest);

        // - login and get authorization token
        UserLoginRequest loginRequest = new UserLoginRequest("james@gmail.com", "1234");
        String jamesToken = userService.login(loginRequest).token();

        // when
        ResultActions resultActions = sut.perform(get("/api/user").header("Authorization", "Bearer " + jamesToken));

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
    @DisplayName("유저 API는 회원정보 업데이트 API를 제공한다")
    void update() throws Exception {
        // given
        // - sign up
        UserSignUpRequest signUpRequest = new UserSignUpRequest("james@gmail.com", "james", "1234");
        userService.signUp(signUpRequest);

        // - login and get authorization token
        UserLoginRequest loginRequest = new UserLoginRequest("james@gmail.com", "1234");
        UserResponse userResponse = userService.login(loginRequest);

        // - update request
        String email = "james.to@gmail.com";
        String username = "james.to";
        String password = "5678";
        String bio = "I like to skateboard";
        String image = "https://i.stack.imgur.com/xHWG8.jpg";
        UserUpdateRequest updateRequest = new UserUpdateRequest(email, username, password, bio, image);

        // when
        ResultActions resultActions = sut.perform(put("/api/user")
                .header("Authorization", "Bearer " + userResponse.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("james.to@gmail.com"))
                .andExpect(jsonPath("$.user.username").value("james.to"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.bio").value("I like to skateboard"))
                .andExpect(jsonPath("$.user.image").value("https://i.stack.imgur.com/xHWG8.jpg"));
    }
}
