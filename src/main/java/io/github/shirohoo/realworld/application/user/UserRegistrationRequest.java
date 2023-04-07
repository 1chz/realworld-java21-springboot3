package io.github.shirohoo.realworld.application.user;

import io.github.shirohoo.realworld.domain.user.User;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
record UserRegistrationRequest(String email, String username, String password) {
    public UserRegistrationRequest encryptPasswords(String encoded) {
        return new UserRegistrationRequest(email, username, encoded);
    }

    public User toUser() {
        return User.builder().email(email).username(username).password(password).build();
    }
}
