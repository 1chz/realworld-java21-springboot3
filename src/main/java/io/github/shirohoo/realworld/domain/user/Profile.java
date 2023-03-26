package io.github.shirohoo.realworld.domain.user;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("profile")
public record Profile(String username, String bio, String image, boolean following) {
    public Profile(User user, boolean following) {
        this(user.getUsername(), user.getBio(), user.getImage(), following);
    }
}
