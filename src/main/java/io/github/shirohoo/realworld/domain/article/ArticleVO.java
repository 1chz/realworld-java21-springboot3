package io.github.shirohoo.realworld.domain.article;

import io.github.shirohoo.realworld.domain.user.ProfileVO;
import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;

public record ArticleVO(
        String slug,
        String title,
        String description,
        String body,
        String[] tagList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean favorited,
        int favoritesCount,
        ProfileVO author) {
    public ArticleVO(User me, Article article) {
        this(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getContent(),
                article.tags(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.isFavoriteBy(me),
                article.favoriteCount(),
                new ProfileVO(me, article.getAuthor()));
    }
}
