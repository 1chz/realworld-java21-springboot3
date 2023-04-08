package io.github.shirohoo.realworld.domain.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String username;

    private String bio;

    private String image;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> followings = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "followings", cascade = CascadeType.ALL)
    private Set<User> followers = new HashSet<>();

    @Transient
    private String token;

    public static User withEmailUsername(String email, String username) {
        return User.builder().email(email).username(username).build();
    }

    public User bindToken(String token) {
        this.token = token;
        return this;
    }

    public void follow(User to) {
        if (this.followings.contains(to)) throw new IllegalStateException("Already following");
        this.followings.add(to);
        to.followers.add(this);
    }

    public void unfollow(User to) {
        if (!this.followings.contains(to)) throw new IllegalStateException("Not following");
        this.followings.remove(to);
        to.followers.remove(this);
    }

    public boolean isFollowing(User to) {
        return this.followings.contains(to);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String encoded) {
        this.password = encoded;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User other) return this.id.equals(other.id);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
