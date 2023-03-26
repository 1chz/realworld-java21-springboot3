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

    UUID guid;

    String token;

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
        guid = UUID.fromString(JsonPath.read(jsonContent, "$.user.guid"));
        token = JsonPath.read(jsonContent, "$.user.token");
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    void shouldSuccessWhenSignUpUser() throws Exception {
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
    void shouldFailureWhenBlankUsername() throws Exception {
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
    void shouldFailureWhenBlankEmail() throws Exception {
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
    void shouldFailureWhenInvalidEmailFormats() throws Exception {
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
    void shouldFailureWhenBlankPassword() throws Exception {
        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content(
                                        """
                                                {
                                                  "user":{
                                                    "username": "",
                                                    "email": "jake@jake.jake",
                                                    "password": ""
                                                  }
                                                }
                                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSuccessWhenGetUser() throws Exception {
        mockMvc.perform(get("/api/user")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token))
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
    void shouldFailureIfNoAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/user").header("Content-Type", "application/json"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSuccessWhenUpdateUser() throws Exception {
        mockMvc.perform(
                        put("/api/user")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
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
    void shouldFailureIfDuplicatedUsername() throws Exception {
        mockMvc.perform(
                        put("/api/user")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
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
    void shouldFailureIfDuplicatedEmail() throws Exception {
        mockMvc.perform(
                        put("/api/user")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
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
