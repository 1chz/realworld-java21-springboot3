package io.zhc1.realworld.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.Article;
import io.zhc1.realworld.core.model.ArticleFavorite;
import io.zhc1.realworld.core.model.ArticleFavoriteRepository;
import io.zhc1.realworld.core.model.User;

@Repository
@RequiredArgsConstructor
class ArticleFavoriteRepositoryAdapter implements ArticleFavoriteRepository {
    private final ArticleFavoriteJpaRepository articleFavoriteJpaRepository;

    @Override
    public void save(ArticleFavorite articleFavorite) {
        articleFavoriteJpaRepository.save(articleFavorite);
    }

    @Override
    @Transactional
    public void deleteBy(User user, Article article) {
        articleFavoriteJpaRepository.deleteByUserAndArticle(user, article);
    }

    @Override
    public boolean existsBy(User user, Article article) {
        return articleFavoriteJpaRepository.existsByUserAndArticle(user, article);
    }
}
