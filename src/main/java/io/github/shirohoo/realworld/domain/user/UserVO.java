package io.github.shirohoo.realworld.domain.user;

public record UserVO(String email, String token, String username, String bio, String image) {
    public UserVO(User user) {
        this(user.email(), user.token(), user.username(), user.bio(), user.image());
    }
}
