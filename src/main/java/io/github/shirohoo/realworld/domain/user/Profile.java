package io.github.shirohoo.realworld.domain.user;

public record Profile(String username, String bio, String image, boolean following) {
    public Profile(User me, User to) {
        this(to.username(), to.bio(), to.image(), me != null && me.isFollowing(to));
    }
}
