package sample.shirohoo.realworld.api.response;

import sample.shirohoo.realworld.core.model.User;

public record ProfileResponse(String username, String bio, String image, boolean following) {
    public static ProfileResponse from(User user) {
        return from(user, false);
    }

    public static ProfileResponse from(User user, boolean following) {
        return new ProfileResponse(user.getEmail(), user.getBio(), user.getImageUrl(), following);
    }
}
