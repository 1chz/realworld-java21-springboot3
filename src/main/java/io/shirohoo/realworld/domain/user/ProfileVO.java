package io.shirohoo.realworld.domain.user;

public record ProfileVO(String username, String bio, String image, boolean following) {
    public ProfileVO(User from, User to) {
        this(to.getUsername(), to.getBio(), to.getImage(), from != null && from.isAlreadyFollowing(to));
    }

    public static ProfileVO following(User target) {
        return new ProfileVO(target.getUsername(), target.getBio(), target.getImage(), true);
    }

    public static ProfileVO unfollowing(User target) {
        return new ProfileVO(target.getUsername(), target.getBio(), target.getImage(), false);
    }
}
