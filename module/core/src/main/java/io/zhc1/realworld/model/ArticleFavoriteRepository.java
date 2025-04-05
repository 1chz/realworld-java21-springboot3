package io.zhc1.realworld.model;

public interface ArticleFavoriteRepository {
    void save(ArticleFavorite articleFavorite);

    void deleteBy(User user, Article article);

    boolean existsBy(User user, Article article);
}
