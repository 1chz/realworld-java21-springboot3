package io.github.shirohoo.realworld.application.content;

import static org.springframework.http.HttpStatus.*;

import io.github.shirohoo.realworld.domain.content.ArticleVO;
import io.github.shirohoo.realworld.domain.user.User;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/api/articles/{slug}")
    public SingleArticleResponse getSingleArticle(User me, @PathVariable String slug) {
        ArticleVO article = articleService.getSingleArticle(me, slug);
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

    @ResponseStatus(CREATED)
    @PostMapping("/api/articles")
    public SingleArticleResponse createArticle(User me, @RequestBody CreateArticleRequest request) {
        ArticleVO article = articleService.createArticle(me, request);
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

    @GetMapping("/api/articles/feed")
    public MultipleArticlesResponse getFeedArticles(
            User me,
            @RequestParam(value = "limit", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "offset", required = false, defaultValue = "20") int limit) {
        ArticleFacets facets = new ArticleFacets(null, null, null, offset, limit);
        List<ArticleVO> articles = articleService.getFeedArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }
}
