package sample.shirohoo.realworld.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleComment;

interface ArticleCommentJpaRepository extends JpaRepository<ArticleComment, Integer> {
    List<ArticleComment> findByArticleOrderByCreatedAtDesc(Article article);

    void deleteByArticle(Article article);
}
