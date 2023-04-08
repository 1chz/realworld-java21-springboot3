package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
record UserSignUpRequest(String email, String username, String password) {
    public UserSignUpRequest encryptPasswords(String encoded) {
        return new UserSignUpRequest(email, username, encoded);
    }

    public User toUser() {
        return User.builder().email(email).username(username).password(password).build();
    }
}
