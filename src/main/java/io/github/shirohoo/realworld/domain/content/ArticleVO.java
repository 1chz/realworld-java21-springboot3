package io.github.shirohoo.realworld.domain.content;

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
                article.slug(),
                article.title(),
                article.description(),
                article.content(),
                article.tags(),
                article.createdAt(),
                article.updatedAt(),
                article.hasFavorited(me),
                article.favoritesCount(),
                new ProfileVO(me, article.author()));
    }
}
