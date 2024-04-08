package sample.shirohoo.realworld.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleFavorite;
import sample.shirohoo.realworld.core.model.ArticleFavoriteRepository;
import sample.shirohoo.realworld.core.model.User;

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
