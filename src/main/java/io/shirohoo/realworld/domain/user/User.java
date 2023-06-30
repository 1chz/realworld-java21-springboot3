package io.shirohoo.realworld.domain.user;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.shirohoo.realworld.domain.article.Article;
import io.shirohoo.realworld.domain.article.ArticleFavorite;
import io.shirohoo.realworld.domain.article.ArticleVO;

@Entity
@Getter
@Builder
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(length = 30, nullable = false, unique = true)
    private String username;

    @Builder.Default
    @Column(length = 500, nullable = false)
    private String bio = "";

    private String image;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "from", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> following = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "to", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> follower = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleFavorite> favoriteArticles = new HashSet<>();

    @Transient
    private String token;

    @Transient
    @Builder.Default
    private boolean anonymous = false;

    public static User anonymous() {
        return User.builder().id(null).anonymous(true).build();
    }

    public boolean isAnonymous() {
        return this.id == null && this.anonymous;
    }

    public boolean isAlreadyFollowing(User target) {
        if (target == null || target.isAnonymous()) {
            throw new IllegalArgumentException("target must not be null or anonymous");
        }

        Follow follow = new Follow(this, target);
        return this.following.stream().anyMatch(follow::equals);
    }

    public boolean isAlreadyFavorite(Article article) {
        if (article == null) {
            throw new IllegalArgumentException("article must not be null");
        }

        ArticleFavorite articleFavorite = new ArticleFavorite(this, article);
        return this.favoriteArticles.stream().anyMatch(articleFavorite::equals);
    }

    public ProfileVO follow(User target) {
        if (target == null || target.isAnonymous()) {
            throw new IllegalArgumentException("target must not be null or anonymous");
        }

        if (isAlreadyFollowing(target)) {
            return new ProfileVO(this, target);
        }

        Follow follow = new Follow(this, target);
        addFollowingToCurrentUser(follow);
        addFollowerToTargetUser(follow);

        return new ProfileVO(this, target);
    }

    public ProfileVO unfollow(User target) {
        if (target == null || target.isAnonymous()) {
            throw new IllegalArgumentException("target must not be null or anonymous");
        }

        findFollowing(target).ifPresent(follow -> {
            this.removeFollowing(follow);
            target.removeFollower(follow);
        });

        return new ProfileVO(this, target);
    }

    public ArticleVO favorite(Article article) {
        if (article == null) {
            throw new IllegalArgumentException("article must not be null");
        }

        if (isAlreadyFavorite(article)) {
            return new ArticleVO(this, article);
        }

        ArticleFavorite articleFavorite = new ArticleFavorite(this, article);
        addFavoriteArticle(articleFavorite);
        addThisUserToFavorite(articleFavorite);

        return new ArticleVO(this, article);
    }

    public ArticleVO unfavorite(Article article) {
        if (article == null) {
            throw new IllegalArgumentException("article must not be null");
        }

        findArticleFavorite(article).ifPresent(articleFavorite -> {
            removeFavoriteArticle(articleFavorite);
            removeUserFromFavorite(articleFavorite);
        });

        return new ArticleVO(this, article);
    }

    public List<User> followUsers() {
        return this.following.stream().map(Follow::getTo).toList();
    }

    public User setToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token must not be null or blank");
        }

        this.token = token;
        return this;
    }

    public void updateEmail(String email) {
        if (email == null || email.isBlank() || this.email.equals(email)) {
            return;
        }

        // Note: You can add some more validations here if you want. (ex. regex)
        this.email = email;
    }

    public void updateUsername(String username) {
        if (username == null || username.isBlank() || this.username.equals(username)) {
            return;
        }

        // Note: You can add some more validations here if you want. (ex. regex)
        this.username = username;
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String plaintext) {
        if (passwordEncoder == null) {
            return;
        }

        if (plaintext == null || plaintext.isBlank()) {
            return;
        }

        // Note: You can add some more validations here if you want. (ex. regex)
        this.password = passwordEncoder.encode(plaintext);
    }

    public void updateBio(String bio) {
        if (bio == null || bio.isBlank()) {
            return;
        }

        this.bio = bio;
    }

    public void updateImage(String imageUrl) {
        if (imageUrl != null && imageUrl.isBlank()) {
            return;
        }

        this.image = imageUrl;
    }

    private void addFollowingToCurrentUser(Follow follow) {
        this.following.add(follow);
    }

    private void addFollowerToTargetUser(Follow follow) {
        follow.getTo().getFollower().add(follow);
    }

    private Optional<Follow> findFollowing(User target) {
        return this.following.stream().filter(target::isFollowing).findFirst();
    }

    private boolean isFollowing(Follow follow) {
        return follow.getTo().equals(this);
    }

    private void removeFollowing(Follow follow) {
        this.following.remove(follow);
    }

    private void removeFollower(Follow follow) {
        this.follower.remove(follow);
    }

    private void addFavoriteArticle(ArticleFavorite articleFavorite) {
        this.favoriteArticles.add(articleFavorite);
    }

    private void addThisUserToFavorite(ArticleFavorite articleFavorite) {
        articleFavorite.getArticle().getFavoriteUsers().add(articleFavorite);
    }

    private Optional<ArticleFavorite> findArticleFavorite(Article article) {
        return this.favoriteArticles.stream().filter(article::equalsArticle).findFirst();
    }

    private void removeFavoriteArticle(ArticleFavorite articleFavorite) {
        this.favoriteArticles.remove(articleFavorite);
    }

    private void removeUserFromFavorite(ArticleFavorite articleFavorite) {
        articleFavorite.getArticle().getFavoriteUsers().remove(articleFavorite);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User other && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
