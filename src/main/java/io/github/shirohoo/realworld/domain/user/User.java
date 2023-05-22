package io.github.shirohoo.realworld.domain.user;

import io.github.shirohoo.realworld.domain.article.Article;
import io.github.shirohoo.realworld.domain.article.ArticleFavorite;
import io.github.shirohoo.realworld.domain.article.ArticleFavoriteId;
import io.github.shirohoo.realworld.domain.article.ArticleVO;

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
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Setter
    @Column(length = 200, nullable = false)
    private String password;

    @Setter
    @Column(length = 30, nullable = false, unique = true)
    private String username;

    @Setter
    @Builder.Default
    @Column(length = 500, nullable = false)
    private String bio = "";

    @Setter
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

    public boolean isAlreadyFollowing(User target) {
        if (target == null) {
            return false;
        }

        Follow follow = createFollow(this, target);
        return this.following.stream().anyMatch(follow::equals);
    }

    public ProfileVO follow(@NotNull User target) {
        if (isAlreadyFollowing(target)) {
            return new ProfileVO(this, target);
        }

        Follow follow = createFollow(this, target);
        addFollowingToCurrentUser(follow);
        addFollowerToTargetUser(follow);

        return new ProfileVO(this, target);
    }

    private Follow createFollow(User from, User to) {
        return Follow.builder()
                .id(new FollowId(from.getId(), to.getId()))
                .from(from)
                .to(to)
                .build();
    }

    private void addFollowingToCurrentUser(Follow follow) {
        this.following.add(follow);
    }

    private void addFollowerToTargetUser(Follow follow) {
        follow.getTo().getFollower().add(follow);
    }

    public ProfileVO unfollow(@NotNull User target) {
        findFollowing(target).ifPresent(follow -> {
            this.removeFollowing(follow);
            target.removeFollower(follow);
        });

        return new ProfileVO(this, target);
    }

    private Optional<Follow> findFollowing(User target) {
        return this.following.stream().filter(target::isFollowing).findFirst();
    }

    private boolean isFollowing(@NotNull Follow follow) {
        return follow.getTo().equals(this);
    }

    private void removeFollowing(@NotNull Follow follow) {
        this.following.remove(follow);
    }

    private void removeFollower(@NotNull Follow follow) {
        this.follower.remove(follow);
    }

    public boolean isAlreadyFavorite(@NotNull Article article) {
        ArticleFavorite articleFavorite = createArticleFavorite(this, article);
        return this.favoriteArticles.stream().anyMatch(articleFavorite::equals);
    }

    private ArticleFavorite createArticleFavorite(@NotNull User user, @NotNull Article article) {
        return ArticleFavorite.builder()
                .id(new ArticleFavoriteId(user.getId(), article.getId()))
                .user(user)
                .article(article)
                .build();
    }

    public ArticleVO favorite(@NotNull Article article) {
        if (isAlreadyFavorite(article)) {
            return new ArticleVO(this, article);
        }

        ArticleFavorite articleFavorite = createArticleFavorite(this, article);
        addFavoriteArticle(articleFavorite);
        addThisUserToFavorite(articleFavorite);

        return new ArticleVO(this, article);
    }

    private void addFavoriteArticle(@NotNull ArticleFavorite articleFavorite) {
        this.favoriteArticles.add(articleFavorite);
    }

    private void addThisUserToFavorite(@NotNull ArticleFavorite articleFavorite) {
        articleFavorite.getArticle().getFavoriteUsers().add(articleFavorite);
    }

    public ArticleVO unfavorite(@NotNull Article article) {
        findArticleFavorite(article).ifPresent(articleFavorite -> {
            removeFavoriteArticle(articleFavorite);
            removeUserFromFavorite(articleFavorite);
        });

        return new ArticleVO(this, article);
    }

    private Optional<ArticleFavorite> findArticleFavorite(@NotNull Article article) {
        return this.favoriteArticles.stream().filter(article::equalsArticle).findFirst();
    }

    private void removeFavoriteArticle(@NotNull ArticleFavorite articleFavorite) {
        this.favoriteArticles.remove(articleFavorite);
    }

    private void removeUserFromFavorite(@NotNull ArticleFavorite articleFavorite) {
        articleFavorite.getArticle().getFavoriteUsers().remove(articleFavorite);
    }

    public List<User> getFollowing() {
        return this.following.stream().map(Follow::getTo).toList();
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
        return Objects.hash(this.id);
    }
}
