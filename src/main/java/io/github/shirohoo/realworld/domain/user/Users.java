package io.github.shirohoo.realworld.domain.user;

public record Users(String email, String token, String username, String bio, String image) {
    public Users(User user) {
        this(user.email(), user.token(), user.username(), user.bio(), user.image());
    }
}
