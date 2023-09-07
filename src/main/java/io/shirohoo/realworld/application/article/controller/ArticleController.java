package io.shirohoo.realworld.application.article.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.shirohoo.realworld.application.article.service.ArticleService;
import io.shirohoo.realworld.domain.article.ArticleFacets;
import io.shirohoo.realworld.domain.article.ArticleVO;
import io.shirohoo.realworld.domain.article.CommentVO;
import io.shirohoo.realworld.domain.user.User;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public SingleArticleRecord createArticle(User me, @RequestBody CreateArticleRequest request) {
        ArticleVO article = articleService.createArticle(me, request);
        return new SingleArticleRecord(article);
    }

    @GetMapping("/api/articles")
    public MultipleArticlesResponse getArticles(
            User me,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset) {
        ArticleFacets facets = new ArticleFacets(tag, author, favorited, offset, limit);
        List<ArticleVO> articles = articleService.getArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }

    @GetMapping("/api/articles/{slug}")
    public SingleArticleRecord getSingleArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.getSingleArticle(me, slug);
        return new SingleArticleRecord(article);
    }

    @PutMapping("/api/articles/{slug}")
    public SingleArticleRecord updateArticle(
            User me, @PathVariable String slug, @RequestBody UpdateArticleRequest request) {
        ArticleVO article = articleService.updateArticle(me, slug, request);
        return new SingleArticleRecord(article);
    }

    @DeleteMapping("/api/articles/{slug}")
    public void deleteArticle(User me, @PathVariable String slug) {
        articleService.deleteArticle(me, slug);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/articles/feed")
    public MultipleArticlesResponse getFeedArticles(
            User me,
            @RequestParam(value = "limit", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "offset", required = false, defaultValue = "20") int limit) {
        ArticleFacets facets = new ArticleFacets(null, null, null, offset, limit);
        List<ArticleVO> articles = articleService.getFeedArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }

    @PostMapping("/api/articles/{slug}/comments")
    public SingleCommentRecord createComment(
            User me, @PathVariable String slug, @RequestBody CreateCommentRequest request) {
        CommentVO comment = articleService.createComment(me, slug, request);
        return new SingleCommentRecord(comment);
    }

    @GetMapping("/api/articles/{slug}/comments")
    public MultipleCommentsRecord getComments(User me, @PathVariable String slug) {
        List<CommentVO> comments = articleService.getArticleComments(me, slug);
        return new MultipleCommentsRecord(comments);
    }

    @DeleteMapping("/api/articles/{slug}/comments/{id}")
    public void deleteComment(User me, @PathVariable String slug, @PathVariable int id) {
        articleService.deleteComment(me, id);
    }

    @PostMapping("/api/articles/{slug}/favorite")
    public SingleArticleRecord favoriteArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.favoriteArticle(me, slug);
        return new SingleArticleRecord(article);
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    public SingleArticleRecord unfavoriteArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.unfavoriteArticle(me, slug);
        return new SingleArticleRecord(article);
    }
}
