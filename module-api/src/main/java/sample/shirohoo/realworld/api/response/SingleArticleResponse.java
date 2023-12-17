package sample.shirohoo.realworld.api.response;

import sample.shirohoo.realworld.core.model.ArticleInfo;

public record SingleArticleResponse(ArticleResponse article) {
    public SingleArticleResponse(ArticleInfo articleInfo) {
        this(new ArticleResponse(
                articleInfo.article(),
                articleInfo.articleTags(),
                articleInfo.favorited(),
                articleInfo.favoritesCount()));
    }
}
