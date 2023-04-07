package io.github.shirohoo.realworld.application.user;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
record UserResponse(String email, String token, String username, String bio, String image) {}
