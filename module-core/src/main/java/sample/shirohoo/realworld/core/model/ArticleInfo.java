package sample.shirohoo.realworld.core.model;

import java.util.Collection;

public record ArticleInfo(Article article, int favoritesCount, boolean favorited) {
    public static ArticleInfo unauthenticated(Article article, int favoritesCount) {
        return new ArticleInfo(article, favoritesCount, false);
    }

    public Collection<ArticleTag> articleTags() {
        return article.getArticleTags();
    }
}
