package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.infrastructure.user.UserJpaRepository;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    UUID jakeGuid;

    String jakeToken;

    @BeforeEach
    void setUp() throws Exception {
        User jake = User.builder()
                .email("jake@jake.jake")
                .password(passwordEncoder.encode("jakejake"))
                .username("jake")
                .bio("I work at statefarm")
                .build();

        User james = User.builder()
                .email("james@james.james")
                .password(passwordEncoder.encode("jamesjames"))
                .username("james")
                .bio("I work at statefarm")
                .build();

        userJpaRepository.save(jake);
        userJpaRepository.save(james);

        MockHttpServletResponse response = mockMvc.perform(
                        post("/api/users/login")
                                .header("Content-Type", "application/json")
                                .content(
                                        """
                        {
                          "user":{
                            "email": "jake@jake.jake",
                            "password": "jakejake"
                          }
                        }
                        """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("jake@jake.jake"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value("jake"))
                .andExpect(jsonPath("$.user.bio").value("I work at statefarm"))
                .andExpect(jsonPath("$.user.image").isEmpty())
                .andReturn()
                .getResponse();

        String jsonContent = response.getContentAsString();
        jakeGuid = UUID.fromString(JsonPath.read(jsonContent, "$.user.guid"));
        jakeToken = JsonPath.read(jsonContent, "$.user.token");
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Sign up is successful")
    void shouldSucceedWhenSignUp() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "user":{
                                                    "username": "oliver",
                                                    "email": "oliver@oliver.oliver",
                                                    "password": "oliver"
                                                  }
                                                }
                                                """))
                .andDo(print())
                .andExpect(status().isTemporaryRedirect());
    }

    @Test
    @DisplayName("Response 400 if username is empty when signing up")
    void shouldFailWhenBlankUsername() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "user":{
                                                    "username": "",
                                                    "email": "jake@jake.jake",
                                                    "password": "jakejake"
                                                  }
                                                }
                                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Response 400 if email is empty when signing up")
    void shouldFailWhenBlankEmail() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "user":{
                                                    "username": "jake",
                                                    "email": "",
                                                    "password": "jakejake"
                                                  }
                                                }
                                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Response 400 if email format is incorrect when signing up")
    void shouldFailWhenInvalidEmailFormats() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "user":{
                                                    "username": "jake",
                                                    "email": "invalid.email",
                                                    "password": "jakejake"
                                                  }
                                                }
                                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Response 400 if password is empty when signing up")
    void shouldFailWhenBlankPassword() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "user":{
                                                    "username": "jake",
                                                    "email": "jake@jake.jake",
                                                    "password": ""
                                                  }
                                                }
                                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Can search the user who has been issued an authentication token")
    void shouldSucceedWhenGetUser() throws Exception {
        mockMvc.perform(get("/api/user")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + jakeToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("jake@jake.jake"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value("jake"))
                .andExpect(jsonPath("$.user.bio").value("I work at statefarm"))
                .andExpect(jsonPath("$.user.image").isEmpty());
    }

    @Test
    @DisplayName("Response 401 if no authorization header")
    void shouldFailIfNoAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/user").header("Content-Type", "application/json"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Can update the user who has been issued an authentication token")
    void shouldSucceedWhenUpdateUser() throws Exception {
        mockMvc.perform(
                        put("/api/user")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + jakeToken)
                                .content(
                                        """
                        {
                          "user":{
                            "email": "jake@jake.jake",
                            "bio": "I like to skateboard",
                            "image": "https://i.stack.imgur.com/xHWG8.jpg"
                          }
                        }
                        """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.email").value("jake@jake.jake"))
                .andExpect(jsonPath("$.user.token").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value("jake"))
                .andExpect(jsonPath("$.user.bio").value("I like to skateboard"))
                .andExpect(jsonPath("$.user.image").value("https://i.stack.imgur.com/xHWG8.jpg"));
    }

    @Test
    @DisplayName("Response 400 if duplicated username")
    void shouldFailIfDuplicatedUsername() throws Exception {
        mockMvc.perform(
                        put("/api/user")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + jakeToken)
                                .content(
                                        """
                {
                  "user":{
                    "username": "james"
                  }
                }
                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Response 400 if duplicated email")
    void shouldFailIfDuplicatedEmail() throws Exception {
        mockMvc.perform(
                        put("/api/user")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + jakeToken)
                                .content(
                                        """
                {
                  "user":{
                    "email": "james@james.james"
                  }
                }
                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
