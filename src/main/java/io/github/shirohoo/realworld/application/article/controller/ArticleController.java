package io.github.shirohoo.realworld.application.article.controller;

import io.github.shirohoo.realworld.application.article.service.ArticleService;
import io.github.shirohoo.realworld.domain.article.ArticleFacets;
import io.github.shirohoo.realworld.domain.article.ArticleVO;
import io.github.shirohoo.realworld.domain.article.CommentVO;
import io.github.shirohoo.realworld.domain.user.User;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public SingleArticleResponse createArticle(User me, @RequestBody CreateArticleRequest request) {
        ArticleVO article = articleService.createArticle(me, request);
        return new SingleArticleResponse(article);
    }

    @GetMapping("/api/articles")
    public MultipleArticlesResponse getArticles(
            User me,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "limit", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "offset", required = false, defaultValue = "20") int limit) {
        ArticleFacets facets = new ArticleFacets(tag, author, favorited, offset, limit);
        List<ArticleVO> articles = articleService.getArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }

    @GetMapping("/api/articles/{slug}")
    public SingleArticleResponse getSingleArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.getSingleArticle(me, slug);
        return new SingleArticleResponse(article);
    }

    @PutMapping("/api/articles/{slug}")
    public SingleArticleResponse updateArticle(
            User me, @PathVariable String slug, @RequestBody UpdateArticleRequest request) {
        ArticleVO article = articleService.updateArticle(me, slug, request);
        return new SingleArticleResponse(article);
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
    public SingleCommentResponse createComment(
            User me, @PathVariable String slug, @RequestBody CreateCommentRequest request) {
        CommentVO comment = articleService.createComment(me, slug, request);
        return new SingleCommentResponse(comment);
    }

    @GetMapping("/api/articles/{slug}/comments")
    public MultipleCommentsResponse getComments(User me, @PathVariable String slug) {
        List<CommentVO> comments = articleService.getArticleComments(me, slug);
        return new MultipleCommentsResponse(comments);
    }

    @DeleteMapping("/api/articles/{slug}/comments/{id}")
    public void deleteComment(User me, @PathVariable String slug, @PathVariable int id) {
        articleService.deleteComment(me, id);
    }

    @PostMapping("/api/articles/{slug}/favorite")
    public SingleArticleResponse favoriteArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.favoriteArticle(me, slug);
        return new SingleArticleResponse(article);
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    public SingleArticleResponse unfavoriteArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.unfavoriteArticle(me, slug);
        return new SingleArticleResponse(article);
    }
}
