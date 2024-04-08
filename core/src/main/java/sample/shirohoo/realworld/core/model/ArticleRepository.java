package sample.shirohoo.realworld.core.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    Article save(Article article);

    Article save(Article article, Collection<Tag> tags);

    List<Article> findAll(ArticleFacets facets);

    Optional<Article> findBySlug(String slug);

    List<Article> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors, ArticleFacets facets);

    ArticleDetails findArticleInfoByAnonymous(Article article);

    ArticleDetails findArticleInfoByUser(User requester, Article article);

    void delete(Article article);

    boolean existsByTitle(String title);
}
