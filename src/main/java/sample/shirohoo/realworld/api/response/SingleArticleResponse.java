package sample.shirohoo.realworld.api.response;

import java.util.Collection;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleTag;

public record SingleArticleResponse(ArticleResponse article) {
    public static SingleArticleResponse from(
            Article article, Collection<ArticleTag> articleTags, boolean favorited, int favoritesCount) {
        return new SingleArticleResponse(ArticleResponse.from(article, articleTags, favorited, favoritesCount));
    }
}
