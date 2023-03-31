package io.github.shirohoo.realworld.application.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.shirohoo.realworld.domain.user.Profile;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UsernamePasswordAuthenticationProcessingFilterTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User jake = User.builder()
                .email("jake@jake.jake")
                .password(passwordEncoder.encode("jakejake"))
                .profile(Profile.builder()
                        .username("jake")
                        .bio("I work at statefarm")
                        .build())
                .build();

        userRepository.save(jake);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Login successfully")
    void shouldSucceedWhenAttemptAuthentication() throws Exception {
        mockMvc.perform(
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
                .andExpect(jsonPath("$.user.image").isEmpty());
    }

    @Test
    @DisplayName("Login failed when missing input content type: application/json")
    void shouldFailWhenMissingInputContentType() throws Exception {
        mockMvc.perform(
                        post("/api/users/login")
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
                .andExpect(status().isBadRequest());
    }
}
