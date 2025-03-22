package io.zhc1.realworld.api.response;

import io.zhc1.realworld.core.model.ArticleDetails;

public record SingleArticleResponse(ArticleResponse article) {
    public SingleArticleResponse(ArticleDetails articleDetails) {
        this(new ArticleResponse(
                articleDetails.article(),
                articleDetails.articleTags(),
                articleDetails.favorited(),
                articleDetails.favoritesCount()));
    }
}
