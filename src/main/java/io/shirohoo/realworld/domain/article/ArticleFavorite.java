package io.shirohoo.realworld.domain.article;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.shirohoo.realworld.domain.user.User;

@Entity
@Getter
@Builder
@Table(name = "article_favorite")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleFavorite {
    @EmbeddedId
    private ArticleFavoriteId id;

    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ArticleFavorite(User user, Article article) {
        this.id = new ArticleFavoriteId(user.getId(), article.getId());
        this.user = user;
        this.article = article;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ArticleFavorite other
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.user, other.user)
                && Objects.equals(this.article, other.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.user, this.article);
    }
}
