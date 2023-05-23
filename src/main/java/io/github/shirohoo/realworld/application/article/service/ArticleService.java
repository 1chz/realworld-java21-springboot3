package io.github.shirohoo.realworld.application.article.service;

import io.github.shirohoo.realworld.application.article.controller.CreateArticleRequest;
import io.github.shirohoo.realworld.application.article.controller.CreateCommentRequest;
import io.github.shirohoo.realworld.application.article.controller.UpdateArticleRequest;
import io.github.shirohoo.realworld.domain.article.Article;
import io.github.shirohoo.realworld.domain.article.ArticleFacets;
import io.github.shirohoo.realworld.domain.article.ArticleRepository;
import io.github.shirohoo.realworld.domain.article.ArticleVO;
import io.github.shirohoo.realworld.domain.article.Comment;
import io.github.shirohoo.realworld.domain.article.CommentRepository;
import io.github.shirohoo.realworld.domain.article.CommentVO;
import io.github.shirohoo.realworld.domain.article.Tag;
import io.github.shirohoo.realworld.domain.article.TagRepository;
import io.github.shirohoo.realworld.domain.user.FollowId;
import io.github.shirohoo.realworld.domain.user.FollowRepository;
import io.github.shirohoo.realworld.domain.user.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final TagRepository tagRepository;
    private final FollowRepository followRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public ArticleVO getSingleArticle(User me, String slug) {
        Article article = findBySlug(slug);
        return new ArticleVO(me, article);
    }

    @Transactional(readOnly = true)
    public List<ArticleVO> getArticles(User me, ArticleFacets facets) {
        String tag = facets.tag();
        String author = facets.author();
        String favorited = facets.favorited();
        Pageable pageable = facets.getPageable();

        Page<Article> byFacets = articleRepository.findByFacets(tag, author, favorited, pageable);
        return byFacets.getContent().stream()
                .map(article -> new ArticleVO(me, article))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArticleVO> getFeedArticles(User me, ArticleFacets facets) {
        List<User> followings = me.followUsers();
        Pageable pageable = facets.getPageable();

        return articleRepository
                .findByAuthorInOrderByCreatedAtDesc(followings, pageable)
                .map(article -> new ArticleVO(me, article))
                .getContent();
    }

    @Transactional
    public ArticleVO createArticle(User me, CreateArticleRequest request) {
        if (articleRepository.existsByTitle(request.title())) {
            throw new IllegalArgumentException("Title `%s` is already in use.".formatted(request.title()));
        }

        Article article = Article.builder()
                .author(me)
                .title(request.title())
                .description(request.description())
                .content(request.body())
                .build();

        for (Tag invalidTag : request.tags()) {
            Optional<Tag> optionalTag = tagRepository.findByName(invalidTag.getName());
            Tag validTag = optionalTag.orElseGet(() -> tagRepository.save(invalidTag));
            validTag.addTo(article);
        }

        article = articleRepository.save(article);
        return new ArticleVO(me, article);
    }

    @Transactional
    public ArticleVO updateArticle(User me, String slug, UpdateArticleRequest request) {
        Article article = findBySlug(slug);

        if (article.isNotWritten(me)) {
            throw new IllegalArgumentException("You can't edit articles written by others.");
        }

        article.updateTitle(request.title());
        article.updateDescription(request.description());
        article.updateContent(request.body());

        return new ArticleVO(me, article);
    }

    @Transactional
    public void deleteArticle(User me, String slug) {
        Article article = findBySlug(slug);

        if (article.isNotWritten(me)) {
            throw new IllegalArgumentException("You can't delete articles written by others.");
        }

        articleRepository.delete(article);
    }

    @Transactional
    public CommentVO createComment(User me, String slug, CreateCommentRequest request) {
        Comment comment = Comment.builder()
                .article(findBySlug(slug))
                .author(me)
                .content(request.body())
                .build();

        commentRepository.save(comment);

        return CommentVO.myComment(comment);
    }

    @Transactional
    public List<CommentVO> getArticleComments(User me, String slug) {
        Article article = findBySlug(slug);
        Set<Comment> comments = commentRepository.findByArticleOrderByCreatedAtDesc(article);

        if (me.isAnonymous()) {
            return comments.stream().map(CommentVO::unfollowing).toList();
        }

        return comments.stream()
                .map(comment -> {
                    User commentAuthor = comment.getAuthor();
                    FollowId userFollowId = new FollowId(me.getId(), commentAuthor.getId());
                    return followRepository.existsById(userFollowId)
                            ? CommentVO.following(comment)
                            : CommentVO.unfollowing(comment);
                })
                .toList();
    }

    @Transactional
    public void deleteComment(User me, int commentId) {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found by id: `%d`".formatted(commentId)));

        if (comment.isNotWritten(me)) {
            throw new IllegalArgumentException("You can't delete comments written by others.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public ArticleVO favoriteArticle(User me, String slug) {
        Article article = findBySlug(slug);
        return me.favorite(article);
    }

    @Transactional
    public ArticleVO unfavoriteArticle(User me, String slug) {
        Article article = findBySlug(slug);
        return me.unfavorite(article);
    }

    private Article findBySlug(String slug) {
        return articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
    }
}
