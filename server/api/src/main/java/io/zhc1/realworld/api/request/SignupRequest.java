package io.zhc1.realworld.api.request;

public record SignupRequest(Params user) {
    public record Params(String email, String username, String password) {}
}
