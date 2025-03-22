package io.zhc1.realworld.api.response;

public record UserResponse(String email, String token, String username, String bio, String image) {}
