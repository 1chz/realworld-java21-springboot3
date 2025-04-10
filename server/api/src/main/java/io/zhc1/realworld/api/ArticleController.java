package io.zhc1.realworld.api;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.request.EditArticleRequest;
import io.zhc1.realworld.api.request.WriteArticleRequest;
import io.zhc1.realworld.api.response.ArticleResponse;
import io.zhc1.realworld.api.response.MultipleArticlesResponse;
import io.zhc1.realworld.api.response.SingleArticleResponse;
import io.zhc1.realworld.config.RealWorldAuthenticationToken;
import io.zhc1.realworld.model.Article;
import io.zhc1.realworld.model.ArticleDetails;
import io.zhc1.realworld.model.ArticleFacets;
import io.zhc1.realworld.service.ArticleService;
import io.zhc1.realworld.service.UserService;

@RestController
@RequiredArgsConstructor
class ArticleController {
    private final UserService userService;
    private final ArticleService articleService;

    @PostMapping("/api/articles")
    SingleArticleResponse postArticle(RealWorldAuthenticationToken jwt, @RequestBody WriteArticleRequest request) {
        var requester = userService.getUser(jwt.userId());
        var article = articleService.write(
                new Article(
                        requester,
                        request.article().title(),
                        request.article().description(),
                        request.article().body()),
                request.tags());

        return new SingleArticleResponse(new ArticleDetails(article, 0, false));
    }

    @GetMapping("/api/articles")
    MultipleArticlesResponse getArticles(
            RealWorldAuthenticationToken jwt,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        var facets = new ArticleFacets(tag, author, favorited, offset, limit);

        if (jwt == null || !jwt.isAuthenticated()) {
            return articleService.getArticles(facets).stream()
                    .map(ArticleResponse::new)
                    .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
        }

        var user = userService.getUser(jwt.userId());
        return articleService.getArticles(user, facets).stream()
                .map(ArticleResponse::new)
                .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
    }

    @GetMapping("/api/articles/{slug}")
    SingleArticleResponse getArticle(RealWorldAuthenticationToken jwt, @PathVariable String slug) {
        var article = articleService.getArticle(slug);

        if (jwt == null || !jwt.isAuthenticated()) {
            return new SingleArticleResponse(articleService.getArticleDetails(article));
        }

        var user = userService.getUser(jwt.userId());
        return new SingleArticleResponse(articleService.getArticleDetails(user, article));
    }

    @PutMapping("/api/articles/{slug}")
    SingleArticleResponse updateArticle(
            RealWorldAuthenticationToken jwt, @PathVariable String slug, @RequestBody EditArticleRequest request) {
        var requester = userService.getUser(jwt.userId());
        var article = articleService.getArticle(slug);

        if (request.article().title() != null) {
            article = articleService.editTitle(
                    requester, article, request.article().title());
        }

        if (request.article().description() != null) {
            article = articleService.editDescription(
                    requester, article, request.article().description());
        }

        if (request.article().body() != null) {
            article = articleService.editContent(
                    requester, article, request.article().body());
        }

        var articleInfo = articleService.getArticleDetails(requester, article);
        return new SingleArticleResponse(articleInfo);
    }

    @DeleteMapping("/api/articles/{slug}")
    void deleteArticle(RealWorldAuthenticationToken jwt, @PathVariable String slug) {
        var requester = userService.getUser(jwt.userId());
        var article = articleService.getArticle(slug);

        articleService.delete(requester, article);
    }

    @GetMapping("/api/articles/feed")
    MultipleArticlesResponse getArticleFeeds(
            RealWorldAuthenticationToken jwt, // Must be verified
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        var facets = new ArticleFacets(offset, limit);
        var requester = userService.getUser(jwt.userId());

        return articleService.getFeeds(requester, facets).stream()
                .map(ArticleResponse::new)
                .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
    }
}
