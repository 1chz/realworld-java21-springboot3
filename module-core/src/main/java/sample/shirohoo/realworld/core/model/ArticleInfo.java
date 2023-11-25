package sample.shirohoo.realworld.core.model;

import java.util.Set;

public record ArticleInfo(Article article, Set<ArticleTag> articleTags, int favoritesCount, boolean favorited) {
    public static ArticleInfo unauthenticated(Article article, Set<ArticleTag> articleTags, int favoritesCount) {
        return new ArticleInfo(article, articleTags, favoritesCount, false);
    }
}
