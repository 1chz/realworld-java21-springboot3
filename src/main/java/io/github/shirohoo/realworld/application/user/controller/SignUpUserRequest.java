package io.github.shirohoo.realworld.application.user.controller;

import io.github.shirohoo.realworld.domain.user.User;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record SignUpUserRequest(String email, String username, String password) {
    public User toUser() {
        return User.builder().email(email).username(username).password(password).build();
    }
}
