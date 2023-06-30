package io.shirohoo.realworld.application.user.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record LoginRequest(String email, String password) {}
