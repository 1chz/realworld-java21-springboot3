package io.shirohoo.realworld.application.user.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record UpdateUserRequest(String email, String username, String password, String bio, String image) {
    public UpdateUserRequest {
        if (email == null) email = "";
        if (username == null) username = "";
        if (password == null) password = "";
        if (bio == null) bio = "";
        if (image == null) image = "";
    }
}
