package io.github.shirohoo.realworld.domain.user;

import io.github.shirohoo.realworld.domain.content.Article;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> followings = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "followings", cascade = CascadeType.ALL)
    private final Set<User> followers = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "favorites", cascade = CascadeType.ALL)
    private final Set<Article> favoritedArticles = new HashSet<>();

    @Transient
    private String token;

    public static ProfileVO retrievesProfile(User me, User to) {
        if (me == null) return new ProfileVO(null, to);
        return new ProfileVO(me, to);
    }

    public ProfileVO follow(User to) {
        if (this.followings.contains(to)) return new ProfileVO(this, to);
        this.followings.add(to);
        to.followers.add(this);
        return User.retrievesProfile(this, to);
    }

    public ProfileVO unfollow(User to) {
        if (!this.followings.contains(to)) return new ProfileVO(this, to);
        this.followings.remove(to);
        to.followers.remove(this);
        return User.retrievesProfile(this, to);
    }

    public boolean isFollowing(User to) {
        return this.followings.contains(to);
    }

    public void favorite(Article article) {
        if (this.favoritedArticles.contains(article)) return;
        this.favoritedArticles.add(article);
        article.favoritedBy(this);
    }

    public void unfavorite(Article article) {
        if (!this.favoritedArticles.contains(article)) return;
        this.favoritedArticles.remove(article);
        article.unfavoritedBy(this);
    }

    public Set<User> followings() {
        return Set.copyOf(this.followings);
    }

    public Set<User> followers() {
        return Set.copyOf(this.followers);
    }

    public Set<Article> favoritedArticles() {
        return Set.copyOf(this.favoritedArticles);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User other) return Objects.equals(this.id, other.id);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
