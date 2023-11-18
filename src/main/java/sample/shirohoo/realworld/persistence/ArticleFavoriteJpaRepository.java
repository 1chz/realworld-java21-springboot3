package sample.shirohoo.realworld.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleFavorite;
import sample.shirohoo.realworld.core.model.User;

interface ArticleFavoriteJpaRepository extends JpaRepository<ArticleFavorite, Integer> {
    void deleteByUserAndArticle(User user, Article article);

    boolean existsByUserAndArticle(User user, Article article);

    int countByArticle(Article article);
}
