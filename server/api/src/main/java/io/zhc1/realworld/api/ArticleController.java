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
    SingleArticleResponse postArticle(
            RealWorldAuthenticationToken authorsToken, @RequestBody WriteArticleRequest request) {
        var author = userService.getUser(authorsToken.userId());
        var article = articleService.write(
                new Article(
                        author,
                        request.article().title(),
                        request.article().description(),
                        request.article().body()),
                request.tags());

        return new SingleArticleResponse(new ArticleDetails(article, 0, false));
    }

    @GetMapping("/api/articles")
    MultipleArticlesResponse getArticles(
            RealWorldAuthenticationToken readersToken,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        var facets = new ArticleFacets(tag, author, favorited, offset, limit);

        boolean isAnonymousReader = readersToken == null || !readersToken.isAuthenticated();
        if (isAnonymousReader) {
            return articleService.getArticles(facets).stream()
                    .map(ArticleResponse::new)
                    .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
        }

        var reader = userService.getUser(readersToken.userId());
        return articleService.getArticles(reader, facets).stream()
                .map(ArticleResponse::new)
                .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
    }

    @GetMapping("/api/articles/{slug}")
    SingleArticleResponse getArticle(RealWorldAuthenticationToken readersToken, @PathVariable String slug) {
        var article = articleService.getArticle(slug);

        boolean isAnonymousReader = readersToken == null || !readersToken.isAuthenticated();
        if (isAnonymousReader) {
            return new SingleArticleResponse(articleService.getArticleDetails(article));
        }

        var reader = userService.getUser(readersToken.userId());
        return new SingleArticleResponse(articleService.getArticleDetails(reader, article));
    }

    @PutMapping("/api/articles/{slug}")
    SingleArticleResponse updateArticle(
            RealWorldAuthenticationToken authorsToken,
            @PathVariable String slug,
            @RequestBody EditArticleRequest request) {
        var author = userService.getUser(authorsToken.userId());
        var article = articleService.getArticle(slug);

        if (request.article().title() != null) {
            article =
                    articleService.editTitle(author, article, request.article().title());
        }

        if (request.article().description() != null) {
            article = articleService.editDescription(
                    author, article, request.article().description());
        }

        if (request.article().body() != null) {
            article = articleService.editContent(
                    author, article, request.article().body());
        }

        var articleDetail = articleService.getArticleDetails(author, article);
        return new SingleArticleResponse(articleDetail);
    }

    @DeleteMapping("/api/articles/{slug}")
    void deleteArticle(RealWorldAuthenticationToken authorsToken, @PathVariable String slug) {
        var author = userService.getUser(authorsToken.userId());
        var article = articleService.getArticle(slug);

        articleService.delete(author, article);
    }

    @GetMapping("/api/articles/feed")
    MultipleArticlesResponse getArticleFeeds(
            RealWorldAuthenticationToken readersToken, // Must be verified
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {
        var reader = userService.getUser(readersToken.userId());
        var facets = new ArticleFacets(offset, limit);

        return articleService.getFeeds(reader, facets).stream()
                .map(ArticleResponse::new)
                .collect(collectingAndThen(toList(), MultipleArticlesResponse::new));
    }
}
