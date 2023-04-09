package io.github.shirohoo.realworld.application.content;

import io.github.shirohoo.realworld.domain.content.Articles;
import io.github.shirohoo.realworld.domain.user.User;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/api/articles/{slug}")
    public SingleArticle getSingleArticle(User me, @PathVariable String slug) {
        Articles article = articleService.getSingleArticle(me, slug);
        return new SingleArticle(article);
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
        List<Articles> articles = articleService.getArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }

    @GetMapping("/api/articles/feed")
    public MultipleArticlesResponse getFeedArticles(
            User me,
            @RequestParam(value = "limit", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "offset", required = false, defaultValue = "20") int limit) {
        ArticleFacets facets = new ArticleFacets(null, null, null, offset, limit);
        List<Articles> articles = articleService.getFeedArticles(me, facets);
        return new MultipleArticlesResponse(articles);
    }
}
