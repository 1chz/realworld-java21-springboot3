package io.github.shirohoo.realworld.application.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.shirohoo.realworld.infrastructure.user.UserJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SignUpControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserJpaRepository userJpaRepository;

    @BeforeEach
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
                                                    "username": "jake",
                                                    "email": "jake@jake.jake",
                                                    "password": "jakejake"
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
}
