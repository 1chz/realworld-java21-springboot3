package io.github.shirohoo.realworld.application.user;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record UserLoginRequest(String email, String password) {}
