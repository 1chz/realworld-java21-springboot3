package io.zhc1.realworld.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.zhc1.realworld.model.Article;
import io.zhc1.realworld.model.ArticleComment;

interface ArticleCommentJpaRepository extends JpaRepository<ArticleComment, Integer> {
    List<ArticleComment> findByArticleOrderByCreatedAtDesc(Article article);

    void deleteByArticle(Article article);
}
