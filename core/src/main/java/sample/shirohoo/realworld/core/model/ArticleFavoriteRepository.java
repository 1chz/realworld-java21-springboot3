package sample.shirohoo.realworld.core.model;

public interface ArticleFavoriteRepository {
    void save(ArticleFavorite articleFavorite);

    void deleteByUserAndArticle(User user, Article article);

    boolean existsByUserAndArticle(User user, Article article);
}
