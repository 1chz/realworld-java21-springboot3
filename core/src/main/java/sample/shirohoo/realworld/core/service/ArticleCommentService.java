package sample.shirohoo.realworld.core.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Article;
import sample.shirohoo.realworld.core.model.ArticleComment;
import sample.shirohoo.realworld.core.model.ArticleCommentRepository;
import sample.shirohoo.realworld.core.model.User;

@Service
@RequiredArgsConstructor
public class ArticleCommentService {
    private final ArticleCommentRepository articleCommentRepository;

    /**
     * Get comment by id.
     *
     * @param commentId comment id
     * @return Returns comment
     */
    public ArticleComment getComment(int commentId) {
        return articleCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("invalid comment id."));
    }

    /**
     * Get all comments by article.
     *
     * @param article article
     * @return Returns all comments
     */
    public List<ArticleComment> getComments(Article article) {
        return articleCommentRepository.findByArticle(article);
    }

    /**
     * Write a comment.
     *
     * @param articleComment comment
     * @return Returns the written comment
     */
    public ArticleComment write(ArticleComment articleComment) {
        return articleCommentRepository.save(articleComment);
    }

    /**
     * Delete comment.
     *
     * @param requester user who requested
     * @param articleComment comment
     */
    public void delete(User requester, ArticleComment articleComment) {
        if (articleComment.isNotAuthor(requester)) {
            throw new IllegalArgumentException("you can't delete comments written by others.");
        }

        articleCommentRepository.delete(articleComment);
    }
}
