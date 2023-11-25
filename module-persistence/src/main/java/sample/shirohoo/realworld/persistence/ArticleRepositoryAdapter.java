package sample.shirohoo.realworld.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleFacets;
import sample.shirohoo.realworld.core.model.ArticleInfo;
import sample.shirohoo.realworld.core.model.ArticleRepository;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.User;

@Repository
@RequiredArgsConstructor
class ArticleRepositoryAdapter implements ArticleRepository {
    private final ArticleJpaRepository articleJpaRepository;
    private final ArticleTagJpaRepository articleTagJpaRepository;
    private final ArticleFavoriteJpaRepository articleFavoriteJpaRepository;

    @Override
    public Article save(Article article) {
        return articleJpaRepository.save(article);
    }

    @Override
    public List<Article> findAll(ArticleFacets facets) {
        Specification<Article> spec = Specification.where(ArticleSpecifications.hasAuthorName(facets.author()))
                .or(ArticleSpecifications.hasTagName(facets.tag()))
                .or(ArticleSpecifications.hasFavoritedUsername(facets.favorited()));

        PageRequest pageable = PageRequest.of(facets.page(), facets.size());

        return articleJpaRepository.findAll(spec, pageable).getContent();
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return articleJpaRepository.findBySlug(slug);
    }

    @Override
    public List<Article> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors, ArticleFacets facets) {
        return articleJpaRepository
                .findByAuthorInOrderByCreatedAtDesc(authors, PageRequest.of(facets.page(), facets.size()))
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleInfo findArticleInfoByAnonymous(Article article) {
        Set<ArticleTag> articleTags = articleTagJpaRepository.findByArticle(article);
        int totalFavorites = articleFavoriteJpaRepository.countByArticle(article);

        return ArticleInfo.unauthenticated(article, articleTags, totalFavorites);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleInfo findArticleInfoByUser(User requester, Article article) {
        Set<ArticleTag> articleTags = articleTagJpaRepository.findByArticle(article);
        int totalFavorites = articleFavoriteJpaRepository.countByArticle(article);
        boolean favorited = articleFavoriteJpaRepository.existsByUserAndArticle(requester, article);

        return new ArticleInfo(article, articleTags, totalFavorites, favorited);
    }

    @Override
    @Transactional
    public void delete(Article article) {
        articleJpaRepository.delete(article);
    }

    @Override
    public boolean existsByTitle(String title) {
        return articleJpaRepository.existsByTitle(title);
    }
}
