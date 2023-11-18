package sample.shirohoo.realworld.persistence;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.User;

interface ArticleJpaRepository extends JpaRepository<Article, Integer> {
    Optional<Article> findBySlug(String slug);

    // Todo: further optimization can be achieved using dynamic queries.
    @Query(
            """
        select article
        from Article article
           left join fetch User author on article.author.id = author.id
           left join fetch ArticleTag articleTag on article.id = articleTag.article.id
           left join fetch Tag tag on articleTag.tag.id = tag.id
           left join fetch ArticleFavorite articleFavorite on article.id = articleFavorite.article.id
           left join fetch User favoritedUser on articleFavorite.user.id = favoritedUser.id
        where
           author.username = :author
           or tag.name = :tag
           or favoritedUser.username = :favorited
        order by
           article.createdAt desc
   """)
    Page<Article> findByFacets(
            @Param("tag") String tag,
            @Param("author") String author,
            @Param("favorited") String favorited,
            Pageable pageable);

    Page<Article> findByAuthorInOrderByCreatedAtDesc(Collection<User> authors, Pageable pageable);

    boolean existsByTitle(String title);
}
