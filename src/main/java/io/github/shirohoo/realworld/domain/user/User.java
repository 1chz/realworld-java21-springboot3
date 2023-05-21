package io.github.shirohoo.realworld.domain.user;

import io.github.shirohoo.realworld.domain.article.Article;
import io.github.shirohoo.realworld.domain.article.ArticleFavorite;
import io.github.shirohoo.realworld.domain.article.ArticleFavoriteId;
import io.github.shirohoo.realworld.domain.article.ArticleVO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    public ProfileVO follow(@NotNull User userToFollow) {
        Follow follow = Follow.builder()
                .id(new FollowId(this.getId(), userToFollow.getId()))
                .from(this)
                .to(userToFollow)
                .build();

        if (this.following.contains(follow)) {
            return new ProfileVO(this, userToFollow);
        }

        this.following.add(follow);
        userToFollow.getFollower().add(follow);

        return new ProfileVO(this, userToFollow);
    }

    public ProfileVO unfollow(@NotNull User userToUnfollow) {
        Follow unfollow = null;
        for (Follow following : this.following) {
            if (following.getTo().equals(userToUnfollow)) {
                unfollow = following;
                break;
            }
        }
        if (unfollow != null) {
            this.following.remove(unfollow);
            userToUnfollow.getFollower().remove(unfollow);
        }

        return new ProfileVO(this, userToUnfollow);
    }

    public boolean isFollowing(User target) {
        return this.following.stream().map(Follow::getTo).anyMatch(target::equals);
    }

    public ArticleVO favorite(Article article) {
        ArticleFavorite like = ArticleFavorite.builder()
                .id(new ArticleFavoriteId(this.getId(), article.getId()))
                .user(this)
                .article(article)
                .build();

        this.favoriteArticles.add(like);
        article.getFavoritedUsers().add(like);

        return article.fetchDetailBy(this);
    }

    public ArticleVO unfavorite(Article article) {
        ArticleFavorite articleFavorite = null;
        for (ArticleFavorite like : this.getFavoriteArticles()) {
            if (like.getArticle().equals(article)) {
                articleFavorite = like;
                break;
            }
        }
        if (articleFavorite != null) {
            this.getFavoriteArticles().remove(articleFavorite);
            article.getFavoritedUsers().remove(articleFavorite);
        }

        return article.fetchDetailBy(this);
    }

    public boolean isFavorited(Article article) {
        return this.favoriteArticles.stream().map(ArticleFavorite::getArticle).anyMatch(article::equals);
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
        return Objects.hash(id);
    }
}
