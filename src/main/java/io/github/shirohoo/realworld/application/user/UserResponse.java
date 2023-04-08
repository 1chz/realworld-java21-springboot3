package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
record UserResponse(String email, String token, String username, String bio, String image) {
    UserResponse(User user) {
        this(user.getEmail(), user.getToken(), user.getUsername(), user.getBio(), user.getImage());
    }
}
