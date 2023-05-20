package io.github.shirohoo.realworld.domain.user;

public record ProfileVO(String username, String bio, String image, boolean following) {
    public ProfileVO(User following, User follower) {
        this(
                follower.getUsername(),
                follower.getBio(),
                follower.getImage(),
                following != null && following.isFollowing(follower));
    }
}
