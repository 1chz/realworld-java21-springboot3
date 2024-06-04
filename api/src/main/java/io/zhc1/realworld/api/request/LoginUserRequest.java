package io.zhc1.realworld.api.request;

public record LoginUserRequest(Params user) {
    public LoginUserRequest(String email, String password) {
        this(new Params(email, password));
    }

    public record Params(String email, String password) {}
}
