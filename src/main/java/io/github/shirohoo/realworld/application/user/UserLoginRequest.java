package io.github.shirohoo.realworld.application.user;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
record UserLoginRequest(String email, String password) {}
