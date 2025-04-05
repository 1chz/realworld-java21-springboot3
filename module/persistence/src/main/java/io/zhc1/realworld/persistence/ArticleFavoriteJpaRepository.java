package io.zhc1.realworld.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import io.zhc1.realworld.model.Article;
import io.zhc1.realworld.model.ArticleFavorite;
import io.zhc1.realworld.model.User;

interface ArticleFavoriteJpaRepository extends JpaRepository<ArticleFavorite, Integer> {
    void deleteByUserAndArticle(User user, Article article);

    boolean existsByUserAndArticle(User user, Article article);

    int countByArticle(Article article);
}
