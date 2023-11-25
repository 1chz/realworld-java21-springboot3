package sample.shirohoo.realworld.core.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    Article save(Article article);

    List<Article> findAll(ArticleFacets facets);

    Optional<Article> findBySlug(String slug);

    List<Article> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors, ArticleFacets facets);

    ArticleInfo findArticleInfoByAnonymous(Article article);

    ArticleInfo findArticleInfoByUser(User requester, Article article);

    void delete(Article article);

    boolean existsByTitle(String title);
}
