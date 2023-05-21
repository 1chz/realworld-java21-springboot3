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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
        return articleRepository
                .findBySlug(slug)
                .map(article -> new ArticleVO(me, article))
                .orElseThrow(() -> new NoSuchElementException("Article not found: `%s`".formatted(slug)));
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
        List<User> followings = me.getFollowing();
        Pageable pageable = facets.getPageable();

        return articleRepository
                .findByAuthorInOrderByCreatedAtDesc(followings, pageable)
                .map(article -> new ArticleVO(me, article))
                .getContent();
    }

    @Transactional
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public ArticleVO createArticle(User me, CreateArticleRequest request) {
        Article newArticle = Article.builder()
                .author(me)
                .title(request.title())
                .description(request.description())
                .content(request.body())
                .build();

        for (Tag tag : request.tags()) {
            if (tagRepository.existsByName(tag.getName())) {
                tag = tagRepository.findByName(tag.getName()).get();
            } else {
                tag = tagRepository.save(tag);
            }
            tag.tagging(newArticle);
        }

        newArticle = articleRepository.save(newArticle);
        return new ArticleVO(me, newArticle);
    }

    @Transactional
    public ArticleVO updateArticle(User me, String slug, UpdateArticleRequest request) {
        return articleRepository
                .findBySlug(slug)
                .map(it -> it.update(me, request.title(), request.description(), request.body()))
                .map(it -> new ArticleVO(me, articleRepository.save(it)))
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
    }

    @Transactional
    public void deleteArticle(User me, String slug) {
        articleRepository
                .findBySlug(slug)
                .ifPresentOrElse(
                        article -> {
                            if (article.isWritten(me)) articleRepository.delete(article);
                            else throw new IllegalArgumentException("You can't delete articles written by others.");
                        },
                        () -> {
                            throw new NoSuchElementException("Article not found by slug: `%s`".formatted(slug));
                        });
    }

    @Transactional
    public CommentVO createComment(User me, String slug, CreateCommentRequest request) {
        return articleRepository
                .findBySlug(slug)
                .map(article -> Comment.builder()
                        .article(article)
                        .author(me)
                        .content(request.body())
                        .build())
                .map(commentRepository::save)
                .map(CommentVO::myComment)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
    }

    @Transactional
    public List<CommentVO> getArticleComments(User me, String slug) {
        Set<Comment> comments = articleRepository
                .findBySlug(slug)
                .map(commentRepository::findByArticleOrderByCreatedAtDesc)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));

        if (me == null) {
            return comments.stream().map(CommentVO::unfollowing).toList();
        }

        List<CommentVO> results = new ArrayList<>();
        for (Comment comment : comments) {
            User author = comment.getAuthor();
            FollowId userFollowId = new FollowId(me.getId(), author.getId());
            CommentVO commentVO = followRepository.existsById(userFollowId)
                    ? CommentVO.following(comment)
                    : CommentVO.unfollowing(comment);
            results.add(commentVO);
        }

        return results;
    }

    @Transactional
    public void deleteComment(User me, int commentId) {
        commentRepository
                .findById(commentId)
                .ifPresentOrElse(
                        comment -> {
                            if (comment.isWritten(me)) commentRepository.delete(comment);
                            else throw new IllegalArgumentException("You can't delete comments written by others.");
                        },
                        () -> {
                            throw new NoSuchElementException("Comment not found by id: `%d`".formatted(commentId));
                        });
    }

    @Transactional
    public ArticleVO favoriteArticle(User me, String slug) {
        return articleRepository
                .findBySlug(slug)
                .map(me::favorite)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
    }

    @Transactional
    public ArticleVO unfavoriteArticle(User me, String slug) {
        return articleRepository
                .findBySlug(slug)
                .map(me::unfavorite)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
    }
}
