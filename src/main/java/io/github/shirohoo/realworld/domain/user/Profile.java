package io.github.shirohoo.realworld.domain.user;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@AllArgsConstructor
@JsonRootName("profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
    @Column(unique = true)
    private String username;

    private String bio;

    private String image;

    @Builder.Default
    private boolean following = false;

    public Profile(Profile profile, Boolean following) {
        this.username = profile.username;
        this.bio = profile.bio;
        this.image = profile.image;
        this.following = following;
    }

    public Profile update(UserVO user) {
        if (StringUtils.hasText(user.username())) this.username = user.username();
        this.bio = user.bio();
        this.image = user.image();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Profile) obj;
        return Objects.equals(this.username, that.username)
                && Objects.equals(this.bio, that.bio)
                && Objects.equals(this.image, that.image)
                && Objects.equals(this.following, that.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, bio, image, following);
    }

    @Override
    public String toString() {
        return "Profile[" + "username="
                + username + ", " + "bio="
                + bio + ", " + "image="
                + image + ", " + "following="
                + following + ']';
    }
}
