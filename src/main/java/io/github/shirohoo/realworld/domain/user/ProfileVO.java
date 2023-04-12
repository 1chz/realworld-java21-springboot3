package io.github.shirohoo.realworld.domain.user;

public record ProfileVO(String username, String bio, String image, boolean following) {
    public ProfileVO(User me, User to) {
        this(to.username(), to.bio(), to.image(), me != null && me.isFollowing(to));
    }
}
