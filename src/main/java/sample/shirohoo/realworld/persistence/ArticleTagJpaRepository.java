package sample.shirohoo.realworld.persistence;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleTag;
import sample.shirohoo.realworld.core.model.Tag;

interface ArticleTagJpaRepository extends JpaRepository<ArticleTag, Integer> {
    Set<ArticleTag> findByArticle(Article article);

    Set<ArticleTag> findByArticleAndTagIn(Article article, Collection<Tag> tags);

    void deleteByArticle(Article article);
}
