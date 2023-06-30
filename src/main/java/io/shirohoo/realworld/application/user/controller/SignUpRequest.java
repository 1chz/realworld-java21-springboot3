package io.shirohoo.realworld.application.user.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record SignUpRequest(String email, String username, String password) {}
