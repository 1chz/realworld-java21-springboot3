package io.zhc1.realworld.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.Article;
import io.zhc1.realworld.core.model.ArticleDetails;
import io.zhc1.realworld.core.model.ArticleFacets;
import io.zhc1.realworld.core.model.ArticleRepository;
import io.zhc1.realworld.core.model.ArticleTag;
import io.zhc1.realworld.core.model.Tag;
import io.zhc1.realworld.core.model.User;

@Repository
@RequiredArgsConstructor
class ArticleRepositoryAdapter implements ArticleRepository {
    private final TagJpaRepository tagJpaRepository;
    private final ArticleJpaRepository articleJpaRepository;
    private final ArticleCommentJpaRepository articleCommentJpaRepository;
    private final ArticleFavoriteJpaRepository articleFavoriteJpaRepository;

    @Override
    public Article save(Article article) {
        return articleJpaRepository.save(article);
    }

    @Override
    @Transactional
    public Article save(Article article, Collection<Tag> tags) {
        var savedArticle = save(article);
        for (var tag : tagJpaRepository.saveAll(tags)) {
            savedArticle.addTag(new ArticleTag(savedArticle, tag));
        }
        return savedArticle;
    }

    @Override
    public List<Article> findAll(ArticleFacets facets) {
        var pageable = PageRequest.of(facets.page(), facets.size());
        var spec = Specification.where(ArticleSpecifications.hasAuthorName(facets.author()))
                .or(ArticleSpecifications.hasTagName(facets.tag()))
                .or(ArticleSpecifications.hasFavoritedUsername(facets.favorited()));

        return articleJpaRepository.findAll(spec, pageable).getContent();
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return articleJpaRepository.findBySlug(slug);
    }

    @Override
    public List<Article> findByAuthors(Collection<User> authors, ArticleFacets facets) {
        return articleJpaRepository
                .findByAuthorInOrderByCreatedAtDesc(authors, PageRequest.of(facets.page(), facets.size()))
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetails findArticleDetails(Article article) {
        int totalFavorites = articleFavoriteJpaRepository.countByArticle(article);

        return ArticleDetails.unauthenticated(article, totalFavorites);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetails findArticleDetails(User requester, Article article) {
        int totalFavorites = articleFavoriteJpaRepository.countByArticle(article);
        boolean favorited = articleFavoriteJpaRepository.existsByUserAndArticle(requester, article);

        return new ArticleDetails(article, totalFavorites, favorited);
    }

    @Override
    @Transactional
    public void delete(Article article) {
        articleCommentJpaRepository.deleteByArticle(article);
        articleJpaRepository.delete(article);
    }

    @Override
    public boolean existsBy(String title) {
        return articleJpaRepository.existsByTitle(title);
    }
}
