package sample.shirohoo.realworld.api.response;

import sample.shirohoo.realworld.core.model.ArticleDetails;

public record SingleArticleResponse(ArticleResponse article) {
    public SingleArticleResponse(ArticleDetails articleDetails) {
        this(new ArticleResponse(
                articleDetails.article(),
                articleDetails.articleTags(),
                articleDetails.favorited(),
                articleDetails.favoritesCount()));
    }
}
