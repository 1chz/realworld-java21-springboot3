package sample.shirohoo.realworld.core.model;

import java.util.Collection;

public record ArticleDetails(Article article, int favoritesCount, boolean favorited) {
    public static ArticleDetails unauthenticated(Article article, int favoritesCount) {
        return new ArticleDetails(article, favoritesCount, false);
    }

    public Collection<ArticleTag> articleTags() {
        return article.getArticleTags();
    }
}
