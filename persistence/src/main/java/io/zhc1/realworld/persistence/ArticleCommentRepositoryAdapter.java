package io.zhc1.realworld.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.Article;
import io.zhc1.realworld.core.model.ArticleComment;
import io.zhc1.realworld.core.model.ArticleCommentRepository;

@Repository
@RequiredArgsConstructor
class ArticleCommentRepositoryAdapter implements ArticleCommentRepository {
    private final ArticleCommentJpaRepository articleCommentJpaRepository;

    @Override
    public ArticleComment save(ArticleComment articleComment) {
        return articleCommentJpaRepository.save(articleComment);
    }

    @Override
    public Optional<ArticleComment> findById(int commentId) {
        return articleCommentJpaRepository.findById(commentId);
    }

    @Override
    public List<ArticleComment> findByArticle(Article article) {
        return articleCommentJpaRepository.findByArticleOrderByCreatedAtDesc(article);
    }

    @Override
    @Transactional
    public void delete(ArticleComment articleComment) {
        articleCommentJpaRepository.delete(articleComment);
    }
}
