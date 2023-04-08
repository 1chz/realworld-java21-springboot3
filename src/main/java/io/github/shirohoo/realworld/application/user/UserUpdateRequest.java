package io.github.shirohoo.realworld.application.user;

record UserUpdateRequest(String email, String username, String password, String bio, String image) {}
