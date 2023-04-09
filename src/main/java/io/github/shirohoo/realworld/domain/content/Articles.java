package io.github.shirohoo.realworld.domain.content;

import io.github.shirohoo.realworld.domain.user.Profile;
import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;

public record Articles(
        String slug,
        String title,
        String description,
        String body,
        String[] tagList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean favorited,
        int favoritesCount,
        Profile author) {
    public Articles(User me, Article article) {
        this(
                article.slug(),
                article.title(),
                article.description(),
                article.content(),
                article.tagList(),
                article.createdAt(),
                article.updatedAt(),
                article.favoritedBy(me),
                article.favoritesCount(),
                new Profile(me, article.author()));
    }
}
