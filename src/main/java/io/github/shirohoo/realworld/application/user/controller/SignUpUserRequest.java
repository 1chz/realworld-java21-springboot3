package io.github.shirohoo.realworld.application.user.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record SignUpUserRequest(String email, String username, String password) {}
