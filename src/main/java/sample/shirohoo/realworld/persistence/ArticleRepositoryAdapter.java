package sample.shirohoo.realworld.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleFacets;
import sample.shirohoo.realworld.core.model.ArticleRepository;
import sample.shirohoo.realworld.core.model.User;

@Repository
@RequiredArgsConstructor
class ArticleRepositoryAdapter implements ArticleRepository {
    private final ArticleJpaRepository articleJpaRepository;

    @Override
    public Article save(Article article) {
        return articleJpaRepository.save(article);
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return articleJpaRepository.findBySlug(slug);
    }

    @Override
    public List<Article> findByFacets(ArticleFacets facets) {
        return articleJpaRepository
                .findByFacets(
                        facets.tag(), facets.author(), facets.favorited(), PageRequest.of(facets.page(), facets.size()))
                .getContent();
    }

    @Override
    public List<Article> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors, ArticleFacets facets) {
        return articleJpaRepository
                .findByAuthorInOrderByCreatedAtDesc(authors, PageRequest.of(facets.page(), facets.size()))
                .getContent();
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
