package io.zhc1.realworld.api.response;

import io.zhc1.realworld.core.model.User;

public record ProfileResponse(String username, String bio, String image, boolean following) {
    public static ProfileResponse from(User user) {
        return from(user, false);
    }

    public static ProfileResponse from(User user, boolean following) {
        return new ProfileResponse(user.getUsername(), user.getBio(), user.getImageUrl(), following);
    }
}
