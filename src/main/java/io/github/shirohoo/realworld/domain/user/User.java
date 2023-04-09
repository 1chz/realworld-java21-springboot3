package io.github.shirohoo.realworld.domain.user;

import io.github.shirohoo.realworld.domain.content.Article;

import java.util.HashSet;
import java.util.List;
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
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Accessors(fluent = true, chain = true)
public class User {
    @Id
    @Setter(AccessLevel.PRIVATE)
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
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> followings = new HashSet<>();

    @Builder.Default
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "followings", cascade = CascadeType.ALL)
    private Set<User> followers = new HashSet<>();

    @Builder.Default
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "favorites", cascade = CascadeType.ALL)
    private Set<Article> favoritedArticles = new HashSet<>();

    @Transient
    private String token;

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

    public List<User> followings() {
        return List.copyOf(this.followings);
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
