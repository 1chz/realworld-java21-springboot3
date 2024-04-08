package sample.shirohoo.realworld.persistence;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleFavorite;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.User;

final class ArticleSpecifications {
    private ArticleSpecifications() {}

    static Specification<Article> hasAuthorName(String authorName) {
        return (root, query, criteriaBuilder) -> {
            if (authorName == null || authorName.isBlank()) {
                return null;
            }

            Join<Article, User> articleAuthor = root.join("author", JoinType.INNER);
            return criteriaBuilder.equal(articleAuthor.get("username"), authorName);
        };
    }

    static Specification<Article> hasTagName(String tagName) {
        return (root, query, criteriaBuilder) -> {
            if (tagName == null || tagName.isBlank()) {
                return null;
            }

            Join<Article, ArticleTag> articleTag = root.join("articleTags", JoinType.LEFT);
            return criteriaBuilder.equal(articleTag.get("tag").get("name"), tagName);
        };
    }

    static Specification<Article> hasFavoritedUsername(String favoritedUsername) {
        return (root, query, criteriaBuilder) -> {
            if (favoritedUsername == null || favoritedUsername.isBlank()) {
                return null;
            }

            Join<ArticleFavorite, User> favoriteUser =
                    query.from(ArticleFavorite.class).join("user", JoinType.LEFT);
            return criteriaBuilder.equal(favoriteUser.get("username"), favoritedUsername);
        };
    }
}
