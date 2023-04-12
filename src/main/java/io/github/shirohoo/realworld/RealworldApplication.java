package io.github.shirohoo.realworld;

import io.github.shirohoo.realworld.application.user.SignUpUserRequest;
import io.github.shirohoo.realworld.application.user.UserService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class RealworldApplication implements CommandLineRunner {
    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(RealworldApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userService.signUp(new SignUpUserRequest("gary@example.org", "gary", "1234"));
        userService.signUp(new SignUpUserRequest("charles@example.org", "charles", "1234"));
    }
}
