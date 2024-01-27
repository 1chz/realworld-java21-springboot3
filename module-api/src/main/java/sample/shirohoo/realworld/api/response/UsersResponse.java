package sample.shirohoo.realworld.api.response;

import sample.shirohoo.realworld.core.model.User;

public record UsersResponse(UserResponse user) {
    public static UsersResponse from(User user, String token) {
        return new UsersResponse(
                new UserResponse(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImageUrl()));
    }
}
