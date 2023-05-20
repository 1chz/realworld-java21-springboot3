package io.github.shirohoo.realworld.domain.user;

import io.github.shirohoo.realworld.domain.article.Article;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(unique = true)
    private String email;

    @Setter
    private String password;

    @Setter
    @Column(unique = true)
    private String username;

    @Setter
    private String bio;

    @Setter
    private String image;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> followings = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "followings")
    private final Set<User> followers = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "favorites")
    private final Set<Article> favoritedArticles = new HashSet<>();

    @Transient
    private String token;

    public ProfileVO fetchProfileBy(User following) {
        if (following == null) {
            return new ProfileVO(null, this);
        }

        return new ProfileVO(following, this);
    }

    public ProfileVO follow(User target) {
        if (this.followings.contains(target)) {
            return new ProfileVO(this, target);
        }

        this.followings.add(target);
        target.followers.add(this);

        return target.fetchProfileBy(this);
    }

    public ProfileVO unfollow(User target) {
        if (!this.followings.contains(target)) {
            return new ProfileVO(this, target);
        }

        this.followings.remove(target);
        target.followers.remove(this);

        return target.fetchProfileBy(this);
    }

    public boolean isFollowing(User target) {
        return this.followings.contains(target);
    }

    public boolean hasFollower(User target) {
        return this.followers.contains(target);
    }

    public void favorite(Article article) {
        if (this.favoritedArticles.contains(article)) {
            return;
        }

        this.favoritedArticles.add(article);
        article.favorite(this);
    }

    public void unfavorite(Article article) {
        if (!this.favoritedArticles.contains(article)) {
            return;
        }

        this.favoritedArticles.remove(article);
        article.unfavorite(this);
    }

    public boolean hasFavorite(Article article) {
        return this.favoritedArticles.contains(article);
    }

    public User setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User other && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
