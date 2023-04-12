package io.github.shirohoo.realworld.application.content;

import io.github.shirohoo.realworld.domain.content.Article;
import io.github.shirohoo.realworld.domain.content.ArticleRepository;
import io.github.shirohoo.realworld.domain.content.ArticleVO;
import io.github.shirohoo.realworld.domain.content.Comment;
import io.github.shirohoo.realworld.domain.content.CommentRepository;
import io.github.shirohoo.realworld.domain.content.CommentVO;
import io.github.shirohoo.realworld.domain.content.Tag;
import io.github.shirohoo.realworld.domain.content.TagRepository;
import io.github.shirohoo.realworld.domain.user.User;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final TagRepository tagRepository;
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

        return articleRepository.findByFacets(tag, author, favorited, pageable).getContent().stream()
                .map(article -> new ArticleVO(me, article))
                .toList();
    }

    public List<ArticleVO> getFeedArticles(User me, ArticleFacets facets) {
        List<User> followings = me.followings();
        Pageable pageable = facets.getPageable();

        return articleRepository.findByAuthorInOrderByCreatedAtDesc(followings, pageable).getContent().stream()
                .map(article -> new ArticleVO(me, article))
                .toList();
    }

    @Transactional
    public ArticleVO createArticle(User me, CreateArticleRequest request) {
        Article article = Article.builder()
                .author(me)
                .slug(request.title().toLowerCase().replaceAll("\\s+", "-"))
                .title(request.title())
                .description(request.description())
                .content(request.body())
                .build();

        for (String tagName : request.tagList()) {
            tagRepository.findByName(tagName).ifPresentOrElse(article::addTag, () -> {
                Tag tag = new Tag(tagName);
                tagRepository.save(tag);
                article.addTag(tag);
            });
        }

        return new ArticleVO(me, articleRepository.save(article));
    }

    @Transactional
    public ArticleVO updateArticle(User me, String slug, UpdateArticleRequest request) {
        Article updated = articleRepository
                .findBySlug(slug)
                .map(article -> {
                    if (article.author().equals(me)) {
                        article.slug(request.title().toLowerCase().replaceAll("\\s+", "-"));
                        article.title(request.title());
                        article.description(request.description());
                        article.content(request.body());
                        return articleRepository.save(article);
                    }
                    throw new IllegalArgumentException("You cannot edit articles written by others.");
                })
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));

        return new ArticleVO(me, updated);
    }

    @Transactional
    public void deleteArticle(User me, String slug) {
        articleRepository
                .findBySlug(slug)
                .ifPresentOrElse(
                        article -> {
                            if (article.author().equals(me)) {
                                articleRepository.delete(article);
                            } else {
                                throw new IllegalArgumentException("You cannot delete articles written by others.");
                            }
                        },
                        () -> {
                            throw new NoSuchElementException("Article not found by slug: `%s`".formatted(slug));
                        });
    }

    @Transactional
    public CommentVO createComment(User me, String slug, CreateCommentRequest request) {
        Comment comment = articleRepository
                .findBySlug(slug)
                .map(article -> Comment.builder()
                        .author(me)
                        .article(article)
                        .content(request.body())
                        .build())
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));

        return new CommentVO(me, commentRepository.save(comment));
    }

    @Transactional
    public List<CommentVO> getArticleComments(User me, String slug) {
        Article article = articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
        return commentRepository.findByArticleOrderByCreatedAtDesc(article).stream()
                .map(comment -> new CommentVO(me, comment))
                .toList();
    }

    @Transactional
    public void deleteComment(User me, int commentId) {
        commentRepository
                .findById(commentId)
                .ifPresentOrElse(
                        comment -> {
                            if (comment.author().equals(me)) {
                                commentRepository.delete(comment);
                            } else {
                                throw new IllegalArgumentException("You cannot delete comments written by others.");
                            }
                        },
                        () -> {
                            throw new NoSuchElementException("Comment not found by id: `%d`".formatted(commentId));
                        });
    }

    @Transactional
    public ArticleVO favoriteArticle(User me, String slug) {
        Article article = articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
        return new ArticleVO(me, article.addFavoritedBy(me));
    }

    @Transactional
    public ArticleVO unfavoriteArticle(User me, String slug) {
        Article article = articleRepository
                .findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Article not found by slug: `%s`".formatted(slug)));
        return new ArticleVO(me, article.removeFavoritedBy(me));
    }
}
